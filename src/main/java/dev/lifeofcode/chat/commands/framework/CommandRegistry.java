package dev.lifeofcode.chat.commands.framework;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
public class CommandRegistry {
    private final Map<String, Command> commandMap = new HashMap<>();

    public void register(String name, Command cmd) {
        commandMap.put(name, cmd);
    }

    public Command query(String name) {
        return commandMap.get(name);
    }
}
