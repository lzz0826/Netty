package com.example.nettypro.googleProto.mydatainfo;

import com.example.nettypro.googleProto.codec.StudentPOJO;
import com.example.nettypro.utils.ServerUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.ssl.SslContext;

public final class NettyClient {

    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "6668"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    public static void main(String[] args) throws Exception {
        // Configure SSL.git
        final SslContext sslCtx = ServerUtil.buildSslContext();

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //Bootstrap 創建(客戶端)的啟動對象 配置參數
            Bootstrap b = new Bootstrap();
            b.group(group) //設置線程組
                    .channel(NioSocketChannel.class) //設置客戶端通道的實現類(反射)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
                            }

                            //**添加順序很重要 會影響剛進來的解碼是用哪個解碼器 每個解碼器會嘗試處理消息，如果解碼成功，則不會傳遞給後續的解碼器。

                            //加入ProtobufEncoder解碼器(需要放入自定生成的物件)
                            p.addLast(new ProtobufDecoder(MyDataInfo.MyMessage.getDefaultInstance()));
                            //加入ProtobufEncoder編碼器
                            p.addLast(new ProtobufEncoder());



                            //自定業務
                            p.addLast(new NettyClientHandler());
                        }
                    });

            System.out.println("Client Start");


            // 啟動客戶端去連接服務端
            // ChannelFuture Netty異步模型
            ChannelFuture f = b.connect(HOST, PORT).sync();


            //對關閉通道進行監聽
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
}