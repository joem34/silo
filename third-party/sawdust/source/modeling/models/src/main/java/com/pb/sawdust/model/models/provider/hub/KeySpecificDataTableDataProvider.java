package com.pb.sawdust.model.models.provider.hub;

import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tensor.alias.vector.id.IdDoubleVector;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.array.DoubleTypeSafeArray;
import com.pb.sawdust.util.collections.SetList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.pb.sawdust.util.Range.*;

/**
 * The {@code KeySpecificDataTableDataProvider} ...
 *
 * @author crf
 *         Started 4/11/12 6:03 AM
 */
public class KeySpecificDataTableDataProvider<K> extends KeySpecificDataProvider<K> {
    private final DataTable table;
    private final SetList<K> keys;
    private final TensorFactory factory;
    private final int dataLength;

    public KeySpecificDataTableDataProvider(DataTable table, SetList<K> keys, int dataLength, TensorFactory factory) {
        super(keys,dataLength,factory);
        this.table = table;
        this.keys = keys;
        this.factory = factory;
        this.dataLength = dataLength;
    }

    protected IdDoubleVector<K> getPolyDataVector(String variable) {
        IdDoubleVector<K> data = factory.doubleVector(keys,dataLength);
        try {
            data.setTensorValues(new DoubleTypeSafeArray(table.getDoubleColumn(variable).getPrimitiveColumn()));
        } catch (NumberFormatException e) {
            //column is a string and not parsable to a double: make it not-a-number
            double[] column = new double[table.getRowCount()];
            Arrays.fill(column,Double.NaN);
            data.setTensorValues(new DoubleTypeSafeArray(column));
        }
        return data;
    }

    @Override
    protected double getVariableData(K key, String variable) {
        try {
            return table.getRow(keys.indexOf(key)).getCellAsDouble(variable);
        } catch (NumberFormatException e) { //if column is a string column that can't be converted to number, make it not-a-number
            return Double.NaN;
        }
    }

    @Override
    public Set<String> getPolyDataVariables() {
        Set<String> variables = new HashSet<>();
        Collections.addAll(variables,table.getColumnLabels());
        return variables;
    }
}