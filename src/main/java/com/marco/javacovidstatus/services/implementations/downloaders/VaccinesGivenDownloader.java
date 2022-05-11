package com.marco.javacovidstatus.services.implementations.downloaders;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.model.dto.VaccinatedPeopleDto;
import com.marco.javacovidstatus.services.interfaces.NotificationSenderInterface;
import com.marco.javacovidstatus.services.interfaces.VaccineDataService;
import com.marco.javacovidstatus.services.interfaces.downloaders.CovidDataDownloader;
import com.marco.javacovidstatus.utils.CovidUtils;
import com.marco.utils.DateUtils;
import com.marco.utils.enums.DateFormats;

/**
 * This class will update the data regarding the given vaccines. (People
 * vaccinated)
 * 
 * @author Marco
 *
 */
public class VaccinesGivenDownloader extends CovidDataDownloader {
    @Autowired
    private VaccineDataService dataService;
    @Autowired
    private NotificationSenderInterface notificationService;
    @Value("${covidstatus.vaccines.monodose.suppliers}")
    private List<String> monodoseSuppliers;

    private static final Logger _LOGGER = LoggerFactory.getLogger(VaccinesGivenDownloader.class);

    private static final String CSV_URL = "https://raw.githubusercontent.com/italia/covid19-opendata-vaccini/master/dati/somministrazioni-vaccini-latest.csv";
    
    // @formatter:off
    public static final String COL_DATE                     = "data";
    public static final String COL_REGION_CODE              = "ISTAT";
    public static final String COL_AREA_CODE                = "area";
    public static final String COL_SUPPLIER                 = "forn";
    public static final String COL_AGE_RANGE                = "eta";
    public static final String COL_MEN_COUNTER              = "m";
    public static final String COL_WOMEN_COUNTER            = "f";
    /*
     * They have removed these columns from the CSV file. I will comment them out
     * but keep them just for reference... Just in case they restore them...
    public static final String COL_NHS_COUNTER              = "categoria_operatori_sanitari_sociosanitari";
    public static final String COL_NON_NHS_COUNTER          = "categoria_personale_non_sanitario";
    public static final String COL_NURSING_COUNTER          = "categoria_ospiti_rsa";
    public static final String COL_AGE_60_69_COUNTER        = "categoria_60_69";
    public static final String COL_AGE_70_79_COUNTER        = "categoria_70_79";
    public static final String COL_OVER_80_COUNTER          = "categoria_over80";
    public static final String COL_PUBLIC_ORDER_COUNTER     = "categoria_forze_armate";
    public static final String COL_SCHOOL_STAFF_COUNTER     = "categoria_personale_scolastico";
    public static final String COL_FRAGILE_COUNTER          = "categoria_soggetti_fragili";
    public static final String COL_OTHER_PEOPLE_COUNTER     = "categoria_altro";
    */
    public static final String COL_FIRST_DOSE_COUNTER       = "d1";
    public static final String COL_SECOND_DOSE_COUNTER      = "d2";
    public static final String COL_VACCINE_AFTER_INFECT     = "dpi";
    public static final String COL_THIRD_DOSE_COUNTER       = "db1";
    public static final String COL_FOURTH_DOSE_COUNTER      = "dbi";
    public static final String COL_FOURTH_DOSE_COUNTER2     = "db2";
    // @formatter:on

    public VaccinesGivenDownloader(WebClient webClient) {
        super(webClient);
    }

