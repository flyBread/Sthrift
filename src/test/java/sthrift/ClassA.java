package sthrift;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhailzh
 * 
 * @Date 2016年3月10日——下午9:15:46
 * 
 */
public class ClassA {

  private int value = 0;

  private ClassB stu;

  public ClassA(int i, ClassB j) {
    this.value = i;
    this.setStu(j);
  }

  public ClassA(int i) {
    this.value = i;
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return this.value + ".";
  }

  public static void change(ClassA b) {
    //    b = new ClassA(2);
    b.stu = new ClassB(200);
    //    return b;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public static void main(String[] args) {

    //main方法的栈帧中保存变量a 以及ClassA(1)对象的索引值
    ClassB aa = new ClassB(100);
    ClassA a = new ClassA(1, aa);
    System.out.println(a.hashCode());
    ClassA b = a;
    System.out.println(b.hashCode());
    change(b);
    System.out.println(b.hashCode());
    System.out.println(a);
    System.out.println(b);

    List<String> value = new ArrayList<>();
    value.add("1");
    value.add("tyui");
    changetValue(value);
    System.out.println(value);

    ClassA aaa = new ClassA(200, new ClassB(300));
    System.out.println(aaa);
    changet(aaa);
    System.out.println(aaa);
  }

  private static void changet(ClassA aaa) {
    //    aaa.stu = new ClassB(0);
    //    aaa.value = 8;
    aaa = new ClassA(0);
    aaa.value = 8;
  }

  private static void changetValue(List<String> value2) {
    value2.remove(0);
  }

  public ClassB getStu() {
    return stu;
  }

  public void setStu(ClassB stu) {
    this.stu = stu;
  }
}

class ClassB {
  private int value;

  public ClassB(int i) {
    this.setValue(i);
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }
}