package com.pb.sawdust.tensor.decorators.id;

import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code IdTensor} interface is used for matrices whose indices can be referenced by aliases (ids). That is, in
 * each dimension, a set of unique ids can be used instead of integer indices to refer to a tensor location. For
 * example, say a two dimensional, <code>2x3</code> tensor is defined which measures flows from locations "{@code A}"
 * and "{@code B}" to locations "{@code A}","{@code B}", and "{@code C}". Graphically, this tensor could presented as:
 * <pre>
 *     <code>
 *         A   B   C
 *     A   2   4   2
 *     B   2   0   1
 *     </code>
 * </pre>
 * Using the standard tensor interface, flows from from "{@code A}" to "{@code C}" would be accessed by {@code getValue(0,2)}.
 * Using this interface, that call can be replaced with a much more natural and descriptive one: {@code getValueById("A","C")}.  
 * <p>
 * Because all {@code Index<I>} instances have such capabilities built in, the purpose of this interface is to extend these
 * capabilities to the tensor that the index represents.
 *
 * @param <T>
 *        The type held by the tensor.
 *
 * @param <I>
 *        The type of id used to reference dimensional indices.
 *
 * @author crf <br/>
 *         Started: Jan 11, 2009 7:52:08 PM
 */
public interface IdTensor<T,I> extends Tensor<T> {
    Index<I> getIndex();

    /**
     * Get the tensor value at a specified location referenced by index ids.
     *
     * @param ids
     *        The ids specifying the tensor location.
     *
     * @return the value at {@code ids}.
     *
     * @throws IllegalArgumentException if the size of {@code ids} does not equal the number of dimensions in this tensor,
     *                                  or if any of {@code ids} does not correspond to an index in that particular dimension.
     */
    @SuppressWarnings({"unchecked", "varargs"})
    T getValueById(I ... ids);

    /**
     * Set the value of the tensor at a specified location referenced by index ids.
     *
     * @param value
     *        The value to set the location to.
     *
     * @param ids
     *        The ids specifying the tensor location.
     *
     * @throws IllegalArgumentException if the size of {@code ids} does not equal the number of dimensions in this tensor,
     *                                  or if any of {@code ids} does not correspond to an index in that particular dimension.
     */
    @SuppressWarnings({"unchecked", "varargs"})
    void setValueById(T value, I ... ids);

    /**
     * {@inheritDoc}
     *
     * The tensors this iterator loops over are guaranteed to be {@code IdTensor<T,I>} tensors.
     *
     */
    Iterator<Tensor<T>> iterator();
}