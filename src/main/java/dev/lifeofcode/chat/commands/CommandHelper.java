package dev.lifeofcode.chat.commands;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CommandHelper {
    public static String generateHelp(String cmdName, Options opts) {
        HelpFormatter formatter = new HelpFormatter();
        var helpMessage = new StringWriter();
        var pw = new PrintWriter(helpMessage);

        try {
            formatter.printHelp(pw, 80, cmdName, "", opts,
                    formatter.getLeftPadding(), formatter.getDescPadding(), "", true);
        } finally {
            pw.close();
        }

        return helpMessage.toString();
    }

    public static String generateUsage(String cmdName, Options opts) {
        HelpFormatter formatter = new HelpFormatter();
        var usageMessage = new StringWriter();
        var pw = new PrintWriter(usageMessage);

        try {
            formatter.printUsage(pw, 80, cmdName, opts);
        } finally {
            pw.close();
        }

        return usageMessage.toString();
    }
}
