package thrift.server;

import org.apache.thrift.TException;

import thrift.BooleanResult;

/**
 * @author zhailzh
 * 
 * @Date 2016��3��8�ա�������2:10:00
 * 
 */
public class ThriftServiceImpl implements thrift.ThriftService.Iface {

  @Override
  public BooleanResult serverState(String userid) throws TException {
    BooleanResult res = new BooleanResult();
    System.out.println("thrift service ��̨����.......");
    res.err_msg = "����ʧ��";
    return res;
  }

}
