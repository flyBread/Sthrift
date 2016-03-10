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
      // 向本机的4700端口发出客户请求
      // 由系统标准输入设备构造BufferedReader对象
      PrintWriter os = new PrintWriter(socket.getOutputStream());
      // 由Socket对象得到输出流，并构造PrintWriter对象
      BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      // 由Socket对象得到输入流，并构造相应的BufferedReader对象
      int flag = 0;
      while (flag < 100) {
        // 若从标准输入读入的字符串为 "bye"则停止循环
        os.println("客户端自动生成发送的消息：" + System.currentTimeMillis());
        os.flush();
        // 将从系统标准输入读入的字符串输出到Server
        String line = is.readLine();
        System.out.println("服务端的应答信息是:" + line);
        Thread.sleep(1000);
        flag += 2;
      } // 继续循环
      is.close();
      os.close();
      socket.close();
    }
    catch (Exception e) {
      System.out.println("Error" + e); // 出错，则打印出错信息
    }
    finally {
      // 关闭Socket
    }
  }

  public static void main(String args[]) {
    TalkClient client = new TalkClient();
    client.run();
  }
}