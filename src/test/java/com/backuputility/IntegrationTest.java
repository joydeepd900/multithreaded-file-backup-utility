package com.backuputility;

import com.backuputility.app.BackupApp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void testEndToEndBackupWithFailure() throws IOException {
        Path sourceDir = tempDir.resolve("source");
        Path destDir = tempDir.resolve("dest");
        Files.createDirectory(sourceDir);
        Files.createDirectory(destDir);
        
        // Good file 1
        Path goodFile1 = sourceDir.resolve("good1.txt");
        Files.writeString(goodFile1, "Data 1");

        // Good file 2 (in subdir)
        Path subDir = sourceDir.resolve("subdir");
        Files.createDirectory(subDir);
        Path goodFile2 = subDir.resolve("good2.txt");
        Files.writeString(goodFile2, "Data 2");

        // Bad file (will fail to overwrite in dest)
        Path badFileSource = sourceDir.resolve("bad.txt");
        Files.writeString(badFileSource, "Secret");
        
        // Create the destination file and make it read-only to force AccessDeniedException
        Path badFileDest = destDir.resolve("bad.txt");
        Files.writeString(badFileDest, "Old");
        badFileDest.toFile().setWritable(false, false);
        
        String[] args = {
            "--source", sourceDir.toString(),
            "--dest", destDir.toString(),
            "--threads", "2"
        };

        assertDoesNotThrow(() -> {
            BackupApp.main(args);
        });

        // Verify good files are copied
        assertTrue(Files.exists(destDir.resolve("good1.txt")), "good1.txt should be copied");
        assertTrue(Files.exists(destDir.resolve("subdir/good2.txt")), "good2.txt should be copied");
        
        // Check log for failure
        Path logFile = destDir.resolve("backup_run.log");
        assertTrue(Files.exists(logFile));
        String logContent = Files.readString(logFile);
        
        assertTrue(logContent.contains("good1.txt"));
        assertTrue(logContent.contains("good2.txt"));
        assertTrue(logContent.contains("ERROR"), "Log should contain an ERROR due to the un-writable file");
        
        // Restore writability so tempDir cleanup doesn't fail
        badFileDest.toFile().setWritable(true, false);
    }
}
