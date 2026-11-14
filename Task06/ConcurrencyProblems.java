import java.util.concurrent.locks.ReentrantLock;

public class ConcurrencyProblems {
    static class Wallet {
        private final int id;
        private int balance;
        private final ReentrantLock lock = new ReentrantLock();

        Wallet(int id, int balance) {
            this.id = id;
            this.balance = balance;
        }
    }

    static void transfer(Wallet from, Wallet to, int amount) throws InterruptedException {
        Wallet first = from.id < to.id ? from : to;
        Wallet second = from.id < to.id ? to : from;
        first.lock.lock();
        try {
            second.lock.lock();
            try {
                if (from.balance >= amount) {
                    from.balance -= amount;
                    to.balance += amount;
                }
            } finally {
                second.lock.unlock();
            }
        } finally {
            first.lock.unlock();
        }
    }

    static class TokenCounter {
        private final int[] buffer = new int[5];
        private int count;
        private int in;
        private int out;

        public synchronized void put(int token) throws InterruptedException {
            while (count == buffer.length) wait();
            buffer[in] = token;
            in = (in + 1) % buffer.length;
            count++;
            notifyAll();
        }

        public synchronized int take() throws InterruptedException {
            while (count == 0) wait();
            int token = buffer[out];
            out = (out + 1) % buffer.length;
            count--;
            notifyAll();
            return token;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Wallet a = new Wallet(1, 1000);
        Wallet b = new Wallet(2, 1000);
        Thread t1 = new Thread(() -> runTransfer(a, b));
        Thread t2 = new Thread(() -> runTransfer(b, a));
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("Total wallet balance: " + (a.balance + b.balance));

        TokenCounter counter = new TokenCounter();
        int[] collected = new int[2];
        Thread cook = new Thread(() -> {
            try {
                for (int token = 1; token <= 20; token++) counter.put(token);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Thread[] students = new Thread[2];
        for (int i = 0; i < students.length; i++) {
            final int id = i;
            students[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 10; j++) collected[id] += counter.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        cook.start();
        for (Thread student : students) student.start();
        cook.join();
        for (Thread student : students) student.join();
        System.out.println("Sum of tokens collected: " + (collected[0] + collected[1])
                + " (expected 210)");
    }

    private static void runTransfer(Wallet from, Wallet to) {
        try {
            for (int i = 0; i < 100; i++) transfer(from, to, 1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}