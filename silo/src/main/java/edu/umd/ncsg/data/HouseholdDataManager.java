/*
 * Copyright  2005 PB Consult Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package edu.umd.ncsg.data;

import java.io.*;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

import com.pb.common.datafile.TableDataSet;
import edu.umd.ncsg.SiloModel;
import edu.umd.ncsg.SiloUtil;
import edu.umd.ncsg.events.EventRules;
import com.pb.sawdust.calculator.Function1;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.concurrent.ForkJoinPoolFactory;
import com.pb.sawdust.util.concurrent.IteratorAction;
import org.apache.log4j.Logger;
import com.pb.common.util.ResourceUtil;
import com.pb.common.util.IndexSort;


/**
 * @author Greg Erhardt 
 * Created on Dec 2, 2009
 *
 */
public class HouseholdDataManager {
    static Logger logger = Logger.getLogger(HouseholdDataManager.class);

    protected static final String PROPERTIES_HH_FILE_ASCII   = "household.file.ascii";
    protected static final String PROPERTIES_PP_FILE_ASCII   = "person.file.ascii";
    protected static final String PROPERTIES_READ_BIN_FILE   = "read.binary.pop.files";
    protected static final String PROPERTIES_POP_FILE_BIN    = "population.file.bin";
    protected static final String PROPERTIES_INCOME_CHANGE   = "mean.change.of.yearly.income";
    protected static final String PROPERTIES_SUMMARIZE_METRO = "summarize.hh.near.selected.metro.stp";
    protected static final String PROPERTIES_SELECTED_METRO  = "selected.metro.stops";
    protected static final String PROPERTIES_HH_NEAR_METRO   = "hh.near.selected.metro.stops.summary";
    private ResourceBundle rb;

    private static int highestHouseholdIdInUse;
    private static int highestPersonIdInUse;
    private float[][] laborParticipationShares;
    private static float[][][] initialIncomeDistribution;              // income by age, gender and occupation
    private static float meanIncomeChange;
    public static int[] startNewJobPersonIds;
    public static int[] quitJobPersonIds;
    private float[][][] currentIncomeDistribution;
    private static float[] medianIncome;


    public HouseholdDataManager(ResourceBundle rb) {
        // constructor
        this.rb = rb;
        meanIncomeChange = (float) ResourceUtil.getDoubleProperty(rb, PROPERTIES_INCOME_CHANGE);
    }


    public void readPopulation () {
        // read population
        boolean readBin = ResourceUtil.getBooleanProperty(rb, PROPERTIES_READ_BIN_FILE, false);
        if (readBin) {
            readBinaryPopulationDataObjects();
        } else {
            readHouseholdData();
            readPersonData();
        }
    }


    private void readHouseholdData() {
        logger.info("Reading household micro data from ascii file");

        int year = SiloUtil.getStartYear();
        String fileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_HH_FILE_ASCII) + "_" + year + ".csv";

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId    = SiloUtil.findPositionInArray("id", header);
            int posDwell = SiloUtil.findPositionInArray("dwelling",header);
            int posTaz   = SiloUtil.findPositionInArray("zone",header);
            int posSize  = SiloUtil.findPositionInArray("hhSize",header);
            int posAutos = SiloUtil.findPositionInArray("autos",header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id         = Integer.parseInt(lineElements[posId]);
                int dwellingID = Integer.parseInt(lineElements[posDwell]);
                int taz        = Integer.parseInt(lineElements[posTaz]);
                int hhSize     = Integer.parseInt(lineElements[posSize]);
                int autos      = Integer.parseInt(lineElements[posAutos]);

                new Household(id, dwellingID, taz, hhSize, autos);  // this automatically puts it in id->household map in Household class
                if (id == SiloUtil.trackHh) {
                    SiloUtil.trackWriter.println("Read household with following attributes from " + fileName);
                    Household.getHouseholdFromId(id).logAttributes(SiloUtil.trackWriter);
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " households.");
    }


