package com.sedion.mynawang.java8.lang;

/**
 * String源码阅读（JDK1.8）
 * @auther mynawang
 * @create 2016-07-27 23:44
 */


import java.io.ObjectStreamField;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * 定义：
 * 实现java.io.Serializable、Comparable<String>、CharSequence接口，类是final修饰的，所以不能被修改不能被继承
 * 可序列化、在集合里直接使用sort(排序)、本质上是一个字符序列
 */
public class TString {

    /**********************************属性***********************************/

    // 字符数组，用于存储字符串内容
    private final char value[];

    // 缓存字符串的hash值，默认为0
    private int hash;

    // 申明序列化的UID
    private static final long serialVersionUID = -6849794470754667710L;

    // 实现了Serializable接口，支持序列化和反序列化
    /*(Java序列化机制是通过在运行时判断类的serialVersionUID来验证版本的一致性。
    在进行反序列化时JVM会把传来的字节的serialVersionUID与本地相应实体类的serialVersionUID进行比较，
    如果相同就认为是一致的，可以进行序列化，否则就会出现序列化版本不一致的异常报InvalidCastException)*/
    private static final ObjectStreamField[] serialPersistentFields =
            new ObjectStreamField[0];

    public TString(char[] value) {
        this.value = value;
    }


    public static void main(String[] args) {

        /**********************************构造方法***********************************/
        String testStr0 = "mynawang";

        String testStr1 = new String("mynawang");

        char test2[] = {'m','y','n','a','w','a','n','g'};
        String testStr2 = new String(test2);

        char test3[] = {'a','m','y','n','a','w','a','n','g'};
        // char数组、起始索引、截取长度
        String testStr3 = new String(test3, 1, 8);

        int[] test4 = {65, 66, 109, 121, 110, 97, 119, 97, 110, 103};
        // 字母对应的ASCII编码数组、起始索引、截取长度
        String testStr4 = new String(test4, 2, 8);

        byte test5[] = {'a','m','y','n','a','w','a','n','g'};
        try {
            // byte数组、起始索引、截取长度、解析字符类型
            String testStr5 = new String(test5, 1, 8, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte test6[] = {'a','m','y','n','a','w','a','n','g'};
        // byte数组、起始索引、截取长度、字符集
        String testStr6 = new String(test6, 1, 8, Charset.defaultCharset());

        byte test7[] = {'m','y','n','a','w','a','n','g'};
        // byte数组、解析字符类型
        try {
            String testStr7 = new String(test7,"GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte test8[] = {'m','y','n','a','w','a','n','g'};
        // byte数组、字符集
        String testStr8 = new String(test8, Charset.defaultCharset());

        byte test9[] = {'a','m','y','n','a','w','a','n','g'};
        // byte数组、起始索引、截取长度
        String testStr9 = new String(test9, 1, 8);

        byte test10[] = {'m','y','n','a','w','a','n','g'};
        // byte数组
        String testStr10 = new String(test10);

        StringBuffer stringBuffer11 = new StringBuffer("mynawang");
        // StringBuffer
        String testStr11 = new String(stringBuffer11);

        StringBuilder stringBuilder12 = new StringBuilder("mynawang");
        // StringBuilder
        String testStr12 = new String(stringBuilder12);
        System.out.println(testStr12);






    }


}
