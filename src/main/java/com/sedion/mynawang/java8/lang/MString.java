package com.sedion.mynawang.java8.lang;

/**
 * String源码阅读（JDK1.8）
 * @auther mynawang
 * @create 2016-07-27 23:44
 */


import java.io.ObjectStreamField;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * 定义：
 * 实现java.io.Serializable、Comparable<String>、CharSequence接口，类是final修饰的，所以不能被修改不能被继承
 */
public final class MString implements java.io.Serializable, Comparable<MString>, CharSequence {

    /**********************************属性***********************************/

    // 字符数组，用于存储字符串内容
    private final char value[];

    // 缓存字符串的hash值，默认为0
    private int hash;

    // 申明序列化的UID
    private static final long serialVersionUID = -6849794470754667710L;

    // 实现了Serializable接口，支持序列化和反序列化
    /*(Java序列化机制是通过在运行时判断类的serialVersionUID来验证版本的一致性。
    在进行反序列化时JVM会把传来的字节的serialVersionUID与本地相应实体类的serialVersionUID进行比较，
    如果相同就认为是一致的，可以进行序列化，否则就会出现序列化版本不一致的异常报InvalidCastException)*/
    private static final ObjectStreamField[] serialPersistentFields =
            new ObjectStreamField[0];


    /**********************************构造方法***********************************/

    public MString() {
        this.value = "".value;
    }

    public MString(MString original) {
        this.value = original.value;
        this.hash = original.hash;
    }

    public MString(char value[]) {
        this.value = Arrays.copyOf(value, value.length);
    }

    public MString(char value[], int offset, int count) {
        if (offset < 0) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        if (count <= 0) {
            if (count < 0) {
                throw new StringIndexOutOfBoundsException(count);
            }
            if (offset <= value.length) {
                this.value = "".value;
                return;
            }
        }
        // Note: offset or count might be near -1>>>1.
        if (offset > value.length - count) {
            throw new StringIndexOutOfBoundsException(offset + count);
        }
        this.value = Arrays.copyOfRange(value, offset, offset+count);
    }

    public MString(int[] codePoints, int offset, int count) {
        if (offset < 0) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        if (count <= 0) {
            if (count < 0) {
                throw new StringIndexOutOfBoundsException(count);
            }
            if (offset <= codePoints.length) {
                this.value = "".value;
                return;
            }
        }
        // Note: offset or count might be near -1>>>1.
        if (offset > codePoints.length - count) {
            throw new StringIndexOutOfBoundsException(offset + count);
        }

        final int end = offset + count;

        // Pass 1: Compute precise size of char[]
        int n = count;
        for (int i = offset; i < end; i++) {
            int c = codePoints[i];
            if (Character.isBmpCodePoint(c))
                continue;
            else if (Character.isValidCodePoint(c))
                n++;
            else throw new IllegalArgumentException(Integer.toString(c));
        }

        // Pass 2: Allocate and fill in char[]
        final char[] v = new char[n];

        for (int i = offset, j = 0; i < end; i++, j++) {
            int c = codePoints[i];
            if (Character.isBmpCodePoint(c))
                v[j] = (char)c;
            else
                Character.toSurrogates(c, v, j++);
        }

        this.value = v;
    }

    public MString(byte ascii[], int hibyte, int offset, int count) {
        checkBounds(ascii, offset, count);
        char value[] = new char[count];

        if (hibyte == 0) {
            for (int i = count; i-- > 0;) {
                value[i] = (char)(ascii[i + offset] & 0xff);
            }
        } else {
            hibyte <<= 8;
            for (int i = count; i-- > 0;) {
                value[i] = (char)(hibyte | (ascii[i + offset] & 0xff));
            }
        }
        this.value = value;
    }

    @Deprecated
    public MString(byte ascii[], int hibyte) {
        this(ascii, hibyte, 0, ascii.length);
    }

    private static void checkBounds(byte[] bytes, int offset, int length) {
        if (length < 0)
            throw new StringIndexOutOfBoundsException(length);
        if (offset < 0)
            throw new StringIndexOutOfBoundsException(offset);
        if (offset > bytes.length - length)
            throw new StringIndexOutOfBoundsException(offset + length);
    }

    public MString(byte bytes[], int offset, int length, String charsetName)
            throws UnsupportedEncodingException {
        if (charsetName == null)
            throw new NullPointerException("charsetName");
        checkBounds(bytes, offset, length);
        this.value = StringCoding.decode(charsetName, bytes, offset, length);
    }




    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int index) {
        return 0;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return null;
    }

    @Override
    public int compareTo(MString o) {
        return 0;
    }




}
