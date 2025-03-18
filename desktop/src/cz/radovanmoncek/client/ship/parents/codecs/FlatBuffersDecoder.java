package cz.radovanmoncek.client.ship.parents.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Logger;

public abstract class FlatBuffersDecoder<FlatBuffersSchema> extends ByteToMessageDecoder {
    private static final Logger logger = Logger.getLogger(FlatBuffersDecoder.class.getName());
    private static final int HEADER_SIZE = Long.BYTES;

    @Override
    protected void decode(final ChannelHandlerContext channelHandlerContext, final ByteBuf in, final List<Object> out) {

        if (in.readableBytes() < HEADER_SIZE) {

            return;
        }

        in.markReaderIndex();

        if (in.readableBytes() < in.readLong()) {

            in.resetReaderIndex();

            return;
        }

        in.markReaderIndex();

        if (in.readableBytes() == 0) {

            logger.warning("Received an empty flat buffer");

            return;
        }

        final var headerNIOBuffer = in.nioBuffer();

        if (!decodeHeader(headerNIOBuffer)) {

            channelHandlerContext.fireChannelRead(in.resetReaderIndex().retain());

            return;
        }

        in.readerIndex(in.readerIndex() + headerNIOBuffer.position());

        out.add(decodeBodyAfterHeader(in.nioBuffer()));

        in.readerIndex(in.writerIndex());
    }

    protected abstract boolean decodeHeader(final ByteBuffer buffer);

    protected abstract FlatBuffersSchema decodeBodyAfterHeader(final ByteBuffer buffer);
}
