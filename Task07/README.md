# Task 07 – Concurrent, Fault-Tolerant Result Processor

**Marks:** 10 | **Bloom's level:** K5 – Evaluate

## Run
```bash
javac ResultProcessor.java
java ResultProcessor
```

The program generates 10,000 score files in a `scores` directory. Files with IDs divisible by 89 are omitted to simulate missing files; IDs divisible by 97 contain invalid text to simulate corrupt files; remaining files contain integer scores from 0 to 100. It then processes all 10,000 paths with fixed thread pools of 1, 2, 4 and 8 workers.

## Design
- **Parallel work:** each student's score file can be read and validated independently.
- **ExecutorService:** bounds the number of worker threads.
- **Callable:** reads one file and returns a processing outcome.
- **Future:** allows the coordinator to retrieve each task result.
- **ConcurrentLinkedQueue:** stores shared success and failure summaries safely.
- **Failure isolation:** each task catches its own exception so one missing or corrupt file does not stop the run.

## Verification and measurement
Expected total processed is 10,000. There are 112 missing files (IDs divisible by 89) and 102 corrupt files (IDs divisible by 97 but not by 89). One ID is divisible by both and is counted as missing. The expected totals are **9,786 successes and 214 failures**. Verify that the four pool runs produce the same counts.

Record actual elapsed times printed by your computer for pool sizes 1, 2, 4 and 8. Do not copy timings from another computer.

For mostly CPU-bound work, a pool near the available processor count is a reasonable starting point. More threads can add context-switching and memory overhead and do not guarantee lower elapsed time. Each task uses O(1) working memory; the demonstration retains O(n) Future objects and result summaries for n tasks. A production version can batch submissions or consume completions incrementally to reduce retained memory.

## Alternatives
Parallel streams use the common ForkJoinPool and offer less explicit lifecycle control. Creating one thread per file uses O(n) threads and large stack/scheduling overhead, making it unsuitable for 10,000 files. The executor gives explicit concurrency bounds and predictable failure aggregation.

Add your console screenshot and measured timing table after execution.