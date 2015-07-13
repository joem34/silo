package com.pb.sawdust.tensor.alias.scalar.impl;

import com.pb.sawdust.tensor.decorators.primitive.AbstractByteTensor;
import com.pb.sawdust.tensor.decorators.primitive.size.AbstractByteD0Tensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdByteTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;

/**
 * The {@code ByteScalarImpl} class provides the default {@code ByteScalar} implementation.
 *
 * @author crf <br/>
 *         Started: Jun 16, 2009 8:14:56 PM
 */
public class ByteScalarImpl extends AbstractByteD0Tensor {
    private byte value;

    /**
     * Constructor placing a default {@code byte} as the scalar value.
     */
    public ByteScalarImpl() {
    }

    /**
     * Constructor specifying the value of the scalar.
     *
     * @param value
     *        The value to set the scalar to.
     */
    public ByteScalarImpl(byte value) {
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
    protected ByteScalarImpl(byte value, Index<?> index) {
        super(index);
        this.value = value;
    }

    @Override
    public byte getCell() {
        return value;
    }

    @Override
    public void setCell(byte value) {
        this.value = value;
    }

    @Override
    @SuppressWarnings("unchecked") //idTensorCaster will turn this to an IdByteTensor, for sure
    public <I> IdByteTensor<I> getReferenceTensor(Index<I> index) {
        if (!index.isValidFor(this))
            throw new IllegalArgumentException("Index invalid for this tensor.");
        return (IdByteTensor<I>) TensorImplUtil.idTensorCaster(new ComposedTensor(index));
    }

    private class ComposedTensor extends AbstractByteTensor {

        public ComposedTensor(Index<?> index) {
            super(index);
        }

        @Override
        public byte getCell(int... indices) {
            return ByteScalarImpl.this.getCell();
        }

        @Override
        public void setCell(byte value, int... indices) {
            ByteScalarImpl.this.setCell(value);
        }
    }
}