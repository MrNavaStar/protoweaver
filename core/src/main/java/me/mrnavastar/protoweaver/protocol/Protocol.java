package me.mrnavastar.protoweaver.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.mrnavastar.protoweaver.api.ClientAuthHandler;
import me.mrnavastar.protoweaver.api.ServerAuthHandler;
import me.mrnavastar.protoweaver.api.ProtoPacket;
import me.mrnavastar.protoweaver.api.ProtoPacketHandler;

import java.util.List;

@Getter
@AllArgsConstructor
public class Protocol {
    
    private final String name;
    private final List<Class<? extends ProtoPacket>> packets;
    private final Class<? extends ProtoPacketHandler> serverHandler;
    private final Class<? extends ProtoPacketHandler> clientHandler;
    private final Class<? extends ServerAuthHandler> serverAuthHandler;
    private final Class<? extends ClientAuthHandler> clientAuthHandler;
    private final CompressionType compression;
    private final int compressionLevel;

    @SneakyThrows
    public ProtoPacketHandler newHandler(@NonNull Side side) {
        return switch (side) {
            case CLIENT -> clientHandler.getDeclaredConstructor().newInstance();
            case SERVER -> serverHandler.getDeclaredConstructor().newInstance();
        };
    }

    @SneakyThrows
    public ServerAuthHandler newServerAuthHandler() {
        return serverAuthHandler.getDeclaredConstructor().newInstance();
    }

    @SneakyThrows
    public ClientAuthHandler newClientAuthHandler() {
        return clientAuthHandler.getDeclaredConstructor().newInstance();
    }

    @SneakyThrows
    public ProtoPacket getPacket(int packetID) {
        if (packetID < 0 || packetID >= packets.size()) return null;
        return packets.get(packetID).getDeclaredConstructor().newInstance();
    }

    public int getPacketId(@NonNull ProtoPacket packet) {
        return packets.indexOf(packet.getClass());
    }
}