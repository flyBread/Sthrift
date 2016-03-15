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
  private Selector selector; // ����һ���¼�ѡ��������selector����
  private ServerSocketChannel serverSocketChannel; // ����һ��serversocketͨ��
  private int port = 5678;
  private Charset charset = Charset.forName("UTF-8");

  public UnBlockExecutorServer() throws Exception {
    selector = Selector.open(); // ����һ���¼�ѡ����selector��
    serverSocketChannel = ServerSocketChannel.open(); // ����һ��serverSocketChannel��
    serverSocketChannel.socket().setReuseAddress(true); // ���������µ�socket�󶨵���ͬ�˿�,������Ӧ�ڰ󶨶˿�֮ǰ�������������Ч
    serverSocketChannel.configureBlocking(false); // ����serverSocketChannelΪ��������ServerSocketChannel
    serverSocketChannel.socket().bind(new InetSocketAddress(port)); // �󶨶˿�
    System.out.println("�������Ѿ�������");
  }

  public static void main(String[] args) throws Exception {
    new UnBlockExecutorServer().server();
  }

  public void server() {
    try {
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // serverSocketChannel��selector��ע�����Ӿ����¼�
      while (selector.select() > 0) {
        Set readyKeys = selector.selectedKeys(); // �õ�selectorѡ�����е������¼�����
        Iterator it = readyKeys.iterator();
        while (it.hasNext()) {
          SelectionKey key = null;
          try {
            key = (SelectionKey) it.next(); // ͨ�������õ�ÿ��key
            it.remove();
            if (key.isAcceptable()) {
              ServerSocketChannel ssc = (ServerSocketChannel) key.channel(); // �õ���Selectionkey������Channel
              SocketChannel socketChannel = ssc.accept(); // �������Ӳ��һ�ú�ServerSocketChannel�������ӵ�SocketChannel
              System.out.println("���տͻ��˵����ӣ������ڣ�" + socketChannel.socket().getInetAddress() + ":"
                  + socketChannel.socket().getPort());
              socketChannel.configureBlocking(false); // ����socketChannelΪ��������socketChannel
              ByteBuffer buffer = ByteBuffer.allocate(1024);// ����һ������Ϊ1024�Ļ�����
              socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE,
                  buffer); // ��selector��ע��������¼���д�����¼������Ҹ���һ��buffer����
            }
            if (key.isReadable()) {
              ByteBuffer buffer = (ByteBuffer) key.attachment(); // �õ�SelectionKey���������ĸ���
              SocketChannel socketChannel = (SocketChannel) key.channel(); // �õ���selectionkey������SocketChannel
              ByteBuffer readbuffer = ByteBuffer.allocate(32); // ����һ��������ȡsocketChannel��readbuffer
              socketChannel.read(readbuffer); // ��readbuffer����ȡsocketChannel������
              readbuffer.flip(); // ��readbuffer��ȡ������֮�󣬽�readbuffer��λ����Ϊ0����������Ϊλ��
              readbuffer.limit(readbuffer.capacity()); // ��readbuffer�ļ��޴�0����Ϊreadbuffer������
              buffer.put(readbuffer); // ��readbuffer�����ݴ��뵽buffer��
            }
            if (key.isWritable()) {
              ByteBuffer buffer = (ByteBuffer) key.attachment(); // �õ���selectionkey������buffer����
              SocketChannel socketChannel = (SocketChannel) key.channel(); // �õ�selectionkey������socketChannel
              buffer.flip(); // ��buffer��λ����Ϊ0����������Ϊλ��
              String data = decode(buffer); // ����utf-8��buffer���н���
              if (data.indexOf("/r/n") == -1) {
                return;
              }
              String outdata = data.substring(0, data.indexOf("/r/n") + 1); // ��ȡdata�г���/r/n֮����ַ���
              System.out.println("��ȡ���������ǣ�" + outdata);

              String return_msg = "Hi,���Ƿ��ص���Ϣ"; // ���ص��ͻ��˵���Ϣ
              ByteBuffer sendBuffer = encode(return_msg);// �����ص��ͻ��˵���Ϣ���б���
              while (sendBuffer.hasRemaining()) {// ���sendBuffer�������ݾͽ�����д�뵽socketChannel
                socketChannel.write(sendBuffer);
              }
              ByteBuffer tempbuffer = encode(outdata); // ����ȡ�������ݱ����Ϊһ��tempbuffer
              buffer.position(tempbuffer.limit()); // ��buffer��λ�ø�Ϊtempbuffer�ļ���(���޸�buffer��λ��position����ȡ��������ռ��λ��)
              buffer.compact();// �����buffer���Ѿ�ռ�õĿռ䣬��֮ǰ��ȡ�����ݵ�λ��position��Ϊ0�����޸�Ϊlimit-position
              if (outdata.equals("bye/r/n")) {
                key.cancel(); // �����selectionkey����Ȥ���¼�
                key.channel().close(); // �رպ�selectionkey������channel
                System.out.println("�رպͿͻ��˵�����");
              }
            }
          }
          catch (Exception e) {
            // TODO Auto-generated catch block
            if (key != null) {
              key.cancel(); // ʹSelectionKeyʧЧ��ʹ��selectionkey������¼�������Ȥ
              key.channel().close(); // �رպ����selectionkey������socketChannel
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

  // ��buffer���н���
  public String decode(ByteBuffer buffer) throws Exception {
    CharBuffer charset = this.charset.decode(buffer); // ��bufferתΪutf-8��ʽ��charbuffer
    return charset.toString();
  }

  // ��String���б���
  public ByteBuffer encode(String str) {
    return this.charset.encode(str);
  }
}