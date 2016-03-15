package thrift.client;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import thrift.BooleanResult;
import thrift.ThriftService;

/**
 * @author zhailzh
 * 
 * @Date 2016年3月8日——上午11:01:33
 * 
 * 我们假定在127.0.0.1 的端口号 8080 有一个服务能够处理客户端的请求
 */
public class ThriftClient {

  private int port = 10111;

  public BooleanResult pushMsgSync() {
    TTransport transport = null;
    int retry = 0;
    int serverNum = 1;
    BooleanResult rt = new BooleanResult();
    while (retry++ < serverNum) {
      try {
        String ip = "localhost";
        transport = new TFramedTransport(new TSocket(ip, port));
        //客户端到服务端的socket的链接
        //thrift 二进制服务
        TProtocol protocol = new TBinaryProtocol(transport);
        //建立socket的链接
        transport.open();
        //        TClient client = new TclientImpl(protocol);

        ThriftService.Client client = new ThriftService.Client(protocol);
        //客户端直接的调用接口
        rt = client.serverState("userId");
        break;
      }
      catch (TException e) {
        e.printStackTrace();
      }
      finally {
        transport.close();
      }
    }
    return rt;
  }

  public static void main(String[] args) {
    ThriftClient client = new ThriftClient();
    client.pushMsgSync();
  }
}
