package dev.lifeofcode.chat.commands;

import dev.lifeofcode.chat.commands.framework.Command;
import dev.lifeofcode.chat.commands.framework.Source;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@Getter
@Component
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
    public void execute(String[] args, Source src) {
        try {
            HelpFormatter formatter = new HelpFormatter();
            var helpMessage = new StringWriter();
            var pw = new PrintWriter(helpMessage);

            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(commandOptions, args);

            String username = line.hasOption("user") ? line.getOptionValue("user") : null;
            String password = line.hasOption("pass") ? line.getOptionValue("pass") : null;
            boolean help = line.hasOption("help");

            if (help) {
                formatter.printHelp(pw, 80, "login", "", commandOptions,
                        formatter.getLeftPadding(), formatter.getDescPadding(), "", true);
                pw.flush();
                src.write(helpMessage.toString());
                return;
            } else if (username == null || password == null) {
                formatter.printUsage(pw, 80, "login", commandOptions);
                pw.flush();
                src.write("not enough arguments\n");
                src.write(helpMessage.toString());
                return;
            }

            // arbitrary logic to authenticate with the parsed data.
            authenticator();

        } catch (ParseException exp) {
            log.error("Error parsing command: {}", exp.getMessage());
            src.write("Failed to parse the command\n");
        }
    }

    public void authenticator() {
        log.info("Authenticating....");
    }
}
