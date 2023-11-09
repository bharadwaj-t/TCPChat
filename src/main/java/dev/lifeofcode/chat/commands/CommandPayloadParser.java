package dev.lifeofcode.chat.commands;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

public interface CommandPayloadParser {
    default JsonObject marshalCommandPayload(List<String> args) {
        var commandPayload = new JsonObject();
        commandPayload.put("args", new JsonArray(args));
        return commandPayload;
    }

    default String[] unmarshalCommandPayload(JsonObject payload) {
        return payload.getJsonArray("args").stream()
                .map(Object::toString)
                .toList()
                .toArray(new String[0]);
    }
}
