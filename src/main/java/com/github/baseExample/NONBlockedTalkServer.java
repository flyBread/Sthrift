package com.github.baseExample;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class NONBlockedTalkServer {

  public static void main(String[] args) throws Exception {
    RandomAccessFile aFile = new RandomAccessFile(new File("intData.txt"), "rw");
    FileChannel inChannel = aFile.getChannel();
    ByteBuffer buf = ByteBuffer.allocate(48);
    int bytesRead = inChannel.read(buf);
    while (bytesRead != -1) {
      System.out.println("Read " + bytesRead);
      System.out.println(buf.position());
      buf.flip();
      System.out.println(buf.position());
      while (buf.hasRemaining()) {
        System.out.println((char) buf.get());
      }
      buf.clear();
      bytesRead = inChannel.read(buf);
    }
    aFile.close();

    final UnBlockExecutorClient unBlockClient = new UnBlockExecutorClient();
    Thread thread = new Thread() {
      public void run() {
        unBlockClient.reciveMessageFromKeyBoard();
      }
    };
    thread.start(); // 启动接收客户端向服务器发送消息的线程
    unBlockClient.talk(); // 开始客户端和服务器的对话

  }

  public static void maifn(String args[]) {
    try {
      @SuppressWarnings("resource")

      Selector selector = Selector.open(); // 静态方法 实例化selector
      ServerSocketChannel serverChannel = ServerSocketChannel.open();
      serverChannel.configureBlocking(false); // 设置为非阻塞方式,如果为true 那么就为传统的阻塞方式
      serverChannel.socket().bind(new InetSocketAddress(4700)); // 绑定IP 及 端口
      serverChannel.register(selector, SelectionKey.OP_ACCEPT); // 注册
                                                                // OP_ACCEPT事件

      // new ServerThread().start(); // 开启一个线程 处理所有请求

      ServerSocket server = new ServerSocket(4700);

      // 创建一个ServerSocket在端口4700监听客户请求
      // 使用accept()阻塞等待客户请求，有客户
      // 请求到来则产生一个Socket对象，并继续执行
      while (true) {
        Socket connection = server.accept();
        handleRequest(connection);
        connection.close();
      }
    }
    catch (Exception e) {
      System.out.println("Error:" + e);
      // 出错，打印出错信息
    }
  }

  private static void handleRequest(Socket socket) throws IOException {
    BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    // 由Socket对象得到输入流，并构造相应的BufferedReader对象
    PrintWriter os = new PrintWriter(socket.getOutputStream());
    // 由Socket对象得到输出流，并构造PrintWriter对象
    // 由系统标准输入设备构造BufferedReader对象
    int flag = 0;
    while (flag < 100) {
      String line = is.readLine();
      if (line != null) {
        System.out.println("服务器接收到客户端的发送的消息：" + line);
        os.println("服务端应答信息：" + Math.random());
        os.flush();
      } else {
        try {
          Thread.sleep(100);
        }
        catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

    }

    os.close(); // 关闭Socket输出流
    is.close(); // 关闭Socket输入流
  }
}