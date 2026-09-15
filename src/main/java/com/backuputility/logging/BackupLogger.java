package com.backuputility.logging;

import com.backuputility.engine.BackupResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Thread-safe singleton logger for recording backup outcomes.
 */
public class BackupLogger {
    
    private static volatile BackupLogger instance;
    private BufferedWriter writer;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private BackupLogger() {
        // Private constructor for singleton
    }

    public static BackupLogger getInstance() {
        if (instance == null) {
            synchronized (BackupLogger.class) {
                if (instance == null) {
                    instance = new BackupLogger();
                }
            }
        }
        return instance;
    }

    /**
     * Initializes the logger with the specified log file.
     * @param logFile Path to the log file.
     * @throws IOException If the file cannot be opened for writing.
     */
    public synchronized void initialize(Path logFile) throws IOException {
        if (writer != null) {
            close();
        }
        writer = new BufferedWriter(new FileWriter(logFile.toFile(), true));
        writer.write("--- Backup Session Started at " + LocalDateTime.now().format(timeFormatter) + " ---\n");
        writer.flush();
    }

    /**
     * Logs the outcome of a single task concurrently.
     * @param result The BackupResult to log.
     */
    public synchronized void log(BackupResult result) {
        if (writer == null) {
            // Fallback to console if not initialized
            System.out.println(formatLogEntry(result));
            return;
        }
        try {
            String entry = formatLogEntry(result);
            writer.write(entry + "\n");
            writer.flush();
            System.out.println(entry); // also print to console for live progress
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }

    private String formatLogEntry(BackupResult result) {
        String timestamp = LocalDateTime.now().format(timeFormatter);
        String base = String.format("[%s] [%s] %s (%d ms)", 
                timestamp, result.getStatus(), result.getPath(), result.getDurationMs());
        
        if (result.isSuccess()) {
            return base + " - " + result.getBytesCopied() + " bytes";
        } else {
            return base + " - ERROR: " + result.getErrorMessage();
        }
    }

    /**
     * Closes the logger.
     */
    public synchronized void close() {
        if (writer != null) {
            try {
                writer.write("--- Backup Session Ended at " + LocalDateTime.now().format(timeFormatter) + " ---\n");
                writer.close();
            } catch (IOException e) {
                System.err.println("Failed to close log writer: " + e.getMessage());
            } finally {
                writer = null;
            }
        }
    }
}
