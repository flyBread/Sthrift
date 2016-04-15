package thrift.client;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import thrift.BoolResult;
import thrift.ThriftService;

/**
 * @author zhailzh
 * 
 */
public class ThriftClient {

  private int port = 10111;

  public BoolResult pushMsgSync() {
    TTransport transport = null;
    int retry = 0;
    int serverNum = 1;
    BoolResult rt = new BoolResult();
    while (retry++ < serverNum) {
      try {
        String ip = "localhost";
        transport = new TFramedTransport(new TSocket(ip, port));
        TProtocol protocol = new TBinaryProtocol(transport);
        transport.open();
        ThriftService.Client client = new ThriftService.Client(protocol);
        rt = client.serverState("userId",10914l,234);
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
