package dev.lifeofcode.chat.commands;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CommandRouterVerticle extends AbstractVerticle implements CommandPayloadParser {
    private static final Map<String, String> commands = new HashMap<>();
    private static final String COMMAND_DELIMITER = " ";

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        deployCommands(vertx);

        var bus = vertx.eventBus();
        bus.<Buffer>consumer("clientbuffer", payload -> {
            Buffer buffer = payload.body();
            var commandWords = new ArrayList<>(Arrays.stream(buffer.toString(StandardCharsets.UTF_8)
                    .split(COMMAND_DELIMITER)).map(String::trim).toList());
            var command = commandWords.get(0);
            commandWords.remove(command);

            var commandPayload = marshalCommandPayload(commandWords);

            var address = commands.get(command);
            if (address != null) {
                log.info("ROUTING: {}", command);
                bus.send(address, commandPayload);
            } else {
                log.error("COULD NOT FIND COMMAND");
                bus.send("command-error-response", "command not found\n");
            }
        });
    }

    private static void deployCommands(Vertx vertx) {
        vertx.deployVerticle(LoginCommandVerticle.class, new DeploymentOptions().setInstances(1)).onFailure(failure -> {
            log.error("Failed to deploy LoginCOmmandVerticle verticle.", failure);
        }).onSuccess(done -> {
            commands.put("login", "login-command");
            log.info("Launched LoginCommandVerticle.");
        });
        vertx.deployVerticle(ListCommandVerticle.class, new DeploymentOptions().setInstances(1)).onFailure(failure -> {
            log.error("Failed to deploy ListCommandVerticle verticle.", failure);
        }).onSuccess(done -> {
            commands.put("list", "list-command");
            log.info("Launched ListCommandVerticle.");
        });
    }
}
