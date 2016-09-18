package com.sedion.mynawang.java8.collection.set;

/**
 * LinkedHashSet源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-08-30 17:11
 */


import java.util.Collection;
import java.util.HashSet;
import java.util.Spliterator;
import java.util.Spliterators;

/**
 * 定义：
 * LinkedHashSet继承HashSet类， HashSet继承AbstractSet，而HashSet本身实现了Set接口，提供Set的默认实现。
 * LinkedHashSet实现了Set接口，由哈希表（实际上是一个HashMap实例）支持。
 * LinkedHashSet实现了Cloneable接口，可以被克隆。
 * LinkedHashSet实现了Serializable接口，支持序列化，能够通过序列化传输。
 */
public class TLinkedHashSet<E> extends HashSet<E> { // extends HashSet<E> implements Set<E>, Cloneable, java.io.Serializable

    /**********************************属性***********************************/

    private static final long serialVersionUID = -2851667679971038690L;

    /**
     * Constructs a new, empty linked hash set with the specified initial
     * capacity and load factor.
     *
     * @param      initialCapacity the initial capacity of the linked hash set
     * @param      loadFactor      the load factor of the linked hash set
     * @throws     IllegalArgumentException  if the initial capacity is less
     *               than zero, or if the load factor is nonpositive
     * 构造带有指定初始容量和加载因子的新空链接哈希set
     * 调用父类构造方法，构造一个有指定初始容量和加载因子的LinkedHashMap实例
     */
    public TLinkedHashSet(int initialCapacity, float loadFactor) {

//        super(initialCapacity, loadFactor, true);
    }

    /**
     * Constructs a new, empty linked hash set with the specified initial
     * capacity and the default load factor (0.75).
     *
     * @param   initialCapacity   the initial capacity of the LinkedHashSet
     * @throws  IllegalArgumentException if the initial capacity is less
     *              than zero
     * 构造一个带指定初始容量和默认加载因子0.75的新空链接哈希set。
     */
    public TLinkedHashSet(int initialCapacity) {
//        super(initialCapacity, .75f, true);
    }

    /**
     * Constructs a new, empty linked hash set with the default initial
     * capacity (16) and load factor (0.75).
     * 构造一个带默认初始容量16和加载因子0.75的新空链接哈希set
     */
    public TLinkedHashSet() {
//        super(16, .75f, true);
    }

    /**
     * Constructs a new linked hash set with the same elements as the
     * specified collection.  The linked hash set is created with an initial
     * capacity sufficient to hold the elements in the specified collection
     * and the default load factor (0.75).
     *
     * @param c  the collection whose elements are to be placed into
     *           this set
     * @throws NullPointerException if the specified collection is null
     *
     *  构造一个与指定collection中的元素相同的新链接哈希set
     */
    public TLinkedHashSet(Collection<? extends E> c) {
//        super(Math.max(2*c.size(), 11), .75f, true);
        addAll(c);
    }

    /**
     * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
     * and <em>fail-fast</em> {@code Spliterator} over the elements in this set.
     *
     * <p>The {@code Spliterator} reports {@link Spliterator#SIZED},
     * {@link Spliterator#DISTINCT}, and {@code ORDERED}.  Implementations
     * should document the reporting of additional characteristic values.
     *
     * @implNote
     * The implementation creates a
     * <em><a href="Spliterator.html#binding">late-binding</a></em> spliterator
     * from the set's {@code Iterator}.  The spliterator inherits the
     * <em>fail-fast</em> properties of the set's iterator.
     * The created {@code Spliterator} additionally reports
     * {@link Spliterator#SUBSIZED}.
     *
     * @return a {@code Spliterator} over the elements in this set
     * @since 1.8
     */
    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, Spliterator.DISTINCT | Spliterator.ORDERED);
    }


}