    @Override
    public boolean downloadData() {
        _LOGGER.info("Downloading Given vaccines data");

        List<String> rows = this.getCsvRows(CSV_URL, null, false);

        if (rows.isEmpty()) {
            notificationService.sendEmailMessage("marcosolina@gmail.com", "Marco Solina - Covid Status", "Non ci sono più i dati nel repository");
            return false;
        }

        Map<String, Integer> columnsPositions = CovidUtils.getColumnsIndex(rows.get(0));
        rows.remove(0);
        
        if (columnsPositions.size() != 13) {
            notificationService.sendEmailMessage("marcosolina@gmail.com", "Marco Solina - Covid Status", "La struttura dei dati vaccini somministrati e' stata modificata...");
            return false;
        }
        

        /*
         * Forcing the re-loading of all the data. I notice that the government updates
         * the data of the previous days. There is no way for me to understand
         * "how much back" they go... so I decided to clear the table at every scan
         */
        dataService.deleteAllGivenVaccineData();

        LocalDate startDate = getStartDate();
        AtomicBoolean error = new AtomicBoolean();

        rows.stream().forEach(row -> {
            try {
                String[] columns = row.split(",");

                String regionCode = columns[columnsPositions.get(COL_REGION_CODE)];
                String areaCode = columns[columnsPositions.get(COL_AREA_CODE)];
                if (areaCode.equals("PAB")) {
                    regionCode = "21";
                } else if (areaCode.equals("PAT")) {
                    regionCode = "22";
                }

                LocalDate date = DateUtils.fromStringToLocalDate(columns[columnsPositions.get(COL_DATE)], DateFormats.DB_DATE);

                if (!date.isAfter(startDate)) {
                    return;
                }

                VaccinatedPeopleDto data = new VaccinatedPeopleDto();
                data.setDate(date);
                data.setRegionCode(("00" + regionCode).substring(regionCode.length()));
                data.setSupplier(columns[columnsPositions.get(COL_SUPPLIER)]);
                data.setAgeRange(columns[columnsPositions.get(COL_AGE_RANGE)]);
                data.setMenCounter(Integer.parseInt(columns[columnsPositions.get(COL_MEN_COUNTER)]));
                data.setWomenCounter(Integer.parseInt(columns[columnsPositions.get(COL_WOMEN_COUNTER)]));
                /*
                data.setNhsPeopleCounter(Integer.parseInt(columns[columnsPositions.get(COL_NHS_COUNTER)]));
                data.setNonNhsPeopleCounter(Integer.parseInt(columns[columnsPositions.get(COL_NON_NHS_COUNTER)]));
                data.setNursingHomeCounter(Integer.parseInt(columns[columnsPositions.get(COL_NURSING_COUNTER)]));
                data.setAge6069counter(Integer.parseInt(columns[columnsPositions.get(COL_AGE_60_69_COUNTER)]));
                data.setAge7079counter(Integer.parseInt(columns[columnsPositions.get(COL_AGE_70_79_COUNTER)]));
                data.setOver80Counter(Integer.parseInt(columns[columnsPositions.get(COL_OVER_80_COUNTER)]));
                data.setPublicOrderCounter(Integer.parseInt(columns[columnsPositions.get(COL_PUBLIC_ORDER_COUNTER)]));
                data.setSchoolStaffCounter(Integer.parseInt(columns[columnsPositions.get(COL_SCHOOL_STAFF_COUNTER)]));
                data.setFragilePeopleCounter(Integer.parseInt(columns[columnsPositions.get(COL_FRAGILE_COUNTER)]));
                data.setOtherPeopleCounter(Integer.parseInt(columns[columnsPositions.get(COL_OTHER_PEOPLE_COUNTER)]));
                */
                
                /**
                 * The goverment put the vaccines which requires just one shot into the "first"
                 * dose. I decided to move these into the mono dose column
                 */
                if(monodoseSuppliers.contains(data.getSupplier())) {
                    data.setMonoDoseCounter(Integer.parseInt(columns[columnsPositions.get(COL_FIRST_DOSE_COUNTER)]));
                }else {
                    data.setFirstDoseCounter(Integer.parseInt(columns[columnsPositions.get(COL_FIRST_DOSE_COUNTER)]));
                    data.setSecondDoseCounter(Integer.parseInt(columns[columnsPositions.get(COL_SECOND_DOSE_COUNTER)]));
                }
                data.setThirdDoseCounter(Integer.parseInt(columns[columnsPositions.get(COL_THIRD_DOSE_COUNTER)]));
                data.setFourthDoseCounter(Integer.parseInt(columns[columnsPositions.get(COL_FOURTH_DOSE_COUNTER)]) + Integer.parseInt(columns[columnsPositions.get(COL_FOURTH_DOSE_COUNTER2)]));
                data.setDoseAfterInfectCounter(Integer.parseInt(columns[columnsPositions.get(COL_VACCINE_AFTER_INFECT)]));
                

                _LOGGER.trace(String.format("Storing Given vaccine data date: %s Region: %s AgeRange: %s Supplier: %s",
                        data.getDate(), data.getRegionCode(), data.getAgeRange(), data.getSupplier()));
                dataService.saveGivenVaccinesData(data);
            } catch (Exception e) {
                String message = String.format("Error while saving: %s", row);
                _LOGGER.error(message);
                error.set(true);
            }
        });

        if (error.get()) {
            String message = "There was an error with the data, cleaning everything and retrying at the next cron tick";
            _LOGGER.error(message);
            notificationService.sendEmailMessage("marcosolina@gmail.com", "Marco Solina - Covid Status", message);
            dataService.deleteAllGivenVaccineData();
        }

        dataService.addMissingRowsForNoVaccinationDays();
        return !error.get();
    }

    @Override
    public LocalDate getStartDate() {
        LocalDate date = dataService.getLastDateOfAvailableDataForGivenVaccines();
        if (date == null) {
            date = this.defaultStartData;
        }
        return date;
    }
}
