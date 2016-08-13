package com.sedion.mynawang.java8.collection.list;

/**
 * ArrayList源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-08-10 16:42
 */

import java.util.*;

/**
 * 定义：
 *  支持泛型，继承AbstractList，而AbstractList实现了List接口，提供List接口的默认实现
 *  ArrayList自身实现了List接口。
 *  同时ArrayList实现了Serializable接口，支持序列化，能够通过序列化传输。
 *  实现RandomAccess接口，支持快速随机访问，实际上是通过下标进行快速访问，
 *  RandomAccess是一个标记接口，接口内没有定义任何内容，实现Cloneable接口能被克隆。
 */
public class TArrayList<E> {

    /**********************************属性***********************************/

    // ArrayList序列版本号
    private static final long serialVersionUID = 8683452581122892189L;

    /**
     * elementData默认长度为10.
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * 空数组，当调用无参数构造函数的时候默认为空数组
     */
    private static final Object[] EMPTY_ELEMENTDATA = {};

    /**
     * Shared empty array instance used for default sized empty instances. We
     * distinguish this from EMPTY_ELEMENTDATA to know how much to inflate when
     * first element is added.
     */
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    /**
     * The array buffer into which the elements of the ArrayList are stored.
     * The capacity of the ArrayList is the length of this array buffer. Any
     * empty ArrayList with elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
     * will be expanded to DEFAULT_CAPACITY when the first element is added.
     *
     * elementData数值被transient修饰，由于实现了Serializable接口，
     * 以此可知ArrayList属性可以通过网络传输，或者被存储到磁盘持久化。
     * 但在有的时候并不希望有些属性被传输或者存储，如一些比较敏感的信息。
     * 使用trainsent标记属性，就可以使ArrayList在序列化的过程中elementData不参与序列化，只存在调用者内存中
     *
     */
    transient Object[] elementData; // non-private to simplify nested class access

    /**
     * The size of the ArrayList (the number of elements it contains).
     *
     * ArrayList中实际数据的数量
     * @serial
     */
    private int size;


    /**********************************构造器***********************************/

