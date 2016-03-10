package com.github.baseExample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TalkServer {

  public static void main(String args[]) {
    try {
      @SuppressWarnings("resource")
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