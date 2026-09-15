package com.backuputility.scanner;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

/**
 * Value object describing one file to back up.
 * Contains the path, file size in bytes, and last modified time.
 */
public class FileMetadata {
    private final Path path;
    private final long sizeBytes;
    private final FileTime lastModified;
    private final String checksum; // optional for future enhancements

    public FileMetadata(Path path, long sizeBytes, FileTime lastModified) {
        this.path = path;
        this.sizeBytes = sizeBytes;
        this.lastModified = lastModified;
        this.checksum = null;
    }

    public FileMetadata(Path path, long sizeBytes, FileTime lastModified, String checksum) {
        this.path = path;
        this.sizeBytes = sizeBytes;
        this.lastModified = lastModified;
        this.checksum = checksum;
    }

    public Path getPath() {
        return path;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public FileTime getLastModified() {
        return lastModified;
    }

    public String getChecksum() {
        return checksum;
    }
}
