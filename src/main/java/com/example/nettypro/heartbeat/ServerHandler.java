package com.example.nettypro.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

public class ServerHandler extends ChannelInboundHandlerAdapter {


    /**
     * ctx 上下文
     * evt 事件
     */

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
       if(evt instanceof IdleStateEvent){

           IdleStateEvent event = (IdleStateEvent) evt;

           String eventStatus = null;


           switch (event.state()){

               //讀空閒
               case READER_IDLE:
                   eventStatus = "讀空閒";
                   break;

               //寫空閒
               case WRITER_IDLE:
                   eventStatus = "寫空閒";

                   break;

               //讀寫空閒
               case ALL_IDLE:
                   eventStatus = "讀寫空閒";
                   break;
           }

           System.out.println(ctx.channel().remoteAddress()+"超時事件 : "+eventStatus);
           System.out.println("服務器做相應處理...");

//           ctx.channel().close();

       }
    }
}
