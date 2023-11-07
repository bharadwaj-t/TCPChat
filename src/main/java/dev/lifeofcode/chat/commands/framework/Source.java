package dev.lifeofcode.chat.commands.framework;

import io.vertx.core.Future;

public interface Source {
    Future<Void> write(String str);
}
