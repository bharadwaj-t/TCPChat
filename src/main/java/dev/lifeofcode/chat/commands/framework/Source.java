package dev.lifeofcode.chat.commands.framework;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;


public interface Source {
     Future<Void> write(String str);
     Future<Void> write(byte[] data);
}
