package org.example.agency.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BackupService {

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    private final String dbName = "agency";
    private final String backupDir = "backups/";

    public String createBackup() throws IOException, InterruptedException {
        File dir = new File(backupDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = "backup_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".sql";
        String filePath = backupDir + fileName;

        ProcessBuilder pb = new ProcessBuilder(
                "pg_dump",
                "-U", dbUser,
                "-f", filePath,
                dbName
        );
        pb.environment().put("PGPASSWORD", dbPassword);
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            return fileName;
        } else {
            throw new RuntimeException("Backup failed with exit code: " + exitCode);
        }
    }

    // Every day at 3 AM
    @Scheduled(cron = "0 0 3 * * *")
    public void scheduledBackup() {
        try {
            System.out.println("Starting scheduled backup...");
            createBackup();
            System.out.println("Scheduled backup completed successfully.");
        } catch (Exception e) {
            System.err.println("Scheduled backup failed: " + e.getMessage());
        }
    }

    public void restoreBackup(String fileName) throws IOException, InterruptedException {
        String filePath = backupDir + fileName;
        ProcessBuilder pb = new ProcessBuilder(
                "psql",
                "-U", dbUser,
                "-d", dbName,
                "-f", filePath
        );
        pb.environment().put("PGPASSWORD", dbPassword);
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Restore failed with exit code: " + exitCode);
        }
    }
}
