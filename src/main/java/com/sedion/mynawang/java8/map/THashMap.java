package com.sedion.mynawang.java8.map;

/**
 * HashMap源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-08-15 11:33
 */

import javax.swing.tree.TreeNode;
import java.util.*;

/**
 * 定义：
 * HashMap继承AbstractMap，而AbstractMap本身实现了Map接口，提供Map的默认实现。
 * HashMap自身实现了Map接口。Map本身定义了键值的映射关系
 * HashMap实现了Cloneable接口，可以被克隆。
 * HashMap实现了Serializable接口，支持序列化，能够通过序列化传输。
 *
 * http://tech.meituan.com/java-hashmap.html
 *
 * 1.键不可重复，值可重复
 * 2.底层哈希表
 * 3.线程不安全
 * 4.允许key值为null，value也可以为null
 *
 */
public class THashMap<K,V> //extends AbstractMap<K,V> implements Map<K,V>, Cloneable, Serializable
{

    /**********************************属性***********************************/

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
     * 在JDK1.6，JDK1.7中，HashMap采用位桶+链表实现，即使用链表处理冲突，
     * 同一hash值的链表都存储在一个链表里。但是当位于一个桶中的元素较多，
     * 即hash值相等的元素较多时，通过key值依次查找的效率较低。而JDK1.8中，
     * HashMap采用位桶+链表+红黑树实现，当链表长度超过阈值（8）时，将链表转换为红黑树，这样大大减少了查找时间。
     *
     */
    static final int TREEIFY_THRESHOLD = 8;

    /**
     * The bin count threshold for untreeifying a (split) bin during a
     * resize operation. Should be less than TREEIFY_THRESHOLD, and at
     * most 6 to mesh with shrinkage detection under removal.
     * 当桶(bucket)上的结点数小于这个值时树转链表
     */
    static final int UNTREEIFY_THRESHOLD = 6;

    /**
     * The smallest table capacity for which bins may be treeified.
     * (Otherwise the table is resized if too many nodes in a bin.)
     * Should be at least 4 * TREEIFY_THRESHOLD to avoid conflicts
     * between resizing and treeification thresholds.
     * 决定是否转为TreeNode的最小容量
     *
     */
    static final int MIN_TREEIFY_CAPACITY = 64;




    // 内部类
    /**
     * Basic hash bin node, used for most entries.  (See below for
     * TreeNode subclass, and in LinkedHashMap for its Entry subclass.)
     * 哈希桶数组,实现Map.Entry接口
     */
    static class Node<K,V> implements Map.Entry<K,V> {
        // 用来定位数值索引位置
        final int hash;
        final K key;
        V value;
        // 链表的下一个Node
        Node<K,V> next;

        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey()        { return key; }
        public final V getValue()      { return value; }
        public final String toString() { return key + "=" + value; }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                if (Objects.equals(key, e.getKey()) &&
                        Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
    }

    /**
     * The table, initialized on first use, and resized as
     * necessary. When allocated, length is always a power of two.
     * (We also tolerate length zero in some operations to allow
     * bootstrapping mechanics that are currently not needed.)
     * 初始化数组   存储元素的数组，总是2的幂次倍
     *
     */
    transient Node<K,V>[] table;

    /**
     * Holds cached entrySet(). Note that AbstractMap fields are used
     * for keySet() and values().
     * 缓存entrySet() 存放具体元素的集
     *
     */
    transient Set<Map.Entry<K,V>> entrySet;

    /**
     * The number of key-value mappings contained in this map.
     * HashMap的大小（HashMap中实际存在的键值对数量） 不等于数组的长度
     *
     */
    transient int size;

    /**
     * The number of times this HashMap has been structurally modified
     * Structural modifications are those that change the number of mappings in
     * the HashMap or otherwise modify its internal structure (e.g.,
     * rehash).  This field is used to make iterators on Collection-views of
     * the HashMap fail-fast.  (See ConcurrentModificationException).
     * HashMap实例结构改变的次数（例如put新键值对，但是某个key对应的value值被覆盖不属于结构变化）
     *
     */
    transient int modCount;

