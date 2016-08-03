package com.sedion.mynawang.java8.math;

/**
 * BigDecimal源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-08-02 17:32
 */


import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

/**
 * 定义：
 * 继承Number类，实现Comparable<BigDecimal>接口
 * 可序列化（实现可序列化）、在集合里直接使用sort(支持排序)
 */
public class TBigDecimal {

    /**********************************属性***********************************/


    // BigDecimal的非标度值
    private final BigInteger intVal;
    // BigDecimal的标度值
    private final int scale;
    //  BigDecimal的精度
    private transient int precision;
    // 用于存储规范化的字符串表示
    private transient String stringCache;

    private final transient long intCompact;

    public TBigDecimal(BigInteger intVal, int scale, long intCompact) {
        this.intVal = intVal;
        this.scale = scale;
        this.intCompact = intCompact;
    }

    /**********************************构造器***********************************/

    public static void getConstructor() {
        BigInteger initBigIntVal = BigInteger.valueOf(100);
        String initStrVal = "100";
        double initDouVal = 100.01;
        int initIntVal = 100;
        long initLongVal = 100L;


        // BigInteger转BigDecimal
        BigDecimal testBig1 = new BigDecimal(initBigIntVal);
        System.out.println("testBig1:" + testBig1);
        BigDecimal testBig2 = new BigDecimal(initBigIntVal, 0);
        System.out.println("testBig2:" + testBig2);
        BigDecimal testBig3 = new BigDecimal(initBigIntVal, 0, MathContext.UNLIMITED);
        System.out.println("testBig3:" + testBig3);
        BigDecimal testBig4 = new BigDecimal(initBigIntVal, MathContext.UNLIMITED);
        System.out.println("testBig4:" + testBig4);

        // char数值转BigDecimal
        BigDecimal testBig5 = new BigDecimal(initStrVal.toCharArray());
        System.out.println("testBig5:" + testBig5);
        BigDecimal testBig6 = new BigDecimal(initStrVal.toCharArray(), 0, initStrVal.length());
        System.out.println("testBig6:" + testBig6);
        BigDecimal testBig7 = new BigDecimal(initStrVal.toCharArray(), MathContext.UNLIMITED);
        System.out.println("testBig7:" + testBig7);
        BigDecimal testBig8 = new BigDecimal(initStrVal.toCharArray(), 0, initStrVal.length(), MathContext.UNLIMITED);
        System.out.println("testBig8:" + testBig8);

        // double转BigDecimal
        BigDecimal testBig9 = new BigDecimal(initDouVal);
        System.out.println("testBig9:" + testBig9);
        BigDecimal testBig10 = new BigDecimal(initDouVal, MathContext.UNLIMITED);
        System.out.println("testBig10:" + testBig10);

        // long转BigDecimal
        BigDecimal testBig11 = new BigDecimal(initLongVal);
        System.out.println("testBig11:" + testBig11);
        BigDecimal testBig12 = new BigDecimal(initLongVal, MathContext.UNLIMITED);
        System.out.println("testBig12:" + testBig12);

        // String转BigDecimal
        BigDecimal testBig13 = new BigDecimal(initStrVal);
        System.out.println("testBig13:" + testBig13);
        BigDecimal testBig14 = new BigDecimal(initStrVal.toCharArray(), MathContext.UNLIMITED);
        System.out.println("testBig14:" + testBig14);

    }

    /**********************************常用方法***********************************/


    public static void main(String[] args) {
        getConstructor();
    }

}
