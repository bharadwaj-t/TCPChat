package dev.lifeofcode.chat;

import dev.lifeofcode.chat.exceptions.AuthenticationException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.DecodeException;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ChatServerVerticle extends AbstractVerticle {
    private final int port;
    private final Map<SocketAddress, Client> chatClients = new HashMap<>();

    public ChatServerVerticle(@Value("${chat.server.port}") int port) {
        this.port = port;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        var serverOpts = new NetServerOptions();
        serverOpts.setRegisterWriteHandler(true);
        var bus = vertx.eventBus();

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

        netSocket.handler(buffer -> {
            var client = chatClients.get(netSocket.remoteAddress());
            if(!client.isAuthenticated()) {
                authenticate(netSocket, buffer, client);
            }
            command(netSocket, buffer, client);
        });

        netSocket.exceptionHandler(err -> {
            log.error("Exception occurred", err);
        });

        netSocket.closeHandler(closed -> {
            log.info("Client disconnected: {}", netSocket.remoteAddress());
        });
    }

    private static void authenticate(NetSocket netSocket, Buffer buffer, Client client) {
        try {
            var authenticationJson = buffer.toJsonObject();
            var name = authenticationJson.getString("client");
            if (name == null) {
                throw new AuthenticationException("\"client\" key is not present");
            } else {
                client.setAuthenticated(true);
                client.setName(name);
            }
            netSocket.write(String.format("Welcome back, %s\n", client.getName()));
        } catch (DecodeException e) {
            netSocket.write("Authentication phase incomplete, identification payload must be Json.\n");
        } catch (AuthenticationException e) {
            log.error("Caught authentication exception: {}", e.getMessage());
            netSocket.write(String.format("Authentication unsuccessful, %s\n", e.getMessage()));
        }
    }

    public static void command(NetSocket netSocket, Buffer buffer, Client client) {
        try {
            var query = buffer.toJsonObject();
        } catch (DecodeException e) {
            netSocket.write("Commands to the server have to be in json format\n");
        }
    }
}
