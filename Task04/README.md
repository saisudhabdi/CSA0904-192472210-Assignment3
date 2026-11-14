# Task 04 – Thread Creation and the Thread Life Cycle

**Marks:** 7 | **Bloom's level:** K4 – Analyse

## Run
```bash
javac ThreadLifecycleDemo.java
java ThreadLifecycleDemo
```

Four scanner stations run concurrently. Station 1 extends `Thread`; the other stations use `Runnable`. The program uses `start()`, `sleep()`, `join()`, and `isAlive()`.

## Explanation
Calling `start()` asks the JVM to schedule a new thread and eventually invokes `run()` on that thread. Calling `run()` directly is an ordinary method call on the current thread. A running station may be in RUNNABLE or TIMED_WAITING while sleeping; after completion it enters TERMINATED. `join()` makes main wait until each station terminates before printing the final message.

The order of station completion is nondeterministic because scheduling and execution timing vary. Correctness must not depend on a particular order. Repeat the program ten times and record finish order. You can also test priorities and daemon behaviour separately; priority is only a scheduling hint.

For n scripts and p workers, idealized processing time is proportional to n/p batches when work is balanced. Each manually created thread consumes a stack and scheduling resources; a fixed ExecutorService pool bounds the number of worker threads.

Add your ten-run observations and a console screenshot after executing locally.