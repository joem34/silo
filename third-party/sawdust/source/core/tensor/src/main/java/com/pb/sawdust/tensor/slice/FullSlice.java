package com.pb.sawdust.tensor.slice;

import com.pb.sawdust.util.Range;
import com.pb.sawdust.util.collections.cache.Cache;
import com.pb.sawdust.util.collections.cache.BoundedCountCache;

/**
 * The {@code FullSlice} class provides a slice which covers a full dimension. That is, for a dimension of length {@code n},
 * a full slice of that dimension will hold the values from {@code 0} to {@code n-1}. This class is useful for cases
 * where a slice is required, but no changes to a dimension are needed.
 *
 * @author crf <br/>
 *         Started: Feb 8, 2009 12:38:25 PM
 */
public class FullSlice extends BaseSlice {
    private static Cache<Integer,FullSlice> sliceCache = new BoundedCountCache<Integer,FullSlice>(3500);

    /**
     * Factory method to get a full slice which specifies the size of the full slice.
     *
     * @param size
     *        The size of the slice.
     *
     * @return a full slice of length {@code size}.
     *
     * @throws IllegalArgumentException if {@code size} is less than one.
     */
    public static FullSlice fullSlice(int size) {
        if (size < 1)
            throw new IllegalArgumentException("Full slice size must be greater than zero.");
        FullSlice slice = sliceCache.get(size);
        if (slice == null)
            sliceCache.put(size,slice = new FullSlice(size));
        return slice;
    }

    private FullSlice(int size) {
        super(Range.range(size).getRangeArray());
    }

    public String toString() {
        return "FullSlice(size=" + getSize() + ")";
    }
}