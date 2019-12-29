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

import com.maxmind.geoip2.DatabaseReader;
import io.d2a.strangeproxy.config.Config;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

public class MaxMindDatabase {

    @Getter
    private MaxMindDatabaseReader reader;

    @Getter
    private DatabaseReader databaseReader;

    public MaxMindDatabase(Config config) throws IOException {
        if (config == null
                || config.maxmind == null
                || config.maxmind.databaseFile == null
                || config.maxmind.databaseFile.length() < 1) {
            System.out.println("Invalid. " + config.maxmind.databaseFile);
            return;
        }

        Config.MaxMind maxMindConfig = config.maxmind;
        File databaseFile = new File(maxMindConfig.databaseFile);

        // Update?
        System.out.println("[MaxMind] Checking for updates ...");
        MaxMindDatabaseUpdater updater = new MaxMindDatabaseUpdater(config, databaseFile);
        if (updater.check()) {
            System.out.println("[MaxMind] Updating MaxMind Database.");
            long start = System.currentTimeMillis();
            updater.updateSync(updater.buildUpdateUrl(), new File(databaseFile.getPath() + ".gz"));
            System.out.println("[MaxMind] Successfully downloaded database. Took " + (System.currentTimeMillis() - start) + "ms");
            start = System.currentTimeMillis();
            System.out.println("[MaxMind] Unzipping downloaded database ...");
            new GZipFile(databaseFile.getPath() + ".gz", databaseFile.getPath()).unzip();
            System.out.println("[MaxMind] Successfully unzipped database. Took " + (System.currentTimeMillis() - start) + "ms");
        }

        this.databaseReader = new DatabaseReader.Builder(databaseFile)
                .build();

        this.reader = new MaxMindDatabaseReader(this);
    }

}
