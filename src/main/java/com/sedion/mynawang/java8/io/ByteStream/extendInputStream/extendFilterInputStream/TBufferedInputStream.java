package com.sedion.mynawang.java8.io.ByteStream.extendInputStream.extendFilterInputStream;

import java.io.*;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;


/**
 * BufferedInputStream源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-10-26 14:30
 */

/**
 * 定义：
 * BufferedInputStream是一个带有内部缓冲数组的输入流，该类主要用于装饰其他输入流，
 * 从而为被装饰的输入流（比如FileInputStream）提供缓冲功能，提高输入效率。
 * 该类中最重要的方法是fill()，该方法负责填充缓冲数组。
 */
public class TBufferedInputStream extends FilterInputStream {

    // 缓冲区数组默认大小8192Byte,也就是8K
    /**
     * BufferedInputStream 会根据“缓冲区大小”来逐次的填充缓冲区；
     * BufferedInputStream 填充缓冲区，用户读取缓冲区，
     * 读完之后，BufferedInputStream 会再次填充缓冲区。
     * 如此循环，直到读完数据...
     */
    private static int DEFAULT_BUFFER_SIZE = 8192;

    // 缓冲区最大的大小
    private static int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;

    // 内部缓冲数组，根据需要进行填充
    protected volatile byte buf[];

    private static final
    AtomicReferenceFieldUpdater<BufferedInputStream, byte[]> bufUpdater =
            AtomicReferenceFieldUpdater.newUpdater
                    (BufferedInputStream.class,  byte[].class, "buf");
    /**
     * 缓冲区中还没有读取的字节数。当count=0时，说明缓冲区内容已读完，会再次填充。
     * 注意，这里是指缓冲区的有效字节数，而不是输入流中的有效字节数。
     */
    protected int count;

    /**
     * 缓冲区指针，记录缓冲区当前读取到的位置
     * 注意，这里是指缓冲区的位置索引，而不是输入流中的位置索引。
     */
    protected int pos;

    /**
     * 当前缓冲区的标记位置
     * markpos和reset()配合使用才有意义。操作步骤：
     * 第一步通过mark()函数，保存pos的值到markpos中。
     * 第二步通过reset()函数，会将pos的值重置为markpos。
     * 接着通过read()读取数据时，就会从mark()保存的位置开始读取。
     */
    protected int markpos = -1;

    /**
     * marklimit是标记的最大值。
     * 关于marklimit的原理，在后面的fill()函数分析中说明。这对理解BufferedInputStream相当重要。
     */
    protected int marklimit;

    /**
     * 读取字节，方法内部使用的还是InputStream
     * @return
     * @throws IOException
     */
    private InputStream getInIfOpen() throws IOException {
        InputStream input = in;
        if (input == null)
            throw new IOException("Stream closed");
        return input;
    }

    /**
     * 创建空缓冲区
     * @return
     * @throws IOException
     */
    private byte[] getBufIfOpen() throws IOException {
        byte[] buffer = buf;
        if (buffer == null)
            throw new IOException("Stream closed");
        return buffer;
    }

