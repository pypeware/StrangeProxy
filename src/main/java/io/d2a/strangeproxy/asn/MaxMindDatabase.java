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
        Config.MaxMind maxMindConfig = config.maxmind;
        File databaseFile = new File(maxMindConfig.databaseFile);

        // Update?
        MaxMindDatabaseUpdater updater = new MaxMindDatabaseUpdater(config, databaseFile);
        if (updater.check()) {
            System.out.println("[MaxMind] Updating MaxMind Database.");
            long start = System.currentTimeMillis();
            updater.updateSync(updater.buildUpdateUrl(), databaseFile);
            System.out.println("[MaxMind] Successfully updated database. Took " + (System.currentTimeMillis() - start) + "ms");
        }

        this.databaseReader = new DatabaseReader.Builder(databaseFile).build();
    }

}
