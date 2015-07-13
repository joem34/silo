package com.pb.sawdust.model.models.logit;

import com.pb.sawdust.calculator.NumericFunctionN;
import com.pb.sawdust.calculator.NumericFunctions;
import com.pb.sawdust.calculator.tensor.CellWiseTensorCalculation;
import com.pb.sawdust.calculator.tensor.DefaultCellWiseTensorCalculation;
import com.pb.sawdust.calculator.tensor.TensorMarginal;
import com.pb.sawdust.model.models.provider.filter.CompositeDataFilter;
import com.pb.sawdust.model.models.provider.filter.DataFilter;
import com.pb.sawdust.model.models.provider.filter.FilteredDataProvider;
import com.pb.sawdust.model.models.provider.filter.VariableDataFilter;
import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.model.models.trace.LabelTrace;
import com.pb.sawdust.model.models.utility.*;
import com.pb.sawdust.model.models.Choice;
import com.pb.sawdust.model.models.ChoiceUtil;
import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.IdData;
import com.pb.sawdust.model.models.provider.hub.PolyDataProvider;
import com.pb.sawdust.model.models.provider.hub.SimplePolyDataProvider;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.StandardTensorMetadataKey;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;
import com.pb.sawdust.tensor.decorators.primitive.DoubleTensor;
import com.pb.sawdust.tensor.index.CollapsingIndex;
import com.pb.sawdust.tensor.index.SliceIndex;
import com.pb.sawdust.util.RandomDeluxe;
import com.pb.sawdust.util.collections.ArraySetList;
import com.pb.sawdust.util.collections.SetList;
import com.pb.sawdust.util.collections.cache.*;

import java.util.*;

import static com.pb.sawdust.calculator.NumericFunctions.*;
import static com.pb.sawdust.calculator.NumericFunctions.ADD;
import static com.pb.sawdust.calculator.NumericFunctions.constant;

/**
 * The {@code PolyLogitModel} class is used to run a logit model across multiple choices simultaneously.  That is, it
 * offers the ability to return model results as a matrix - where each choice is represented by a column (dimension index 1) -
 * as opposed to a map. More importantly, it allows (parts of) the utility equations to be processed across all choices
 * by variable as opposed to across all variables by choice. This operational shift can provide significant performance
 * improvements, especially for models with large numbers of choices.  Additionally, the final matrix form of the model
 * results is often desirable for other applications (<i>e.g.</i> aggregate destination choice models or mode choice
 * logsums).
 * <p>
 * The {@code PolyLogitModel} separates utilities into two classes: those which are choice-specific, and those which are
 * shared across choices.  Only those shared across utilities, referred to as poly-utilities, can be applied aggregately
 * across choices.  The choice specific utility calculations are applied as they would be in the parent {@code LogitModel}
 * class. The variables for a poly-utility are provided by a {@code PolyDataProvider}, which provides both poly-data
 * (data for all choices by variable) and choice-specific {@code DataProvider}s (data for all variables by choice).
 * <p>
 * It is noted that the inherited {@code LogitModel} methods calculating results on {@code DataProvider}s or
 * {@code DataProviderHub}s <i>do not</i> process the model results aggregately across choices; the calculations for each
 * choice are performed separately.
 * <p>
 * Like its parent, this class holds a calculation cache for holding previous results. As with the parent, the {@code clearCache}
 * methods can be used to manage the memory footprint of the class as it is used.
 *
 * @param <C>
 *        The type of the model choices.
 *
 * @author crf <br/>
 *         Started Sep 14, 2010 10:05:34 PM
 */
public class PolyLogitModel<C extends Choice> extends LogitModel<C> {
    private final Cache<Integer,DoubleMatrix> utilityCache;
    private final CellWiseTensorCalculation cwc;
    private final LinearUtility polyUtility;
    private final boolean usesOnlyPolyUtility;
    private final String polyAvailibilityFilter;

    private static <C extends Choice> Map<C,LinearUtility> getEmptyUtilities(Set<C> choices) {
        Map<C, LinearUtility> map = new HashMap<C, LinearUtility>();
        for (C choice : choices)
            map.put(choice,EmptyUtility.getEmptyLinearUtility());
        return map;
    }

