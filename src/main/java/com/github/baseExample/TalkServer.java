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

      // 创建一个ServerSocket在端口4700监听客户请求
      // 使用accept()阻塞等待客户请求，有客户
      // 请求到来则产生一个Socket对象，并继续执行
      while (true) {
        Socket connection = server.accept();
        handleRequest(connection);
        connection.close();
      }
    }
    catch (Exception e) {
      System.out.println("Error:" + e);
      // 出错，打印出错信息
    }
  }

  private static void handleRequest(Socket socket) throws IOException {
    BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    // 由Socket对象得到输入流，并构造相应的BufferedReader对象
    PrintWriter os = new PrintWriter(socket.getOutputStream());
    // 由Socket对象得到输出流，并构造PrintWriter对象
    // 由系统标准输入设备构造BufferedReader对象
    int flag = 0;
    while (flag < 100) {
      String line = is.readLine();
      if (line != null) {
        System.out.println("服务器接收到客户端的发送的消息：" + line);
        os.println("服务端应答信息：" + Math.random());
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

    os.close(); // 关闭Socket输出流
    is.close(); // 关闭Socket输入流
  }
}