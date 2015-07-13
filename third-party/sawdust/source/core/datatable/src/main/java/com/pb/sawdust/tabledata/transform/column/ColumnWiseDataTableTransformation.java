package com.pb.sawdust.tabledata.transform.column;

import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.transform.DataTableTransformation;

import java.util.Set;

/**
 * The {@code ColumnWiseDataTableTransformation} class is used to construct column-wise data table transformations. Column-wise
 * means that the transformation will be applied to an entire column at once. These transformations can be both mutating,
 * as well as wrapping, depending on the situation.
 *
 * @author crf
 *         Started 1/19/12 5:29 PM
 */
public abstract class ColumnWiseDataTableTransformation implements DataTableTransformation {
    /**
     * Get the set of columns that are transformed by this transformation.
     *
     * @return the columns this transformation transforms.
     */
    abstract public Set<String> getColumnsToTransform();

    /**
     * Transform the columns in an input data table.
     *
     * @param columns
     *        The columns in the table to transform.
     *
     * @param table
     *        The table which will be transformed.
     *
     * @return the transformed table.
     */
    abstract public DataTable transformColumns(Set<String> columns, DataTable table);

    @Override
    public DataTable transform(DataTable table) {
        return transformColumns(getColumnsToTransform(),table);
    }
}