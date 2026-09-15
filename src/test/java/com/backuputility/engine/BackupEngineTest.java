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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BackupEngineTest {

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
        config = new BackupConfig(sourceDir, destDir, 2);
    }

    @Test
    void testEngineExecutesAllTasks() throws IOException {
        List<FileMetadata> catalog = new ArrayList<>();
        
        // Create 3 valid files
        for (int i = 0; i < 3; i++) {
            Path file = sourceDir.resolve("file" + i + ".txt");
            Files.writeString(file, "data");
            catalog.add(new FileMetadata(file, 4, FileTime.fromMillis(System.currentTimeMillis())));
        }

        // Add 1 invalid file that won't exist on disk
        Path missingFile = sourceDir.resolve("missing.txt");
        catalog.add(new FileMetadata(missingFile, 0, FileTime.fromMillis(System.currentTimeMillis())));

        BackupEngine engine = new BackupEngine();
        List<BackupResult> results = engine.runBackup(catalog, config);

        assertEquals(4, results.size(), "Should have results for all 4 tasks.");

        long successCount = results.stream().filter(BackupResult::isSuccess).count();
        long failureCount = results.stream().filter(r -> !r.isSuccess()).count();

        assertEquals(3, successCount, "3 tasks should succeed.");
        assertEquals(1, failureCount, "1 task should fail.");
    }
}
