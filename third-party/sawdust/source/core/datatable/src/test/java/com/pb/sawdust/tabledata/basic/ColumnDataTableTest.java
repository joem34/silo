package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.util.test.TestBase;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.AbstractDataTableTest;
import com.pb.sawdust.tabledata.metadata.TableSchema;

/**
 * @author crf <br/>
 *         Started: Sep 24, 2008 12:15:36 PM
 */
public class ColumnDataTableTest extends AbstractDataTableTest {

    public static void main(String ... args) {
        TestBase.main();
    }

    protected DataTable getDataTable(Object[][] tableData, TableSchema schema) {
        return new ColumnDataTable(schema,tableData);
    }
}