    public static <C extends Choice> PolyLogitModel<C> getModelWithAvailabilityFilters(PolyLogitModel<C> model, Map<C,DataFilter> availabilityFilters) {
        Map<C,DataFilter> currentAvailabilityFilters = model.getAvailabilityFilters();
        if (currentAvailabilityFilters.size() != 0) {
            Map<C,DataFilter> updatedAvailabilityFilters = new HashMap<C,DataFilter>(availabilityFilters);
            for (C c : currentAvailabilityFilters.keySet()) {
                if (updatedAvailabilityFilters.containsKey(c))
                    updatedAvailabilityFilters.put(c,new CompositeDataFilter(currentAvailabilityFilters.get(c),updatedAvailabilityFilters.get(c)));
                else
                    updatedAvailabilityFilters.put(c,currentAvailabilityFilters.get(c));
            }
            availabilityFilters = updatedAvailabilityFilters;
        }
        return new PolyLogitModel<C>(model.getName(),model.getNonPolyUtilities(),model.polyUtility,availabilityFilters,model.polyAvailibilityFilter,model.factory,model.usesOnlyPolyUtility);
    }

    public static <C extends Choice> PolyLogitModel<C> getModelWithPolyAvailabilityFilters(PolyLogitModel<C> model, String polyAvailibilityFilter) {
        if (model.polyAvailibilityFilter != null)
            throw new IllegalArgumentException("Input model already has poly-availability filter defined: " + model.polyAvailibilityFilter);
        return new PolyLogitModel<C>(model.getName(),model.getNonPolyUtilities(),model.polyUtility,model.getAvailabilityFilters(),polyAvailibilityFilter,model.factory,model.usesOnlyPolyUtility);
    }

    private PolyLogitModel(String name, Map<C,? extends LinearUtility> utilities, LinearUtility polyUtility, Map<C,DataFilter> availabilityFilters, String polyAvailibilityFilter, TensorFactory factory, boolean usesOnlyPolyUtility) {
        super(name,utilities,availabilityFilters,factory);
        utilityCache = new SoftCache<Integer,DoubleMatrix>(1);
        cwc = new DefaultCellWiseTensorCalculation(factory);
        this.polyUtility = polyUtility;
        this.polyAvailibilityFilter = polyAvailibilityFilter;
        this.usesOnlyPolyUtility = usesOnlyPolyUtility;
    }

    private PolyLogitModel(String name, Map<C,? extends LinearUtility> utilities, LinearUtility polyUtility, TensorFactory factory, boolean usesOnlyPolyUtility) {
        this(name,utilities,polyUtility,null,null,factory,usesOnlyPolyUtility);
    }

    /**
     * Constructor specifying the model name, the choice-specific utilities, the poly-utility shared across choices, and
     * the tensor factory used to build results.  For each choice, the choice utility will be the sum of the choice-specific
     * utility and the shared poly-utility results.
     *
     * @param name
     *        The name of the model.
     *
     * @param utilities
     *        The choice specific utility components, as a mapping from the model choices to the choice utility function.
     *
     * @param polyUtility
     *        The utility component shared across choices.
     *
     * @param factory
     *        The tensor factory used to build results.
     */
    public PolyLogitModel(String name, Map<C,? extends LinearUtility> utilities, LinearUtility polyUtility, TensorFactory factory) {
        this(name,utilities,polyUtility,factory,false);
    }

    /**
     * Constructor specifying the the choice-specific model utilities, the poly-utility shared across choices, and
     * the tensor factory used to build results. For each choice, the choice utility will be the sum of the choice-specific
     * utility and the shared poly-utility results. An empty string will be used for the model name.
     *
     * @param utilities
     *        The choice specific utility components, as a mapping from the model choices to the choice utility function.
     *
     * @param polyUtility
     *        The utility component shared across choices.
     *
     * @param factory
     *        The tensor factory used to build results.
     */
    public PolyLogitModel(Map<C,? extends LinearUtility> utilities, LinearUtility polyUtility, TensorFactory factory) {
        this("",utilities,polyUtility,factory);
    }

