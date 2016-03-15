package com.github.baseExample.fileChannel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author zhailzh
 * 
 * @Date 2016年3月7日——下午4:17:26
 * 
 */
public class SelectorNIOTest {

  public void selector() throws IOException {
    Selector selector = Selector.open();
    ServerSocket server = new ServerSocket(4700);
    Socket connection = server.accept();
    SocketChannel channel = connection.getChannel();
    channel.configureBlocking(false);
    /*为了将Channel和Selector配合使用，必须将channel注册到selector上。
     * 通过SelectableChannel.register()方法来实现，如下：
     * 与Selector一起使用时，Channel必须处于非阻塞模式下。
     * 这意味着不能将FileChannel与Selector一起使用，因为FileChannel不能切换到非阻塞模式。而套接字通道都可以。
     * 注意register()方法的第二个参数。这是一个“interest集合”，意思是在通过Selector监听Channel时对什么事件感兴趣。
     * 可以监听四种不同类型的事件：    Connect     Accept    Read    Write
     * */
    //    SelectionKey key = 
    channel.register(selector, SelectionKey.OP_READ);
    while (true) {
      int readyChannels = selector.select();
      if (readyChannels == 0)
        continue;
      Set<SelectionKey> selectedKeys = selector.selectedKeys();
      Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
      while (keyIterator.hasNext()) {
        SelectionKey key = keyIterator.next();
        if (key.isAcceptable()) {
          // a connection was accepted by a ServerSocketChannel.
        } else if (key.isConnectable()) {
          // a connection was established with a remote server.
        } else if (key.isReadable()) {
          // a channel is ready for reading
        } else if (key.isWritable()) {
          // a channel is ready for writing
        }
        keyIterator.remove();
      }
    }
  }

  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
