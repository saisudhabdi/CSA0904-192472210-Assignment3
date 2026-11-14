import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ResultProcessor {
    static class Result {
        final int studentId;
        final int score;
        Result(int studentId, int score) {
            this.studentId = studentId;
            this.score = score;
        }
    }

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
        ScoreTask(int id) { this.id = id; }

        @Override
        public ProcessingOutcome call() {
            try {
                if (id % 97 == 0) throw new IllegalArgumentException("Corrupt score file");
                if (id % 89 == 0) throw new java.io.FileNotFoundException("Missing score file");
                int score = (id * 37) % 101;
                if (score < 0 || score > 100) throw new IllegalArgumentException("Invalid score");
                Result result = new Result(id, score);
                return new ProcessingOutcome(true,
                        "Student " + result.studentId + " score " + result.score);
            } catch (Exception e) {
                return new ProcessingOutcome(false, "Student " + id + ": " + e.getMessage());
            }
        }
    }

    private static void processWithPool(int poolSize, int count) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        ConcurrentLinkedQueue<String> successes = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<String> failures = new ConcurrentLinkedQueue<>();
        List<Future<ProcessingOutcome>> futures = new ArrayList<>();

        long start = System.nanoTime();
        try {
            for (int id = 1; id <= count; id++) {
                futures.add(executor.submit(new ScoreTask(id)));
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
        for (int pool : new int[]{1, 2, 4, 8}) processWithPool(pool, count);
    }
}