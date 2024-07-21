package com.example.nettypro.dubborpcmy.netty;

import com.example.nettypro.dubborpcmy.provider.HelloServerImp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j2;

import java.net.SocketAddress;

import static com.example.nettypro.dubborpcmy.common.RpcRule.HELLOSERVERIMP_SAYHELLO_RULE;

/**
 * 服務端事件管理
 */

@Log4j2
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {


    //確保變量在多個線程之間的可見性
    private volatile HelloServerImp helloServerImp;


    /**
     * 當接收到數據時觸發該方法
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        System.out.println("服務端收到消息....");

        if(msg instanceof String){
            String m = msg.toString();
            if(m.contains(HELLOSERVERIMP_SAYHELLO_RULE)){
                log.info("服務端器收到消息 : " + msg);
                //這裡需要自己定義某種規則
                //匹如使用 物件 +方法名 當前綴 #號後才是真正的訊息 : HelloServerImp_sayHello#
                String rep = getHelloServerImp().sayHello(m.substring(m.lastIndexOf("#")+1));

                System.out.println("rep:" + rep);

                ctx.writeAndFlush(rep);
            }

        }



    }


    /**
     * 當發生異常時觸發該方法
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        log.error("服務端異常:"+socketAddress.hashCode() + "\t" + cause.getMessage());
        System.out.println(cause.getMessage());
        ctx.close();
    }

    /**
     * 多線程 只創建一次HelloServerImp
     */
    private HelloServerImp getHelloServerImp(){
        if(helloServerImp == null){
            synchronized (this){
                if(helloServerImp == null){
                    helloServerImp = new HelloServerImp();
                }

            }
        }
        return helloServerImp;
    }


}
