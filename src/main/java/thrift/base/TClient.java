package thrift.base;

import thrift.BoolResult;

/**
 * @author zhailzh
 * 
 */
public interface TClient {

  public BoolResult serverState(String userid) throws org.apache.thrift.TException;

}
