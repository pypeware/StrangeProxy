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

package io.d2a.strangeproxy.mirroring;

import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoHandler;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.d2a.strangeproxy.StrangeProxy;
import io.d2a.strangeproxy.config.Config;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.chat.TextComponentSerializer;

import java.net.Proxy;
import java.util.Arrays;

public class StrangeProxyClient {

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(TextComponent.class, new TextComponentSerializer())
            .create();

    private Config config;

    public StrangeProxyClient(Config config) {
        this.config = config;

        System.out.println("[Mirroring] Checking: " + config.mirroring.host + ":" + config.mirroring.port);
    }

    public void update() {
        MinecraftProtocol protocol = new MinecraftProtocol(SubProtocol.STATUS);
        Client client = new Client(config.mirroring.host,
                config.mirroring.port,
                protocol,
                new TcpSessionFactory(Proxy.NO_PROXY));

        client.getSession().setFlag(MinecraftConstants.AUTH_PROXY_KEY, Proxy.NO_PROXY);
        client.getSession().setFlag(MinecraftConstants.SERVER_INFO_HANDLER_KEY, new ServerInfoHandler() {
            @Override
            public void handle(Session session, ServerStatusInfo info) {
                final BaseComponent[] parse = ComponentSerializer.parse(info.getDescription().toJsonString());

                StrangeProxy.getConfig().status.motd = new TextComponent(parse).toLegacyText();
                StrangeProxy.getConfig().status.maxPlayers = info.getPlayerInfo().getMaxPlayers();
                StrangeProxy.getConfig().status.currentPlayers = info.getPlayerInfo().getOnlinePlayers();

                StrangeProxy.getConfig().versionInfo = info.getVersionInfo();

                // Disconnect client
                session.disconnect("");
            }
        });
        client.getSession().connect();
    }

}
