package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.Set;

/**
 * The {@code LazySubDataProvider} class provides an efficient way to create sub-data providers. This class wraps
 * a source data provider and only actually partitions it when the data is requested (<i>i.e.</i> when {@link #getVariableData(String)},
 * {@link #getVariableData(String, int, int)}, or {@link #getData(java.util.List)} are called).  This means that the
 * (sub-) data providers can be repeatedly partitioned without a major performance penalty.
 * <p>
 * For instances of this class to work correctly, the source {@code DataProvider} classes must implement their
 * {@link com.pb.sawdust.model.models.provider.DataProvider#getVariableData(String, int, int)} method to
 * actually get the subdata. This is because this method is used by this class to create the actual subdata. Because of
 * this, for example, if that method is implemented to call {@link com.pb.sawdust.model.models.provider.DataProvider#getData(java.util.List)}
 * then when data is requested from this class an infinite recursion will occur, resulting in a {@code StackOverflowException}. 
 *
 * @author crf <br/>
 *         Started Sep 14, 2010 2:23:36 PM
 */
public class LazySubDataProvider extends AbstractDataProvider {
    private final DataProvider provider;

    /**
     * The start (inclusive) of the data provider partition.
     */
    protected final int start;

    /**
     * The end (exclusive) of the data provider partition.
     */
    protected final int end;

    /**
     * Constructor specifying the data provider, the tensor factory to build the data results from, and the range of
     * observations in the provider.
     *
     * @param provider
     *        The source data provider.
     *
     * @param factory
     *        The tensor factory used to build the data results.
     *
     * @param start
     *        The starting (inclusive) observation from {@code provider} for the data.
     *
     * @param end
     *        The ending (exclusive) observation from {@code provider} for the data.
     *
     * @throws IllegalArgumentException if <code>end &lt;= start</code> or if {@code start} and/or {@code end} are out of
     *                                  the provider's data bounds (<i>i.e.</i> if either are less than zero or greater
     *                                  than the data provider's length).
     */
    public LazySubDataProvider(DataProvider provider, TensorFactory factory, int start, int end) {
        super(factory);
        if (end <= start)
            throw new IllegalArgumentException("Subdata must have a strictly positive range (start=" + start + ", end=" + end + ")");
        int length = provider.getDataLength();
        if (end > length  || start < 0)
            throw new IllegalArgumentException(String.format("Subdata (start: %d, end: %d) out of bounds for provider of length %d",start,end,length));
        this.provider = provider;
        this.start = start;
        this.end = end;
    }

    @Override
    public int getDataLength() {
        return end - start;
    }

    @Override
    public DataProvider getSubData(int start, int end) {
        //have to precheck, because going out of bounds (which is invalid) might still be in bounds for parent provider
        if (end > getDataLength()  || start < 0)
            throw new IllegalArgumentException(String.format("Subdata (start: %d, end: %d) out of bounds for provider of length %d",start,end,getDataLength()));
        return provider.getSubData(start+this.start,end+this.start);
    }

    @Override
    public double[] getVariableData(String variable) {
        return provider.getVariableData(variable,start,end);
    }

    @Override
    public double[] getVariableData(String variable, int start, int end) {
        //have to precheck, because going out of bounds (which is invalid) might still be in bounds for parent provider
        if (end > getDataLength()  || start < 0)
            throw new IllegalArgumentException(String.format("Subdata (start: %d, end: %d) out of bounds for provider of length %d",start,end,getDataLength()));
        return provider.getVariableData(variable,start+this.start,end+this.start);
    }

    @Override
    public boolean hasVariable(String variable) {
        return provider.hasVariable(variable);
    }

    @Override
    public Set<String> getVariables() {
        return provider.getVariables();
    }

    @Override
    public int getAbsoluteStartIndex() {
        return provider.getAbsoluteStartIndex()+start;
    }

    @Override
    public CalculationTrace getVariableTrace(String variable, int observation) {
        return provider.getVariableTrace(variable,observation+start);
    }
}