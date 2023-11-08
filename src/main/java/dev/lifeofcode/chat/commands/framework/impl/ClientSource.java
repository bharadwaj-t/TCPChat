package dev.lifeofcode.chat.commands.framework.impl;

import dev.lifeofcode.chat.commands.framework.Source;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientSource implements Source {
    private final NetSocket netSocket;

    public ClientSource(NetSocket netSocket) {
        this.netSocket = netSocket;
    }

    @Override
    public Future<Void> write(String str) {
        return netSocket.write(str);
    }

    @Override
    public Future<Void> write(byte[] data) {
        netSocket.write(Buffer.buffer(data));
        return null;
    }
}
