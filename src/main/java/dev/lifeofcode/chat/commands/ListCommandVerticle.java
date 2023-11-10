package dev.lifeofcode.chat.commands;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListCommandVerticle extends AbstractVerticle implements CommandPayloadParser, CommandHelpGenerator {
    private static final Logger log = LoggerFactory.getLogger(ListCommandVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        var options = buildOptions();
        var bus = vertx.eventBus();

        bus.<JsonObject>consumer("list-command", msg -> {
            log.info("LIST: {}", msg.body());
            String[] args = unmarshalCommandPayload(msg.body());

            try {
                CommandLineParser parser = new DefaultParser();
                CommandLine line = parser.parse(options, args);

                boolean channel = line.hasOption("chan");
                boolean help = line.hasOption("help");

                if (help) {
                    bus.send("command-response", generateHelp("list", options));
                } else if (!channel) {
                    bus.send("command-response", generateUsage("list", options));
                }
            } catch (ParseException exp) {
                log.error("Error parsing command: {}", exp.getMessage());
                bus.send("command-response", "Failed to parse the command\n");
            }
        });
        startPromise.complete();
    }

    private static Options buildOptions() {
        var helpOption = new Option("help", "prints help");
        var chanOption = new Option("chan", "lists public channels");

        var options = new Options();
        options.addOption(helpOption);
        options.addOption(chanOption);
        return options;
    }
}
