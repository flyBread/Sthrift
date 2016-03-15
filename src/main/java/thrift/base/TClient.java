package thrift.base;

import thrift.BooleanResult;

/**
 * @author zhailzh
 * 
 * @Date 2016年3月10日——下午2:39:18
 * 客户端的标志的，必须继承的类
 */
public interface TClient {

  public BooleanResult serverState(String userid) throws org.apache.thrift.TException;

}
