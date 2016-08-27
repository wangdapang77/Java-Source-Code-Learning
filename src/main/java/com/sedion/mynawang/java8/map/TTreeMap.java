package com.sedion.mynawang.java8.map;

import java.util.*;

/**
 * TreeMap源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-08-24 10:06
 */

/**
 * 定义：
 * TreeMap 是一个有序的key-value集合，它是通过红黑树实现的。
 * TreeMap 继承于AbstractMap，所以它是一个Map，即一个key-value集合。
 * TreeMap 实现了NavigableMap接口，意味着它支持一系列的导航方法。比如返回有序的key集合。
 * TreeMap 实现了Cloneable接口，意味着它能被克隆。
 * TreeMap 实现了java.io.Serializable接口，意味着它支持序列化。
 */
public class TTreeMap<K,V> {
    /**
     * The comparator used to maintain order in this tree map, or
     * null if it uses the natural ordering of its keys.
     * 比较器，用来给TreeMap排序
     * @serial
     */
    private final Comparator<? super K> comparator;

    /**
     * 红黑树的根节点
     */
    private transient Entry<K,V> root;

    /**
     * The number of entries in the tree
     * 红黑树的节点总数
     */
    private transient int size = 0;

    /**
     * The number of structural modifications to the tree.
     * 结构被修改的次数
     */
    private transient int modCount = 0;

    // Red-black mechanics

    // 红黑树的颜色-红色
    private static final boolean RED   = false;
    // 红黑树的颜色-黑色
    private static final boolean BLACK = true;



    /**
     * Node in the Tree.  Doubles as a means to pass key-value pairs back to
     * user (see Map.Entry).
     * 红黑树的叶子节点，内部类
     */

    static final class Entry<K,V> implements Map.Entry<K,V> {
        // 键
        K key;
        // 值
        V value;
        // 左孩子树
        Entry<K,V> left;
        // 右孩子树
        Entry<K,V> right;
        // 父亲节点
        Entry<K,V> parent;
        // 颜色（默认为黑）
        boolean color = BLACK;

        /**
         * Make a new cell with given key, value, and parent, and with
         * {@code null} child links, and BLACK color.
         */
        Entry(K key, V value, Entry<K,V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }

        /**
         * Returns the key.
         *
         * @return the key
         */
        public K getKey() {
            return key;
        }

        /**
         * Returns the value associated with the key.
         *
         * @return the value associated with the key
         */
        public V getValue() {
            return value;
        }

        /**
         * Replaces the value currently associated with the key with the given
         * value.
         *
         * @return the value associated with the key before this method was
         *         called
         */
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>)o;

