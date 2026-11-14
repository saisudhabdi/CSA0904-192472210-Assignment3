# Task 03 – Resource Safety, Debugging and Robust Code

**Marks:** 7 | **Bloom's level:** K4 – Analyse

## Run
```bash
javac SalesReportResources.java
java SalesReportResources
```

The program creates two sample store files under `sales-input`, writes a report using explicit close operations, then deliberately attempts a try-with-resources run containing a missing file. It reports the failure and checks the tracked reader count. Finally, it compares the valid manual and try-with-resources reports.

## Analysis
The manual implementation closes resources in a finally block. If a close operation itself throws, later closes in a simple sequence may not run; production code should close each resource independently or use try-with-resources. Try-with-resources closes resources in reverse declaration order and preserves secondary close failures as suppressed exceptions.

For f input files and r total records, reading and writing is O(r) time. The try-with-resources loop holds at most one input reader plus the output writer, O(1) open handles relative to f. Accumulating all report lines in the demonstration uses O(r) memory; a streaming production report can avoid that list.

A robust report generator should also validate data, write to a temporary output and atomically publish only after success, and log the filename and failure. Add your debugger observations and console screenshot after running locally.