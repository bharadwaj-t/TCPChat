package dev.lifeofcode.chat.commands.framework;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
@Component
public class CommandRegistry {
    private final List<Command> commands;
    private final Map<String, Command> commandMap = new HashMap<>();

    public CommandRegistry(List<Command> commands) {
        this.commands = commands;
    }

    public void register(String name, Command cmd) {
        commandMap.put(name, cmd);
    }

    public Command query(String name) {
        return commandMap.get(name);
    }

    @PostConstruct
    public void regsiterCommands() {
        for(Command cmd: commands) {
            log.info("Registering command: {}", cmd.getName());
            register(cmd.getName(), cmd);
        }
    }
}
