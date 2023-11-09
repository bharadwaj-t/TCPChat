package dev.lifeofcode.chat.commands;

import dev.lifeofcode.chat.commands.framework.Command;
import dev.lifeofcode.chat.commands.framework.CommandHelper;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Getter
@Configuration
public class LoginCommand implements Command {
    private final String name = "login";
    private Options commandOptions;

    @Override
    @PostConstruct
    public void buildCommand() {
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
        this.commandOptions = options;
    }

    @Override
    public void execute(String[] args, WriteStream<Buffer> src) {
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(commandOptions, args);

            String username = line.hasOption("user") ? line.getOptionValue("user") : null;
            String password = line.hasOption("pass") ? line.getOptionValue("pass") : null;
            boolean help = line.hasOption("help");

            if (help) {
                src.write(Buffer.buffer(CommandHelper.generateHelp("login", commandOptions)));
                return;
            } else if (username == null || password == null) {
                src.write(Buffer.buffer(CommandHelper.generateUsage("login", commandOptions)));
                return;
            }
        } catch (ParseException exp) {
            log.error("Error parsing command: {}", exp.getMessage());
            src.write(Buffer.buffer("Failed to parse the command\n"));
        }
    }
}
