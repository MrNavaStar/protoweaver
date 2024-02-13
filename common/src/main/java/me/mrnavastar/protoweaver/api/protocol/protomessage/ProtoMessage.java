package me.mrnavastar.protoweaver.api.protocol.protomessage;

import me.mrnavastar.protoweaver.api.protocol.Protocol;
import lombok.Getter;
import me.mrnavastar.protoweaver.api.protocol.ProtoBuilder;
import me.mrnavastar.protoweaver.api.netty.ProtoConnection;
import me.mrnavastar.protoweaver.api.ProtoPacket;
import me.mrnavastar.protoweaver.api.ProtoConnectionHandler;
import me.mrnavastar.protoweaver.api.protocol.CompressionType;
import me.mrnavastar.protoweaver.api.util.Event;

/**
 * Serves mostly as an example protocol, however it can be used in your mod if your so desire.
 */
public class ProtoMessage implements ProtoConnectionHandler {

    @Getter
    private static final Protocol protocol = ProtoBuilder.protocol("protoweaver", "proto-message")
            .enableCompression(CompressionType.SNAPPY)
            .setServerHandler(ProtoMessage.class)
            .setClientHandler(ProtoMessage.class)
            .addPacket(Message.class)
            .build();

    @Override
    public void handlePacket(ProtoConnection connection, ProtoPacket packet) {
        if (packet instanceof Message message) MESSAGE_RECEIVED.getInvoker().trigger(connection, message.getChannel(), message.getMessage());
    }

    /**
     * This event is triggered when a message is received and can be used both on the server and the client.
     * Be sure to load this protocol.
     */
    public static final Event<MessageReceived> MESSAGE_RECEIVED = new Event<>(callbacks -> (connection, channel, message) -> {
        callbacks.forEach(callback -> callback.trigger(connection, channel, message));
    });

    @FunctionalInterface
    public interface MessageReceived {
        void trigger(ProtoConnection connection, String channel, String message);
    }
}