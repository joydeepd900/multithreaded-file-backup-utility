package com.backuputility.engine;

/**
 * Checked exception wrapping I/O failures during backup.
 */
public class BackupException extends Exception {
    public BackupException(String message) {
        super(message);
    }

    public BackupException(String message, Throwable cause) {
        super(message, cause);
    }
}
