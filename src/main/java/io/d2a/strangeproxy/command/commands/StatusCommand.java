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

package io.d2a.strangeproxy.command.commands;

import io.d2a.strangeproxy.command.Command;

public class StatusCommand implements Command {

    @Override
    public void execute(String[] args) {
        System.out.println("Ok!");
    }

}
