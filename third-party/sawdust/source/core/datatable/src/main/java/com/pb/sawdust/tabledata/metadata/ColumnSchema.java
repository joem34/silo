package com.pb.sawdust.tabledata.metadata;


/**
 * The {@code ColumnSchema} class provides a representation of a data table column's structure.
 *
 * @author crf <br/>
 *         Started: May 19, 2008 4:12:16 PM
 */
public class ColumnSchema {
    private String columnLabel;
    private DataType type;

    /**
     * Constructor specifying the column label and data type.
     *
     * @param columnLabel
     *        The column's name.
     *
     * @param type
     *        The data type held by the column.
     */
    public ColumnSchema(String columnLabel,DataType type) {
        this.columnLabel = columnLabel;
        this.type = type;
    }

    /**
     * Get the label for the column.
     *
     * @return the column's name.
     */
    public String getColumnLabel() {
        return columnLabel;
    }

    /**
     * Get the column's data type.
     *
     * @return the data type held by the column.
     */
    public DataType getType() {
        return type;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ColumnSchema))
            return false;
        ColumnSchema schema = (ColumnSchema) o;
        return columnLabel.equals(schema.columnLabel) &&
                type.equals(schema.type);
    }

    public int hashCode() {
        int result = 17;
        result = 31*result + columnLabel.hashCode();
        result = 31*result + type.hashCode();
        return result;

    }
}