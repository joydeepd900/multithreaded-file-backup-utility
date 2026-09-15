package com.backuputility.app;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Immutable run configuration for the backup process.
 */
public class BackupConfig {
    private final Path sourceDir;
    private final Path destDir;
    private final int threadCount;

    public BackupConfig(Path sourceDir, Path destDir, int threadCount) {
        validate(sourceDir, destDir, threadCount);
        this.sourceDir = sourceDir;
        this.destDir = destDir;
        this.threadCount = threadCount;
    }

    private void validate(Path sourceDir, Path destDir, int threadCount) {
        if (sourceDir == null || destDir == null) {
            throw new IllegalArgumentException("Source and destination directories must not be null.");
        }
        
        if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
            throw new IllegalArgumentException("Source directory does not exist or is not a directory: " + sourceDir);
        }

        if (Files.exists(destDir)) {
            if (!Files.isDirectory(destDir)) {
                throw new IllegalArgumentException("Destination path exists but is not a directory: " + destDir);
            }
            if (!Files.isWritable(destDir)) {
                throw new IllegalArgumentException("Destination directory is not writable: " + destDir);
            }
        }
        
        if (threadCount <= 0) {
            throw new IllegalArgumentException("Thread count must be greater than 0, got: " + threadCount);
        }
    }

    public Path getSourceDir() {
        return sourceDir;
    }

    public Path getDestDir() {
        return destDir;
    }

    public int getThreadCount() {
        return threadCount;
    }
}
