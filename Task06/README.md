# Task 06 – Deadlock and Inter-Thread Communication

**Marks:** 7 | **Bloom's level:** K4 – Analyse

## Run
```bash
javac ConcurrencyProblems.java
java ConcurrencyProblems
```

## Part A – Wallet transfer
Each transfer locks both wallets in ascending wallet-ID order. A global lock order prevents circular wait, one of the four necessary Coffman conditions for deadlock. The total balance should remain 2000.

## Part B – Bounded producer-consumer buffer
The token counter has capacity five. `put()` waits while full; `take()` waits while empty. Both methods use a while loop because a thread can wake spuriously or another thread can change the condition before it reacquires the monitor. State changes are followed by `notifyAll()`.

The sum of tokens 1 through 20 is 210, which is the expected collected sum. The buffer count remains between zero and five by construction.

## Concepts
`wait()` releases the monitor while waiting; `sleep()` pauses the current thread without releasing monitors it owns. Calling wait/notify outside synchronized ownership causes IllegalMonitorStateException. Replacing while with if is unsafe because a wake-up does not guarantee the condition is still true.

An `ArrayBlockingQueue<Integer>(5)` is a simpler bounded-buffer alternative. A timed `tryLock()` approach can avoid indefinite lock waiting but must handle timeout and retry outcomes. Each put/take is O(1); buffer space is O(5). Lock ordering is deterministic and easy to reason about.

Add a console screenshot after running locally.