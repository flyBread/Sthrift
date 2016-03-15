package com.github.baseExample.fileChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TalkServer {

  @SuppressWarnings("resource")
  public static void main(String args[]) throws Exception {
    ServerSocket server = new ServerSocket(4700);
    while (true) {
      Socket connection = server.accept();
      handleRequest(connection);
      connection.close();
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
      }
    }
    os.close(); // �ر�Socket�����
    is.close(); // �ر�Socket������
  }
}