import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class SeatBooking {
    private final boolean[] booked;
    private int bookingsMade;

    SeatBooking(int seats) {
        booked = new boolean[seats];
    }

    public synchronized boolean book(int seat) {
        if (booked[seat]) return false;
        booked[seat] = true;
        bookingsMade++;
        return true;
    }

    public boolean bookWithBlock(int seat) {
        synchronized (this) {
            if (booked[seat]) return false;
            booked[seat] = true;
            bookingsMade++;
            return true;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SeatBooking screen = new SeatBooking(10);
        AtomicInteger successful = new AtomicInteger();
        CountDownLatch ready = new CountDownLatch(100);
        CountDownLatch start = new CountDownLatch(1);
        Thread[] users = new Thread[100];

        long begin = System.nanoTime();
        for (int i = 0; i < users.length; i++) {
            final int seat = i % 10;
            users[i] = new Thread(() -> {
                ready.countDown();
                try {
                    start.await();
                    if (screen.book(seat)) successful.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            users[i].start();
        }

        ready.await();
        start.countDown();
        for (Thread user : users) user.join();
        long elapsed = System.nanoTime() - begin;

        System.out.println("Seats sold: " + screen.bookingsMade);
        System.out.println("Successful bookings: " + successful.get());
        System.out.println("Elapsed time (ms): " + elapsed / 1_000_000.0);
    }
}