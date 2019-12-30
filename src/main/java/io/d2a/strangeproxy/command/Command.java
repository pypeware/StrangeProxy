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

package io.d2a.strangeproxy.command;

import com.google.common.collect.ImmutableList;

import java.util.List;

public interface Command {

    void execute(String[] args);

    default List<String> suggest(String[] currentArgs) {
        return ImmutableList.of();
    }

}