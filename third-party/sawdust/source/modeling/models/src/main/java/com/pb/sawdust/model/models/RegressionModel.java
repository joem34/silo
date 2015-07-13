package com.pb.sawdust.model.models;

import com.pb.sawdust.model.models.provider.SimpleDataProvider;
import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.model.models.utility.SimpleLinearUtility;
import com.pb.sawdust.model.models.utility.Utility;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.RandomDeluxe;
import com.pb.sawdust.util.ThreadTimer;
import com.pb.sawdust.util.collections.LinkedSetList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The {@code RegressionModel} represents a model whose values are calculated directly from utilities. It is probably
 * the simplest and most generic utility model (in fact, it is essentially a wrapper on a {@code Utility} instance). Its
 * name is based on the fact that often the utility coefficients for such a model are estimated using regression, even
 * though no such requirements are placed on this class.
 *
 * @author crf <br/>
 *         Started Jul 24, 2010 10:31:35 AM
 */
public class RegressionModel {
    private final Utility utility;

    /**
     * Constructor specifying the utility upon which the model is based.
     *
     * @param utility
     *        The utility that the model's results are calculated from.
     */
    public RegressionModel(Utility utility) {
        this.utility = utility;
    }

    /**
     * Get the model results for all observations in a data provider. The order of the results will be the same as that
     * of the data passed to the method.
     *
     * @param data
     *        The data provider to run the model on.
     *
     * @return the model results for all of the observations in {@code order}.
     *
     * @throws IllegalArgumentException if any of the variables in this model's utility are not provided by {@code data}.
     */
    public DoubleVector runModel(DataProvider data) {
        return utility.getUtilities(data);
    }

    /**
     * Trace the model calculation for a single observation in a data provider.
     *
     * @param data
     *        The data provider holding the observations.
     *
     * @param observation
     *        The (0-based) observation whose model calculation will be traced.
     *
     * @return the trace of the model on {@code observation} from {@code data}.
     *
     * @throws IllegalArgumentException if {@code observation} is less than zero or greater than or equal to the length
     *                                  of {@code data}, or if any of the variables in this model's utility are not provided
     *                                  by {@code data}.
     */
    public CalculationTrace traceModel(DataProvider data, int observation) {
        return utility.traceCalculation(data,observation);
    }

    public static void main(String ... args) {
        ThreadTimer timer = new ThreadTimer(TimeUnit.MILLISECONDS);

        timer.startTimer();
        RandomDeluxe rd = new RandomDeluxe();
        //int length = 501;
        int length = 1000;
        double[] consta = new double[length];
        Arrays.fill(consta,1.0);
        double[] ttime = rd.nextDoubles(length);
        System.out.println("Random: " + timer.resetTimer());

        TensorFactory factory = ArrayTensor.getFactory();

        Map<String,double[]> testData = new HashMap<String,double[]>();
        testData.put("constant",consta);
        testData.put("lrtime",ttime);
        DataProvider dp = new SimpleDataProvider(testData,factory);

        RegressionModel model = new RegressionModel(new SimpleLinearUtility(
                new LinkedSetList<String>("constant","lrtime"),
                Arrays.asList(2.3,4.5),
                factory
        ));

        System.out.println(model.traceModel(dp,4));
    }
}