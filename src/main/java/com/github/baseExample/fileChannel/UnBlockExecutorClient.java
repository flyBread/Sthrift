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

  private Selector selector = null;// ����һ���¼�ѡ����
  private ByteBuffer sendBuffer = ByteBuffer.allocate(1024);// �����洢�������������Ϣ��buffer
  private ByteBuffer reciveBuffer = ByteBuffer.allocate(1024);// �������շ�����������Ϣ��buffer
  private SocketChannel socketChannel = null; // ����һ��SocketChannel
  private Charset charest = Charset.forName("GBK"); // ����һ���ַ���

  public UnBlockExecutorClient() throws Exception {
    socketChannel = SocketChannel.open(); // ����socketChannel
    InetSocketAddress is = new InetSocketAddress("127.0.0.1", 5678); // ��ip�Ͷ˿ڵ�socket
    socketChannel.connect(is);
    socketChannel.configureBlocking(false);// ����socketchannel�Ƿ�������socket
    System.out.println("�������Ѿ�����");
    selector = Selector.open(); // �����¼�ѡ����
  }

  public static void main(String[] args) throws Exception {
    final UnBlockExecutorClient unBlockClient = new UnBlockExecutorClient();
    Thread thread = new Thread() {
      public void run() {
        unBlockClient.reciveMessageFromKeyBoard();
      }
    };
    thread.start(); // �������տͻ����������������Ϣ���߳�
    unBlockClient.talk(); // ��ʼ�ͻ��˺ͷ������ĶԻ�

  }

  /**
   * @Description �ͻ��˺ͷ�����������Ϣ
   */
  public void talk() throws Exception {
    socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);// ���¼�ѡ������ע���¼�
    while (selector.select() > 0) {
      Set<?> keySet = selector.keys();
      Iterator<?> iterator = keySet.iterator();
      while (iterator.hasNext()) {
        SelectionKey key = null;
        try {
          key = (SelectionKey) iterator.next();
          iterator.remove();
          if (key.isReadable()) {
            recive(key);// ��׼��������ʼ������Ϣ
          }
          if (key.isWritable()) {
            sendToClient(key);// д׼������������Ϣ
          }
        }
        catch (Exception e) {
          // TODO Auto-generated catch block
          if (key != null) {
            key.cancel(); // ����¼�
            key.channel().close();// �رպ��¼�������channel
          }
        }
      }
    }
  }

  /**
   * @Description ���մӼ����������Ϣ
   */
  public void reciveMessageFromKeyBoard() {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    String recive_msg = "";
    while (true) {
      System.out.println("���������������Ϣ��");
      try {
        recive_msg = reader.readLine();// ��ȡ����������͵���Ϣ
      }
      catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      synchronized (sendBuffer) {

        // ��Ҫ��������������Ϣ�����뵽sendBuffer��;��������Ϊ��д׼�������¼��е�
        // �õ�Ҳ�ǽ�sendBuffer�����͵�������������Ϊ���߳�ͬ����ȫ���Լ�����
        sendBuffer.put(encode(recive_msg));
      }
      if (recive_msg.equals("bye")) {
        break;
      }
    }
  }

  /**
   * @param ׼���������¼�
   * @��ͻ��˷�����Ϣ
   */
  public void sendToClient(SelectionKey key) throws Exception {
    SocketChannel socketChannel = (SocketChannel) key.channel();// �õ�socketChannel
    synchronized (sendBuffer) {
      sendBuffer.flip(); // �Ѽ�����Ϊλ�ã���λ����Ϊ��
      socketChannel.write(sendBuffer); // ��Ҫ���͵���Ϣд��socketChannel��
      sendBuffer.compact();// ��buffer���ѷ��͵�����ɾ��
    }
  }

  /**
   * @param ׼���������¼�
   * @���շ��������ص���Ϣ
   */
  public void recive(SelectionKey key) {
    SocketChannel socketChannel = (SocketChannel) key.channel(); // �õ�socketChannel

    try {
      socketChannel.read(reciveBuffer);// ��socketChannel���յ���Ϣ���뵽reciveBuffer��
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

    String out_msg = recive_msg.substring(0, recive_msg.indexOf("\n") + 1);// ��ȥ����\n\r�����ַ�ȥ��֮����ַ������
    System.out.println("���������ص���Ϣ�ǣ�" + out_msg);

    if (out_msg.equals("bye")) {
      try {
        key.cancel();// ����¼�
        socketChannel.close();// �ر�socketChannel����
        System.out.println("�رպͷ�����������");
        selector.close();// �ر��¼�ѡ����
      }
      catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }

  /**
   * @���룺��bytebufferת��Ϊ�ַ���
   */
  public String decode(ByteBuffer buffer) {
    CharBuffer charBuffer = charest.decode(buffer);
    return charBuffer.toString();
  }

  /**
   * @���룺���ַ���תΪbyteBuffer
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