    /**
     * The next size value at which to resize (capacity * load factor).
     * HashMap所能容纳的最大数据量的Node(键值对)个数 threshold = length * Load factor。
     * 也就是说，在数组定义好长度之后，负载因子越大，所能容纳的键值对个数越多。
     * 临界值 当实际大小(容量*填充因子)超过临界值时，会进行扩容
     * @serial
     */
    // (The javadoc description is true upon serialization.
    // Additionally, if the table array has not been allocated, this
    // field holds the initial array capacity, or zero signifying
    // DEFAULT_INITIAL_CAPACITY.)
    int threshold;

    /**
     * The load factor for the hash table.
     * 加载因子
     *
     * @serial
     */
    final float loadFactor;


    public THashMap(float loadFactor) {
        this.loadFactor = loadFactor;
    }






    /**
     * Computes key.hashCode() and spreads (XORs) higher bits of hash
     * to lower.  Because the table uses power-of-two masking, sets of
     * hashes that vary only in bits above the current mask will
     * always collide. (Among known examples are sets of Float keys
     * holding consecutive whole numbers in small tables.)  So we
     * apply a transform that spreads the impact of higher bits
     * downward. There is a tradeoff between speed, utility, and
     * quality of bit-spreading. Because many common sets of hashes
     * are already reasonably distributed (so don't benefit from
     * spreading), and because we use trees to handle large sets of
     * collisions in bins, we just XOR some shifted bits in the
     * cheapest possible way to reduce systematic lossage, as well as
     * to incorporate impact of the highest bits that would otherwise
     * never be used in index calculations because of table bounds.
     * 计算Hash值
     *
     */
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * Returns a power of two size for the given target capacity.
     * capacity必须满足是2的N次方,如果在构造函数内指定的容量cap不满足,
     * 通过下面的算法将其转换为大于n的最小的2的N次方数
     */
    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }


