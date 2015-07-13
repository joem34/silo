package com.pb.sawdust.model.models;

import com.pb.sawdust.model.models.provider.DataProvider;
import com.pb.sawdust.model.models.provider.hub.DataProviderHub;
import com.pb.sawdust.model.models.trace.CalculationTrace;
import com.pb.sawdust.tensor.alias.vector.primitive.BooleanVector;
import com.pb.sawdust.tensor.alias.vector.primitive.DoubleVector;

import java.util.Map;
import java.util.Set;

/**
 * The {@code DiscreteChoiceModel} ...
 *
 * @author crf <br/>
 *         Started 3/2/11 6:51 AM
 */
public interface DiscreteChoiceModel<C extends Choice> {
    /**
     * Get the choices for this model.
     *
     * @return this model's choices.
     */
    Set<C> getChoices();

     /**
     * Get the probabilities for a specified choice for this model on the provided data. The order of the probabilities
     * in the returned vector will be the same as the data in the data provider.
     *
     * @param data
     *        The data provider to use to calculate the probabilities.
     *
     * @param choice
     *        The choice to get the probabilities for.
     *
     * @return a vector of probabilities for {@code choice} on {@code data}.
     *
     * @throws IllegalArgumentException if {@code data} does not contain all of the variable data required by this model,
      *                                 or if {@code choice} is not a valid choice for this model.
     */
    DoubleVector getProbabilities(C choice, DataProvider data);

    /**
     * Get the choice probabilities for this model on the provided data. The order of the probabilities in the returned
     * vectors will be the same as the data in the data provider.
     *
     * @param data
     *        The data provider to use to calculate the probabilities.
     *
     * @return a mapping from each model choice to its probabilities on {@code data}.
     *
     * @throws IllegalArgumentException if {@code data} does not contain all of the variable data required by this model.
     */
    Map<C,DoubleVector> getProbabilities(DataProvider data);

    /**
     * Get the probabilities for a specified choice for this model on the provided data. The data provider hub can provide
     * different variable data by choice. The order of the probabilities in the returned vector will be the same as the
     * data in the data provider hub.
     *
     * @param data
     *        The data provider hub to use to calculate the probabilities.
     *
     * @param choice
     *        The choice to get the probabilities for.
     *
     * @return a vector of probabilities for {@code choice} on {@code data}.
     *
     * @throws IllegalArgumentException if {@code data} does not contain all of the variable data required by this model,
     *                                  or if {@code choice} is not a valid choice for this model or {@code data}.
     */
    DoubleVector getProbabilities(C choice, DataProviderHub<C> data);

    /**
     * Get the choice probabilities for this model on the provided data. The data provider hub can provide different variable
     * data by choice. The order of the probabilities in the returned vectors will be the same as the data in the data provider
     * hub.
     *
     * @param data
     *        The data provider hub to use to calculate the probabilities.
     *
     * @return a mapping from each model choice to its probabilities on {@code data}.
     *
     * @throws IllegalArgumentException if the providers in {@code data} do not contain all of the variable data required
     *                                  by their respective choice's utility function, or if {@code data} does not have a
     *                                  provider for each choice in this model.
     */
    Map<C,DoubleVector> getProbabilities(DataProviderHub<C> data);

    /**
     * Get the choice availabilities for this model on the provided data. A choice is considered available to an observation
     * if its corresponding entry in the availability vector is {@code true}. The order of the availabilities in the returned
     * vector will be the same as the data in the data provider.
     *
     * @param data
     *        The data provider to use to calculate the availabilities.
     *
     * @param choice
     *        The choice to get the availabilities for.
     *
     * @return a vector of availabilities for {@code choice} on {@code data}.
     *
     * @throws IllegalArgumentException if {@code data} does not contain all of the variable data required by this model's
     *                                  availability cacluations, or if {@code choice} is not a valid choice for this model.
     */
    BooleanVector getAvailabilities(C choice, DataProvider data);