    /**
     * 构造函数，创建默认缓冲区大小的BufferedInputStream
     * @param in
     */
    public TBufferedInputStream(InputStream in) {
        this(in, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 构造函数，创建自定义大小的缓冲区
     * @param in
     * @param size 缓冲区大小
     */
    public TBufferedInputStream(InputStream in, int size) {
        super(in);
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        buf = new byte[size];
    }

    /**
     * 填充缓冲区数组
     * 阅读blog看懂这个方法：http://www.cnblogs.com/daxin/p/3771293.html
     * @throws IOException
     */
    private void fill() throws IOException {
        byte[] buffer = getBufIfOpen();
        if (markpos < 0)                                                                // 情形1：输入流是否被标记，若被标记，则markpos大于等于0，否则，markpos=-1
            pos = 0;            /* no mark: throw away the buffer */

        else if (pos >= buffer.length)  /* no room left in buffer */                    // buffer中没有多余的空间
            if (markpos > 0) {  /* can throw away early part of the buffer */           // 情形2：缓冲区的标记位置大于0
                int sz = pos - markpos;                                                 // 获取“‘被标记位置’到‘buffer末尾’”的数据长度
                System.arraycopy(buffer, markpos, buffer, 0, sz);                       // 将buffer中从markpos开始的数据”拷贝到buffer中
                pos = sz;                                                               // 将sz赋值给pos，即pos就是“被标记位置”到“buffer末尾”的数据长度
                markpos = 0;                                                            // 标记位置置0
            } else if (buffer.length >= marklimit) {                                    // 情形3：
                markpos = -1;   /* buffer got too big, invalidate mark */               // 取消标记
                pos = 0;        /* drop buffer contents */                              // 设置初始化位置为0
            } else if (buffer.length >= MAX_BUFFER_SIZE) {
                throw new OutOfMemoryError("Required array size too large");
            } else {            /* grow buffer */                                       // 情形4：新建一个数组nbuf，其大小是pos2与marklimit中较小值
                int nsz = (pos <= MAX_BUFFER_SIZE - pos) ?
                        pos * 2 : MAX_BUFFER_SIZE;
                if (nsz > marklimit)
                    nsz = marklimit;
                byte nbuf[] = new byte[nsz];
                System.arraycopy(buffer, 0, nbuf, 0, pos);                              // 将buffer中的数据拷贝到新数组nbuf中
                /*
                if (!bufUpdater.compareAndSet(this, buffer, nbuf)) {
                    // Can't replace buf if there was an async close.
                    // Note: This would need to be changed if fill()
                    // is ever made accessible to multiple threads.
                    // But for now, the only way CAS can fail is via close.
                    // assert buf == null;
                    throw new IOException("Stream closed");
                }
                */
                buffer = nbuf;
            }
        count = pos;
        int n = getInIfOpen().read(buffer, pos, buffer.length - pos);                   // 从输入流中读取出“buffer.length - pos”的数据，然后填充到buffer中
        if (n > 0)
            count = n + pos;                                                            // 根据从输入流中读取的实际数据的多少，来更新buffer中数据的实际大小
    }

    /**
     * 读取下一个字节。
     * 读取下一个字节 与FileInputStream中的read()方法不同的是，这里是从缓冲区数组中读取了一个字节
     * 也就是直接从内存中获取的，效率远高于前者
     * @return
     */
    public synchronized int read() throws IOException {
        // 是否读完buffer中的数据
        if (pos >= count) {
            // 若已经读完缓冲区中的数据，则调用fill()从输入流读取下一部分数据来填充缓冲区
            fill();
            // 若物理数据源也没有多于可读数据，则返回-1，表示EOF
            if (pos >= count)
                return -1;
        }
        // 从缓冲区读取buffer[pos]的字节并返回（由于这里读取的是一个字节，而返回的是整型，所以需要把高位置0）
        return getBufIfOpen()[pos++] & 0xff;
    }

    /**
     * 将缓冲区中的数据写入到字节数组b中。off是字节数组b的起始位置，len是写入长度
     *
     * @param b
     * @param off 起始位置
     * @param len 读取长度
     * @return
     * @throws IOException
     */
    private int read1(byte[] b, int off, int len) throws IOException {
        int avail = count - pos;
        if (avail <= 0) {
            /* If the requested length is at least as large as the buffer, and
               if there is no mark/reset activity, do not bother to copy the
               bytes into the local buffer.  In this way buffered streams will
               cascade harmlessly. */
            // 加速机制。
            // 如果读取的长度大于缓冲区的长度，并且没有markpos， 则直接从原始输入流中进行读取并返回，
            // 从而避免无谓的缓冲区数据填充
            if (len >= getBufIfOpen().length && markpos < 0) {
                return getInIfOpen().read(b, off, len);
            }
            // 若已经读完缓冲区中的数据，则调用fill()从输入流读取下一部分数据来填充缓冲区
            fill();
            avail = count - pos;
            if (avail <= 0) return -1;
        }
        int cnt = (avail < len) ? avail : len;
        System.arraycopy(getBufIfOpen(), pos, b, off, cnt);
        pos += cnt;
        return cnt;
    }

    /**
     * 将缓冲区中的数据写入到字节数组b中。off是字节数组b的起始位置，len是写入长度
     * @param b
     * @param off 起始位置
     * @param len 读取长度
     * @return
     * @throws IOException
     */
    public synchronized int read(byte b[], int off, int len)
            throws IOException
    {
        getBufIfOpen(); // Check for closed stream
        if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        int n = 0;
        for (;;) {
            int nread = read1(b, off + n, len - n);
            if (nread <= 0)
                return (n == 0) ? nread : n;
            n += nread;
            if (n >= len)
                return n;
            // if not closed but no bytes available, return
            InputStream input = in;
            if (input != null && input.available() <= 0)
                return n;
        }
    }

    /**
     * 跳过和丢弃此输入流中数据的 n 个字节。
     */
    public synchronized long skip(long n) throws IOException {
        getBufIfOpen(); // Check for closed stream
        if (n <= 0) {
            return 0;
        }
        long avail = count - pos;

        if (avail <= 0) {
            // If no mark position set then don't keep in buffer
            if (markpos <0)
                return getInIfOpen().skip(n);

            // Fill in buffer to save bytes for reset
            fill();
            avail = count - pos;
            if (avail <= 0)
                return 0;
        }

        long skipped = (avail < n) ? avail : n;
        pos += skipped;
        return skipped;
    }

    /**
     * 返回下一次对此输入流调用的方法可以不受阻塞地从此输入流读取（或跳过）的估计剩余字节数
     * @return 可以不受阻塞地从此输入流中读取（或跳过）的估计字节数
     */
    public synchronized int available() throws IOException {
        int n = count - pos;
        int avail = getInIfOpen().available();
        return n > (Integer.MAX_VALUE - avail)
                ? Integer.MAX_VALUE
                : n + avail;
    }

    /**
     * 标记“缓冲区”中当前位置，设定marklimit值。
     */
    public synchronized void mark(int readlimit) {
        marklimit = readlimit;
        markpos = pos;
    }

    /**
     * 将“缓冲区”中当前位置重置到mark()所标记的位置
     */
    public synchronized void reset() throws IOException {
        getBufIfOpen(); // Cause exception if closed
        if (markpos < 0)
            throw new IOException("Resetting to invalid mark");
        pos = markpos;
    }

    public boolean markSupported() {
        return true;
    }

    /**
     * 关闭输入流
     */
    public void close() throws IOException {
        byte[] buffer;
        while ( (buffer = buf) != null) {
            /*
            if (bufUpdater.compareAndSet(this, buffer, null)) {
                InputStream input = in;
                in = null;
                if (input != null)
                    input.close();
                return;
            }
            */
            // Else retry in case a new buf was CASed in fill()
        }
    }

    // 使用方式查看 BufferedInputStream_test.java测试类


}
