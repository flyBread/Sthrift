package thrift.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;

import thrift.BoolResult;
import thrift.ThriftService;

/**
 * @author zhailzh
 * 
 */
public class ThriftClient {

	private static int port = 10111;

	private static Logger logger = org.slf4j.LoggerFactory.getLogger(ThriftClient.class);

	public static ThriftService.Iface getClient() {
		return ClientProxy.getProxy(ThriftService.Iface.class, port);
	}

	public BoolResult serverStatePara(List<String> userids, long from, int to) throws org.apache.thrift.TException {
		return getClient().methodPara(userids, from, to);
	}

	public BoolResult serverState(String userid, long from, int to) throws org.apache.thrift.TException {
		BoolResult rt = null;
		TTransport transport = null;
		int retry = 0;
		String ip = null;
		long flag = System.currentTimeMillis();
		int serverNum = ClusterCenter.getInstance().getServiceSize();
		if (serverNum <= 0) {
			logger.error("no service started");
			return null;
		}
		while (retry < serverNum) {
			try {
				ip = ClusterCenter.getInstance().getServerIp(flag);
				TSocket tSocket = new TSocket(ip, port);
				tSocket.setTimeout(1000);
				transport = new TFramedTransport(tSocket);
				transport.open();
				TProtocol protocol = new TJSONProtocol(transport);
				ThriftService.Client client = new ThriftService.Client(protocol);
				rt = client.method(userid, from, to);
				return rt;
			} catch (Exception e) {
				logger.error("invoke serverState on " + ip + ":" + port + " error", e);
				flag++;
			} finally {
				retry++;
				transport.close();
			}
		}
		logger.error("invoke serverState  on all server failed");
		return null;
	}

	/**
	 * @param args
	 * @throws TException
	 */
	public static void main(String[] args) throws TException {
		final ThriftClient client = new ThriftClient();
		final List<String> userids = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			userids.add("value大大大滴滴答答" + i);
		}
		final long from = 1l;
		final int to = 0;
		logger.info("client:{}",userids);
		
		for (int i = 0; i < 2; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						logger.info("client:{}",userids);
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
