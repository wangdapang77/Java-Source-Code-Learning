package com.sedion.mynawang.java8.collection.list;

import java.util.*;

/**
 * LinkedList源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-08-13 9:32
 */

/**
 * 定义：
 * LinkedList继承了抽象类AbstractSequentialList,而AbstractSequentialList继承了AbstractList，
 * LinkedList实现了List接口，提供List接口的默认实现。
 * LinkedList实现了Serializable接口，可以被序列化，能通过序列化传输。
 * LinkedList实现了Cloneable接口，可以被克隆。
 * LinkedList实现了Deque接口，Deque是双端队列(也就是既可以先入先出，又可以先入后出)接口，
 * 提供了类似push(入栈)、pop(出栈)和peek(查看栈顶元素)等适用于栈和队列的方法
 *
 */
public class TLinkedList<E> //extends AbstractSequentialList<E> implements List<E>, Deque<E>, Cloneable, java.io.Serializable
{
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

    // 移除索引位置的元素
    public E remove(int index) {
        // checkElementIndex(index);
        return unlink(node(index));
    }

    // 移除元素
    public boolean remove(Object o) {
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the first element in this list.
     * 返回第一个节点的元素
     *
     * @return the first element in this list
     * @throws NoSuchElementException if this list is empty
     */
    public E getFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return f.item;
    }

    /**
     * Returns the last element in this list.
     * 返回最后一个节点的元素
     *
     * @return the last element in this list
     * @throws NoSuchElementException if this list is empty
     */
    public E getLast() {
        final Node<E> l = last;
        if (l == null)
            throw new NoSuchElementException();
        return l.item;
    }

