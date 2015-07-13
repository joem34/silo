package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.calculator.NumericFunctionN;
import com.pb.sawdust.calculator.NumericFunctions;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code AbstractCalculationProvider} ...
 *
 * @deprecated not used anywhere and duplicates functionality contained in SimpleCalculationDataProvider
 *
 * @author crf <br/>
 *         Started 2/16/11 10:47 AM
 */
@Deprecated
abstract class CalculationProviderImplUtil implements CalculationProvider {

    public static VariableCalculation getResolvedCalculation(CalculationProvider provider, String variable) {
        VariableCalculation calc = provider.getCalculation(variable);
        List<VariableCalculation> subCalcs = new LinkedList<VariableCalculation>();
        for (String arg : calc.getArguments())
            subCalcs.add(getResolvedCalculation(provider,arg));
        boolean unnested = true;
        for (VariableCalculation subCalc : subCalcs)
            unnested &= !subCalc.isCalculated();
        if (unnested) {
            return calc; //no subcalculations, so can just return original
        } else {
            //need to unroll subcalculations into numeric function
            List<NumericFunctionN> functions = new LinkedList<NumericFunctionN>();
            List<String> arguments = new LinkedList<String>();
            for (VariableCalculation subCalc : subCalcs) {
                if (subCalc.isCalculated()) {
                    for (String arg : subCalc.getArguments()) {
                        functions.add(NumericFunctions.PARAMETER);
                        arguments.add(arg);
                    }
                    functions.add(subCalc.getFunction());
                } else {
                    functions.add(NumericFunctions.PARAMETER);
                    arguments.add(subCalc.getName());
                }
            }
            functions.add(calc.getFunction());
            return new VariableCalculation(variable,NumericFunctions.compositeNumericFunction(functions),arguments);
        }
    }
}