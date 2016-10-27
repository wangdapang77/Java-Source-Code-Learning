package com.sedion.mynawang.java8.io.ByteStream;

import java.io.Closeable;
import java.io.IOException;

/**
 * Queue源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-10-26 11:59
 */

/**
 * 定义：
 * InputStream是字节输入流、是所有字节输入流的父类、本身是个抽象类
 * InputStream为字节输入流提供一个标准、和基本的方法及简单的实现、子类可以根据自己的特点进行重写和扩展
 * InputStream中有一个抽象方法read()、是字节输入流的核心、要求子类必须实现此方法、此方法也是字节输入流的核心方法
 */
public abstract class TInputStream implements Closeable {

    // 当执行skip时，能跳过的缓冲区字节最大的值
    private static final int MAX_SKIP_BUFFER_SIZE = 2048;

    /*
     * 从输入流中读取数据的下一个字节，以int返回
     * @return 0 到 255 之间的整数。如果读到流的结尾则返回 -1
     */
    public abstract int read() throws IOException;

    /**
     * 调用read(byte[] b, 0, b.length)
     * @param b 作为一个缓存字节数组来存放从流中读取的字节
     * @return 0 到 255 之间的整数。如果读到流的结尾则返回 -1
     * @throws IOException
     */
    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * 调用read()方法来读取流中的字节、存放到b 中
     * @param b  用来存放读取的字节的字节缓存数组
     * @param off 从b[off]开始放
     * @param len 放len个
     * @return
     * @throws IOException
     */
    public int read(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        int c = read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte)c;

        int i = 1;
        try {
            for (; i < len ; i++) {
                c = read();
                if (c == -1) {
                    break;
                }
                b[off + i] = (byte)c;
            }
        } catch (IOException ee) {
        }
        return i;
    }

    /**
     *
     * @param n
     * @return
     * @throws IOException
     */
    public long skip(long n) throws IOException {

        long remaining = n;
        int nr;

        if (n <= 0) {
            return 0;
        }

        int size = (int)Math.min(MAX_SKIP_BUFFER_SIZE, remaining);
        byte[] skipBuffer = new byte[size];
        while (remaining > 0) {
            nr = read(skipBuffer, 0, (int)Math.min(size, remaining));
            if (nr < 0) {
                break;
            }
            remaining -= nr;
        }

        return n - remaining;
    }

    public int available() throws IOException {
        return 0;
    }

    public void close() throws IOException {}

    public synchronized void mark(int readlimit) {}

    public synchronized void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

    public boolean markSupported() {
        return false;
    }
    //InputStream

}
