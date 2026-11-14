# Task 05 – Race Conditions in a Cinema Seat-Booking System

**Marks:** 6 | **Bloom's level:** K4 – Analyse

## Run
```bash
javac SeatBooking.java
java SeatBooking
```

One hundred threads compete for ten seats. A start gate increases contention. The synchronized method makes the check-then-book critical section atomic. Expected result: exactly ten seats sold.

## Analysis
Without synchronization, two threads can both observe a seat as free before either marks it booked, causing a double booking and an incorrect counter. The synchronized method locks the booking object for the complete method. The synchronized-block alternative locks only the critical section on the same monitor.

For s seats, the boolean array uses O(s) space. Each booking checks one array element, O(1) expected work, though contention can increase waiting time. `AtomicBoolean[]` with `compareAndSet` offers per-seat atomicity; `ReentrantLock` provides explicit lock control; `ConcurrentHashMap.putIfAbsent` can reserve a seat by key. These alternatives add per-seat objects or map-entry overhead.

The program measures one synchronized run. Repeat it ten times and record timing. To reproduce the race, create a separate unsynchronized version and run the same contention test; do not treat a particular number of failures as guaranteed because thread scheduling varies. Static synchronization is needed only when the shared booking state itself is static across all instances.

Add a screenshot of the executed result.