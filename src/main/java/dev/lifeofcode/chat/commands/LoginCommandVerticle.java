package dev.lifeofcode.chat.commands;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginCommandVerticle extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(LoginCommandVerticle.class);
    private final String NAME = "login";

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        var options = buildOptions();
        var bus = vertx.eventBus();

        bus.<JsonObject>consumer("login-command", msg -> {
            String[] args = msg.body().getJsonArray("args")
                    .stream()
                    .map(Object::toString)
                    .toList()
                    .toArray(new String[0]);

            try {
                CommandLineParser parser = new DefaultParser();
                CommandLine line = parser.parse(options, args);

                String username = line.hasOption("user") ? line.getOptionValue("user") : null;
                String password = line.hasOption("pass") ? line.getOptionValue("pass") : null;
                boolean help = line.hasOption("help");

                if (help) {
                    bus.send("command-response", CommandHelper.generateHelp("login", options));
                } else if (username == null || password == null) {
                    bus.send("command-response", CommandHelper.generateUsage("login", options));
                }
            } catch (ParseException exp) {
                log.error("Error parsing command: {}", exp.getMessage());
                bus.send("command-response","Failed to parse the command\n");
            }
        });
        startPromise.complete();
    }

    private static Options buildOptions() {
        var usernameOption = Option.builder("user")
                .argName("username")
                .hasArg()
                .desc("username of the client")
                .build();

        var passwordOption = Option.builder("pass")
                .argName("password")
                .hasArg()
                .desc("password of the client")
                .build();
        var helpOption = new Option("help", "prints help");

        var options = new Options();
        options.addOption(usernameOption);
        options.addOption(passwordOption);
        options.addOption(helpOption);
        return options;
    }
}
