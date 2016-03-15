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
 * @Date 2016��3��7�ա�������4:17:26
 * 
 */
public class SelectorNIOTest {

  public void selector() throws IOException {
    Selector selector = Selector.open();
    ServerSocket server = new ServerSocket(4700);
    Socket connection = server.accept();
    SocketChannel channel = connection.getChannel();
    channel.configureBlocking(false);
    /*Ϊ�˽�Channel��Selector���ʹ�ã����뽫channelע�ᵽselector�ϡ�
     * ͨ��SelectableChannel.register()������ʵ�֣����£�
     * ��Selectorһ��ʹ��ʱ��Channel���봦�ڷ�����ģʽ�¡�
     * ����ζ�Ų��ܽ�FileChannel��Selectorһ��ʹ�ã���ΪFileChannel�����л���������ģʽ�����׽���ͨ�������ԡ�
     * ע��register()�����ĵڶ�������������һ����interest���ϡ�����˼����ͨ��Selector����Channelʱ��ʲô�¼�����Ȥ��
     * ���Լ������ֲ�ͬ���͵��¼���    Connect     Accept    Read    Write
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
