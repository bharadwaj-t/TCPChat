package dev.lifeofcode.chat;

import dev.lifeofcode.chat.commands.CommandRouterVerticle;
import dev.lifeofcode.chat.server.ChatServerVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TcpChatApplication extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        vertx.deployVerticle(ChatServerVerticle.class, new DeploymentOptions().setInstances(10)).onFailure(failure -> {
            log.error("Failed to deploy ChatServerVerticle verticle.", failure);
        });
        vertx.deployVerticle(CommandRouterVerticle.class, new DeploymentOptions().setInstances(10)).onFailure(failure -> {
            log.error("Failed to deploy COmmandRouterVerticle verticle.", failure);
        });
    }
}