    /**
     * Constructor specifying the model name, the model choices, the poly-utility shared across choices, and the tensor
     * factory used to build results.  This constructor should be used for models with no choice-specific utility components.
     *
     * @param name
     *        The name of the model.
     *
     * @param choices
     *        The set of choices for the model.
     *
     * @param polyUtility
     *        The utility component shared across choices.
     *
     * @param factory
     *        The tensor factory used to build results.
     */
    public PolyLogitModel(String name, Set<C> choices, LinearUtility polyUtility, TensorFactory factory) {
        this(name,getEmptyUtilities(choices),polyUtility,factory,true);
    }

    /**
     * Constructor specifying the model choices, the poly-utility shared across choices, and the tensor factory used to
     * build results.  This constructor should be used for models with no choice-specific utility components. An empty
     * string will be used for the model name.
     *
     * @param choices
     *        The set of choices for the model.
     *
     * @param polyUtility
     *        The utility component shared across choices.
     *
     * @param factory
     *        The tensor factory used to build results.
     */
    public PolyLogitModel(SetList<C> choices, LinearUtility polyUtility, TensorFactory factory) {
        this("",choices,polyUtility,factory);
    }

    @SuppressWarnings("unchecked") //linear in, linear out
    private Map<C,LinearUtility> getNonPolyUtilities() {
        return (Map<C,LinearUtility>) super.getUtilities();
    }

    public Map<C,LinearUtility> getUtilities() {
        Map<C,LinearUtility> utilities = new HashMap<C,LinearUtility>();
        Map<C,LinearUtility> nonPolyUtilities = getNonPolyUtilities();
        for (C choice : getChoices())
            utilities.put(choice,new LinearCompositeUtility(factory,nonPolyUtilities.get(choice),polyUtility));
        return utilities;
    }