    /**
     * Constructs an empty list with the specified initial capacity.
     * 带容量构造函数，默认为空
     *
     * @param  initialCapacity  the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity
     *         is negative
     */
    public TArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initialCapacity);
        }
    }

    /**
     * Constructs an empty list with an initial capacity of ten.
     * 无参构造函数，默认为空
     */
    public TArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     * 将传入的Collection转到elementData
     *
     * @param c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public TArrayList(Collection<? extends E> c) {
        elementData = c.toArray();
        // 如果size为0，则说明没有内容，从而进行初始化
        if ((size = elementData.length) != 0) {
            // c.toArray might (incorrectly) not return Object[] (see 6260652)
            if (elementData.getClass() != Object[].class)
                elementData = Arrays.copyOf(elementData, size, Object[].class);
        } else {
            // replace with empty array.
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }

    public static void getConstructor() {
        List<String> testArrayList = new ArrayList<String>(1);
        List<String> testArrayList1 = new ArrayList<String>();
        Collection testCon = new ArrayList<String>();
        List<String> testArrayList2 = new ArrayList<String>(testCon);

        System.out.println("testArrayList: " + testArrayList.hashCode());
        System.out.println("testArrayList1: " + testArrayList1.hashCode());
        System.out.println("testCon: " + testCon.hashCode());
        System.out.println("testArrayList2: " + testArrayList2.hashCode());

        testArrayList.add("testArrayList--test1");
        testArrayList.add("testArrayList--test2");
        System.out.println(testArrayList.toString());

        testArrayList1.add("testArrayList1--test3");
        testArrayList1.add("testArrayList1--test4");
        System.out.println(testArrayList1.toString());

        testArrayList2.add("testArrayList2--test5");
        testArrayList2.add("testArrayList2--test6");
        System.out.println(testArrayList2.toString());
    }

    /**********************************常用方法***********************************/

    /**
     * Appends the specified element to the end of this list.
     * 确保对象数组elementData有足够的容量，可以将新加入的元素e加到列表尾端
     *
     * @param e element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    public boolean add(E e) {
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        elementData[size++] = e;
        return true;
    }

    private void ensureCapacityInternal(int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        ensureExplicitCapacity(minCapacity);
    }
    private void ensureExplicitCapacity(int minCapacity) {
        //modCount++;
        // 需要满足的最小容量超出elementData的长度时进行扩容
        if (minCapacity - elementData.length > 0){
            grow(minCapacity);
        }
    }

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     *
     * 数值分配容量的最大大小
     *
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        // 将老的长度扩容1.5倍
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        // 扩容长度还小于最小要求长度，则设置扩容长度等于最小要求长度
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        // 最小
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    /**
     * Trims the capacity of this <tt>ArrayList</tt> instance to be the
     * list's current size.  An application can use this operation to minimize
     * the storage of an <tt>ArrayList</tt> instance.
     *
     * 更改list容量，释放空间，类似String.trim()功能
     *
     */
    public void trimToSize() {
        //modCount++;
        if (size < elementData.length) {

            /*elementData = (size == 0)
                    ? EMPTY_ELEMENTDATA
                    : Arrays.copyOf(elementData, size);*/

            // 代码转化
            if (0 == size) {
                elementData = EMPTY_ELEMENTDATA;
            } else {
                elementData = Arrays.copyOf(elementData, size);
            }

        }
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     *
     * 检索某个元素所在的位置，从头开始检索
     *
     */
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     *
     * 检索某个元素所在的位置，从尾开始检索
     */
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size-1; i >= 0; i--)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = size-1; i >= 0; i--)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     *
     * 删除ArrayList内的指定位置的元素,返回删除的元素值
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E remove(int index) {
        /*rangeCheck(index);
        modCount++;*/
        E oldValue = elementData(index);

        int numMoved = size - index - 1;
        if (numMoved > 0)
            // remove之后容量大于0则进行拷贝
            // 源数组、源数组开始位置、目标数组、目标数组起始位置、要复制数组的数量
            System.arraycopy(elementData, index+1, elementData, index,
                    numMoved);
        elementData[--size] = null; // clear to let GC do its work

        return oldValue;
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If the list does not contain the element, it is
     * unchanged.  More formally, removes the element with the lowest index
     * <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists).  Returns <tt>true</tt> if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *
     * 删除ArrayList内的指定元素（先超找元素位置，调用fastRemove）
     *
     * @param o element to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified element
     */
    public boolean remove(Object o) {
        if (o == null) {
            for (int index = 0; index < size; index++)
                if (elementData[index] == null) {
                    fastRemove(index);
                    return true;
                }
        } else {
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index])) {
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }

    /*
     * Private remove method that skips bounds checking and does not
     * return the value removed.
     *
     * 删除指定位置元素
     *
     */
    private void fastRemove(int index) {
        /*modCount++;*/
        int numMoved = size - index - 1;
        if (numMoved > 0)
            // remove之后容量大于0则进行拷贝
            // 源数组、源数组开始位置、目标数组、目标数组起始位置、要复制数组的数量
            System.arraycopy(elementData, index+1, elementData, index,
                    numMoved);
        elementData[--size] = null; // clear to let GC do its work
    }

    E elementData(int index) {
        return (E) elementData[index];
    }


    /**
     * Removes from this list all of the elements whose index is between
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.
     * Shifts any succeeding elements to the left (reduces their index).
     * This call shortens the list by {@code (toIndex - fromIndex)} elements.
     * (If {@code toIndex==fromIndex}, this operation has no effect.)
     *
     * 删除某两个索引范围内的元素
     *
     * @throws IndexOutOfBoundsException if {@code fromIndex} or
     *         {@code toIndex} is out of range
     *         ({@code fromIndex < 0 ||
     *          fromIndex >= size() ||
     *          toIndex > size() ||
     *          toIndex < fromIndex})
     */
    protected void removeRange(int fromIndex, int toIndex) {
        /*modCount++;*/
        int numMoved = size - toIndex;
        System.arraycopy(elementData, toIndex, elementData, fromIndex,
                numMoved);

        // clear to let GC do its work
        int newSize = size - (toIndex-fromIndex);
        for (int i = newSize; i < size; i++) {
            elementData[i] = null;
        }
        size = newSize;
    }



    /**
     * Removes from this list all of its elements that are contained in the
     * specified collection.
     *
     * @param c collection containing elements to be removed from this list
     * @return {@code true} if this list changed as a result of the call
     * @throws ClassCastException if the class of an element of this list
     *         is incompatible with the specified collection
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *         specified collection does not permit null elements
     * (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     *
     *  删除elementData中与指定集合c相同的元素，通过传值给batchRemove的complement来区分（false）
     *
     * @see Collection#contains(Object)
     */
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return batchRemove(c, false);
    }

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection.  In other words, removes from this list all
     * of its elements that are not contained in the specified collection.
     *
     * @param c collection containing elements to be retained in this list
     * @return {@code true} if this list changed as a result of the call
     * @throws ClassCastException if the class of an element of this list
     *         is incompatible with the specified collection
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *         specified collection does not permit null elements
     * (<a href="Collection.html#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     *
     *  保留elementData中与指定集合c相同的元素，删除其他元素，通过传值给batchRemove的complement来区分（true）
     *
     * @see Collection#contains(Object)
     */
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return batchRemove(c, true);
    }

    private boolean batchRemove(Collection<?> c, boolean complement) {
        final Object[] elementData = this.elementData;
        int r = 0, w = 0;
        boolean modified = false;
        try {
            for (; r < size; r++)
                // 传true时保留元素，false时删除元素
                if (c.contains(elementData[r]) == complement)
                    elementData[w++] = elementData[r];
        } finally {
            // Preserve behavioral compatibility with AbstractCollection,
            // even if c.contains() throws.
            if (r != size) {
                System.arraycopy(elementData, r,
                        elementData, w,
                        size - r);
                w += size - r;
            }
            if (w != size) {
                // clear to let GC do its work
                for (int i = w; i < size; i++)
                    elementData[i] = null;
                // modCount += size - w;
                size = w;
                modified = true;
            }
        }
        return modified;
    }


    public static void getMethod() {

        ArrayList<String> trimArrayList = new ArrayList<String>(100);
        trimArrayList.add("test1");
        trimArrayList.add("test2");
        // 减小ArrayList内数组elementData数组容量，释放内存
        trimArrayList.trimToSize();

        // 检索某个元素所在的位置，从头开始检索索引位置
        int testIndexOf = trimArrayList.indexOf("test2");
        System.out.println("testIndexOf: " + testIndexOf);

        // 检索某个元素所在的位置，从头开始检索索引位置
        int testLastIndexOf = trimArrayList.indexOf("test2");
        System.out.println("testLastIndexOf: " + testLastIndexOf);

        // 删除ArrayList内的指定元素, 返回结果
        boolean testremoveFlag = trimArrayList.remove("test1");
        System.out.println("testremoveFlag: " + testremoveFlag);

        // 删除ArrayList内的指定位置的元素,返回删除的元素值
        String testStrRemove = trimArrayList.remove(0);
        System.out.println("testStrRemove: " + testStrRemove);

        // 清空ArrayList
        trimArrayList.clear();
        System.out.println("trimArrayListClear: " + trimArrayList);


        ArrayList<String> trimArrayList2 = new ArrayList<String>(100);
        trimArrayList2.add("test1");
        trimArrayList2.add("test2");
        trimArrayList2.add("test3");

        ArrayList<String> trimArrayList3 = new ArrayList<String>(100);
        trimArrayList3.add("test1");

        /*
        // 移除相同元素
        trimArrayList2.removeAll(trimArrayList3);
        System.out.println("removeAllCollection: " + trimArrayList2);*/

        // 保留相同元素，移除其他元素
        trimArrayList2.retainAll(trimArrayList3);
        System.out.println("retainAllCollection: " + trimArrayList2);
    }


    public static void main(String[] args) {
        // getConstructor();
        getMethod();
    }





}
