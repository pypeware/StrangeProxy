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

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.AsnResponse;
import io.d2a.strangeproxy.StrangeProxy;

import java.io.IOException;
import java.net.InetAddress;

public class MaxMindDatabaseReader {

    private MaxMindDatabase db;

    public MaxMindDatabaseReader(MaxMindDatabase database) {
        this.db = database;
    }

    public int getAsn(InetAddress address) throws IOException, GeoIp2Exception {
        StrangeProxy.getLogger().debug("  -> Get asn for " + address);
        final AsnResponse asn = this.db.getDatabaseReader().asn(address);
        return asn.getAutonomousSystemNumber();
    }

    public Integer getAsnUnsafe(InetAddress address) {
        try {
            return getAsn(address);
        } catch (IOException | GeoIp2Exception e) {
            StrangeProxy.getLogger().error("Error reading asn for " + address, e);
            return null;
        }
    }

}
