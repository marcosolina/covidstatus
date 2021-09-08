package com.marco.javacovidstatus.repositories.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.marco.javacovidstatus.model.entitites.vaccines.AgeRangeGivenVaccines;
import com.marco.javacovidstatus.model.entitites.vaccines.DailySumGivenVaccines;
import com.marco.javacovidstatus.model.entitites.vaccines.DoseCounter;
import com.marco.javacovidstatus.model.entitites.vaccines.EntitySomministrazioneVaccini;
import com.marco.javacovidstatus.model.entitites.vaccines.TotalVaccineGivenPerRegion;
import com.marco.javacovidstatus.model.entitites.vaccines.VaccinesGivenPerRegion;

/**
 * This interface provides the contract to query the database
 * 
 * @see {@link EntitySomministrazioneVaccini}
 * @author Marco
 *
 */
public interface VaccinesGivenRepo {
    /**
     * It stores the data in the database
     * 
     * @param {@link EntitySomministrazioneVaccini}
     * @return the operation status
     */
    public boolean saveEntity(EntitySomministrazioneVaccini entity);

    /**
     * It clears the table
     * 
     * @return
     */
    public boolean deleteAllData();

    /**
     * It returns an array with total vaccines given between the provided dates
     * grouped by "Dose"
     * 
     * @param start
     * @param end
     * @return
     */
    public List<DoseCounter> getListOfGivenDosesBetween(LocalDate start, LocalDate end);

    /**
     * It returns an array with total vaccines given between the provided dates to
     * the different type of people
     * 
     * @param start
     * @param end
     * @return
     */
    public List<DailySumGivenVaccines> getDailyReportOfGivenVaccinesBetween(LocalDate start, LocalDate end);

    /**
     * It performs some queries to add empty rows for the days where the data are
     * missing
     */
    public void addMissingRowsForNoVaccinationDays();

    /**
     * It returns an array with the total vaccines given betweeb the provided dates
     * group by age range
     * 
     * @param start
     * @param end
     * @return
     */
    public List<AgeRangeGivenVaccines> getListOfGivenVaccinesBetweenDatesGroupByAgeRange(LocalDate start, LocalDate end);

    /**
     * It returns the last date of available data, null if none are available
     * 
     * @return
     */
    public LocalDate getLastDateForAvailableData();

    /**
     * It returns the total number of people who received the vaccine, first +
     * second shot + mono dose
     * 
     * @return
     */
    public Long getTotalGivenVaccines();

    /**
     * It returns the number of vaccination (mono dose or full cycle) listed per region
     * 
     * @return
     */
    public List<TotalVaccineGivenPerRegion> getListTotalVaccinatedPeoplePerRegion();

    /**
     * It removes the informations stored for the specific date
     * 
     * @param date
     */
    public void deleteInformationForDate(LocalDate date);

    /**
     * It returns the total number of given vaccines per age range
     * 
     * @return
     */
    public List<AgeRangeGivenVaccines> getListOfTotalGivenVaccinesGorupByAgeRange();
    
    /**
     * It returns the total number of people vaccinated.
     * @return
     */
    public AgeRangeGivenVaccines getTotalPeolpleVaccinated();
    
    /**
     * It returns the total number of people vaccinated grouped by region.
     * @return
     */
    public List<VaccinesGivenPerRegion> getListOfTotalGivenVaccinesGorupByRegion();
}
