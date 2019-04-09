package com.lx.qz.sample;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


public class NIOClient {
    private final static InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 9000);
    /* 表示数字 */
    private static int flag = 1;
    /* 缓冲区大小 */
    private static int blockSize = 4096;
    /* 接收缓冲区 */
    private static ByteBuffer sendBuffer = ByteBuffer.allocate(blockSize);
    /* 发送缓存区 */
    private static ByteBuffer receiveBuffer = ByteBuffer.allocate(blockSize);

    public static void main(String[] args) throws IOException {
        //打开socket通道
        SocketChannel socketChannel = SocketChannel.open();
        //设置为阻塞方式
        socketChannel.configureBlocking(false);
        //打开选择器
        Selector selector = Selector.open();
        //注册连接服务端socket动作
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        //连接
        socketChannel.connect(serverAddress);

        Set<SelectionKey> selectionKeys;
        Iterator<SelectionKey> iterator;
        SelectionKey selectionKey;
        SocketChannel client;
        String receiveTest;
        String sendText;
        int count = 0;

        while (true) {
            // 连接一组键，其相应的通道已为I/O操作准备就绪
            // 此方法执行处于阻塞模式的选择操作
            selector.select();
            // 返回此选择器的已选集键集
            selectionKeys = selector.selectedKeys();
            iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                selectionKey = iterator.next();
                if (selectionKey.isConnectable()) {
                    System.out.println("client connect");
                    client = (SocketChannel) selectionKey.channel();
                    // 判断此通道上是否正在进行连接操作
                    // 完成套接字通道的连接过程。
                    if (client.isConnectionPending()) {
                        client.finishConnect();
                        System.out.println("客户端完成连接操作!");
                        sendBuffer.clear();
                        sendBuffer.put("hello,Server".getBytes());
                        sendBuffer.flip();
                        client.write(sendBuffer);
                    }
                    client.register(selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    client = (SocketChannel) selectionKey.channel();
                    // 读取服务器发送来的数据到缓存区中
                    receiveBuffer.clear();
                    count = client.read(receiveBuffer);
                    if (count > 0) {
                        receiveTest = new String(receiveBuffer.array(), 0, count);
                        System.out.println("客户端接收到服务端的数据：" + receiveTest);
                        client.register(selector, SelectionKey.OP_WRITE);
                    }
                } else if (selectionKey.isWritable()) {
                    sendBuffer.clear();
                    client = (SocketChannel) selectionKey.channel();
                    sendText = "Msg send to Server->" + flag++;
                    sendBuffer.put(sendText.getBytes());
                    sendBuffer.flip();
                    client.write(sendBuffer);
                    System.out.println("客户端发送数据给服务器端");
                    client.register(selector, SelectionKey.OP_READ);
                }
            }
            selectionKeys.clear();
        }

    }
}