package dev.lifeofcode.chat.commands;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class CommandRouterVerticle extends AbstractVerticle {
    private static final Map<String, String> commands = new HashMap<>();
    private static final String COMMAND_DELIMITER = " ";

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        deployComamnds(vertx);

        var bus = vertx.eventBus();
        bus.<Buffer>consumer("clientbuffer", payload -> {
            Buffer buffer = payload.body();
            var commandWords = new ArrayList<>(Arrays.stream(buffer.toString(StandardCharsets.UTF_8)
                    .split(COMMAND_DELIMITER)).map(String::trim).toList());
            var command = commandWords.get(0);
            commandWords.remove(command);

            var commandPayload = new JsonObject();
            commandPayload.put("args", new JsonArray(commandWords));

            var address = commands.get(command);
            if (address != null) {
                bus.send(address, commandPayload);
            } else {
                bus.send("command-response", "command not found");
            }
        });
    }

    private static void deployComamnds(Vertx vertx) {
        vertx.deployVerticle(LoginCommandVerticle.class, new DeploymentOptions().setInstances(10)).onFailure(failure -> {
            log.error("Failed to deploy LoginCOmmandVerticle verticle.", failure);
        }).onSuccess(done -> {
            commands.put("login", "login-command");
            log.info("Launched LoginCommandVerticle.");
        });
        vertx.deployVerticle(ListCommandVerticle.class, new DeploymentOptions().setInstances(10)).onFailure(failure -> {
            log.error("Failed to deploy ListCommandVerticle verticle.", failure);
        }).onSuccess(done -> {
            commands.put("list", "list-command");
            log.info("Launched ListCommandVerticle.");
        });
    }
}
