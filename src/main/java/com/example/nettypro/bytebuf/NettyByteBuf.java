package com.example.nettypro.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class NettyByteBuf {


    public static void main(String[] args) {

//        useBufferCreat();//使用Unpooled.buffer創建

        useCopiedBuffer();//使用CopiedBuffer


    }


    /**
     * 創建 ByteBuf
     *
     * 創建 對象 該對象包含一個數組 arr  是一個[10]
     * 在netty 的 buffer 中 不需要使用 flip 進行反轉 readerindex 底層維護了 和 writerIndex
     * 通過 readerindex 和 writerIndex 和 capacity 將 buffer 分成三個區域
     * 0 -- readerindex 已讀取的區域
     * readerindex -- writerIndex 可讀區域
     * writerIndex -- capacity 可寫的區域
     *
     *
     */
    public static void useBufferCreat(){
        ByteBuf byteBuf = Unpooled.buffer(10);
        for(int i = 0 ; i < 10 ; i++){
            byteBuf.writeByte(i);
        }
        for(int i = 0 ; i < byteBuf.capacity() ; i ++){

            //透過 index 來取
            System.out.println(byteBuf.getByte(i));
        }
        for(int i = 0 ; i < byteBuf.capacity(); i ++){

            //透過 readerlndex
            System.out.println(byteBuf.readByte());
        }
    }


    public static void useCopiedBuffer(){

        ByteBuf byteBuf = Unpooled.copiedBuffer("Hello World!", CharsetUtil.UTF_8);

        //相關方法
        if(byteBuf.hasArray()){

            byte[] content = byteBuf.array();
            System.out.println((new String(content, CharsetUtil.UTF_8)));

            System.out.println("ByteBuf = " + byteBuf);
            System.out.println(byteBuf.arrayOffset()); // 數組偏移量
            System.out.println(byteBuf.readerIndex()); // 讀取索引
            System.out.println(byteBuf.writerIndex()); // 寫入索引
            System.out.println(byteBuf.capacity()); // 容量

            System.out.println(byteBuf.getByte(2)); // 使用索引取值(不會實際消耗該值)

            System.out.println(byteBuf.readByte()); // 讀取下一個值，並消耗該值

            int len = byteBuf.readableBytes(); // 可讀的字節數

            System.out.println("len : " + len);

            for(int i = 0 ; i< len ; i ++){
                System.out.println((char) byteBuf.getByte(i));
            }

            // 獲取從索引位置0開始，長度為4的字符序列，並將其轉換為UTF-8字符串
            System.out.println(byteBuf.getCharSequence(0,4,CharsetUtil.UTF_8));
            System.out.println(byteBuf.getCharSequence(4,6,CharsetUtil.UTF_8));
            System.out.println("len : " + len);




        }


    }


}
