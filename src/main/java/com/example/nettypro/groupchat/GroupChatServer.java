package com.example.nettypro.groupchat;

import com.example.nettypro.utils.ServerUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import lombok.extern.log4j.Log4j2;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;


@Log4j2
public class GroupChatServer {

    private int port;


    GroupChatServer(int port){
        this.port = port;
    }

    public void run() throws CertificateException, SSLException, InterruptedException {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();

        // 創建SSL上下文，用於SSL/TLS加密通信
        final SslContext sslCtx = ServerUtil.buildSslContext();


        try {

            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)//使用NioSocketChannel 作為服務器的通道實現
                    .option(ChannelOption.SO_BACKLOG,128)//設置線程隊列得到連接數
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();

                            // 如果SSL上下文不為空，則添加SSL處理器
                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc()));
                            }

                            //向 pipeline 添加解碼器
                            p.addLast("decoder",new StringDecoder());
                            //向 pipeline 添加編碼器
                            p.addLast("encoder",new StringEncoder());

                            //加入業務處理
                            p.addLast(new CroupChatServerHandler());
                        }
                    });
            ChannelFuture c = bootstrap.bind(port).sync();
            System.out.println("Server 啟動....");

            //對關閉通道進行監聽
            c.channel().closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws CertificateException, SSLException, InterruptedException {


        new GroupChatServer(8088).run();
    }

}
