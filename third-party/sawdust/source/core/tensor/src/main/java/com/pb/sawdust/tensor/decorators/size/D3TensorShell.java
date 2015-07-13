package com.pb.sawdust.tensor.decorators.size;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorImplUtil;



/**
 * The {@code D3TensorShell} provides an basic shell implementation of {@code D3Tensor} which wraps a tensor with {@code 3} dimensions.
 *
 * @author crf <br/>
 *         Started: Oct 24, 2008 10:05:14 PM
 *         Revised: Dec 14, 2009 12:35:26 PM
 */
public class D3TensorShell<T> extends AbstractDnTensorShell<T> implements D3Tensor<T>{

    /**
     * Constructor specifying the tensor to wrap.
     *
     * @param tensor
     *        The 3-dimensional tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor.size{} != 3}.
     */
    public D3TensorShell(Tensor<T> tensor) {
        super(tensor);
    }

    protected int getSize() {
        return 3;
    }

    public T getValue(int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        return getValue(indices[0],indices[1],indices[2]);
    }

    public T getValue(int d0index, int d1index, int d2index) {
        return tensor.getValue(d0index,d1index,d2index);
    }

    public void setValue(T value, int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        setValue(value,indices[0],indices[1],indices[2]);
    }
    
    public void setValue(T value,int d0index, int d1index, int d2index) {
        tensor.setValue(value,d0index,d1index,d2index);
    }
}