package dev.lifeofcode.chat.commands.framework;

import dev.lifeofcode.chat.exceptions.CommandNotFoundException;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CommandRouter {
    private final CommandRegistry commandRegistry;
    private static final String COMMAND_DELIMITER = " ";

    public CommandRouter(List<Command> commands,CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    public void route(Buffer buffer, WriteStream<Buffer> src) {
        // parse the command.
        var commandWords = new java.util.ArrayList<>(Arrays.stream(buffer.toString(StandardCharsets.UTF_8)
                .split(COMMAND_DELIMITER)).map(String::trim).toList());
        var command = commandWords.get(0);
        commandWords.remove(command);

        log.info("buffer: {}", commandWords);

        try {
            var resolvedCommand = commandRegistry.query(command);
            if (resolvedCommand == null) {
                var error = String.format("Command %s not found\n", command);
                throw new CommandNotFoundException(error);
            }

            log.info("Command found: {}", resolvedCommand.getName());
            var args = commandWords.toArray(new String[0]);
            resolvedCommand.execute(args, src);

        } catch (CommandNotFoundException e) {
            src.write(Buffer.buffer(e.getMessage()));
        }
    }
}
