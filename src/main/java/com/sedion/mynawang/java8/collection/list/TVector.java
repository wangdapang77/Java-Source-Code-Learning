package com.sedion.mynawang.java8.collection.list;

/**
 * Vector源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-08-15 9:25
 */

import java.util.*;

/**
 * 定义：
 *  Vector支持泛型，继承AbstractList，而AbstractList实现了List接口，提供List接口的默认实现
 *  ArrayList自身实现了List接口。
 *  ArrayList实现RandomAccess接口，支持快速随机访问，实际上是通过下标进行快速访问，
 *  RandomAccess是一个标记接口，接口内没有定义任何内容，实现Cloneable接口能被克隆。
 *  ArrayList实现了Cloneable接口，可以被克隆。
 *  ArrayList实现了Serializable接口，支持序列化，能够通过序列化传输。
 */
public class TVector<E> //extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable
{
    /**********************************属性***********************************/
    // 保存数据的数组
    protected Object[] elementData;

    // 时间数据的数量（可以理解为是elementData的length）
    protected int elementCount;

    // 容量增长系数(element容量不足时自动扩容capacityIncrement的大小，如果capacityIncrement没设定，则扩容为1倍)
    protected int capacityIncrement;

    // 序列化版本号
    private static final long serialVersionUID = -2767605614048989439L;

    /**********************************构造器***********************************/

    // 指定初始容量和容量增长系数
    public TVector(int initialCapacity, int capacityIncrement) {
        super();
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initialCapacity);
        this.elementData = new Object[initialCapacity];
        this.capacityIncrement = capacityIncrement;
    }

    // 由此看出默认capacityIncrement为0，即默认增长1倍
    public TVector(int initialCapacity) {
        this(initialCapacity, 0);
    }

    // 由此看出默认initialCapacity为10.即默认初始容量为10
    public TVector() {
        this(10);
    }

    // 将Collection转为Vector
    public TVector(Collection<? extends E> c) {
        elementData = c.toArray();
        elementCount = elementData.length;
        // c.toArray might (incorrectly) not return Object[] (see 6260652)
        if (elementData.getClass() != Object[].class)
            elementData = Arrays.copyOf(elementData, elementCount, Object[].class);
    }

    public static void getConstructor() {
        Vector<String> testVector = new Vector<>();
        Vector<String> testVector1 = new Vector<>(20);
        Vector<String> testVector2 = new Vector<>(30,15);
        Collection<String> testCollection = new ArrayList<>();
        Vector<String> testVector3 = new Vector<>(testCollection);

    }

    /**********************************常用方法***********************************/
    // 将Vector内的元素拷贝到anArray数组中
    public synchronized void copyInto(Object[] anArray) {
        System.arraycopy(elementData, 0, anArray, 0, elementCount);
    }

    // 将当前容量值设为实际元素个数
    public synchronized void trimToSize() {
        // modCount++;
        int oldCapacity = elementData.length;
        if (elementCount < oldCapacity) {
            elementData = Arrays.copyOf(elementData, elementCount);
        }
    }




    public static void main(String[] args) {
        getConstructor();

        // vector方法与ArrayList类似，主要区别为Vector各方法加了synchronized,保证线程安全
    }

}
