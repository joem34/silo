package com.pb.sawdust.tensor.alias.vector.id;

import com.pb.sawdust.tensor.TensorFactoryTests;
import com.pb.sawdust.tensor.decorators.id.TensorIdFactoryTests;
import com.pb.sawdust.tensor.factory.TensorFactoryTest;
import com.pb.sawdust.tensor.decorators.id.primitive.size.*;
import com.pb.sawdust.tensor.decorators.id.size.IdD1Tensor;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.array.*;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code VectorIdPackageFactoryTests} ...
 *
 * @author crf <br/>
 *         Started Dec 24, 2010 10:42:23 AM
 */
public class TensorVectorIdFactoryTests {
    public static List<Class<? extends TestBase>> TEST_CLASSES = new LinkedList<Class<? extends TestBase>>();
    static {
        TEST_CLASSES.add(TensorFactoryIdVectorTest.class);
        TEST_CLASSES.add(TensorFactoryIdBooleanVectorTest.class);
        TEST_CLASSES.add(TensorFactoryIdCharVectorTest.class);
        TEST_CLASSES.add(TensorFactoryIdByteVectorTest.class);
        TEST_CLASSES.add(TensorFactoryIdShortVectorTest.class);
        TEST_CLASSES.add(TensorFactoryIdIntVectorTest.class);
        TEST_CLASSES.add(TensorFactoryIdLongVectorTest.class);
        TEST_CLASSES.add(TensorFactoryIdFloatVectorTest.class);
        TEST_CLASSES.add(TensorFactoryIdDoubleVectorTest.class);
    }

    public static class TensorFactoryIdVectorTest extends IdVectorTest<Double,String> {
        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected IdD1Tensor<Double,String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdD1Tensor<Double,String> tensor = (IdD1Tensor<Double,String>) factory.<Double,String>tensor(ids, ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }

        protected TypeSafeArray<Double> getData() {
            return TensorFactoryTests.doubleObjectData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected JavaType getJavaType() {
            return JavaType.OBJECT;
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }
    }

    public static class TensorFactoryIdBooleanVectorTest extends IdBooleanVectorTest<String> {

        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected BooleanTypeSafeArray getData() {
            return TensorFactoryTests.booleanData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Boolean getRandomElement() {
            return TensorFactoryTests.randomBoolean(random);
        }

        protected IdBooleanD1Tensor<String> getIdTensor(TypeSafeArray<Boolean> data, List<List<String>> ids) {
            IdBooleanD1Tensor<String> tensor = (IdBooleanD1Tensor<String>) factory.booleanTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdCharVectorTest extends IdCharVectorTest<String> {

        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected CharTypeSafeArray getData() {
            return TensorFactoryTests.charData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Character getRandomElement() {
            return TensorFactoryTests.randomChar(random);
        }

        protected IdCharD1Tensor<String> getIdTensor(TypeSafeArray<Character> data, List<List<String>> ids) {
            IdCharD1Tensor<String> tensor = (IdCharD1Tensor<String>) factory.charTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdByteVectorTest extends IdByteVectorTest<String> {

        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(), TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected ByteTypeSafeArray getData() {
            return TensorFactoryTests.byteData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Byte getRandomElement() {
            return TensorFactoryTests.randomByte(random);
        }

        protected IdByteD1Tensor<String> getIdTensor(TypeSafeArray<Byte> data, List<List<String>> ids) {
            IdByteD1Tensor<String> tensor = (IdByteD1Tensor<String>) factory.byteTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdShortVectorTest extends IdShortVectorTest<String> {

        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected ShortTypeSafeArray getData() {
            return TensorFactoryTests.shortData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Short getRandomElement() {
            return TensorFactoryTests.randomShort(random);
        }

        protected IdShortD1Tensor<String> getIdTensor(TypeSafeArray<Short> data, List<List<String>> ids) {
            IdShortD1Tensor<String> tensor = (IdShortD1Tensor<String>) factory.shortTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdIntVectorTest extends IdIntVectorTest<String> {

        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected IntTypeSafeArray getData() {
            return TensorFactoryTests.intData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Integer getRandomElement() {
            return TensorFactoryTests.randomInt(random);
        }

        protected IdIntD1Tensor<String> getIdTensor(TypeSafeArray<Integer> data, List<List<String>> ids) {
            IdIntD1Tensor<String> tensor = (IdIntD1Tensor<String>) factory.intTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdLongVectorTest extends IdLongVectorTest<String> {

        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected LongTypeSafeArray getData() {
            return TensorFactoryTests.longData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Long getRandomElement() {
            return TensorFactoryTests.randomLong(random);
        }

        protected IdLongD1Tensor<String> getIdTensor(TypeSafeArray<Long> data, List<List<String>> ids) {
            IdLongD1Tensor<String> tensor = (IdLongD1Tensor<String>) factory.longTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdFloatVectorTest extends IdFloatVectorTest<String> {

        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(),TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected FloatTypeSafeArray getData() {
            return TensorFactoryTests.floatData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Float getRandomElement() {
            return TensorFactoryTests.randomFloat(random);
        }

        protected IdFloatD1Tensor<String> getIdTensor(TypeSafeArray<Float> data, List<List<String>> ids) {
            IdFloatD1Tensor<String> tensor = (IdFloatD1Tensor<String>) factory.floatTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }

    public static class TensorFactoryIdDoubleVectorTest extends IdDoubleVectorTest<String> {

        protected TensorFactory factory;

        public static void main(String ... args) {
            TestBase.main();
        }

        @Before
        public void beforeTest() {
            factory = (TensorFactory) getTestData(getCallingContextInstance().getClass(), TensorFactoryTest.TENSOR_FACTORY_KEY);
            super.beforeTest();
        }

        protected List<List<String>> getIds(int ... dimensions) {
            return TensorIdFactoryTests.randomIds(random,dimensions);
        }

        protected DoubleTypeSafeArray getData() {
            return TensorFactoryTests.doubleData(TensorFactoryTests.randomDimensions(random,1),random);
        }

        protected Double getRandomElement() {
            return TensorFactoryTests.randomDouble(random);
        }

        protected IdDoubleD1Tensor<String> getIdTensor(TypeSafeArray<Double> data, List<List<String>> ids) {
            IdDoubleD1Tensor<String> tensor = (IdDoubleD1Tensor<String>) factory.doubleTensor(ids,ArrayUtil.getDimensions(data.getArray()));
            tensor.setTensorValues(data);
            return tensor;
        }
    }
}