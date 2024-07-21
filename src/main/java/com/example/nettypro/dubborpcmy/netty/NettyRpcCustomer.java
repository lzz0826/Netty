package com.example.nettypro.dubborpcmy.netty;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


import java.lang.reflect.Proxy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * 客戶端Netty
 *
 */
public class NettyRpcCustomer {


    //創建線程池
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


    //靜態 共用NettyRpcClientHandler
    private static NettyRpcClientHandler nettyRpcClientHandler;

    private String host;

    private int port;

    public NettyRpcCustomer(String host , int port){
     this.host = host;
     this.port = port;
    }

    private int count = 0;



    //編寫方法使用代理模式，獲取一個代理對象
    public Object getBen(final Class<?> serverClass , final String rpcRule) throws Exception {

        //初始化NettyRpcClientHandler 確保Handler有加近 p.addLast(new NettyRpcClientHandler());
        if(nettyRpcClientHandler == null ){
            initClient(host,port);
        }

        return  Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{serverClass},

                //    使用ava 的動態代理機制 可以在方前後添加新的業務邏輯
                //    proxy: 代理實例，通過這個實例調用目標方法。
                //    method: 被調用的方法的 Method 對象。
                //    args: 調用方法時傳遞的參數數組。
                (proxy, method, args) ->{

                    System.out.println("(proxy, method, args) 進入...." + (++count) + " 次");

//                    //**放在這裡 call()會早於 channelActive()
//                    //初始化NettyRpcClientHandler 確保Handler有加近 p.addLast(new NettyRpcClientHandler());
//                    if(nettyRpcClientHandler == null ){
//                        initClient(host,port);
//                    }

                    //設置要發給服務器端的信息
                    //rpcRule 協議頭 args[0] 就是客戶端調用api hello(???), 參數
                    nettyRpcClientHandler.setPara(rpcRule+args[0]);

                    //使用線程池等待 ettyRpcClientHandler 裡的Callable 返回
                    //這裡會返回nettyRpcClientHandler裡裡的Callable的東西 也就是服務端返回的字串

                    return executor.submit(nettyRpcClientHandler).get();
                });
    }

    private static void initClient(String host , int port) throws Exception {

        nettyRpcClientHandler = new NettyRpcClientHandler();
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
//                            p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast(new StringDecoder());
                            p.addLast(new StringEncoder());
                            p.addLast(nettyRpcClientHandler);

                        }
                    });

            b.connect(host, port).sync();
            //釋放所
            System.out.println("Client Start");

        } catch (Exception e) {
            e.printStackTrace();
        }

        // ** 這裡不能 關閉 不然會導致 服務端回發時 channelRead 無法接收
//        finally {
//            // Shut down the event loop to terminate all threads.
//            group.shutdownGracefully();
//        }
    }


}
