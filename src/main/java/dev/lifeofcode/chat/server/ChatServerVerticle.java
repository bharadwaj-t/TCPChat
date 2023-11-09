package dev.lifeofcode.chat.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.net.NetServerOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServerVerticle extends AbstractVerticle {
    private final static int PORT = 4322;

    @Override
    public void start(Promise<Void> startPromise) {
        var serverOpts = new NetServerOptions();
        serverOpts.setRegisterWriteHandler(true);
        var bus = vertx.eventBus();

        vertx.createNetServer(serverOpts)
                .connectHandler(netSocket -> {
                    netSocket.write("Welcome to chat server\n");
                    log.info("Client connected: {}", netSocket.remoteAddress());

                    netSocket.handler(buffer -> {

                        // send all the incoming buffer.
                        bus.send("clientbuffer", buffer);

                        // write command responses to client.
                        bus.<String>consumer("command-response", response -> {
                            netSocket.write(response.body());
                        });
                    });

                    netSocket.exceptionHandler(err -> {
                        log.error("Exception occurred", err);
                    });

                    netSocket.endHandler(closed -> {
                        log.info("Client disconnected: {}", netSocket.remoteAddress());
                    });
                })
                .listen(PORT)
                .onSuccess(done -> {
                    log.info("Chat Server listening on {}", PORT);
                    startPromise.complete();
                })
                .onFailure(startPromise::fail);
    }
}
