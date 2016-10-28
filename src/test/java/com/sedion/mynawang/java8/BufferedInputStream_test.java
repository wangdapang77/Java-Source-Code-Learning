package com.sedion.mynawang.java8;

import java.io.*;

/**
 * 比较FileInputStream复制和加了BufferedInputSream缓冲区之后使用的时间
 * @auther mynawang
 * @create 2016-10-28 15:36
 */
public class BufferedInputStream_test {


    private static File file = new File("E:\\823096_all.sql");
    private static File file_cp = new File("E:\\823096_all_cp.sql");


    // FileInputStream复制
    public static void copy() throws IOException {
        FileInputStream in = new FileInputStream(file);
        FileOutputStream out = new FileOutputStream(file_cp);
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = in.read(buf)) != -1) {
            out.write(buf);
        }
        in.close();
        out.close();
    }
    // BufferedStream复制
    public void copyByBuffer() throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file_cp));
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf);
        }
        in.close();
        out.close();
    }
    public static void main(String[] args) throws IOException {


        BufferedInputStream_test bufferedInputStream_test = new BufferedInputStream_test();
        long time1=System.currentTimeMillis();
        copy();
        long time2=System.currentTimeMillis();
        System.out.println("直接复制用时："+(time2-time1)+"毫秒");

        long time3=System.currentTimeMillis();
        copy();
        long time4=System.currentTimeMillis();

        System.out.println("缓冲区复制用时："+(time4-time3)+"毫秒");
    }


}
