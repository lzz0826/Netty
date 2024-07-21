package com.example.nettypro.dubborpc.provider;


import com.example.nettypro.dubborpc.netty.NettyServer;

//ServerBootstrap 會啟動一個服務提供者，就是 NettyServer
public class ServerBootstrap {
    public static void main(String[] args) {

        //代碼代填..
        NettyServer.startServer("127.0.0.1", 7000);
    }
}
