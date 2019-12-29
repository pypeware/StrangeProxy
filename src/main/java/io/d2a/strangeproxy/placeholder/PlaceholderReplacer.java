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
import io.d2a.strangeproxy.placeholder.placeholders.AsnPlaceholder;

import java.util.Arrays;
import java.util.LinkedList;

public class PlaceholderReplacer {

    private static final LinkedList<Placeholder> placeholders = new LinkedList<>(Arrays.asList(
            new AsnPlaceholder()
    ));

    public static String apply(Session session, String string) {
        String result = string;

        for (Placeholder placeholder : placeholders) {
            if (string.contains(placeholder.fullPlaceholder())) {
                final String apply = placeholder.apply(session, string);
                if (apply != null) {
                    result = result.replace(placeholder.fullPlaceholder(), apply);
                }
            }
        }

        return result;
    }

    public static String coloredApply(Session session, String string) {
        return apply(session, string)
                .replaceAll("&[0-9a-fk-or]", "\u00A7");
    }

}
