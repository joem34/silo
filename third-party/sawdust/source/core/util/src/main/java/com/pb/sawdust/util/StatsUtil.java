package com.pb.sawdust.util;


/**
 * The {@code StatsUtil} ...
 *
 * @author crf
 *         Started 1/18/12 4:02 PM
 */
public class StatsUtil {

    public static double getMaximum(double[] values) {
        double max = values[0];
        for (double d : values)
            max = Math.max(max,d);
        return max;
    }

    public static double getMinimum(double[] values) {
        double min = values[0];
        for (double d : values)
            min = Math.min(min,d);
        return min;
    }

    public static double getAverage(double[] values) {
        double runningAverage = 0.0;
        for (int i : Range.range(values.length))
            runningAverage = (runningAverage*i + values[i])/(i+1);
        return runningAverage;
    }

    public static double getStandardDeviation(double[] values) {
        double average = getAverage(values);
        double squareDifferences = 0.0;
        for (double d : values)
            squareDifferences += Math.pow(d-average,2);
        return Math.sqrt(squareDifferences)/values.length;
    }
}