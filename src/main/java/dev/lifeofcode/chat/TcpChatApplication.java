package dev.lifeofcode.chat;

import dev.lifeofcode.chat.commands.ListCommandVerticle;
import dev.lifeofcode.chat.server.ChatServerVerticle;
import dev.lifeofcode.chat.commands.CommandRouterVerticle;
import dev.lifeofcode.chat.commands.LoginCommandVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class TcpChatApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(TcpChatApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        var vertx = Vertx.vertx();

        vertx.deployVerticle(ChatServerVerticle.class, new DeploymentOptions().setInstances(10)).onFailure(failure -> {
            log.error("Failed to deploy ChatServerVerticle verticle.", failure);
        });
        vertx.deployVerticle(CommandRouterVerticle.class, new DeploymentOptions().setInstances(10)).onFailure(failure -> {
            log.error("Failed to deploy COmmandRouterVerticle verticle.", failure);
        });
    }
}
