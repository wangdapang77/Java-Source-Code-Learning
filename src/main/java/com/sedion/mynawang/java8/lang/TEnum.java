package com.sedion.mynawang.java8.lang;

/**
 * enum源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-07-30 22:51
 */


/**
 * 定义：
 * 所有java枚举类型都继承自该抽象类。enum声明枚举类型，不可以通过显式继承（entends）该抽象类的方式声明
 * 可序列化（实现可序列化）、在集合里直接使用sort(支持排序)
 */
public class TEnum{

    /**********************************属性***********************************/
    // 枚举常量的名称 一般使用toString获取名称
    private final String name;
    // 返回枚举常量的序数,在枚举声明中的位置，初始值为0
    private final int ordinal;

    /**********************************构造器***********************************/
    public TEnum(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }

    /**********************************常用方法***********************************/
    enum programmer {
        ARCHITECT, PROJECTMANAGER, TESTENGINEER
    }

    public  static void getMethod() {
        // 获取名称
        System.out.println(programmer.ARCHITECT.name());

        // 获取在枚举中的序列顺序
        System.out.println(programmer.ARCHITECT.ordinal());
        System.out.println(programmer.PROJECTMANAGER.ordinal());

        // 获取名称
        System.out.println(programmer.ARCHITECT.toString());

        // 前者序列减去后者序列
        System.out.println(programmer.ARCHITECT.compareTo(programmer.PROJECTMANAGER));

        // 获取hash值
        System.out.println(programmer.ARCHITECT.hashCode());

        // 获取与此枚举常量的枚举类型相对应的 Class 对象
        System.out.println(programmer.ARCHITECT.getDeclaringClass());

    }

    public static void main(String[] args) {
        getMethod();
    }

}
