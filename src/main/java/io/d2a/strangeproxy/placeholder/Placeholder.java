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

package io.d2a.strangeproxy.placeholder;

import com.github.steveice10.packetlib.Session;

public interface Placeholder {

    String placeholder();

    String apply(Session session, String input);

    default String fullPlaceholder() {
        return String.format("{%s}", placeholder());
    }

}
