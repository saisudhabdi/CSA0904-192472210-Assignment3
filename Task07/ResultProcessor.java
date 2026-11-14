import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ResultProcessor {
    static class ProcessingOutcome {
        final boolean success;
        final String detail;
        ProcessingOutcome(boolean success, String detail) {
            this.success = success;
            this.detail = detail;
        }
    }

    static class ScoreTask implements Callable<ProcessingOutcome> {
        private final int id;
        private final Path folder;

        ScoreTask(int id, Path folder) {
            this.id = id;
            this.folder = folder;
        }

        @Override
        public ProcessingOutcome call() {
            Path file = folder.resolve("score-" + id + ".txt");
            try {
                String text = Files.readString(file).trim();
                int score = Integer.parseInt(text);
                if (score < 0 || score > 100) {
                    throw new IllegalArgumentException("Score outside 0-100");
                }
                return new ProcessingOutcome(true, "Student " + id + " score " + score);
            } catch (Exception e) {
                return new ProcessingOutcome(false, "Student " + id + ": " + e.getMessage());
            }
        }
    }

    private static void createInputFiles(Path folder, int count) throws Exception {
        Files.createDirectories(folder);
        for (int id = 1; id <= count; id++) {
            Path file = folder.resolve("score-" + id + ".txt");
            if (id % 89 == 0) {
                Files.deleteIfExists(file);
            } else if (id % 97 == 0) {
                Files.writeString(file, "CORRUPT_SCORE");
            } else {
                Files.writeString(file, Integer.toString((id * 37) % 101));
            }
        }
    }

    private static void processWithPool(int poolSize, int count, Path folder) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        ConcurrentLinkedQueue<String> successes = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<String> failures = new ConcurrentLinkedQueue<>();
        List<Future<ProcessingOutcome>> futures = new ArrayList<>();

        long start = System.nanoTime();
        try {
            for (int id = 1; id <= count; id++) {
                futures.add(executor.submit(new ScoreTask(id, folder)));
            }
            for (Future<ProcessingOutcome> future : futures) {
                ProcessingOutcome outcome = future.get();
                if (outcome.success) successes.add(outcome.detail);
                else failures.add(outcome.detail);
            }
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) executor.shutdownNow();
        }
        long elapsed = System.nanoTime() - start;
        System.out.printf("Pool=%d, processed=%d, success=%d, failure=%d, time=%.2f ms%n",
                poolSize, count, successes.size(), failures.size(), elapsed / 1_000_000.0);
    }

    public static void main(String[] args) throws Exception {
        int count = 10000;
        Path folder = Path.of("scores");
        createInputFiles(folder, count);
        for (int pool : new int[]{1, 2, 4, 8}) {
            processWithPool(pool, count, folder);
        }
    }
}