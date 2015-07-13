package com.pb.sawdust.geography;

import com.pb.sawdust.geography.tensor.GeographicBooleanMatrix;
import com.pb.sawdust.geography.tensor.GeographicDoubleMatrix;
import com.pb.sawdust.tensor.SparseTensor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The {@code FunctionalGeographicMapping} class is used for geographic mappings where each of the "to" geography elements
 * overlaps with at most one from the "from" geography. That is, each element the "to" geography is a set of <i>complete</i>
 * "from" elements, and each of these sets is completely disjoint. If this relationship exists between two geographies,
 * then this mapping implementation should be used in preference to {@code FullGeographicMapping}, as it will be much more
 * efficient.
 *
 * @param <F>
 *        The type of the "from" geography.
 *
 * @param <T>
 *        The type of the "to" geography.
 *
 * @author crf
 *         Started 10/17/11 1:10 PM
 */
public class FunctionalGeographicMapping<F extends GeographyElement<?>,T extends GeographyElement<?>> implements GeographicMapping<F,T> {
    private final Geography<?,F> fromGeography;
    private final Geography<?,T> toGeography;
    private final Map<F,T> mapping;
    private final Map<Integer,F> fromMapping;
    private final Map<Integer,T> toMapping;
    private final GeographicBooleanMatrix<F,T> usageMatrix;                                        
    private final GeographicDoubleMatrix<F,T> overlayMatrix;

    /**
     * Constructor specifying the two geographies, and their mapping relationship.
     *
     * @param fromGeography
     *        The "from" geography for the mapping.
     *
     * @param toGeography
     *        The "to" geography for the mapping.
     *
     * @param mapping
     *        A map specifying which "to" geography element each "from" geography element maps to. If a particular "from"
     *        element does not map to any "to" element, then it should either map to {@code null} or be left absent from
     *        the map.
     */
    public FunctionalGeographicMapping(Geography<?,F> fromGeography, Geography<?,T> toGeography, Map<F,T> mapping) {
        this.fromGeography = fromGeography;
        this.toGeography = toGeography;
        this.mapping = new HashMap<>(mapping);
        fromMapping = new HashMap<>();
        int counter = 0;
        for (F from : fromGeography.getGeography())
            fromMapping.put(counter++,from);
        toMapping = new HashMap<>();
        counter = 0;
        for (T to : toGeography.getGeography())
            toMapping.put(counter++,to);
        usageMatrix = new FunctionalUsageMatrix();
        overlayMatrix = new FunctionalOverlayMatrix();
    }

    public static <G extends GeographyElement<?>> FunctionalGeographicMapping<G,G> getDegenerateMapping(Geography<?,G> geography) {
        Map<G,G> mapping = new HashMap<>();
        for (G element : geography.getGeography())
            mapping.put(element,element);
        return new FunctionalGeographicMapping<>(geography,geography,mapping);
    }

    @Override
    public Geography<?,F> getFromGeography() {
        return fromGeography;
    }

    @Override
    public Geography<?,T> getToGeography() {
        return toGeography;
    }

    @Override
    public GeographicDoubleMatrix<F,T> getOverlay() {
        return overlayMatrix;
    }

    @Override
    public GeographicBooleanMatrix<F,T> getUsageOverlay() {
        return usageMatrix;
    }

    private class FunctionalUsageMatrix extends GeographicBooleanMatrix<F,T> {
        public FunctionalUsageMatrix() {
            super(fromGeography,toGeography,SparseTensor.getFactory());
        }

        @Override
        public boolean getCellById(GeographyElement<?> d0id, GeographyElement<?> d1id) {
            if (!fromGeography.getGeography().contains(d0id))
                throw new IllegalArgumentException("Id not found: " + d0id);
            if (!fromGeography.getGeography().contains(d1id))
                throw new IllegalArgumentException("Id not found: " + d1id);
            return getCellByIdUnchecked(d0id,d1id);
        }
        
        @Override
        public boolean getCell(int d0, int d1) {
            GeographyElement<?> element0 = fromMapping.get(d0);
            GeographyElement<?> element1 = toMapping.get(d1);
            if (element0 == null)
                throw new IndexOutOfBoundsException("Tensor dimension 0 index out of bounds: " + d0);
            if (element1 == null)
                throw new IndexOutOfBoundsException("Tensor dimension 1 index out of bounds: " + d1);
            return getCellByIdUnchecked(element0,element1);
        }
        
        private boolean getCellByIdUnchecked(GeographyElement<?> d0id, GeographyElement<?> d1id) {
            return Objects.equals(mapping.get(d0id),d1id);
        }

        @Override
        public void setCell(boolean value, int d0, int d1) {
            throw new UnsupportedOperationException("Usage matrix is unmodifiable.");
        }
    }
    
    private class FunctionalOverlayMatrix extends GeographicDoubleMatrix<F,T> {
        
        public FunctionalOverlayMatrix() {
            super(fromGeography,toGeography,SparseTensor.getFactory());
        }

        @Override
        public double getCellById(GeographyElement<?> d0id, GeographyElement<?> d1id) {
            return usageMatrix.getCellById(d0id,d1id) ? 1.0 : 0.0;
        }
        
        @Override
        public double getCell(int d0, int d1) {
            return usageMatrix.getCell(d0,d1) ? 1.0 : 0.0;
        }

        @Override
        public void setCell(double value, int d0, int d1) {
            throw new UnsupportedOperationException("Overlay matrix is unmodifiable.");
        }
    }
    
        
    
}