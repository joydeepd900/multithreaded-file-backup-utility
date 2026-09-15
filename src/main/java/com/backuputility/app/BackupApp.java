package com.backuputility.app;

import com.backuputility.engine.BackupEngine;
import com.backuputility.engine.BackupResult;
import com.backuputility.logging.BackupLogger;
import com.backuputility.report.ReportGenerator;
import com.backuputility.scanner.FileMetadata;
import com.backuputility.scanner.FileScanner;

import java.nio.file.Path;
import java.util.List;

/**
 * Main entry point for the Multi-threaded File Backup Utility.
 */
public class BackupApp {

    public static void main(String[] args) {
        String sourceStr = null;
        String destStr = null;
        int threads = 4; // default

        // Simple CLI argument parsing
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--source":
                    if (i + 1 < args.length) sourceStr = args[++i];
                    break;
                case "--dest":
                    if (i + 1 < args.length) destStr = args[++i];
                    break;
                case "--threads":
                    if (i + 1 < args.length) {
                        try {
                            threads = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid thread count. Using default: " + threads);
                        }
                    }
                    break;
            }
        }

        if (sourceStr == null || destStr == null) {
            System.err.println("Usage: java -jar backup-utility.jar --source <dir> --dest <dir> [--threads <num>]");
            System.exit(1);
        }

        Path source = Path.of(sourceStr);
        Path dest = Path.of(destStr);
        BackupConfig config = null;

        try {
            config = new BackupConfig(source, dest, threads);
        } catch (IllegalArgumentException e) {
            System.err.println("Configuration Error: " + e.getMessage());
            System.exit(1);
        }

        BackupApp app = new BackupApp();
        app.run(config);
    }

    public void run(BackupConfig config) {
        BackupLogger logger = BackupLogger.getInstance();
        try {
            // Ensure dest directory exists before creating log file
            if (!java.nio.file.Files.exists(config.getDestDir())) {
                java.nio.file.Files.createDirectories(config.getDestDir());
            }

            Path logFile = config.getDestDir().resolve("backup_run.log");
            logger.initialize(logFile);

            System.out.println("Scanning source directory: " + config.getSourceDir());
            FileScanner scanner = new FileScanner();
            List<FileMetadata> catalog = scanner.scan(config.getSourceDir());
            
            System.out.println("Found " + catalog.size() + " files to back up.");
            System.out.println("Starting backup using " + config.getThreadCount() + " threads...");

            BackupEngine engine = new BackupEngine();
            List<BackupResult> results = engine.runBackup(catalog, config);

            ReportGenerator reportGenerator = new ReportGenerator();
            reportGenerator.generate(results, logFile);

        } catch (Exception e) {
            System.err.println("An unexpected error occurred during backup: " + e.getMessage());
            e.printStackTrace();
        } finally {
            logger.close();
        }
    }
}
