package com.example.nettypro.dubborpc.customer;


import com.example.nettypro.dubborpc.netty.NettyClient;
import com.example.nettypro.dubborpc.publicinterface.HelloService;

public class ClientBootstrap {


    //這裏定義協議頭
    public static final String providerName = "HelloService#hello#";

    public static void main(String[] args) throws  Exception{

        //創建一個消費者
        NettyClient customer = new NettyClient();

        //創建代理對象
        HelloService service = (HelloService) customer.getBean(HelloService.class, providerName);

        for (;; ) {
            Thread.sleep(2 * 1000);
            //通過代理對象調用服務提供者的方法(服務)
            String res = service.hello("你好 dubbo~");
            System.out.println("調用的結果 res= " + res);
        }
    }
}