    /**
     * Get the exponentiated utilities for all choices. Each column (dimension index 1) of the returned matrix holds the
     * exponentiated utilities for a given choice; the choice (column) ordering is the same as the choice ordering in
     * {@code data}. The order of the results in the returned vector will be the same as the observations in {@code data}.
     *
     * @param data
     *        The poly data provider to use to calculate the utilities.
     *
     * @return the exponentiated utilities for all model choices on {@code data}.
     *
     * @throws IllegalArgumentException if the model uses choice-specific utilities and {@code data} does not contain all
     *                                  of the variables required by each choice's utility function, if {@code data}
     *                                  does not contain poly-data for each variable in this model's poly-utility, or if
     *                                  the choices (keys) represented by {@code data} do not exactly match the choices
     *                                  in this model.
     */
    @SuppressWarnings("unchecked") //index type doesn't matter where the warnings are occuring
    public DoubleMatrix getExponentiatedUtilities(PolyDataProvider<C> data) {
        if (!utilityCache.containsKey(data.getDataId())) {
            SetList<C> orderedChoices = data.getDataKeys();
            if (!getChoices().equals(orderedChoices))
                throw new IllegalArgumentException("Choices in this model must be exactly represented by poly data provider.");
            Map<C,DataFilter> availabilityFilters = getAvailabilityFilters();
            DoubleMatrix m = null;
            if (!usesOnlyPolyUtility) {
                m = (DoubleMatrix) factory.doubleTensor(data.getDataLength(),orderedChoices.size());
                //fill starting matrix with non-poly utilities
                Map<C,LinearUtility> nonPolyUtilities = getNonPolyUtilities();
                int rows = m.size(0);
                int counter = -1;
                for (C choice : orderedChoices) {
                    counter++;
                    DataFilter availabilityFilter = availabilityFilters.get(choice);
                    Utility ut = nonPolyUtilities.get(choice);
                    if (ut instanceof EmptyUtility) {
                        if (availabilityFilter != null) {
                            DoubleVector v = (DoubleVector) m.getReferenceTensor(new CollapsingIndex(m.getIndex(),1,counter));
                            TensorUtil.fill(v.getReferenceTensor(SliceIndex.getSliceIndex(v.getIndex(),availabilityFilter.getUnfilteredSlice(data.getProvider(choice)))),UNAVAILABLE_UTILITY);
                        }
                        continue; //just skip empty guys
                    }
                    DataProvider d = data.getProvider(choice);
                    DoubleVector v;
                    if (availabilityFilter == null) {
                        v = ut.getUtilities(d);
                        for (int i = 0; i < rows; i++)
                            m.setCell(v.getCell(i),i,counter);
                    } else {
                        v = (DoubleVector) m.getReferenceTensor(new CollapsingIndex(m.getIndex(),1,counter));
//                        v = factory.doubleVector(data.getDataLength()); //result vector
                        DoubleVector r = ut.getUtilities(new FilteredDataProvider(factory, d, availabilityFilter)); //only available utilities
                        v.getReferenceTensor(SliceIndex.getSliceIndex(v.getIndex(), availabilityFilter.getFilteredSlice(d))).setTensorValues(r); //fill in available data
                        TensorUtil.fill(v.getReferenceTensor(SliceIndex.getSliceIndex(v.getIndex(),availabilityFilter.getUnfilteredSlice(d))),UNAVAILABLE_UTILITY);
                    }
                    super.clearCache(d); //done with this
                }
            }

            List<String> variables = polyUtility.getVariables();
            if (variables.size() > 0) {
                List<NumericFunctionN> formula = new LinkedList<NumericFunctionN>();
                List<DoubleTensor> polyData = new LinkedList<DoubleTensor>();
                if (!usesOnlyPolyUtility) {
                    polyData.add(m);
                    formula.add(parameter("non-poly utility components")); //data utilities
                }
                for (String variable : variables) {
                    double val = polyUtility.getCoefficient(variable);
                    if (val == 0.0)
                        continue;
                    formula.add(parameter(variable));
                    polyData.add(data.getPolyData(variable));
                    if (val != 1.0) {
                        formula.add(constant(val));
                        formula.add(MULTIPLY); //poly*coefficient
                    }
                    if (polyData.size() > 1)
                        formula.add(ADD); //sum up values
                }
                formula.add(EXP);

//                System.out.println(formula);
//                for (DoubleTensor t : polyData) {
//                    if (t.containsMetadataKey(StandardTensorMetadataKey.NAME.getKey()))
//                        System.out.println(t.getMetadataValue(StandardTensorMetadataKey.NAME.getKey()));
//                    System.out.println(TensorUtil.toString(t));
//                }
//                System.out.println(polyData);
                NumericFunctionN f = compositeNumericFunction(formula.toArray(new NumericFunctionN[formula.size()]));

                if (polyAvailibilityFilter != null) {
                    f = NumericFunctions.shortCircuitTernary(NumericFunctions.PASS,f,NumericFunctions.constant(0.0));
                    polyData.add(0,data.getPolyData(polyAvailibilityFilter));
                }

                m = (DoubleMatrix) cwc.calculate(f,polyData);
            }
            utilityCache.put(data.getDataId(), m);
        }

        return utilityCache.get(data.getDataId());
    }

    /**
     * Get the expsums for all choices. An expsum is the sum of exponentiated utilities across all choices in the model;
     * it is used as the denominator of the individual choice probability calculations. The order of the expsums in the
     * returned vector will be the same as the observations in {@code data}.
     *
     * @param data
     *        The poly data provider to use to calculate the expsums.
     *
     * @return the expsums calculated on {@code data}.
     *
     * @throws IllegalArgumentException if the model uses choice-specific utilities and {@code data} does not contain all
     *                                  of the variables required by each choice's utility function, if {@code data}
     *                                  does not contain poly-data for each variable in this model's poly-utility, or if
     *                                  the choices (keys) represented by {@code data} do not exactly match the choices
     *                                  in this model.
     */
    public DoubleVector getExpsums(PolyDataProvider<C> data) {
        TensorMarginal marginal = new TensorMarginal(factory);
        return (DoubleVector) marginal.getMarginal(getExponentiatedUtilities(data),1,TensorMarginal.Marginal.SUM);
    }

