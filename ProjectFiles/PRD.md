# PRD — Multi-threaded File Backup Utility

## 1. Problem statement

Copying large numbers of files to a backup location sequentially is slow
and gives no visibility into what succeeded, what failed, or how long it
took. This project builds a command-line utility that backs up a directory
tree using multiple threads in parallel, with clear logging and a summary
report, so backups are faster and their outcome is verifiable.

## 2. Objectives

- Apply Java OOP, exception handling, and multithreading concepts from the
  course to a real, runnable tool.
- Demonstrate correct use of a thread pool for parallel I/O without race
  conditions.
- Produce a CLI tool that is fully executable and verifiable from a
  terminal, with no GUI dependency.

## 3. Target users

- A student or individual who wants to back up a project folder or set of
  documents to another local drive or directory.
- Course evaluators reviewing the project for correct use of Java
  concurrency, OOP design, and exception handling.

## 4. Scope

**In scope**
- Local-to-local directory backup (source folder → destination folder).
- Configurable thread pool size.
- Per-file success/failure logging and an end-of-run summary report.
- Command-line configuration (arguments or an interactive menu).

**Out of scope**
- Cloud storage destinations (S3, Google Drive, etc.) — noted as a future
  enhancement.
- A graphical interface.
- Real-time file-system watching / continuous sync.

## 5. Functional requirements

| ID | Requirement | Module |
|---|---|---|
| FR1 | The system shall recursively scan a given source directory and catalog every file (path, size, last-modified time). | Scan & catalog |
| FR2 | The system shall allow the user to specify source directory, destination directory, and thread pool size via CLI. | Scan & catalog |
| FR3 | The system shall copy all cataloged files to the destination directory using multiple worker threads running concurrently. | Backup engine |
| FR4 | The system shall continue processing remaining files if an individual file copy fails, recording the failure instead of aborting. | Backup engine |
| FR5 | The system shall log every file operation (success, failure, or skip) to a run log, safely under concurrent access from multiple threads. | Logs & reports |
| FR6 | The system shall generate a summary report at the end of the run: total files, succeeded, failed, total bytes copied, and elapsed time. | Logs & reports |

## 6. Non-functional requirements

| ID | Requirement |
|---|---|
| NFR1 — Performance | Backing up N files with a thread pool of size T should be measurably faster than sequential copying for T > 1 and N large enough to saturate the pool. |
| NFR2 — Reliability | A single file failure (permissions, missing path, disk full) must not crash the application or stop other files from being copied. |
| NFR3 — Scalability | Thread pool size must be configurable at runtime, not hard-coded, so the tool scales with available CPU cores. |
| NFR4 — Maintainability | Code is organized into single-responsibility classes across clearly named packages (see architecture.md). |
| NFR5 — Logging/monitoring | Every run produces a log file and a human-readable summary report, without requiring a debugger to see what happened. |
| NFR6 — Resource efficiency | File copies use buffered NIO streams; the thread pool is bounded (no unbounded thread creation per file). |

## 7. User stories / CLI interaction flow

1. User runs `java -jar backup-utility.jar --source ./docs --dest ./backup --threads 4`.
2. The tool scans `./docs`, prints how many files were found.
3. The tool copies files in parallel, printing progress as each file
   finishes (or fails).
4. On completion, the tool prints a summary: files copied, failed, total
   size, elapsed time — and writes the same to a log file.

## 8. Success criteria

- All functional requirements (FR1–FR6) are implemented and demonstrable
  from the terminal.
- A backup of a directory with intentionally mixed content (including at
  least one file that will fail to copy) completes without crashing and
  reports the failure clearly.
- Increasing the thread pool size on a large file set visibly reduces
  total run time versus a single-threaded run.

## 9. Constraints & assumptions

- Source and destination are both accessible from the local filesystem
  (no network paths required for the base implementation).
- Java 17+ is available in the evaluation environment.
- No external database is required — this satisfies the "no DB / no GUI"
  path in the course's project guidelines, simplifying setup for
  evaluators.

## 10. Milestones

| Milestone | Deliverable |
|---|---|
| M1 | `FileScanner` + `FileMetadata` working, unit tested |
| M2 | `BackupEngine` + `BackupTask` running sequentially (no threading yet) |
| M3 | Thread pool integrated, `BackupLogger` made thread-safe |
| M4 | `ReportGenerator` + final CLI polish |
| M5 | README, architecture doc, UML diagrams, project report finalized |
