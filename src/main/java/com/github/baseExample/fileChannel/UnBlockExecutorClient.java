package com.github.baseExample.fileChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class UnBlockExecutorClient {

  private Selector selector = null;// 创建一个事件选择器
  private ByteBuffer sendBuffer = ByteBuffer.allocate(1024);// 用来存储向服务器发送消息的buffer
  private ByteBuffer reciveBuffer = ByteBuffer.allocate(1024);// 用来接收服务器返回消息的buffer
  private SocketChannel socketChannel = null; // 创建一个SocketChannel
  private Charset charest = Charset.forName("GBK"); // 创建一个字符集

  public UnBlockExecutorClient() throws Exception {
    socketChannel = SocketChannel.open(); // 生成socketChannel
    InetSocketAddress is = new InetSocketAddress("127.0.0.1", 5678); // 绑定ip和端口到socket
    socketChannel.connect(is);
    socketChannel.configureBlocking(false);// 设置socketchannel是非阻塞的socket
    System.out.println("服务器已经连接");
    selector = Selector.open(); // 生成事件选择器
  }

  public static void main(String[] args) throws Exception {
    final UnBlockExecutorClient unBlockClient = new UnBlockExecutorClient();
    Thread thread = new Thread() {
      public void run() {
        unBlockClient.reciveMessageFromKeyBoard();
      }
    };
    thread.start(); // 启动接收客户端向服务器发送消息的线程
    unBlockClient.talk(); // 开始客户端和服务器的对话

  }

  /**
   * @Description 客户端和服务器交互消息
   */
  public void talk() throws Exception {
    socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);// 向事件选择器中注入事件
    while (selector.select() > 0) {
      Set<?> keySet = selector.keys();
      Iterator<?> iterator = keySet.iterator();
      while (iterator.hasNext()) {
        SelectionKey key = null;
        try {
          key = (SelectionKey) iterator.next();
          iterator.remove();
          if (key.isReadable()) {
            recive(key);// 读准备就绪开始接收消息
          }
          if (key.isWritable()) {
            sendToClient(key);// 写准备就绪发送消息
          }
        }
        catch (Exception e) {
          // TODO Auto-generated catch block
          if (key != null) {
            key.cancel(); // 清空事件
            key.channel().close();// 关闭和事件关联的channel
          }
        }
      }
    }
  }

  /**
   * @Description 接收从键盘输入的消息
   */
  public void reciveMessageFromKeyBoard() {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    String recive_msg = "";
    while (true) {
      System.out.println("请向服务器发送消息：");
      try {
        recive_msg = reader.readLine();// 读取向服务器发送的消息
      }
      catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      synchronized (sendBuffer) {

        // 将要发到服务器的消息，放入到sendBuffer中;加锁是因为在写准备就绪事件中调
        // 用的也是将sendBuffer对象发送到服务器，所以为了线程同步安全所以加了锁
        sendBuffer.put(encode(recive_msg));
      }
      if (recive_msg.equals("bye")) {
        break;
      }
    }
  }

  /**
   * @param 准备就绪的事件
   * @向客户端发送消息
   */
  public void sendToClient(SelectionKey key) throws Exception {
    SocketChannel socketChannel = (SocketChannel) key.channel();// 得到socketChannel
    synchronized (sendBuffer) {
      sendBuffer.flip(); // 把极限设为位置，把位置设为零
      socketChannel.write(sendBuffer); // 将要发送的消息写入socketChannel中
      sendBuffer.compact();// 将buffer中已发送的数据删除
    }
  }

  /**
   * @param 准备就绪的事件
   * @接收服务器返回的消息
   */
  public void recive(SelectionKey key) {
    SocketChannel socketChannel = (SocketChannel) key.channel(); // 得到socketChannel

    try {
      socketChannel.read(reciveBuffer);// 将socketChannel接收到消息读入到reciveBuffer中
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    reciveBuffer.flip();
    String recive_msg = decode(reciveBuffer);
    if (recive_msg.indexOf("\n") == -1) {
      return;
    }

    String out_msg = recive_msg.substring(0, recive_msg.indexOf("\n") + 1);// 将去除“\n\r”的字符去掉之后的字符串输出
    System.out.println("服务器返回的消息是：" + out_msg);

    if (out_msg.equals("bye")) {
      try {
        key.cancel();// 清空事件
        socketChannel.close();// 关闭socketChannel连接
        System.out.println("关闭和服务器的连接");
        selector.close();// 关闭事件选择器
      }
      catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }

  /**
   * @解码：将bytebuffer转换为字符串
   */
  public String decode(ByteBuffer buffer) {
    CharBuffer charBuffer = charest.decode(buffer);
    return charBuffer.toString();
  }

  /**
   * @编码：将字符串转为byteBuffer
   */
  public ByteBuffer encode(String str) {
    return charest.encode(str);
  }

  public BufferedReader getReader(Socket socket) throws Exception {
    return new BufferedReader(new InputStreamReader(socket.getInputStream()));
  }

  public PrintWriter getWriter(Socket socket) throws Exception {
    return new PrintWriter(socket.getOutputStream(), true);
  }
}