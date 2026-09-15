package com.backuputility.engine;

import com.backuputility.app.BackupConfig;
import com.backuputility.scanner.FileMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import static org.junit.jupiter.api.Assertions.*;

class BackupTaskTest {

    @TempDir
    Path tempDir;

    private Path sourceDir;
    private Path destDir;
    private BackupConfig config;

    @BeforeEach
    void setUp() throws IOException {
        sourceDir = tempDir.resolve("source");
        destDir = tempDir.resolve("dest");
        Files.createDirectory(sourceDir);
        Files.createDirectory(destDir);
        config = new BackupConfig(sourceDir, destDir, 4);
    }

    @Test
    void testSuccessfulCopy() throws IOException {
        Path file = sourceDir.resolve("test.txt");
        Files.writeString(file, "content");
        FileMetadata metadata = new FileMetadata(file, 7, FileTime.fromMillis(System.currentTimeMillis()));

        BackupTask task = new BackupTask(metadata, config);
        BackupResult result = task.call();

        assertTrue(result.isSuccess(), "Copy should be successful.");
        assertEquals(file, result.getPath());
        assertEquals(7, result.getBytesCopied());

        Path destFile = destDir.resolve("test.txt");
        assertTrue(Files.exists(destFile), "Destination file should exist.");
        assertEquals("content", Files.readString(destFile));
    }

    @Test
    void testCopyFailsWhenSourceIsUnreadable() {
        Path file = sourceDir.resolve("unreadable.txt");
        // Creating the metadata but not the file, this simulates a missing file or unreadable file.
        // We just don't create it, so Files.copy will throw NoSuchFileException.
        FileMetadata metadata = new FileMetadata(file, 7, FileTime.fromMillis(System.currentTimeMillis()));

        BackupTask task = new BackupTask(metadata, config);
        BackupResult result = task.call();

        assertFalse(result.isSuccess(), "Copy should fail.");
        assertEquals(file, result.getPath());
        assertNotNull(result.getErrorMessage(), "Should contain error message");
        assertTrue(result.getErrorMessage().contains("Failed to copy file"));
    }
}
