package com.backuputility.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class BackupConfigTest {

    @TempDir
    Path tempDir;

    @Test
    void testValidConfig() {
        Path source = tempDir.resolve("source");
        Path dest = tempDir.resolve("dest");
        assertDoesNotThrow(() -> {
            Files.createDirectory(source);
            BackupConfig config = new BackupConfig(source, dest, 4);
            assertEquals(source, config.getSourceDir());
            assertEquals(dest, config.getDestDir());
            assertEquals(4, config.getThreadCount());
        });
    }

    @Test
    void testSourceDoesNotExist() {
        Path source = tempDir.resolve("nonExistent");
        Path dest = tempDir.resolve("dest");
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new BackupConfig(source, dest, 4);
        });
        assertTrue(ex.getMessage().contains("Source directory does not exist"));
    }

    @Test
    void testDestinationIsNotDirectory() throws Exception {
        Path source = tempDir.resolve("source");
        Files.createDirectory(source);
        Path dest = tempDir.resolve("destFile.txt");
        Files.createFile(dest); // create a file instead of dir
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new BackupConfig(source, dest, 4);
        });
        assertTrue(ex.getMessage().contains("Destination path exists but is not a directory"));
    }

    @Test
    void testInvalidThreadCount() throws Exception {
        Path source = tempDir.resolve("source");
        Files.createDirectory(source);
        Path dest = tempDir.resolve("dest");
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            new BackupConfig(source, dest, 0);
        });
        assertTrue(ex.getMessage().contains("Thread count must be greater than 0"));
    }
}
