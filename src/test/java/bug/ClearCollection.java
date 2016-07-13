package bug;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhailz
 *
 *         时间：2016年5月29日 ### 上午11:45:37
 */
public class ClearCollection {
	public static void main(String[] args) {
		// 巨量的toids，需要1000一次，分批量的处理
		List<String> toidsm = new ArrayList<>(10000);
		List<String> toids = new ArrayList<>();
		for (int i = 0; i < toidsm.size(); i++) {
			if(toids.size() < 1000){
				toids.add(toidsm.get(i));
			}else{
				Batch bach = new Batch(toids);
				ThreadClass thread = new ThreadClass(bach);
				thread.start();
				toids.clear();
			}
		}
	}
}

class ThreadClass extends Thread {
	Batch bach;
	public ThreadClass(Batch bach) {
		this.bach = bach;
	}

	@Override
	public void run() {
		System.out.println("处理了："+this.bach.getToids());
	}
}

class Batch {
	private List<String> toids;

	public Batch(List<String> toids2) {
		this.toids = toids2;
	}

	public List<String> getToids() {
		return toids;
	}

	public void setToids(List<String> toids) {
		this.toids = toids;
	}
}