package thrift.base;

import thrift.BooleanResult;

/**
 * @author zhailzh
 * 
 * @Date 2016��3��10�ա�������2:39:18
 * �ͻ��˵ı�־�ģ�����̳е���
 */
public interface TClient {

  public BooleanResult serverState(String userid) throws org.apache.thrift.TException;

}
