package com.example.nettypro.dubborpcmy.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.Callable;



@Log4j2
public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter implements Callable {
    //上下文 在連接建立后存 讓其他方法調用
    private ChannelHandlerContext context;//上下文


    //發送給服務端的消息
    private String para;

    //服務端返回的消息
    private String rep;

    /**
     * (4) 當接收到數據時觸發該方法
     *  需要跟  call() 同步 方法鎖
     */
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("channelRead....");

        //接收服務端返回消息
        if(msg instanceof String){
            this.rep = (String)msg;
        }

        //喚醒之前等待的線程
        notify();
    }

    /**
     * (1) 當通道變為活動狀態時觸發該方法（連接建立后調用）
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive....");
        this.context = ctx;
        System.out.println(this.context.channel().id());
    }



    /**
     * (3) -> (5) 被代理對象調用, 發送數據給服務器，-> wait -> 等待被喚醒(channelRead) -> 返回結果
     * 需要跟  channelRead 同步 方法鎖
     */
    @Override
    public synchronized Object call() throws Exception {
        log.info("para call....");

        //發送消息後等待服務端回覆
        this.context.writeAndFlush(para);

        wait(); // 等待channelRead喚醒
        log.info("rep call....");

        //收到後端消息 返回
        return this.rep;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("異常:" + cause.getMessage());
        log.error(cause.getMessage());
        ctx.close();
    }

    //(2) 存發送用的消息
    public void setPara(String para){
        System.out.println("para...");
        this.para = para;
    }

    public boolean checkChannelHandlerContextIsNull(){
        return this.context == null;

    }





}