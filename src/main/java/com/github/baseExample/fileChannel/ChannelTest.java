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
 * 测试文件对应的FileChannel的功能
 */
public class ChannelTest {

  public static void fileChannel() {
    /*
     * File的 Channel的，这个channel是通过RandomAccessFile得到的
     * FileChannel为一个抽象类，实现为FileChannnelImpl 的构造，一般为上面的方法获得Channel
     * fileChannel 直接向buffer中”read“写数据。
     **/
    try {
      @SuppressWarnings("resource")
      RandomAccessFile fromFile = new RandomAccessFile("fromFile.txt", "rw");
      FileChannel fromChannel = fromFile.getChannel();
      long count = fromChannel.size();
      System.out.println("fromChannel的size的大小是: " + count);
      ByteBuffer buf = ByteBuffer.allocate(250); //分配缓存
      //The number of bytes read, possibly zero, or -1 if 
      //the channel has reached end-of-stream
      int bytesRead = fromChannel.read(buf);

      StringBuilder builder = new StringBuilder();
      while (bytesRead != -1 /*&& builder.length() < 500*/) {
        //buffer写模式
        System.out.println("buf--> position: " + buf.position() + " limit: " + buf.limit()
            + " capacity: " + buf.capacity());

        String value = new String(buf.array(), 0, bytesRead, "GBK");
        builder.append(value);
        buf.flip();
        //flip调用，position变为0，limit变为原来的position位置 
        //变为读模式
        System.out.println("buf--> position: " + buf.position() + " limit: " + buf.limit()
            + " capacity: " + buf.capacity());
        //        buf.clear();//清理buf的数据，并不挪动position，limit的位置
        //        buf.compact();//buf的compact跳过已经写入的数据，然后使用没有被利用的空间，同时变为写模式。
        System.out.println("buf--> position: " + buf.position() + " limit: " + buf.limit()
            + " capacity: " + buf.capacity());
        bytesRead = fromChannel.read(buf);
      }

      //这样我们能够控制文本中的读取的长度
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
      ByteBuffer buf1 = ByteBuffer.allocate(250); //分配缓存
      ByteBuffer buf2 = ByteBuffer.allocate(250); //分配缓存
      ByteBuffer[] bufs = new ByteBuffer[] { buf1, buf2 };
      /* Scattering Reads在移动下一个buffer前，必须填满当前的buffer，
       * 这也意味着它不适用于动态消息(译者注：消息大小不固定)。换句话说，如果存在消息头和消息体，
       * 消息头必须完成填充（例如 128byte），Scattering Reads才能正常工作。
      */
      Long value = fromChannel.read(bufs);

      StringBuilder builder = new StringBuilder();
      while (value != -1 /*&& builder.length() < 500*/) {
        //buffer写模式
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

      //这样我们能够控制文本中的读取的长度
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

      /* buffers数组是write()方法的入参，write()方法会按照buffer在数组中的顺序，
       * 将数据写入到channel，注意只有position和limit之间的数据才会被写入。
       * 因此，如果一个buffer的容量为128byte，但是仅仅包含58byte的数据，
       * 那么这58byte的数据将被写入到channel中。因此与Scattering Reads相反，
       * Gathering Writes能较好的处理动态消息。
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
     * File的 Channel的，这个channel是通过RandomAccessFile得到的
     * FileChannel为一个抽象类，实现为FileChannnelImpl 的构造，一般为上面的方法获得Channel
     **/
    long position = 0;
    long count = fromChannel.size();
    long recive = toChannel.transferFrom(fromChannel, position, count);
    System.out.println("toChannel:" + recive);
    long to = fromChannel.transferTo(0, count, toChannel);
    System.out.println("toChannel: " + to);
    ByteBuffer buf = ByteBuffer.allocate(480); //分配缓存
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
