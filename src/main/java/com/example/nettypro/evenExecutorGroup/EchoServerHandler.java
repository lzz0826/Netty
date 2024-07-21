/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.example.nettypro.evenExecutorGroup;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    //static 可以讓所有 Handler 共用  group 充當業務線程池 可以將任務提交到該線程
    static final EventExecutorGroup group = new DefaultEventExecutorGroup(16);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        System.out.println("EchoServerHandler 線程:"+Thread.currentThread().getId());

        //使用一般eventLoop會使用同一線程會阻塞
//        eventLoop(ctx);

        //使用 EventExecutorGroup 異步線程池
        eventExecutorGroup(ctx, msg);

        //接收客戶端消息 測試在 EchoServer 使用 EventExecutorGroup
//        ByteBuf byteBuf = (ByteBuf) msg;
//        byte[] bytes = new byte[byteBuf.readableBytes()];
//        byteBuf.readBytes(bytes);
//        String s = new String(bytes, CharsetUtil.UTF_8);
//        System.out.println(s);
//        System.out.println("groupEchoServer 線程:"+Thread.currentThread().getId());


        System.out.println("GO ON.....");
    }

    /**
     *
     * 使用EventExecutorGroup異步線程池
     * EchoServerHandler 線程:18
     * Hello Server
     * eventExecutorGroup 線程:18
     * GO ON.....
     * EventExecutorGroup call() Hello Client001 線程:19
     * EventExecutorGroup call() Hello Client002 線程:20
     */
    private void eventExecutorGroup(ChannelHandlerContext ctx,Object msg){

        //接收客戶端消息
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        String s = new String(bytes, CharsetUtil.UTF_8);
        System.out.println(s);

        System.out.println("eventExecutorGroup 線程:"+Thread.currentThread().getId());


        group.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                Thread.sleep(10000);
                System.out.println("EventExecutorGroup call() Hello Client001 線程:"+Thread.currentThread().getId());
                //向客戶端發消息
                ctx.writeAndFlush(Unpooled.copiedBuffer("Hello Client001",CharsetUtil.UTF_8));
                return null;
            }
        });

        group.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                Thread.sleep(10000);
                System.out.println("EventExecutorGroup call() Hello Client002 線程:"+Thread.currentThread().getId());
                //向客戶端發消息
                ctx.writeAndFlush(Unpooled.copiedBuffer("Hello Client002",CharsetUtil.UTF_8));
                return null;
            }
        });

    }

    /**
     * 使用般的eventLoop會有阻塞問題(使用同一個線程)
     * EchoServerHandler 線程:18
     * GO ON.....
     * eventLoop 線程:18
     */
    private void eventLoop(ChannelHandlerContext ctx){
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    System.out.println("eventLoop 線程:"+Thread.currentThread().getId());
                    ctx.writeAndFlush(Unpooled.copiedBuffer("Hello Client001",CharsetUtil.UTF_8));
                } catch (Exception e) {
                    System.out.println("發生異常"+e.getMessage());
                }
            }
        });

        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    System.out.println("eventLoop 線程:"+Thread.currentThread().getId());
                    ctx.writeAndFlush(Unpooled.copiedBuffer("Hello Client002",CharsetUtil.UTF_8));
                } catch (Exception e) {
                    System.out.println("發生異常"+e.getMessage());
                }
            }
        });
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
