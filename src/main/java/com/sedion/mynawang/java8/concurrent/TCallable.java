package com.sedion.mynawang.java8.concurrent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Callable源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-09-26 14:25
 *
 *
 * 定义：
 * Callable接口代表一段可以调用并返回结果的代码，用于产生结果和Runnable接口类似
 */
public interface TCallable<V> {

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     *
     * 计算结果（产生）
     */
    V call() throws Exception;


    /**
     * Runnable和Callable的比较
     * 1.Runnable从Java1.1开始就有了，Callable从1.5之后才加上去
     * 2.Runnable规定的方法是run(),Callable规定的方法是call()
     * 3.Runnable的任务执行后不能放回值(void),而Callable的任务执行后可返回值
     * 4.run方法不可以抛出异常，call方法可以抛出异常
     */


    class TestCallable implements Callable{

        private String word;

        public TestCallable(String word) {
            this.word = word;
        }

        @Override
        public Object call() throws Exception {
            return Integer.valueOf(word.length());
        }
    }

    static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(3);

        Set<Future<Integer>> set = new HashSet<Future<Integer>>();

        for (String word : args) {
            Callable<Integer> callable = new TestCallable(word);
            Future<Integer> future = pool.submit(callable);
            set.add(future);
        }

        int sum = 0;
        for (Future<Integer> future : set) {
            sum += future.get();
        }

        // 输入
        System.out.println("The sum of length is: " + sum);
        System.exit(sum);
    }

}
