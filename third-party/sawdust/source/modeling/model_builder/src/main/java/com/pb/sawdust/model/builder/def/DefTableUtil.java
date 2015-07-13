package com.pb.sawdust.model.builder.def;

import com.pb.sawdust.model.builder.DataTableSources;
import com.pb.sawdust.tabledata.*;
import com.pb.sawdust.tabledata.basic.RowDataTable;
import com.pb.sawdust.tabledata.basic.WrappedDataRow;
import com.pb.sawdust.tabledata.read.TableReader;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.property.PropertyDeluxe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The {@code DefTableUtil} ...
 *
 * @author crf
 *         Started 5/29/12 1:04 PM
 */
public class DefTableUtil {
    private DefTableUtil() {}


    //options need to be key1=value1;key2=value2;...
    public static Map<String,String> parseOptions(String options) {
        Map<String,String> parsedOptions = new HashMap<>();
        options = options.trim();
        if (options.length() == 0)
            return parsedOptions;
        String[] splitOptions = options.split(";");
        for (String splitOption : splitOptions) {
            splitOption = splitOption.trim();
            String[] keyValue = splitOption.split("=",2);
            if (keyValue.length != 2)
                throw new IllegalArgumentException("Invalid option entry (must be of form key=value): " + splitOption);
            parsedOptions.put(keyValue[0].trim(),keyValue[1].trim());
        }
        return parsedOptions;
    }

//    public static String getString(String string,PropertyDeluxe properties) {
//        if (properties != null) {
//            if (string.startsWith(PROPERTY_IDENTIFIER_PREFIX)) {
//                String key = string.substring(PROPERTY_IDENTIFIER_PREFIX.length());
//                if (properties.hasKey(key))
//                    return properties.getString(key);
//            }
//        }
//        return string;
//    }

    //note: only works if getCellAs... methods call getCell first!
    public static class PoundPropertyDataRow extends WrappedDataRow {
        public static final String PROPERTY_IDENTIFIER_PREFIX = "#";
        private final PropertyDeluxe properties;

        public PoundPropertyDataRow(DataRow row, PropertyDeluxe properties) {
            super(row);
            this.properties = properties;
        }

        private Object getValue(Object result) {
            return (result instanceof String) ? getString((String ) result) : result;
        }

        private String getString(String string) {
            if (properties != null) {
                if (string.startsWith(PROPERTY_IDENTIFIER_PREFIX)) {
                    String key = string.substring(PROPERTY_IDENTIFIER_PREFIX.length());
                    if (properties.hasKey(key))
                        return properties.getString(key);
                }
            }
            return string;
        }

        @Override
        public Object[] getData() {
            Object[] r = ArrayUtil.copyArray(super.getData());
            int counter = 0;
            for (Object o : r)
                r[counter++] = getValue(o);
            return r;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCell(int columnIndex) {
            return (T) getValue(row.getCell(columnIndex));
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCell(String columnLabel) {
            return (T) getValue(row.getCell(columnLabel));
        }

        @Override
        public Object[] getIndexValues(TableIndex index) {
            return row.getIndexValues(index);
        }

        @Override
        public String getCellAsString(int columnIndex) {
            return getString(super.getCellAsString(columnIndex));
        }

        @Override
        public String getCellAsString(String columnLabel) {
            return getString(super.getCellAsString(columnLabel));
        }
    }


    public static TensorFactory getTensorFactory(Map<String,String> options, TensorFactory factory) {
        //todo: allow different tensor factories
        return factory;
    }

    public static TensorFactory getTensorFactory(Map<String,String> options) {
        return getTensorFactory(options,ArrayTensor.getFactory());
    }

    public static DataTableSources.DataTableBuilder getTableBuilder(Map<String,String> options) {
        //todo: allow different data tables via options
        return new DataTableSources.DataTableBuilder() {
            @Override
            public DataTable buildTable(TableReader reader) {
                return new RowDataTable(reader);
            }
        };
    }
}