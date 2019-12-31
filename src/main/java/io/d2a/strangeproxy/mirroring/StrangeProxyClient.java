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

import io.d2a.strangeproxy.StrangeProxy;
import io.d2a.strangeproxy.config.Config;

public class StrangeProxyClient {

    private Config config;
    private Thread updateThread;

    public StrangeProxyClient(Config config) {
        this.config = config;
    }

    public void startUpdateTask(Config config) {
        if (config.mirroring.enabled) {
            startUpdateTask();
        }
    }

    public void startUpdateTask() {
        if (this.updateThread != null
                && this.updateThread.isAlive()) {
            this.updateThread.interrupt();
        }

        StrangeProxy.getLogger().info("[Mirroring] Checking: " + config.mirroring.host + ":" + config.mirroring.port);
        this.updateThread = new Thread(new MirrorTask(config), "Mirroring Task");
        this.updateThread.start();
    }

}
