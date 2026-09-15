package com.backuputility.engine;

import com.backuputility.app.BackupConfig;
import com.backuputility.logging.BackupLogger;
import com.backuputility.scanner.FileMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Owns the ExecutorService, submits BackupTasks, and collects the results.
 */
public class BackupEngine {

    /**
     * Executes the backup for the provided catalog of files concurrently.
     *
     * @param catalog The list of files to back up.
     * @param config The backup configuration.
     * @return A list of BackupResult for each file in the catalog.
     */
    public List<BackupResult> runBackup(List<FileMetadata> catalog, BackupConfig config) {
        int threadCount = config.getThreadCount();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<BackupTask> tasks = new ArrayList<>();

        for (FileMetadata file : catalog) {
            tasks.add(new BackupTask(file, config));
        }

        List<BackupResult> results = new ArrayList<>();
        BackupLogger logger = BackupLogger.getInstance();

        try {
            // invokeAll executes all tasks and blocks until all are complete
            List<Future<BackupResult>> futures = executor.invokeAll(tasks);
            
            for (Future<BackupResult> future : futures) {
                BackupResult result = future.get();
                logger.log(result);
                results.add(result);
            }
        } catch (InterruptedException e) {
            System.err.println("Backup process was interrupted!");
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.err.println("Unexpected execution exception: " + e.getMessage());
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        return results;
    }
}
