package thrift.server;

import java.util.List;

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

	@Override
	public BoolResult serverStatePara(List<String> userids, long from, int to) throws TException {
		BoolResult res = new BoolResult();
		System.out.println("tuserids ：" + userids.size());
		res.msg = "thrift server is caled.";
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

}
