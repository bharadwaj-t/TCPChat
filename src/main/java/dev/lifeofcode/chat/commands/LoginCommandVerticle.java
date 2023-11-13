package dev.lifeofcode.chat.commands;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginCommandVerticle extends AbstractVerticle implements CommandPayloadParser, CommandHelpGenerator {
    private static final Logger log = LoggerFactory.getLogger(LoginCommandVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        var options = buildOptions();
        var bus = vertx.eventBus();

        bus.<JsonObject>consumer("login-command", msg -> {
            log.info("LOGIN: {}", msg.body());
            String[] args = unmarshalCommandPayload(msg.body());

            try {
                CommandLineParser parser = new DefaultParser();
                CommandLine line = parser.parse(options, args);

                String username = line.hasOption("user") ? line.getOptionValue("user") : null;
                String password = line.hasOption("pass") ? line.getOptionValue("pass") : null;
                boolean help = line.hasOption("help");
                boolean register = line.hasOption("register");

                if (help) {
                    bus.send("command-response", generateHelp("login", options));
                    return;
                } else if (register && username != null && password != null) {
                    log.info("Registering user....");
                    bus.<String>request("user-signup", new JsonObject().put("username", username).put("password", password))
                            .onSuccess(signupMsg -> {
                                bus.send("command-response", signupMsg.body());
                            })
                            .onFailure(err -> log.error("Error in signup", err));
                } else if (username == null || password == null) {
                    bus.send("command-response", generateUsage("login", options));
                }

                log.info("logging in...");
                bus.<String>request("user-authentication", new JsonObject().put("username", username).put("password", password))
                        .onSuccess(authMsg -> {
                            bus.send("command-response", authMsg.body());
                        })
                        .onFailure(err -> log.error("Error in auth", err));

            } catch (ParseException exp) {
                log.error("Error parsing command: {}", exp.getMessage());
                bus.send("command-response", "Failed to parse the command\n");
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
        var registerOption = new Option("register", "register account");

        var options = new Options();
        options.addOption(usernameOption);
        options.addOption(passwordOption);
        options.addOption(helpOption);
        options.addOption(registerOption);
        return options;
    }
}
