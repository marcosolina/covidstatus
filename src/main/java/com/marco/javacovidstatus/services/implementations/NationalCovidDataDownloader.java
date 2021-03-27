package com.marco.javacovidstatus.services.implementations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.model.dto.NationalDailyDataDto;
import com.marco.javacovidstatus.services.interfaces.CovidDataDownloader;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;
import com.marco.utils.MarcoException;

/**
 * It downloads and process the National data
 * 
 * @author Marco
 *
 */
public class NationalCovidDataDownloader extends CovidDataDownloader {
    private static final Logger _LOGGER = LoggerFactory.getLogger(NationalCovidDataDownloader.class);

    @Autowired
    private CovidDataService dataService;
    private String url = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-andamento-nazionale/dpc-covid19-ita-andamento-nazionale-%s.csv";
    
    private static final int COL_NEW_CASUALTIES = 10;
    private static final int COL_NEW_TESTS = 14;
    private static final int COL_NEW_INFECTIONS = 8;
    private static final int COL_NEW_HOSPITALISED = 4;
    private static final int COL_NEW_INTENSIVE_THERAPY = 3;
    private static final int COL_NEW_RECOVERED = 9;

    public NationalCovidDataDownloader(WebClient webClient) {
        super(webClient);
    }

    @Override
    public void downloadData() {
        _LOGGER.info("Downloading National Data");

        LocalDate end = LocalDate.now();
        LocalDate start = getStartDate();
        if (start == null) {
            start = this.defaultStartData;
        }


        try {
        	List<Integer> lastWeeknNewInfection = getNationalLastWeeknNewInfection(end);
        	
            while (start.isBefore(end)) {
                start = start.plusDays(1);
                if (_LOGGER.isDebugEnabled()) {
                    _LOGGER.debug(String.format("Looking for national data at date: %s", start.toString()));
                }

                /*
                 * Retrieve the data
                 */
                NationalDailyDataDto precedente = getNationalDataAtDate(start.minusDays(1));
                NationalDailyDataDto corrente = getNationalDataAtDate(start);
                if (corrente == null) {
                    break;
                }

                lastWeeknNewInfection.add(corrente.getNewInfections());

                NationalDailyDataDto dataToSave = calculateNationalDailyDataDelta(corrente, precedente,
                        lastWeeknNewInfection.get(0));

                if (lastWeeknNewInfection.size() > 7) {
                    lastWeeknNewInfection.remove(0);
                }

                /*
                 * Store the info
                 */
                dataService.storeData(dataToSave);
            }
        } catch (Exception e) {
            _LOGGER.error(e.getMessage());
        }

    }

    @Override
    public LocalDate getStartDate() {
        return dataService.getNationalMaxDateAvailable();
    }

    /**
     * It returns the number of the new infections in the last week
     * 
     * @param end
     * @return
     * @throws MarcoException 
     */
    private List<Integer> getNationalLastWeeknNewInfection(LocalDate end) throws MarcoException {
        List<NationalDailyDataDto> list = dataService.getDatesInRangeAscending(end.minusDays(7), end);
        return list.stream().map(NationalDailyDataDto::getNewInfections).collect(Collectors.toList());
    }

    /**
     * It parses the National daily CSV
     * 
     * @param date
     * @return
     */
    private NationalDailyDataDto getNationalDataAtDate(LocalDate date) {
        _LOGGER.trace("Inside CronServiceGit.getNationalDataAtDate");

        /*
         * Inserting the date into the URL
         */
        String uriFile = String.format(url, date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        List<String> listRows = getCsvRows(uriFile);
        if (listRows.isEmpty()) {
            return null;
        }
        List<String> columns = Arrays.asList(listRows.get(0).split(","));

        NationalDailyDataDto data = new NationalDailyDataDto();
        data.setDate(date);
        data.setNewCasualties(Integer.parseInt(columns.get(COL_NEW_CASUALTIES)));
        data.setNewTests(Integer.parseInt(columns.get(COL_NEW_TESTS)));
        data.setNewInfections(Integer.parseInt(columns.get(COL_NEW_INFECTIONS)));
        data.setNewHospitalized(Integer.parseInt(columns.get(COL_NEW_HOSPITALISED)));
        data.setNewIntensiveTherapy(Integer.parseInt(columns.get(COL_NEW_INTENSIVE_THERAPY)));
        data.setNewRecovered(Integer.parseInt(columns.get(COL_NEW_RECOVERED)));
        
        return data;
    }

    /**
     * It calculates the national daily data
     * 
     * @param current
     * @param previous
     * @param newInfectionOneWeekAgo
     * @return
     */
    private NationalDailyDataDto calculateNationalDailyDataDelta(NationalDailyDataDto current, NationalDailyDataDto previous,
            int newInfectionOneWeekAgo) {
        NationalDailyDataDto newDailyData = new NationalDailyDataDto();
        /*
         * Do some math
         */
        BigDecimal infectionPercentage = BigDecimal
                .valueOf(infectedPercentage(current.getNewTests(), previous.getNewTests(), current.getNewInfections()))
                .setScale(2, RoundingMode.DOWN);

        /*
         * Store the data
         */
        newDailyData.setDate(current.getDate());
        newDailyData.setInfectionPercentage(infectionPercentage);
        newDailyData.setNewCasualties(current.getNewCasualties() - previous.getNewCasualties());
        newDailyData.setNewInfections(current.getNewInfections());
        newDailyData.setNewTests(current.getNewTests() - previous.getNewTests());
        newDailyData.setNewHospitalized(current.getNewHospitalized() - previous.getNewHospitalized());
        newDailyData.setNewIntensiveTherapy(current.getNewIntensiveTherapy() - previous.getNewIntensiveTherapy());
        newDailyData.setNewRecovered(current.getNewRecovered() - previous.getNewRecovered());

        if (newDailyData.getInfectionPercentage().compareTo(BigDecimal.ZERO) < 0) {
        	newDailyData.setInfectionPercentage(BigDecimal.ZERO);
        }
        
        if (newDailyData.getNewTests() < 0) {
        	newDailyData.setNewTests(0);
        }
        
        if (newDailyData.getNewIntensiveTherapy() < 0) {
            newDailyData.setNewIntensiveTherapy(0);
        }
        if (newDailyData.getNewHospitalized() < 0) {
            newDailyData.setNewHospitalized(0);
        }
        if (newDailyData.getNewRecovered() < 0) {
            newDailyData.setNewRecovered(0);
        }
        if (newDailyData.getNewCasualties() < 0) {
            newDailyData.setNewCasualties(0);
        }

        /*
         * Do some math
         */
        BigDecimal casualtiesPercentage = BigDecimal
                .valueOf(casualtiesPercentage(newDailyData.getNewCasualties(), newInfectionOneWeekAgo))
                .setScale(2, RoundingMode.DOWN);
        newDailyData.setCasualtiesPercentage(casualtiesPercentage);

        if (newDailyData.getCasualtiesPercentage().compareTo(BigDecimal.ZERO) < 0) {
            newDailyData.setCasualtiesPercentage(BigDecimal.ZERO);
        }

        return newDailyData;
    }
}
