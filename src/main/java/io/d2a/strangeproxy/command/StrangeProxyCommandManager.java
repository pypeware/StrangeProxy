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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.*;

public class StrangeProxyCommandManager {

    private final Map<String, Command> commands = new HashMap<>();

    public void register(final Command command, final String... aliases) {
        Preconditions.checkNotNull(aliases, "aliases");
        Preconditions.checkNotNull(command, "executor");
        for (int i = 0, length = aliases.length; i < length; i++) {
            final String alias = aliases[i];
            Preconditions.checkNotNull(alias, "alias at index %s", i);
            this.commands.put(alias.toLowerCase(Locale.ENGLISH), command);
        }
    }

    public void unregister(final String alias) {
        Preconditions.checkNotNull(alias, "name");
        this.commands.remove(alias.toLowerCase(Locale.ENGLISH));
    }

    public boolean execute(String cmdLine) {
        Preconditions.checkNotNull(cmdLine, "cmdLine");

        String[] split = cmdLine.split(" ", -1);
        if (split.length == 0) {
            return false;
        }

        String alias = split[0];
        Command command = commands.get(alias.toLowerCase(Locale.ENGLISH));
        if (command == null) {
            return false;
        }

        @SuppressWarnings("nullness")
        String[] actualArgs = Arrays.copyOfRange(split, 1, split.length);
        try {
            command.execute(actualArgs);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Unable to invoke command " + cmdLine);
        }
    }

    public boolean hasCommand(String command) {
        return commands.containsKey(command);
    }

    public Set<String> getAllRegisteredCommands() {
        return ImmutableSet.copyOf(commands.keySet());
    }

    /**
     * Offer suggestions to fill in the command.
     *
     * @param cmdLine the partially completed command
     * @return a {@link List}, possibly empty
     */
    public List<String> offerSuggestions(String cmdLine) {
        Preconditions.checkNotNull(cmdLine, "cmdLine");

        String[] split = cmdLine.split(" ", -1);
        if (split.length == 0) {
            // No command available.
            return ImmutableList.of();
        }

        String alias = split[0];
        if (split.length == 1) {
            // Offer to fill in commands.
            ImmutableList.Builder<String> availableCommands = ImmutableList.builder();
            for (Map.Entry<String, Command> entry : commands.entrySet()) {
                if (entry.getKey().regionMatches(true, 0, alias, 0, alias.length())) {
                    availableCommands.add("/" + entry.getKey());
                }
            }
            return availableCommands.build();
        }

        Command command = commands.get(alias.toLowerCase(Locale.ENGLISH));
        if (command == null) {
            // No such command, so we can't offer any tab complete suggestions.
            return ImmutableList.of();
        }

        @SuppressWarnings("nullness")
        String[] actualArgs = Arrays.copyOfRange(split, 1, split.length);
        try {
            return ImmutableList.copyOf(command.suggest(actualArgs));
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to invoke suggestions for command " + alias);
        }
    }
}