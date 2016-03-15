package com.github.baseExample.fileChannel;

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
  private Selector selector; // 创建一个事件选择器的类selector对象
  private ServerSocketChannel serverSocketChannel; // 创建一个serversocket通道
  private int port = 5678;
  private Charset charset = Charset.forName("UTF-8");

  public UnBlockExecutorServer() throws Exception {
    selector = Selector.open(); // 生成一个事件选择器selector类
    serverSocketChannel = ServerSocketChannel.open(); // 生成一个serverSocketChannel类
    serverSocketChannel.socket().setReuseAddress(true); // 设置允许新的socket绑定到相同端口,该设置应在绑定端口之前，否则该设置无效
    serverSocketChannel.configureBlocking(false); // 设置serverSocketChannel为非阻塞的ServerSocketChannel
    serverSocketChannel.socket().bind(new InetSocketAddress(port)); // 绑定端口
    System.out.println("服务器已经启动：");
  }

  public static void main(String[] args) throws Exception {
    new UnBlockExecutorServer().server();
  }

  public void server() {
    try {
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // serverSocketChannel向selector中注入连接就绪事件
      while (selector.select() > 0) {
        Set readyKeys = selector.selectedKeys(); // 得到selector选择器中的所有事件集合
        Iterator it = readyKeys.iterator();
        while (it.hasNext()) {
          SelectionKey key = null;
          try {
            key = (SelectionKey) it.next(); // 通过迭代得到每个key
            it.remove();
            if (key.isAcceptable()) {
              ServerSocketChannel ssc = (ServerSocketChannel) key.channel(); // 得到和Selectionkey关联的Channel
              SocketChannel socketChannel = ssc.accept(); // 建立连接并且获得和ServerSocketChannel建立连接的SocketChannel
              System.out.println("接收客户端的连接，来自于：" + socketChannel.socket().getInetAddress() + ":"
                  + socketChannel.socket().getPort());
              socketChannel.configureBlocking(false); // 设置socketChannel为非阻塞的socketChannel
              ByteBuffer buffer = ByteBuffer.allocate(1024);// 创建一个容量为1024的缓冲区
              socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE,
                  buffer); // 向selector中注入读就绪事件和写就绪事件，并且附带一个buffer附件
            }
            if (key.isReadable()) {
              ByteBuffer buffer = (ByteBuffer) key.attachment(); // 得到SelectionKey中所关联的附件
              SocketChannel socketChannel = (SocketChannel) key.channel(); // 得到与selectionkey关联的SocketChannel
              ByteBuffer readbuffer = ByteBuffer.allocate(32); // 创建一个用来读取socketChannel的readbuffer
              socketChannel.read(readbuffer); // 用readbuffer来读取socketChannel的数据
              readbuffer.flip(); // 在readbuffer读取过数据之后，将readbuffer的位置设为0，将极限设为位置
              readbuffer.limit(readbuffer.capacity()); // 将readbuffer的极限从0设置为readbuffer的容量
              buffer.put(readbuffer); // 将readbuffer的数据存入到buffer中
            }
            if (key.isWritable()) {
              ByteBuffer buffer = (ByteBuffer) key.attachment(); // 得到与selectionkey关联的buffer附件
              SocketChannel socketChannel = (SocketChannel) key.channel(); // 得到selectionkey关联的socketChannel
              buffer.flip(); // 将buffer的位置设为0，将极限设为位置
              String data = decode(buffer); // 按照utf-8对buffer进行解码
              if (data.indexOf("/r/n") == -1) {
                return;
              }
              String outdata = data.substring(0, data.indexOf("/r/n") + 1); // 截取data中出了/r/n之外的字符串
              System.out.println("读取到的数据是：" + outdata);

              String return_msg = "Hi,我是返回的消息"; // 返回到客户端的消息
              ByteBuffer sendBuffer = encode(return_msg);// 将返回到客户端的消息进行编码
              while (sendBuffer.hasRemaining()) {// 如果sendBuffer中有数据就将数据写入到socketChannel
                socketChannel.write(sendBuffer);
              }
              ByteBuffer tempbuffer = encode(outdata); // 将读取到的数据编码成为一个tempbuffer
              buffer.position(tempbuffer.limit()); // 将buffer的位置改为tempbuffer的极限(即修改buffer的位置position到读取的数据所占的位置)
              buffer.compact();// 清除掉buffer中已经占用的空间，将之前读取的数据的位置position设为0，极限改为limit-position
              if (outdata.equals("bye/r/n")) {
                key.cancel(); // 清除掉selectionkey感兴趣的事件
                key.channel().close(); // 关闭和selectionkey关联的channel
                System.out.println("关闭和客户端的连接");
              }
            }
          }
          catch (Exception e) {
            // TODO Auto-generated catch block
            if (key != null) {
              key.cancel(); // 使SelectionKey失效，使得selectionkey对这个事件不感兴趣
              key.channel().close(); // 关闭和这个selectionkey关联的socketChannel
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

  // 对buffer进行解码
  public String decode(ByteBuffer buffer) throws Exception {
    CharBuffer charset = this.charset.decode(buffer); // 将buffer转为utf-8格式的charbuffer
    return charset.toString();
  }

  // 对String进行编码
  public ByteBuffer encode(String str) {
    return this.charset.encode(str);
  }
}