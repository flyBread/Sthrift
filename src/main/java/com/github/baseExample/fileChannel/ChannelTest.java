package com.github.baseExample.fileChannel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author zhailzh
 * 
 * �����ļ���Ӧ��FileChannel�Ĺ���
 */
public class ChannelTest {

  public static void fileChannel() {
    /*
     * File�� Channel�ģ����channel��ͨ��RandomAccessFile�õ���
     * FileChannelΪһ�������࣬ʵ��ΪFileChannnelImpl �Ĺ��죬һ��Ϊ����ķ������Channel
     * fileChannel ֱ����buffer�С�read��д���ݡ�
     **/
    try {
      @SuppressWarnings("resource")
      RandomAccessFile fromFile = new RandomAccessFile("fromFile.txt", "rw");
      FileChannel fromChannel = fromFile.getChannel();
      long count = fromChannel.size();
      System.out.println("fromChannel��size�Ĵ�С��: " + count);
      ByteBuffer buf = ByteBuffer.allocate(250); //���仺��
      //The number of bytes read, possibly zero, or -1 if 
      //the channel has reached end-of-stream
      int bytesRead = fromChannel.read(buf);

      StringBuilder builder = new StringBuilder();
      while (bytesRead != -1 /*&& builder.length() < 500*/) {
        //bufferдģʽ
        System.out.println("buf--> position: " + buf.position() + " limit: " + buf.limit()
            + " capacity: " + buf.capacity());

        String value = new String(buf.array(), 0, bytesRead, "GBK");
        builder.append(value);
        buf.flip();
        //flip���ã�position��Ϊ0��limit��Ϊԭ����positionλ�� 
        //��Ϊ��ģʽ
        System.out.println("buf--> position: " + buf.position() + " limit: " + buf.limit()
            + " capacity: " + buf.capacity());
        //        buf.clear();//����buf�����ݣ�����Ų��position��limit��λ��
        //        buf.compact();//buf��compact�����Ѿ�д������ݣ�Ȼ��ʹ��û�б����õĿռ䣬ͬʱ��Ϊдģʽ��
        System.out.println("buf--> position: " + buf.position() + " limit: " + buf.limit()
            + " capacity: " + buf.capacity());
        bytesRead = fromChannel.read(buf);
      }

      //���������ܹ������ı��еĶ�ȡ�ĳ���
      System.out.println(builder.toString());

      //      ByteBuffer write = ByteBuffer.allocate(100);
      String returnvalue = "over!!! \n";
      byte[] vy = returnvalue.getBytes("GBK");
      buf.put(vy, 0, vy.length);
      buf.flip();
      System.out.println("buf--> position: " + buf.position() + " limit: " + buf.limit()
          + " capacity: " + buf.capacity());
      fromChannel.write(buf);
      fromChannel.close();

    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void scattergather() {
    try {
      RandomAccessFile fromFile = new RandomAccessFile("fromFile.txt", "rw");
      FileChannel fromChannel = fromFile.getChannel();
      ByteBuffer buf1 = ByteBuffer.allocate(250); //���仺��
      ByteBuffer buf2 = ByteBuffer.allocate(250); //���仺��
      ByteBuffer[] bufs = new ByteBuffer[] { buf1, buf2 };
      /* Scattering Reads���ƶ���һ��bufferǰ������������ǰ��buffer��
       * ��Ҳ��ζ�����������ڶ�̬��Ϣ(����ע����Ϣ��С���̶�)�����仰˵�����������Ϣͷ����Ϣ�壬
       * ��Ϣͷ���������䣨���� 128byte����Scattering Reads��������������
      */
      Long value = fromChannel.read(bufs);

      StringBuilder builder = new StringBuilder();
      while (value != -1 /*&& builder.length() < 500*/) {
        //bufferдģʽ
        System.out.println("buf--> position: " + buf1.position() + " limit: " + buf1.limit()
            + " capacity: " + buf1.capacity());
        System.out.println("buf--> position: " + buf2.position() + " limit: " + buf2.limit()
            + " capacity: " + buf2.capacity());
        String value1 = new String(buf1.array(), 0, buf1.position(), "GBK");
        String value2 = new String(buf2.array(), 0, buf2.position(), "GBK");
        builder.append(value1);
        builder.append(value2);
        buf1.flip();
        buf2.flip();
        value = fromChannel.read(bufs);
      }

      //���������ܹ������ı��еĶ�ȡ�ĳ���
      System.out.println(builder.toString());

      //      ByteBuffer write = ByteBuffer.allocate(100);
      String returnvalue = "over!!! \n";
      byte[] vy = returnvalue.getBytes("GBK");
      buf1.put(vy, 0, vy.length);
      buf2.put(vy, 0, vy.length);
      buf1.flip();
      buf2.flip();

      System.out.println("buf--> position: " + buf1.position() + " limit: " + buf1.limit()
          + " capacity: " + buf1.capacity());
      System.out.println("buf--> position: " + buf2.position() + " limit: " + buf2.limit()
          + " capacity: " + buf2.capacity());

      /* buffers������write()��������Σ�write()�����ᰴ��buffer�������е�˳��
       * ������д�뵽channel��ע��ֻ��position��limit֮������ݲŻᱻд�롣
       * ��ˣ����һ��buffer������Ϊ128byte�����ǽ�������58byte�����ݣ�
       * ��ô��58byte�����ݽ���д�뵽channel�С������Scattering Reads�෴��
       * Gathering Writes�ܽϺõĴ���̬��Ϣ��
      */ fromChannel.write(bufs);
      fromChannel.close();

    }
    catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();

    }
  }

  public static void main(String[] args) {
    //    fileChannel();
    scattergather();
  }

  public static void channelToChannel(String[] args) throws Exception {
    RandomAccessFile fromFile = new RandomAccessFile("fromFile.txt", "rw");
    FileChannel fromChannel = fromFile.getChannel();
    RandomAccessFile toFile = new RandomAccessFile("toFile.txt", "rw");
    FileChannel toChannel = toFile.getChannel();

    /*
     * File�� Channel�ģ����channel��ͨ��RandomAccessFile�õ���
     * FileChannelΪһ�������࣬ʵ��ΪFileChannnelImpl �Ĺ��죬һ��Ϊ����ķ������Channel
     **/
    long position = 0;
    long count = fromChannel.size();
    long recive = toChannel.transferFrom(fromChannel, position, count);
    System.out.println("toChannel:" + recive);
    long to = fromChannel.transferTo(0, count, toChannel);
    System.out.println("toChannel: " + to);
    ByteBuffer buf = ByteBuffer.allocate(480); //���仺��
    int bytesRead = toChannel.read(buf);
    while (bytesRead != -1) {
      System.out.println(buf.position());
      while (buf.hasRemaining()) {
        System.out.print((char) buf.get());
      }
      buf.clear();
      bytesRead = toChannel.read(buf);//
    }
    String value = new String(buf.array(), "GBK");
    System.out.println(value);
  }
}
