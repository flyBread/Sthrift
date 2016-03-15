package thrift.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppServer {
  private static Logger logger = LoggerFactory.getLogger(AppServer.class);

  @SuppressWarnings("resource")
  public static void main(String args[]) throws Exception {
    new ClassPathXmlApplicationContext("classpath:thrift-server.xml");
  }
}