package com.sedion.mynawang.java8.concurrent;

import java.util.concurrent.RejectedExecutionException;

/**
 * Executor源码阅读和使用（JDK1.8）
 * @auther mynawang
 * @create 2016-09-26 16:53
 */
public interface TExecutor {

    /**
     * Executes the given command at some time in the future.  The command
     * may execute in a new thread, in a pooled thread, or in the calling
     * thread, at the discretion of the {@code Executor} implementation.
     *
     * @param command the runnable task
     * @throws RejectedExecutionException if this task cannot be
     * accepted for execution
     * @throws NullPointerException if command is null
     */
    void execute(Runnable command);
}
