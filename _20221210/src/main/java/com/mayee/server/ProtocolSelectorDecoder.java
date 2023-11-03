package com.mayee.server;

import com.mayee.server.tcp.TcpChannelInboundHandlerAdapter;
import com.mayee.server.ws.WsChannelInboundHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 消息解码，用来判断协议类型，动态选择协议处理器
 * &#064;link:  <a href="https://www.freesion.com/article/3651404721/#ProtocolSelectorHandler_46">多协议并存</a>
 */
public class ProtocolSelectorDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        ChannelPipeline pipeline = channelHandlerContext.pipeline();

        // 目前仅用 netty 实现了 tcp 和 ws 协议
        if (isWebSocketUrl(byteBuf)) {
            addWebSocketHandlers(pipeline);
        } else {
            addTCPProtocolHandlers(pipeline);
        }

        pipeline.remove(this);
    }

    private static final String WEBSOCKET_LINE_PREFIX = "GET /ws";
    private static final String WEBSOCKET_PREFIX = "/ws";

    private boolean isWebSocketUrl(ByteBuf byteBuf) {
        if (byteBuf.readableBytes() < WEBSOCKET_LINE_PREFIX.length()) {
            return false;
        }
        byteBuf.markReaderIndex();
        byte[] content = new byte[WEBSOCKET_LINE_PREFIX.length()];
        byteBuf.readBytes(content);
        byteBuf.resetReaderIndex();
        String s = new String(content, CharsetUtil.UTF_8);
        return s.equals(WEBSOCKET_LINE_PREFIX);
    }

    private void addWebSocketHandlers(ChannelPipeline pipeline) {
        //websocket协议本身是基于http协议的，所以这边也要使用http解编码器
        pipeline.addLast(new HttpServerCodec());
        //以块的方式来写的处理器
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(8192));
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws", "WebSocket", true, 65536 * 10));
        pipeline.addLast(new WsChannelInboundHandler());//自定义消息处理类
    }

    private void addTCPProtocolHandlers(ChannelPipeline pipeline) {
        // ChannelOutboundHandler，依照逆序执行
        pipeline.addLast("encoder", new StringEncoder(StandardCharsets.UTF_8));

        // 属于ChannelInboundHandler，依照顺序执行
        pipeline.addLast("decoder", new StringDecoder(StandardCharsets.UTF_8));

        // 自定义ChannelInboundHandlerAdapter
        pipeline.addLast(new TcpChannelInboundHandlerAdapter());
    }
}
