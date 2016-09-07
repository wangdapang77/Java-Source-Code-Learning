package com.sedion.mynawang.java8.thread;

import sun.nio.ch.Interruptible;

import java.security.AccessControlContext;
import java.security.AccessController;

/**
 * Thread源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-09-07 10:10
 */



/**
 * 定义：
 * Thread实现了Runnable接口
 * 每个线程都有一个优先级。线程具有更高的优先级优先执行线程优先级较低
 * 每个线程都可以或不可以也被标记为一个守护线程
 * 有两种方法创建新的执行线程。一种为声明一个类Thread的子类，另一种方式创建一个线程是声明实现Runnable接口的类
 */
public class TThread implements Runnable{

    /**********************************属性***********************************/

    /* Make sure registerNatives is the first thing <clinit> does. */
    // 类加载时执行registerNatives，确保registerNatives方法执行是第一个
    private static native void registerNatives();
    static {
        registerNatives();
    }

    // 线程名称
    private volatile char  name[];
    // 线程的优先级，优先级越高，越优先被执行
    private int            priority;
    private Thread         threadQ;
    private long           eetop;

    /* Whether or not to single_step this thread. */
    private boolean     single_step;

    /* Whether or not the thread is a daemon thread. */
    // 是否为守护线程，当所有非守护线程结束或死亡后，程序将停止
    private boolean     daemon = false;

    /* JVM state */
    private boolean     stillborn = false;

    /* What will be run. */
    // 将执行的Runnable
    private Runnable target;

    /* The group of this thread */
    // 线程的线程组
    private ThreadGroup group;

    /* The context ClassLoader for this thread */
    // 这个线程的类加载器
    private ClassLoader contextClassLoader;

    /* The inherited AccessControlContext of this thread */
    private AccessControlContext inheritedAccessControlContext;

    /* For autonumbering anonymous threads. */
    // 第几个线程，在init初始化线程的时候用来赋给thread.name
    private static int threadInitNumber;
    private static synchronized int nextThreadNum() {
        return threadInitNumber++;
    }



    /* ThreadLocal values pertaining to this thread. This map is maintained
     * by the ThreadLocal class. */

    //ThreadLocal.ThreadLocalMap threadLocals = null;

    /*
     * InheritableThreadLocal values pertaining to this thread. This map is
     * maintained by the InheritableThreadLocal class.
     */

    //ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;

    /*
     * The requested stack size for this thread, or 0 if the creator did
     * not specify a stack size.  It is up to the VM to do whatever it
     * likes with this number; some VMs will ignore it.
     * 这个线程的栈大小
     */
    private long stackSize;

    /*
     * JVM-private state that persists after native thread termination.
     */
    private long nativeParkEventPointer;

    /*
     * Thread ID
     * 这个线程的id
     */
    private long tid;

    /* For generating thread ID */
    private static long threadSeqNumber;

    /* Java thread status for tools,
     * initialized to indicate thread 'not yet started'
     */

    private volatile int threadStatus = 0;


    private static synchronized long nextThreadID() {
        return ++threadSeqNumber;
    }

    /**
     * The argument supplied to the current call to
     * java.util.concurrent.locks.LockSupport.park.
     * Set by (private) java.util.concurrent.locks.LockSupport.setBlocker
     * Accessed using java.util.concurrent.locks.LockSupport.getBlocker
     */
    volatile Object parkBlocker;

    /* The object in which this thread is blocked in an interruptible I/O
     * operation, if any.  The blocker's interrupt method should be invoked
     * after setting this thread's interrupt status.
     */
    private volatile Interruptible blocker;
    private final Object blockerLock = new Object();

    /* Set the blocker field; invoked via sun.misc.SharedSecrets from java.nio code
     */
    void blockedOn(Interruptible b) {
        synchronized (blockerLock) {
            blocker = b;
        }
    }

    /**
     * The minimum priority that a thread can have.
     * 线程优先级为1
     */
    public final static int MIN_PRIORITY = 1;

    /**
     * The default priority that is assigned to a thread.
     * 线程优先级为5，默认的线程优先级
     */
    public final static int NORM_PRIORITY = 5;

    /**
     * The maximum priority that a thread can have.
     * 线程优先级为10，最大的线程优先级
     */
    public final static int MAX_PRIORITY = 10;

    /**
     * Returns a reference to the currently executing thread object.
     *
     * @return  the currently executing thread.
     * 当前线程
     */
    public static native Thread currentThread();

    /**
     * A hint to the scheduler that the current thread is willing to yield
     * its current use of a processor. The scheduler is free to ignore this
     * hint.
     *
     * <p> Yield is a heuristic attempt to improve relative progression
     * between threads that would otherwise over-utilise a CPU. Its use
     * should be combined with detailed profiling and benchmarking to
     * ensure that it actually has the desired effect.
     *
     * <p> It is rarely appropriate to use this method. It may be useful
     * for debugging or testing purposes, where it may help to reproduce
     * bugs due to race conditions. It may also be useful when designing
     * concurrency control constructs such as the ones in the
     * {@link java.util.concurrent.locks} package.
     * 暂停当前正在执行的线程对象，并执行其他线程。只是暂停一下
     *
     */
    public static native void yield();

