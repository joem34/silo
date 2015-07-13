package com.pb.sawdust.popsynth.em.classifiers;

import com.pb.sawdust.popsynth.em.BalanceElement;
import com.pb.sawdust.popsynth.em.extensions.PumsBalanceElement;
import com.pb.sawdust.tabledata.DataRow;

import java.util.*;

/**
 * The {@code StringClassifier} ...
 *
 * @author crf
 *         Started 10/1/11 12:48 PM
 */
public class PumsStringClassifier extends AbstractBalanceDimensionClassifier<String> {
    private final Map<String,String> valueToClassificationMap;
    protected final String dataRowField;
    private final boolean household;

    public PumsStringClassifier(String name, Map<String,String> valueToClassificationMap, TargetDataSpec<String> dataSpec, String dataRowField, boolean household) {
        super(name,getClassifications(valueToClassificationMap),dataSpec);
        this.valueToClassificationMap = new HashMap<>(valueToClassificationMap);
        this.household = household;
        this.dataRowField = dataRowField;
    }

    private static Set<String> getClassifications(Map<String,String> valueToClassificationMap) {
        return new LinkedHashSet<>(valueToClassificationMap.values());
    }

    @Override
    public Map<String,Double> getParticipationMap(BalanceElement element) {
        Map<String,Double> participationMap = new LinkedHashMap<>();
        for (String classification : getClassificationCategories())
            participationMap.put(classification,0.0);
        if (household) {
            String classification = valueToClassificationMap.get(element.getElementData().get(PumsBalanceElement.HOUSEHOLD_ROW_KEY).getCellAsString(dataRowField));
            if (classification != null)
                participationMap.put(classification,1.0);
        } else {
            for (Map.Entry<String,DataRow> row : element.getElementData().entrySet()) {
                if (row.getKey().startsWith(PumsBalanceElement.PERSON_ROW_KEY_PREFIX)) {
                    String classification = valueToClassificationMap.get(row.getValue().getCellAsString(dataRowField));
                    if (classification != null)
                        participationMap.put(classification,participationMap.get(classification)+1.0);
                }
            }
        }
        return participationMap;
    }
}