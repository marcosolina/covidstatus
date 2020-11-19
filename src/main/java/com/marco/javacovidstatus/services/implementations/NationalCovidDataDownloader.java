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

import com.marco.javacovidstatus.model.dto.NationalDailyData;
import com.marco.javacovidstatus.services.interfaces.CovidDataDownloader;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;

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

        List<Integer> lastWeeknNewInfection = getNationalLastWeeknNewInfection(end);

        try {
            while (start.isBefore(end)) {
                start = start.plusDays(1);
                if (_LOGGER.isDebugEnabled()) {
                    _LOGGER.debug(String.format("Looking for national data at date: %s", start.toString()));
                }

                /*
                 * Retrieve the data
                 */
                NationalDailyData precedente = getNationalDataAtDate(start.minusDays(1));
                NationalDailyData corrente = getNationalDataAtDate(start);
                if (corrente == null) {
                    break;
                }

                lastWeeknNewInfection.add(corrente.getNewInfections());

                NationalDailyData dataToSave = calculateNationalDailyDataDelta(corrente, precedente,
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
     */
    private List<Integer> getNationalLastWeeknNewInfection(LocalDate end) {
        List<NationalDailyData> list = dataService.getDatesInRangeAscending(end.minusDays(7), end);
        return list.stream().map(NationalDailyData::getNewInfections).collect(Collectors.toList());
    }

    /**
     * It parses the National daily CSV
     * 
     * @param date
     * @return
     */
    private NationalDailyData getNationalDataAtDate(LocalDate date) {
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

        NationalDailyData data = new NationalDailyData();
        data.setDate(date);
        data.setNewCasualties(Integer.parseInt(columns.get(10)));
        data.setNewTests(Integer.parseInt(columns.get(14)));
        data.setNewInfections(Integer.parseInt(columns.get(8)));
        data.setNewHospitalized(Integer.parseInt(columns.get(4)));
        data.setNewIntensiveTherapy(Integer.parseInt(columns.get(3)));
        data.setNewRecovered(Integer.parseInt(columns.get(9)));

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
    private NationalDailyData calculateNationalDailyDataDelta(NationalDailyData current, NationalDailyData previous,
            int newInfectionOneWeekAgo) {
        NationalDailyData newDailyData = new NationalDailyData();
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