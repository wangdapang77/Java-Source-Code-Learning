package com.sedion.mynawang.java8.net;

import com.sedion.mynawang.java8.lang.TInteger;
import sun.misc.Resource;
import sun.misc.URLClassPath;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.security.*;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.jar.Manifest;

/**
 * URLClassLoader源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-08-10 9:57
 */

/**
 * 定义：
 * 这个类加载器用于指向jar文件和目录的url的搜索路径加载类和资源，这里假设任何用“/”结束的url都是指向目录的。
 * 如果不是以该字符结束，则认为该url指向一个根据需要开单的jar文件。
 *
 * 创建URLClassLoader实例的AccessControlContent线程将在后续加载类和资源时使用
 *
 * 被加载的类默认授权只能访问URLClassLoader创建时指定的URL权限
 *
 */
public class TURLClassLoader {

    /**********************************属性***********************************/

    // 类和资源文件的搜索路径
    private final URLClassPath ucp;

    // 装载时使用的上下文的类和资源
    private final AccessControlContext acc;



    /**********************************构造器***********************************/

    public TURLClassLoader(URLClassPath ucp, AccessControlContext acc) {
        this.ucp = ucp;
        this.acc = acc;
    }


    /**
     *  为给定的URL构造新URLClassLoader。首先在指定的父类加载器中搜索URL，
     *  然后按照为类和资源指定的顺序搜索URL。这里假定任何以“/”结束的URL都是指向目录的。
     *  如果不是以该字符结束，则认为该URL指向一个将根据需要下载和打开的jar文件
     *
     *  如果有安全管理器，该方法首先调用安全管理器checkCreateClassLoader方法以确保允许创建类加载器
     *
     * @param urls 从其位置加载类和资源的URL
     * @param parent 用于托管的父类加载器
     */
    public TURLClassLoader(URL[] urls, ClassLoader parent) {
        // super(parent);
        // this is to make the stack depth consistent with 1.1
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        ucp = new URLClassPath(urls);
        this.acc = AccessController.getContext();
    }

    public TURLClassLoader(URL[] urls) {
        super();
        // this is to make the stack depth consistent with 1.1
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        ucp = new URLClassPath(urls);
        this.acc = AccessController.getContext();
    }

    /**
     *
     * @param urls
     * @param parent
     * @param factory 创建URL时使用的URLStreamHandlerFactory
     */
    public TURLClassLoader(URL[] urls, ClassLoader parent,
                          URLStreamHandlerFactory factory) {
        // super(parent);
        // this is to make the stack depth consistent with 1.1
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkCreateClassLoader();
        }
        ucp = new URLClassPath(urls, factory);
        acc = AccessController.getContext();
    }


    /**********************************常用方法***********************************/


    protected Class<?> findClass(final String name)
            throws ClassNotFoundException
    {
        final Class<?> result;
        try {
            result = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<Class<?>>() {
                        public Class<?> run() throws ClassNotFoundException {
                            String path = name.replace('.', '/').concat(".class");
                            // 1.URLClassPath ucp,帮助获取class文件字节流
                            // URLClassPath会用FileLoader或者JarLoader去加载字节码
                            Resource res = ucp.getResource(path, false);
                            if (res != null) {
                                try {
                                    // 2. defineClass创建类对象，将字节流解析成JVM能够识别的Class对象
                                    return defineClass(name, res);
                                } catch (IOException e) {
                                    throw new ClassNotFoundException(name, e);
                                }
                            } else {
                                return null;
                            }
                        }
                    }, acc);
        } catch (java.security.PrivilegedActionException pae) {
            throw (ClassNotFoundException) pae.getException();
        }
        if (result == null) {
            throw new ClassNotFoundException(name);
        }
        return result;
    }


    /*
    * Defines a Class using the class bytes obtained from the specified
    * Resource. The resulting Class must be resolved before it can be
    * used.
    */
    private Class<?> defineClass(String name, Resource res) throws IOException {
        long t0 = System.nanoTime();
        int i = name.lastIndexOf('.');
        URL url = res.getCodeSourceURL();
        if (i != -1) {
            String pkgname = name.substring(0, i);
            // Check if package already loaded.
            Manifest man = res.getManifest();
           // definePackageInternal(pkgname, man, url);
        }
        // Now read the class bytes and define the class
        java.nio.ByteBuffer bb = res.getByteBuffer();
        if (bb != null) {
            // Use (direct) ByteBuffer:
            CodeSigner[] signers = res.getCodeSigners();
            CodeSource cs = new CodeSource(url, signers);
            sun.misc.PerfCounter.getReadClassBytesTime().addElapsedTimeFrom(t0);
           // return defineClass(name, bb, cs);
        } else {
            byte[] b = res.getBytes();
            // must read certificates AFTER reading bytes.
            CodeSigner[] signers = res.getCodeSigners();
            CodeSource cs = new CodeSource(url, signers);
            sun.misc.PerfCounter.getReadClassBytesTime().addElapsedTimeFrom(t0);
           // return defineClass(name, b, 0, b.length, cs);
        }
        return null;
    }


    /**
     * Finds the resource with the specified name on the URL search path.
     *
     * @param name the name of the resource
     * @return a {@code URL} for the resource, or {@code null}
     * if the resource could not be found, or if the loader is closed.
     */
    public URL findResource(final String name) {
        /*
         * The same restriction to finding classes applies to resources
         */
        URL url = AccessController.doPrivileged(
                new PrivilegedAction<URL>() {
                    public URL run() {
                        return ucp.findResource(name, true);
                    }
                }, acc);

        return url != null ? ucp.checkURL(url) : null;
    }

    /**
     * Returns an Enumeration of URLs representing all of the resources
     * on the URL search path having the specified name.
     *
     * @param name the resource name
     * @exception IOException if an I/O exception occurs
     * @return an {@code Enumeration} of {@code URL}s
     *         If the loader is closed, the Enumeration will be empty.
     */
    public Enumeration<URL> findResources(final String name)
            throws IOException
    {
        final Enumeration<URL> e = ucp.findResources(name, true);

        return new Enumeration<URL>() {
            private URL url = null;

            private boolean next() {
                if (url != null) {
                    return true;
                }
                do {
                    URL u = AccessController.doPrivileged(
                            new PrivilegedAction<URL>() {
                                public URL run() {
                                    if (!e.hasMoreElements())
                                        return null;
                                    return e.nextElement();
                                }
                            }, acc);
                    if (u == null)
                        break;
                    url = ucp.checkURL(u);
                } while (url == null);
                return url != null;
            }

            public URL nextElement() {
                if (!next()) {
                    throw new NoSuchElementException();
                }
                URL u = url;
                url = null;
                return u;
            }

            public boolean hasMoreElements() {
                return next();
            }
        };
    }


    public static void getMethod() {

       /* ClassLoader只能load位于classpath（src目录）下的类；
        而URLClassLoader可以load任意目录下的类！*/

        try {
            Class cls = Class.forName("com.sedion.mynawang.java8.lang.TInteger");
            TInteger t = (TInteger) cls.newInstance();

            URL[] urls = new URL[] {new URL("file://F:\\MynawangBlog\\github\\Java-Source-Code-Learning\\target\\classes\\com\\sedion\\mynawang\\java8\\lang\\") };
            URLClassLoader ucl = new URLClassLoader(urls);
            Class cls2 = ucl.loadClass("com.sedion.mynawang.java8.lang.TInteger");
            TInteger t2 = (TInteger) cls2.newInstance();

            System.out.println(t.hashCode());
            System.out.println(t2.hashCode());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }


    public static void main(String[] args) {
        getMethod();
    }


}
