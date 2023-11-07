package dev.lifeofcode.chat.commands.framework;

import io.vertx.core.buffer.Buffer;

import java.util.List;

public interface Command {
    String getName();
    void execute(List<String> args, Source src);
}
