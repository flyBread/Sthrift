package thrift.server;

import org.apache.thrift.TException;

import thrift.BoolResult;

/**
 * @author zhailzh
 * 
 */
public class ThriftServiceImpl implements thrift.ThriftService.Iface {

  

@Override
public BoolResult serverState(String userid, long from, int to) throws TException {
	    BoolResult res = new BoolResult();
	    System.out.println("thrift service is called .......");
	    res.msg = "thrift server is caled.";
	    return res;
}

}