    /**
     * Removes and returns the first element from this list.
     * 移除第一个节点的元素
     *
     * @return the first element from this list
     * @throws NoSuchElementException if this list is empty
     */
    public E removeFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return unlinkFirst(f);
    }

    /**
     * Removes and returns the last element from this list.
     * 移除最后一个节点的元素
     *
     * @return the last element from this list
     * @throws NoSuchElementException if this list is empty
     */
    public E removeLast() {
        final Node<E> l = last;
        if (l == null)
            throw new NoSuchElementException();
        return unlinkLast(l);
    }

    /**
     * Inserts the specified element at the beginning of this list.
     * 加到LinkedList的第一个节点
     *
     * @param e the element to add
     */
    public void addFirst(E e) {
        linkFirst(e);
    }

    /**
     * Appends the specified element to the end of this list.
     * 加到LinkedList的最后一个节点
     *
     * <p>This method is equivalent to {@link #add}.
     *
     * @param e the element to add
     */
    public void addLast(E e) {
        linkLast(e);
    }


    /**
     * Retrieves, but does not remove, the head (first element) of this list.
     * 返回第一个节点元素
     *
     * @return the head of this list, or {@code null} if this list is empty
     * @since 1.5
     */
    public E peek() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }

    /**
     * Retrieves, but does not remove, the head (first element) of this list.
     * 返回第一个节点的元素
     *
     * @return the head of this list
     * @throws NoSuchElementException if this list is empty
     * @since 1.5
     */
    public E element() {
        return getFirst();
    }

    /**
     * Adds the specified element as the tail (last element) of this list.
     * 添加元素到LinkedList内（末端）
     *
     * @param e the element to add
     * @return {@code true} (as specified by {@link Queue#offer})
     * @since 1.5
     */
    public boolean offer(E e) {
        return add(e);
    }

    /**
     * Inserts the specified element at the front of this list.
     * 添加元素到LinkedList开头
     *
     * @param e the element to insert
     * @return {@code true} (as specified by {@link Deque#offerFirst})
     * @since 1.6
     */
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    /**
     * Inserts the specified element at the end of this list.
     * 添加元素到LinkedList末端
     *
     * @param e the element to insert
     * @return {@code true} (as specified by {@link Deque#offerLast})
     * @since 1.6
     */
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    /**
     * Retrieves, but does not remove, the first element of this list,
     * or returns {@code null} if this list is empty.
     * 返回LinkedList内第一个节点的元素
     *
     * @return the first element of this list, or {@code null}
     *         if this list is empty
     * @since 1.6
     */
    public E peekFirst() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }

    /**
     * Retrieves, but does not remove, the last element of this list,
     * or returns {@code null} if this list is empty.
     * 返回LinkedList最后一个节点的元素
     *
     * @return the last element of this list, or {@code null}
     *         if this list is empty
     * @since 1.6
     */
    public E peekLast() {
        final Node<E> l = last;
        return (l == null) ? null : l.item;
    }

    /**
     * Retrieves and removes the first element of this list,
     * or returns {@code null} if this list is empty.
     * 删除第一个节点的元素并且返回该元素
     *
     * @return the first element of this list, or {@code null} if
     *     this list is empty
     * @since 1.6
     */
    public E pollFirst() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    /**
     * Retrieves and removes the last element of this list,
     * or returns {@code null} if this list is empty.
     * 删除最后一个节点的元素并且返回该元素
     *
     * @return the last element of this list, or {@code null} if
     *     this list is empty
     * @since 1.6
     */
    public E pollLast() {
        final Node<E> l = last;
        return (l == null) ? null : unlinkLast(l);
    }

    /**
     * Pushes an element onto the stack represented by this list.  In other
     * words, inserts the element at the front of this list.
     * 加入到LinkedList的第一个节点（入栈）
     *
     * <p>This method is equivalent to {@link #addFirst}.
     *
     * @param e the element to push
     * @since 1.6
     */
    public void push(E e) {
        addFirst(e);
    }

    /**
     * Pops an element from the stack represented by this list.  In other
     * words, removes and returns the first element of this list.
     * 移除LinkedList的第一个元素（出栈）
     *
     * <p>This method is equivalent to {@link #removeFirst()}.
     *
     * @return the element at the front of this list (which is the top
     *         of the stack represented by this list)
     * @throws NoSuchElementException if this list is empty
     * @since 1.6
     */
    public E pop() {
        return removeFirst();
    }


    /**
     * Removes the first occurrence of the specified element in this
     * list (when traversing the list from head to tail).  If the list
     * does not contain the element, it is unchanged.
     * 从第一个元素开始查询是否存在o元素并且删除
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if the list contained the specified element
     * @since 1.6
     */
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    /**
     * Removes the last occurrence of the specified element in this
     * list (when traversing the list from head to tail).  If the list
     * does not contain the element, it is unchanged.
     * 从 最后一个元素开始查询是否存在o元素并且删除
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if the list contained the specified element
     * @since 1.6
     */
    public boolean removeLastOccurrence(Object o) {
        if (o == null) {
            for (Node<E> x = last; x != null; x = x.prev) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = last; x != null; x = x.prev) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
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

        String testRemoveE = testLinkedList.remove(0);
        System.out.println("testRemoveE: " + testRemoveE);
        boolean testRemoveFlag = testLinkedList.remove("test1");
        System.out.println("testRemoveFlag: " + testRemoveFlag);

        testLinkedList.add("test2");
        testLinkedList.add("test3");
        testLinkedList.add("test4");

        String testGetFirstE = testLinkedList.getFirst();
        System.out.println("testGetFirstE: " + testGetFirstE);
        String testRemoveFirstE = testLinkedList.removeFirst();
        System.out.println("testRemoveFirstE: " + testRemoveFirstE);
        testLinkedList.addFirst("testAddFirst");
        System.out.println("afterAddFirst: " + testLinkedList);


        String testGetLastE = testLinkedList.getLast();
        System.out.println("testGetLastE: " + testGetLastE);
        String testRemoveLastE = testLinkedList.removeLast();
        System.out.println("testRemoveLastE: " + testRemoveLastE);
        testLinkedList.addLast("testAddLast");
        System.out.println("afterAddLast: " + testLinkedList);


        String testPeek = testLinkedList.peek();
        System.out.println("testPeek: " + testPeek);
        System.out.println(testLinkedList);
        String testPeekFirst = testLinkedList.peekFirst();
        String testPeekLast = testLinkedList.peekLast();
        System.out.println("testPeekFirst: " + testPeekFirst);
        System.out.println("testPeekLast: " + testPeekLast);


        String testPoll = testLinkedList.poll();
        System.out.println("testPoll: " + testPoll);
        System.out.println("afterPoll: " + testLinkedList);

        String testPollFirst = testLinkedList.pollFirst();
        System.out.println("testPollFirst: " + testPollFirst);
        System.out.println("afterPollFirst: " + testLinkedList);

        String testPollLast = testLinkedList.pollLast();
        System.out.println("testPollLast: " + testPollLast);
        System.out.println("afterPollLast: " + testLinkedList);


        testLinkedList.add("test5");
        testLinkedList.add("test6");
        testLinkedList.add("test7");


        testLinkedList.push("testPush");
        System.out.println("afterPush: " + testLinkedList);

        String testPop = testLinkedList.pop();
        System.out.println("testPop: " + testPop);
        System.out.println("afterPop: " + testLinkedList);

        testLinkedList.removeFirstOccurrence("test6");
        System.out.println("afterRemoveFirstOccurrence: " + testLinkedList);

        testLinkedList.removeLastOccurrence("test5");
        System.out.println("afterRemoveLastOccurrence: " + testLinkedList);

    }


    public static void main(String[] args) {
        getConstructor();

        getMethod();

    }


}