    /**
     * Get the logsums for all choices. An logsum is the logarithm of the model's expsum. The order of the logsums in the
     * returned vector will be the same as the observations in {@code data}.
     *
     * @param data
     *        The poly data provider to use to calculate the logsums.
     *
     * @return the logsums calculated on {@code data}.
     *
     * @throws IllegalArgumentException if the model uses choice-specific utilities and {@code data} does not contain all
     *                                  of the variables required by each choice's utility function, if {@code data}
     *                                  does not contain poly-data for each variable in this model's poly-utility, or if
     *                                  the choices (keys) represented by {@code data} do not exactly match the choices
     *                                  in this model.
     */
    public DoubleVector getLogsums(PolyDataProvider<C> data) {
        return (DoubleVector) cwtc.calculate(getExpsums(data),NumericFunctions.LOG);
    }

    /**
     * Get the probabilities for the model on the provided data. The order of the probabilities in the returned vector will
     * be the same as the observations in {@code data}.
     *
     * @param data
     *        The poly data provider to use to calculate the logsums.
     *
     * @return the probabilities calculated on {@code data}.
     *
     * @throws IllegalArgumentException if the model uses choice-specific utilities and {@code data} does not contain all
     *                                  of the variables required by each choice's utility function, if {@code data}
     *                                  does not contain poly-data for each variable in this model's poly-utility, or if
     *                                  the choices (keys) represented by {@code data} do not exactly match the choices
     *                                  in this model.
     */
    public DoubleMatrix getProbabilities(PolyDataProvider<C> data) {
        return (DoubleMatrix) cwtc.calculate(getExponentiatedUtilities(data),getExpsums(data),NumericFunctions.ZERO_SAFE_DIVIDE);
    }

    public void clearCache(IdData data) {
        super.clearCache(data);
        //utilityCache.pullKey(data.getDataId());
        utilityCache.remove(data.getDataId());
    }

    public void clearCache() {
        super.clearCache();
        utilityCache.clear();
    }

    /**
     * Trace the model probability calculations for a single observation in a data provider.
     *
     * @param data
     *        The data provider to use to calculate the probabilities.
     *
     * @param observation
     *        The (0-based) observation whose model calculation will be traced.
     *
     * @return a mapping from the model choices to the trace of the model (on that choice) of {@code observation} from {@code data}.
     *
     * @throws IllegalArgumentException if {@code observation} is less than zero or greater than or equal to the length
     *                                  of {@code data}, or if {@code data} does not contain all of the variables required
     *                                  by this model's utility functions.
     */
    public Map<C,CalculationTrace> traceCalculation(PolyDataProvider<C> data, int observation) {
        double expsum = getExpsums(data).getCell(observation);
//        double expsum = getExpsums(data.getSubDataHub(observation,observation+1)).getCell(0);
        Map<C, CalculationTrace> utilityTrace = new HashMap<C, CalculationTrace>();
        for (C choice : getChoices())
            utilityTrace.put(choice,traceCalculation(choice,data.getFullProvider(choice),observation,expsum));
        return utilityTrace;
    }

    public CalculationTrace traceCalculation(C choice, PolyDataProvider<C> data, int observation) {
        DataProvider provider = data.getFullProvider(choice);
        if (polyAvailibilityFilter != null) {
            DataFilter availabilityFilter = new VariableDataFilter(polyAvailibilityFilter,factory);
            if (!availabilityFilter.getFilter(provider).getCell(observation)) {
                CalculationTrace trace = new CalculationTrace("Probability for choice " + choice + " (unavailable)",0.0);
                trace.addTraceElement(availabilityFilter.traceFilterCalculation(provider,observation));
                return trace; //unavailable, so trace ends here
            }
        }
        return traceCalculation(choice,provider,observation,getExpsums(data).getCell(observation));
    }

