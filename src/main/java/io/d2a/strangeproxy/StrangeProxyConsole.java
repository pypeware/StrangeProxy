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

package io.d2a.strangeproxy;

import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.IoBuilder;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import java.util.List;

public class StrangeProxyConsole extends SimpleTerminalConsole {

    private static final Logger logger = LogManager.getLogger(StrangeProxyConsole.class);

    private StrangeProxy proxy;

    public StrangeProxyConsole(StrangeProxy strangeProxy) {
        this.proxy = strangeProxy;
    }

    public void setupStreams() {
        System.setOut(IoBuilder.forLogger(logger).setLevel(Level.INFO).buildPrintStream());
        System.setErr(IoBuilder.forLogger(logger).setLevel(Level.ERROR).buildPrintStream());
    }

    public void sendMessage(Component component) {
        logger.info(LegacyComponentSerializer.INSTANCE.serialize(component));
    }

    @Override
    protected LineReader buildReader(LineReaderBuilder builder) {
        return super.buildReader(builder
                .appName("StrangeProxy")
                .completer((reader, parsedLine, list) -> {
                    try {
                        boolean isCommand = parsedLine.line().indexOf(' ') == -1;
                        List<String> offers = this.proxy.getCommandManager()
                                .offerSuggestions(parsedLine.line());
                        for (String offer : offers) {
                            if (isCommand) {
                                list.add(new Candidate(offer.substring(1)));
                            } else {
                                list.add(new Candidate(offer));
                            }
                        }
                    } catch (Exception e) {
                        logger.error("An error occurred while trying to perform tab completion.", e);
                    }
                })
        );
    }

    @Override
    protected boolean isRunning() {
        return false;
    }

    @Override
    protected void runCommand(String command) {
        logger.info("Running '" + command + "'");
        try {
            if (!this.proxy.getCommandManager().execute(command)) {
                sendMessage(TextComponent.of("Command not found.", TextColor.RED));
            }
        } catch (Exception e) {
            logger.error("An error occurred while running this command.", e);
        }
    }

    @Override
    protected void shutdown() {

    }

}
