package thrift.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientProxy implements InvocationHandler {
	  private int port;
	  private Class<?> iface;
	  public static final Logger logger = LoggerFactory.getLogger(ClientProxy.class);

	  @SuppressWarnings("unchecked")
	public static <T> T getProxy(Class<T> iface, int port) {
	    ClientProxy proxy = new ClientProxy();
	    proxy.iface = iface;
	    proxy.port = port;
	    return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[] { iface }, proxy);
	  }

	  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
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
	        Object client = ClientFactory.getClient(this.iface, transport);
	        transport.open();
	        return method.invoke(client, args);
	      }
	      catch (Exception e) {
	        logger.error("invoke " + method.getName() + " on " + ip + ":" + port + " error", e);
	        flag++;
	      }
	      finally {
	        retry++;
	        transport.close();
	      }
	    }
	    logger.error("invoke " + method.getName() + " on all server failed");
	    return null;
	  }

}
