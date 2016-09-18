package thrift.client;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClusterCenter {

	public static final Logger logger = LoggerFactory.getLogger(ClusterCenter.class);
	  private static ClusterCenter instance = null;
	  public List<String> listServer = null;

	  private ClusterCenter() {

	  }

	  public synchronized static ClusterCenter getInstance() {
	    if (instance == null) {
	      instance = new ClusterCenter();
	      instance.init();
	    }
	    return instance;
	  }

	  /**
	   * 获取计数服务节点列表 对节点添加监听
	   * 
	   * @return
	   */
	  public void init() {
	    listServer = new ArrayList<String>();
	    listServer.add("127.0.0.1");
	  }

	  /**
	   * 获取将要连接的ip
	   * 
	   * @param flag
	   * @return
	   */
	  public String getServerIp(long flag) {
	    long num = flag % listServer.size();
	    String ip = listServer.get((int) num);
	    return ip;
	  }

	public int getServiceSize() {
		return listServer.size();
	}
}
