package com.example.nettypro.dubborpc.provider;


import com.example.nettypro.dubborpc.publicinterface.HelloService;

public class HelloServiceImpl implements HelloService {

    private static int count = 0;
    //當有消費方調用該方法時， 就返回一個結果
    @Override
    public String hello(String mes) {
        System.out.println("收到客戶端消息=" + mes);
        //根據mes 返回不同的結果
        if(mes != null) {
            return "你好客戶端, 我已經收到你的消息 [" + mes + "] 第" + (++count) + " 次";
        } else {
            return "你好客戶端, 我已經收到你的消息 ";
        }
    }
}
