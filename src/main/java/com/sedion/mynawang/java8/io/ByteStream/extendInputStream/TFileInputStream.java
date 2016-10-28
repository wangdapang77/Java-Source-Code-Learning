package com.sedion.mynawang.java8.io.ByteStream.extendInputStream;


import sun.nio.ch.FileChannelImpl;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * FileInputStream源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-10-26 12:03
 */

/**
 * 定义：
 * FileInputStream为文件输入流，从文件中获得字节流byte
 * 用于读取诸如图像数据之类的原始字节流，若要读取字符流，可考虑使用java.io.FileReader
 */
public class TFileInputStream extends InputStream{

    /**********************************属性***********************************/

    // 文件描述符对象，打开文件句柄
    private final FileDescriptor fd;

    // 引用文件的路径
    private final String path;

    // 文件通道（NIO）
    private FileChannel channel = null;

    private final Object closeLock = new Object();

    private volatile boolean closed = false;


    /**********************************构造器***********************************/

    /**
     * 构造函数
     * @param name 传入文件路径名和文件名
     * @throws FileNotFoundException
     */
    public TFileInputStream(String name) throws FileNotFoundException {
        // 内部是调用File来创造的
        this(name != null ? new File(name) : null);
    }

    /**
     * 构造函数
     * @param file 传入文件
     * @throws FileNotFoundException
     */
    public TFileInputStream(File file) throws FileNotFoundException {
        String name = (file != null ? file.getPath() : null);
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(name);
        }
        if (name == null) {
            throw new NullPointerException();
        }
        /*
        if (file.isInvalid()) {
            throw new FileNotFoundException("Invalid file path");
        }
        */
        fd = new FileDescriptor();
        /*
        fd.attach(this);
        */
        path = name;
        open(name);
    }

    /**
     * 构造函数
     * @param fdObj 传入文件描述符对象
     */
    public TFileInputStream(FileDescriptor fdObj) {
        SecurityManager security = System.getSecurityManager();
        if (fdObj == null) {
            throw new NullPointerException();
        }
        if (security != null) {
            security.checkRead(fdObj);
        }
        fd = fdObj;
        path = null;

        /*
         * FileDescriptor is being shared by streams.
         * Register this stream with FileDescriptor tracker.
         */
        /*
        fd.attach(this);
        */
    }


    /**********************************常用方法***********************************/


    /**
     * 打开文件，调用open0方法
     * @param name 传入文件路径和文件名
     * @throws FileNotFoundException
     */
    private void open(String name) throws FileNotFoundException {
        open0(name);
    }

    /**
     * 打开文件，native方法
     * @param name 传入文件路径和文件名
     * @throws FileNotFoundException
     */
    private native void open0(String name) throws FileNotFoundException;

    /**
     * 读取1个字节的数据(返回ASCII编码对应的十进制数字)
     * @return
     * @throws IOException
     */
    @Override
    public int read() throws IOException {
        return read0();
    }

    /**
     * 读取1个字节的数据，如果没有输入，则该方法阻断（native方法）
     * @return
     * @throws IOException
     */
    private native int read0() throws IOException;

    /**
     * 读取所有的字节到字节数组b中，如果输入不可用，则该方法阻断
     * @param b
     * @return
     * @throws IOException
     */
    public int read(byte b[]) throws IOException {
        return readBytes(b, 0, b.length);
    }

    /**
     * 读取len长度的字节到字节数组b中，如果len不为0并且输入不可用，则该方法阻断，如果为0则返回0
     * @param b
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    public int read(byte b[], int off, int len) throws IOException {
        return readBytes(b, off, len);
    }

    /**
     * 读取指定长度的字节到字节数组b中（native方法）
     * @param b
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    private native int readBytes(byte b[], int off, int len) throws IOException;

    /**
     * 从输入流中跳过并丢弃 n 个字节的数据
     * @param n
     * @return
     * @throws IOException
     */
    public native long skip(long n) throws IOException;

    /**
     * 返回下一次对此输入流调用的方法可以不受阻塞地从此输入流读取（或跳过）的估计剩余字节数。
     * 在某些情况下，非阻塞的读取（或跳过）操作在执行很慢时看起来受阻塞，例如，在网速缓慢的网络上读取大文件时
     * @return
     * @throws IOException
     */
    public native int available() throws IOException;

    /**
     * 关闭流并且释放所有和这个流关联的系统资源
     * @throws IOException
     */
    public void close() throws IOException {
        synchronized (closeLock) {
            if (closed) {
                return;
            }
            closed = true;
        }
        if (channel != null) {
            channel.close();
        }

        /*
        fd.closeAll(new Closeable() {
            public void close() throws IOException {
                close0();
            }
        });
        */
    }

    /**
     * 返回表示到文件系统中实际文件的连接的FileDescriptor对象，该文件正被当前的FileInputStream实例所使用
     * @return
     * @throws IOException
     */
    public final FileDescriptor getFD() throws IOException {
        if (fd != null) {
            return fd;
        }
        throw new IOException();
    }

    /**
     * 获取文件通道，这个通道用于建立文件NIO操作
     * @return
     */
    public FileChannel getChannel() {
        synchronized (this) {
            if (channel == null) {
                channel = FileChannelImpl.open(fd, path, true, false, this);
            }
            return channel;
        }
    }

    private static native void initIDs();

    private native void close0() throws IOException;

    /*
    static {
        initIDs();
    }
    */

    /**
     * 确保没有对象和当前流关联时，调用close()方法来释放资源
     * @throws IOException
     */
    protected void finalize() throws IOException {
        if ((fd != null) &&  (fd != FileDescriptor.in)) {
            /* if fd is shared, the references in FileDescriptor
             * will ensure that finalizer is only called when
             * safe to do so. All references using the fd have
             * become unreachable. We can call close()
             */
            close();
        }
    }




    public static void fileInputStreamMe1() {
        String fileName = "F:\\MynawangBlog\\github\\Java-Source-Code-Learning\\src\\main\\java\\com\\sedion\\mynawang\\java8\\io\\ByteStream\\extendInputStream\\TFileInputStream.java";
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(fileName);
            int read = -1;
            while((read = fileInputStream.read()) != -1 ){
                System.out.print((char)read );
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fileInputStream) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    fileInputStream = null;
                }
            }
        }
    }

    public static void fileInputStreamMe2() {
        String fileName = "F:\\MynawangBlog\\github\\Java-Source-Code-Learning\\src\\main\\java\\com\\sedion\\mynawang\\java8\\io\\ByteStream\\extendInputStream\\TFileInputStream.java";
        FileInputStream fileInputStream = null;
        try {
            File file = new File(fileName);
            fileInputStream = new FileInputStream(file);
            int read = -1;
            while((read = fileInputStream.read()) != -1 ){
                System.out.print((char)read );
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fileInputStream) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    fileInputStream = null;
                }
            }
        }
    }

    public static void main(String args[]) {
        //fileInputStreamMe1();
        fileInputStreamMe2();
    }

}
