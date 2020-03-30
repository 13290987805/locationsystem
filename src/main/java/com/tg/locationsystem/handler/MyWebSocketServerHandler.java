package com.tg.locationsystem.handler;

import com.tg.locationsystem.utils.SystemMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * ClassName:MyWebSocketServerHandler Function:  TODO ADD FUNCTION.
 * @author hxy
 */
public class MyWebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = Logger.getLogger(WebSocketServerHandshaker.class.getName());
    private WebSocketServerHandshaker handshaker;
    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 添加
        Global.group.clear();
        Global.group.add(ctx.channel());
        System.out.println("客户端与服务端连接开启");
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 移除
        Global.group.remove(ctx.channel());
        System.out.println("客户端与服务端连接关闭");
       // SystemMap.getChannelmap().remove(ctx.channel().id()+","+userid);
        for (String key : SystemMap.getChannelmap().keySet()) {
            if(SystemMap.getChannelmap().get(key)==ctx.channel()){
                SystemMap.getChannelmap().remove(key);
            }
        }

    }

    protected void messageReceived(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, ((FullHttpRequest) msg));
        } else if (msg instanceof WebSocketFrame) {
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    private void handlerWebSocketFrame(ChannelHandlerContext ctx,
                                       WebSocketFrame frame) {
        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame
                    .retain());
        }
        // 判断是否ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(
                    new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 本例程仅支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            return;
        }
        // 返回应答消息
        String userid = ((TextWebSocketFrame) frame).text();
        //getStations
        System.out.println("服务端收到：" + userid);
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(String.format("%s received %s", ctx.channel(),
                    userid));
        }
        TextWebSocketFrame tws = new TextWebSocketFrame("ok");

        // 群发
        //  Globle.group.writeAndFlush(tws);
        // 返回【谁发的发给谁】
        ctx.writeAndFlush(tws);
        SystemMap.getChannelmap().put(ctx.channel().id()+","+userid,ctx.channel());
    }
    private void handleHttpRequest(ChannelHandlerContext ctx,
                                   FullHttpRequest req) {
        if (!req.getDecoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:9999/websocket", null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory
                    .sendUnsupportedWebSocketVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }
    private static void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
    private static boolean isKeepAlive(FullHttpRequest req) {
        return false;
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}