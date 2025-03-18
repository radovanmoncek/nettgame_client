package cz.radovanmoncek.client.modules.chats.handlers;

import cz.radovanmoncek.client.ship.parents.handlers.ServerChannelHandler;
import cz.radovanmoncek.client.ship.tables.ChatMessage;
import io.netty.channel.ChannelHandlerContext;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Example handler for a chat system.
 */
public class ChatServerChannelHandler extends ServerChannelHandler<ChatMessage> {
    private static final Logger logger = Logger.getLogger(ChatServerChannelHandler.class.getName());

    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final ChatMessage chatMessage) {

        logger.log(Level.SEVERE, "Message from player received {0}", chatMessage);
    }
}
