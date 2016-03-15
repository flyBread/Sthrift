package thrift.client;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocol;

import thrift.ThriftService.serverState_args;

/**
 * @author zhailzh
 * 
 * @Date 2016��3��10�ա�������2:41:47
 * 
 */
public class TclientAbstract extends TServiceClient {

  public TclientAbstract(TProtocol prot) {
    super(prot);
  }

  public void send_serverState(String userid) throws org.apache.thrift.TException {
    serverState_args args = new serverState_args();
    args.setUserid(userid);
    sendBase("serverState", args);
  }

}