    public static void writeBinaryPopulationDataObjects(ResourceBundle appRb) {
        // Store population object data in binary file
        String fileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(appRb, PROPERTIES_POP_FILE_BIN);
        logger.info("  Writing population data to binary file.");
        Object[] data = {Household.getHouseholds().toArray(new Household[Household.getHouseholdCount()]),
                Person.getPersons().toArray(new Person[Person.getPersonCount()])};
        try {
            File fl = new File(fileName);
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fl));
            out.writeObject(data);
            out.close();
        } catch (Exception e) {
            logger.error("Error saving to binary file " + fileName + ". Object not saved.\n" + e);
        }
    }


    private void readBinaryPopulationDataObjects() {
        // read households and persons from binary file
        String fileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_POP_FILE_BIN);
        logger.info("Reading population data from binary file.");
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(fileName)));
            Object[] data = (Object[]) in.readObject();
            Household.saveHouseholds((Household[]) data[0]);
            Person.savePersons((Person[]) data[1]);
        } catch (Exception e) {
            logger.error ("Error reading from binary file " + fileName + ". Object not read.\n" + e);
        }
        setHighestHouseholdAndPersonId();
        logger.info("Finished reading " + Household.getHouseholdCount() + " households.");
        logger.info("Finished reading " + Person.getPersonCount() + " persons.");
    }


    private void readPersonData() {
        logger.info("Reading person micro data from ascii file");

        int year = SiloUtil.getStartYear();
        String fileName = SiloUtil.baseDirectory + ResourceUtil.getProperty(rb, PROPERTIES_PP_FILE_ASCII) + "_" + year + ".csv";

        String recString = "";
        int recCount = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            recString = in.readLine();

            // read header
            String[] header = recString.split(",");
            int posId = SiloUtil.findPositionInArray("id", header);
            int posHhId = SiloUtil.findPositionInArray("hhid",header);
            int posAge = SiloUtil.findPositionInArray("age",header);
            int posGender = SiloUtil.findPositionInArray("gender",header);
            int posRelShp = SiloUtil.findPositionInArray("relationShip",header);
            int posRace = SiloUtil.findPositionInArray("race",header);
            int posOccupation = SiloUtil.findPositionInArray("occupation",header);
            int posWorkplace = SiloUtil.findPositionInArray("workplace",header);
            int posIncome = SiloUtil.findPositionInArray("income",header);

            // read line
            while ((recString = in.readLine()) != null) {
                recCount++;
                String[] lineElements = recString.split(",");
                int id         = Integer.parseInt(lineElements[posId]);
                int hhid       = Integer.parseInt(lineElements[posHhId]);
                int age        = Integer.parseInt(lineElements[posAge]);
                int gender     = Integer.parseInt(lineElements[posGender]);
                String relShp  = lineElements[posRelShp].replace("\"", "");
                PersonRole pr  = PersonRole.valueOf(relShp);
                String strRace = lineElements[posRace].replace("\"", "");
                Race race = Race.valueOf(strRace);
                int occupation = Integer.parseInt(lineElements[posOccupation]);
                int workplace  = Integer.parseInt(lineElements[posWorkplace]);
                int income     = Integer.parseInt(lineElements[posIncome]);
                Person pp = new Person(id, hhid, age, gender, race, occupation, workplace, income); //this automatically puts it in id->person map in Person class
                pp.setRole(pr);
                if (id == SiloUtil.trackPp) {
                    SiloUtil.trackWriter.println("Read person with following attributes from " + fileName);
                    Person.getPersonFromId(id).logAttributes(SiloUtil.trackWriter);
                }
            }
        } catch (IOException e) {
            logger.fatal("IO Exception caught reading synpop household file: " + fileName);
            logger.fatal("recCount = " + recCount + ", recString = <" + recString + ">");
        }
        logger.info("Finished reading " + recCount + " persons.");
    }


    public void connectPersonsToHouseholds () {
        // connect person objects to household objects
        for (Person per: Person.getPersonArray()) {
            Household hhOfThisPerson = Household.getHouseholdFromId(per.getHhId());
            hhOfThisPerson.addPersonForInitialSetup(per);
            if (per.getHhId() == SiloUtil.trackHh || per.getId() == SiloUtil.trackPp) {
                SiloUtil.trackWriter.println("Connected person " + per.getId() + " to household " + per.getHhId());
            }
        }
    }


    public void setTypeOfAllHouseholds () {
        // define household types
        for (Household hh: Household.getHouseholdArray()) {
            hh.setType();
            hh.setHouseholdRace();
        }
    }


    public static HouseholdType defineHouseholdType (int hhSize, int hhIncome) {
        // define household type based on size and income

        HouseholdType ht = null;
        if (hhSize == 1) {
            if (hhIncome == 1) ht = HouseholdType.size1inc1;
            else if (hhIncome == 2) ht = HouseholdType.size1inc2;
            else if (hhIncome == 3) ht = HouseholdType.size1inc3;
            else ht = HouseholdType.size1inc4;
        } else if (hhSize == 2) {
            if (hhIncome == 1) ht = HouseholdType.size2inc1;
            else if (hhIncome == 2) ht = HouseholdType.size2inc2;
            else if (hhIncome == 3) ht = HouseholdType.size2inc3;
            else ht = HouseholdType.size2inc4;
        } else if (hhSize == 3) {
            if (hhIncome == 1) ht = HouseholdType.size3inc1;
            else if (hhIncome == 2) ht = HouseholdType.size3inc2;
            else if (hhIncome == 3) ht = HouseholdType.size3inc3;
            else ht = HouseholdType.size3inc4;
        } else if (hhSize > 3) {
            if (hhIncome == 1) ht = HouseholdType.size4inc1;
            else if (hhIncome == 2) ht = HouseholdType.size4inc2;
            else if (hhIncome == 3) ht = HouseholdType.size4inc3;
            else ht = HouseholdType.size4inc4;
        }
        return ht;
    }


    public static int getIncomeCategoryForIncome(int hhInc) {
        // return income category defined exogenously

        for (int category = 1; category <= SiloUtil.incBrackets.length; category++) {
            if (hhInc <= SiloUtil.incBrackets[category - 1]) return category;
        }
        return SiloUtil.incBrackets.length + 1;  // if income is larger than highest category
    }


    public static int getSpecifiedIncomeCategoryForIncome(int[] incCats, int hhInc) {
        // return income category defined exogenously

        for (int category = 1; category <= incCats.length; category++) {
            if (hhInc <= incCats[category - 1]) return category;
        }
        return incCats.length + 1;  // if income is larger than highest category
    }


    public static int getNumberOfWorkersInHousehold(Household hh) {
        // return number of workers in household hh
        int numberOfWorkers = 0;
        for (Person pp: hh.getPersons()) {
            if (pp.getOccupation() == 1) numberOfWorkers++;
        }
        return numberOfWorkers;
    }


    public static void definePersonRolesInHousehold (Household hh) {
        // define roles in this household
        findMarriedCouple(hh);
        defineUnmarriedPersons(hh);
    }


    public static void findMarriedCouple(Household hh) {
        // define role of person with ageMain in household where members have ageAll[]
        int[] ages = new int[hh.getHhSize()];
        Person[] pers = hh.getPersons();
        for (int i = 0; i < pers.length; i++) ages[i] = pers[i].getAge();

        int[] sortedAgeIndex = IndexSort.indexSort(ages);
        for (int i = sortedAgeIndex.length - 1; i >= 0; i--) {
            Person pp = pers[sortedAgeIndex[i]];
            int partnerId = findMostLikelyUnmarriedPartner(pp, hh);
            if (partnerId != -1) {
                Person partner = Person.getPersonFromId(partnerId);
                partner.setRole(PersonRole.married);
                pp.setRole(PersonRole.married);
                if (pp.getId() == SiloUtil.trackPp || pp.getHhId() == SiloUtil.trackHh)
                    SiloUtil.trackWriter.println("Defined role of person  " + pp.getId() + " in household " + pp.getHhId() +
                            " as " + pp.getRole());
                if (partner.getId() == SiloUtil.trackPp || partner.getHhId() == SiloUtil.trackHh)
                    SiloUtil.trackWriter.println("Defined role of partner " + partner.getId() + " in household " + partner.getHhId() +
                            " as " + partner.getRole());
                return;
            }
        }
    }


    public static void defineUnmarriedPersons (Household hh) {
        // For those that did not become the married couple define role in household (child or single)
        Person[] pps = hh.getPersons();
        for (Person pp: pps) {
            if (pp.getRole() == PersonRole.married) continue;
            boolean someone15to40yearsOlder = false;      // assumption that this person is a parent
            int ageMain = pp.getAge();
            for (Person per: pps) {
                if (per.getId() == pp.getId()) continue;
                int age = per.getAge();
                if (age >= ageMain + 15 && age <= ageMain + 40) someone15to40yearsOlder = true;
            }
            if ((someone15to40yearsOlder && ageMain < 50) || ageMain <= 15) pp.setRole(PersonRole.child);
            else pp.setRole(PersonRole.single);
            if (pp.getId() == SiloUtil.trackPp || pp.getHhId() == SiloUtil.trackHh)
                SiloUtil.trackWriter.println("Defined role of person " + pp.getId() + " in household " + pp.getHhId() +
                        " as " + pp.getRole());
        }
    }


    public static void removeHousehold(int householdId) {
        // remove household and add dwelling to vacancy list

        int dwellingId = Household.getHouseholdFromId(householdId).getDwellingId();
        if (dwellingId != -1) {
            Dwelling dd = Dwelling.getDwellingFromId(dwellingId);
            dd.setResidentID(-1);
            RealEstateDataManager.addDwellingToVacancyList(dd);
        }
        Household.remove(householdId);
        if (householdId == SiloUtil.trackHh)
            SiloUtil.trackWriter.println("Households " + householdId + " was removed");
    }


    public int getNumberOfHouseholds() {
        return Household.getHouseholdCount();
    }


    public int getNumberOfPersons() {
        return Person.getPersonCount();
    }


    public Collection<Person> getPersons() {
        return Person.getPersons();
    }


    public static void summarizePopulation () {
        // summarize population for summary file

        int pers[][] = new int[2][101];
        int ppRace[] = new int[4];
        for (Person per: Person.getPersonArray()) {
            int gender = per.getGender();
            int age = Math.min(per.getAge(), 100);
            pers[gender-1][age] += 1;
            ppRace[per.getRace().ordinal()]++;
        }
        int hhs[] = new int[10];
        int hht[] = new int[HouseholdType.values().length + 1];
        int hhRace[] = new int[4];
        int[] hhIncome = new int[Household.getHouseholdCount()];
        int hhIncomePos = 0;
        int hhByRegion[] = new int[SiloUtil.getHighestVal(geoData.getRegionList()) + 1];
        summarizeData.resultFile("Age,Men,Women");
        for (int i = 0; i <= 100; i++) {
            String row = i + "," + pers[0][i] + "," + pers[1][i];
            summarizeData.resultFile(row);
        }
        summarizeData.resultFile("ppByRace,hh");
        summarizeData.resultFile("white," + ppRace[0]);
        summarizeData.resultFile("black," + ppRace[1]);
        summarizeData.resultFile("hispanic," + ppRace[2]);
        summarizeData.resultFile("other," + ppRace[3]);
        for (Household hh: Household.getHouseholdArray()) {
            int hhSize = Math.min(hh.getHhSize(), 10);
            hhs[hhSize - 1]++;
            hht[hh.getHouseholdType().ordinal()]++;
            hhRace[hh.getRace().ordinal()]++;
            hhIncome[hhIncomePos] = hh.getHhIncome();
            hhIncomePos++;
            int region = geoData.getRegionOfZone(hh.getHomeZone());
            hhByRegion[region]++;
        }
                summarizeData.resultFile("hhByType,hh");
        for (HouseholdType ht: HouseholdType.values()) {
            String row = ht + "," + hht[ht.ordinal()];
            summarizeData.resultFile(row);
        }
        summarizeData.resultFile("hhByRace,hh");
        summarizeData.resultFile("white," + hhRace[0]);
        summarizeData.resultFile("black," + hhRace[1]);
        summarizeData.resultFile("hispanic," + hhRace[2]);
        summarizeData.resultFile("other," + hhRace[3]);
        String row = "hhBySize";
        for (int i: hhs) row = row + "," + i;
        summarizeData.resultFile(row);
        row = "AveHHSize," + Household.getAverageHouseholdSize();
        summarizeData.resultFile(row);
        double aveHHincome = SiloUtil.getSum(hhIncome) / Household.getHouseholdCount();
        row = "AveHHInc," + aveHHincome + ",MedianHHInc," + SiloUtil.getMedian(hhIncome);
        summarizeData.resultFile(row);
        // labor participation and commuting distance
        float[][][] labP = new float[2][2][5];
        float[][] commDist = new float[2][SiloUtil.getHighestVal(geoData.getRegionList()) + 1];
        for (Person per: Person.getPersonArray()) {
            int age = per.getAge();
            int gender = per.getGender() - 1;
            boolean employed = per.getWorkplace() > 0;
            int ageGroup = 0;
            if (age >= 65) ageGroup = 4;
            else if (age >= 50) ageGroup = 3;
            else if (age >= 30) ageGroup = 2;
            else if (age >= 18) ageGroup = 1;
            if (employed) labP[1][gender][ageGroup]++;
            else labP[0][gender][ageGroup]++;
            if (employed) {
                float ds = Accessibility.getAutoTravelTime(per.getHomeTaz(), Job.getJobFromId(per.getWorkplace()).getZone());
                commDist[0][geoData.getRegionOfZone(per.getHomeTaz())] += ds;
                commDist[1][geoData.getRegionOfZone(per.getHomeTaz())] ++;
            }
        }
        String[] grp = {"<18","18-29","30-49","50-64",">=65"};
        summarizeData.resultFile("laborParticipationRateByAge,male,female");
        for (int ag = 0; ag < 5; ag++) {
            Formatter f = new Formatter();
            f.format("%s,%f,%f", grp[ag], labP[1][0][ag]/(labP[0][0][ag]+labP[1][0][ag]), labP[1][1][ag]/(labP[0][1][ag]+labP[1][1][ag]));
            summarizeData.resultFile(f.toString());
        }
        summarizeData.resultFile("aveCommuteDistByRegion,miles");
        for (int i: geoData.getRegionList()) summarizeData.resultFile(i + "," + commDist[0][i] / commDist[1][i]);
    }


    public void setHighestHouseholdAndPersonId () {
        // identify highest household ID and highest person ID in use
        highestHouseholdIdInUse = 0;
        for (Household hh: Household.getHouseholdArray()) highestHouseholdIdInUse = Math.max(highestHouseholdIdInUse, hh.getId());
        highestPersonIdInUse = 0;
        for (Person pp: Person.getPersonArray()) highestPersonIdInUse = Math.max(highestPersonIdInUse, pp.getId());
    }


    public static int getNextHouseholdId () {
        // increase highestHouseholdIdInUse by 1 and return value
        highestHouseholdIdInUse++;
        return highestHouseholdIdInUse;
    }


    public static int getNextPersonId () {
        // increase highestPersonIdInUse by 1 and return value
        highestPersonIdInUse++;
        return highestPersonIdInUse;
    }


    public static int getHighestHouseholdIdInUse() {
        return highestHouseholdIdInUse;
    }

    public static int getHighestPersonIdInUse() {
        return highestPersonIdInUse;
    }

    private static int findMostLikelyUnmarriedPartner (Person per, Household hh) {
        // when assigning roles to persons, look for likely partner in household that is not married yet

        Person[] pers = hh.getPersons();
        float[] util = new float[pers.length];
        int selectedId = -1;
        for (int i = 0; i < pers.length; i++) {
            if (pers[i].getGender() != per.getGender() && pers[i].getRole() != PersonRole.married) {
                int ageDiff = Math.abs(per.getAge() - pers[i].getAge());
                if (ageDiff == 0) util[i] = 2;
                else util[i] = 1f / (float) ageDiff;
                if (selectedId == -1) selectedId = i;
                if (util[i] > util[selectedId]) selectedId = i;     // find most likely partner
            }
        }
        if (selectedId == -1) return selectedId;
        else return pers[selectedId].getId();
    }


    public static int findMostLikelyPartner(Person per, Household hh) {

        // find married partner that fits best for person per
        Person[] pers = hh.getPersons();
        float[] util = new float[pers.length];
        int selectedId = -1;
        for (int i = 0; i < pers.length; i++) {
            if (pers[i].getGender() != per.getGender() && pers[i].getRole() == PersonRole.married) {
                int ageDiff = Math.abs(per.getAge() - pers[i].getAge());
                if (ageDiff == 0) util[i] = 2;
                else util[i] = 1f / (float) ageDiff;
                if (selectedId == -1) selectedId = i;
                if (util[i] > util[selectedId]) selectedId = i;     // find most likely partner
            }
        }
        if (selectedId == -1) {
            logger.error("Could not find spouse of person " + per.getId() + " in household " + hh.getId());
            for (int i = 0; i < pers.length; i++)
                logger.error("Houshold member " + pers[i].getId() + " (gender: " + pers[i].getGender() + ") is " +
                        pers[i].getRole() + ", util: " + util[i]);

        }
        return pers[selectedId].getId();
    }


    public void calculateInitialSettings () {
        calculateInitialLaborParticipation();
        initialIncomeDistribution = calculateIncomeDistribution();
    }


    private void calculateInitialLaborParticipation() {
        // calculate share of people employed by age and gender

        laborParticipationShares = new float[2][100];
        int[][] count = new int[2][100];
        for (Person pp: Person.getPersonArray()) {
            int age = pp.getAge();
            if (age > 99) continue;  // people older than 99 will always be unemployed/retired
            int gender = pp.getGender();
            boolean employed = pp.getWorkplace() > 0;
            if (employed) laborParticipationShares[gender-1][age]++;
            count[gender-1][age]++;
        }
        // calculate shares
        for (int gen = 0; gen <=1; gen++) {
            for (int age = 0; age < 100; age++) {
                if (count[gen][age] > 0) laborParticipationShares[gen][age] = laborParticipationShares[gen][age] / (1f * count[gen][age]);
            }

            // smooth out shares
            for (int age = 18; age < 98; age++) {
                laborParticipationShares[gen][age] = (laborParticipationShares[gen][age-2]/4f +
                        laborParticipationShares[gen][age-1]/2f + laborParticipationShares[gen][age] +
                        laborParticipationShares[gen][age+1]/2f + laborParticipationShares[gen][age+2]/4f) / 2.5f;
            }
        }
    }


    private float[][][] calculateIncomeDistribution() {
        // calculate income distribution by age, gender and occupation

        float[][][] averageIncome = new float[2][100][2];              // income by gender, age and unemployed/employed
        int[][][] count = new int[2][100][2];
        for (Person pp: Person.getPersonArray()) {
            int age = Math.min(99, pp.getAge());
            int occupation = 0;
            if (pp.getOccupation() == 1) occupation = 1;
            averageIncome[pp.getGender() - 1][age][occupation] += pp.getIncome();
            count[pp.getGender() - 1][age][occupation]++;
        }
        for (int i = 0; i < averageIncome.length; i++) {
            for (int j = 0; j < averageIncome[i].length; j++) {
                for (int k = 0; k < averageIncome[i][j].length; k++) {
                    if (count[i][j][k] > 0) averageIncome[i][j][k] = averageIncome[i][j][k] / count[i][j][k];
                }
            }
        }
        // smooth out income
        for (int i = 0; i < averageIncome.length; i++) {
            for (int j = 2; j < averageIncome[i].length - 2; j++) {
                for (int k = 0; k < averageIncome[i][j].length; k++) {
                    averageIncome[i][j][k] = (averageIncome[i][j-2][k]/4f + averageIncome[i][j-1][k]/2f +
                            averageIncome[i][j][k] + averageIncome[i][j+1][k]/2f + averageIncome[i][j+2][k]/4f) / 2.5f;
                }
            }
        }
        return averageIncome;
    }


    public void adjustIncome() {
        // select who will get a raise or drop in salary

        currentIncomeDistribution = calculateIncomeDistribution();   // income by gender, age and unemployed/employed
        Integer[] personArray = new Integer[Person.getPersonCount()];
        Person[] pps = Person.getPersonArray();
        for (int per = 0; per < pps.length; per++) personArray[per] = pps[per].getId();

        // Multi-threading code
        Function1<Integer, Void> incomeChangeFunction = new Function1<Integer, Void>() {
            public Void apply(Integer per) {
                adjustIncomeForPerson(per);
                return null;
            }
        };
//
        Iterator<Integer> incomeChangeIterator = ArrayUtil.getIterator(personArray);
        IteratorAction<Integer> itTask = new IteratorAction<>(incomeChangeIterator, incomeChangeFunction);
        ForkJoinPool pool = ForkJoinPoolFactory.getForkJoinPool();
        pool.execute(itTask);
        itTask.waitForCompletion();
    }


    private void adjustIncomeForPerson (int per) {
        // adjust income of person with ID per

        Person pp = Person.getPersonFromId(per);
        int gender = pp.gender - 1;
        int age = Math.min(99, pp.age);
        int occ = 0;
        if (pp.occupation == 1) occ = 1;
        float desiredShift = initialIncomeDistribution[gender][age][occ] - currentIncomeDistribution[gender][age][occ];
        int newIncome = selectNewIncome(pp.getIncome(), desiredShift);
        pp.setIncome(newIncome);
    }


    private int selectNewIncome (int currentIncome, float desiredShift) {
        // calculate new income using a normal distribution

        double[] prob = new double[21];
        int lowerBound;
        int upperBound;
        if (Math.abs(desiredShift) < 1000) {
            lowerBound = -5000;
            upperBound = 5000;
        } else if (desiredShift > 1000) {
            lowerBound = (int) -desiredShift;
            upperBound = (int) desiredShift * 3;
        } else {
            lowerBound = (int) desiredShift * 3;
            upperBound = (int) -desiredShift;
        }
        int smallestAbsValuePos = 0;
        float smallestAbsValue = Float.MAX_VALUE;
        for (int i = 0; i < prob.length; i++) {
            int change = lowerBound + (upperBound - lowerBound) / (prob.length-1) * i;
            if (Math.abs(change) < smallestAbsValue) {
                smallestAbsValuePos = i;
                smallestAbsValue = Math.abs(change);
            }
            // normal distribution to calculate change of income
            prob[i] = (1 / (meanIncomeChange * Math.sqrt(2 * 3.1416))) * Math.exp(-(Math.pow((desiredShift - change), 2) /
                    (2 * Math.pow(meanIncomeChange, 2))));
        }
        prob[smallestAbsValuePos] = prob[smallestAbsValuePos] * 10;   // make no change most likely
        int sel = SiloUtil.select(prob);
        return Math.max((currentIncome + lowerBound + (upperBound - lowerBound) / prob.length * sel), 0);
    }


    public static int selectIncomeForPerson (int gender, int age, int occupation) {
        // select income for household based on gender, age and occupation

        double[] prob = new double[21];
        int[] change = new int[21];
        for (int i = 0; i < prob.length; i++) {
            // normal distribution to calculate change of income
            change[i] = (int) (-5000f + 10000f * (float) i / (prob.length - 1f));
            prob[i] = (1 / (meanIncomeChange * Math.sqrt(2 * 3.1416))) *
                    Math.exp(-(Math.pow(change[i], 2) / (2 * Math.pow(meanIncomeChange, 2))));
        }
        int sel = SiloUtil.select(prob);
        return Math.max((int) initialIncomeDistribution[gender][age][occupation] + change[sel], 0);
    }


    public void setUpChangeOfJob(int year) {
        // select people that will lose employment or start new job

        if (!EventRules.ruleQuitJob() && !EventRules.ruleStartNewJob()) return;
        logger.info("  Planning job changes (hire and fire) for the year " + year);

        // count currently employed people
        final float[][] currentlyEmployed = new float[2][100];
        final float[][] currentlyUnemployed = new float[2][100];
        for (Person pp : Person.getPersonArray()) {
            int age = pp.getAge();
            if (age > 99) continue;  // people older than 99 will always be unemployed/retired
            int gender = pp.getGender();
            boolean employed = pp.getWorkplace() > 0;
            if (employed) {
                currentlyEmployed[gender - 1][age]++;
            } else {
                currentlyUnemployed[gender - 1][age]++;
            }
        }

        // calculate change rates
        float[][] changeRate = new float[2][100];
        for (int gen = 0; gen <= 1; gen++) {
            for (int age = 0; age < 100; age++) {
                float change = laborParticipationShares[gen][age] *
                        (currentlyEmployed[gen][age] + currentlyUnemployed[gen][age]) - currentlyEmployed[gen][age];
                if (change > 0) {
                    // probability to find job
                    changeRate[gen][age] = (change / (1f * currentlyUnemployed[gen][age]));
                } else {
                    // probability to lose job
                    changeRate[gen][age] = (change / (1f * currentlyEmployed[gen][age]));
                }
            }
        }

        int[][] testCounter = new int[2][100];
        // plan employment changes
        ArrayList<Integer> alFindJob = new ArrayList<>();
        ArrayList<Integer> alQuitJob = new ArrayList<>();
        for (Person pp : Person.getPersonArray()) {
            int age = pp.getAge();
            if (age > 99) continue;  // people older than 99 will always be unemployed/retired
            int gen = pp.getGender() - 1;
            boolean employed = pp.getWorkplace() > 0;

            // find job
            if (changeRate[gen][age] > 0 && !employed) {
                if (SiloModel.rand.nextFloat() < changeRate[gen][age]) {
                    alFindJob.add(pp.getId());
                    testCounter[gen][age]++;
                }
            }
            // lose job
            if (changeRate[gen][age] < 0 && employed) {
                if (SiloModel.rand.nextFloat() < Math.abs(changeRate[gen][age])) {
                    alQuitJob.add(pp.getId());
                    testCounter[gen][age]--;
                }
            }
        }

        quitJobPersonIds = SiloUtil.convertIntegerArrayListToArray(alQuitJob);
        startNewJobPersonIds = SiloUtil.convertIntegerArrayListToArray(alFindJob);

    }


    public static int[] getStartNewJobPersonIds() {
        return startNewJobPersonIds;
    }

    public static int[] getQuitJobPersonIds() {
        return quitJobPersonIds;
    }


    public HashMap<Integer, int[]> getHouseholdsByZone () {
        // return HashMap<Zone, ArrayOfHouseholdIds>

        HashMap<Integer, int[]> hhByZone = new HashMap<>();
        for (Household hh: Household.getHouseholdArray()) {
            int zone = hh.getHomeZone();
            if (hhByZone.containsKey(zone)) {
                int[] oldList = hhByZone.get(zone);
                int[] newList = SiloUtil.expandArrayByOneElement(oldList, hh.getId());
                hhByZone.put(zone, newList);
            } else {
                hhByZone.put(zone, new int[]{hh.getId()});
            }

        }
        return hhByZone;
    }


    public static int[] getNumberOfHouseholdsByRegion() {
        // return number of households by region
        int[] hhByRegion = new int[geoData.getRegionList().length];
        for (Household hh: Household.getHouseholdArray()) {
            if (hh.getHomeZone() == -1) continue;  // unclear why this is needed
            int region = geoData.getRegionOfZone(hh.getHomeZone());
            hhByRegion[geoData.getRegionIndex(region)]++;
        }
        return hhByRegion;
    }


    public static void calculateMedianHouseholdIncomeByMSA() {
        // calculate median income for each MSA

        HashMap<Integer, ArrayList<Integer>> incomeHashMap = new HashMap<>();
        for (Household hh: Household.getHouseholdArray()) {
            int homeMSA = geoData.getMSAOfZone(hh.getHomeZone());
            if (incomeHashMap.containsKey(homeMSA)) {
                ArrayList<Integer> inc = incomeHashMap.get(homeMSA);
                inc.add(hh.getHhIncome());
            } else {
                ArrayList<Integer> inc = new ArrayList<>();
                inc.add(hh.getHhIncome());
                incomeHashMap.put(homeMSA, inc);
            }
        }
        medianIncome = new float[99999];
        for (Integer thisMsa: incomeHashMap.keySet()) {
            medianIncome[thisMsa] = SiloUtil.getMedian(SiloUtil.convertIntegerArrayListToArray(incomeHashMap.get(thisMsa)));
        }
    }


    public static float getMedianIncome(int msa) {
        return medianIncome[msa];
    }


    public void summarizeHouseholdsNearMetroStations () {
        // summarize households in the vicinity of selected Metro stops

        if (!ResourceUtil.getBooleanProperty(rb, PROPERTIES_SUMMARIZE_METRO)) return;
        TableDataSet selectedMetro = SiloUtil.readCSVfile(rb.getString(PROPERTIES_SELECTED_METRO));

        String directory = SiloUtil.baseDirectory + "scenOutput/" + SiloUtil.scenarioName;
        SiloUtil.createDirectoryIfNotExistingYet(directory);
        String fileName = (directory + "/" + rb.getString(PROPERTIES_HH_NEAR_METRO) + "_" +
                SiloUtil.gregorianIterator + ".csv");
        PrintWriter pw = SiloUtil.openFileForSequentialWriting(fileName, false);
        pw.print("income,dist");
        for (int row = 1; row <= selectedMetro.getRowCount(); row++) pw.print("," +
                selectedMetro.getStringValueAt(row, "MetroStation") + " (" + (int) selectedMetro.getValueAt(row, "Zone") + ")");
        pw.println();

        // summarize households by distance from Metro stop and income group
        int[][][] hhCounter = new int[selectedMetro.getRowCount()][11][4];
        HashMap<Integer, ArrayList> hhByDistToMetro = new HashMap<>();
        for (Integer dist = 0; dist <= 20; dist++) hhByDistToMetro.put(dist, new ArrayList<Integer>());

        for (Household hh: Household.getHouseholdArray()) {
            int incCat = getIncomeCategoryForIncome(hh.getHhIncome());
            Integer smallestDist = 21;
            for (int row = 1; row <= selectedMetro.getRowCount(); row++) {
                int metroZone = (int) selectedMetro.getValueAt(row, "Zone");
                int dist = (int) SiloUtil.rounder(Accessibility.getAutoTravelTime(hh.getHomeZone(), metroZone), 0);
                smallestDist = Math.min(smallestDist, dist);
                if (dist > 10) continue;
                hhCounter[row-1][dist][incCat-1]++;
            }
            if (smallestDist <= 20) {
                ArrayList<Integer> al = hhByDistToMetro.get(smallestDist);
                al.add(hh.getHhIncome());
                hhByDistToMetro.put(smallestDist, al);
            }
        }

        // write out summary by Metro Stop
        for (int inc = 1; inc <= 4; inc++) {
            for (int dist = 0; dist <= 10; dist++) {
                pw.print(inc + "," + dist);
                for (int row = 1; row <= selectedMetro.getRowCount(); row++) {
                    pw.print("," + hhCounter[row - 1][dist][inc - 1]);
                }
                pw.println();
            }
        }

        // write out summary by distance bin
        pw.println("distanceRing,householdCount,medianIncome");
        for (int dist = 0; dist <= 20; dist++) {
            int[] incomes = SiloUtil.convertIntegerArrayListToArray(hhByDistToMetro.get(dist));
            pw.println(dist + "," + incomes.length + "," + SiloUtil.getMedian(incomes));
        }

        pw.close();
    }
}
