package com.github.baseExample;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * @author zhailzh
 * 
 * @Date 2016年1月26日——上午9:45:00
 * 
 */
public class PipleExample {

	public static void main(String[] args) throws IOException {
		final PipedOutputStream pipedout = new PipedOutputStream();
		final PipedInputStream pipedint = new PipedInputStream(pipedout);

		Thread thread1 = new Thread() {
			public void run() {
				for (int i = 0; i < 1; i++) {
					try {
						pipedout.write("hello      ，88%%￥%*E^E^^E \n".getBytes("UTF-8"));
						Thread.sleep(1000);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		};

		Thread thread2 = new Thread() {
			public void run() {
				try {
					StringBuilder buil = new StringBuilder();
					byte[] value = new byte[10];
					int length = pipedint.read(value);
					while ( length == 10) {
						buil.append(new String(value,"UTF-8"));
						value = new byte[10];
						length = pipedint.read(value);
					}
					buil.append(new String(value,0,length));
					System.out.println(buil.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		};

		thread1.start();
		thread2.start();

	}
}
