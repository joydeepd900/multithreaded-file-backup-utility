package com.backuputility.engine;

import com.backuputility.app.BackupConfig;
import com.backuputility.scanner.FileMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Callable;

/**
 * A task that copies a single file from the source to the destination.
 * Catches I/O errors and wraps them in a BackupResult to prevent killing the thread pool.
 */
public class BackupTask implements Callable<BackupResult> {

    private final FileMetadata fileMetadata;
    private final BackupConfig config;

    public BackupTask(FileMetadata fileMetadata, BackupConfig config) {
        this.fileMetadata = fileMetadata;
        this.config = config;
    }

    @Override
    public BackupResult call() {
        long startTime = System.currentTimeMillis();
        Path sourcePath = fileMetadata.getPath();
        
        try {
            // Determine the relative path from the source root
            Path relativePath = config.getSourceDir().relativize(sourcePath);
            Path destPath = config.getDestDir().resolve(relativePath);

            // Ensure parent directories exist in the destination
            if (destPath.getParent() != null && !Files.exists(destPath.getParent())) {
                Files.createDirectories(destPath.getParent());
            }

            // Copy the file
            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            
            long duration = System.currentTimeMillis() - startTime;
            return BackupResult.success(sourcePath, duration, fileMetadata.getSizeBytes());
            
        } catch (IOException e) {
            long duration = System.currentTimeMillis() - startTime;
            BackupException exception = new BackupException("Failed to copy file: " + sourcePath, e);
            return BackupResult.failure(sourcePath, duration, exception.getMessage());
        } catch (Exception e) {
            // Catching any other unexpected exceptions so it never bubbles up
            long duration = System.currentTimeMillis() - startTime;
            return BackupResult.failure(sourcePath, duration, "Unexpected error: " + e.getMessage());
        }
    }
}
