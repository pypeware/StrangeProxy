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

package io.d2a.strangeproxy.stats;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StatsManager {

    public StatsManager() {
        new Thread(() -> {
            AtomicInteger step = new AtomicInteger(0);
            AtomicLong in = new AtomicLong(0L);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {

                    long total = Stats.connections;
                    long per10Sec = Stats.connectionsPer10Seconds;
                    long per60Sec = Stats.connectionsPer60Seconds;

                    if(step.incrementAndGet() >= 10) {
                        step.set(0);

                        Stats.connectionsPer60Seconds = 0L;
                    }

                    Stats.connectionsPer10Seconds = 0L;
                }
            }, 10000, 10000);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {

                    System.out.print("\r[STATUS] Pings: " + Stats.pings +
                            " | Total: " + Stats.connections +
                            " | 60s: " + Stats.connectionsPer60Seconds +
                            " | 10s: " + Stats.connectionsPer10Seconds);

                }
            }, 100, 100);

        }).start();


        //
    }

}
