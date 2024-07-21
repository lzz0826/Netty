package com.example.nettypro.dubborpcmy.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 *
 * 服務端Netty
 *
 */
public class NettyRpcServer {

    public static void startNettyRpcServer(String ip , int port) throws InterruptedException {
        startNettyRpcServer01(ip ,port);
    }

    private static void startNettyRpcServer01(String ip , int port) throws InterruptedException {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //ServerBootstrap 創建(服務器端)的啟動對象 配置參數
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup,workerGroup) // 設置兩個現成組
                    .channel(NioServerSocketChannel.class)//使用NioSocketChannel 作為服務器的通道實現
                    .childHandler(new ChannelInitializer<SocketChannel>() { //創建一個通道測試對象 workerGroup
                        //給pipeline 設置處理器
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();

//                            p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast(new StringDecoder());
                            p.addLast(new StringEncoder());

                            p.addLast(new NettyRpcServerHandler());
                        }
                    });

            System.out.println("服務器 啟動...");


            //綁定一個端口並同步 生成一個ChannelFutuer 對象
            //起動服務器(並綁定端口)
            ChannelFuture cf = bootstrap.bind(ip,port).sync();

            listenerPort(cf,port);

            //對關閉通道進行監聽
            cf.channel().closeFuture().sync();

        }finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static void listenerPort(ChannelFuture cf , int port){
        cf.addListener(new GenericFutureListener(){
            @Override
            public void operationComplete(Future future) throws Exception {
                if (future.isSuccess()){
                    System.out.println("監聽端口"+ port +"成功");
                }else {
                    System.out.println("監聽端口"+ port +"失敗");

                }
            }
        });

    }


}
