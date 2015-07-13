package com.pb.sawdust.calculator.tensor.la.mm;

import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code MatrixMultiplicationFactory} ...
 *
 * @author crf
 *         Started 10/18/11 10:15 AM
 */
public class MatrixMultiplicationFactory {
    public enum MatrixMultiplicationPriority {
        HIGH,LOW
    }

    public interface MatrixMultiplicationProvider {
        MatrixMultiplication getMatrixMultiplication(TensorFactory factory);
        boolean isAvailable();
        Class<? extends MatrixMultiplication> getMatrixMultiplicationClass();
    }

    private static final List<MatrixMultiplicationProvider> PROVIDERS = new LinkedList<>();

    static {
        addProvider(new MatrixMultiplicationProvider() {
            @Override
            public MatrixMultiplication getMatrixMultiplication(TensorFactory factory) {
                return new DefaultMatrixMultiplication(factory);
            }

            @Override
            public boolean isAvailable() {
                return true;
            }

            @Override
            public Class<? extends MatrixMultiplication> getMatrixMultiplicationClass() {
                return DefaultMatrixMultiplication.class;
            }
        });
    }

    public static void addProvider(MatrixMultiplicationProvider provider, MatrixMultiplicationPriority priority) {
        for (MatrixMultiplicationProvider p : PROVIDERS)
            if (p.getMatrixMultiplicationClass() == provider.getMatrixMultiplicationClass())
                throw new IllegalArgumentException("Provider already exists in providers list.");
        if (priority == MatrixMultiplicationPriority.HIGH)
            PROVIDERS.add(0,provider);
        else
            PROVIDERS.add(provider);
    }

    public static void addProvider(MatrixMultiplicationProvider provider) {
        addProvider(provider,MatrixMultiplicationPriority.HIGH);
    }

    public static MatrixMultiplication getMatrixMultiplication(TensorFactory factory) {
        for (MatrixMultiplicationProvider provider : PROVIDERS)
            if (provider.isAvailable())
                return provider.getMatrixMultiplication(factory);
        throw new IllegalStateException("No providers found.");
    }
}