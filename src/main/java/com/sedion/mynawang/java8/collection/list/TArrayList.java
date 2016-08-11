package com.sedion.mynawang.java8.collection.list;

/**
 * ArrayList源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-08-10 16:42
 */

import java.util.Arrays;
import java.util.Collection;

/**
 * 定义：
 *  支持泛型，继承AbstractList，而AbstractList实现了List接口，提供List接口的默认实现
 *  ArrayList自身实现了List接口。同时ArrayList实现了Serializable接口，支持序列化，
 *  能够通过序列化传输。实现RandomAccess接口，支持快速随机访问，实际上是通过下标进行快速访问，
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
        //
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
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
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

    E elementData(int index) {
        return (E) elementData[index];
    }

}
