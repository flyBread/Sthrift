package thrift.server;

import org.apache.thrift.TException;

import thrift.BooleanResult;

/**
 * @author zhailzh
 * 
 * @Date 2016年3月8日——下午2:10:00
 * 
 */
public class ThriftServiceImpl implements thrift.ThriftService.Iface {

  @Override
  public BooleanResult serverState(String userid) throws TException {
    BooleanResult res = new BooleanResult();
    System.out.println("thrift service 后台服务.......");
    res.err_msg = "请求失败";
    return res;
  }

}
