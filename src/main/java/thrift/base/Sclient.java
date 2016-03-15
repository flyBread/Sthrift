package thrift.base;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;

/**
 * @author zhailzh
 * 
 * @Date 2016年3月10日——下午4:33:43
 * 
 * 主要的工作是封装socket链接的客户端，地位相当于thrift中的
 */
public class Sclient {

  // 客户端和服务端的双方按照协定的协议通信
  public Sclient(TProtocol prot) {
    this(prot, prot);
  }

  public Sclient(TProtocol iprot, TProtocol oprot) {
    iprot_ = iprot;
    oprot_ = oprot;
  }

  protected TProtocol iprot_;
  protected TProtocol oprot_;

  protected int seqid_;

  /**
   * Get the TProtocol being used as the input (read) protocol.
   * @return the TProtocol being used as the input (read) protocol.
   */
  public TProtocol getInputProtocol() {
    return this.iprot_;
  }

  /**
   * Get the TProtocol being used as the output (write) protocol.
   * @return the TProtocol being used as the output (write) protocol.
   */
  public TProtocol getOutputProtocol() {
    return this.oprot_;
  }

  protected void sendBase(String methodName, TBase args) throws TException {
    oprot_.writeMessageBegin(new TMessage(methodName, TMessageType.CALL, ++seqid_));
    args.write(oprot_);
    oprot_.writeMessageEnd();
    oprot_.getTransport().flush();
  }

  protected void receiveBase(TBase result, String methodName) throws TException {
    TMessage msg = iprot_.readMessageBegin();
    if (msg.type == TMessageType.EXCEPTION) {
      TApplicationException x = TApplicationException.read(iprot_);
      iprot_.readMessageEnd();
      throw x;
    }
    if (msg.seqid != seqid_) {
      throw new TApplicationException(TApplicationException.BAD_SEQUENCE_ID, methodName
          + " failed: out of sequence response");
    }
    result.read(iprot_);
    iprot_.readMessageEnd();
  }
}
