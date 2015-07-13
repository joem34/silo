package com.pb.sawdust.calculator.tensor;

import com.pb.sawdust.calculator.NumericFunction1;
import com.pb.sawdust.calculator.NumericFunction2;
import com.pb.sawdust.calculator.NumericFunctionN;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.decorators.primitive.*;

import java.util.List;

/**
 * The {@code CellWiseTensorCalculation} class provides methods for running cell-wise (element-by-element) calculations
 * on tensors. Cell-wise calculations work not only on equaly sized tensors, but also on tensors with unequal
 * dimensionality but whose dimension sizes line up exactly.  For example, two tensors with dimensionality
 * {@code [3,4,6]} and {@code [3,6]} are valid for an cell-wise calculation whose result has dimensionality
 * {@code [3,4,6]} and is filled with the calculation applied between the slices of the first tensor (holding its
 * second dimension constant) and the entire second tensor.
 * <p>
 * For tensors which have unequal sizes, all of the dimensions of the smaller tensor must match to one of the dimensions
 * of the larger tensor.  The manner by which the dimensions match may either be specified or automatically determined.
 * If specified, a "fixed dimension" array is specified, which has one element for each dimension of the smaller tensor
 * which indicates which dimension in the larger tensor it corrsponds to.  If automatically determined, the smaller
 * tensor's dimensions are matched sequentially to the larger tensor.  So, two tensors with dimensionality
 * {@code [3,4,6]} and {@code [6,3]} would not match automatically, but could be matched with the fixed dimension
 * specification {@code [2,0]}.
 * <p>
 * It is possible to chain calculations to build composite ones.  For example, the calculation {@code (A+B)*C} (where
 * {@code A}, {@code B}, and {@code C} are tensors) could be made with the following call (assume a static import of
 * {@code NumericFunctions.*} has been made):
 * <pre><code>
 *     calculate(calculate(A,B,ADD),C,MULTIPLY)
 * </code></pre>
 * However, in almost all cases, it is better to build a composite function:
 * <pre><code>
 *     calculate(NumericFunctions.compositeNumericFunction(PARAMETER,PARAMETER,ADD,PARAMETER,MULTIPLY),A,B,C)
 * </code></pre>
 * Not only is this call (somewhat) more readable, it will have improved performance - both in terms of processing speed
 * and memory usage.  The chained call example must create a sub-result tensor for each {@code calculate} call, which
 * uses memory as well requring an additional pair of set-get calls to use the intermediate result.  The composite function
 * call should execute the function calculation once using all input tensor values (for a given index position), eliminating
 * the need for an intermediate result tensor.  Additionally, the chained call can take individual numbers as arguments,
 * and will automatically apply them across the other tensor arguments appropriately.
 *
 * @author crf <br/>
 *         Started Nov 18, 2010 1:32:16 PM
 */
public interface CellWiseTensorCalculation {
    
    /**
     * Perform an cell-wise unary calculation on a tensor holding {@code byte}s.
     *
     * @param t
     *        The input tensor.
     *
     * @param function
     *        The function to apply to the tensor cells.
     *
     * @return the result of {@code function} applied to {@code t}.
     */
    ByteTensor calculate(ByteTensor t, NumericFunction1 function);

    /**
     * Perform an cell-wise unary calculation on a tensor holding {@code short}s.
     *
     * @param t
     *        The input tensor.
     *
     * @param function
     *        The function to apply to the tensor cells.
     *
     * @return the result of {@code function} applied to {@code t}.
     */
    ShortTensor calculate(ShortTensor t, NumericFunction1 function);

    /**
     * Perform an cell-wise unary calculation on a tensor holding {@code int}s.
     *
     * @param t
     *        The input tensor.
     *
     * @param function
     *        The function to apply to the tensor cells.
     *
     * @return the result of {@code function} applied to {@code t}.
     */
    IntTensor calculate(IntTensor t, NumericFunction1 function);

    /**
     * Perform an cell-wise unary calculation on a tensor holding {@code long}s.
     *
     * @param t
     *        The input tensor.
     *
     * @param function
     *        The function to apply to the tensor cells.
     *
     * @return the result of {@code function} applied to {@code t}.
     */
    LongTensor calculate(LongTensor t, NumericFunction1 function);

