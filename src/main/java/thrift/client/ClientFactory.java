package thrift.client;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thrift.ThriftService;

public class ClientFactory {

  public static final Logger logger = LoggerFactory.getLogger(ClientFactory.class);

  public static Object getClient(Class<?> clazz, TTransport transport) {
    TProtocol protocol = new TBinaryProtocol(transport);
    if (clazz == ThriftService.Iface.class) {
      return new ThriftService.Client(protocol);
    } else {
      return null;
    }
  }
}