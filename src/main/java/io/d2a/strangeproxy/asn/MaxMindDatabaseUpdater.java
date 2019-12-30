/*
 * Copyright (c) 2019.
 * Created at 29.12.2019
 * ---------------------------------------------
 * @author hyWse
 * @see https://hywse.eu
 * ---------------------------------------------
 * If you have any questions, please contact
 * E-Mail: admin@hywse.eu
 * Discord: hyWse#0126
 */

package io.d2a.strangeproxy.asn;

import io.d2a.strangeproxy.StrangeProxy;
import io.d2a.strangeproxy.config.Config;
import lombok.SneakyThrows;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class MaxMindDatabaseUpdater {

    private File databaseFile;
    private File updateInfoFile;
    private long updateInterval;
    private Config config;

    public MaxMindDatabaseUpdater(Config config, File databaseFile) {
        this.config = config;
        this.databaseFile = databaseFile;
        this.updateInfoFile = new File(databaseFile.getParent(), "update_info.txt");

        this.updateInterval = TimeUnit.DAYS.toMillis(config.maxmind.updateInterval);
    }

    /**
     * @return TRUE if you should update, FALSE if not
     */
    @SneakyThrows
    public boolean check() {

        if (!databaseFile.exists()) {
            StrangeProxy.getLogger().info("[MaxMind] Update-Reason: Datebase-File does not exist!");
            return true;
        }

        if (!updateInfoFile.exists()) {
            StrangeProxy.getLogger().info("[MaxMind] Update-Reason: Updateinfo-File does not exist!");
            return true;
        }

        long lastUpdate = Long.parseLong(Files.readAllLines(updateInfoFile.toPath()).get(0));
        if (lastUpdate + updateInterval < System.currentTimeMillis()) {
            StrangeProxy.getLogger().info("[MaxMind] Update-Reason: Interval (" + updateInterval + ") catched");
            return true;
        }

        return false;
    }

    public String buildUpdateUrl() {
        return config.maxmind.url
                .replace("{license_key}", config.maxmind.licenseKey);
    }

    public void updateSync(String url, File outputFile) throws IOException {

        if (!outputFile.getParentFile().exists()) {
            StrangeProxy.getLogger().warn("[MaxMind] Output-Directory does not exist. Creating: " +
                    ((outputFile.getParentFile().mkdirs())
                            ? "success" : "error"));
        }

        if (outputFile.exists()) {
            StrangeProxy.getLogger().warn("[MaxMind] Out-File already exists. Deleting: " +
                    ((outputFile.delete())
                            ? "success" : "error"));
        }

        // Download file
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }

        // Download successful
        // write info
        Files.write(updateInfoFile.toPath(),
                Collections.singletonList(String.valueOf(System.currentTimeMillis())));
    }

}
