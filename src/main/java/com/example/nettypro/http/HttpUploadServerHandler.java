package com.example.nettypro.http;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;


/**
 * HttpObject 客戶端和服務器端相互通訊的數據被封裝成 HttpObject
 *
 * channelRegistered: 當 Channel 已經註冊到它的 EventLoop 並且能夠處理 I/O 時被調用。
 * channelUnregistered: 當 Channel 從它的 EventLoop 註銷並且無法處理任何 I/O 時被調用。
 * channelActive: 當 Channel 處於活動狀態（已連接）時被調用。
 * channelInactive: 當 Channel 處於非活動狀態（已斷開連接）時被調用。
 * channelRead0: 當從 Channel 讀取到消息時被調用。注意，這個方法會自動釋放接收的消息。
 * channelReadComplete: 當 Channel 上的一批數據被讀取完成並且準備就緒時被調用。
 * exceptionCaught: 當處理過程中發生異常時被調用。
 * userEventTriggered: 當 ChannelInboundHandler.fireUserEventTriggered() 方法被調用時，且有事件觸發時被調用。
 * channelWritabilityChanged: 當 Channel 的可寫狀態發生改變時被調用（例如：可寫狀態從可寫變為不可寫時）。
 *
 */
public class HttpUploadServerHandler extends SimpleChannelInboundHandler<HttpObject> {


    //channelRead0 讀取客戶端數據
    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

//        System.out.println("對應的channel=" + ctx.channel() + " pipeline=" + ctx.pipeline() + " 通過pipeline獲取channel" + ctx.pipeline().channel());
//        System.out.println("當前ctx的handler=" + ctx.handler());

        if (msg instanceof HttpRequest){

//            System.out.println("ctx 類型="+ctx.getClass());
//            System.out.println("pipeline hashcode" + ctx.pipeline().hashCode() + " TestHttpServerHandler hash=" + this.hashCode());
//            System.out.println("msg 類型=" + msg.getClass());
//            System.out.println("客戶端地址" + ctx.channel().remoteAddress());

            HttpRequest request = (HttpRequest) msg;

            //過濾URI /favicon.ico
            URI uri = new URI(request.uri());
            if (uri.getPath().equals("/favicon.ico")) {
                System.out.println("請求了 favicon.ico, 不做響應");
                return;
            }
            System.out.println(ctx.channel().localAddress());

            Channel channel = ctx.channel();


            //回複信息給瀏覽器 [http協議]
            ByteBuf content = Unpooled.copiedBuffer("Hi I my Http Sever", CharsetUtil.UTF_8);


            //構造一個http的相應，即 httpresponse
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            channel.writeAndFlush(response);

        }


    }

}