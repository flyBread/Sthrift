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
    thread.start(); // �������տͻ����������������Ϣ���߳�
    unBlockClient.talk(); // ��ʼ�ͻ��˺ͷ������ĶԻ�

  }

  public static void maifn(String args[]) {
    try {
      @SuppressWarnings("resource")

      Selector selector = Selector.open(); // ��̬���� ʵ����selector
      ServerSocketChannel serverChannel = ServerSocketChannel.open();
      serverChannel.configureBlocking(false); // ����Ϊ��������ʽ,���Ϊtrue ��ô��Ϊ��ͳ��������ʽ
      serverChannel.socket().bind(new InetSocketAddress(4700)); // ��IP �� �˿�
      serverChannel.register(selector, SelectionKey.OP_ACCEPT); // ע��
                                                                // OP_ACCEPT�¼�

      // new ServerThread().start(); // ����һ���߳� ������������

      ServerSocket server = new ServerSocket(4700);

      // ����һ��ServerSocket�ڶ˿�4700�����ͻ�����
      // ʹ��accept()�����ȴ��ͻ������пͻ�
      // �����������һ��Socket���󣬲�����ִ��
      while (true) {
        Socket connection = server.accept();
        handleRequest(connection);
        connection.close();
      }
    }
    catch (Exception e) {
      System.out.println("Error:" + e);
      // ������ӡ������Ϣ
    }
  }

  private static void handleRequest(Socket socket) throws IOException {
    BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    // ��Socket����õ�����������������Ӧ��BufferedReader����
    PrintWriter os = new PrintWriter(socket.getOutputStream());
    // ��Socket����õ��������������PrintWriter����
    // ��ϵͳ��׼�����豸����BufferedReader����
    int flag = 0;
    while (flag < 100) {
      String line = is.readLine();
      if (line != null) {
        System.out.println("���������յ��ͻ��˵ķ��͵���Ϣ��" + line);
        os.println("�����Ӧ����Ϣ��" + Math.random());
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

    os.close(); // �ر�Socket�����
    is.close(); // �ر�Socket������
  }
}