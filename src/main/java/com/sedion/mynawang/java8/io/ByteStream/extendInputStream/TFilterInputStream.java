package com.sedion.mynawang.java8.io.ByteStream.extendInputStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * FilterInputStream源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-10-26 14:29
 */

/**
 * 定义：
 * FileterInputStream 过滤器字节输入流，本身什么事都没有做，只是简单的重写InputStream的所有方法。
 */
public class TFilterInputStream extends InputStream{

    /**
     * 接收构造函数传递进来的 InputStream具体实现类。
     */
    protected volatile InputStream in;

    /**
     * 根据传入的InputStream具体实现类创建FilterInputStream，
     * 并将此实现类赋给全局变量in，方便重写此实现类从InputStream继承的或重写的所有方法。
     */
    protected TFilterInputStream(InputStream in) {
        this.in = in;
    }

    /**
     * 具体为InputStream实现类的read()方法，若子类自己实现的则是子类的，
     * 若子类没有实现则用的是InputStream本身的方法。（详细实现解释看TInputStream源码）
     */
    public int read() throws IOException {
        return in.read();
    }

    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte b[], int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    public int available() throws IOException {
        return in.available();
    }

    public void close() throws IOException {
        in.close();
    }

    public synchronized void mark(int readlimit) {
        in.mark(readlimit);
    }

    public synchronized void reset() throws IOException {
        in.reset();
    }

    public boolean markSupported() {
        return in.markSupported();
    }

}
