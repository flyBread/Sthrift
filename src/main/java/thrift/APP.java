package thrift;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zhailz
 *
 * 时间：2016年4月24日 ### 上午9:24:57
 */
/**
 * @author zhailz
 *
 * 时间：2016年5月27日 ### 下午3:34:19
 */
public class APP {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("classpath:thrift-server.xml");
	}
}
