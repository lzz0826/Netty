package com.example.nettypro.googleProto.mydatainfo;

import com.example.nettypro.googleProto.codec.StudentPOJO;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Random;


/**
 *
 * 繼承Netty 規定的某個HandlerAdapter(需要遵守netty規範)
 */
@Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<MyDataInfo.MyMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyDataInfo.MyMessage msg) throws Exception {

        System.out.println("服務端收到消息...");

        MyDataInfo.MyMessage.DataType type = msg.getDataType();

        switch (type){
            case StudentType :
                MyDataInfo.Student student = msg.getStudent();
                System.out.println("收到student類: id : "+student.getId() + "name : "+ student.getName());
                break;
            case WorkerType :
                MyDataInfo.Worker worker = msg.getWorker();
                System.out.println("收到worker類: name : "+worker.getName() + " age : "+ worker.getAge());

                break;
            default :
                System.out.println("數據類型都不符合");
                break;
        }
    }
    /**
     * 數據讀取完畢
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {

        System.out.println("服務端發送消息...");

        Random random = new Random();
        int randomNumber = random.nextInt(2); // 生成 0 或 1

        MyDataInfo.MyMessage message = null ;
        if(randomNumber == 1){
            MyDataInfo.MyMessage clientStudent = MyDataInfo.MyMessage.newBuilder()
                    .setDataType(MyDataInfo.MyMessage.DataType.StudentType)
                    .setStudent(
                            MyDataInfo.Student.newBuilder()
                                    .setId(111)
                                    .setName("Server Student")
                                    .build()
                    )
                    .build();
            message = clientStudent;
        }else {
            MyDataInfo.MyMessage clientWorker = MyDataInfo.MyMessage.newBuilder()
                    .setDataType(MyDataInfo.MyMessage.DataType.WorkerType)
                    .setWorker(
                            MyDataInfo.Worker.newBuilder()
                                    .setName("Server Work")
                                    .setAge(1111)
                                    .build()
                    )
                    .build();
            message = clientWorker;
        }

        ctx.writeAndFlush(message);


    }


    /**
     * 處理異常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }



}