     public CalculationTrace traceLogsumCalculation(PolyDataProvider<C> data, int observation) {
         //todo: this
         return null;
//        DataProvider subData = data.getSubData(observation,observation+1);
//        CalculationTrace trace = new CalculationTrace("Logsum for " + getName(),"log(" + getExpsums(subData).getCell(0) + ")",getLogsums(subData).getCell(0));
//        Map<C, CalculationTrace> utilityTrace = new HashMap<C, CalculationTrace>();
//        Map<C,? extends Utility> uts = getUtilities();
//        for (C choice : uts.keySet()) {
//            DataFilter availabilityFilter = availabilityFilters.get(choice);
//            if (availabilityFilter != null) {
//                boolean available = availabilityFilter.getFilter(subData).getCell(0);
//                if (!available) {
//                    CalculationTrace uaTrace = new CalculationTrace("Utility for choice " + choice + " (unavailable)",UNAVAILABLE_UTILITY);
//                    trace.addTraceElement(availabilityFilter.traceFilterCalculation(data,observation));
//                    utilityTrace.put(choice,uaTrace);
//                    continue;
//                }
//            }
//            utilityTrace.put(choice,uts.get(choice).traceCalculation(subData,0));
//        }
//        trace.addTraceElement(new LabelTrace("log("));
//        boolean first = true;
//        for (C choice : utilityTrace.keySet()) {
//            CalculationTrace subTrace = new CalculationTrace("exp(" + getUtilityName(choice) + ")","exp(" + utilityTrace.get(choice).getResult() + ")",Math.exp(utilityTrace.get(choice).getResult()));
//            subTrace.addTraceElement(new LabelTrace("exp("));
//            subTrace.addTraceElement(uts.get(choice).traceCalculation(data,observation));
//            subTrace.addTraceElement(new LabelTrace(")"));
//            if (first)
//                first = false;
//            else
//                trace.addTraceElement(new LabelTrace("+"));
//            trace.addTraceElement(subTrace);
//        }
//        trace.addTraceElement(new LabelTrace(")"));
//        return trace;
     }

    public static void main(String ... args) {
        int dim = 2000;

        TensorFactory f = ArrayTensor.getFactory();
        DoubleMatrix skim1 = (DoubleMatrix) TensorUtil.asDoubleTensor(f.floatTensor(dim,dim));
        DoubleMatrix skim2 = (DoubleMatrix) TensorUtil.asDoubleTensor(f.floatTensor(dim,dim));
        DoubleMatrix skim3 = (DoubleMatrix) TensorUtil.asDoubleTensor(f.floatTensor(dim,dim));

        final RandomDeluxe rand = new RandomDeluxe();

        TensorUtil.DoubleTensorValueFunction func  = new TensorUtil.DoubleTensorValueFunction() {
            public double getValue(int ... indices) {
                return rand.nextDouble();
            }
        };

        TensorUtil.fill(skim1,func);
        TensorUtil.fill(skim2,func);
        TensorUtil.fill(skim3,func);

//        DoubleVector coefficients = (DoubleVector) f.doubleTensor(3);
//        coefficients.setCell(2.5,0);
//        coefficients.setCell(1.2,1);
//        coefficients.setCell(43.3,2);

        SetList<ChoiceUtil.IntChoice> choices = ChoiceUtil.getChoiceRange(dim);
        SimplePolyDataProvider<ChoiceUtil.IntChoice> pdp = new SimplePolyDataProvider<ChoiceUtil.IntChoice>(choices,f);
        pdp.addPolyData("skim1",skim1);
        pdp.addPolyData("skim2",skim2);
        pdp.addPolyData("skim3",skim3);

        TensorMarginal tm = new TensorMarginal(f);


        System.out.println("start");
        SimpleLinearUtility ut = new SimpleLinearUtility(new ArraySetList<String>("skim2","skim3","skim1"),Arrays.asList(2.5,1.2,43.3),f);
        PolyLogitModel<ChoiceUtil.IntChoice> model = new PolyLogitModel<ChoiceUtil.IntChoice>(ChoiceUtil.getChoiceRange(dim),ut,f);
        DoubleMatrix probs = model.getProbabilities(pdp);

        System.out.println(TensorUtil.toString(probs));
        System.out.println(TensorUtil.toString(tm.getMarginal(probs,1,TensorMarginal.Marginal.SUM)));
        System.out.println(TensorUtil.toString(tm.getMarginal(probs,0,TensorMarginal.Marginal.SUM)));
    }
}