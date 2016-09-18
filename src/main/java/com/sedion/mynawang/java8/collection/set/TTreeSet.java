package com.sedion.mynawang.java8.collection.set;


/**
 * TreeSet源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-08-30 17:06
 */


import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 定义：
 * TreeSet继承AbstractSet类， AbstractSet是一个抽象类，而AbstractSet本身实现了Set接口，提供Set的默认实现。
 * TreeSet实现NavigableSet接口，NavigableSet接口继承SortedSet接口，为给定搜索目标报告最接近匹配项的导航方法
 * TreeSet实现了Cloneable接口，可以被克隆。
 * TreeSet实现了Serializable接口，支持序列化，能够通过序列化传输。
 * 与HashSet是基于HashMap实现一样，TreeSet同样是基于TreeMap实现的。
 * http://www.cnblogs.com/chenssy/p/3772661.html
 */
 public class TTreeSet<E> { //extends AbstractSet<E> implements NavigableSet<E>, Cloneable, java.io.Serializable

    /**********************************属性***********************************/

    /**
     * The backing map.
     */
    private transient NavigableMap<E,Object> m;

    // Dummy value to associate with an Object in the backing Map
    // 定义一个Object对象作为TreeSet的value
    private static final Object PRESENT = new Object();


    /**********************************构造器***********************************/
    /**
     * Constructs a set backed by the specified navigable map.
     */
    TTreeSet(NavigableMap<E,Object> m) {
        this.m = m;
    }

    /**
     * Constructs a new, empty tree set, sorted according to the
     * natural ordering of its elements.  All elements inserted into
     * the set must implement the {@link Comparable} interface.
     * Furthermore, all such elements must be <i>mutually
     * comparable</i>: {@code e1.compareTo(e2)} must not throw a
     * {@code ClassCastException} for any elements {@code e1} and
     * {@code e2} in the set.  If the user attempts to add an element
     * to the set that violates this constraint (for example, the user
     * attempts to add a string element to a set whose elements are
     * integers), the {@code add} call will throw a
     * {@code ClassCastException}.
     * 默认的构造方法，根据元素的自然顺序进行排序，其内部是TreeMap
     */
    public TTreeSet() {
        this(new TreeMap<E,Object>());
    }

    /**
     * Constructs a new, empty tree set, sorted according to the specified
     * comparator.  All elements inserted into the set must be <i>mutually
     * comparable</i> by the specified comparator: {@code comparator.compare(e1,
     * e2)} must not throw a {@code ClassCastException} for any elements
     * {@code e1} and {@code e2} in the set.  If the user attempts to add
     * an element to the set that violates this constraint, the
     * {@code add} call will throw a {@code ClassCastException}.
     *
     * @param comparator the comparator that will be used to order this set.
     *        If {@code null}, the {@linkplain Comparable natural
     *        ordering} of the elements will be used.
     * 包含指定 collection 元素的TreeSet，根据元素的自然顺序进行排序
     */
    public TTreeSet(Comparator<? super E> comparator) {
        this(new TreeMap<>(comparator));
    }

    /**
     * Constructs a new tree set containing the elements in the specified
     * collection, sorted according to the <i>natural ordering</i> of its
     * elements.  All elements inserted into the set must implement the
     * {@link Comparable} interface.  Furthermore, all such elements must be
     * <i>mutually comparable</i>: {@code e1.compareTo(e2)} must not throw a
     * {@code ClassCastException} for any elements {@code e1} and
     * {@code e2} in the set.
     *
     * @param c collection whose elements will comprise the new set
     * @throws ClassCastException if the elements in {@code c} are
     *         not {@link Comparable}, or are not mutually comparable
     * @throws NullPointerException if the specified collection is null
     *
     * 构造一个新的空 TreeSet，它根据指定比较器进行排序。
     */
    public TTreeSet(Collection<? extends E> c) {
        this();
        // addAll(c);
    }

    /**
     * Constructs a new tree set containing the same elements and
     * using the same ordering as the specified sorted set.
     *
     * @param s sorted set whose elements will comprise the new set
     * @throws NullPointerException if the specified sorted set is null
     * 构造一个与指定有序的set具有相同映射关系和相同排序的TreeSet
     */
    public TTreeSet(SortedSet<E> s) {
        this(s.comparator());
       // addAll(s);
    }


    public static void getConstructor() {
        TreeSet testTreeSet1 = new TreeSet();
        System.out.println("testTreeSet1: " + testTreeSet1);

        ConcurrentSkipListSet concurrentSkipListSet = new ConcurrentSkipListSet();
        concurrentSkipListSet.add("test1");
        TreeSet testTreeSet2 = new TreeSet(concurrentSkipListSet.comparator());
        System.out.println("testTreeSet2: " + testTreeSet2);

        HashSet hashSet = new HashSet();
        hashSet.add("test2");
        TreeSet testTreeSet3 = new TreeSet(hashSet);
        System.out.println("testTreeSet3: " + testTreeSet3);

        ConcurrentSkipListSet concurrentSkipListSet2 = new ConcurrentSkipListSet();
        concurrentSkipListSet.add("test3");
        TreeSet testTreeSet4 = new TreeSet(concurrentSkipListSet2);
        System.out.println("testTreeSet4: " + testTreeSet4);
    }


    /**********************************常用方法***********************************/
    /**
     * Adds the specified element to this set if it is not already present.
     * More formally, adds the specified element {@code e} to this set if
     * the set contains no element {@code e2} such that
     * <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>.
     * If this set already contains the element, the call leaves the set
     * unchanged and returns {@code false}.
     *
     * @param e element to be added to this set
     * @return {@code true} if this set did not already contain the specified
     *         element
     * @throws ClassCastException if the specified object cannot be compared
     *         with the elements currently in this set
     * @throws NullPointerException if the specified element is null
     *         and this set uses natural ordering, or its comparator
     *         does not permit null elements
     * 添加指定元素到TreeSet中
     */
    public boolean add(E e) {
        // 保证集合中元素的唯一性,添加成功返回null
        return m.put(e, PRESENT)==null;
    }

    /**
     * Adds all of the elements in the specified collection to this set.
     *
     * @param c collection containing elements to be added to this set
     * @return {@code true} if this set changed as a result of the call
     * @throws ClassCastException if the elements provided cannot be compared
     *         with the elements currently in the set
     * @throws NullPointerException if the specified collection is null or
     *         if any element is null and this set uses natural ordering, or
     *         its comparator does not permit null elements
     * 添加集合里的元素到TreeSet中
     */
    public  boolean addAll(Collection<? extends E> c) {
        // Use linear-time version if applicable
        if (m.size()==0 && c.size() > 0 &&
                c instanceof SortedSet &&
                m instanceof TreeMap) {
            SortedSet<? extends E> set = (SortedSet<? extends E>) c;
            TreeMap<E,Object> map = (TreeMap<E, Object>) m;
            Comparator<?> cc = set.comparator();
            Comparator<? super E> mc = map.comparator();
            if (cc==mc || (cc != null && cc.equals(mc))) {
               // map.addAllForTreeSet(set, PRESENT);
                return true;
            }
        }
        // return super.addAll(c);

        // 随意添加，忽略
        return true;
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified element is null
     *         and this set uses natural ordering, or its comparator
     *         does not permit null elements
     * @since 1.6
     * 返回set中小于等于给定元素的最大元素，如果没有这样的元素则返回null
     */
    public E floor(E e) {
        return m.floorKey(e);
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified element is null
     *         and this set uses natural ordering, or its comparator
     *         does not permit null elements
     * @since 1.6
     * 返回set中严格大于给定元素的最小元素，如果没有这样的元素则返回null
     */
    public E higher(E e) {
        return m.higherKey(e);
    }

    /**
     * @throws ClassCastException {@inheritDoc}
     * @throws NullPointerException if the specified element is null
     *         and this set uses natural ordering, or its comparator
     *         does not permit null elements
     * @since 1.6
     * 返回set中大于等于给定元素的最小元素，如果没有这样的元素则返回null
     */
    public E ceiling(E e) {
        return m.ceilingKey(e);
    }



    /**
     * Removes all of the elements from this set.
     * The set will be empty after this call returns.
     * 清除set中的所有元素
     */
    public void clear() {
        m.clear();
    }

    /**
     * Returns a shallow copy of this {@code TreeSet} instance. (The elements
     * themselves are not cloned.)
     *
     * @return a shallow copy of this set
     */
    @SuppressWarnings("unchecked")
    public Object clone() {
        TreeSet<E> clone;
        try {
            clone = (TreeSet<E>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
        // 浅拷贝
        // clone.m = new TreeMap<>(m);
        return clone;
    }

    public static void main(String[] args) {
        getConstructor();
    }


}
