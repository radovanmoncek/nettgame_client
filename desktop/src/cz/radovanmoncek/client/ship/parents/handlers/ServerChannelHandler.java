package cz.radovanmoncek.client.ship.parents.handlers;

import com.google.flatbuffers.Table;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ServerChannelHandler<FlatBuffersSchema extends Table> extends SimpleChannelInboundHandler<FlatBuffersSchema> {
    private static final Logger logger = Logger.getLogger(ServerChannelHandler.class.getName());
    private Channel serverChannel;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {

        serverChannel = ctx.channel();

        logger.log(Level.INFO, "Server channel added");
    }

    protected void unicast(final Object message) {

        if(serverChannel == null){

            logger.severe("serverChannel is null");

            return;
        }

        serverChannel.writeAndFlush(message);
    }

    protected void disconnect() {

        if(Objects.isNull(serverChannel)){

            logger.warning("serverChannel is null, this should not happen");

            return;
        }

        serverChannel
                .close()
                .addListener(future -> logger.log(Level.SEVERE, "Disconnected with success status {0}", future.isSuccess()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        logger.throwing(this.getClass().getName(), "exceptionCaught", cause);
    }
}
