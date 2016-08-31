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
     * 桶内单向链表通过 next 链接
     * 全局的双向链表通过 before 和 after 链接
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
     * 访问顺序 true表示按照访问顺序迭代，false时表示按照插入顺序
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



    /**********************************常用方法***********************************/

    // link at the end of list
    // 添加元素到双向链表的末尾
    private void linkNodeLast(TLinkedHashMap.Entry<K,V> p) {
        // 链表原尾节点赋值给last
        TLinkedHashMap.Entry<K,V> last = tail;
        // 定义新加的节点为尾节点
        tail = p;
        // 如果之前的尾节点为空（说明这个链表形的HashMap为空）
        if (last == null)
            // 头节点为新加节点
            head = p;
        // 如果之前的尾节点为空（说明这个链表形的HashMap不为空）
        else {
            // 新节点的前一个节点为原来的尾节点
            p.before = last;
            // 原来尾节点的后一个节点是新加的节点
            last.after = p;
        }
    }

    // apply src's links to dst
    // 用dst节点替换src节点，其中只考虑了before与after域，并没有考虑next域，next会在调用tranferLinks函数中进行设定。
    private void transferLinks(TLinkedHashMap.Entry<K,V> src,
                               TLinkedHashMap.Entry<K,V> dst) {
        // 修改dst的前驱和后继指向    src.before同时赋值给b和dst.before
        TLinkedHashMap.Entry<K,V> b = dst.before = src.before;
        TLinkedHashMap.Entry<K,V> a = dst.after = src.after;
        // b节点为空
        if (b == null)
            // 头节点为dst
            head = dst;
        else
            // b节点的后节点指向dst节点
            b.after = dst;
        // a节点为空（src为最后一个节点）
        if (a == null)
            // 尾节点为dst节点
            tail = dst;
        else
            // a节点的前节点指向dst节点
            a.before = dst;
    }

    /*
    * 当桶中结点类型为HashMap.Node类型时，调用此函数
    Node<K,V> newNode(int hash, K key, V value, Node<K,V> e) {
        LinkedHashMap.Entry<K,V> p =
                new LinkedHashMap.Entry<K,V>(hash, key, value, e);
        // 将该结点插入双链表末尾
        linkNodeLast(p);
        return p;
    }*/

   /*
    * 当桶中结点类型为HashMap.TreeNode类型时，调用此函数
   TreeNode<K,V> newTreeNode(int hash, K key, V value, Node<K,V> next) {
        TreeNode<K,V> p = new TreeNode<K,V>(hash, key, value, next);
         // 将该结点插入双链表末尾
        linkNodeLast(p);
        return p;
    }*/


    /**
     * 将指定节点移到尾节点位置
     * 当accessOrder为true（按最近最少使用）时，结点访问（get，put操作）后需要调整结点顺序，
     * 将当前被操作节点移动到head结点之前，即链表的尾部
     *
    void afterNodeAccess(Node<K,V> e) { // move node to last
        LinkedHashMap.Entry<K,V> last;
         // 若访问顺序为true，且访问的对象不是尾结点, 先将结点e从原链表中解除出来，再链到head之前
        if (accessOrder && (last = tail) != e) {
            // 声明当前p节点，b为前节点，a为后节点
            LinkedHashMap.Entry<K,V> p =
                    (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
            // 需要分别考虑p结点是head和tail的情况, 断开p=e与其后继结点的关联
            // 假设p为尾节点后，它的后节点为空（其实为头节点）
            p.after = null;
            // 若p=e的前一个结点为空，则p为原头结点head，所以删除后，p的原后继结点a成为新的头结点head
            if (b == null)
                // p的后节点为头节点
                head = a;
            // p不是头结点，则b的后继指向a结点
            else
                b.after = a;
            // 后继结点a不为空，a指向p的前驱结点b
            if (a != null)
                a.before = b;
            // 后继结点为空，则p结点为尾结点tail，所以删除后，p的前驱节点成为新的尾结点
            else
                last = b;
            // 若last=b为空，则p为头结点
            if (last == null)
                head = p;
            // last不为空
            else {
                p.before = last;
                last.after = p;
            }
            // 将p结点链到尾部
            tail = p;
            ++modCount;
        }
    }
    */














    static class testQ {
        int testNum;

        public int getTestNum() {
            return testNum;
        }

        public void setTestNum(int testNum) {
            this.testNum = testNum;
        }

        testQ test2;
    }



    public static void main(String[] args) {
        getConstructor();

        /*TLinkedHashMap.testQ test11 = new testQ();
        test11.setTestNum(1);
        test11.test2 = test11;
        System.out.println(test11.hashCode());

        TLinkedHashMap.testQ test22 = new testQ();
        test22.setTestNum(2);
        test22.test2 = test22;
        System.out.println(test22.hashCode());

        TLinkedHashMap.testQ i = test11.test2 = test22.test2;

        System.out.println("i: " + i.getTestNum());
        System.out.println("i: " + i.hashCode());

        System.out.println("test11: " + test11.getTestNum());
        System.out.println("test11: " + test11.hashCode());

        System.out.println("test22: " + test22.getTestNum());
        System.out.println("test22: " + test22.hashCode());*/

    }


}
