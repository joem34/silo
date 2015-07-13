package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.util.test.TestBase;

import static com.pb.sawdust.util.Range.range;
import org.junit.Test;

/**
 * The {@code AbstractIdDataTest} ...
 *
 * @author crf <br/>
 *         Started Sep 25, 2010 7:58:53 AM
 */
public class AbstractIdDataTest extends IdDataTest {

    public static void main(String ... args) {
        TestBase.main();
        ConcreteAbstractIdDataTest.main();
    }

    protected IdData getConcreteDataId(int id) {
        return new ConcreteIdData(id);
    }

    @Override
    protected IdData getIdData(int id) {
        int startId = Integer.parseInt(System.getProperty(AbstractIdData.INITIAL_DATA_ID_PROPERTY,"0"));
        for (int i : range(startId,id+1))
            new ConcreteIdData();
        return getConcreteDataId(id);
    }

    private static class ConcreteIdData extends AbstractIdData {
        ConcreteIdData() {
            super();
        }
        ConcreteIdData(int id) {
            super(id);
        }
    }

    public static class ConcreteAbstractIdDataTest extends AbstractIdDataTest {

        public static void main() {
            TestBase.main();
        }

        @Test(expected=IllegalArgumentException.class)
        public void testInvalidIdTooLow() {
            //assumes that we haven't used all the ids yet - should be ok assumption
            new ConcreteIdData(Integer.parseInt(System.getProperty(AbstractIdData.INITIAL_DATA_ID_PROPERTY,"0"))-1);
        }

        @Test(expected=IllegalArgumentException.class)
        public void testInvalidIdTooHigh() {
            //assumes that we haven't done 10,000 ids between the two calls
            int id = new ConcreteIdData().getDataId();
            new ConcreteIdData(id+10000);
        }

    }
}