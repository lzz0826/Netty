package com.example.nettypro.dubborpcmy.customer;


import com.example.nettypro.dubborpcmy.netty.NettyRpcCustomer;
import com.example.nettypro.dubborpcmy.publicinterface.HelloServer;

import static com.example.nettypro.dubborpcmy.common.RpcRule.HELLOSERVERIMP_SAYHELLO_RULE;

/**
 *
 * 客戶端啟動類
 *
 */
public class CustomerBootstrap {


    public static void main(String[] args) throws Exception {


        //創建一個消費者
        NettyRpcCustomer nettyRpcCustomer = new NettyRpcCustomer("127.0.0.1",8888);


        //創建代理對象
        HelloServer helloServer = (HelloServer) nettyRpcCustomer.getBen(HelloServer.class, HELLOSERVERIMP_SAYHELLO_RULE);


        //通過代理對象調用服務提供者的方法(服務)
        for (;;){
            Thread.sleep(2000);
            String s = helloServer.sayHello("你好");
            System.out.println("返回內容:" + s );
        }

    }
}
