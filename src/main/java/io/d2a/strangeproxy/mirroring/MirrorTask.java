/*
 * Copyright (c) 2019.
 * Created at 30.12.2019
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
import io.d2a.strangeproxy.StrangeProxy;
import io.d2a.strangeproxy.config.Config;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.net.Proxy;
import java.util.Timer;
import java.util.TimerTask;

public class MirrorTask extends TimerTask implements Runnable  {

    private Config config;

    public MirrorTask(Config config) {
        this.config = config;
        StrangeProxy.getLogger().info("[Mirroring] Started mirroring with an interval of " + (config.mirroring.updateInterval / 1000) + " seconds");
    }

    @Override
    public void run() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                StrangeProxy.getLogger().debug("[Mirroring] Checking mirror ...");
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

                        StrangeProxy.getConfig().serverStatusInfo = info;

                        // Disconnect client
                        session.disconnect("");

                        StrangeProxy.getLogger().debug("[Mirroring] Done");
                    }
                });
                client.getSession().connect();
            }
        }, 0, config.mirroring.updateInterval);
    }

}