    /**********************************构造器***********************************/

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and load factor.
     *
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is nonpositive
     */
    public THashMap(int initialCapacity, float loadFactor) {
        // 判断初始化容量是否大于0
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                    initialCapacity);
        // 如果初始化容量大于最大值，则将最大值赋值给初始化容量
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        // 加载因子小于0或者为非数字值则抛出异常
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                    loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and the default load factor (0.75).
     * 初始化容量&&默认加载因子0.75
     *
     * @param  initialCapacity the initial capacity.
     * @throws IllegalArgumentException if the initial capacity is negative.
     */
    public THashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the default initial capacity
     * (16) and the default load factor (0.75).
     */
    public THashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
    }

    /**
     * Constructs a new <tt>HashMap</tt> with the same mappings as the
     * specified <tt>Map</tt>.  The <tt>HashMap</tt> is created with
     * default load factor (0.75) and an initial capacity sufficient to
     * hold the mappings in the specified <tt>Map</tt>.
     *
     * @param   m the map whose mappings are to be placed in this map
     * @throws  NullPointerException if the specified map is null
     */
    public THashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false);
    }

    public static void getConstructor() {
        HashMap<Object, Object> testHashMap = new HashMap<>();
        System.out.println("testHashMap: " + testHashMap);
        HashMap<Object, Object> testHashMap1 = new HashMap<>(10, 0.75F);
        System.out.println("testHashMap1: " + testHashMap1);
        Map<Object, Object> testMap = new HashMap<Object, Object>();
        HashMap<Object, Object> testHashMap2 = new HashMap<Object, Object>(testMap);
        System.out.println("testHashMap2: " + testHashMap2);

    }


    /**********************************常用方法***********************************/




    /**
     * Implements Map.putAll and Map constructor
     *
     * @param m the map
     * @param evict false when initially constructing this map, else
     * true (relayed to method afterNodeInsertion).
     */
    final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {
        int s = m.size();
        if (s > 0) {
            if (table == null) { // pre-size
                float ft = ((float)s / loadFactor) + 1.0F;
                int t = ((ft < (float)MAXIMUM_CAPACITY) ?
                        (int)ft : MAXIMUM_CAPACITY);
                if (t > threshold)
                    threshold = tableSizeFor(t);
            }
            else if (s > threshold)
                resize();
            for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                putVal(hash(key), key, value, false, evict);
            }
        }
    }

    /**
     * Initializes or doubles table size.  If null, allocates in
     * accord with initial capacity target held in field threshold.
     * Otherwise, because we are using power-of-two expansion, the
     * elements from each bin must either stay at same index, or move
     * with a power of two offset in the new table.
     * HashMap扩容（超过最大容量threshold时触发）
     *
     * Java里的数组是无法自动扩容的，方法是使用一个新的数组代替已有的容量小的数组，
     * 就像我们用一个小桶装水，如果想装更多的水，就得换大水桶
     *
     * @return the table
     */

    /*
     * JDK1.7 resize源码
     *
    // 传入新的容量
    void resize(int newCapacity) {
        // 扩容前的table数组
        Entry[] oldTable = table;
        int oldCapacity = oldTable.length;
        // 扩容前的数组大小如果已经达到最大值MAXIMUM_CAPACITY（2^30）
        if (oldCapacity == MAXIMUM_CAPACITY) {
            // 修改阀值为int的最大值（2^31-1）,以后就不会扩容了
            threshold = Integer.MAX_VALUE;
            return;
        }
        // 初始化一个新的Entry数组
        Entry[] newTable = new Entry[newCapacity];
        // 将数值转移到新的Entry的数值里
        transfer(newTable);
        // 原HashMap的table属性引用新的Entry数组
        table = newTable;
        // 修改最大容量阀值
        threshold = (int)(newCapacity * loadFactor);
    }

    void transfer(Entry[] newTable) {
        // 旧的Entry数组
        Entry[] src = table;
        int newCapacity = newTable.length;
        // 遍历旧数值
        for (int j = 0; j < src.length; j++) {
            // 获取旧Entry数组内的每一个旧元素
            Entry<K,V> e = src[j];
            if (e != null) {
                // 释放旧Entry数组的对象引用
                src[j] = null;
                do {
                    Entry<K,V> next = e.next;
                    // 重新计算每个元素在数值中的位置
                    int i = indexFor(e.hash, newCapacity);
                    e.next = newTable[i];
                    // 将元素放入新数值内
                    newTable[i] = e;
                    // 访问下一个Entry链上的元素
                    e = next;
                } while (e != null);
            }
        }
    }
    */


    /**
     * 初始化或者是把table大小加倍，如果为空，则按threshold分配空间，否则，
     * 加倍后，每个容器中的元素在新的table中要么呆在原索引处，要么有一个2的次幂的位移
     * @return
     */
    final Node<K,V>[] resize() {
        // 当前table保存
        Node<K,V>[] oldTab = table;
        // 保存table大小
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        // 保存当前阈值（临界值）
        int oldThr = threshold;
        int newCap, newThr = 0;
        // 之前table大小大于0
        if (oldCap > 0) {
            // 超过最大值就不再扩充了，阈值为最大整形
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            // 没超过最大值，阈值就扩充为原来的2倍
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                    oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold
        }
        // 之前阈值大于0
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        // 计算新的resize上限
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                    (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
        // 初始化table
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        table = newTab;
        // 之前的table已经初始化过
        if (oldTab != null) {
            // 把每个bucket都移动到新的buckets中
            for (int j = 0; j < oldCap; ++j) {
                Node<K,V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    else if (e instanceof TreeNode) {
                        // 红黑树分裂
                        //((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    }else { // preserve order
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        do {
                            next = e.next;
                            // 原索引
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            // 原索引+oldCap
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        // 原索引放到bucket里
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        // 原索引+oldCap放到bucket里
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }

    /**
     * Replaces all linked nodes in bin at index for given hash unless
     * table is too small, in which case resizes instead.
     */
    final void treeifyBin(Node<K,V>[] tab, int hash) {
        int n, index; Node<K,V> e;
        /*
        if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
            resize();
        else if ((e = tab[index = (n - 1) & hash]) != null) {
            TreeNode<K,V> hd = null, tl = null;
            do {
                TreeNode<K,V> p = replacementTreeNode(e, null);
                if (tl == null)
                    hd = p;
                else {
                    p.prev = tl;
                    tl.next = p;
                }
                tl = p;
            } while ((e = e.next) != null);
            if ((tab[index] = hd) != null)
                hd.treeify(tab);
        }
        */
    }




    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * 传入key和value
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    public V put(K key, V value) {
        // 对key的hashcode()做hash
        return putVal(hash(key), key, value, false, true);
    }



    /*
    ①.判断键值对数组table[i]是否为空或为null，否则执行resize()进行扩容；
    ②.根据键值key计算hash值得到插入的数组索引i，如果table[i]==null，直接新建节点添加，转向⑥，如果table[i]不为空，转向③；
    ③.判断table[i]的首个元素是否和key一样，如果相同直接覆盖value，否则转向④，这里的相同指的是hashCode以及equals；
    ④.判断table[i] 是否为treeNode，即table[i] 是否是红黑树，如果是红黑树，则直接在树中插入键值对，否则转向⑤；
    ⑤.遍历table[i]，判断链表长度是否大于8，大于8的话把链表转换为红黑树，在红黑树中执行插入操作，否则进行链表的插入操作；遍历过程中若发现key已经存在直接覆盖value即可；
    ⑥.插入成功后，判断实际存在的键值对数量size是否超多了最大容量threshold，如果超过，进行扩容。
    */
    /**
     * Implements Map.put and related methods
     *
     * @param hash hash for key
     * @param key the key
     * @param value the value to put
     * @param onlyIfAbsent if true, don't change existing value
     * @param evict if false, the table is in creation mode.
     * @return previous value, or null if none
     */
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        // 1.如果当前map中无数据，执行resize方法。并且返回n
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        // 2.计算index索引，并对null做处理（新建节点）
        if ((p = tab[i = (n - 1) & hash]) == null) {
            // tab[i] = newNode(hash, key, value, null);
        } else {
            Node<K,V> e = null; K k;
            // 3.如果节点key存在则直接覆盖value
            if (p.hash == hash &&
                    ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
                // 4.判断该链是否为红黑树
            else if (p instanceof TreeNode) {
                // e = ((TreeNode<K, V>) p).putTreeVal(this, tab, hash, key, value);
            }
            // 5.该链为链表
            else {
                // 遍历链表
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        //p.next = newNode(hash, key, value, null);
                        // 如果链表长度大于8则转换为红黑树进行处理
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            // treeifyBin(tab, hash);
                            break;
                    }
                    // 已经存在直接覆盖value
                    if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                // afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        // 6.超过threshold容量就扩容，threshold = 容量 * 增长因子
        if (++size > threshold)
            resize();
        // afterNodeInsertion(evict);
        return null;
    }

    public static void getMethod() {
        Map<String, String> maptest = new HashMap<>();

        // 映射中的键-值映射关系数
        int mapSize = maptest.size();
        System.out.println("mapSize: " + mapSize);

        // 判断此map是否不包含键-值映射关系
        boolean mapIsEmpty = maptest.isEmpty();
        System.out.println("mapIsEmpty: " + mapIsEmpty);

        // 放入值与键
        maptest.put("testKey1", "testVal1");
        System.out.println("maptest: " + maptest);

        // 返回指定键所映射的值
        String testVal1 = maptest.get("testKey1");
        System.out.println("testVal1: " + testVal1);

        // 判断map中是否包含指定键
        boolean mapContainsKey = maptest.containsKey("testKey1");
        System.out.println("mapContainsKey: " + mapContainsKey);

        // 判断map中是否包含指定值
        boolean mapContainsVlaue = maptest.containsValue("testVal1");
        System.out.println("mapContainsVlaue: " + mapContainsVlaue);

        // 移除指定键的映射关系
        String mapRemove = maptest.remove(1); // "testKey1"
        System.out.println("mapRomeve: " + mapRemove);

        maptest.put("testKey2", "testVal2");
        maptest.put("testKey3", "testVal3");
        maptest.put("testKey4", "testVal4");
        maptest.put("testKey5", "testVal5");
        System.out.println("maptest: " + maptest);

        // 得到map中的所有值
        Collection<String> mapToCollection = maptest.values();
        System.out.println("mapToCollection: " + mapToCollection);

        // 得到map中的所有键值对
        Set<Map.Entry<String,String>> mapToSet = maptest.entrySet();
        System.out.println("mapToSet: " + mapToSet);

        // 从此map中移除所有映射关系。此调用返回后，map将为空。
        maptest.clear();
        System.out.println("maptest: " + maptest);






    }

    // threshold=2*0.75=1,就是说当put第二个key的时候，map就需要进行resize
    private static HashMap<Integer,String> map = new HashMap<Integer,String>(2,0.75f);


    public static void main(String[] args) {
       // getConstructor();
       // getMethod();


        // 线程不安全demo
        map.put(5,"C");
        new Thread("Thread1") {
            public void run() {
                map.put(7, "B");
                System.out.println(map);
            }
        }.start();
        new Thread("Thread2") {
            public void run() {
                map.put(3,"A");
                System.out.println(map);
            }
        }.start();
    }

}
