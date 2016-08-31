package com.sedion.mynawang.java8.map;

/**
 * TLinkedHashMap源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-08-30 17:21
 */

import java.util.*;

/**
 * 定义：
 * LinkedHashMap继承HashMap,即LinkedHashMap为HashMap的子类
 * LinkedHashMap实现Map。Map本身定义了键值的映射关系
 *
 * LinkedHashMap是key键有序的HashMap的一种实现，哈希表这个数据结构，使用双向链表来保证key的顺序
 */
public class TLinkedHashMap<K,V> extends HashMap<K,V>//  implements Map<K,V>
{
    /**
     * HashMap.Node subclass for normal LinkedHashMap entries.
     * Entry<K,V>继承自THashMap.Node<K,V>，而THashMap.Node<K,V>继承自Map.Entry<K,V>
     *
     * 一个元素包含几项信息：
     * 继承自HashMap： hash值、key、value、next(指向下一个Node【table位置上连接的Entry的顺序】)
     * 自己新加：before（指向Entry插入的上一个元素结点） after(指向Entry插入的下一个元素结点）
     */
    static class Entry<K,V> extends THashMap.Node<K,V> {
        Entry<K,V> before, after;
        Entry(int hash, K key, V value, THashMap.Node<K,V> next) {
            super(hash, key, value, next);
        }
    }

    // 版本序列号
    private static final long serialVersionUID = 3801124242820219131L;

    /**
     * The head (eldest) of the doubly linked list.
     * 链表头节点
     */
    transient TLinkedHashMap.Entry<K,V> head;

    /**
     * The tail (youngest) of the doubly linked list.
     * 链表尾节点
     */
    transient TLinkedHashMap.Entry<K,V> tail;

    /**
     * The iteration ordering method for this linked hash map: <tt>true</tt>
     * for access-order, <tt>false</tt> for insertion-order.
     * 访问顺序
     * @serial
     */
    final boolean accessOrder;

    /**
     * Constructs an empty insertion-ordered <tt>LinkedHashMap</tt> instance
     * with the specified initial capacity and load factor.
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is nonpositive
     * 初始化一个空的LinkedHashMap，并使用默认初始容量为16和加载因子0.75。
     *
     */
    public TLinkedHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        accessOrder = false;
    }

    /**
     * Constructs an empty insertion-ordered <tt>LinkedHashMap</tt> instance
     * with the specified initial capacity and a default load factor (0.75).
     *
     * @param  initialCapacity the initial capacity
     * @throws IllegalArgumentException if the initial capacity is negative
     * 构造一个新的空LinkedHashMap，其底层 HashMap 实例具有指定的初始容量和默认的加载因子（0.75）。
     *
     */
    public TLinkedHashMap(int initialCapacity) {
        super(initialCapacity);
        accessOrder = false;
    }

    /**
     * Constructs an empty insertion-ordered <tt>LinkedHashMap</tt> instance
     * with the default initial capacity (16) and load factor (0.75).
     * 默认构造函数
     * 初始化一个空的HashMap，并使用默认初始容量为16和加载因子0.75。
     */
    public TLinkedHashMap() {
        super();
        accessOrder = false;
    }

    /**
     * Constructs an insertion-ordered <tt>LinkedHashMap</tt> instance with
     * the same mappings as the specified map.  The <tt>LinkedHashMap</tt>
     * instance is created with a default load factor (0.75) and an initial
     * capacity sufficient to hold the mappings in the specified map.
     *
     * @param  m the map whose mappings are to be placed in this map
     * @throws NullPointerException if the specified map is null
     *
     * 构造一个包含指定 Map 中的元素的新 LinkedHashMap。
     */
    public TLinkedHashMap(Map<? extends K, ? extends V> m) {
        super();
        accessOrder = false;
       // putMapEntries(m, false);
    }

    /**
     * Constructs an empty <tt>LinkedHashMap</tt> instance with the
     * specified initial capacity, load factor and ordering mode.
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @param  accessOrder     the ordering mode - <tt>true</tt> for
     *         access-order, <tt>false</tt> for insertion-order
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is nonpositive
     * 以指定的initialCapacity和loadFactor构造一个新的空链接哈希集合
     *
     */
    public TLinkedHashMap(int initialCapacity,
                         float loadFactor,
                         boolean accessOrder) {
        super(initialCapacity, loadFactor);
        this.accessOrder = accessOrder;
    }

    public static void getConstructor() {
        LinkedHashMap<Object, Object> testLinkedHashMap = new LinkedHashMap<>();
        System.out.println("testLinkedHashMap: " + testLinkedHashMap);
        LinkedHashMap<Object, Object> testLinkedHashMap1 = new LinkedHashMap<>(10, 0.75F);
        System.out.println("testLinkedHashMap1: " + testLinkedHashMap1);
        Map<Object, Object> testMap = new HashMap<Object, Object>();
        LinkedHashMap<Object, Object> testLinkedHashMap2 = new LinkedHashMap<Object, Object>(testMap);
        System.out.println("testLinkedHashMap2: " + testLinkedHashMap2);
    }






    public static void main(String[] args) {
        getConstructor();
    }


}
