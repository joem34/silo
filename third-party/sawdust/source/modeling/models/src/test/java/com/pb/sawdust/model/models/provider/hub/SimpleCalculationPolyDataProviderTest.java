package com.pb.sawdust.model.models.provider.hub;

import com.pb.sawdust.model.models.provider.SimpleDataProvider;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.alias.matrix.id.IdDoubleMatrix;
import com.pb.sawdust.util.collections.LinkedSetList;
import com.pb.sawdust.util.collections.SetList;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code WrappedCalculationPolyDataProviderTest} ...
 *
 * @author crf <br/>
 *         Started 3/8/11 12:23 PM
 */
//todo: add calculated variables and test 'em - actually need abstract class between this and PolyDataProviderTest to capture interface...
public class SimpleCalculationPolyDataProviderTest extends PolyDataProviderTest<String> {
    protected SimpleCalculationPolyDataProvider<String> calculationPolyDataProvider;

    public static void main(String ... args) {
        TestBase.main();
    }

    protected void addSubDataTest(List<Class<? extends TestBase>> additionalClassContainer) {
        super.addSubDataTest(additionalClassContainer);
        additionalClassContainer.add(SimplePolyDataProviderParentTest.class);
    }

    protected SimpleCalculationPolyDataProvider<String> getCalculationProvider(int id, PolyDataProvider<String> baseProvider) {
        return new SimpleCalculationPolyDataProvider<String>(id,baseProvider,ArrayTensor.getFactory());
    }

    @Override
    protected PolyDataProvider<String> getPolyProvider(int id, int dataLength, SetList<String> keys, Map<String,double[]> sharedData, Map<String,Map<String,double[]>> data, Map<String, IdDoubleMatrix<? super String>> polyData) {
        SimplePolyDataProvider<String> pdp = new SimplePolyDataProvider<String>(keys,ArrayTensor.getFactory());
        for (String key : data.keySet())
            pdp.addKeyedProvider(key,new SimpleDataProvider(data.get(key),ArrayTensor.getFactory()));
        pdp.addProvider(new SimpleDataProvider(sharedData,ArrayTensor.getFactory()));
        for (String variable : polyData.keySet())
            pdp.addPolyData(variable,polyData.get(variable));
        return getCalculationProvider(id,pdp);
    }

    @Override
    protected SimplePolyDataProvider<String> getUninitializedProvider(int dataId, SetList<String> keys) {
        return null;
    }

    @Override
    protected SetList<String> getKeys() {
        SetList<String> keys = new LinkedSetList<String>();
        for (int i : range(random.nextInt(3,15)))
            keys.add(random.nextAsciiString(10));
        return keys;
    }

    @Override
    protected String getBadKey() {
        String key;
        while (keys.contains(key = random.nextAsciiString(12)));
        return key;
    }

    @Override
    @Before
    public void beforeTest() {
        super.beforeTest();
        calculationPolyDataProvider = (SimpleCalculationPolyDataProvider<String>) providerHub;
    }

    public static class SimplePolyDataProviderParentTest extends SimpleDataProviderHubTest {
        @Override
        protected DataProviderHub<String> getUninitializedProvider(int id, Set<String> keys) {
            if (id == UNSPECIFIED_ID_ID)
                return new SimplePolyDataProvider<String>(new LinkedSetList<String>(keys), ArrayTensor.getFactory());
            else
                return new SimplePolyDataProvider<String>(id,new LinkedSetList<String>(keys), ArrayTensor.getFactory());
        }
    }
}