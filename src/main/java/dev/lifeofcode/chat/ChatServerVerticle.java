package dev.lifeofcode.chat;

import dev.lifeofcode.chat.commands.framework.CommandRouter;
import dev.lifeofcode.chat.commands.framework.impl.ClientSource;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ChatServerVerticle extends AbstractVerticle {
    private final int port;
    private final Map<SocketAddress, Client> chatClients = new HashMap<>();
    private final List<Client> clientList = new ArrayList<>();

    @Autowired
    CommandRouter router;

    public ChatServerVerticle(@Value("${chat.server.port}") int port) {
        this.port = port;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        var serverOpts = new NetServerOptions();
        serverOpts.setRegisterWriteHandler(true);

        vertx.createNetServer(serverOpts)
                .connectHandler(this::connectionHandler)
                .listen(port)
                .onSuccess(done -> {
                    log.info("Chat Server listening on {}", port);
                    startPromise.complete();
                })
                .onFailure(startPromise::fail);
    }

    private void connectionHandler(NetSocket netSocket) {
        netSocket.write("Welcome to chat server\n");
        log.info("Client connected: {}", netSocket.remoteAddress());

        chatClients.put(netSocket.remoteAddress(), new Client());

        var clientSource = new ClientSource(netSocket);
        netSocket.handler(buffer -> {
            router.route(buffer, clientSource);
        });

        netSocket.exceptionHandler(err -> {
            log.error("Exception occurred", err);
        });

        netSocket.closeHandler(closed -> {
            log.info("Client disconnected: {}", netSocket.remoteAddress());
        });
    }
}
