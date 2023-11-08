package dev.lifeofcode.chat.commands;

import dev.lifeofcode.chat.commands.framework.Command;
import dev.lifeofcode.chat.commands.framework.CommandHelper;
import dev.lifeofcode.chat.commands.framework.Source;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Getter
@Configuration
public class ListCommand implements Command {
    private final String name = "list";
    private Options commandOptions;

    @Override
    @PostConstruct
    public void buildCommand() {
        var helpOption = new Option("help", "prints help");
        var chanOption = new Option("chan", "lists public channels");

        var options = new Options();
        options.addOption(helpOption);
        options.addOption(chanOption);
        this.commandOptions = options;
    }

    @Override
    public void execute(String[] args, Source src) {
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(commandOptions, args);

            boolean channel = line.hasOption("chan");
            boolean help = line.hasOption("help");

            if (help) {
                src.write(CommandHelper.generateHelp("list", commandOptions));
                return;
            } else if (!channel) {
                src.write(CommandHelper.generateUsage("list", commandOptions));
                return;
            }

            src.write("{\"data\": \"value\"}\n");

        } catch (ParseException exp) {
            log.error("Error parsing command: {}", exp.getMessage());
            src.write("Failed to parse the command\n");
        }
    }
}