    /**
     * Perform an cell-wise unary calculation on a tensor holding {@code float}s.
     *
     * @param t
     *        The input tensor.
     *
     * @param function
     *        The function to apply to the tensor cells.
     *
     * @return the result of {@code function} applied to {@code t}.
     */
    FloatTensor calculate(FloatTensor t, NumericFunction1 function);

    /**
     * Perform an cell-wise unary calculation on a tensor holding {@code double}s.
     *
     * @param t
     *        The input tensor.
     *
     * @param function
     *        The function to apply to the tensor cells.
     *
     * @return the result of {@code function} applied to {@code t}.
     */
    DoubleTensor calculate(DoubleTensor t, NumericFunction1 function);

    /**
     * Perform an cell-wise calculation between a {@code byte} value and a tensor holding {@code byte}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    ByteTensor calculate(byte value, ByteTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code byte}s and a {@code byte} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    ByteTensor calculate(ByteTensor t, byte value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a {@code short} value and a tensor holding {@code byte}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    ShortTensor calculate(short value, ByteTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code byte}s and a {@code short} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    ShortTensor calculate(ByteTensor t, short value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a {@code int} value and a tensor holding {@code byte}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    IntTensor calculate(int value, ByteTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code byte}s and a {@code int} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    IntTensor calculate(ByteTensor t, int value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a {@code long} value and a tensor holding {@code byte}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    LongTensor calculate(long value, ByteTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code byte}s and a {@code long} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    LongTensor calculate(ByteTensor t, long value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a {@code float} value and a tensor holding {@code byte}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    FloatTensor calculate(float value, ByteTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code byte}s and a {@code float} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    FloatTensor calculate(ByteTensor t, float value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a {@code double} value and a tensor holding {@code byte}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    DoubleTensor calculate(double value, ByteTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code byte}s and a {@code double} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    DoubleTensor calculate(ByteTensor t, double value, NumericFunction2 function);
    
    /**
     * Perform an cell-wise calculation between a {@code short} value and a tensor holding {@code short}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    ShortTensor calculate(short value, ShortTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code short}s and a {@code short} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    ShortTensor calculate(ShortTensor t, short value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a {@code int} value and a tensor holding {@code short}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    IntTensor calculate(int value, ShortTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code short}s and a {@code int} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    IntTensor calculate(ShortTensor t, int value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a {@code long} value and a tensor holding {@code short}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    LongTensor calculate(long value, ShortTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code short}s and a {@code long} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    LongTensor calculate(ShortTensor t, long value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a {@code float} value and a tensor holding {@code short}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    FloatTensor calculate(float value, ShortTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code short}s and a {@code float} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    FloatTensor calculate(ShortTensor t, float value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a {@code double} value and a tensor holding {@code short}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    DoubleTensor calculate(double value, ShortTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code short}s and a {@code double} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    DoubleTensor calculate(ShortTensor t, double value, NumericFunction2 function);
    
    /**
     * Perform an cell-wise calculation between a {@code int} value and a tensor holding {@code int}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    IntTensor calculate(int value, IntTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code int}s and a {@code int} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    IntTensor calculate(IntTensor t, int value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a {@code long} value and a tensor holding {@code int}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    LongTensor calculate(long value, IntTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code int}s and a {@code long} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    LongTensor calculate(IntTensor t, long value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a {@code float} value and a tensor holding {@code int}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    FloatTensor calculate(float value, IntTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code int}s and a {@code float} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    FloatTensor calculate(IntTensor t, float value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a {@code double} value and a tensor holding {@code int}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    DoubleTensor calculate(double value, IntTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code int}s and a {@code double} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    DoubleTensor calculate(IntTensor t, double value, NumericFunction2 function);
    
    
    /**
     * Perform an cell-wise calculation between a {@code long} value and a tensor holding {@code long}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    LongTensor calculate(long value, LongTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code long}s and a {@code long} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    LongTensor calculate(LongTensor t, long value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a {@code float} value and a tensor holding {@code long}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    FloatTensor calculate(float value, LongTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code long}s and a {@code float} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    FloatTensor calculate(LongTensor t, float value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a {@code double} value and a tensor holding {@code long}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    DoubleTensor calculate(double value, LongTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code long}s and a {@code double} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    DoubleTensor calculate(LongTensor t, double value, NumericFunction2 function);
    
    /**
     * Perform an cell-wise calculation between a {@code float} value and a tensor holding {@code float}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    FloatTensor calculate(float value, FloatTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code float}s and a {@code float} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    FloatTensor calculate(FloatTensor t, float value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a {@code double} value and a tensor holding {@code float}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    DoubleTensor calculate(double value, FloatTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code float}s and a {@code double} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    DoubleTensor calculate(FloatTensor t, double value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a {@code double} value and a tensor holding {@code double}s.
     * 
     * @param value
     *        The first argument to the function.
     * 
     * @param t
     *        The tensor holding the second arguments to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code value} and {@code t} applied to {@code function}.
     */
    DoubleTensor calculate(double value, DoubleTensor t, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code double}s and a {@code double} value.
     * 
     * @param t
     *        The tensor holding the first arguments to the function.                                                   
     * 
     * @param value
     *        The second argument to the function.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return the result of {@code t} and {@code value} applied to {@code function}.
     */
    DoubleTensor calculate(DoubleTensor t, double value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    ByteTensor calculate(ByteTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    ShortTensor calculate(ShortTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's). 
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    ShortTensor calculate(ByteTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    IntTensor calculate(IntTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    IntTensor calculate(ByteTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    LongTensor calculate(LongTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    LongTensor calculate(ByteTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    FloatTensor calculate(FloatTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    FloatTensor calculate(ByteTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    DoubleTensor calculate(DoubleTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    DoubleTensor calculate(ByteTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    ShortTensor calculate(ShortTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    IntTensor calculate(IntTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    IntTensor calculate(ShortTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    LongTensor calculate(LongTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    LongTensor calculate(ShortTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    FloatTensor calculate(FloatTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    FloatTensor calculate(ShortTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    DoubleTensor calculate(DoubleTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    DoubleTensor calculate(ShortTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    IntTensor calculate(IntTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    LongTensor calculate(LongTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    LongTensor calculate(IntTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    FloatTensor calculate(FloatTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    FloatTensor calculate(IntTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    DoubleTensor calculate(DoubleTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    DoubleTensor calculate(IntTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    LongTensor calculate(LongTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    FloatTensor calculate(FloatTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    FloatTensor calculate(LongTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    DoubleTensor calculate(DoubleTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    DoubleTensor calculate(LongTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    FloatTensor calculate(FloatTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    DoubleTensor calculate(DoubleTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    DoubleTensor calculate(FloatTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for the tensor
     * with fewer dimensions.  A discussion of matching dimensions is found in this class's documentation.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for the tensor with fewer dimensions. If both tensors have the same number of dimensions, 
     *        then this parameter must provide the matching dimensions for the second tensor (<i>i.e.</i> the map from the second 
     *        tensor's dimensions to the first's).
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if the size of {@code matchingDimensions} does not equal the number of dimensions
     *                                  in the tensor with fewer dimensions, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in the tensor with more dimensions, if
     *                                  if any value in {@code matchingDimensions} is repeated, or if {@code matchingDimensions}
     *                                  does not correctly map the two tensors's dimensions.
     */
    DoubleTensor calculate(DoubleTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    ByteTensor calculate(ByteTensor t1, ByteTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    ShortTensor calculate(ShortTensor t1, ByteTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    ShortTensor calculate(ByteTensor t1, ShortTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    IntTensor calculate(IntTensor t1, ByteTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    IntTensor calculate(ByteTensor t1, IntTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    LongTensor calculate(LongTensor t1, ByteTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    LongTensor calculate(ByteTensor t1, LongTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    FloatTensor calculate(FloatTensor t1, ByteTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    FloatTensor calculate(ByteTensor t1, FloatTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    DoubleTensor calculate(DoubleTensor t1, ByteTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    DoubleTensor calculate(ByteTensor t1, DoubleTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    ShortTensor calculate(ShortTensor t1, ShortTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    IntTensor calculate(IntTensor t1, ShortTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    IntTensor calculate(ShortTensor t1, IntTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    LongTensor calculate(LongTensor t1, ShortTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    LongTensor calculate(ShortTensor t1, LongTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    FloatTensor calculate(FloatTensor t1, ShortTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    FloatTensor calculate(ShortTensor t1, FloatTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    DoubleTensor calculate(DoubleTensor t1, ShortTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    DoubleTensor calculate(ShortTensor t1, DoubleTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    IntTensor calculate(IntTensor t1, IntTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    LongTensor calculate(LongTensor t1, IntTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    LongTensor calculate(IntTensor t1, LongTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    FloatTensor calculate(FloatTensor t1, IntTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    FloatTensor calculate(IntTensor t1, FloatTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    DoubleTensor calculate(DoubleTensor t1, IntTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    DoubleTensor calculate(IntTensor t1, DoubleTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    LongTensor calculate(LongTensor t1, LongTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    FloatTensor calculate(FloatTensor t1, LongTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    FloatTensor calculate(LongTensor t1, FloatTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    DoubleTensor calculate(DoubleTensor t1, LongTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    DoubleTensor calculate(LongTensor t1, DoubleTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    FloatTensor calculate(FloatTensor t1, FloatTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    DoubleTensor calculate(DoubleTensor t1, FloatTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     *
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    DoubleTensor calculate(FloatTensor t1, DoubleTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * @param t1
     *        The tensor holding the first arguments to the function.
     *
     * @param t2
     *        The tensor holding the second arguments to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimension matching procedure cannot align the tensors.
     */
    DoubleTensor calculate(DoubleTensor t1, DoubleTensor t2, NumericFunction2 function);

    /**
     * Convenience method which will take a list of tensors and numbers ((boxed) primitives or {@code Number} instances) and 
     * convert them to a parameter list suitable for use with the {@code NumericFunctions.NumericFunctionN} {@code calculate}
     * methods in this interface.  The input parameters list must only contain numeric tensors and/or numbers; anything
     * else will cause an exception to be thrown.  Numbers will be converted to {@code Scalar} instances.
     *
     * @param parameters
     *        The list of tensor and/or number parameters to convert.
     *
     * @return a list of tensors suitable for using with the {@code NumericFunctions.NumericFunctionN} {@code calculate}
     *         methods in this interface.
     *
     * @throws IllegalArgumentException if any element in {@code parameters} is not a {@code Number} (directly or via
     *                                  boxing) nor a numeric tensor (such that <code>((Tensor) t).getType().isNumeric() == true</code>).
     *
     * @see #calculateByte(com.pb.sawdust.calculator.NumericFunctionN, java.util.List)
     * @see #calculateShort(com.pb.sawdust.calculator.NumericFunctionN, java.util.List)
     * @see #calculateInt(com.pb.sawdust.calculator.NumericFunctionN, java.util.List)
     * @see #calculateLong(com.pb.sawdust.calculator.NumericFunctionN, java.util.List)
     * @see #calculateFloat(com.pb.sawdust.calculator.NumericFunctionN, java.util.List)
     * @see #calculateDouble(com.pb.sawdust.calculator.NumericFunctionN, java.util.List)
     * @see #calculate(com.pb.sawdust.calculator.NumericFunctionN, java.util.List)
     */
    List<? extends Tensor<? extends Number>> getCalculationParameters(List<?> parameters);
    
    /**
     * Perform a compound cell-wise calculation on a series of input parameters.  The returned tensor will be the same
     * shape as the input parameter with the largest dimension count.  If the parameters are not all of the same shape,
     * then the dimension matching rules described above in this class's documentation will be used.
     * <p>
     * To use a single number (constant) as a paraemter, a {@code Scalar} should be used. Alternatively, the
     * {@link #getCalculationParameters(java.util.List)} can be used to automatically convert numeric arguments to a
     * list of tensors suitable for use in this method.
     * <p>
     * Note that all parameters will be converted to {@code byte}s for the calculation (instead of converting the result),
     * which may lead to a loss of numerical precision in some cases.
     *  
     * @param function
     *        The function to apply to the parameters.
     *
     * @param parameters
     *        The parameters used as arguments for the function.
     * 
     * @return the result of {@code function} applied to {@code remainingParameters}.
     * 
     * @throws IllegalArgumentException if the number of parameters in {@code remainingParameters} does not equal what
     *                                  {@code function} requires or if any of the tensors cannot have their dimensions matched for 
     *                                  the calculation.
     */
    ByteTensor calculateByte(NumericFunctionN function, List<? extends Tensor<? extends Number>> parameters);
    
    /**
     * Perform a compound cell-wise calculation on a series of input parameters.  The returned tensor will be the same
     * shape as the input parameter with the largest dimension count.  If the parameters are not all of the same shape,
     * then the dimension matching rules described above in this class's documentation will be used.
     * <p>
     * To use a single number (constant) as a paraemter, a {@code Scalar} should be used. Alternatively, the
     * {@link #getCalculationParameters(java.util.List)} can be used to automatically convert numeric arguments to a
     * list of tensors suitable for use in this method.
     * <p>
     * Note that all parameters will be converted to {@code short}s for the calculation (instead of converting the result),
     * which may lead to a loss of numerical precision in some cases.
     *  
     * @param function
     *        The function to apply to the parameters.
     *
     * @param parameters
     *        The parameters used as arguments for the function.
     * 
     * @return the result of {@code function} applied to {@code remainingParameters}.
     * 
     * @throws IllegalArgumentException if the number of parameters in {@code remainingParameters} does not equal what
     *                                  {@code function} requires or if any of the tensors cannot have their dimensions matched for 
     *                                  the calculation.
     */
    ShortTensor calculateShort(NumericFunctionN function, List<? extends Tensor<? extends Number>> parameters);
    
    /**
     * Perform a compound cell-wise calculation on a series of input parameters.  The returned tensor will be the same
     * shape as the input parameter with the largest dimension count.  If the parameters are not all of the same shape,
     * then the dimension matching rules described above in this class's documentation will be used.
     * <p>
     * To use a single number (constant) as a paraemter, a {@code Scalar} should be used. Alternatively, the
     * {@link #getCalculationParameters(java.util.List)} can be used to automatically convert numeric arguments to a
     * list of tensors suitable for use in this method.
     * <p>
     * Note that all parameters will be converted to {@code int}s for the calculation (instead of converting the result),
     * which may lead to a loss of numerical precision in some cases.
     *  
     * @param function
     *        The function to apply to the parameters.
     *
     * @param parameters
     *        The parameters used as arguments for the function.
     * 
     * @return the result of {@code function} applied to {@code remainingParameters}.
     * 
     * @throws IllegalArgumentException if the number of parameters in {@code remainingParameters} does not equal what
     *                                  {@code function} requires or if any of the tensors cannot have their dimensions matched for 
     *                                  the calculation.
     */
    IntTensor calculateInt(NumericFunctionN function, List<? extends Tensor<? extends Number>> parameters);
    
    /**
     * Perform a compound cell-wise calculation on a series of input parameters.  The returned tensor will be the same
     * shape as the input parameter with the largest dimension count.  If the parameters are not all of the same shape,
     * then the dimension matching rules described above in this class's documentation will be used.
     * <p>
     * To use a single number (constant) as a paraemter, a {@code Scalar} should be used. Alternatively, the
     * {@link #getCalculationParameters(java.util.List)} can be used to automatically convert numeric arguments to a
     * list of tensors suitable for use in this method.
     * <p>
     * Note that all parameters will be converted to {@code long}s for the calculation (instead of converting the result),
     * which may lead to a loss of numerical precision in some cases.
     *  
     * @param function
     *        The function to apply to the parameters.
     *
     * @param parameters
     *        The parameters used as arguments for the function.
     * 
     * @return the result of {@code function} applied to {@code remainingParameters}.
     * 
     * @throws IllegalArgumentException if the number of parameters in {@code remainingParameters} does not equal what
     *                                  {@code function} requires or if any of the tensors cannot have their dimensions matched for 
     *                                  the calculation.
     */
    LongTensor calculateLong(NumericFunctionN function, List<? extends Tensor<? extends Number>> parameters);
    
    /**
     * Perform a compound cell-wise calculation on a series of input parameters.  The returned tensor will be the same
     * shape as the input parameter with the largest dimension count.  If the parameters are not all of the same shape,
     * then the dimension matching rules described above in this class's documentation will be used.
     * <p>
     * To use a single number (constant) as a paraemter, a {@code Scalar} should be used. Alternatively, the
     * {@link #getCalculationParameters(java.util.List)} can be used to automatically convert numeric arguments to a
     * list of tensors suitable for use in this method.
     * <p>
     * Note that all parameters will be converted to {@code float}s for the calculation (instead of converting the result),
     * which may lead to a loss of numerical precision in some cases.
     *  
     * @param function
     *        The function to apply to the parameters.
     *
     * @param parameters
     *        The parameters used as arguments for the function.
     * 
     * @return the result of {@code function} applied to {@code remainingParameters}.
     * 
     * @throws IllegalArgumentException if the number of parameters in {@code remainingParameters} does not equal what
     *                                  {@code function} requires or if any of the tensors cannot have their dimensions matched for 
     *                                  the calculation.
     */
    FloatTensor calculateFloat(NumericFunctionN function, List<? extends Tensor<? extends Number>> parameters);
    
    /**
     * Perform a compound cell-wise calculation on a series of input parameters.  The returned tensor will be the same
     * shape as the input parameter with the largest dimension count.  If the parameters are not all of the same shape,
     * then the dimension matching rules described above in this class's documentation will be used.
     * <p>
     * To use a single number (constant) as a paraemter, a {@code Scalar} should be used. Alternatively, the
     * {@link #getCalculationParameters(java.util.List)} can be used to automatically convert numeric arguments to a
     * list of tensors suitable for use in this method.
     * <p>
     * Note that all parameters will be converted to {@code double}s for the calculation (instead of converting the result).
     *  
     * @param function
     *        The function to apply to the parameters.
     *
     * @param parameters
     *        The parameters used as arguments for the function.
     * 
     * @return the result of {@code function} applied to {@code remainingParameters}.
     * 
     * @throws IllegalArgumentException if the number of parameters in {@code remainingParameters} does not equal what
     *                                  {@code function} requires or if any of the tensors cannot have their dimensions matched for 
     *                                  the calculation.
     */
    DoubleTensor calculateDouble(NumericFunctionN function, List<? extends Tensor<? extends Number>> parameters);
    
    /**
     * Perform a compound cell-wise calculation on a series of input parameters.  The result tensor will be of the type of
     * the "highest" numeric type, and will be castable to the appropriate primitive tensor ({@code ByteTensor}, 
     * {@code ShortTensor}, <i>etc.</i>).  The types are ordered (lowest to highest): {@code byte}, {@code short}, {@code int}, 
     * {@code long}, {@code float}, {@code double}.  The returned tensor will be the same shape as the input parameter with 
     * the largest dimension count. If the parameters are not all of the same shape, then the dimension matching rules 
     * described above in this class's documentation will be used.
     * <p>
     * To use a single number (constant) as a paraemter, a {@code Scalar} should be used. Alternatively, the
     * {@link #getCalculationParameters(java.util.List)} can be used to automatically convert numeric arguments to a
     * list of tensors suitable for use in this method.
     * <p>
     * Note that all parameters will be converted to the appropriate type for the calculation (instead of converting the result),
     * which may lead to a loss of numerical precision in some cases.
     *  
     * @param function
     *        The function to apply to the parameters.
     *
     * @param parameters
     *        The parameters used as arguments for the function.
     * 
     * @return the result of {@code function} applied to {@code remainingParameters}.
     * 
     * @throws IllegalArgumentException if the number of parameters in {@code remainingParameters} does not equal what
     *                                  {@code function} requires or if any of the tensors cannot have their dimensions matched for 
     *                                  the calculation.
     */
    Tensor<? extends Number> calculate(NumericFunctionN function, List<? extends Tensor<? extends Number>> parameters);
}