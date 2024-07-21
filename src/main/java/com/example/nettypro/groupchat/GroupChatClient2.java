package com.example.nettypro.groupchat;

import com.example.nettypro.utils.ServerUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.Scanner;

public class GroupChatClient2 {


    private String host;
    private int port;

    GroupChatClient2(String host , int port){
        this.port = port;
        this.host = host;
    }

    public void run() throws CertificateException, SSLException {

        final SslContext sslCtx = ServerUtil.buildSslContext();

        NioEventLoopGroup group = new NioEventLoopGroup();

        try {

            Bootstrap b = new Bootstrap();
            b.group(group) //設置線程組
                    .channel(NioSocketChannel.class) //設置客戶端通道的實現類(反射)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                            }
                            p.addLast(new StringEncoder()); //添加 String編碼
                            p.addLast(new StringDecoder()); //添加 String解碼
                            //添加業務
                            p.addLast(new CroupChatClientHandler());

                        }
                    });

            System.out.println("Client Start");



            // 啟動客戶端去連接服務端
            // ChannelFuture Netty異步模型
            ChannelFuture future = b.connect(host, port).sync();

            if (future.isSuccess()) {
                //取得連接到的 channel
                Channel channel = future.channel();

                System.out.println("========" + channel.localAddress() + "========");
                //客戶端需要輸入信息， 創建一個掃描器
                Scanner scanner = new Scanner(System.in);
                while (scanner.hasNextLine()) {
                    String msg = scanner.nextLine();
                    //通過 channel 發送到服務器端
                    channel.writeAndFlush(msg);
                }

            }

            //對關閉通道進行監聽
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {


            System.out.println("客戶端關閉...");
            group.shutdownGracefully();
        }



    }


    public static void main(String[] args) throws CertificateException, SSLException {

        new GroupChatClient2("127.0.0.1",8088).run();



    }


}
