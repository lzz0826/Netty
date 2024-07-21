package com.example.nettypro.googleProto.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class NettyClientHandler extends ChannelInboundHandlerAdapter {


    /**
     * Creates a client-side handler.
     */
    public NettyClientHandler() {

    }

    //通道就緒觸發
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {


        //使用生成的StudentPOJO Builder
        StudentPOJO.Student builder = StudentPOJO.Student.newBuilder()
                .setId(1)
                .setName("tony")
                .build();
        //傳給服務端
        System.out.println("客戶端發消息.....");
        ctx.writeAndFlush(builder);

    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if(msg instanceof StudentPOJO.Student){

            StudentPOJO.Student st = (StudentPOJO.Student) msg;
            int id = st.getId();
            String name = st.getName();
            System.out.println("服務端返回 POJO :" + id + "\n" + name);

        }

    }



    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}