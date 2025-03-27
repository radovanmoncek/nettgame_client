package cz.radovanmoncek.client.ship.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * An example client for the containerized nettgame server.
 * </p>
 * <p>
 * This class uses the <a href=https://refactoring.guru>Singleton Design Pattern</a>, since it is bound to a Socket.
 * There can exist at most one instance of this class per runtime.
 * </p>
 */
public final class NettgameClientBootstrap {
    private static final Logger logger = Logger.getLogger(NettgameClientBootstrap.class.getName());
    /**
     * Singleton instance
     */
    private static NettgameClientBootstrap instance;
    private final LinkedList<ChannelHandler> channelHandlers;
    private ChannelHandler loggingHandler;
    private final MultiThreadIoEventLoopGroup workerGroup;
    private int gameServerPort;
    private InetAddress gameServerAddress;
    private boolean shutdownOnDisconnect;
    private int shutdownTimeout;
    /**
     * The number of seconds between reconnect attempts.
     */
    private int reconnectDelay;
    /**
     * The number of reconnect retries.
     */
    private int reconnectAttempts;
    private int reconnectCounter;

    //https://stackoverflow.com/questions/6307648/change-global-setting-for-logger-instances
    static {

        Logger
                .getLogger("")
                .setLevel(Level.FINE);

        for(final var handler : Logger.getLogger("").getHandlers()) {

            handler.setLevel(Level.FINE);
        }
    }

    private NettgameClientBootstrap() {

        channelHandlers = new LinkedList<>();
        shutdownOnDisconnect = true;
        reconnectCounter = 0;
        workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
    }

    public void setShutdownOnDisconnect(boolean shutdownOnDisconnect) {

        this.shutdownOnDisconnect = shutdownOnDisconnect;
    }

    public void run() {

        try {

            final var serverChannel = new Bootstrap()
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(loggingHandler)
                    .connect(gameServerAddress, gameServerPort)
                    .sync()
                    //Thank you: https://kpavlov.me/blog/implementing-automatic-reconnection-for-netty-client/
                    .addListener((ChannelFutureListener) future -> {

                        if (!future.isSuccess()) {

                            logger.log(Level.SEVERE, "Reconnection failed");

                            return;
                        }

                        future
                                .channel()
                                .closeFuture()
                                .addListener(closeFuture -> {

                            if (!closeFuture.isSuccess()) {

                                logger.log(Level.SEVERE, closeFuture.cause().getMessage(), closeFuture.cause());

                                return;
                            }

                            logger.info("\nSuccess -> disconnected from the nettgame server");

                            if (shutdownOnDisconnect) {

                                logger.log(Level.INFO, "Shutting down gracefully in {0} seconds", shutdownTimeout);

                                TimeUnit.SECONDS.sleep(shutdownTimeout);

                                workerGroup.shutdownGracefully();
                            }
                        });
                    })
                    .sync()
                    .channel();

            while (!serverChannel.isActive()){

                TimeUnit.MILLISECONDS.sleep(reconnectDelay);
            }

            channelHandlers.forEach(serverChannel.pipeline()::addLast);
        } catch (final Exception exception) {

            try {

                logger.log(Level.SEVERE, exception.getMessage(), exception);

                if (reconnectCounter++ > reconnectAttempts) {

                    workerGroup
                            .shutdownGracefully()
                            .sync();

                    return;
                }

                TimeUnit.SECONDS.sleep(reconnectDelay);

                run();
            } catch (InterruptedException interruptedException) {

                logger.log(Level.SEVERE, interruptedException.getMessage(), interruptedException);
            }
        }
    }

    public static NettgameClientBootstrap returnNewInstance() {

        if (Objects.isNull(instance))
            instance = new NettgameClientBootstrap();

        instance.setGameServerPort(4321);
        instance.setGameServerAddress(InetAddress.getLoopbackAddress());

        return instance;
    }

    public void setGameServerPort(final int serverPort) {

        if (serverPort <= 1024 || serverPort > 65535)
            throw new IllegalArgumentException("Server port must be between 1024 and 65535");

        this.gameServerPort = serverPort;
    }

    public void setGameServerAddress(final InetAddress gameServerAddress) {

        this.gameServerAddress = gameServerAddress;
    }

    public void setLogLevel(LogLevel logLevel) {

        loggingHandler = new LoggingHandler(logLevel);
    }

    public void addChannelHandler(final ChannelHandler channelHandler) {

        channelHandlers.add(channelHandler);
    }

    public void setReconnectAttempts(int reconnectAttempts) {
        this.reconnectAttempts = reconnectAttempts;
    }

    public void setReconnectDelay(int reconnectDelay) {
        this.reconnectDelay = reconnectDelay;
    }

    public void setShutdownTimeout(int shutdownTimeout) {
        this.shutdownTimeout = shutdownTimeout;
    }
}
