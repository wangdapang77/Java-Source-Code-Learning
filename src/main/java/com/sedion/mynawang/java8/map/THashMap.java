package com.sedion.mynawang.java8.map;

/**
 * HashMap源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-08-15 11:33
 */

/**
 * 定义：
 * HashMap实现了Map接口，继承AbstractMap，而AbstractMap本身实现了Map接口，提供Map的默认实现。
 * HashMap自身实现了Map接口。Map本身定义了键值的映射关系
 * HashMap实现了Cloneable接口，可以被克隆。
 * HashMap实现了Serializable接口，支持序列化，能够通过序列化传输。
 */
public class THashMap {

    private static final long serialVersionUID = 362498820763181265L;

    /**
     * The default initial capacity - MUST be a power of two.
     * 默认的初始容量（HashMap中槽的数目）为2的4次方16
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     * 最大容量，必须为2的幂次方且小于2的30次方
     *
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     * 默认加载因子为0.75
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The bin count threshold for using a tree rather than list for a
     * bin.  Bins are converted to trees when adding an element to a
     * bin with at least this many nodes. The value must be greater
     * than 2 and should be at least 8 to mesh with assumptions in
     * tree removal about conversion back to plain bins upon
     * shrinkage.
     * 当add一个元素到某个位桶，其链表长度达到8时将链表转换为红黑树
     *
     */
    static final int TREEIFY_THRESHOLD = 8;

    /**
     * The bin count threshold for untreeifying a (split) bin during a
     * resize operation. Should be less than TREEIFY_THRESHOLD, and at
     * most 6 to mesh with shrinkage detection under removal.
     */
    static final int UNTREEIFY_THRESHOLD = 6;

    /**
     * The smallest table capacity for which bins may be treeified.
     * (Otherwise the table is resized if too many nodes in a bin.)
     * Should be at least 4 * TREEIFY_THRESHOLD to avoid conflicts
     * between resizing and treeification thresholds.
     *
     *
     */
    static final int MIN_TREEIFY_CAPACITY = 64;




}
