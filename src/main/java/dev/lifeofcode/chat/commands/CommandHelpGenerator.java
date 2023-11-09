package dev.lifeofcode.chat.commands;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.io.PrintWriter;
import java.io.StringWriter;

public interface CommandHelpGenerator {
    default String generateHelp(String cmdName, Options opts) {
        HelpFormatter formatter = new HelpFormatter();
        var helpMessage = new StringWriter();

        try (var pw = new PrintWriter(helpMessage)) {
            formatter.printHelp(pw, 80, cmdName, "", opts,
                    formatter.getLeftPadding(), formatter.getDescPadding(), "", true);
        }
        return helpMessage.toString();
    }

    default String generateUsage(String cmdName, Options opts) {
        HelpFormatter formatter = new HelpFormatter();
        var usageMessage = new StringWriter();

        try (var pw = new PrintWriter(usageMessage)) {
            formatter.printUsage(pw, 80, cmdName, opts);
        }
        return usageMessage.toString();
    }
}
