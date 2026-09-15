package com.backuputility.report;

import com.backuputility.engine.BackupResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Builds and outputs the end-of-run summary report.
 */
public class ReportGenerator {

    /**
     * Generates a summary report based on the backup results, prints it to the console,
     * and appends it to the specified log file.
     *
     * @param results The list of backup outcomes.
     * @param logFile The path to the log file.
     */
    public void generate(List<BackupResult> results, Path logFile) {
        long totalFiles = results.size();
        long successCount = results.stream().filter(BackupResult::isSuccess).count();
        long failureCount = totalFiles - successCount;
        
        long totalBytesCopied = results.stream()
                .filter(BackupResult::isSuccess)
                .mapToLong(BackupResult::getBytesCopied)
                .sum();
                
        long totalDurationMs = results.stream()
                .mapToLong(BackupResult::getDurationMs)
                .sum();

        StringBuilder report = new StringBuilder();
        report.append("\n=========================================\n");
        report.append("          BACKUP SUMMARY REPORT          \n");
        report.append("=========================================\n");
        report.append(String.format("Total Files Processed : %d\n", totalFiles));
        report.append(String.format("Successfully Copied   : %d\n", successCount));
        report.append(String.format("Failed to Copy        : %d\n", failureCount));
        report.append(String.format("Total Bytes Copied    : %d bytes\n", totalBytesCopied));
        report.append(String.format("Total Time Spent (ms) : %d ms (across threads)\n", totalDurationMs));
        report.append("=========================================\n");

        String reportStr = report.toString();
        System.out.println(reportStr);

        try {
            Files.writeString(logFile, reportStr, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to write report to log file: " + e.getMessage());
        }
    }
}
