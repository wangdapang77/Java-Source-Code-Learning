package com.sedion.mynawang.java8.collection.list;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * LinkedList源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-08-13 9:32
 */

/**
 * 定义：
 * LinkedList继承了抽象类AbstractSequentialList,而AbstractSequentialList继承了AbstractList，
 * AbstractList实现了List接口，提供List接口的默认实现。
 * LinkedList实现了Serializable接口，可以被序列化，能通过序列化传输。
 * LinkedList实现了Cloneable接口，可以被克隆。
 * LinkedList实现了Deque接口，Deque是双端队列(也就是既可以先入先出，又可以先入后出)接口，
 * 提供了类似push(入栈)、pop(出栈)和peek(查看栈顶元素)等适用于栈和队列的方法
 *
 */
public class TLinkedList<E> {

    /**********************************属性***********************************/
    // 节点个数
    transient int size = 0;

    /**
     * Pointer to first node.
     * Invariant: (first == null && last == null) ||
     *            (first.prev == null && first.item != null)
     * 链表的首节点
     */
    transient Node<E> first;

    /**
     * Pointer to last node.
     * Invariant: (first == null && last == null) ||
     *            (last.next == null && last.item != null)
     * 链表的尾节点
     */
    transient Node<E> last;

    // Node内部类
    private static class Node<E> {
        // 节点值（当前值）
        E item;
        // 节点的后一个节点（下一个元素）
        Node<E> next;
        // 节点的前一个节点（上一个元素）
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    /**********************************构造器***********************************/
    /**
     * Constructs an empty list.
     * 构造空链表
     */
    public TLinkedList() {
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * 传入对象利用addAll方法初始化链表
     *
     * @param  c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public TLinkedList(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        // 校验index >= 0 && index <= size
        //checkPositionIndex(index);

        Object[] a = c.toArray();
        int numNew = a.length;
        // 若需要插入的节点个数为0则返回false，表示没有插入元素
        if (numNew == 0)
            return false;

        // 创建2个节点的引用
        // succ保存index位置的元素
        Node<E> pred, succ;
        // 如果index和size相等，说明index正好是list尾部的位置，
        // 当前元素的上一个元素prev就是之前已经存在链表的最后一个元素last
        if (index == size) {
            succ = null;
            pred = last;
        } else {
            // 当index != size时，index一定出现在之前的链表中
            // node(index)判断index所处在的位置是在之前链表的上半部分还是下半部分，返回index所在的上一个元素
            // 【在上半部分则从第一个元素开查找，在下半部分则从最后一个元素开始】
            succ = node(index);
            // 得到index所在元素的上一个元素引用prev
            pred = succ.prev;
        }

        //
        for (Object o : a) {
            // 将每个元素插入双向链表
            @SuppressWarnings("unchecked") E e = (E) o;
            // 新节点 e.prev = pred e.next = null
            Node<E> newNode = new Node<>(pred, e, null);
            if (pred == null)
                // 如果前一个元素为null, 那么说明之前不存在链表，此元素将设置为链表的第一个元素
                first = newNode;
            else
                // 如果已存在链表，那么就从之前链表的index位置开始插入
                pred.next = newNode;
            pred = newNode;
        }

        if (succ == null) {
            last = pred;
        } else {
            pred.next = succ;
            succ.prev = pred;
        }

        size += numNew;
        //modCount++;
        return true;
    }

    Node<E> node(int index) {
        // assert isElementIndex(index);
        if (index < (size >> 1)) {
            Node<E> x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }

    public static void getConstructor() {
        List<String> testLinkedList = new LinkedList<String>();
        Collection testCon = new LinkedList<String>();
        List<String> testLinkedList2 = new LinkedList<String>(testCon);

        System.out.println("testLinkedList: " + testLinkedList.hashCode());
        System.out.println("testLinkedList2: " + testLinkedList2.hashCode());
    }


    /**********************************常用方法***********************************/

    // 添加元素，默认加在最后
    public boolean add(E e) {
        linkLast(e);
        return true;
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     *
     *  根据索引位置进行添加
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public void add(int index, E element) {
        // checkPositionIndex(index);

        // 如果index == size，则正好加在结尾
        if (index == size)
            linkLast(element);
        else
            // node(index)判断index所处在的位置是在之前链表的上半部分还是下半部分,返回index所在的上一个元素
            linkBefore(element, node(index));
    }

    /**
     * Links e as first element.
     * 将元素加载LinkedList最前面
     */
    private void linkFirst(E e) {
        final Node<E> f = first;
        final Node<E> newNode = new Node<>(null, e, f);
        first = newNode;
        if (f == null)
            last = newNode;
        else
            f.prev = newNode;
        size++;
        // modCount++;
    }

    /**
     * Links e as last element.
     * 将元素加载LinkedList最后面
     */
    void linkLast(E e) {
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        // 如果节点为空
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
        // modCount++;
    }

    /**
     * Inserts element e before non-null Node succ.
     * 将元素加在succ元素的前面
     */
    void linkBefore(E e, Node<E> succ) {
        // assert succ != null;
        final Node<E> pred = succ.prev;
        final Node<E> newNode = new Node<>(pred, e, succ);
        succ.prev = newNode;
        // 前一个元素为空则说明为第一个元素
        if (pred == null)
            first = newNode;
        else
            // 不为空则将前一个元素的下一个元素的地址让新节点保存
            pred.next = newNode;
        size++;
        //modCount++;
    }

    /**
     * Unlinks non-null first node f.
     * 去掉头节点
     */
    private E unlinkFirst(Node<E> f) {
        // assert f == first && f != null;
        final E element = f.item;
        final Node<E> next = f.next;
        f.item = null;
        f.next = null; // help GC
        first = next;
        if (next == null)
            last = null;
        else
            next.prev = null;
        size--;
        // modCount++;
        return element;
    }

    /**
     * Unlinks non-null last node l.
     * 去掉尾节点
     */
    private E unlinkLast(Node<E> l) {
        // assert l == last && l != null;
        final E element = l.item;
        final Node<E> prev = l.prev;
        l.item = null;
        l.prev = null; // help GC
        last = prev;
        if (prev == null)
            first = null;
        else
            prev.next = null;
        size--;
        // modCount++;
        return element;
    }

    /**
     * Unlinks non-null node x.
     * 去掉x节点
     */
    E unlink(Node<E> x) {
        // assert x != null;
        final E element = x.item;
        final Node<E> next = x.next;
        final Node<E> prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.item = null;
        size--;
        // modCount++;
        return element;
    }




    public static void getMethod() {
        LinkedList<String> testLinkedList = new LinkedList<String>();
        testLinkedList.add("test1");
    }


    public static void main(String[] args) {
        getConstructor();
    }


}
