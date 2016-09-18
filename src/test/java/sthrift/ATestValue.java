package sthrift;

import java.util.HashMap;

/**
 * @author zhailz
 *
 * 时间：2016年5月27日 ### 下午6:08:01
 */
public class ATestValue {
	public static void main(String[] args) {
		HashMap<String, String> value = new HashMap<String, String>(7);
		value.put("a", "b");
		value.put("a5", "b");
		System.out.println(value);
	}
}
