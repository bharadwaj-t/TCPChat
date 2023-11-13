package dev.lifeofcode.chat;

import dev.lifeofcode.chat.commands.CommandRouterVerticle;
import dev.lifeofcode.chat.server.ChatServerVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TcpChatApplication {

    public static void main(String[] args) {
        ClusterManager mgr = new HazelcastClusterManager();
        var opts = new VertxOptions().setClusterManager(mgr);

        Vertx
                .clusteredVertx(opts)
                .onSuccess(vertx -> {
                    vertx.deployVerticle(ChatServerVerticle.class, new DeploymentOptions().setInstances(1)).onFailure(failure -> {
                        log.error("Failed to deploy ChatServerVerticle verticle.", failure);
                    });
                    vertx.deployVerticle(CommandRouterVerticle.class, new DeploymentOptions().setInstances(1)).onFailure(failure -> {
                        log.error("Failed to deploy COmmandRouterVerticle verticle.", failure);
                    });
                })
                .onFailure(err -> {
                    log.error("Couldn't start vertx.", err);
                });
    }
}
