package com.github.baseExample.unblockedsocket;

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

  private Selector selector = null;
  private ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
  private ByteBuffer reciveBuffer = ByteBuffer.allocate(1024);
  private SocketChannel socketChannel = null; 
  private Charset charest = Charset.forName("GBK"); 

  public UnBlockExecutorClient() throws Exception {
    socketChannel = SocketChannel.open(); 
    InetSocketAddress is = new InetSocketAddress("127.0.0.1", 5678); 
    socketChannel.connect(is);//服务器没有启动的时候，客户端连接就会直接的报错
    socketChannel.configureBlocking(false);
    selector = Selector.open(); 
  }

  public static void main(String[] args) throws Exception {
    final UnBlockExecutorClient unBlockClient = new UnBlockExecutorClient();
    Thread thread = new Thread() {
      public void run() {
        unBlockClient.reciveMessageFromKeyBoard();
      }
    };
    thread.start(); 
    unBlockClient.talk(); 

  }

  /**
   * @Description 向服务端发送消息
   */
  public void talk() throws Exception {
	//想这个selector注册 感兴趣的事件
	//这个在设计模式里面叫做 观察者模式
    socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    while (selector.select() > 0) {
      Set<?> keySet = selector.keys();
      Iterator<?> iterator = keySet.iterator();
      while (iterator.hasNext()) {
        SelectionKey key = null;
        try {
          key = (SelectionKey) iterator.next();
          iterator.remove();
          if (key.isReadable()) {
            recive(key);
          }
          if (key.isWritable()) {
            sendToClient(key);
          }
        }
        catch (Exception e) {
          if (key != null) {
            key.cancel(); 
            key.channel().close();
          }
        }
      }
    }
  }

  /**
   * @Description 接受键盘输入的内容，放进缓存中
   */
  public void reciveMessageFromKeyBoard() {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    String recive_msg = "";
    while (true) {
      System.out.println("请输入的客户端输入内容：");
      try {
        recive_msg = reader.readLine();
      }
      catch (IOException e) {
        e.printStackTrace();
      }

      synchronized (sendBuffer) {
        sendBuffer.put(encode(recive_msg));
      }
      if (recive_msg.equals("bye")) {
        break;
      }
    }
  }

  public void sendToClient(SelectionKey key) throws Exception {
    SocketChannel socketChannel = (SocketChannel) key.channel();// õsocketChannel
    synchronized (sendBuffer) {
      sendBuffer.flip(); // ѼΪλãλΪ
      socketChannel.write(sendBuffer); // Ҫ͵ϢдsocketChannel
      sendBuffer.compact();// bufferѷ͵ɾ
    }
  }

  /**
   * @param ׼¼
   * @շصϢ
   */
  public void recive(SelectionKey key) {
    SocketChannel socketChannel = (SocketChannel) key.channel(); // õsocketChannel

    try {
      socketChannel.read(reciveBuffer);// socketChannelյϢ뵽reciveBuffer
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

    String out_msg = recive_msg.substring(0, recive_msg.indexOf("\n") + 1);// ȥ\n\rַȥַ֮
    System.out.println("صϢǣ" + out_msg);

    if (out_msg.equals("bye")) {
      try {
        key.cancel();
        socketChannel.close();
        System.out.println("رպͷ");
        selector.close();
      }
      catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }

  public String decode(ByteBuffer buffer) {
    CharBuffer charBuffer = charest.decode(buffer);
    return charBuffer.toString();
  }

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