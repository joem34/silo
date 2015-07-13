package com.pb.sawdust.tensor.alias.matrix.primitive;

import com.pb.sawdust.tensor.decorators.primitive.size.ShortD2TensorTest;
import com.pb.sawdust.tensor.alias.vector.primitive.ShortVector;
import com.pb.sawdust.tensor.Tensor;
import org.junit.Test;                                                      
import static org.junit.Assert.*;

/**
 * @author crf <br/>
 *         Started: Jul 5, 2009 9:19:38 AM
 */
public abstract class ShortMatrixTest extends ShortD2TensorTest {    
    @Test
    public void testIteratorType() {
        for (Tensor<Short> t : tensor)
            assertTrue(t instanceof ShortVector);
    }
}