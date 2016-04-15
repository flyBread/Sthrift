package thrift.client;

import org.apache.thrift.protocol.TProtocol;

import thrift.BooleanResult;
import thrift.ThriftService.serverState_args;
import thrift.base.TClient;

/**
 * @author zhailzh
 * 
 * 
 */
public class TclientImpl extends TclientAbstract implements TClient {

  public TclientImpl(TProtocol prot) {
    super(prot);
  }

  public BooleanResult serverState(String userid) throws org.apache.thrift.TException {
    send_serverState(userid);
    return recv_serverState();
  }

  public void send_serverState(String userid) throws org.apache.thrift.TException {
    serverState_args args = new serverState_args();
    args.setUserid(userid);
    sendBase("serverState", args);
  }

  private BooleanResult recv_serverState() {
    // TODO Auto-generated method stub
    return null;
  }
}