    /**
     * Get the choice availabilities for this model on the provided data. The data provider hub can provide different variable
     * data by choice. A choice is considered available to an observation if its corresponding entry in the availability
     * vector is {@code true}. The order of the availabilities in the returned vector will be the same as the data in the
     * data provider.
     *
     * @param data
     *        The data provider to use to calculate the availabilities.
     *
     * @param choice
     *        The choice to get the availabilities for.
     *
     * @return a vector of availabilities for {@code choice} on {@code data}.
     *
     * @throws IllegalArgumentException if {@code data} does not contain all of the variable data required by this model's
     *                                  availability cacluations, or if {@code choice} is not a valid choice for this model
     *                                  or {@code data}.
     */
    BooleanVector getAvailabilities(C choice, DataProviderHub<C> data);

    /**
     * Get the choice availabilities for this model on the provided data. A choice is considered available to an observation
     * if its corresponding entry in the availability vector is {@code true}. The order of the availabilities in the returned
     * vectors will be the same as the data in the data provider.
     *
     * @param data
     *        The data provider to use to calculate the availabilities.
     *
     * @return a mapping from each model choice to its availabilities on {@code data}.
     *
     * @throws IllegalArgumentException if {@code data} does not contain all of the variable data required by this model's
     *                                  availability cacluations
     */
    Map<C,BooleanVector> getAvailabilities(DataProvider data);

    /**
     * Get the choice availabilities for this model on the provided data. A choice is considered available to an observation
     * if its corresponding entry in the availability vector is {@code true}. The data provider hub can provide different variable
     * data by choice. The order of the availabilities in the returned vectors will be the same as the data in the data provider
     * hub.
     *
     * @param data
     *        The data provider hub to use to calculate the availabilities.
     *
     * @return a mapping from each model choice to its availabilities on {@code data}.
     *
     * @throws IllegalArgumentException if the providers in {@code data} do not contain all of the variable data required
     *                                  by their respective choice's availability calculations, or if {@code data} does
     *                                  not have a provider for each choice in this model.
     */
    Map<C,BooleanVector> getAvailabilities(DataProviderHub<C> data);

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
     *                                  of {@code data}, or if {@code data} does not contain all of the variable data required
     *                                  by this model's utility functions.
     */
    Map<C,CalculationTrace> traceCalculation(DataProvider data, int observation);

    /**
     * Trace the model probability calculations for a single observation and choice in a data provider.
     *
     * @param choice
     *        The choice whose model calculation will be traced.
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
     *                                  of {@code data}, if {@code choice} is not a valid choice for this model, or if
     *                                  {@code data} does not contain all of the variable data required by this model's
     *                                  utility functions.
     */
    CalculationTrace traceCalculation(C choice, DataProvider data, int observation);

    /**
     * Trace the model probability calculations for a single observation in a data provider hub.
     *
     * @param data
     *        The data provider hub to use to calculate the probabilities.
     *
     * @param observation
     *        The (0-based) observation whose model calculation will be traced.
     *
     * @return a mapping from the model choices to the trace of the model (on that choice) of {@code observation} from {@code data}.
     *
     * @throws IllegalArgumentException if {@code observation} is less than zero or greater than or equal to the length
     *                                  of {@code data}, or if {@code data} does not contain all of the variable data required
     *                                  by this model's utility functions.
     */
    Map<C,CalculationTrace> traceCalculation(DataProviderHub<C> data, int observation);

    /**
     * Trace the model probability calculations for a single observation and choice in a data provider hub.
     *
     * @param choice
     *        The choice whose model calculation will be traced.
     *
     * @param data
     *        The data provider hub to use to calculate the probabilities.
     *
     * @param observation
     *        The (0-based) observation whose model calculation will be traced.
     *
     * @return a mapping from the model choices to the trace of the model (on that choice) of {@code observation} from {@code data}.
     *
     * @throws IllegalArgumentException if {@code observation} is less than zero or greater than or equal to the length
     *                                  of {@code data}, if {@code choice} is not a valid choice for this model or {@code data},
     *                                  or if {@code data} does not contain all of the variable data required by this model's
     *                                  utility functions.
     */
    CalculationTrace traceCalculation(C choice, DataProviderHub<C> data, int observation);
}