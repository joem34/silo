package com.pb.sawdust.tensor.decorators.id.size;

import com.pb.sawdust.tensor.decorators.size.D1TensorShell;
import com.pb.sawdust.tensor.decorators.size.D0Tensor;
import com.pb.sawdust.tensor.decorators.size.D1Tensor;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.BaseIndex;
import com.pb.sawdust.tensor.alias.vector.id.IdVector;

import java.util.List;
import java.util.Iterator;


/**
 * The {@code IdD1TensorShell} class is a wrapper which sets a {@code Vector} as an {@code IdTensor<I>} (or,
 * more specifically, an {@code IdVector}).
 *
 * @author crf <br/>
 *         Started: Jan 16, 2009 8:53:36 AM
 *         Revised: Dec 14, 2009 12:35:24 PM
 */
public class IdD1TensorShell<T,I> extends D1TensorShell<T> implements IdVector<T,I> {
    private final D1Tensor<T> tensor;
    private final Index<I> index;

    IdD1TensorShell(D1Tensor<T> tensor, Index<I> index) {
        super(tensor);
        this.tensor = tensor;
        this.index = index;
    }  

    /**
     * Constructor which will wrap a tensor, using its current index for the ids. Note that this constructor is
     * inherently unsafe, as there is no way to check that the type of the tensor's index matches the type declared
     * when this constructor is called. Therefore, it should be used with care or in situations where the index type
     * can be verified.
     *
     * @param tensor
     *        The tensor to wrap.
     */
    @SuppressWarnings("unchecked") //warning is duly noted and clearly stated in the documentation
    public IdD1TensorShell(D1Tensor<T> tensor) {
        this(tensor,(Index<I>) tensor.getIndex());
    }

    /**
     * Constructor specifying the tensor to wrap, as well as the ids to use to reference the indices.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @param ids
     *        The ids to reference the indices with.
     *
     * @throws IllegalArgumentException if {@code ids.length != 1}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    @SafeVarargs
    @SuppressWarnings({"unchecked","varargs"})
    public IdD1TensorShell(D1Tensor<T> tensor, I[] ... ids) {
        this(tensor,new BaseIndex<I>(tensor,ids));
    } 

    /**
     * Constructor specifying the tensor to wrap, as well as the ids to use to reference the indices.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @param ids
     *        The ids to reference the indices with.
     *
     * @throws IllegalArgumentException if {@code ids.size() != 1}, if the length of each array in {@code ids} 
     *                                  does not match its respective dimension's size in {@code tensor}, or if any array 
     *                                  in {@code ids} contains repeated elements.
     */
    public IdD1TensorShell(D1Tensor<T> tensor, List<List<I>> ids) {
        this(tensor,new BaseIndex<I>(tensor,ids));
    }

    public Index<I> getIndex() {
        return index;
    }

    public T getValueById(I id) {
        return tensor.getValue(index.getIndex(0,id));
    }

    public void setValueById(T value, I id) {
        tensor.setValue(value,index.getIndex(0,id));
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public T getValueById(I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        return getValueById(ids[0]);
    }

    @SuppressWarnings({"unchecked", "varargs"})
    public void setValueById(T value, I ... ids) {
        TensorImplUtil.checkIndicesLength(this,ids);
        setValueById(value,ids[0]);
    }

    public Iterator<Tensor<T>> iterator() {
        return new Iterator<Tensor<T>>() {
            private final Iterator<Tensor<T>> iterator = IdD1TensorShell.super.iterator();
            
            public boolean hasNext() {
                return iterator.hasNext();
            }

            public Tensor<T> next() {
                return new IdD0TensorShell<T,I>((D0Tensor<T>) iterator.next());
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}