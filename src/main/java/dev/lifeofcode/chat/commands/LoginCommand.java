package dev.lifeofcode.chat.commands;

import dev.lifeofcode.chat.commands.framework.Command;
import dev.lifeofcode.chat.commands.framework.Source;
import dev.lifeofcode.chat.exceptions.AuthenticationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Getter
@Component
public class LoginCommand implements Command {
    private final String name = "login";
    @Override
    public void execute(List<String> args, Source src) {
        try {
            if (args.size() != 2) {
                var error = "Too little or too many args, Usage: login;username;password\n";
                throw new AuthenticationException(error);
            }

            var username = args.get(0);
            var password = args.get(1);

            // login work
            // UserService.login(username, password);

            src.write("Logged in, welcome back " + username + "\n");
        } catch (AuthenticationException e) {
            src.write(e.getMessage()).onFailure(err -> log.error("Failed", err));
        }
    }
}
