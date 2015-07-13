package com.pb.sawdust.data.census.pums;

import java.util.Objects;

/**
 * The {@code Puma} class represents a single Census Public Use Microdata Area (PUMA). A PUMA is uniquely identified by
 * its state and PUMA code, and this class ensures that each individual PUMA is uniquely identified.
 *
 * @author crf
 *         Started 10/13/11 8:08 AM
 */
public class Puma {
    private final int compositeCode;

    /**
     * Constructor specifying the state FIPS and PUMA codes.
     *
     * @param stateFips
     *        The state FIPS code.
     *
     * @param puma
     *        The PUMA code.
     */
    public Puma(int stateFips, String puma) {
        this(stateFips,Integer.parseInt(puma));
    }

    /**
     * Constructor specifying the state FIPS and PUMA codes.
     *
     * @param stateFips
     *        The state FIPS code.
     *
     * @param puma
     *        The PUMA code.
     */
    public Puma(int stateFips, int puma) {
        compositeCode = 1000*puma+stateFips;
    }

    @Override
    public int hashCode() {
        return Objects.hash(compositeCode);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (this == o)
            return true;
        if (o instanceof Puma)
            return compositeCode == ((Puma) o).compositeCode;
        return false;
    }
}