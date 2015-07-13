package com.pb.sawdust.util.collections.cache;

import com.pb.sawdust.util.collections.SetDeque;
import com.pb.sawdust.util.collections.LinkedSetList;

import java.util.Map;
import java.util.HashMap;

/**
 * The {@code BoundedCache} is a cache which is limited by the sum of its values's sizes. The size of the values is
 * defined by the {@code getValueSize(V)} method. If the maxiumum size of the cache is reached, then any new elements
 * added will cause the keys with least recent reference (and its value) to be dropped until the total size of all of
 * the values are below the cache limit. Key referencing is defined in the documentation for {@code AbstractRefCache}.
 *
 * @param <K>
 *        The type of the keys held by this cache.
 *
 * @param <V>
 *        The type of the values held by this cache.
 *
 * @author crf <br/>
 *         Started: Aug 11, 2009 9:59:29 AM
 */
public abstract class BoundedCache<K,V>  extends AbstractRefCache<K,V> {
    private final int limit;
    private int currentCount = 0;

    /**
     * Constructor specifying the cache limit and cleaning priority.
     *
     * @param cacheLimit
     *        The bounding limit for this cache.
     *
     * @param priority
     *        The cleaning priority that will be used for this cache.
     */
    public BoundedCache(int cacheLimit, AbstractRefCache.CacheCleaningPriority priority) {
        super(priority);
        limit = cacheLimit;
    }

    /**
     * Constructor specifying the cache limit and using a {@link AbstractRefCache.CacheCleaningPriority#KEY_REFERENCE}
     * cache cleaning priority.
     *
     * @param cacheLimit
     *        The bounding limit for this cache.
     */
    public BoundedCache(int cacheLimit) {
        super();
        limit = cacheLimit;
    }

    /**
     * Get the size of a given value. This size is the amount the value counts against against this cache's bounding limit.
     *
     * @param value
     *        The value.
     *
     * @return the amount the value counts against this cache's bounding limit.
     */
    abstract protected int getValueSize(V value);

    /**
     * Get the size of a given key's value. This size is the amount the key's value counts against against this cache's
     * bounding limit. This is (and only should be) called after {@code key} (and its corresponding value) has been put
     * into the cache. This method is called when cleaning the cache, and by default returns the following:
     * <pre><code>
     *     getValueSize(get(key));
     * </code></pre>
     * In some cases, it may be possible to avoid looking up the value to get the value's size; in such cases, this
     * method can be overridden to avoid the performance penalty of the cache lookup.
     *
     * @param key
     *        The key.
     *
     * @return the amount {@code key}'s corresponding value counts against the bounding limit.
     */
    protected int getValueSizeFromKey(K key) {
        return getValueSize(get(key));
    }

    //not safe for concurrent access (not that AbstractRefCache is either) - be careful
    public V put(K key, V value) {
        int size = getValueSize(value);
        if (size >= limit)
            throw new IllegalArgumentException("Cache limit (" + limit + ") exceeded by single value (" + size + "): " + value);
        currentCount += size;
        return super.put(key,value);
    }

    protected K clipCache(SetDeque<K> keys) {
        if (currentCount >= limit) {
            K key = keys.removeLast();
            currentCount -= getValueSizeFromKey(key);
            return keys.removeLast();
        }
        return null;
    }
}