package com.example.nettypro.heartbeat;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class Server {


    private int port;

    Server(int port){
        this.port = port;
    }

    public void run() throws Exception{

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)//使用NioSocketChannel 作為服務器的通道實現
                    .option(ChannelOption.SO_BACKLOG,128)//設置線程隊列得到連接數
                    .childOption(ChannelOption.SO_KEEPALIVE,true)//設置保持活動連接狀態
                    .handler(new LoggingHandler(LogLevel.INFO)) // bossGroup 設置日誌
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            /*
                            * IdleStateHandler: netty 提供的處理空間狀態的處理器
                            * readerIdleTime 多久時間沒有讀 會發送心跳檢測包 檢測是否連接
                            * long writerIdleTime 多久時間沒有寫 會發送心跳檢測包 檢測是否連接
                            * long allIdleTime 多久時間沒有 讀 寫 會發送心跳檢測包 檢測是否連接
                            *
                            * 當 IdleStateHandler 觸發後 就會傳遞給管道的下一個Handler去處理
                            * 通過調用(觸發)下一個handler的userEventTriggered 在該方法中去處理
                            * IdleStateEvent(讀空閑 寫空閑 讀寫空閑)
                            * */
                            pipeline.addLast(new IdleStateHandler(3,5,7, TimeUnit.SECONDS));
                            //處理空閑檢測 進一步處理的Handler(自定義)
                            pipeline.addLast(new ServerHandler());
                        }
                    });

            System.out.println("服務端 啟動...");
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();

        }finally {

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();


        }




    }

    public static void main(String[] args) throws Exception {
        new Server(8088).run();

    }
}
