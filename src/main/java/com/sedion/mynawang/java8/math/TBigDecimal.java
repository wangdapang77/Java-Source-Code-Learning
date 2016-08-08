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
    // BigDecimal的标度的值
    private final int scale;
    //  BigDecimal的精度
    private transient int precision;
    // 用于存储规范化的字符串表示
    private transient String stringCache;

    private final transient long intCompact;


    /*
    BigDecimal test11 = new BigDecimal(-1111111.02);
    System.out.println(test11);
    // 小数后的位数
    System.out.println(test11.scale());
    // 非标度数值，没有小数时的数值
    System.out.println(test11.unscaledValue());

    test11 = unscaledValue * 10 (-scale次方)
   */

    // 值为0，标度为0
    BigDecimal ZERO = BigDecimal.ZERO;
    // 值为1，标度为0
    BigDecimal ONE = BigDecimal.ONE;
    // 值为10，标度为0
    BigDecimal TEN = BigDecimal.TEN;


    // 始终对非零舍弃部分前面的数字加1【向前加一舍去】
    private static final int ROUND_UP = BigDecimal.ROUND_UP;
    // 从不对舍弃部分前面的数字加1【截取数字】
    private static final int ROUND_DOWN = BigDecimal.ROUND_DOWN;
    // 大于0【向前加一舍去】，小于0【截取数字】
    private static final int ROUND_CEILING = BigDecimal.ROUND_CEILING;
    // 小于0【向前加一舍去】，大于0【截取数字】
    private static final int ROUND_FLOOR = BigDecimal.ROUND_FLOOR;
    // 如果舍弃部分 >= 0.5，【向前加一舍去】;否则【截取数字】 【四舍五入】
    private static final int ROUND_HALF_UP = BigDecimal.ROUND_HALF_UP;
    // 如果舍弃部分 > 0.5，【向前加一舍去】;否则【截取数字】   【五舍六入】
    private static final int ROUND_HALF_DOWN = BigDecimal.ROUND_HALF_DOWN;
    // 如果舍弃部分左边的数字为奇数，则【四舍五入】;如果为偶数，则【五舍六入】   【银行家舍入法】
    private static final int ROUND_HALF_EVEN = BigDecimal.ROUND_HALF_EVEN;
    // 不需要舍入，确认结果为精确值，否则抛出ArithmeticException
    private static final int ROUND_UNNECESSARY = BigDecimal.ROUND_UNNECESSARY;


    public TBigDecimal(BigInteger intVal, int scale, long intCompact) {
        this.intVal = intVal;
        this.scale = scale;
        this.intCompact = intCompact;
    }

    /**********************************构造器***********************************/

    public static void getConstructor() {
        BigInteger initBigIntVal = BigInteger.valueOf(100);
        String initStrVal = "100.01";
        double initDouVal = 100.01;
        int initIntVal = 100;
        long initLongVal = 100L;


        // BigInteger转BigDecimal
        BigDecimal testBig1 = new BigDecimal(initBigIntVal);
        System.out.println("testBig1:" + testBig1);
        BigDecimal testBig2 = new BigDecimal(initBigIntVal, 2);
        System.out.println("testBig2:" + testBig2);
        BigDecimal testBig3 = new BigDecimal(initBigIntVal, 2, MathContext.UNLIMITED);
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

        // String转BigDecimal【推荐】
        BigDecimal testBig13 = new BigDecimal(initStrVal);
        System.out.println("testBig13:" + testBig13);
        BigDecimal testBig14 = new BigDecimal(initStrVal.toCharArray(), MathContext.UNLIMITED);
        System.out.println("testBig14:" + testBig14);

        System.out.println("\n");

    }

    /**********************************常用方法***********************************/
    public  static void getMethod() {

        BigDecimal test1 = new BigDecimal("100.00");
        // 获取绝对值
        BigDecimal testAbs = test1.abs();
        System.out.println("testAbs: " + testAbs);

        BigDecimal test2 = new BigDecimal("10.12");
        // 两数相加
        BigDecimal testAdd = test1.add(test2);
        System.out.println("testAdd: " + testAdd);

        // 两数相减
        BigDecimal testSubtract = test1.subtract(test2);
        System.out.println("testSubtract: " + testSubtract);

        // 两数相乘（结果的scale为两数scale相加之和）
        BigDecimal testMultiply = test1.multiply(test2);
        System.out.println("testMultiply: " + testMultiply);

        // 两数相除(默认小数点保留位数为 ROUND_UNNECESSARY)
        BigDecimal testDivide = test1.divide(test2, BigDecimal.ROUND_HALF_UP);
        System.out.println("testDivide: " + testDivide);

        // 返回最大值|还有min最小值
        BigDecimal testMax = test1.max(test2);
        System.out.println("testMax: " + testMax);

        // 小数点往左移位|还有往右移
        BigDecimal testMovePointLeft = test1.movePointLeft(1);
        System.out.println("testMovePointLeft: " + testMovePointLeft);

        // 返回相反数
        BigDecimal testNegate = test1.negate();
        System.out.println("testNegate: " + testNegate);

    }


    public static void main(String[] args) {
        getConstructor();
        getMethod();
    }

}
