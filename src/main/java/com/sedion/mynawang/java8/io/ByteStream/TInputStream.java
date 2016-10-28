package com.sedion.mynawang.java8.io.ByteStream;

import java.io.Closeable;
import java.io.IOException;

/**
 * InputStream源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-10-26 11:59
 */
/**
 *  流的概念：
 *  1、流是字节顺序的抽象概念。
 *  2、文件是数据的静态存储形式、而流是指数据传输时的形态。
 *  3、流类分为两个大类：节点流和过滤流类(也叫处理流类)。
 */

/**
 * 定义：
 * 程序可以从中连续读取字节的对象叫输入流，在java中，用InputStream类来描述所有输入流的抽象概念
 * InputStream是字节输入流、是所有字节输入流的父类、本身是个抽象类
 * InputStream为字节输入流提供一个标准、和基本的方法及简单的实现、子类可以根据自己的特点进行重写和扩展
 * InputStream中有一个抽象方法read()、是字节输入流的核心、要求子类必须实现此方法、此方法也是字节输入流的核心方法
 */
public abstract class TInputStream implements Closeable {

    /**********************************属性***********************************/

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
     * 跳过字节
     * @param n 想从输入流中跳过的字节数
     * @return  实际跳过的字节数（当输入流中没有那么多有效字节被抛弃、此时会跳过剩余所有字节、返回的字节数就是跳之前剩余的字节数）
     * @throws IOException
     */
    public long skip(long n) throws IOException {

        // 记录剩余需要跳过的字节数
        long remaining = n;
        // 记录每次读取的实际字节数
        int nr;


        if (n <= 0) {
            return 0;
        }

        int size = (int)Math.min(MAX_SKIP_BUFFER_SIZE, remaining);
        byte[] skipBuffer = new byte[size];

        // 如果还有字节需要被跳过
        while (remaining > 0) {
            nr = read(skipBuffer, 0, (int)Math.min(size, remaining));
            if (nr < 0) {
                break;
            }
            // 修改记录还要跳过的字节数的值
            remaining -= nr;
        }

        return n - remaining;
    }

    // 返回输入流中可读取的有效的字节数、若子类提供此功能、则需要重新实现。
    public int available() throws IOException {
        return 0;
    }

    // 关闭流，释放所有和流有关的资源
    public void close() throws IOException {}

    // 在输入流中标记当前位置，与reset()结合使用
    public synchronized void mark(int readlimit) {}

    // 与mark结合使用，将此流定位到最后一次mark的位置，即调用reset之后程序继续从mark的位置读取字节流
    public synchronized void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

    // 查看当前流是否支持mark、默认是false（不支持）、即如果实现类不重写此方法就意味着其不支持mark
    public boolean markSupported() {
        return false;
    }




    /**********************************常用方法***********************************/



}
