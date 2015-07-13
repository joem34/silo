package com.pb.sawdust.data.census.pums.decennial;

import com.pb.sawdust.data.census.pums.PumaDataDictionary;
import com.pb.sawdust.data.census.pums.PumaDataField;

/**
 * The {@code PumsDataDictionary} provides a skeletal implementation of {@code PumaDataDictionary} for use with decennial
 * Census PUMS data. Decennial PUMS data is held in fixed format files holding both household and person 
 * records.
 *       
 * @param <H>
 *        The type of the household field this reader reads. This field must be an {@code enum}.  
 *        
 * @param <P>
 *        The type of the person field this reader reads. This field must be an {@code enum}.
 *
 * @author crf
 *         Started 10/14/11 10:27 AM
 */
public abstract class PumsDataDictionary<H extends Enum<H> & PumsDataDictionary.PumsHouseholdField ,P extends Enum<P> & PumsDataDictionary.PumsPersonField> implements PumaDataDictionary<H,P> {
    private final Class<H> householdFieldClass;
    private final Class<P> personFieldClass;

    /**
     * Constructor specifying the household and person field classes used by the dictionary.
     * 
     * @param householdFieldClass
     *        The household field class.
     *        
     * @param personFieldClass
     *        The person field class.
     */
    public PumsDataDictionary(Class<H> householdFieldClass, Class<P> personFieldClass) {
        this.householdFieldClass = householdFieldClass;
        this.personFieldClass = personFieldClass;
    }

    public Class<H> getHouseholdFieldClass() {
        return householdFieldClass;
    }

    public Class<P> getPersonFieldClass() {
        return  personFieldClass;
    }

    /**
     * The {@code PumsHouseholdField} interface is an extension of {@code PumaDataHouseholdField} used to indicate an decennial
     * Census PUMS microdata household record field. Decennial PUMS data is held in fixed format files holding both household 
     * and person records.
     */
    public static interface PumsHouseholdField extends PumaDataField.PumaDataHouseholdField { 
        /**
         * Get the starting position of the field in the fixed-width record.
         * 
         * @return the starting position of the field.
         */
        int getStart();
        
        /**
         * Get the width (in characters) of the field.
         * 
         * @return the field width.
         */
        int getWidth();
    }

    /**
     * The {@code PumsPersonField} interface is an extension of {@code PumaDataHouseholdField} used to indicate an decennial
     * Census PUMS microdata person record field. Decennial PUMS data is held in fixed format files holding both household 
     * and person records.
     */
    public static interface PumsPersonField extends PumaDataField.PumaDataPersonField {
        /**
         * Get the starting position of the field in the fixed-width record.
         * 
         * @return the starting position of the field.
         */
        int getStart();
        
        /**
         * Get the width (in characters) of the field.
         * 
         * @return the field width.
         */
        int getWidth();
    }
}