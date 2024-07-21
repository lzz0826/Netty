package com.example.nettypro.dubborpc.netty;


import com.example.nettypro.dubborpc.customer.ClientBootstrap;
import com.example.nettypro.dubborpc.provider.HelloServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

//服務器這邊handler比較簡單
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //獲取客戶端發送的消息，並調用服務
        System.out.println("msg=" + msg);
        //客戶端在調用服務器的api 時，我們需要定義一個協議
        //比如我們要求 每次發消息是都必須以某個字符串開頭 "HelloService#hello#你好"
        if(msg.toString().startsWith(ClientBootstrap.providerName)) {

            String result = new HelloServiceImpl().hello(msg.toString().substring(msg.toString().lastIndexOf("#") + 1));
            ctx.writeAndFlush(result);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
