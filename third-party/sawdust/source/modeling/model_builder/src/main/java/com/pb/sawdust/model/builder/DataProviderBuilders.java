package com.pb.sawdust.model.builder;

import com.pb.sawdust.excel.tabledata.read.ExcelTableReader;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.DataTableDataProvider;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.basic.RowDataTable;
import com.pb.sawdust.tabledata.read.CsvTableReader;
import com.pb.sawdust.tabledata.read.DbfTableReader;
import com.pb.sawdust.tabledata.read.TableReader;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.exceptions.RuntimeWrappingException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;

/**
 * The {@code DataProviderBuilders} ...
 *
 * @author crf
 *         Started 5/29/12 6:08 AM
 */
public class DataProviderBuilders {

    private static abstract class DataTableProviderBuilder implements DataProviderBuilder {
        private final TensorFactory factory;
        private final String tableName;

        protected DataTableProviderBuilder(String tableName, TensorFactory factory) {
            this.factory = factory;
            this.tableName = tableName;
        }

        protected TensorFactory getTensorFactory() {
            return factory;
        }

        protected String getTableName() {
            return tableName;
        }

        public abstract DataTable getDataTable();

        @Override
        public DataProvider getProvider() {
            return new DataTableDataProvider(getDataTable(),getTensorFactory());
        }
    }

    private static abstract class FileDataTableProviderBuilder extends DataTableProviderBuilder {
        private final Path tableFile;

        protected FileDataTableProviderBuilder(Path tableFile, String tableName, TensorFactory factory) {
            super(tableName,factory);
            this.tableFile = tableFile;
        }

        protected Path getTableFile() {
            return tableFile;
        }

        protected DataTable buildDataTable(TableReader reader) {
            return new RowDataTable(reader);
        }

        protected abstract TableReader getTableReader();

        @Override
        public DataTable getDataTable() {
            return buildDataTable(getTableReader());
        }
    }

    public static class CsvDataTableProviderBuilder extends FileDataTableProviderBuilder {

        public CsvDataTableProviderBuilder(Path tableFile, String tableName, TensorFactory factory) {
            super(tableFile,tableName,factory);
        }

        @Override
        public TableReader getTableReader() {
            return new CsvTableReader(getTableFile().toString(),getTableName());
        }
    }

    public static class DbfDataTableProviderBuilder extends FileDataTableProviderBuilder {

        public DbfDataTableProviderBuilder(Path tableFile, String tableName, TensorFactory factory) {
            super(tableFile,tableName,factory);
        }

        @Override
        public TableReader getTableReader() {
            return new DbfTableReader(getTableFile().toString(),getTableName());
        }
    }

    public static class ExcelDataTableProviderBuilder extends FileDataTableProviderBuilder {

        public ExcelDataTableProviderBuilder(Path tableFile, String tableName, TensorFactory factory) {
            super(tableFile,tableName,factory);
        }

        @Override
        public TableReader getTableReader() {
            return ExcelTableReader.excelTableReader(getTableFile().toString(),getTableName());
        }
    }

    public static class JavaStaticDataTableProviderBuilder extends DataTableProviderBuilder {
        private final String className;
        private final String methodName;

        public JavaStaticDataTableProviderBuilder(String tableName, TensorFactory factory, String className, String methodName) {
            super(tableName,factory);
            this.className = className;
            this.methodName = methodName;
        }

        @Override
        public DataTable getDataTable() {
            try {
                Class<?> sourceClass = Class.forName(className);
                Method method = sourceClass.getMethod(methodName);
                if (!Modifier.isStatic(method.getModifiers()))
                    throw new IllegalStateException("Method " + methodName + " is not static on " + className);
                if (!DataTable.class.isAssignableFrom(method.getReturnType()))
                    throw new IllegalStateException("Static method " + className + "." + methodName + " does not return a DataTable");
                return (DataTable) method.invoke(null);
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeWrappingException(e);
            }
        }
    }
}