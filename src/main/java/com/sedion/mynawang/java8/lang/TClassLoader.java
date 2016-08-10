package com.sedion.mynawang.java8.lang;

/**
 * ClassLoader源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-08-08 15:57
 */


/**
 * 定义：
 * 类加载器是一个抽象类
 * 负责加载classes对象，给出类的二进制名称，ClassLoader尝试定位或者产生一个class的数据，
 * 一个典型的策略是把二进制名字转换成文件名然后到文件系统中找到该文件
 */
public abstract class TClassLoader{

    /**
     * Loads the class with the specified <a href="#name">binary name</a>.  The
     * default implementation of this method searches for classes in the
     * following order:
     *
     * <T>使用特殊的二进制name来加载类，这个方法默认的实现方式按以下的顺序查找：<T/>
     *
     * <ol>
     *
     *   <li><p> Invoke {@link #findLoadedClass(String)} to check if the class
     *   has already been loaded.  </p></li>
     *
     *  <T>1.调用findLoadedClass(String)方法校验这个类是否已经被加载过<T/>
     *
     *
     *   <li><p> Invoke the {@link #loadClass(String) <tt>loadClass</tt>} method
     *   on the parent class loader.  If the parent is <tt>null</tt> the class
     *   loader built-in to the virtual machine is used, instead.  </p></li>
     *
     *  <T>使用父加载器调用loadClass(String)方法，如果父加载器为Null，
     *  则类装载器装载虚拟机内置的加载器的findClass(String)方法装载类</T>
     *
     *
     *   <li><p> Invoke the {@link #findClass(String)} method to find the
     *   class.  </p></li>
     *
     * </ol>
     *
     *
     * <p> If the class was found using the above steps, and the
     * <tt>resolve</tt> flag is true, this method will then invoke the {@link
     * #resolveClass(Class)} method on the resulting <tt>Class</tt> object.
     *
     * <T>如果按照上面的步骤找到指定的类，且得到的resolve值为true，
     * 接下去会调用resolveClass(Class)来处理类</T>
     *
     *
     * <p> Subclasses of <tt>ClassLoader</tt> are encouraged to override {@link
     * #findClass(String)}, rather than this method.  </p>
     *
     * <T>ClassLoader的子类最好覆盖findClass(String)，而不是这个方法</T>
     *
     *
     * <p> Unless overridden, this method synchronizes on the result of
     * {@link #getClassLoadingLock <tt>getClassLoadingLock</tt>} method
     * during the entire class loading process.
     *
     * <T>除非被重写，这个方法在整个装载过程中都是用getClassLoadingLock方法保证同步的</T>
     *
     * @param  name
     *         The <a href="#name">binary name</a> of the class
     *
     * @param  resolve
     *         If <tt>true</tt> then resolve the class
     *
     * @return  The resulting <tt>Class</tt> object
     *
     * @throws  ClassNotFoundException
     *          If the class could not be found
     */

    /*
    protected Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException
    {
        synchronized (getClassLoadingLock(name)) {
            //  第一步检查这个类是否已经被加载（）
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();
                try {
                    // parent为父加载类
                    if (parent != null) {
                        // 将搜索类或者资源的任务委托给其父类加载器
                        c = parent.loadClass(name, false);
                    } else {
                        // 检查该类是否被BootstarpClassLoader加载
                        c = findBootstrapClassOrNull(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }

                if (c == null) {
                    // 如果上述步骤任没找到加载的class，则调用findClass方法
                    long t1 = System.nanoTime();
                    c = findClass(name);

                    // this is the defining class loader; record the stats
                    sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    sun.misc.PerfCounter.getFindClasses().increment();
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }*/



//    protected Object getClassLoadingLock(String className) {
//        Object lock = this;
//        if (parallelLockMap != null) {
//            Object newLock = new Object();
//            lock = parallelLockMap.putIfAbsent(className, newLock);
//            if (lock == null) {
//                lock = newLock;
//            }
//        }
//        return lock;
//    }



    /**
     * Returns the class with the given <a href="#name">binary name</a> if this
     * loader has been recorded by the Java virtual machine as an initiating
     * loader of a class with that <a href="#name">binary name</a>.  Otherwise
     * <tt>null</tt> is returned.
     *
     * @param  name
     *         The <a href="#name">binary name</a> of the class
     *
     * @return  The <tt>Class</tt> object, or <tt>null</tt> if the class has
     *          not been loaded
     *
     * @since  1.1
     */

//    protected final Class<?> findLoadedClass(String name) {
//        if (!checkName(name))
//            return null;
//        return findLoadedClass0(name);
//    }
//
//    private native final Class<?> findLoadedClass0(String name);



    /**
     * Returns a class loaded by the bootstrap class loader;
     * or return null if not found.
     */

//    private Class<?> findBootstrapClassOrNull(String name)
//    {
//        if (!checkName(name)) return null;
//
//        return findBootstrapClass(name);
//    }
//
//    private native Class<?> findBootstrapClass(String name);



    /**
     * Finds the class with the specified <a href="#name">binary name</a>.
     * This method should be overridden by class loader implementations that
     * follow the delegation model for loading classes, and will be invoked by
     * the {@link #//loadClass <tt>loadClass</tt>} method after checking the
     * parent class loader for the requested class.  The default implementation
     * throws a <tt>ClassNotFoundException</tt>.
     *
     * @param  name
     *         The <a href="#name">binary name</a> of the class
     *
     * @return  The resulting <tt>Class</tt> object
     *
     * @throws  ClassNotFoundException
     *          If the class could not be found
     *
     * @since  1.2
     */
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        throw new ClassNotFoundException(name);
    }

    private static void test1() {
        ClassLoader appClassLoader = ClassLoader.getSystemClassLoader();
        System.out.println("系统类装载器： " + appClassLoader);

        ClassLoader extensionClassLoader = appClassLoader.getParent();
        System.out.println("系统类装载器的父类加载器-扩展类加载器：" + extensionClassLoader);

        ClassLoader bootstarpClassLoader = extensionClassLoader.getParent();
        System.out.println("扩展类加载器的父类加载器-引导类加载器：" + bootstarpClassLoader);
    }




    public static void getMethod() {
        try {
            Class cls = Class.forName("com.sedion.mynawang.java8.lang.TInteger");
            TInteger t = (TInteger) cls.newInstance();

            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Class cls2 = cl.loadClass("com.sedion.mynawang.java8.lang.TInteger");
            TInteger t2 = (TInteger) cls2.newInstance();

            TInteger t3 = new TInteger();

            System.out.println(t.hashCode());
            System.out.println(t2.hashCode());
            System.out.println(t3.hashCode());

            System.out.println(t == t2);
            System.out.println(t == t3);
            System.out.println(t2 == t3);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        /*test1();*/
        getMethod();
    }



}
