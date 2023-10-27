package me.mrnavastar.protoweaver.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import me.mrnavastar.protoweaver.api.ProtoPacket;
import me.mrnavastar.protoweaver.api.ProtoPacketHandler;
import me.mrnavastar.protoweaver.protocol.Protocol;
import me.mrnavastar.protoweaver.protocol.Side;

import java.util.List;

public class ProtoPacketDecoder extends ByteToMessageDecoder {

    private final ProtoConnection connection;
    private ProtoPacketHandler handler;

    public ProtoPacketDecoder(ProtoConnection connection, Side side) {
        this.connection = connection;
        this.handler = connection.getProtocol().newHandler(side);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handler.ready(connection);
    }

    public void setHandler(ProtoPacketHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) {
        if (byteBuf.readableBytes() == 0) return;

        int packetLen = byteBuf.readInt();
        int packetID = byteBuf.readInt();
        ProtoPacket packet = connection.getProtocol().getPacket(packetID);
        if (packet == null) {
            unknownPacket(connection.getProtocol(), packetID);
            return;
        }

        try {
            packet.decode(byteBuf.readBytes(packetLen));
        } catch (IndexOutOfBoundsException e) {
            failedToDecodePacket(connection.getProtocol(), packetID, packet.getClass());
        }

        handler.handlePacket(connection, packet);
    }

    private void unknownPacket(Protocol protocol, int packetID) {

    }

    private void failedToDecodePacket(Protocol protocol, int packetID, Class<? extends ProtoPacket> packetClass) {

    }
}