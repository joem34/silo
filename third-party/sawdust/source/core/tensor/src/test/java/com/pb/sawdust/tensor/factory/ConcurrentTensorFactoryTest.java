package com.pb.sawdust.tensor.factory;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorFactoryTests;
import com.pb.sawdust.tensor.alias.matrix.TensorMatrixFactoryTests;
import com.pb.sawdust.tensor.alias.matrix.id.TensorMatrixIdFactoryTests;
import com.pb.sawdust.tensor.alias.matrix.primitive.TensorMatrixPrimitiveFactoryTests;
import com.pb.sawdust.tensor.alias.scalar.TensorScalarFactoryTests;
import com.pb.sawdust.tensor.alias.scalar.id.TensorScalarIdFactoryTests;
import com.pb.sawdust.tensor.alias.scalar.primitive.TensorScalarPrimitiveFactoryTests;
import com.pb.sawdust.tensor.alias.vector.TensorVectorFactoryTests;
import com.pb.sawdust.tensor.alias.vector.id.TensorVectorIdFactoryTests;
import com.pb.sawdust.tensor.alias.vector.primitive.TensorVectorPrimitiveFactoryTests;
import com.pb.sawdust.tensor.decorators.id.TensorIdFactoryTests;
import com.pb.sawdust.tensor.decorators.id.IdTensor;
import com.pb.sawdust.tensor.decorators.id.primitive.*;
import com.pb.sawdust.tensor.decorators.id.primitive.size.TensorIdPrimitiveSizeFactoryTests;
import com.pb.sawdust.tensor.decorators.id.size.TensorIdSizeFactoryTests;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.decorators.primitive.size.TensorPrimitiveSizeFactoryTests;
import com.pb.sawdust.tensor.decorators.size.TensorSizeFactoryTests;
import com.pb.sawdust.tensor.read.TensorReader;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * The {@code ConcurrentTensorFactoryTest} ...
 *
 * @author crf <br/>
 *         Started Dec 24, 2010 11:21:27 AM
 */
public abstract class ConcurrentTensorFactoryTest extends TestBase {

