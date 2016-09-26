package com.sedion.mynawang.java8.map;

/**
 * EnumMap源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-09-13 10:15
 */

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * 定义：
 * EnumMap继承AbstractMap，而AbstractMap本身实现了Map接口，提供Map的默认实现。
 * EnumMap实现了Cloneable接口，可以被克隆。
 * EnumMap实现了Serializable接口，支持序列化，能够通过序列化传输。
 * 特点：
 * 1.EnumMap要求key为枚举（Enum）类型
 * 2.内部使用数组来实现，效率高
 * 3.非线程安全
 * 4.允许value为null，不允许key为null
 * 5.Iterator没有ConcurrentModificationException保护，出现并发问题不容易发现，如数组越界不抛异常
 */
public class TEnumMap<K extends Enum<K>, V> { //extends AbstractMap<K, V> implements java.io.Serializable, Cloneable

    /**********************************属性***********************************/

    /**
     * The <tt>Class</tt> object for the enum type of all the keys of this map.
     *
     * @serial
     * EnumMap的key的类，在keyType中的enumConstants存放key的所有枚举值
     */
    private final Class<K> keyType;

    /**
     * All of the values comprising K.  (Cached for performance.)
     * 存放key的所有枚举值
     */
    private transient K[] keyUniverse;

    /**
     * Array representation of this map.  The ith element is the value
     * to which universe[i] is currently mapped, or null if it isn't
     * mapped to anything, or NULL if it's mapped to null.
     * EnumMap的value值（内部数组）
     */
    private transient Object[] vals;

    /**
     * The number of mappings in this map.
     * map的大小
     */
    private transient int size = 0;

    /**
     * Distinguished non-null value for representing null values.
     * 用非空对象表示null值
     */
    private static final Object NULL = new Object() {
        public int hashCode() {
            return 0;
        }

        public String toString() {
            return "java.util.EnumMap.NULL";
        }
    };

    // 判断对象是否为空，返回相应的数据
    private Object maskNull(Object value) {
        return (value == null ? NULL : value);
    }

    // 判断对象是否为NULL值，返回相应的数据
    @SuppressWarnings("unchecked")
    private V unmaskNull(Object value) {
        return (V)(value == NULL ? null : value);
    }

    // 0个元素的Enum
    private static final Enum<?>[] ZERO_LENGTH_ENUM_ARRAY = new Enum<?>[0];


    /**********************************构造器***********************************/

    /**
     * Creates an empty enum map with the specified key type.
     *
     * @param keyType the class object of the key type for this enum map
     * @throws NullPointerException if <tt>keyType</tt> is null
     *
     * 指定key类型的构造函数
     */
    public TEnumMap(Class<K> keyType) {
        this.keyType = keyType;
//        keyUniverse = getKeyUniverse(keyType);
        vals = new Object[keyUniverse.length];
    }

    /**
     * Creates an enum map with the same key type as the specified enum
     * map, initially containing the same mappings (if any).
     *
     * @param m the enum map from which to initialize this enum map
     * @throws NullPointerException if <tt>m</tt> is null
     * 创建一个EnumMap和指定EnumMap有相同的key类型，用该EnumMap初始化
     */
    public TEnumMap(TEnumMap<K, ? extends V> m) {
//        keyType = m.keyType;
//        keyUniverse = m.keyUniverse;
//        vals = m.vals.clone();
//        size = m.size;
        this.keyType = m.keyType;
    }

    /**
     * Creates an enum map initialized from the specified map.  If the
     * specified map is an <tt>EnumMap</tt> instance, this constructor behaves
     * identically to {@link #//EnumMap(EnumMap)}.  Otherwise, the specified map
     * must contain at least one mapping (in order to determine the new
     * enum map's key type).
     *
     * @param m the map from which to initialize this enum map
     * @throws IllegalArgumentException if <tt>m</tt> is not an
     *     <tt>EnumMap</tt> instance and contains no mappings
     * @throws NullPointerException if <tt>m</tt> is null
     * 利用指定map创建一个EnumMap
     */
    public TEnumMap(Map<K, ? extends V> m) {
//        if (m instanceof EnumMap) {
//            EnumMap<K, ? extends V> em = (EnumMap<K, ? extends V>) m;
//            keyType = em.keyType;
//            keyUniverse = em.keyUniverse;
//            vals = em.vals.clone();
//            size = em.size;
//        } else {
//            if (m.isEmpty())
//                throw new IllegalArgumentException("Specified map is empty");
//            keyType = m.keySet().iterator().next().getDeclaringClass();
//            keyUniverse = getKeyUniverse(keyType);
//            vals = new Object[keyUniverse.length];
//            putAll(m);
//        }
        TEnumMap<K, ? extends V> em = (TEnumMap<K, ? extends V>) m;
        this.keyType = em.keyType;
    }


    enum  testEnum {
        TEST1, TEST2, TEST3
    }

    public static void getConstructor() {

        EnumMap<testEnum, String> enumMap1 = new EnumMap<testEnum, String>(testEnum.class);
        enumMap1.put(testEnum.TEST1, "test1");
        enumMap1.put(testEnum.TEST2, "test2");
        enumMap1.put(testEnum.TEST3, "test3");

        EnumMap<testEnum, String> enumMap2 = new EnumMap<testEnum, String>(enumMap1);
        enumMap2.put(testEnum.TEST1, "test1");
        enumMap2.put(testEnum.TEST2, "test2");
        enumMap2.put(testEnum.TEST3, "test3");

        Map<testEnum, String> map = new HashMap<testEnum, String>();
        map.put(testEnum.TEST1, "test1");
        map.put(testEnum.TEST2, "test2");
        map.put(testEnum.TEST3, "test3");
        EnumMap<testEnum, String> enumMap3 = new EnumMap<testEnum, String>(map);

        System.out.println(enumMap1);
        System.out.println(enumMap2);
        System.out.println(enumMap3);
    }


    /**********************************常用方法***********************************/




    public static void main(String[] args) {
        getConstructor();
    }

}