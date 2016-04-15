package com.github.baseExample.unblockedsocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class UnBlockExecutorServer {
  private Selector selector;  
  private ServerSocketChannel serverSocketChannel;  
  private int port = 5678;
  private Charset charset = Charset.forName("UTF-8");

  public UnBlockExecutorServer() throws Exception {
    selector = Selector.open(); 
    serverSocketChannel = ServerSocketChannel.open();  
    serverSocketChannel.socket().setReuseAddress(true); 
    serverSocketChannel.configureBlocking(false);  
    serverSocketChannel.socket().bind(new InetSocketAddress(port));  
  }

  public static void main(String[] args) throws Exception {
    new UnBlockExecutorServer().server();
  }

  public void server() {
    try {
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
      while (selector.select() > 0) {//没有Client段连接的时候，会在selector的时候
        Set<?> readyKeys = selector.selectedKeys();  
        Iterator<?> it = readyKeys.iterator();
        while (it.hasNext()) {
          SelectionKey key = null;
          try {
            key = (SelectionKey) it.next(); 
            it.remove();
            if (key.isAcceptable()) {
              ServerSocketChannel ssc = (ServerSocketChannel) key.channel(); //与客户端建立channel
              SocketChannel socketChannel = ssc.accept(); 
              System.out.println("接受客户端的连接：" + socketChannel.socket().getInetAddress() + ":"
                  + socketChannel.socket().getPort());
              socketChannel.configureBlocking(false); // socketChannelΪsocketChannel
              
              ByteBuffer buffer = ByteBuffer.allocate(1024);
              socketChannel.write(encode("向客户端发送一条已经接受连接的消息"));
              socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE,
                  buffer); //接受到客户端的连接以后，注册感兴趣的读事件和写事件
            }
//            if (key.isReadable()) {
//              ByteBuffer buffer = (ByteBuffer) key.attachment(); 
//              SocketChannel socketChannel = (SocketChannel) key.channel(); 
//              ByteBuffer readbuffer = ByteBuffer.allocate(32); 
//              socketChannel.read(readbuffer); 
//              readbuffer.flip(); 
//              readbuffer.limit(readbuffer.capacity()); 
//              buffer.put(readbuffer);
//            }
//            if (key.isWritable()) {
//              ByteBuffer buffer = (ByteBuffer) key.attachment(); 
//              SocketChannel socketChannel = (SocketChannel) key.channel(); 
//              buffer.flip(); 
//              String data = decode(buffer); 
//              if (data.indexOf("/r/n") == -1) {
//                return;
//              }
//              String outdata = data.substring(0, data.indexOf("/r/n") + 1); 
//              System.out.println("接受：" + outdata);
//
//              String return_msg = "Hi,client"; 
//              ByteBuffer sendBuffer = encode(return_msg);
//              while (sendBuffer.hasRemaining()) {
//                socketChannel.write(sendBuffer);
//              }
//              ByteBuffer tempbuffer = encode(outdata); 
//              buffer.position(tempbuffer.limit()); 
//              buffer.compact();
//              if (outdata.equals("bye/r/n")) {
//                key.cancel();
//                key.channel().close();
//                System.out.println("结束了");
//              }
//            }
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
    catch (ClosedChannelException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public String decode(ByteBuffer buffer) throws Exception {
    CharBuffer charset = this.charset.decode(buffer); // bufferתΪutf-8ʽcharbuffer
    return charset.toString();
  }

  public ByteBuffer encode(String str) {
    return this.charset.encode(str);
  }
}