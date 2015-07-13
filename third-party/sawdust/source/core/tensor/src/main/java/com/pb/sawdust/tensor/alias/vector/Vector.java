package com.pb.sawdust.tensor.alias.vector;

import com.pb.sawdust.tensor.decorators.size.D1Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code Vector} interface provides an alternate name for 1-dimensional tensors.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 9:12:23 AM
 */
public interface Vector<T> extends D1Tensor<T> {  


    /**
     * {@inheritDoc}
     *
     * The tensors this iterator loops over are guaranteed to be {@code Scalar<T>} instances.
     *
     */
    Iterator<Tensor<T>> iterator();
}