    abstract protected ConcurrentTensorFactory getFactory();

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        addTestData(TensorFactoryTest.TENSOR_FACTORY_KEY,new ConcurrentTensorFactoryWrap(getFactory()));
        List<Class<? extends TestBase>> adds = new LinkedList<Class<? extends TestBase>>();
        adds.addAll(TensorFactoryTests.TEST_CLASSES);
        adds.addAll(TensorSizeFactoryTests.TEST_CLASSES);
        adds.addAll(TensorPrimitiveFactoryTests.TEST_CLASSES);
        adds.addAll(TensorPrimitiveSizeFactoryTests.TEST_CLASSES);
        adds.addAll(TensorIdFactoryTests.TEST_CLASSES);
        adds.addAll(TensorIdSizeFactoryTests.TEST_CLASSES);
        adds.addAll(TensorIdPrimitiveFactoryTests.TEST_CLASSES);
        adds.addAll(TensorIdPrimitiveSizeFactoryTests.TEST_CLASSES);
        adds.addAll(TensorScalarFactoryTests.TEST_CLASSES);
        adds.addAll(TensorScalarPrimitiveFactoryTests.TEST_CLASSES);
        adds.addAll(TensorScalarIdFactoryTests.TEST_CLASSES);
        adds.addAll(TensorVectorFactoryTests.TEST_CLASSES);
        adds.addAll(TensorVectorPrimitiveFactoryTests.TEST_CLASSES);
        adds.addAll(TensorVectorIdFactoryTests.TEST_CLASSES);
        adds.addAll(TensorMatrixFactoryTests.TEST_CLASSES);
        adds.addAll(TensorMatrixPrimitiveFactoryTests.TEST_CLASSES);
        adds.addAll(TensorMatrixIdFactoryTests.TEST_CLASSES);
        return adds;
    }

    @Test
    public void placeholderTest() { }

    private static class ConcurrentTensorFactoryWrap extends AbstractTensorFactory {
        private final ConcurrentTensorFactory factory;

        public <T> Tensor<T> copyTensor(Tensor<T> tensor) {
            return factory.copyTensor(tensor,getConcurrencyLevel(tensor.size()));
        }

        public <T, I> IdTensor<T, I> copyTensor(IdTensor<T, I> tensor) {
            return factory.copyTensor(tensor,getConcurrencyLevel(tensor.size()));
        }

        private final Random random = new Random();

        ConcurrentTensorFactoryWrap(ConcurrentTensorFactory factory) {
            this.factory = factory;
        }

        private int getConcurrencyLevel(int dimensionCount) {
            return dimensionCount == 0 ? 0 :Math.min(9,random.nextInt(dimensionCount));
        }

        public ByteTensor byteTensor(int... dimensions) {
            return concurrentByteTensor(getConcurrencyLevel(dimensions.length),dimensions);
        }

        public ByteTensor initializedByteTensor(byte defaultValue, int... dimensions) {
            return initializedConcurrentByteTensor(defaultValue,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdByteTensor<I> byteTensor(List<List<I>> ids, int... dimensions) {
            return concurrentByteTensor(ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdByteTensor<I> initializedByteTensor(byte defaultValue, List<List<I>> ids, int... dimensions) {
            return initializedConcurrentByteTensor(defaultValue,ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdByteTensor<I> byteTensor(I[][] ids, int... dimensions) {
            return concurrentByteTensor(ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdByteTensor<I> initializedByteTensor(byte defaultValue, I[][] ids, int... dimensions) {
            return initializedConcurrentByteTensor(defaultValue,ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public ByteTensor concurrentByteTensor(int concurrencyLevel, int... dimensions) {
            return factory.concurrentByteTensor(concurrencyLevel,dimensions);
        }

        public ByteTensor initializedConcurrentByteTensor(byte defaultValue, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentByteTensor(defaultValue,concurrencyLevel,dimensions);
        }

        public <I> IdByteTensor<I> concurrentByteTensor(List<List<I>> ids, int concurrencyLevel, int... dimensions) {
            return factory.concurrentByteTensor(ids,concurrencyLevel,dimensions);
        }

        public <I> IdByteTensor<I> initializedConcurrentByteTensor(byte defaultValue, List<List<I>> ids, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentByteTensor(defaultValue,ids,concurrencyLevel,dimensions);
        }

        public <I> IdByteTensor<I> concurrentByteTensor(I[][] ids, int concurrencyLevel, int... dimensions) {
            return factory.concurrentByteTensor(ids,concurrencyLevel,dimensions);
        }

        public <I> IdByteTensor<I> initializedConcurrentByteTensor(byte defaultValue, I[][] ids, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentByteTensor(defaultValue,ids,concurrencyLevel,dimensions);
        }

        public ShortTensor shortTensor(int... dimensions) {
            return concurrentShortTensor(getConcurrencyLevel(dimensions.length),dimensions);
        }

        public ShortTensor initializedShortTensor(short defaultValue, int... dimensions) {
            return initializedConcurrentShortTensor(defaultValue,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdShortTensor<I> shortTensor(List<List<I>> ids, int... dimensions) {
            return concurrentShortTensor(ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdShortTensor<I> initializedShortTensor(short defaultValue, List<List<I>> ids, int... dimensions) {
            return initializedConcurrentShortTensor(defaultValue,ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdShortTensor<I> shortTensor(I[][] ids, int... dimensions) {
            return concurrentShortTensor(ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdShortTensor<I> initializedShortTensor(short defaultValue, I[][] ids, int... dimensions) {
            return initializedConcurrentShortTensor(defaultValue,ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public ShortTensor concurrentShortTensor(int concurrencyLevel, int... dimensions) {
            return factory.concurrentShortTensor(concurrencyLevel,dimensions);
        }

        public ShortTensor initializedConcurrentShortTensor(short defaultValue, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentShortTensor(defaultValue,concurrencyLevel,dimensions);
        }

        public <I> IdShortTensor<I> concurrentShortTensor(List<List<I>> ids, int concurrencyLevel, int... dimensions) {
            return factory.concurrentShortTensor(ids,concurrencyLevel,dimensions);
        }

        public <I> IdShortTensor<I> initializedConcurrentShortTensor(short defaultValue, List<List<I>> ids, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentShortTensor(defaultValue,ids,concurrencyLevel,dimensions);
        }

        public <I> IdShortTensor<I> concurrentShortTensor(I[][] ids, int concurrencyLevel, int... dimensions) {
            return factory.concurrentShortTensor(ids,concurrencyLevel,dimensions);
        }

        public <I> IdShortTensor<I> initializedConcurrentShortTensor(short defaultValue, I[][] ids, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentShortTensor(defaultValue,ids,concurrencyLevel,dimensions);
        }

        public IntTensor intTensor(int... dimensions) {
            return concurrentIntTensor(getConcurrencyLevel(dimensions.length),dimensions);
        }

        public IntTensor initializedIntTensor(int defaultValue, int... dimensions) {
            return initializedConcurrentIntTensor(defaultValue,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdIntTensor<I> intTensor(List<List<I>> ids, int... dimensions) {
            return concurrentIntTensor(ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdIntTensor<I> initializedIntTensor(int defaultValue, List<List<I>> ids, int... dimensions) {
            return initializedConcurrentIntTensor(defaultValue,ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdIntTensor<I> intTensor(I[][] ids, int... dimensions) {
            return concurrentIntTensor(ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdIntTensor<I> initializedIntTensor(int defaultValue, I[][] ids, int... dimensions) {
            return initializedConcurrentIntTensor(defaultValue,ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public IntTensor concurrentIntTensor(int concurrencyLevel, int... dimensions) {
            return factory.concurrentIntTensor(concurrencyLevel,dimensions);
        }

        public IntTensor initializedConcurrentIntTensor(int defaultValue, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentIntTensor(defaultValue,concurrencyLevel,dimensions);
        }

        public <I> IdIntTensor<I> concurrentIntTensor(List<List<I>> ids, int concurrencyLevel, int... dimensions) {
            return factory.concurrentIntTensor(ids,concurrencyLevel,dimensions);
        }

        public <I> IdIntTensor<I> initializedConcurrentIntTensor(int defaultValue, List<List<I>> ids, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentIntTensor(defaultValue,ids,concurrencyLevel,dimensions);
        }

        public <I> IdIntTensor<I> concurrentIntTensor(I[][] ids, int concurrencyLevel, int... dimensions) {
            return factory.concurrentIntTensor(ids,concurrencyLevel,dimensions);
        }

        public <I> IdIntTensor<I> initializedConcurrentIntTensor(int defaultValue, I[][] ids, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentIntTensor(defaultValue,ids,concurrencyLevel,dimensions);
        }

        public LongTensor longTensor(int... dimensions) {
            return concurrentLongTensor(getConcurrencyLevel(dimensions.length),dimensions);
        }

        public LongTensor initializedLongTensor(long defaultValue, int... dimensions) {
            return initializedConcurrentLongTensor(defaultValue,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdLongTensor<I> longTensor(List<List<I>> ids, int... dimensions) {
            return concurrentLongTensor(ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdLongTensor<I> initializedLongTensor(long defaultValue, List<List<I>> ids, int... dimensions) {
            return initializedConcurrentLongTensor(defaultValue,ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdLongTensor<I> longTensor(I[][] ids, int... dimensions) {
            return concurrentLongTensor(ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdLongTensor<I> initializedLongTensor(long defaultValue, I[][] ids, int... dimensions) {
            return initializedConcurrentLongTensor(defaultValue,ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public LongTensor concurrentLongTensor(int concurrencyLevel, int... dimensions) {
            return factory.concurrentLongTensor(concurrencyLevel,dimensions);
        }

        public LongTensor initializedConcurrentLongTensor(long defaultValue, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentLongTensor(defaultValue,concurrencyLevel,dimensions);
        }

        public <I> IdLongTensor<I> concurrentLongTensor(List<List<I>> ids, int concurrencyLevel, int... dimensions) {
            return factory.concurrentLongTensor(ids,concurrencyLevel,dimensions);
        }

        public <I> IdLongTensor<I> initializedConcurrentLongTensor(long defaultValue, List<List<I>> ids, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentLongTensor(defaultValue,ids,concurrencyLevel,dimensions);
        }

        public <I> IdLongTensor<I> concurrentLongTensor(I[][] ids, int concurrencyLevel, int... dimensions) {
            return factory.concurrentLongTensor(ids,concurrencyLevel,dimensions);
        }

        public <I> IdLongTensor<I> initializedConcurrentLongTensor(long defaultValue, I[][] ids, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentLongTensor(defaultValue,ids,concurrencyLevel,dimensions);
        }

        public FloatTensor floatTensor(int... dimensions) {
            return concurrentFloatTensor(getConcurrencyLevel(dimensions.length),dimensions);
        }

        public FloatTensor initializedFloatTensor(float defaultValue, int... dimensions) {
            return initializedConcurrentFloatTensor(defaultValue,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdFloatTensor<I> floatTensor(List<List<I>> ids, int... dimensions) {
            return concurrentFloatTensor(ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdFloatTensor<I> initializedFloatTensor(float defaultValue, List<List<I>> ids, int... dimensions) {
            return initializedConcurrentFloatTensor(defaultValue,ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdFloatTensor<I> floatTensor(I[][] ids, int... dimensions) {
            return concurrentFloatTensor(ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdFloatTensor<I> initializedFloatTensor(float defaultValue, I[][] ids, int... dimensions) {
            return initializedConcurrentFloatTensor(defaultValue,ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public FloatTensor concurrentFloatTensor(int concurrencyLevel, int... dimensions) {
            return factory.concurrentFloatTensor(concurrencyLevel,dimensions);
        }

        public FloatTensor initializedConcurrentFloatTensor(float defaultValue, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentFloatTensor(defaultValue,concurrencyLevel,dimensions);
        }

        public <I> IdFloatTensor<I> concurrentFloatTensor(List<List<I>> ids, int concurrencyLevel, int... dimensions) {
            return factory.concurrentFloatTensor(ids,concurrencyLevel,dimensions);
        }

        public <I> IdFloatTensor<I> initializedConcurrentFloatTensor(float defaultValue, List<List<I>> ids, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentFloatTensor(defaultValue,ids,concurrencyLevel,dimensions);
        }

        public <I> IdFloatTensor<I> concurrentFloatTensor(I[][] ids, int concurrencyLevel, int... dimensions) {
            return factory.concurrentFloatTensor(ids,concurrencyLevel,dimensions);
        }

        public <I> IdFloatTensor<I> initializedConcurrentFloatTensor(float defaultValue, I[][] ids, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentFloatTensor(defaultValue,ids,concurrencyLevel,dimensions);
        }

        public DoubleTensor doubleTensor(int... dimensions) {
            return concurrentDoubleTensor(getConcurrencyLevel(dimensions.length),dimensions);
        }

        public DoubleTensor initializedDoubleTensor(double defaultValue, int... dimensions) {
            return initializedConcurrentDoubleTensor(defaultValue,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdDoubleTensor<I> doubleTensor(List<List<I>> ids, int... dimensions) {
            return concurrentDoubleTensor(ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdDoubleTensor<I> initializedDoubleTensor(double defaultValue, List<List<I>> ids, int... dimensions) {
            return initializedConcurrentDoubleTensor(defaultValue,ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdDoubleTensor<I> doubleTensor(I[][] ids, int... dimensions) {
            return concurrentDoubleTensor(ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdDoubleTensor<I> initializedDoubleTensor(double defaultValue, I[][] ids, int... dimensions) {
            return initializedConcurrentDoubleTensor(defaultValue,ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public DoubleTensor concurrentDoubleTensor(int concurrencyLevel, int... dimensions) {
            return factory.concurrentDoubleTensor(concurrencyLevel,dimensions);
        }

        public DoubleTensor initializedConcurrentDoubleTensor(double defaultValue, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentDoubleTensor(defaultValue,concurrencyLevel,dimensions);
        }

        public <I> IdDoubleTensor<I> concurrentDoubleTensor(List<List<I>> ids, int concurrencyLevel, int... dimensions) {
            return factory.concurrentDoubleTensor(ids,concurrencyLevel,dimensions);
        }

        public <I> IdDoubleTensor<I> initializedConcurrentDoubleTensor(double defaultValue, List<List<I>> ids, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentDoubleTensor(defaultValue,ids,concurrencyLevel,dimensions);
        }

        public <I> IdDoubleTensor<I> concurrentDoubleTensor(I[][] ids, int concurrencyLevel, int... dimensions) {
            return factory.concurrentDoubleTensor(ids,concurrencyLevel,dimensions);
        }

        public <I> IdDoubleTensor<I> initializedConcurrentDoubleTensor(double defaultValue, I[][] ids, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentDoubleTensor(defaultValue,ids,concurrencyLevel,dimensions);
        }

        public CharTensor charTensor(int... dimensions) {
            return concurrentCharTensor(getConcurrencyLevel(dimensions.length),dimensions);
        }

        public CharTensor initializedCharTensor(char defaultValue, int... dimensions) {
            return initializedConcurrentCharTensor(defaultValue,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdCharTensor<I> charTensor(List<List<I>> ids, int... dimensions) {
            return concurrentCharTensor(ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdCharTensor<I> initializedCharTensor(char defaultValue, List<List<I>> ids, int... dimensions) {
            return initializedConcurrentCharTensor(defaultValue,ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdCharTensor<I> charTensor(I[][] ids, int... dimensions) {
            return concurrentCharTensor(ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdCharTensor<I> initializedCharTensor(char defaultValue, I[][] ids, int... dimensions) {
            return initializedConcurrentCharTensor(defaultValue,ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public CharTensor concurrentCharTensor(int concurrencyLevel, int... dimensions) {
            return factory.concurrentCharTensor(concurrencyLevel,dimensions);
        }

        public CharTensor initializedConcurrentCharTensor(char defaultValue, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentCharTensor(defaultValue,concurrencyLevel,dimensions);
        }

        public <I> IdCharTensor<I> concurrentCharTensor(List<List<I>> ids, int concurrencyLevel, int... dimensions) {
            return factory.concurrentCharTensor(ids,concurrencyLevel,dimensions);
        }

        public <I> IdCharTensor<I> initializedConcurrentCharTensor(char defaultValue, List<List<I>> ids, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentCharTensor(defaultValue,ids,concurrencyLevel,dimensions);
        }

        public <I> IdCharTensor<I> concurrentCharTensor(I[][] ids, int concurrencyLevel, int... dimensions) {
            return factory.concurrentCharTensor(ids,concurrencyLevel,dimensions);
        }

        public <I> IdCharTensor<I> initializedConcurrentCharTensor(char defaultValue, I[][] ids, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentCharTensor(defaultValue,ids,concurrencyLevel,dimensions);
        }

        public BooleanTensor booleanTensor(int... dimensions) {
            return concurrentBooleanTensor(getConcurrencyLevel(dimensions.length),dimensions);
        }

        public BooleanTensor initializedBooleanTensor(boolean defaultValue, int... dimensions) {
            return initializedConcurrentBooleanTensor(defaultValue,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdBooleanTensor<I> booleanTensor(List<List<I>> ids, int... dimensions) {
            return concurrentBooleanTensor(ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdBooleanTensor<I> initializedBooleanTensor(boolean defaultValue, List<List<I>> ids, int... dimensions) {
            return initializedConcurrentBooleanTensor(defaultValue,ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdBooleanTensor<I> booleanTensor(I[][] ids, int... dimensions) {
            return concurrentBooleanTensor(ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <I> IdBooleanTensor<I> initializedBooleanTensor(boolean defaultValue, I[][] ids, int... dimensions) {
            return initializedConcurrentBooleanTensor(defaultValue,ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public BooleanTensor concurrentBooleanTensor(int concurrencyLevel, int... dimensions) {
            return factory.concurrentBooleanTensor(concurrencyLevel,dimensions);
        }

        public BooleanTensor initializedConcurrentBooleanTensor(boolean defaultValue, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentBooleanTensor(defaultValue,concurrencyLevel,dimensions);
        }

        public <I> IdBooleanTensor<I> concurrentBooleanTensor(List<List<I>> ids, int concurrencyLevel, int... dimensions) {
            return factory.concurrentBooleanTensor(ids,concurrencyLevel,dimensions);
        }

        public <I> IdBooleanTensor<I> initializedConcurrentBooleanTensor(boolean defaultValue, List<List<I>> ids, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentBooleanTensor(defaultValue,ids,concurrencyLevel,dimensions);
        }

        public <I> IdBooleanTensor<I> concurrentBooleanTensor(I[][] ids, int concurrencyLevel, int... dimensions) {
            return factory.concurrentBooleanTensor(ids,concurrencyLevel,dimensions);
        }

        public <I> IdBooleanTensor<I> initializedConcurrentBooleanTensor(boolean defaultValue, I[][] ids, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentBooleanTensor(defaultValue,ids,concurrencyLevel,dimensions);
        }

        public <T> Tensor<T> tensor(int... dimensions) {
            return concurrentTensor(getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <T> Tensor<T> initializedTensor(T defaultValue, int... dimensions) {
            return initializedConcurrentTensor(defaultValue,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <T,I> IdTensor<T,I> tensor(List<List<I>> ids, int... dimensions) {
            return concurrentTensor(ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <T,I> IdTensor<T,I> initializedTensor(T defaultValue, List<List<I>> ids, int... dimensions) {
            return initializedConcurrentTensor(defaultValue,ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <T,I> IdTensor<T,I> tensor(I[][] ids, int... dimensions) {
            return concurrentTensor(ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <T,I> IdTensor<T,I> initializedTensor(T defaultValue, I[][] ids, int... dimensions) {
            return initializedConcurrentTensor(defaultValue,ids,getConcurrencyLevel(dimensions.length),dimensions);
        }

        public <T> Tensor<T> concurrentTensor(int concurrencyLevel, int... dimensions) {
            return factory.concurrentTensor(concurrencyLevel,dimensions);
        }

        public <T> Tensor<T> initializedConcurrentTensor(T defaultValue, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentTensor(defaultValue,concurrencyLevel,dimensions);
        }

        public <T,I> IdTensor<T,I> concurrentTensor(List<List<I>> ids, int concurrencyLevel, int... dimensions) {
            return factory.concurrentTensor(ids,concurrencyLevel,dimensions);
        }

        public <T,I> IdTensor<T,I> initializedConcurrentTensor(T defaultValue, List<List<I>> ids, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentTensor(defaultValue,ids,concurrencyLevel,dimensions);
        }

        public <T,I> IdTensor<T,I> concurrentTensor(I[][] ids, int concurrencyLevel, int... dimensions) {
            return factory.concurrentTensor(ids,concurrencyLevel,dimensions);
        }

        public <T,I> IdTensor<T,I> initializedConcurrentTensor(T defaultValue, I[][] ids, int concurrencyLevel, int... dimensions) {
            return factory.initializedConcurrentTensor(defaultValue,ids,concurrencyLevel,dimensions);
        }

        public <T,I> Tensor<T> tensor(TensorReader<T,I> reader) {
            return factory.concurrentTensor(reader,getConcurrencyLevel(reader.getDimensions().length));
        }

        public <T,I> Tensor<T> concurrentTensor(TensorReader<T,I> reader, int concurrencyLevel) {
            return factory.concurrentTensor(reader,concurrencyLevel);
        }
    }
}