package com.example.nettypro.dubborpcmy.provider;

import com.example.nettypro.dubborpcmy.publicinterface.HelloServer;

public class HelloServerImp implements HelloServer {
    @Override
    public String sayHello(String msg) {

        System.out.println("sayHello......");

        if(msg != null){
            return "服務端收到你的消息 : " + msg;
        }else {
            return "服務端回傳..." ;
        }


    }
}
