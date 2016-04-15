package thrift.server;

import org.apache.thrift.TException;

import thrift.BooleanResult;

/**
 * @author zhailzh
 * 
 */
public class ThriftServiceImpl implements thrift.ThriftService.Iface {

  @Override
  public BooleanResult serverState(String userid) throws TException {
    BooleanResult res = new BooleanResult();
    System.out.println("thrift service is called .......");
    res.err_msg = "thrift server is caled.";
    return res;
  }

}
