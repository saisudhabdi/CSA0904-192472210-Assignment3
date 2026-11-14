# Task 07 – Concurrent, Fault-Tolerant Result Processor

**Marks:** 10 | **Bloom's level:** K5 – Evaluate

## Run
```bash
javac ResultProcessor.java
java ResultProcessor
```

The program submits 10,000 independent score-processing tasks to fixed thread pools of 1, 2, 4 and 8 workers. It injects deterministic corrupt and missing-file failures, catches each task's failure locally, collects successful and failed outcomes in thread-safe concurrent queues, and retrieves every result through a Future.

## Design
- **Parallel work:** each student's score can be validated independently.
- **ExecutorService:** bounds the number of worker threads.
- **Callable:** returns a processing outcome.
- **Future:** allows the coordinator to wait for and retrieve each outcome.
- **ConcurrentLinkedQueue:** stores shared success and failure summaries safely.
- **Failure isolation:** each task catches its own exception so one bad record does not cancel the full run.

## Verification and measurement
Expected total processed is 10,000. A deterministic task fails when its ID is divisible by 97 or 89; all other generated scores are between 0 and 100. The exact success/failure counts and timings are printed by the program. Record your actual timing results for pool sizes 1, 2, 4 and 8 in a table; do not copy timings from another computer.

For mostly CPU-bound work, a pool near the available processor count is a reasonable starting point. More threads can add context-switching and memory overhead and do not guarantee lower elapsed time. Each task uses O(1) working memory; the demonstration retains O(n) Future objects and result summaries for n tasks. A production version can batch submissions or consume completions incrementally to reduce retained memory.

## Alternatives
Parallel streams use the common ForkJoinPool and offer less explicit lifecycle control. Creating one thread per file uses O(n) threads and large stack/scheduling overhead, making it unsuitable for 10,000 files. The executor gives explicit concurrency bounds and predictable failure aggregation.

The score records are deterministic in-memory stand-ins for file input so the program runs without external datasets. Extend ScoreTask to read actual files when integrating a real score-file directory. Add your console screenshot and measured timing table after execution.