package com.example.nettypro.googleProto.codec;

import com.example.nettypro.utils.ServerUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

public final class NettyServer {

    static final int PORT = Integer.parseInt(System.getProperty("port", "6668"));


    public static void main(String[] args) throws InterruptedException, CertificateException, SSLException {

        /**
         * 創建 bossGroup workerGroup 兩個都是無限循環
         * bossGroup 只處理連接請求 實際業務處理交給workerGroup
         */

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        // 創建SSL上下文，用於SSL/TLS加密通信
        final SslContext sslCtx = ServerUtil.buildSslContext();




        final NettyServerHandler serverHandler = new NettyServerHandler();

        try {
            //ServerBootstrap 創建(服務器端)的啟動對象 配置參數
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup,workerGroup) // 設置兩個現成組
                    .channel(NioServerSocketChannel.class)//使用NioSocketChannel 作為服務器的通道實現
                    .option(ChannelOption.SO_BACKLOG,128)//設置線程隊列得到連接數
                    .childOption(ChannelOption.SO_KEEPALIVE,true)//設置保持活動連接狀態
                    .childHandler(new ChannelInitializer<SocketChannel>() { //創建一個通道測試對象 workerGroup
                        //給pipeline 設置處理器
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();

                            // 如果SSL上下文不為空，則添加SSL處理器
                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(socketChannel.alloc()));
                            }

                            //**添加順序很重要 會影響剛進來的解碼是用哪個解碼器 每個解碼器會嘗試處理消息，如果解碼成功，則不會傳遞給後續的解碼器。

                            //加入ProtobufEncoder解碼器(需要放入自定生成的物件)
                            p.addLast(new ProtobufDecoder(StudentPOJO.Student.getDefaultInstance()));
                            //加入ProtobufEncoder編碼器
                            p.addLast(new ProtobufEncoder());


                            //自定業務
                            p.addLast(serverHandler);
                        }
                    });
            System.out.println("服務器 啟動...");


            //綁定一個端口並同步 生成一個ChannelFutuer 對象
            //起動服務器(並綁定端口)
            ChannelFuture cf = bootstrap.bind(PORT).sync();

            listenerPort(cf);

            //對關閉通道進行監聽
            cf.channel().closeFuture().sync();

        }finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    /**
     * ChannelFuture : 監聽異步 I/O 操作的完成狀態並執行相應的操作
     *
     * addListener(ChannelFutureListener listener): 添加一個監聽器，在操作完成時會被通知。
     * sync(): 阻塞當前線程，直到 I/O 操作完成。
     * await(): 使當前線程等待，直到 I/O 操作完成，但不會拋出異常。
     * isSuccess(): 判斷操作是否成功。
     * cause(): 如果操作失敗，返回導致失敗的異常。
     * isDone(): 判斷操作是否已經完成。
     */
    public static void listenerPort(ChannelFuture cf){
        cf.addListener(new GenericFutureListener(){
            @Override
            public void operationComplete(Future future) throws Exception {
                if (future.isSuccess()){
                    System.out.println("監聽端口"+ PORT +"成功");
                }else {
                    System.out.println("監聽端口"+ PORT +"失敗");

                }
            }
        });

    }

}
