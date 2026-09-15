package com.backuputility.logging;

import com.backuputility.engine.BackupResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class BackupLoggerTest {

    @TempDir
    Path tempDir;

    private BackupLogger logger;
    private Path logFile;

    @BeforeEach
    void setUp() throws IOException {
        logger = BackupLogger.getInstance();
        logFile = tempDir.resolve("test.log");
        logger.initialize(logFile);
    }

    @AfterEach
    void tearDown() {
        logger.close();
    }

    @Test
    void testConcurrentLogging() throws InterruptedException, IOException {
        int threadCount = 10;
        int logsPerThread = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await(); // wait for all threads to be ready
                    for (int j = 0; j < logsPerThread; j++) {
                        BackupResult result = BackupResult.success(
                                Path.of("file_" + threadId + "_" + j + ".txt"), 
                                10, 
                                1024
                        );
                        logger.log(result);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // start all threads at once
        endLatch.await(); // wait for all threads to finish
        executor.shutdown();

        logger.close(); // flush and close

        long lines = Files.lines(logFile).count();
        // 1 start line + 1 end line + (10 * 100) log lines = 1002
        assertEquals(1002, lines, "All concurrent logs should be written without loss.");
    }
}
