package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.List;
import java.util.Set;

/**
 * The {@code WrappedDataProvider} class is a data provider which wraps another data provider. By default it just delegates
 * all of its methods to its wrapped provider. This class can be used to override provider methods without extending the
 * source provider.
 *
 * @author crf <br/>
 *         Started 3/11/11 12:08 PM
 */
public class WrappedDataProvider extends AbstractIdData implements DataProvider {
    private final DataProvider provider;

    /**
     * Constructor specifying the wrapped data provider.
     *
     * @param provider
     *        The data provider to wrap.
     */
    public WrappedDataProvider(DataProvider provider) {
        this.provider = provider;
    }

    /**
     * Constructor specifying the wrapped data provider and the data source identifier. This constructor should only be
     * called if the data provider is equivalent to another (already constructed) provider, and that the equivalence needs
     * to be recognized through the data identifier.
     *
     * @param provider
     *        The data provider to wrap.
     *
     * @param id
     *        The identifier for this provider.
     *
     * @throws IllegalArgumentException if {@code id} has not already been allocated via {@code AbstractIdData}.
     */
    public WrappedDataProvider(DataProvider provider, int id) {
        super(id);
        this.provider = provider;
    }

    @Override
    public int getDataLength() {
        return provider.getDataLength();
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
    public double[] getVariableData(String variable) {
        return provider.getVariableData(variable);
    }

    @Override
    public DataProvider getSubData(int start, int end) {
        return provider.getSubData(start,end);
    }

    @Override
    public int getAbsoluteStartIndex() {
        return provider.getAbsoluteStartIndex();
    }

    @Override
    public DoubleMatrix getData(List<String> variables) {
        return provider.getData(variables);
    }

    @Override
    public DoubleMatrix getData(List<String> variables, TensorFactory factory) {
        return provider.getData(variables,factory);
    }

    @Override
    public double[] getVariableData(String variable, int start, int end) {
        return provider.getVariableData(variable,start,end);
    }

    @Override
    public CalculationTrace getVariableTrace(String variable, int observation) {
        return provider.getVariableTrace(variable,observation);
    }
}