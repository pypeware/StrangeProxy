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
import com.github.steveice10.mc.protocol.ServerLoginHandler;
import com.github.steveice10.mc.protocol.data.message.TextMessage;
import com.github.steveice10.mc.protocol.data.status.PlayerInfo;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.VersionInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.ServerClosedEvent;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import io.d2a.strangeproxy.asn.MaxMindDatabase;
import io.d2a.strangeproxy.config.Config;
import io.d2a.strangeproxy.placeholder.PlaceholderReplacer;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;

public class StrangeProxy implements Runnable {

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final File CONFIG_FILE = new File("config.toml");

    ////////////////////////////////////////////////////////////////////////
    @Getter
    private static StrangeProxy instance;
    @Getter
    private static Config config;
    @Getter
    private MaxMindDatabase database;

    ////////////////////////////////////////////////////////////////////////
    @Getter
    private String host;
    @Getter
    private int port;

    ////////////////////////////////////////////////////////////////////////

    /*
     * Constructor
     */
    public StrangeProxy(String host, int port) {
        this.host = host;
        this.port = port;

        // Read config
        try {

            // Config exists?
            if (CONFIG_FILE.exists() && !CONFIG_FILE.isFile()) {
                throw new RuntimeException("Invalid file.");
            }

            if (!CONFIG_FILE.exists()) {
                throw new RuntimeException("Config file does not exist!");
            }

            StrangeProxy.config = new Toml().read(CONFIG_FILE).to(Config.class);
        } catch (RuntimeException rtex) {
            rtex.printStackTrace();
            System.exit(1);
            return;
        }

        System.out.println("Loaded config:");
        System.out.println(GSON.toJson(StrangeProxy.config));

        // Database
        try {
            this.database = new MaxMindDatabase(config);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }

        //run();
    }

    /*
     * Main
     */
    public static void main(String[] args) {
        new StrangeProxy("127.0.0.1", 25566);
    }

    @Override
    public void run() {
        Server server = new Server(
                host,
                port,
                MinecraftProtocol.class,
                new TcpSessionFactory(Proxy.NO_PROXY)
        );

        // Global Flags
        server.setGlobalFlag(MinecraftConstants.AUTH_PROXY_KEY, Proxy.NO_PROXY);
        server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, false);

        server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, new ServerInfoBuilder() {
            @Override
            public ServerStatusInfo buildInfo(Session session) {
                return new ServerStatusInfo(
                        new VersionInfo(MinecraftConstants.GAME_VERSION, MinecraftConstants.PROTOCOL_VERSION),
                        new PlayerInfo(config.status.maxPlayers, config.status.currentPlayers, new GameProfile[0]),
                        new TextMessage(PlaceholderReplacer.coloredApply(session, config.status.motd)),
                        null
                );
            }
        });

        server.setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, (ServerLoginHandler) session -> session.disconnect(""));
        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 100);
        server.addListener(new ServerAdapter() {
            @Override
            public void serverClosed(ServerClosedEvent event) {
                System.out.println("-- Server closed. --");
            }
        });

        System.out.println("-> Binding on port: " + port + ", host: " + host + " ...");
        server.bind();
        System.out.println("-> Done!");
    }

}
