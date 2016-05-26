package thrift.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;

import thrift.BoolResult;
import thrift.ThriftService;

/**
 * @author zhailzh
 * 
 */
public class ThriftClient {

	private static int port = 10111;

	public static ThriftService.Iface getClient() {
		return ClientProxy.getProxy(ThriftService.Iface.class, port);
	}

	public BoolResult serverStatePara(List<String> userids, long from, int to) throws org.apache.thrift.TException {
		return getClient().serverStatePara(userids, from, to);
	}

	public static void main(String[] args) throws TException {
		final ThriftClient client = new ThriftClient();
		final List<String> userids = new ArrayList<>();
		for (int i = 0; i < 10000; i++) {
			userids.add("value大大大滴滴答答"+i);
		}
		final long from = 1l;
		final int to = 0;
		for (int i = 0; i < 2000; i++) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						client.serverStatePara(userids, from, to);
					} catch (TException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
		
	}
}
