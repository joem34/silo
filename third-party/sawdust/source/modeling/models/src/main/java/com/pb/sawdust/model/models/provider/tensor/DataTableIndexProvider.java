package com.pb.sawdust.model.models.provider.tensor;

import com.pb.sawdust.tabledata.DataTable;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code DataTableIndexProvider} class is an index provider whose index values are obtained from a {@code DataTable}.
  * The length of this provider is the same as the number of rows in the source data table, and the index lookup values
  * are specified as columns in the data provider. Because the indices must be {@code int}s, it is recommended that data
 * coercion be turned on in the data table before using it an instance of this class.
 *
 * @author crf
 *         Started 4/6/12 8:00 AM
 */
public class DataTableIndexProvider extends VariableIndexProvider<Object> {
    private final DataTable dataTable;

    /**
     * Constructor specifying the source data table, the columns to use for the index lookups, and the index ids.
     *
     * @param dataTable
     *        The source data table.
     *
     * @param indexColumns
     *        The index columns, in order (of the dimensions they corresponding to).
     *
     * @param ids
     *        A mapping from the dimension to use id mapping to the mapping to use. Any dimensions not present in this
     *        map will not use id mapping.
     *
     * @throws IllegalArgumentException if any dimension key in {@code ids} is less than zero or greater than or equal to
     *                                  {@code indexColumns.size()}, or if any column in {@code indexColumns} is not found
     *                                  in {@code dataTable}.
     */
    public DataTableIndexProvider(DataTable dataTable, List<String> indexColumns, Map<Integer,List<?>> ids) {
        super(indexColumns,ids);
        this.dataTable = dataTable;
        for (String column : indexColumns)
            if (!dataTable.hasColumn(column))
                throw new IllegalArgumentException("Column not found in data table: " + column);
    }

    /**
     * Constructor specifying the source data table and the columns to use for the index lookups. No index id mapping will
     * be used.
     *
     * @param dataTable
     *        The source data table.
     *
     * @param indexColumns
     *        The index columns, in order (of the dimensions they corresponding to).
     *
     * @throws IllegalArgumentException if any column in {@code indexColumns} is not found in {@code dataTable}.
     */
    public DataTableIndexProvider(DataTable dataTable, List<String> indexColumns) {
        this(dataTable,indexColumns,new HashMap<Integer,List<?>>());
    }

    @Override
    protected Object getIndexId(String variable, int location) {
        return dataTable.getRow(location).getCell(variable);
    }

    @Override
    protected Object[] getIndexIds(String variable, int start, int end) {
        return dataTable.getTablePartition(start,end).getColumn(variable).getData();
    }

    @Override
    protected int getIndex(String variable, int location) {
        return dataTable.getRow(location).getCellAsInt(variable);
    }

    @Override
    protected int[] getIndices(String variable, int start, int end) {
        return dataTable.getTablePartition(start,end).getIntColumn(variable).getPrimitiveColumn();
    }

    @Override
    public int getIndexLength() {
        return dataTable.getRowCount();
    }
}