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

package io.d2a.strangeproxy.config;

import com.google.gson.annotations.SerializedName;

public class Config {

    public MaxMind maxmind;
    public Status status;
    public Mirroring mirroring;

    public static class MaxMind {
        @SerializedName("url")
        public String url;

        @SerializedName("license_key")
        public String licenseKey;

        @SerializedName("database_file")
        public String databaseFile;

        @SerializedName("update_interval")
        public int updateInterval;
    }

    public static class Status {
        @SerializedName("current_players")
        public int currentPlayers = 69;

        @SerializedName("max_players")
        public int maxPlayers = 88;

        @SerializedName("motd")
        public String motd = "&c&lBitte deaktivere deinen VPN.";
    }

    public static class Mirroring {
        @SerializedName("enabled")
        public boolean enabled;

        @SerializedName("host")
        public String host;

        @SerializedName("port")
        public int port;
    }

}
