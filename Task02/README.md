# Task 02 – User-Defined Exceptions and Propagation

**Marks:** 7 | **Bloom's level:** K4 – Analyse

## Run
```bash
javac ExamPortal.java
java ExamPortal
```

## Design
- `ExamException` is the checked base exception.
- `ExamWindowClosedException`, `StudentNotRegisteredException`, and `SubmissionStorageException` are checked domain exceptions.
- `SubmissionTooLargeException` is unchecked because it represents a caller/input validation violation.
- Storage translates low-level `IOException` into a meaningful `SubmissionStorageException` and retains the original exception as its cause.

## Propagation
Controller → ExamService → SubmissionStorage. The service propagates checked domain failures; the controller catches them and displays a user-facing message. The cause is available through `getCause()`.

## Test scenarios
The main method covers successful storage, unregistered student, closed exam window, storage quota failure (800 KB exceeds the 500 KB storage threshold), and an oversized file (over 1000 KB). For the quota case, the cause should remain `Disk quota exceeded`.

## If the cause is discarded
Calling `new SubmissionStorageException(message, null)` means `getCause()` returns null and the low-level reason is lost. The exception chain also retains stack-trace data for diagnosis.

## Checked versus unchecked
If domain exceptions extended `RuntimeException`, callers would not be forced to declare or catch them. Checked exceptions make recoverable business outcomes explicit in method contracts. Exception creation and stack-trace capture have non-constant practical overhead; normal validation work is O(1), and exception-chain space is proportional to the number of retained causes.

Capture your own console screenshot after compiling and running.