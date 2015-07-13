package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.TableIndex;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.AbstractTableIndexTest;
import com.pb.sawdust.util.test.TestBase;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;

/**
 * @author crf <br/>
 *         Started: Sep 25, 2008 2:26:00 PM
 */
public class BasicTableIndexTest<I,K> extends AbstractTableIndexTest<I,K> {

    public static void main(String ... args) {
        TestBase.main();
    }

    protected TableIndex<I> getTableIndex(String[] indexColumnLabels, DataTable table) {
        return new BasicTableIndex<I>(table,indexColumnLabels);
    }

    @SuppressWarnings("unchecked") //a valid warning, butframework will prevent errors
    public void testBuildIndexKey() {
        Object[] a = new Object[] {"",7,false};
        List<Object> key = Arrays.asList(a);
        assertEquals(key,getBuildIndexKey((I[]) a));
    }
}