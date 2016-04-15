package thrift.base;

import thrift.BooleanResult;

/**
 * @author zhailzh
 * 
 */
public interface TClient {

  public BooleanResult serverState(String userid) throws org.apache.thrift.TException;

}
