package thrift.server;

import java.lang.reflect.Constructor;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @author zhailzh
 * 
 */
public class RegisterServer implements BeanFactoryAware {

	// private static final Log log = LogFactory.getLog(RegisterServer.class);
	private static final Logger log = LoggerFactory.getLogger(RegisterServer.class);

	private Class<? extends TProcessor> thriftProcessorClass;

	private Class<?> serviceImplClass;

	private BeanFactory beanFactory;

	protected int port;

	// private static SelfUncaughtExceptionHandler su = new
	// SelfUncaughtExceptionHandler();

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	public void start() {
		new Thread() {
			public void run() {
				// this.setUncaughtExceptionHandler(su);
				startServerInternal();
			}
		}.start();
	}

	protected void startServerInternal() {
		try {
			TProcessor process = getProcessor();
			TBinaryProtocol.Factory proFactory = new TBinaryProtocol.Factory();
			TNonblockingServerTransport trans = new TNonblockingServerSocket(port);
			TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(trans);
			args.transportFactory(new TFramedTransport.Factory());
			args.protocolFactory(proFactory);
			args.processor(process);
			args.selectorThreads(16);
			args.workerThreads(32);
			args.selectorThreads(Runtime.getRuntime().availableProcessors() + 1);
			args.acceptQueueSizePerThread(12);
			args.workerThreads(64);

			TServer server = new TThreadedSelectorServer(args);
			log.info("[Server] >>> " + serviceImplClass.getSimpleName().replace("Impl", "") + " is starting on port "
					+ port + " protocal = " + proFactory.getClass());
			server.serve();

		} catch (TTransportException e) {
			log.error("Start server error!", e);
		}
	}

	@SuppressWarnings("unchecked")
	protected TProcessor getProcessor() {
		Constructor<TProcessor> constructor = (Constructor<TProcessor>) thriftProcessorClass.getConstructors()[0];
		Object serviceImpl = beanFactory.getBean(serviceImplClass);
		return BeanUtils.instantiateClass(constructor, serviceImpl);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Class<? extends TProcessor> getThriftProcessorClass() {
		return thriftProcessorClass;
	}

	public void setThriftProcessorClass(Class<? extends TProcessor> thriftProcessorClass) {
		this.thriftProcessorClass = thriftProcessorClass;
	}

	public Class<?> getServiceImplClass() {
		return serviceImplClass;
	}

	public void setServiceImplClass(Class<?> serviceImplClass) {
		this.serviceImplClass = serviceImplClass;
	}

}
