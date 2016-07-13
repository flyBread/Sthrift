

/**
 * @author zhailz
 *
 * 时间：2016年5月29日 ### 下午5:07:10
 */
public class ChangeValue {

	private String value = "a";
	public  void change(ChangeValue value){
		value.value = "b";
	}
	public  void change(String value){
		value = "b";
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
	
	public static void main(String[] args) {
		ChangeValue value = new ChangeValue();
		value.change(value.value);
		System.out.println(value.value);
		value.change(value);
		System.out.println(value.value);
	}
}
