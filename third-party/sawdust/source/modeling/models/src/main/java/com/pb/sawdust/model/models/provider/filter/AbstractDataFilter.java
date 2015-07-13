package com.pb.sawdust.model.models.provider.filter;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;
import com.pb.sawdust.tensor.slice.BaseSlice;
import com.pb.sawdust.tensor.slice.NullSlice;
import com.pb.sawdust.tensor.slice.Slice;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code AbstractFilter} ...
 *
 * @author crf <br/>
 *         Started 3/3/11 7:56 AM
 */
public abstract class AbstractDataFilter implements DataFilter {

    private Slice getFilteredSlice(DataProvider provider, boolean un) {
        BooleanVector filter = getFilter(provider);
        List<Integer> indices = new LinkedList<Integer>();
        if (un) {
            for (int i = 0; i < filter.size(0); i++)
                if (!filter.getCell(i))
                    indices.add(i);
        } else {
            for (int i = 0; i < filter.size(0); i++)
                if (filter.getCell(i))
                    indices.add(i);
        }
        int[] ind = new int[indices.size()];
        int counter = 0;
        for (int i : indices)
            ind[counter++] = i;
        return ind.length == 0 ? NullSlice.getNullSlice() : new BaseSlice(ind);
    }

    @Override
    public Slice getFilteredSlice(DataProvider provider) {
        return getFilteredSlice(provider,false);
    }

    @Override
    public Slice getUnfilteredSlice(DataProvider provider) {
        return getFilteredSlice(provider,true);
    }

    public CalculationTrace traceFilterCalculation(DataProvider data, int observation) {
        Boolean result = getFilter(data).getCell(observation);
        return new CalculationTrace("availability filter",result.toString(),result ? 1.0 : 0.0);
    }
}