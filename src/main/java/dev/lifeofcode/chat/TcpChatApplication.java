package dev.lifeofcode.chat;

import dev.lifeofcode.chat.commands.CommandRouterVerticle;
import dev.lifeofcode.chat.server.ChatServerVerticle;
import dev.lifeofcode.chat.services.SqlVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TcpChatApplication {
    public static void main(String[] args) {
        var mgr = new HazelcastClusterManager();
        var opts = new VertxOptions().setClusterManager(mgr);


        Vertx
                .clusteredVertx(opts)
                .onSuccess(vertx -> {
                    var file = new ConfigStoreOptions()
                            .setType("file")
                            .setFormat("properties")
                            .setConfig(new JsonObject().put("path", "application.properties"));
                    var retrieverOpts = new ConfigRetrieverOptions().addStore(file);
                    var retriever = ConfigRetriever.create(vertx, retrieverOpts);

                    retriever
                            .getConfig()
                            .onFailure(err -> {
                                log.error("Error reading config", err);
                            })
                            .onSuccess(cfg -> {
                                vertx.deployVerticle(ChatServerVerticle.class, new DeploymentOptions().setInstances(1)).onFailure(failure -> {
                                    log.error("Failed to deploy ChatServerVerticle verticle.", failure);
                                });
                                vertx.deployVerticle(CommandRouterVerticle.class, new DeploymentOptions().setInstances(1)).onFailure(failure -> {
                                    log.error("Failed to deploy CommandRouterVerticle verticle.", failure);
                                });

                                var sqlConfig = new JsonObject().put("user", cfg.getString("sql.user"))
                                        .put("password", cfg.getString("sql.password"))
                                        .put("host", cfg.getString("sql.host"))
                                        .put("database", cfg.getString("sql.database"));
                                vertx.deployVerticle(SqlVerticle.class, new DeploymentOptions().setConfig(sqlConfig).setInstances(1)).onFailure(failure -> {
                                    log.error("Failed to deploy SqlVerticle verticle.", failure);
                                });
                            });
                })
                .onFailure(err -> {
                    log.error("Couldn't start vertx.", err);
                });
    }
}
