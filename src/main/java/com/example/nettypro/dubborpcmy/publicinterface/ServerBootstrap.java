package com.example.nettypro.dubborpcmy.publicinterface;


import com.example.nettypro.dubborpcmy.netty.NettyRpcServer;

/**
 *
 * 服務端啟動類
 *
 */
public class ServerBootstrap {

    public static void main(String[] args) throws InterruptedException {


        NettyRpcServer.startNettyRpcServer("127.0.0.1",8888);




    }
}
