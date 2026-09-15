package com.backuputility.engine;

import java.nio.file.Path;

/**
 * Outcome of one file backup task.
 */
public class BackupResult {

    public enum Status {
        SUCCESS,
        FAILED
    }

    private final Path path;
    private final Status status;
    private final long durationMs;
    private final String errorMessage;
    private final long bytesCopied;

    public BackupResult(Path path, Status status, long durationMs, long bytesCopied, String errorMessage) {
        this.path = path;
        this.status = status;
        this.durationMs = durationMs;
        this.bytesCopied = bytesCopied;
        this.errorMessage = errorMessage;
    }

    public static BackupResult success(Path path, long durationMs, long bytesCopied) {
        return new BackupResult(path, Status.SUCCESS, durationMs, bytesCopied, null);
    }

    public static BackupResult failure(Path path, long durationMs, String errorMessage) {
        return new BackupResult(path, Status.FAILED, durationMs, 0, errorMessage);
    }

    public Path getPath() {
        return path;
    }

    public Status getStatus() {
        return status;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public long getBytesCopied() {
        return bytesCopied;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    
    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
}
