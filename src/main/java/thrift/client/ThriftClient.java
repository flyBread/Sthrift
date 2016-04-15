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
        TProtocol protocol = new TBinaryProtocol(transport);
        transport.open();
        //        TClient client = new TclientImpl(protocol);

        ThriftService.Client client = new ThriftService.Client(protocol);
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
