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

package io.d2a.strangeproxy;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.message.TextMessage;
import com.github.steveice10.mc.protocol.data.status.PlayerInfo;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.VersionInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.ServerClosedEvent;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import io.d2a.strangeproxy.asn.MaxMindDatabase;
import io.d2a.strangeproxy.config.Config;
import io.d2a.strangeproxy.mirroring.StrangeProxyClient;
import io.d2a.strangeproxy.placeholder.PlaceholderReplacer;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;

public class StrangeProxy implements Runnable {

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final File CONFIG_FILE = new File("config.toml");

    @Getter
    private static final Logger logger = LogManager.getLogger(StrangeProxy.class);

    ////////////////////////////////////////////////////////////////////////
    @Getter
    private static StrangeProxy instance;
    @Getter
    private static Config config;

    // For log4j 2
    static {
        @SuppressWarnings("unused")
        Class<?>[] classes = new Class<?>[]{
                ConsoleAppender.class,
                PatternLayout.class
        };
    }

    @Getter
    private MaxMindDatabase database;

    ////////////////////////////////////////////////////////////////////////

    /*
     * Constructor
     */
    public StrangeProxy() {
        StrangeProxy.instance = this;

        getLogger().info("Loading config ...");

        // Read config
        try {

            // Config exists?
            if (CONFIG_FILE.exists() && !CONFIG_FILE.isFile()) {
                throw new RuntimeException("Invalid file.");
            }

            if (!CONFIG_FILE.exists()) {
                throw new RuntimeException("Config file does not exist!");
            }

            // Parse config
            StrangeProxy.config = new Toml()
                    .read(CONFIG_FILE)
                    .to(Config.class);

        } catch (RuntimeException rtex) {
            getLogger().error("Could not read config", rtex);
            System.exit(1);
            return;
        }
//        System.out.println(GSON.toJson(StrangeProxy.config));

        // Database
        getLogger().info("Loading database ...");
        try {
            this.database = new MaxMindDatabase(config);
        } catch (IOException e) {
            getLogger().error("Could not read database", e);
            System.exit(1);
            return;
        }

        run();
    }

    /*
     * Main
     */
    public static void main(String[] args) {
        new StrangeProxy();
    }

    @Override
    public void run() {
        final String host = config.strangeproxy.host;
        final int port = config.strangeproxy.port;

        Server server = new Server(
                host,
                port,
                MinecraftProtocol.class,
                new TcpSessionFactory(Proxy.NO_PROXY)
        );

        // Global Flags
        server.setGlobalFlag(MinecraftConstants.AUTH_PROXY_KEY, Proxy.NO_PROXY);
        server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, false);

        server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, (ServerInfoBuilder) session -> config.serverStatusInfo != null
                        ? config.serverStatusInfo
                        : new ServerStatusInfo(
                        new VersionInfo(MinecraftConstants.GAME_VERSION, MinecraftConstants.PROTOCOL_VERSION),
                        new PlayerInfo(config.status.maxPlayers, config.status.currentPlayers, new GameProfile[0]),
                        new TextMessage(PlaceholderReplacer.coloredApply(session, config.status.motd)),
                        null
                )
        );

        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 100);
        server.addListener(new ServerAdapter() {
            @Override
            public void serverClosed(ServerClosedEvent event) {
                getLogger().warn("-- Server closed. --");
            }
        });

        getLogger().info("Binding on port: " + port + ", host: " + host + " ...");
        server.bind();
        getLogger().info("Done binding!");

        // Mirroring Task
        new StrangeProxyClient(config)
                .startUpdateTask();
    }

}
