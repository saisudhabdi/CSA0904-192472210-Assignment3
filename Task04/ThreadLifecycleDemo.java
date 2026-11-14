public class ThreadLifecycleDemo {
    static class StationThread extends Thread {
        private final int scripts;
        StationThread(String name, int scripts) {
            super(name);
            this.scripts = scripts;
        }
        @Override
        public void run() {
            scan(scripts);
        }
    }

    static class StationTask implements Runnable {
        private final String station;
        private final int scripts;
        StationTask(String station, int scripts) {
            this.station = station;
            this.scripts = scripts;
        }
        @Override
        public void run() {
            scan(scripts);
        }
        private void scan(int count) {
            try {
                for (int i = 1; i <= count; i++) {
                    Thread.sleep(100);
                }
                System.out.println(station + " scanned " + count + " scripts.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(station + " interrupted.");
            }
        }
    }

    private static void scan(int count) {
        try {
            for (int i = 1; i <= count; i++) Thread.sleep(100);
            System.out.println(Thread.currentThread().getName() + " scanned " + count + " scripts.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] stations = {
            new StationThread("Station-1", 3),
            new Thread(new StationTask("Station-2", 4), "Station-2"),
            new Thread(new StationTask("Station-3", 2), "Station-3"),
            new Thread(new StationTask("Station-4", 5), "Station-4")
        };

        for (Thread station : stations) {
            station.start();
            System.out.println(station.getName() + " alive after start: " + station.isAlive());
        }
        for (Thread station : stations) station.join();
        System.out.println("All scanner stations completed.");
    }
}