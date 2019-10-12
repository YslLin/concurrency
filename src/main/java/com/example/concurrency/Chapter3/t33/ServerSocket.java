package com.example.concurrency.Chapter3.t33;

import com.example.concurrency.utils.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * echo 程序服务端
 * Java NIO的ServerSocketChannel就是用来监听TCP连接的，就像标准的Java网络编程里的ServerSocket一样。
 * ServerSocketChannel类在java.nio.channels包下。
 */
public class ServerSocket {

    public static void main(String[] args) {
        ServerSocketChannel ssc = null;
        try {
            // ServerSocketChannel.open() 打开一个 ServerSocketChannel
            // .bind() 绑定端口号 8080
            ssc = ServerSocketChannel.open().bind(new InetSocketAddress(8080));
            while (true) {
                // accept() 监听连入的TCP连接。方法会阻塞
                // 一般我们都不会只监听一个连接，所以你可以在一个while-循环内部调用accpet()方法。
                SocketChannel sc = ssc.accept();
                System.out.println(44444);
                new Thread(() -> {
                    try {
                        // 读 Socket
                        ByteBuffer rb = ByteBuffer.allocateDirect(1024);
                        sc.read(rb);
                        // 模拟处理请求
                        System.out.println(11111);
                        Utils.sleep(2);
                        ByteBuffer wb = (ByteBuffer) rb.flip();
                        sc.write(wb);
                        // 关闭 Socket
                        sc.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ssc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
