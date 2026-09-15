package com.backuputility.scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileScannerTest {

    @TempDir
    Path tempDir;

    @Test
    void testScanEmptyDirectory() throws IOException {
        FileScanner scanner = new FileScanner();
        List<FileMetadata> files = scanner.scan(tempDir);
        assertTrue(files.isEmpty(), "Scan result should be empty for an empty directory.");
    }

    @Test
    void testScanWithFilesAndSubdirectories() throws IOException {
        // Setup a structure:
        // tempDir/
        //   file1.txt
        //   subdir/
        //     file2.txt
        
        Path file1 = tempDir.resolve("file1.txt");
        Files.writeString(file1, "Hello");

        Path subdir = tempDir.resolve("subdir");
        Files.createDirectory(subdir);

        Path file2 = subdir.resolve("file2.txt");
        Files.writeString(file2, "World!");

        FileScanner scanner = new FileScanner();
        List<FileMetadata> files = scanner.scan(tempDir);

        assertEquals(2, files.size(), "Should find exactly 2 files.");
        
        boolean foundFile1 = files.stream().anyMatch(f -> f.getPath().equals(file1) && f.getSizeBytes() == 5);
        boolean foundFile2 = files.stream().anyMatch(f -> f.getPath().equals(file2) && f.getSizeBytes() == 6);
        
        assertTrue(foundFile1, "Should contain file1.txt with size 5");
        assertTrue(foundFile2, "Should contain file2.txt with size 6");
    }

    @Test
    void testScanInvalidDirectory() {
        FileScanner scanner = new FileScanner();
        Path nonExistent = tempDir.resolve("doesNotExist");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            scanner.scan(nonExistent);
        });
        
        assertTrue(exception.getMessage().contains("Source path must be an existing directory"));
    }
}
