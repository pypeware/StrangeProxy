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

package io.d2a.strangeproxy.placeholder.placeholders;

import com.github.steveice10.packetlib.Session;
import io.d2a.strangeproxy.StrangeProxy;
import io.d2a.strangeproxy.placeholder.Placeholder;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AsnPlaceholder implements Placeholder {

    @Override
    public String placeholder() {
        return "asn";
    }

    @Override
    public String apply(Session session, String input) {

        try {
            InetAddress address = InetAddress.getByName(session.getHost());
            final Integer asnUnsafe = StrangeProxy.getInstance().getDatabase().getReader().getAsnUnsafe(address);
            return asnUnsafe != null ? String.valueOf(asnUnsafe) : null;
        } catch (UnknownHostException ignored) {
        }

        return null;
    }

}
