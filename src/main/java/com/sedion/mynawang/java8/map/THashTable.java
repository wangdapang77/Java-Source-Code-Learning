package com.sedion.mynawang.java8.map;

/**
 * Hashtable源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-08-23 21:07
 */


import java.util.*;

/**
 * 定义：
 * HashTable实现了Map接口，继承Dictionary类，在任何一个 Dictionary 对象中，每个键至多与一个值相关联
 * HashTable实现了Cloneable接口，可以被克隆。
 * HashTable实现了Serializable接口，支持序列化，能够通过序列化传输。
 *
 * 1.键不可重复，值可重复
 * 2.底层哈希表
 * 3.线程安全
 * 4.key、value不允许为null
 *
 */
public class THashtable<K,V> {

    /**********************************属性***********************************/

    /**
     * The hash table data.
     * 存储元素的数组
     * Hashtable是采用拉链法实现的，每一个Entry本质上是一个单向链表
     */
    private transient Entry<?,?>[] table;

    /**
     * The total number of entries in the hash table.
     * Hashtable中元素的实际数量
     */
    private transient int count;

    /**
     * The table is rehashed when its size exceeds this threshold.  (The
     * value of this field is (int)(capacity * loadFactor).)
     * 阈值，用于判断是否需要调整Hashtable的容量（threshold = 容量*加载因子）
     *
     * @serial
     */
    private int threshold;

    /**
     * The load factor for the hashtable.
     * 加载因子
     *
     * @serial
     */
    private float loadFactor;

    /**
     * The number of times this Hashtable has been structurally modified
     * Structural modifications are those that change the number of entries in
     * the Hashtable or otherwise modify its internal structure (e.g.,
     * rehash).  This field is used to make iterators on Collection-views of
     * the Hashtable fail-fast.  (See ConcurrentModificationException).
     * Hashtable结构被改变的次数
     *
     */
    private transient int modCount = 0;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 1421746759512286392L;


    /**
     * Hashtable bucket collision list entry
     * Hashtable桶数组,实现Map.Entry接口
     */
    private static class Entry<K,V> implements Map.Entry<K,V> {
        // 用来定位数值索引位置
        final int hash;
        final K key;
        V value;
        // 指向的下一个Entry，即链表的下一个节点
        Entry<K,V> next;

        protected Entry(int hash, K key, V value, Entry<K,V> next) {
            this.hash = hash;
            this.key =  key;
            this.value = value;
            this.next = next;
        }

        @SuppressWarnings("unchecked")
        protected Object clone() {
            return new Entry<>(hash, key, value,
                    (next==null ? null : (Entry<K,V>) next.clone()));
        }

        // Map.Entry Ops

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        // 设置value。若value是null，则抛出空指针异常。
        public V setValue(V value) {
            if (value == null)
                throw new NullPointerException();

            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        // 覆盖equals()方法，判断两个Entry是否相等。
        // 若两个Entry的key和value都相等，则认为它们相等。
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>)o;

            return (key==null ? e.getKey()==null : key.equals(e.getKey())) &&
                    (value==null ? e.getValue()==null : value.equals(e.getValue()));
        }

        public int hashCode() {
            return hash ^ Objects.hashCode(value);
        }