            return valEquals(key,e.getKey()) && valEquals(value,e.getValue());
        }

        public int hashCode() {
            int keyHash = (key==null ? 0 : key.hashCode());
            int valueHash = (value==null ? 0 : value.hashCode());
            return keyHash ^ valueHash;
        }

        public String toString() {
            return key + "=" + value;
        }
    }

    /**
     * Test two values for equality.  Differs from o1.equals(o2) only in
     * that it copes with {@code null} o1 properly.
     */
    static final boolean valEquals(Object o1, Object o2) {
        return (o1==null ? o2==null : o1.equals(o2));
    }



    /**
     * Constructs a new, empty tree map, using the natural ordering of its
     * keys.  All keys inserted into the map must implement the {@link
     * Comparable} interface.  Furthermore, all such keys must be
     * <em>mutually comparable</em>: {@code k1.compareTo(k2)} must not throw
     * a {@code ClassCastException} for any keys {@code k1} and
     * {@code k2} in the map.  If the user attempts to put a key into the
     * map that violates this constraint (for example, the user attempts to
     * put a string key into a map whose keys are integers), the
     * {@code put(Object key, Object value)} call will throw a
     * {@code ClassCastException}.
     * 默认构造器
     *
     */
    public TTreeMap() {
        comparator = null;
    }

    /**
     * Constructs a new, empty tree map, ordered according to the given
     * comparator.  All keys inserted into the map must be <em>mutually
     * comparable</em> by the given comparator: {@code comparator.compare(k1,
     * k2)} must not throw a {@code ClassCastException} for any keys
     * {@code k1} and {@code k2} in the map.  If the user attempts to put
     * a key into the map that violates this constraint, the {@code put(Object
     * key, Object value)} call will throw a
     * {@code ClassCastException}.
     *
     * @param comparator the comparator that will be used to order this map.
     *        If {@code null}, the {@linkplain Comparable natural
     *        ordering} of the keys will be used.
     * 给定比较器的构造函数
     *
     */
    public TTreeMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }

    /**
     * Constructs a new tree map containing the same mappings as the given
     * map, ordered according to the <em>natural ordering</em> of its keys.
     * All keys inserted into the new map must implement the {@link
     * Comparable} interface.  Furthermore, all such keys must be
     * <em>mutually comparable</em>: {@code k1.compareTo(k2)} must not throw
     * a {@code ClassCastException} for any keys {@code k1} and
     * {@code k2} in the map.  This method runs in n*log(n) time.
     *
     * @param  m the map whose mappings are to be placed in this map
     * @throws ClassCastException if the keys in m are not {@link Comparable},
     *         or are not mutually comparable
     * @throws NullPointerException if the specified map is null
     * 带map的构造函数，map会成为TreeMap的子集
     *
     */
    public TTreeMap(Map<? extends K, ? extends V> m) {
        comparator = null;
        putAll(m);
    }

    /**
     * Constructs a new tree map containing the same mappings and
     * using the same ordering as the specified sorted map.  This
     * method runs in linear time.
     *
     * @param  m the sorted map whose mappings are to be placed in this map,
     *         and whose comparator is to be used to sort this map
     * @throws NullPointerException if the specified map is null
     * 带sortmap的构造函数，sortmap会成为TreeMap的子集
     *
     */
    public TTreeMap(SortedMap<K, ? extends V> m) {
        comparator = m.comparator();
        try {
            buildFromSorted(m.size(), m.entrySet().iterator(), null, null);
        } catch (java.io.IOException cannotHappen) {
        } catch (ClassNotFoundException cannotHappen) {
        }
    }


    /**
     * Copies all of the mappings from the specified map to this map.
     * These mappings replace any mappings that this map had for any
     * of the keys currently in the specified map.
     *
     * @param  map mappings to be stored in this map
     * @throws ClassCastException if the class of a key or value in
     *         the specified map prevents it from being stored in this map
     * @throws NullPointerException if the specified map is null or
     *         the specified map contains a null key and this map does not
     *         permit null keys
     * 将map中的全部节点添加到TreeMap中
     *
     */
    public void putAll(Map<? extends K, ? extends V> map) {
        // 获取map的大小
        int mapSize = map.size();
        // 如果TreeMap大小不是0，且map大小不是0，且map是已排序的key-value
        if (size==0 && mapSize!=0 && map instanceof SortedMap) {
            Comparator<?> c = ((SortedMap<?,?>)map).comparator();
            // 如果TreeMap和map的比较器相等
            // 则将map的元素全部拷贝到TreeMap中
            if (c == comparator || (c != null && c.equals(comparator))) {
                ++modCount;
                try {
                    buildFromSorted(mapSize, map.entrySet().iterator(),
                            null, null);
                } catch (java.io.IOException cannotHappen) {
                } catch (ClassNotFoundException cannotHappen) {
                }
                return;
            }
        }

        // 调用AbstractMap的putAll(),AbstractMap中的putAll会调用TreeMap中的put
        // super.putAll(map);
    }

    /**
     * Linear time tree building algorithm from sorted data.  Can accept keys
     * and/or values from iterator or stream. This leads to too many
     * parameters, but seems better than alternatives.  The four formats
     * that this method accepts are:
     *
     *    1) An iterator of Map.Entries.  (it != null, defaultVal == null).
     *    2) An iterator of keys.         (it != null, defaultVal != null).
     *    3) A stream of alternating serialized keys and values.
     *                                   (it == null, defaultVal == null).
     *    4) A stream of serialized keys. (it == null, defaultVal != null).
     *
     * It is assumed that the comparator of the TreeMap is already set prior
     * to calling this method.
     *
     * @param size the number of keys (or key-value pairs) to be read from
     *        the iterator or stream
     * @param it If non-null, new entries are created from entries
     *        or keys read from this iterator.
     * @param str If non-null, new entries are created from keys and
     *        possibly values read from this stream in serialized form.
     *        Exactly one of it and str should be non-null.
     * @param defaultVal if non-null, this default value is used for
     *        each value in the map.  If null, each value is read from
     *        iterator or stream, as described above.
     * @throws java.io.IOException propagated from stream reads. This cannot
     *         occur if str is null.
     * @throws ClassNotFoundException propagated from readObject.
     *         This cannot occur if str is null.
     * 根据已经一个排好序的map创建一个TreeMap
     */
    private void buildFromSorted(int size, Iterator<?> it,
                                 java.io.ObjectInputStream str,
                                 V defaultVal)
            throws  java.io.IOException, ClassNotFoundException {
        this.size = size;
        root = buildFromSorted(0, 0, size-1, computeRedLevel(size),
                it, str, defaultVal);
    }

    /**
     * Recursive "helper method" that does the real work of the
     * previous method.  Identically named parameters have
     * identical definitions.  Additional parameters are documented below.
     * It is assumed that the comparator and size fields of the TreeMap are
     * already set prior to calling this method.  (It ignores both fields.)
     *
     * @param level the current level of tree. Initial call should be 0.
     * @param lo the first element index of this subtree. Initial should be 0.
     * map开头元素的索引
     * @param hi the last element index of this subtree.  Initial should be
     *        size-1.
     * map最后一个元素的索引
     * @param redLevel the level at which nodes should be red.
     *        Must be equal to computeRedLevel for tree of this size.
     *
     * 根据已经一个排好序的map创建一个TreeMap
     * 将map中的元素逐个添加到TreeMap中，并返回map的中间元素作为根节点。
     */
    @SuppressWarnings("unchecked")
    private final Entry<K,V> buildFromSorted(int level, int lo, int hi,
                                             int redLevel,
                                             Iterator<?> it,
                                             java.io.ObjectInputStream str,
                                             V defaultVal)
            throws  java.io.IOException, ClassNotFoundException {
        /*
         * Strategy: The root is the middlemost element. To get to it, we
         * have to first recursively construct the entire left subtree,
         * so as to grab all of its elements. We can then proceed with right
         * subtree.
         *
         * The lo and hi arguments are the minimum and maximum
         * indices to pull out of the iterator or stream for current subtree.
         * They are not actually indexed, we just proceed sequentially,
         * ensuring that items are extracted in corresponding order.
         */

        if (hi < lo) return null;

        // 获取中间元素
        int mid = (lo + hi) >>> 1;

        Entry<K,V> left  = null;

        // 若lo小于mid，则递归调用获取(middel的)左孩子。
        if (lo < mid)
            left = buildFromSorted(level+1, lo, mid - 1, redLevel,
                    it, str, defaultVal);

        // extract key and/or value from iterator or stream
        // 获取middle节点对应的key和value
        K key;
        V value;
        if (it != null) {
            if (defaultVal==null) {
                Map.Entry<?,?> entry = (Map.Entry<?,?>)it.next();
                key = (K)entry.getKey();
                value = (V)entry.getValue();
            } else {
                key = (K)it.next();
                value = defaultVal;
            }
        } else { // use stream
            key = (K) str.readObject();
            value = (defaultVal != null ? defaultVal : (V) str.readObject());
        }

        // 创建middle节点
        Entry<K,V> middle =  new Entry<>(key, value, null);

        // color nodes in non-full bottommost level red
        // 若当前节点的深度等于红色节点的深度，则将节点着色为红色
        if (level == redLevel)
            middle.color = RED;

        // 设置middle为left的父亲节点，left为middle的左孩子
        if (left != null) {
            middle.left = left;
            left.parent = middle;
        }

        if (mid < hi) {
            // 递归调用获取middle的右孩子
            Entry<K,V> right = buildFromSorted(level+1, mid+1, hi, redLevel,
                    it, str, defaultVal);
            // 设置middle为right的父亲节点，right为middle的右孩子
            middle.right = right;
            right.parent = middle;
        }

        return middle;
    }

    /**
     * Find the level down to which to assign all nodes BLACK.  This is the
     * last `full' level of the complete binary tree produced by
     * buildTree. The remaining nodes are colored RED. (This makes a `nice'
     * set of color assignments wrt future insertions.) This level number is
     * computed by finding the number of splits needed to reach the zeroeth
     * node.  (The answer is ~lg(N), but in any case must be computed by same
     * quick O(lg(N)) loop.)
     * 计算节点数为sz的最大深度，也是红色节点的深度值
     *
     */
    private static int computeRedLevel(int sz) {
        int level = 0;
        for (int m = sz - 1; m >= 0; m = m / 2 - 1)
            level++;
        return level;
    }



    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     *
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}.
     *         (A {@code null} return can also indicate that the map
     *         previously associated {@code null} with {@code key}.)
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     * 构建排序二叉树，平衡二叉树
     *
     */
    public V put(K key, V value) {
        // 二叉树的当前节点
        Entry<K,V> t = root;
        // 空树，TreeMap中没有元素
        if (t == null) {

            compare(key, key); // type (and possibly null) check
            // 创建新key-value键值对赋值给root
            root = new Entry<>(key, value, null);
            // 容器size=1表示有一个元素存在了
            size = 1;
            // 结构修改次数+1
            modCount++;
            return null;
        }
        // key排序的返回结果
        int cmp;
        // 父节点
        Entry<K,V> parent;
        // split comparator and comparable paths
        // 指定的排序算法
        Comparator<? super K> cpr = comparator;
        // 如果cpr不为空，则用既定的排序算法进行创建TreeMap集合
        if (cpr != null) {
            do {
                // parent指向上次循环后的t
                parent = t;
                // 比较新增节点的key和当前节点的key大小
                cmp = cpr.compare(key, t.key);
                // cmp小于0，表示新增节点的key小于当前节点的key，则以当前节点的左子节点作为新的当前节点
                if (cmp < 0)
                    t = t.left;
                // cmp大于0，表示新增节点的key大于当前节点的key，则以当前节点的右子节点作为新的当前节点
                else if (cmp > 0)
                    t = t.right;
                // cmp返回值等于0，表示两个key相等，则新值覆盖旧值，并且返回新值
                else
                    return t.setValue(value);
            } while (t != null);
        }
        // 如果cpr为空，则采用默认的排序算法进行创建TreeMap集合
        else {
            // key为空报异常
            if (key == null)
                throw new NullPointerException();
            @SuppressWarnings("unchecked")
            // 下面算法和上面一致
            Comparable<? super K> k = (Comparable<? super K>) key;
            do {
                parent = t;
                cmp = k.compareTo(t.key);
                if (cmp < 0)
                    t = t.left;
                else if (cmp > 0)
                    t = t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        // 新增节点当作parent子节点
        Entry<K,V> e = new Entry<>(key, value, parent);
        // 如果新增节点的key小于parent的key，则当做左子节点
        if (cmp < 0)
            parent.left = e;
        // 如果新增节点的key大于parent的key，则当做右子节点
        else
            parent.right = e;
        /*
        *  上面已经完成了排序二叉树的的构建，将新增节点插入该树中的合适位置
        *  下面fixAfterInsertion()方法就是对这棵树进行调整、平衡，具体过程参考上面的五种情况
        */
        fixAfterInsertion(e);
        // TreeMap元素数量 + 1
        size++;
        // TreeMap容器修改次数 + 1
        modCount++;
        return null;
    }

    /** From CLR */
    /*
     * 新增节点后的修复操作【调整二叉树的平衡】，x为新增节点
     */
    private void fixAfterInsertion(Entry<K,V> x) {
        // 新增节点的颜色默认为红色
        x.color = RED;
        // 循环直到x不是根节点且父节点的颜色不是红色【一条路径上不能出现相邻的两个红色结点】
        while (x != null && x != root && x.parent.color == RED) {
            // 如果x的父节点是其父节点的父节点的左节点
            if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
                // 获取x的叔节点
                Entry<K,V> y = rightOf(parentOf(parentOf(x)));
                // 如果x的叔节点为红色
                if (colorOf(y) == RED) {
                    // 设置x的父节点为黑色
                    setColor(parentOf(x), BLACK);
                    // 设置x的叔节点为黑色
                    setColor(y, BLACK);
                    // 设置x的父节点的父节点为红色
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                }
                // 如果x的叔节点为黑色
                else {
                    // 如果x是其父节点的右子树，则进行左旋转
                    if (x == rightOf(parentOf(x))) {
                        // 将x的父节点作为x
                        x = parentOf(x);
                        // 右旋转
                        rotateLeft(x);
                    }
                    // 将x的父节点设为黑色
                    setColor(parentOf(x), BLACK);
                    // 将x的父节点的父节点设为红色
                    setColor(parentOf(parentOf(x)), RED);
                    // 以x的父节点的父节点作为中心进行右旋转
                    rotateRight(parentOf(parentOf(x)));
                }
            }
            // 如果X的父节点（P）是其父节点的父节点（G）的右节点
            else {
                // 获取X的叔节点
                Entry<K,V> y = leftOf(parentOf(parentOf(x)));
                // 如果X的叔节点为红色
                if (colorOf(y) == RED) {
                    // 设置x的父节点为黑色
                    setColor(parentOf(x), BLACK);
                    // 设置x的叔节点为黑色
                    setColor(y, BLACK);
                    // 设置x的父节点的父节点为红色
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                }
                // 如果X的叔节点为黑色
                else {
                    // 如果X节点为其父节点的右子树，则进行左旋转
                    if (x == leftOf(parentOf(x))) {
                        // 将X的父节点作为X
                        x = parentOf(x);
                        // 右旋转
                        rotateRight(x);
                    }
                    // 将X的父节点设置为黑色
                    setColor(parentOf(x), BLACK);
                    // 将X的父节点的父节点设置红色
                    setColor(parentOf(parentOf(x)), RED);
                    // 以X的父节点的父节点为中心右旋转
                    rotateLeft(parentOf(parentOf(x)));
                }
            }
        }
        // 将根节点强制设置为黑色
        root.color = BLACK;
    }


    /**
     * Removes the mapping for this key from this TreeMap if present.
     *
     * @param  key key for which mapping should be removed
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}.
     *         (A {@code null} return can also indicate that the map
     *         previously associated {@code null} with {@code key}.)
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    public V remove(Object key) {
        Entry<K,V> p = getEntry(key);
        if (p == null)
            return null;

        V oldValue = p.value;
        deleteEntry(p);
        return oldValue;
    }

    /**
     * Returns this map's entry for the given key, or {@code null} if the map
     * does not contain an entry for the key.
     *
     * @return this map's entry for the given key, or {@code null} if the map
     *         does not contain an entry for the key
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    final Entry<K,V> getEntry(Object key) {
        // Offload comparator-based version for sake of performance
        if (comparator != null)
            return getEntryUsingComparator(key);
        if (key == null)
            throw new NullPointerException();
        @SuppressWarnings("unchecked")
        Comparable<? super K> k = (Comparable<? super K>) key;
        Entry<K,V> p = root;
        while (p != null) {
            int cmp = k.compareTo(p.key);
            if (cmp < 0)
                p = p.left;
            else if (cmp > 0)
                p = p.right;
            else
                return p;
        }
        return null;
    }

    /**
     * Version of getEntry using comparator. Split off from getEntry
     * for performance. (This is not worth doing for most methods,
     * that are less dependent on comparator performance, but is
     * worthwhile here.)
     */
    final Entry<K,V> getEntryUsingComparator(Object key) {
        @SuppressWarnings("unchecked")
        K k = (K) key;
        Comparator<? super K> cpr = comparator;
        if (cpr != null) {
            Entry<K,V> p = root;
            while (p != null) {
                int cmp = cpr.compare(k, p.key);
                if (cmp < 0)
                    p = p.left;
                else if (cmp > 0)
                    p = p.right;
                else
                    return p;
            }
        }
        return null;
    }

    /**
     * Delete node p, and then rebalance the tree.
     */
    private void deleteEntry(Entry<K,V> p) {
        modCount++;
        size--;

        // If strictly internal, copy successor's element to p and then make p
        // point to successor.
        if (p.left != null && p.right != null) {
            Entry<K,V> s = successor(p);
            p.key = s.key;
            p.value = s.value;
            p = s;
        } // p has 2 children

        // Start fixup at replacement node, if it exists.
        Entry<K,V> replacement = (p.left != null ? p.left : p.right);

        if (replacement != null) {
            // Link replacement to parent
            replacement.parent = p.parent;
            if (p.parent == null)
                root = replacement;
            else if (p == p.parent.left)
                p.parent.left  = replacement;
            else
                p.parent.right = replacement;

            // Null out links so they are OK to use by fixAfterDeletion.
            p.left = p.right = p.parent = null;

            // Fix replacement
            if (p.color == BLACK)
                fixAfterDeletion(replacement);
        } else if (p.parent == null) { // return if we are the only node.
            root = null;
        } else { //  No children. Use self as phantom replacement and unlink.
            if (p.color == BLACK)
                fixAfterDeletion(p);

            if (p.parent != null) {
                if (p == p.parent.left)
                    p.parent.left = null;
                else if (p == p.parent.right)
                    p.parent.right = null;
                p.parent = null;
            }
        }
    }

    /**
     * Returns the successor of the specified Entry, or null if no such.
     */
    static <K,V> TTreeMap.Entry<K,V> successor(Entry<K,V> t) {
        if (t == null)
            return null;
        else if (t.right != null) {
            Entry<K,V> p = t.right;
            while (p.left != null)
                p = p.left;
            return p;
        } else {
            Entry<K,V> p = t.parent;
            Entry<K,V> ch = t;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    /** From CLR */
    private void fixAfterDeletion(Entry<K,V> x) {
        while (x != root && colorOf(x) == BLACK) {
            if (x == leftOf(parentOf(x))) {
                Entry<K,V> sib = rightOf(parentOf(x));

                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateLeft(parentOf(x));
                    sib = rightOf(parentOf(x));
                }

                if (colorOf(leftOf(sib))  == BLACK &&
                        colorOf(rightOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (colorOf(rightOf(sib)) == BLACK) {
                        setColor(leftOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateRight(sib);
                        sib = rightOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(rightOf(sib), BLACK);
                    rotateLeft(parentOf(x));
                    x = root;
                }
            } else { // symmetric
                Entry<K,V> sib = leftOf(parentOf(x));

                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateRight(parentOf(x));
                    sib = leftOf(parentOf(x));
                }

                if (colorOf(rightOf(sib)) == BLACK &&
                        colorOf(leftOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (colorOf(leftOf(sib)) == BLACK) {
                        setColor(rightOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateLeft(sib);
                        sib = leftOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(leftOf(sib), BLACK);
                    rotateRight(parentOf(x));
                    x = root;
                }
            }
        }

        setColor(x, BLACK);
    }




    final int compare(Object k1, Object k2) {
        return comparator==null ? ((Comparable<? super K>)k1).compareTo((K)k2)
                : comparator.compare((K)k1, (K)k2);
    }


    private static <K,V> boolean colorOf(Entry<K,V> p) {
        return (p == null ? BLACK : p.color);
    }

    private static <K,V> Entry<K,V> parentOf(Entry<K,V> p) {
        return (p == null ? null: p.parent);
    }

    private static <K,V> void setColor(Entry<K,V> p, boolean c) {
        if (p != null)
            p.color = c;
    }

    private static <K,V> Entry<K,V> leftOf(Entry<K,V> p) {
        return (p == null) ? null: p.left;
    }

    private static <K,V> Entry<K,V> rightOf(Entry<K,V> p) {
        return (p == null) ? null: p.right;
    }

    /** From CLR */
    private void rotateLeft(Entry<K,V> p) {
        if (p != null) {
            Entry<K,V> r = p.right;
            p.right = r.left;
            if (r.left != null)
                r.left.parent = p;
            r.parent = p.parent;
            if (p.parent == null)
                root = r;
            else if (p.parent.left == p)
                p.parent.left = r;
            else
                p.parent.right = r;
            r.left = p;
            p.parent = r;
        }
    }

    /** From CLR */
    private void rotateRight(Entry<K,V> p) {
        if (p != null) {
            Entry<K,V> l = p.left;
            p.left = l.right;
            if (l.right != null) l.right.parent = p;
            l.parent = p.parent;
            if (p.parent == null)
                root = l;
            else if (p.parent.right == p)
                p.parent.right = l;
            else p.parent.left = l;
            l.right = p;
            p.parent = l;
        }
    }


}
