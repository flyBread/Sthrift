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
 * @Date 2016��3��8�ա�������11:01:33
 * 
 * ���Ǽٶ���127.0.0.1 �Ķ˿ں� 8080 ��һ�������ܹ�����ͻ��˵�����
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
        //�ͻ��˵�����˵�socket������
        //thrift �����Ʒ���
        TProtocol protocol = new TBinaryProtocol(transport);
        //����socket������
        transport.open();
        //        TClient client = new TclientImpl(protocol);

        ThriftService.Client client = new ThriftService.Client(protocol);
        //�ͻ���ֱ�ӵĵ��ýӿ�
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
