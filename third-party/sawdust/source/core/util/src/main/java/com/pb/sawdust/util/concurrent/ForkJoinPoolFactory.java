package com.pb.sawdust.util.concurrent;


import com.pb.sawdust.util.collections.cache.Cache;
import com.pb.sawdust.util.collections.cache.BoundedCache;

import java.util.concurrent.ForkJoinPool;

/**
 * The {@code ForkJoinPoolFactory} class provides a simple factory for getting {@code ForkJoinPool}s.  The factory caches
 * the pools it returns, so that any requests for identically sized pools should generally return the previously constructed
 * pool.  The intention of this class is that it will be used to recycle a handful (at most) of pools; generally speaking,
 * only a few differently sized pools should be needed (depending on the application) and their resources should be shared
 * amongst applications.  The internal caching employed by this class limits its references to too many pool threads so
 * asking for many differently-sized pools can actually lead to an explosion in thread creation and a severe reduction
 * in sharing of resources.
 *
 * @author crf <br/>
 *         Started: Aug 11, 2009 9:40:02 AM
 */
public class ForkJoinPoolFactory {
    private static int MAXIMUM_TOTAL_THREAD_COUNT = Runtime.getRuntime().availableProcessors()*5; //todo: find heuristic for this
    private static int DEFAULT_PARALLELISM = -1;

    private static Cache<Integer,ForkJoinPool> poolCache = new BoundedCache<Integer,ForkJoinPool>(MAXIMUM_TOTAL_THREAD_COUNT) {
        @Override
        protected int getValueSize(ForkJoinPool pool) {
            return pool.getParallelism();
        }

        @Override
        protected int getValueSizeFromKey(Integer key) {
            return key;
        }
    };

    private ForkJoinPoolFactory(){}

    /**
     * Get a fork join pool using a default number of threads (usually number of cores/processors).
     *
     * @return a fork join pool using a default number of threads.
     */
    synchronized public static ForkJoinPool getForkJoinPool() {
        if (DEFAULT_PARALLELISM < 0) {
            ForkJoinPool pool = new ForkJoinPool();
            DEFAULT_PARALLELISM = pool.getParallelism();
            if (!poolCache.containsKey(DEFAULT_PARALLELISM))
                poolCache.put(DEFAULT_PARALLELISM,pool);
        }
        return poolCache.get(DEFAULT_PARALLELISM);
    }

    /**
     * Get a fork join pool using a specified number of threads.
     *
     * @param threadCount
     *        The number of threads used by the pool.
     *
     * @return a fork join pool using {@code threadCount} threads.
     */
    synchronized public static ForkJoinPool getForkJoinPool(int threadCount) {
        if (!poolCache.containsKey(threadCount))
            poolCache.put(threadCount,new ForkJoinPool(threadCount));
        return poolCache.get(threadCount);
    }
}