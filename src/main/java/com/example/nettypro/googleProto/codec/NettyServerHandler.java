package com.example.nettypro.googleProto.codec;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


/**
 *
 * 繼承Netty 規定的某個HandlerAdapter(需要遵守netty規範)
 */
@Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {


    /**
     * 讀取數據實際(讀客戶端的訊息)
     * ChannelHandlerContext(上下文對象):
     * pipeline管道 裡面有很多Handler 業務邏輯處理管道
     * channel 通道 數據的讀和寫 數據通道 可以控制傳給哪個客戶端
     * ...
     *
     * Object msg : 客戶段發送的數據默認 Object
     */

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("收到客戶端消息");
        System.out.println(msg.getClass().getTypeName());

        if(msg instanceof StudentPOJO.Student){
            StudentPOJO.Student st = (StudentPOJO.Student) msg;
            int id = st.getId();
            String name = st.getName();

            System.out.println("客戶端 POJO :" + id + "\n" + name);

        }
    }

    /**
     * 數據讀取完畢
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {

        //發送StudentPOJO
        StudentPOJO.Student build = StudentPOJO.Student.newBuilder()
                .setId(33)
                .setName("Tom")
                .build();
        ctx.writeAndFlush(build);

        System.out.println("服務端發消息.....");


    }


    /**
     * 處理異常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }



}
