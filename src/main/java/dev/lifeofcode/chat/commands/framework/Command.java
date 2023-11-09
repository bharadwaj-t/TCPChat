package dev.lifeofcode.chat.commands.framework;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.WriteStream;

import java.util.List;

public interface Command {
    String getName();
    void buildCommand();
    void execute(String[] args, WriteStream<Buffer> src);
}
