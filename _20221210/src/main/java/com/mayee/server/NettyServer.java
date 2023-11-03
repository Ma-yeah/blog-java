package com.mayee.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer {
    public void bind(int port) {

        /**
         * 配置服务端的NIO线程组
         * NioEventLoopGroup 是用来处理I/O操作的Reactor线程组
         * bossGroup：用来接收进来的连接，workerGroup：用来处理已经被接收的连接,进行socketChannel的网络读写，
         * bossGroup接收到连接后就会把连接信息注册到workerGroup
         * workerGroup的EventLoopGroup默认的线程数是CPU核数的二倍
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            /**
             * ServerBootstrap 是一个启动NIO服务的辅助启动类
             */
            ServerBootstrap bootstrap = new ServerBootstrap()
                    /**
                     * 设置group，将bossGroup， workerGroup线程组传递到ServerBootstrap
                     */
                    .group(bossGroup, workerGroup)
                    /**
                     * option是设置 bossGroup，childOption是设置workerGroup
                     * netty 默认数据包传输大小为1024字节, 设置它可以自动调整下一次缓冲区建立时分配的空间大小，避免内存的浪费    最小  初始化  最大 (根据生产环境实际情况来定)
                     * 使用对象池，重用缓冲区
                     */
//                    .option(ChannelOption.SO_BACKLOG, 1024) // 设置TCP缓冲区
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 10496, 1048576))
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 10496, 1048576))
//                    .childOption(ChannelOption.TCP_NODELAY,true)
//                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 两小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
                    /**
                     * ServerSocketChannel是以NIO的selector为基础进行实现的，用来接收新的连接，这里告诉Channel通过NioServerSocketChannel获取新的连接
                     */
                    .channel(NioServerSocketChannel.class) //设置NIO的模式
                    .localAddress(port)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    /**
                     * 设置 I/O处理类,主要用于网络I/O事件，记录日志，编码、解码消息
                     */
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) {
                            channel.pipeline().addLast(new ProtocolSelectorDecoder());
                        }
                    });
            /**
             * 绑定端口，同步等待成功
             */
            ChannelFuture future = bootstrap.bind().sync();
            log.info("服务已启动，正在监听: {}", future.channel().localAddress());
            /**
             * 等待服务器监听端口关闭
             */
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            /**
             * 退出，释放线程池资源
             */
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
