package com.github.baseExample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TalkClient {

  public void run() {

    Socket socket = null;
    try {
      socket = new Socket("127.0.0.1", 4700);
      // �򱾻���4700�˿ڷ����ͻ�����
      // ��ϵͳ��׼�����豸����BufferedReader����
      PrintWriter os = new PrintWriter(socket.getOutputStream());
      // ��Socket����õ��������������PrintWriter����
      BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      // ��Socket����õ�����������������Ӧ��BufferedReader����
      int flag = 0;
      while (flag < 100) {
        // ���ӱ�׼���������ַ���Ϊ "bye"��ֹͣѭ��
        os.println("�ͻ����Զ����ɷ��͵���Ϣ��" + System.currentTimeMillis());
        os.flush();
        // ����ϵͳ��׼���������ַ��������Server
        String line = is.readLine();
        System.out.println("����˵�Ӧ����Ϣ��:" + line);
        Thread.sleep(1000);
        flag += 2;
      } // ����ѭ��
      is.close();
      os.close();
      socket.close();
    }
    catch (Exception e) {
      System.out.println("Error" + e); // �������ӡ������Ϣ
    }
    finally {
      // �ر�Socket
    }
  }

  public static void main(String args[]) {
    TalkClient client = new TalkClient();
    client.run();
  }
}