    /**
     * Causes the currently executing thread to sleep (temporarily cease
     * execution) for the specified number of milliseconds, subject to
     * the precision and accuracy of system timers and schedulers. The thread
     * does not lose ownership of any monitors.
     *
     * @param  millis
     *         the length of time to sleep in milliseconds
     *
     * @throws  IllegalArgumentException
     *          if the value of {@code millis} is negative
     *
     * @throws  InterruptedException
     *          if any thread has interrupted the current thread. The
     *          <i>interrupted status</i> of the current thread is
     *          cleared when this exception is thrown.
     */
    public static native void sleep(long millis) throws InterruptedException;

    /**
     * Causes the currently executing thread to sleep (temporarily cease
     * execution) for the specified number of milliseconds plus the specified
     * number of nanoseconds, subject to the precision and accuracy of system
     * timers and schedulers. The thread does not lose ownership of any
     * monitors.
     *
     * @param  millis
     *         the length of time to sleep in milliseconds
     *
     * @param  nanos
     *         {@code 0-999999} additional nanoseconds to sleep
     *
     * @throws  IllegalArgumentException
     *          if the value of {@code millis} is negative, or the value of
     *          {@code nanos} is not in the range {@code 0-999999}
     *
     * @throws  InterruptedException
     *          if any thread has interrupted the current thread. The
     *          <i>interrupted status</i> of the current thread is
     *          cleared when this exception is thrown.
     */
    public static void sleep(long millis, int nanos)
            throws InterruptedException {
        // 传入毫秒
        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        // 传入纳秒
        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
                    "nanosecond timeout value out of range");
        }

        // 就是纳秒大于500000 ，即大于0.5毫秒的时候四舍五入。或者毫秒为0、纳秒不为0时，休眠一微秒
        if (nanos >= 500000 || (nanos != 0 && millis == 0)) {
            millis++;
        }
        sleep(millis);
    }


    /**
     * Initializes a Thread with the current AccessControlContext.
     * @see #init(ThreadGroup,Runnable,String,long,AccessControlContext)
     */
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize) {
        init(g, target, name, stackSize, null);
    }

    /**
     * Initializes a Thread.
     *
     * @param g the Thread group
     * @param target the object whose run() method gets called
     * @param name the name of the new Thread
     * @param stackSize the desired stack size for the new thread, or
     *        zero to indicate that this parameter is to be ignored.
     * @param acc the AccessControlContext to inherit, or
     *            AccessController.getContext() if null
     * 线程初始化
     */
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize, AccessControlContext acc) {
        // 判断名字是否为空
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        this.name = name.toCharArray();

        // 返回当前执行的线程
        Thread parent = currentThread();
        // 安全管理，查看线程拥有的功能（读写文件，访问网络）
        SecurityManager security = System.getSecurityManager();
        // 判断线程组是否为空
        if (g == null) {
            /* Determine if it's an applet or not */

            /* If there is a security manager, ask the security manager
               what to do. */
            if (security != null) {
                g = security.getThreadGroup();
            }

            /* If the security doesn't have a strong opinion of the matter
               use the parent thread group. */
            if (g == null) {
                g = parent.getThreadGroup();
            }
        }

        /* checkAccess regardless of whether or not threadgroup is
           explicitly passed in. */
        // 确定当前运行的线程是否有权修改此线程组
        g.checkAccess();

        /*
         * Do we have the required permissions?
         */
        if (security != null) {
            // 校验是不是子类
            if (isCCLOverridden(getClass())) {
                // 检查什么权限
                security.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
            }
        }
        // 记录一个线程到线程组里，只是取得一个线程号
        /*g.addUnstarted();*/

        this.group = g;
        // 守护线程赋值
        this.daemon = parent.isDaemon();
        // 线程的优先级
        this.priority = parent.getPriority();
        if (security == null || isCCLOverridden(parent.getClass()))
            // 类加载器赋值，加载类或者资源的东西
            this.contextClassLoader = parent.getContextClassLoader();
        else
            /*this.contextClassLoader = parent.contextClassLoader;*/
        //
        this.inheritedAccessControlContext =
                acc != null ? acc : AccessController.getContext();
        this.target = target;
        // 设置这个线程的优先级
       /* setPriority(priority);*/

        /*if (parent.inheritableThreadLocals != null)
            // 本地线程创建
            this.inheritableThreadLocals =
                    ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
        */

        /* Stash the specified stack size in case the VM cares */
        // 栈赋值
        this.stackSize = stackSize;

        /* Set thread ID */
        // 设置线程id，+1
        tid = nextThreadID();
    }

    /**
     * Verifies that this (possibly subclass) instance can be constructed
     * without violating security constraints: the subclass must not override
     * security-sensitive non-final methods, or else the
     * "enableContextClassLoaderOverride" RuntimePermission is checked.
     */
    private static boolean isCCLOverridden(Class<?> cl) {
       /* if (cl == Thread.class)
            return false;
        processQueue(Caches.subclassAuditsQueue, Caches.subclassAudits);
        WeakClassKey key = new WeakClassKey(cl, Caches.subclassAuditsQueue);
        Boolean result = Caches.subclassAudits.get(key);
        if (result == null) {
            result = Boolean.valueOf(auditSubclass(cl));
            Caches.subclassAudits.putIfAbsent(key, result);
        }

        return result.booleanValue();*/
        return true;
    }

    private static final RuntimePermission SUBCLASS_IMPLEMENTATION_PERMISSION =
            new RuntimePermission("enableContextClassLoaderOverride");
















    @Override
    public void run() {

    }
}
