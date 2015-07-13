package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.size.D2TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.CharTensor;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.decorators.id.primitive.IdCharTensor;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.CharTypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.tensor.alias.matrix.primitive.CharMatrix;

/**
 * The {@code CharD2TensorShell} class is a wrapper which sets a 2-dimensional {@code CharTensor} as a {@code Matrix} (or,
 * more specifically, a {@code CharMatrix}).
 *
 * @author crf <br/>
 *         Started: Sat Oct 25 21:35:12 2008
 *         Revised: Dec 14, 2009 12:35:25 PM
 */
public class CharD2TensorShell extends D2TensorShell<Character> implements CharMatrix {
    private final CharTensor tensor;

    /**
     * Constructor specifying tensor to wrap. The tensor must be 2-dimensional or an exception will be thrown.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @throws IllegalArgumentException if {@code tensor} is not 2 dimension in size.
     */
    public CharD2TensorShell(CharTensor tensor) {
        super(tensor);
        this.tensor = tensor;
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code CharTensor.getCell(d0index,d1index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public char getCell(int d0index, int d1index) {
        return tensor.getCell(d0index,d1index);
    }

    public char getCell(int ... indices) {
        if (indices.length != 2)
            throw new IllegalArgumentException("CharD2Tensor is 2 dimension in size, getValue passed with " + indices.length + " indices.");
        return getCell(indices[0],indices[1]);
    }

    public Character getValue(int ... indices) {
        return getCell(indices);
    }

    public Character getValue(int d0index, int d1index) {
        return getCell(d0index,d1index);
    }

    /**
     * {@inheritDoc}
     *
     * This method just calls {@code CharTensor.setCell(char,d0index,d1index)}, 
     * and should be overridden if any efficiencies over that method can be made.
     */
    public void setCell(char value, int d0index, int d1index) {
        tensor.setCell(value,d0index,d1index);
    }

    public void setCell(char value, int ... indices) {
        if (indices.length != 2)
            throw new IllegalArgumentException("CharD2Tensor is 2 dimension in size, getValue passed with " + indices.length + " indices.");
        setCell(value,indices[0],indices[1]);
    }

    public void setValue(Character value, int ... indices) {
        setCell(value,indices);
    }

    public void setValue(Character value, int d0index, int d1index) {
        setCell(value,d0index,d1index);
    }
    
    public CharTypeSafeArray getTensorValues(Class<Character> type) {
        return getTensorValues();
    }

    public CharTypeSafeArray getTensorValues() {
       @SuppressWarnings("unchecked") //getType requirements in tensor make this ok
        CharTypeSafeArray array = TypeSafeArrayFactory.charTypeSafeArray(getDimensions());
        for (int[] index : IterableAbacus.getIterableAbacus(getDimensions()))
            array.set(getCell(index),index);
        return array;
    }

    public void setTensorValues(CharTypeSafeArray valuesArray) {
        TensorImplUtil.setTensorValues(this,valuesArray);
    }

    public <I> IdCharTensor<I> getReferenceTensor(Index<I> index) {
        return (IdCharTensor<I>) super.getReferenceTensor(index);
    }

    protected CharTensor getComposedTensor(Index<?> index) {
        return TensorImplUtil.getComposedTensor(this,index); 
    }
}