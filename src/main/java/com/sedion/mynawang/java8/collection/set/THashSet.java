package com.sedion.mynawang.java8.collection.set;

/**
 * HashSet源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-08-30 16:24
 */


import java.util.*;

/**
 * 定义：
 * HashSet继承AbstractSet类， AbstractSet是一个抽象类，而AbstractSet本身实现了Set接口，提供Set的默认实现。
 * HashSet实现了Set接口，由哈希表（实际上是一个HashMap实例）支持。
 * HashSet实现了Cloneable接口，可以被克隆。
 * HashSet实现了Serializable接口，支持序列化，能够通过序列化传输。
 */
public class THashSet<E> {


    /**********************************属性***********************************/

    static final long serialVersionUID = -5024744406713321676L;

    // 基于HashMap实现，底层使用HashMap保存所有元素
    private transient HashMap<E,Object> map;

    // 定义一个Object对象作为HashMap的value
    private static final Object PRESENT = new Object();


    /**********************************构造器***********************************/
    /**
     * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
     * default initial capacity (16) and load factor (0.75).
     * 默认构造函数
     * 初始化一个空的HashMap，并使用默认初始容量为16和加载因子0.75。
     *
     */
    public THashSet() {
        map = new HashMap<>();
    }


    /**
     * Constructs a new set containing the elements in the specified
     * collection.  The <tt>HashMap</tt> is created with default load factor
     * (0.75) and an initial capacity sufficient to contain the elements in
     * the specified collection.
     *
     * @param c the collection whose elements are to be placed into this set
     * @throws NullPointerException if the specified collection is null
     *
     * 构造一个包含指定 collection 中的元素的新 set。
     */
    public THashSet(Collection<? extends E> c) {
        map = new HashMap<>(Math.max((int) (c.size()/.75f) + 1, 16));
       // addAll(c);
    }


    /**
     * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
     * the specified initial capacity and the specified load factor.
     *
     * @param      initialCapacity   the initial capacity of the hash map
     * @param      loadFactor        the load factor of the hash map
     * @throws     IllegalArgumentException if the initial capacity is less
     *             than zero, or if the load factor is nonpositive
     *
     * 构造一个新的空 set，其底层 HashMap 实例具有指定的初始容量和指定的加载因子
     */
    public THashSet(int initialCapacity, float loadFactor) {
        map = new HashMap<>(initialCapacity, loadFactor);
    }

    /**
     * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
     * the specified initial capacity and default load factor (0.75).
     *
     * @param      initialCapacity   the initial capacity of the hash table
     * @throws     IllegalArgumentException if the initial capacity is less
     *             than zero
     * 构造一个新的空 set，其底层 HashMap 实例具有指定的初始容量和默认的加载因子（0.75）。
     */
    public THashSet(int initialCapacity) {
        map = new HashMap<>(initialCapacity);
    }

    /**
     * Constructs a new, empty linked hash set.  (This package private
     * constructor is only used by LinkedHashSet.) The backing
     * HashMap instance is a LinkedHashMap with the specified initial
     * capacity and the specified load factor.
     *
     * @param      initialCapacity   the initial capacity of the hash map
     * @param      loadFactor        the load factor of the hash map
     * @param      dummy             ignored (distinguishes this
     *             constructor from other int, float constructor.)
     * @throws     IllegalArgumentException if the initial capacity is less
     *             than zero, or if the load factor is nonpositive
     *
     * 没有修饰符，访问权限为包权限，不对外公开
     * 以指定的initialCapacity和loadFactor构造一个新的空链接哈希集合。
     * dummy 为标识 该构造函数主要作用是对LinkedHashSet起到一个支持作用
     */
    THashSet(int initialCapacity, float loadFactor, boolean dummy) {
        map = new LinkedHashMap<>(initialCapacity, loadFactor);
    }

    public static void getConstructor() {
        HashSet<Object> testHashSet = new HashSet<>();
        System.out.println("testHashSet: " + testHashSet);
        HashSet<Object> testHashSet1 = new HashSet<>(10, 0.75F);
        System.out.println("testHashSet1: " + testHashSet1);
        HashSet<Object> testHashSet2 = new HashSet<>(10);
        System.out.println("testHashSet2: " + testHashSet2);
        Collection testCon = new ArrayList<String>();
        HashSet<Object> testHashSet3 = new HashSet<>(testCon);
        System.out.println("testHashSet3: " + testHashSet3);
    }


    /**********************************常用方法***********************************/
    // 调用HashMap方法，此处忽略


    public static void main(String[] args) {
        getConstructor();
    }

}
