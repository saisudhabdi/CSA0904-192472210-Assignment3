# Task 01 – Handling Failure in a Bank ATM Simulation

**Marks:** 6 | **Bloom's level:** K4 – Analyse

## Implementation
`ATMWithdrawal.java` reads an account number and withdrawal amount, checks a card-reader status file, validates the transaction, and reports the remaining balance. The sample account balance is Rs. 5,000.

## Files
- `ATMWithdrawal.java` – ATM implementation.
- `card-reader.txt` – sample card-reader status; contains `ACTIVE`.

## Compile and run
```bash
javac ATMWithdrawal.java
java ATMWithdrawal
```
Run from this folder so the program can find `card-reader.txt`. Try: valid account + 1000; text instead of amount; negative amount; amount above 5000; missing card-reader.txt.

## Failure classification
| Failure | Java type | Classification | Handling |
|---|---|---|---|
| Missing card-reader file | NoSuchFileException | Checked | Specific catch |
| Other file access problem | IOException | Checked | IOException catch |
| Non-numeric amount | InputMismatchException | Unchecked | Wrapped as IllegalArgumentException |
| Empty account / invalid amount | IllegalArgumentException | Unchecked | Multi-catch |
| Inactive card | IllegalStateException | Unchecked | Multi-catch |
| JVM resource exhaustion | OutOfMemoryError | Error | Not caught |

Catch blocks are ordered from specific checked exceptions to broader checked exceptions, then application validation exceptions. A broad `catch (Exception e)` first would make later narrower catches unreachable. A single broad catch can be suitable at an application boundary for logging, but it loses precise recovery and error classification.

## Finally guarantee
The `finally` block runs on normal completion and handled exceptions (but cannot guarantee execution after abrupt JVM termination or process kill). Scanner is also closed using try-with-resources.

## Alternative: validation and error codes
Using `hasNextDouble()` and return codes avoids stack-trace construction for expected invalid input, but requires explicit checks and less descriptive propagation. Both approaches use O(1) auxiliary space for one transaction; ordinary validation is O(1). Exception construction is relatively expensive compared with returning a status code.

## Test record
Record the actual console output for each case on your machine. The finally message should appear for every handled test path.
