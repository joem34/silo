package com.pb.sawdust.tensor.decorators.primitive.size;

import com.pb.sawdust.tensor.decorators.primitive.ShortTensor;
import com.pb.sawdust.tensor.decorators.size.D4Tensor;
import com.pb.sawdust.tensor.Tensor;

import java.util.Iterator;

/**
 * The {@code ShortD4Tensor} interface combines the {@code ShortTensor} and {@code D4Tensor} interfaces. The notes and caveats
 * specified in those interfaces apply here as well.
 *
 * @author crf <br/>
 *         Started: Sat Oct 25 21:35:12 2008
 *         Revised: Dec 14, 2009 12:35:27 PM
 */
public interface ShortD4Tensor extends ShortTensor, D4Tensor<Short> {
    /**
     * Get the cell in this tensor at the specified location. This method should be more efficient than the other {@code getCell} and {@code getValue} 
     * methods in the interfaces being extended.
     *
     * @param d0index
     *        The index of dimension 0 of the tensor element.
     *
     * @param d1index
     *        The index of dimension 1 of the tensor element.
     *
     * @param d2index
     *        The index of dimension 2 of the tensor element.
     *
     * @param d3index
     *        The index of dimension 3 of the tensor element.
     *
     * @return the tensor value at the specified location.
     *
     * @throws IndexOutOfBoundsException if any index in {@code remainingIndices} is less than zero or greater than or
     *                                   equal to the size of its corresponding dimension.
     */
    short getCell(int d0index, int d1index, int d2index, int d3index);
    
    /**
     * Set the value of a cell in this tensor at the specified location.  This method should be more efficient than the other {@code setCell} and 
     * {@code getValue} methods in the interfaces being extended.
     *
     * @param value
     *        The value to set the cell to.
     *
     * @param d0index
     *        The index of dimension 0 of the tensor element.
     *
     * @param d1index
     *        The index of dimension 1 of the tensor element.
     *
     * @param d2index
     *        The index of dimension 2 of the tensor element.
     *
     * @param d3index
     *        The index of dimension 3 of the tensor element.
     *
     * @throws IndexOutOfBoundsException if any index in {@code remainingIndices} is less than zero or greater than or
     *                                   equal to the size of its corresponding dimension.
     */
    void setCell(short value, int d0index, int d1index, int d2index, int d3index);
    
    /**
     * {@inheritDoc}
     * 
     * The tensors this iterator loops over are guaranteed to be {@code ShortD3Tensor} tensors.
     * 
     */
    Iterator<Tensor<Short>> iterator();
}