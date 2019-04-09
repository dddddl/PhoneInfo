package com.lx.qz.sample;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import static java.lang.Thread.sleep;

/**
 * NIO Socket Server
 *
 * @author coshaho
 */
public class NioTcpServer {

    /* 标识数字 */
    private int flag = 1;
    /* 缓冲区大小 */
    private int blockSize = 4096;
    /* 接受数据缓冲区 */
    private ByteBuffer sendBuffer = ByteBuffer.allocate(blockSize);
    /* 发送数据缓冲区 */
    private ByteBuffer receiveBuffer = ByteBuffer.allocate(blockSize);

    private Selector selector;

    public NioTcpServer(int port) throws IOException {

        //打开服务器套接字通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //服务器配置为阻塞
        serverSocketChannel.configureBlocking(false);
        //检索与此通道关联的服务器套接字
        ServerSocket serverSocket = serverSocketChannel.socket();
        //进行服务的绑定
        serverSocket.bind(new InetSocketAddress(port));
        //通过open()方法接到Selector
        selector = Selector.open();
        //注册selector,等待连接
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server start--->" + port);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 9000;
        NioTcpServer nioServer = new NioTcpServer(port);
        nioServer.listen();
    }

    //监听
    public void listen() throws IOException, InterruptedException {
        while (true) {
            //选择一组件，并且相应的通道已经打开
            selector.select();
            //返回此选择器的已选择键集
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                //具体业务逻辑
                handleKey(selectionKey);
            }
            sleep(1000);
        }
    }

    //处理请求
    public void handleKey(SelectionKey selectionKey) throws IOException {
        //接收请求
        ServerSocketChannel server = null;
        SocketChannel client = null;
        String receiveText;
        String sendText;
        int count = 0;
        // 测试此键的通道是否已经准备好接收新的套接字连接
        if (selectionKey.isAcceptable()) {
            //返回为之创建此键的通道
            server = (ServerSocketChannel) selectionKey.channel();
            //接受到此通道套接字的连接
            //此方法返回的套接字通道(如果有)将处于阻塞模式。
            client = server.accept();
            //配置为阻塞
            client.configureBlocking(false);
            //注册selector，等待连接
            client.register(selector, SelectionKey.OP_READ);
        } else if (selectionKey.isReadable()) {
            client = (SocketChannel) selectionKey.channel();
            // 将缓冲区清空以备下次读取
            receiveBuffer.clear();
            // 读取哭护短发送来的数据到缓冲区中
            count = client.read(receiveBuffer);
            if (count > 0) {
                receiveText = new String(receiveBuffer.array(), 0, count);
                System.out.println("服务端接收到的客户信息：" + receiveText);
                client.register(selector, SelectionKey.OP_WRITE, ByteBuffer.wrap("TEST".getBytes()));
            }
        } else if (selectionKey.isWritable()) {
            // 将缓冲区清空以备下次写入
            sendBuffer.clear();
            client = (SocketChannel) selectionKey.channel();

            ByteBuffer buf = (ByteBuffer) selectionKey.attachment();
            if (buf == null) {
                //发送数据
                sendText = "msg send to client：" + flag++;
                buf = ByteBuffer.wrap(sendText.getBytes());
                System.out.println("服务端发送数据给客户端：" + sendText);
            }else {
                System.out.println("buf == null");
            }

            // 向缓冲区中输入数据
            sendBuffer.put(buf);
            // 将缓冲区各标志复位,因为向里面put了数据标志被改变要想从中读取数据发向哭护短,就要复位
            sendBuffer.flip();
            // 输出到通道
            client.write(sendBuffer);

//            client.register(selector, SelectionKey.OP_WRITE);
            if (!sendBuffer.hasRemaining()) {
                //如果缓冲区中的数据已经全部写入了信道，则将该信道感兴趣的操作设置为可读
                selectionKey.interestOps(SelectionKey.OP_READ);
            }
            sendBuffer.compact();
        }

    }
}