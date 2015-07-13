package com.pb.sawdust.tabledata.sql.impl.sqlite;

import com.pb.sawdust.tabledata.sql.SqlDataSetTest;
import com.pb.sawdust.tabledata.sql.SqlDataSet;
import com.pb.sawdust.tabledata.sql.impl.SqlImplTestUtil;
import com.pb.sawdust.util.test.TestBase;
import com.pb.sawdust.util.Filter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author crf <br/>
 *         Started: Dec 2, 2008 10:12:34 AM
 */
public class SqliteSqlDataSetTest extends SqlDataSetTest {

    public static void main(String ... args) {
        TestBase.main();
        if (SqlImplTestUtil.shouldPerformTestFinishOperations(args))
            SqlImplTestUtil.performTestFinishOperations();
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Map<String,Object>> context = new LinkedList<Map<String,Object>>();
        for (SqlitePackageTests.SqliteDataSetType dataSetType : SqlitePackageTests.SqliteDataSetType.values())
            context.add(buildContext(SqlitePackageTests.SQLITE_DATA_SET_TYPE_KEY,dataSetType));
        addClassRunContext(this.getClass(),context);
        return super.getAdditionalTestClasses();
    }

    protected SqlDataSet getDistinctSqlDataSet() {
        return new SqliteMemoryDataSet("distinct" + Thread.currentThread().getId());
    }

    protected SqlDataSet getDataSet() {
        return SqlitePackageTests.getDataSet((SqlitePackageTests.SqliteDataSetType) getTestData(SqlitePackageTests.SQLITE_DATA_SET_TYPE_KEY));
    }

     protected Filter<String> getTableDropFilter() {
        return new Filter<String>() {
            public boolean filter(String input) {
                return input.length() < 7 || !input.substring(0,7).equalsIgnoreCase("SQLITE_");
//                return input.indexOf("SQLITE_") != 0;
            }
        };
    }
}