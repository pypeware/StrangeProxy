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
import lombok.Getter;

import java.net.Proxy;

public class StrangeProxy implements Runnable {

    final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Getter
    private String host;
    //////////////////////////////////////////////////
    @Getter
    private int port;

    /*
     * Main
     */
    public static void main(String[] args) {
        new StrangeProxy()
                .setHost("127.0.0.1")
                .setPort(25565)
                .run();
    }

    public StrangeProxy setHost(String host) {
        this.host = host;
        return this;
    }

    public StrangeProxy setPort(int port) {
        this.port = port;
        return this;
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
        server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, (ServerInfoBuilder) session -> new ServerStatusInfo(
                new VersionInfo(MinecraftConstants.GAME_VERSION, MinecraftConstants.PROTOCOL_VERSION),
                new PlayerInfo(10000, 1337, new GameProfile[0]),
                new TextMessage("§c§lBitte deaktiviere deinen VPN!"),
                null
        ));
        server.setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, new ServerLoginHandler() {
            @Override
            public void loggedIn(Session session) {
//                System.out.println("+ Session: " + session.getHost());
                session.disconnect("");
            }
        });
        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 100);
        server.addListener(new ServerAdapter() {
            @Override
            public void serverClosed(ServerClosedEvent event) {
                System.out.println("-- Server closed. --");
            }

//            @Override
//            public void sessionAdded(SessionAddedEvent event) {
//                System.out.println("New Session: " + gson.toJson(event.getSession()));
//
//                event.getSession().addListener(new SessionAdapter() {
//                    @Override
//                    public void packetReceived(PacketReceivedEvent event) {
//                        if(event.getPacket() instanceof ClientChatPacket) {
//
//                            System.out.println(gson.toJson(event.getPacket()));
//
//                            ClientChatPacket packet = event.getPacket();
//                            GameProfile profile = event.getSession().getFlag(MinecraftConstants.PROFILE_KEY);
//                            System.out.println(profile.getName() + ": " + packet.getMessage());
//                            Message msg = new TextMessage("Hello, ").setStyle(new MessageStyle().setColor(ChatColor.GREEN));
//                            Message name = new TextMessage(profile.getName()).setStyle(new MessageStyle().setColor(ChatColor.AQUA).addFormat(ChatFormat.UNDERLINED));
//                            Message end = new TextMessage("!");
//                            msg.addExtra(name);
//                            msg.addExtra(end);
//                            event.getSession().send(new ServerChatPacket(msg));
//                        }
//                    }
//                });

//                event.getSession().addListener(new SessionAdapter() {
//                    @Override
//                    public void connected(ConnectedEvent event) {
//                        event.getSession().disconnect("§aHi");
//                    }
//                });
//                event.getSession().disconnect("§c§lNö!", true);
//                event.getSession().addListener(new SessionAdapter() {
//                    @Override
//                    public void packetReceived(PacketReceivedEvent event) {
//                        if(event.getPacket() instanceof ClientChatPacket) {
//                            ClientChatPacket packet = event.getPacket();
//                            GameProfile profile = event.getSession().getFlag(MinecraftConstants.PROFILE_KEY);
//                            System.out.println(profile.getName() + ": " + packet.getMessage());
//                            Message msg = new TextMessage("Hello, ").setStyle(new MessageStyle().setColor(ChatColor.GREEN));
//                            Message name = new TextMessage(profile.getName()).setStyle(new MessageStyle().setColor(ChatColor.AQUA).addFormat(ChatFormat.UNDERLINED));
//                            Message end = new TextMessage("!");
//                            msg.addExtra(name);
//                            msg.addExtra(end);
//                            event.getSession().send(new ServerChatPacket(msg));
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void sessionRemoved(SessionRemovedEvent event) {
//                MinecraftProtocol protocol = (MinecraftProtocol) event.getSession().getPacketProtocol();
//                if(protocol.getSubProtocol() == SubProtocol.GAME) {
//                    System.out.println("Closing server.");
//                    event.getServer().close(false);
//                }
//            }
        });

        System.out.println("-> Binding on port: " + port + ", host: " + host + " ...");
        server.bind();
        System.out.println("-> Done!");
    }

}
