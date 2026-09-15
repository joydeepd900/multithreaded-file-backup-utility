package com.backuputility.scanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Recursively scans the source directory and catalogs files to be backed up.
 */
public class FileScanner {
    
    /**
     * Recursively walks the source directory and builds a catalog of files.
     * 
     * @param root The source directory to scan.
     * @return A List of FileMetadata representing the files to back up.
     * @throws IOException If an I/O error occurs during the scan.
     */
    public List<FileMetadata> scan(Path root) throws IOException {
        if (!Files.exists(root) || !Files.isDirectory(root)) {
            throw new IllegalArgumentException("Source path must be an existing directory: " + root);
        }

        try (Stream<Path> paths = Files.walk(root)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        try {
                            return new FileMetadata(
                                    path,
                                    Files.size(path),
                                    Files.getLastModifiedTime(path)
                            );
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to read metadata for file: " + path, e);
                        }
                    })
                    .collect(Collectors.toList());
        }
    }
}
