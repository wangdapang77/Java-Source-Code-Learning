package com.sedion.mynawang.java8.lang;

/**
 * integer源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-07-29 17:02
 */


import java.lang.annotation.Native;

/**
 * 定义：
 * 继承Number、实现Comparable<Integer>接口，类是final修饰的，所以不能被修改不能被继承
 * 可序列化（Number实现可序列化）、在集合里直接使用sort(支持排序)
 */
public class TInteger {


    /**********************************属性***********************************/
    // 最小值为-（2的31次方），表示 int 类型能够表示的最小值 -2147483648
    @Native
    public static final int   MIN_VALUE = 0x80000000;

    // 最大值为（（2的31次方）-1），表示 int 类型能够表示的最大值 2147483647
    @Native
    public static final int   MAX_VALUE = 0x7fffffff;

    // 用来以二进制补码形式表示 int 值的比特位数(占32位)
    @Native
    public static final int SIZE = 32;

    // 用来以二进制补码形式表示 int 值的字节数（占4个字节）。JDK1.8以后才有
    public static final int BYTES = SIZE / Byte.SIZE;


    /**********************************构造器***********************************/



}
