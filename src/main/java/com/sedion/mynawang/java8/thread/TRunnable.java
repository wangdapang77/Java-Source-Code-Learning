package com.sedion.mynawang.java8.thread;

/**
 * Runnable源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-09-07 10:32
 *
 * 定义：
 * 设计该接口的目的是为希望在活动时执行代码的对象提供一个公共协议。
 * 如：Thread类实现了Runnable接口。活动时的意思就是一个线程已经启动但还没被停止。
 * Runnable 为非 Thread 子类的类提供了一种激活方式。通过实例化某个 Thread 实例并
 * 将自身作为运行目标，就可以运行实现 Runnable 的类而无需创建 Thread 的子类。大
 * 多数情况下，如果只想重写 run() 方法，而不重写其他 Thread 方法，那么应使用 Runnable 接口
 */
public interface TRunnable {
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see     java.lang.Thread#run()
     *
     * 此处的abstract没作用（接口内声明抽象方法）
     */
    void run();
}