        public String toString() {
            return key.toString()+"="+value.toString();
        }
    }



    /**********************************构造器***********************************/

    /**
     * Constructs a new, empty hashtable with the specified initial
     * capacity and the specified load factor.
     *
     * @param      initialCapacity   the initial capacity of the hashtable.
     * @param      loadFactor        the load factor of the hashtable.
     * @exception  IllegalArgumentException  if the initial capacity is less
     *             than zero, or if the load factor is nonpositive.
     * 初始容量&加载因子
     *
     */
    public THashtable(int initialCapacity, float loadFactor) {
        // 验证初始容量
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initialCapacity);
        // 验证加载因子
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal Load: "+loadFactor);

        if (initialCapacity==0)
            initialCapacity = 1;
        this.loadFactor = loadFactor;
        table = new Entry<?,?>[initialCapacity];
        // 计算阀值
        threshold = (int)Math.min(initialCapacity * loadFactor, MAX_ARRAY_SIZE + 1);
    }

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     * 最大容量
     *
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;


    /**
     * Constructs a new, empty hashtable with the specified initial capacity
     * and default load factor (0.75).
     *
     * @param     initialCapacity   the initial capacity of the hashtable.
     * @exception IllegalArgumentException if the initial capacity is less
     *              than zero.
     * 初始容量
     */
    public THashtable(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    /**
     * Constructs a new, empty hashtable with a default initial capacity (11)
     * and load factor (0.75).
     */
    public THashtable() {
        this(11, 0.75f);
    }

    /**
     * Constructs a new hashtable with the same mappings as the given
     * Map.  The hashtable is created with an initial capacity sufficient to
     * hold the mappings in the given Map and a default load factor (0.75).
     *
     * @param t the map whose mappings are to be placed in this map.
     * @throws NullPointerException if the specified map is null.
     * @since   1.2
     */
    public THashtable(Map<? extends K, ? extends V> t) {
        this(Math.max(2*t.size(), 11), 0.75f);
        // putAll(t);
    }

    public static void getConstructor() {
        Hashtable<Object, Object> testHashtable = new Hashtable<>();
        System.out.println("testHashtable: " + testHashtable);
        Hashtable<Object, Object> testHashtable1 = new Hashtable<>(10, 0.75F);
        System.out.println("testHashtable1: " + testHashtable1);
        Map<Object, Object> testMap = new Hashtable<Object, Object>();
        Hashtable<Object, Object> testHashtable2 = new Hashtable<Object, Object>(testMap);
        System.out.println("testHashtable2: " + testHashtable2);
    }

    /**********************************常用方法***********************************/


    /**
     * Maps the specified <code>key</code> to the specified
     * <code>value</code> in this hashtable. Neither the key nor the
     * value can be <code>null</code>. <p>
     *
     * The value can be retrieved by calling the <code>get</code> method
     * with a key that is equal to the original key.
     *
     * @param      key     the hashtable key
     * @param      value   the value
     * @return     the previous value of the specified key in this hashtable,
     *             or <code>null</code> if it did not have one
     * @exception  NullPointerException  if the key or value is
     *               <code>null</code>
     * @see     Object#equals(Object)
     *
     *  设置key-value值
     *
     */
    public synchronized V put(K key, V value) {
        // Make sure the value is not null
        // 判断value是否为null，抛出空指针
        if (value == null) {
            throw new NullPointerException();
        }
        // Makes sure the key is not already in the hashtable.
        /*
         * 确保key在tab[]是不重复的
         * 处理过程：
         * 1、计算key的hash值，确认在tab[]中的索引位置
         * 2、迭代index索引位置，如果该位置处的链表中存在一个一样的key，则替换其value，返回旧值
         */
        Entry<?,?> tab[] = table;
        // 获取key的hashcode
        int hash = key.hashCode();
        // 确认key的索引
        int index = (hash & 0x7FFFFFFF) % tab.length;
        @SuppressWarnings("unchecked")
        Entry<K,V> entry = (Entry<K,V>)tab[index];
        for(; entry != null ; entry = entry.next) {
            if ((entry.hash == hash) && entry.key.equals(key)) {
                V old = entry.value;
                entry.value = value;
                return old;
            }
        }
        addEntry(hash, key, value, index);
        return null;
    }

    private void addEntry(int hash, K key, V value, int index) {
        modCount++;

        Entry<?,?> tab[] = table;
        //如果容器中的元素数量已经达到阀值，则进行扩容操作
        if (count >= threshold) {
            // Rehash the table if the threshold is exceeded
            //
            rehash();
            tab = table;
            hash = key.hashCode();
            index = (hash & 0x7FFFFFFF) % tab.length;
        }

        // Creates the new entry.
        @SuppressWarnings("unchecked")
        Entry<K,V> e = (Entry<K,V>) tab[index];
        tab[index] = new Entry<>(hash, key, value, e);
        count++;
    }

    /**
     * Increases the capacity of and internally reorganizes this
     * hashtable, in order to accommodate and access its entries more
     * efficiently.  This method is called automatically when the
     * number of keys in the hashtable exceeds this hashtable's capacity
     * and load factor.
     * 调整hashtable的长度，变为原来的2倍+1
     *
     */
    @SuppressWarnings("unchecked")
    protected void rehash() {
        int oldCapacity = table.length;

        // 旧数组赋值给临时变量
        Entry<?,?>[] oldMap = table;

        // overflow-conscious code
        int newCapacity = (oldCapacity << 1) + 1;
        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            if (oldCapacity == MAX_ARRAY_SIZE)
                // Keep running with MAX_ARRAY_SIZE buckets
                return;
            newCapacity = MAX_ARRAY_SIZE;
        }
        // 创建新数组赋值给旧数组
        Entry<?,?>[] newMap = new Entry<?,?>[newCapacity];

        modCount++;
        threshold = (int)Math.min(newCapacity * loadFactor, MAX_ARRAY_SIZE + 1);
        table = newMap;

        // 将旧数组内的元素依次添加到新数组中
        for (int i = oldCapacity ; i-- > 0 ;) {
            for (Entry<K,V> old = (Entry<K,V>)oldMap[i] ; old != null ; ) {
                Entry<K,V> e = old;
                old = old.next;

                int index = (e.hash & 0x7FFFFFFF) % newCapacity;
                e.next = (Entry<K,V>)newMap[index];
                newMap[index] = e;
            }
        }
    }

    public static void getMethod() {
        Map<String, String> tabletest = new Hashtable<>();

        // 映射中的键-值映射关系数
        int tableSize = tabletest.size();
        System.out.println("tableSize: " + tableSize);

        // 判断此table是否不包含键-值映射关系
        boolean tableIsEmpty = tabletest.isEmpty();
        System.out.println("tableIsEmpty: " + tableIsEmpty);

        // 放入值与键
        tabletest.put("testKey1", "testVal1");
        System.out.println("tabletest: " + tabletest);

        // 返回指定键所映射的值
        String testVal1 = tabletest.get("testKey1");
        System.out.println("testVal1: " + testVal1);

        // 判断table中是否包含指定键
        boolean mapContainsKey = tabletest.containsKey("testKey1");
        System.out.println("mapContainsKey: " + mapContainsKey);

        // 判断table中是否包含指定值
        boolean mapContainsVlaue = tabletest.containsValue("testVal1");
        System.out.println("mapContainsVlaue: " + mapContainsVlaue);

        // 移除指定键的映射关系
        String mapRemove = tabletest.remove(1); // "testKey1"
        System.out.println("mapRomeve: " + mapRemove);

        tabletest.put("testKey2", "testVal2");
        tabletest.put("testKey3", "testVal3");
        tabletest.put("testKey4", "testVal4");
        tabletest.put("testKey5", "testVal5");
        System.out.println("tabletest: " + tabletest);

        // 得到table中的所有值
        Collection<String> tableToCollection = tabletest.values();
        System.out.println("tableToCollection: " + tableToCollection);

        // 得到table中的所有键值对
        Set<Map.Entry<String,String>> tableToSet = tabletest.entrySet();
        System.out.println("tableToSet: " + tableToSet);

        // 从此table中移除所有映射关系。此调用返回后，table将为空。
        tabletest.clear();
        System.out.println("tabletest: " + tabletest);

    }

    public static void main(String[] args) {
        // getConstructor();
        getMethod();
    }
}
