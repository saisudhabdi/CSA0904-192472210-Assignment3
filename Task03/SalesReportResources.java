import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SalesReportResources {
    private static int openReaders;

    private static BufferedReader trackedReader(Path path) throws IOException {
        openReaders++;
        BufferedReader reader = Files.newBufferedReader(path);
        return new BufferedReader(reader) {
            private boolean closed;
            @Override
            public void close() throws IOException {
                if (!closed) {
                    closed = true;
                    openReaders--;
                }
                super.close();
            }
        };
    }

    private static List<String> manualReport(List<Path> files, Path output) throws IOException {
        List<BufferedReader> readers = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(output);
            for (Path file : files) {
                BufferedReader reader = trackedReader(file);
                readers.add(reader);
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                    writer.write(line);
                    writer.newLine();
                }
            }
            return lines;
        } finally {
            if (writer != null) writer.close();
            for (BufferedReader reader : readers) reader.close();
        }
    }

    private static List<String> safeReport(List<Path> files, Path output) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedWriter writer = Files.newBufferedWriter(output)) {
            for (Path file : files) {
                try (BufferedReader reader = trackedReader(file)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
        }
        return lines;
    }

    public static void main(String[] args) throws IOException {
        Path folder = Path.of("sales-input");
        Files.createDirectories(folder);
        Path store1 = folder.resolve("store1.txt");
        Path store2 = folder.resolve("store2.txt");
        Path missing = folder.resolve("missing-store.txt");
        Files.writeString(store1, "Store 1, 1200\nStore 1, 800\n");
        Files.writeString(store2, "Store 2, 950\n");

        List<Path> validFiles = List.of(store1, store2);
        List<String> manual = manualReport(validFiles, Path.of("manual-summary.txt"));
        System.out.println("Manual report lines: " + manual.size());
        System.out.println("Open readers after manual version: " + openReaders);

        try {
            safeReport(List.of(store1, missing, store2), Path.of("safe-summary.txt"));
        } catch (IOException e) {
            System.out.println("Safe version logged failure: " + e.getMessage());
        }
        System.out.println("Open readers after try-with-resources: " + openReaders);

        List<String> safe = safeReport(validFiles, Path.of("safe-summary-valid.txt"));
        System.out.println("Reports match: " + manual.equals(safe));
    }
}