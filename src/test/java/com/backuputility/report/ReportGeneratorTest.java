package com.backuputility.report;

import com.backuputility.engine.BackupResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportGeneratorTest {

    @TempDir
    Path tempDir;

    @Test
    void testGenerateReport() throws IOException {
        Path logFile = tempDir.resolve("test-report.log");

        List<BackupResult> results = Arrays.asList(
                BackupResult.success(Path.of("file1.txt"), 100, 1024),
                BackupResult.success(Path.of("file2.txt"), 150, 2048),
                BackupResult.failure(Path.of("file3.txt"), 50, "Access denied")
        );

        ReportGenerator generator = new ReportGenerator();
        generator.generate(results, logFile);

        assertTrue(Files.exists(logFile), "Report should be written to log file.");
        String content = Files.readString(logFile);
        
        assertTrue(content.contains("Total Files Processed : 3"));
        assertTrue(content.contains("Successfully Copied   : 2"));
        assertTrue(content.contains("Failed to Copy        : 1"));
        assertTrue(content.contains("Total Bytes Copied    : 3072 bytes")); // 1024 + 2048
        assertTrue(content.contains("Total Time Spent (ms) : 300 ms")); // 100 + 150 + 50
    }
}
