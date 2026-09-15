# Project Statement

## Problem Statement

Copying large numbers of files to a backup location sequentially is slow
and gives no visibility into what succeeded, what failed, or how long it
took. This project addresses that by building a command-line utility that
backs up a directory tree using multiple threads in parallel, with clear
logging and a summary report, so backups are faster and their outcome is
verifiable.

## Scope of the Project

**In scope**
- Local-to-local directory backup (source folder → destination folder).
- Configurable thread pool size for parallel file copying.
- Per-file success/failure logging and an end-of-run summary report.
- Command-line configuration via arguments.

**Out of scope**
- Cloud storage destinations (S3, Google Drive, etc.).
- A graphical interface — the tool is CLI-only by design.
- Real-time file-system watching or continuous sync.

## Target Users

- A student or individual who wants to back up a project folder or set of
  documents to another local drive or directory.
- Anyone who wants a fast, verifiable local backup without installing a
  heavyweight backup application.

## High-Level Features

- Recursively scans a source directory and catalogs every file (path,
  size, last-modified time).
- Copies files concurrently using a configurable, fixed-size thread pool.
- Isolates per-file failures — one bad file (permissions, disk full,
  missing path) is logged and skipped rather than aborting the whole run.
- Logs every operation through a thread-safe logger, safe under
  concurrent access from multiple worker threads.
- Generates a summary report at the end of each run: total files,
  succeeded, failed, total bytes copied, and elapsed time.
