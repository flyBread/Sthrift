package com.github.baseExample.fileChannel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author zhailzh
 */
public class ChannelTest {

	public static void testFileChannel() {
		try {
			@SuppressWarnings("resource")
			RandomAccessFile fromFile = new RandomAccessFile("fromFile.txt", "rw");
			// from RandomAccessFile get channel
			FileChannel fromChannel = fromFile.getChannel();
			long count = fromChannel.size();
			System.out.println("fromChannel `s size : " + count);
			ByteBuffer buf = ByteBuffer.allocate(20); // allocate buffer
			// The number of bytes read, possibly zero, or -1 if
			// the channel has reached end-of-stream
			int bytesRead = fromChannel.read(buf);

			StringBuilder builder = new StringBuilder();
			while (bytesRead != -1 /* && builder.length() < 500 */) {
				System.out.println("buf--> position: " + buf.position() + " limit: " + buf.limit() + " capacity: "
						+ buf.capacity());
				//buffer is on write pattern，buffer.position and buffer.limit
				String value = new String(buf.array(), 0, bytesRead, "GBK");
				builder.append(value);
				//change buffer to read pattern，buffer.limit=position,buffer.position=0
				buf.flip();
				System.out.println("buf--> position: " + buf.position() + " limit: " + buf.limit() + " capacity: "
						+ buf.capacity());
				bytesRead = fromChannel.read(buf);
			}

			System.out.println(builder.toString());

			// ByteBuffer write = ByteBuffer.allocate(100);
			String returnvalue = "over!!! \n";
			byte[] vy = returnvalue.getBytes("GBK");
			//from beginning to write
			System.out.println(
					"buf--> position: " + buf.position() + " limit: " + buf.limit() + " capacity: " + buf.capacity());
			System.out.println(buf.limit());
			buf.clear();
			buf.put(vy, 0, vy.length);
			System.out.println(
					"buf--> position: " + buf.position() + " limit: " + buf.limit() + " capacity: " + buf.capacity());

//			buf.flip();
			System.out.println(
					"buf--> position: " + buf.position() + " limit: " + buf.limit() + " capacity: " + buf.capacity());
			fromChannel.write(buf);
			System.out.println(
					"buf--> position: " + buf.position() + " limit: " + buf.limit() + " capacity: " + buf.capacity());
			fromChannel.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
/**
 * 读的时候 fromChannel.read(buf) ，是从position开始，然后慢慢的增加(一直到到达capacity)，停止的位置是limit
 * buf.flip(); 挪动的是position的位置
 * fromChannel.write(buf); 从position的位置开始，一致写入channnel 直到limit的位置。所以写之前需要把position挪动到
 * 开始读的位置，limit挪动到position位置，所以需要调用一下flip()
 * **/
	public static void scattergather() {
		try {
			RandomAccessFile fromFile = new RandomAccessFile("fromFile.txt", "rw");
			FileChannel fromChannel = fromFile.getChannel();
			ByteBuffer buf1 = ByteBuffer.allocate(250); 
			ByteBuffer buf2 = ByteBuffer.allocate(250); 
			ByteBuffer[] bufs = new ByteBuffer[] { buf1, buf2 };
			Long value = fromChannel.read(bufs);

			StringBuilder builder = new StringBuilder();
			while (value != -1 /* && builder.length() < 500 */) {
				System.out.println("buf--> position: " + buf1.position() + " limit: " + buf1.limit() + " capacity: "
						+ buf1.capacity());
				System.out.println("buf--> position: " + buf2.position() + " limit: " + buf2.limit() + " capacity: "
						+ buf2.capacity());
				String value1 = new String(buf1.array(), 0, buf1.position(), "GBK");
				String value2 = new String(buf2.array(), 0, buf2.position(), "GBK");
				builder.append(value1);
				builder.append(value2);
				buf1.flip();
				buf2.flip();
				value = fromChannel.read(bufs);
			}

			System.out.println(builder.toString());

			// ByteBuffer write = ByteBuffer.allocate(100);
			String returnvalue = "over!!! \n";
			byte[] vy = returnvalue.getBytes("GBK");
			buf1.put(vy, 0, vy.length);
			buf2.put(vy, 0, vy.length);
			buf1.flip();
			buf2.flip();

			System.out.println("buf--> position: " + buf1.position() + " limit: " + buf1.limit() + " capacity: "
					+ buf1.capacity());
			System.out.println("buf--> position: " + buf2.position() + " limit: " + buf2.limit() + " capacity: "
					+ buf2.capacity());

			fromChannel.write(bufs);
			fromChannel.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	public static void main(String[] args) {
		 testFileChannel();
		scattergather();
	}

	public static void channelToChannel(String[] args) throws Exception {
		RandomAccessFile fromFile = new RandomAccessFile("fromFile.txt", "rw");
		FileChannel fromChannel = fromFile.getChannel();
		RandomAccessFile toFile = new RandomAccessFile("toFile.txt", "rw");
		FileChannel toChannel = toFile.getChannel();
		long position = 0;
		long count = fromChannel.size();
		long recive = toChannel.transferFrom(fromChannel, position, count);
		System.out.println("toChannel:" + recive);
		long to = fromChannel.transferTo(0, count, toChannel);
		System.out.println("toChannel: " + to);
		ByteBuffer buf = ByteBuffer.allocate(480); 
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
