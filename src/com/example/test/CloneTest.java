package com.example.test;

import java.util.Arrays;

/**
 * Test and learn object's Clone()
 */
public class CloneTest implements Cloneable {
    int var1;
    String var2;
    Long var3;
    Student var4;

    public CloneTest(int var1, String var2, Long var3, Student var4) {
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
    }

    public static void main(String[] args) {
        testShallowCopyAndDeepCopy();
        testArray();
    }

    /**
     * 将clone()方法注释掉用于测试ShallowCopy，去掉注释测试ShallowCopy
     */
    private static void testShallowCopyAndDeepCopy() {
        Student student = new Student("lucy", "man");
        CloneTest origin = new CloneTest(1, "hello", 9999L, student);
        System.out.println(origin.toString());
        try {
            // 确实克隆一个新的CloneTest对象
            // 但是，因为是浅复制，clone.var4和origin.var4将指向相同对象。
            // 因此，修改 clone.var4.sex值将影响origin对象
            CloneTest clone = (CloneTest) origin.clone();
            clone.var4.sex = "women";
            System.out.println(clone.toString());
            System.out.println(origin.toString());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    private static void testArray() {
        // 数组支持克隆
        int[] arr = new int[10];
        Arrays.fill(arr, 5);
        System.out.println(arr.toString());
        int[] cloneArr = arr.clone();
        System.out.println(cloneArr.toString());
    }

    @Override
    public String toString() {
        return "CloneTest{" +
                "var1=" + var1 +
                ", var2='" + var2 + '\'' +
                ", var3=" + var3 +
                ", var4=" + var4 +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        // super.clone()方法返回克隆的新的CloneTest对象
        CloneTest clone = (CloneTest) super.clone();
        // 如果我们希望克隆后的对象和旧对象完全无关，将引用可变对象的字段，使用可变对象的克隆对象代替原对象。
        // 对于基本类型和引用不可变对象的字段，不用做任何处理
        clone.var4 = (Student) clone.var4.clone();
        return clone;
    }

    public static class Student implements Cloneable {
        String name;
        String sex;

        public Student(String name, String sex) {
            this.name = name;
            this.sex = sex;
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public String toString() {
            return "Student{" +
                    "name='" + name + '\'' +
                    ", sex='" + sex + '\'' +
                    '}';
        }
    }
}
