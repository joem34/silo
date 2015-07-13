package com.pb.sawdust.tensor.alias.scalar.impl;

import com.pb.sawdust.tensor.decorators.primitive.AbstractShortTensor;
import com.pb.sawdust.tensor.decorators.primitive.size.AbstractShortD0Tensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdShortTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;

/**
 * The {@code ShortScalarImpl} class provides the default {@code ShortScalar} implementation.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 8:14:56 PM
 */
public class ShortScalarImpl extends AbstractShortD0Tensor {
    private short value;

    /**
     * Constructor placing a default {@code short} as the scalar value.
     */
    public ShortScalarImpl() {
    }

    /**
     * Constructor specifying the value of the scalar.
     *
     * @param value
     *        The value to set the scalar to.
     */
    public ShortScalarImpl(short value) {
        this.value = value;
    }

    /**
     * Constructor specifying the value and index of the scalar. This constructor should only be used for calls to
     * {@code getReferenceTensor(Index)}.
     *
     * @param value
     *        The value to set the scalar to.
     *
     * @param index
     *        The index to use with this scalar.
     *
     * @throws IllegalArgumentException if {@code index.size() != 0}.
     */
    protected ShortScalarImpl(short value, Index<?> index) {
        super(index);
        this.value = value;
    }

    @Override
    public short getCell() {
        return value;
    }

    @Override
    public void setCell(short value) {
        this.value = value;
    }

    @Override
    @SuppressWarnings("unchecked") //idTensorCaster will turn this to an IdShortTensor, for sure
    public <I> IdShortTensor<I> getReferenceTensor(Index<I> index) {
        if (!index.isValidFor(this))
            throw new IllegalArgumentException("Index invalid for this tensor.");
        return (IdShortTensor<I>) TensorImplUtil.idTensorCaster(new ComposedTensor(index));
    }

    private class ComposedTensor extends AbstractShortTensor {

        public ComposedTensor(Index<?> index) {
            super(index);
        }

        @Override
        public short getCell(int... indices) {
            return ShortScalarImpl.this.getCell();
        }

        @Override
        public void setCell(short value, int... indices) {
            ShortScalarImpl.this.setCell(value);
        }
    }
}