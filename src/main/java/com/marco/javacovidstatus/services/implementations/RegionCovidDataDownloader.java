package com.marco.javacovidstatus.services.implementations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.model.RegionalDailyData;
import com.marco.javacovidstatus.services.interfaces.CovidDataDownloader;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;

/**
 * It downloads and process the Regions data
 * 
 * @author Marco
 *
 */
public class RegionCovidDataDownloader extends CovidDataDownloader {

    private static final Logger _LOGGER = LoggerFactory.getLogger(RegionCovidDataDownloader.class);

    @Autowired
    private CovidDataService dataService;
    private String url = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-regioni/dpc-covid19-ita-regioni-%s.csv";

    public RegionCovidDataDownloader(WebClient webClient) {
        super(webClient);
    }

    @Override
    public void downloadData() {
        _LOGGER.info("Downloading Regions Data");

        LocalDate end = LocalDate.now();
        LocalDate start = getStartDate();
        if (start == null) {
            start = this.defaultStartData;
        }

        Map<String, List<Integer>> mapInfectionLastWeek = getRegionalLastWeeknNewInfection(end);

        try {
            while (start.isBefore(end)) {
                start = start.plusDays(1);
                _LOGGER.debug(String.format("Looking for regional data at date: %s", start.toString()));

                /*
                 * Retrieve the data from the repository
                 */
                Map<String, RegionalDailyData> precedente = parseRegionData(start.minusDays(1));
                Map<String, RegionalDailyData> corrente = parseRegionData(start);

                corrente.forEach((k, v) -> {
                    RegionalDailyData regioneCorrente = v;
                    RegionalDailyData regionePrecedente = precedente.get(k);

                    List<Integer> listInfectionLastWeek = mapInfectionLastWeek.computeIfAbsent(k,
                            key -> new ArrayList<>());

                    listInfectionLastWeek.add(regioneCorrente.getNewInfections());

                    RegionalDailyData dataToStore = calculateRegionalDailyDataDelta(regioneCorrente, regionePrecedente,
                            listInfectionLastWeek.get(0));

                    if (listInfectionLastWeek.size() > 7) {
                        listInfectionLastWeek.remove(0);
                    }

                    dataService.saveRegionalDailyData(dataToStore);
                });

            }
        } catch (Exception e) {
            _LOGGER.error(e.getMessage());
        }

    }

    @Override
    public LocalDate getStartDate() {
        return dataService.getRegionMaxDateAvailable();
    }

    /**
     * It loads the new infections data of the last week
     * 
     * @param end
     * @return
     */
    private Map<String, List<Integer>> getRegionalLastWeeknNewInfection(LocalDate end) {
        List<RegionalDailyData> list = dataService.getRegionalDatesInRangeAscending(end.minusDays(7), end);
        Map<String, List<Integer>> mapInfectionLastWeek = new HashMap<>();

        list.stream().forEach(rdd -> mapInfectionLastWeek.computeIfAbsent(rdd.getRegionCode(), k -> new ArrayList<>())
                .add(rdd.getNewInfections()));

        return mapInfectionLastWeek;
    }

    /**
     * It parses the Regional Daily data CSV
     * 
     * @param date
     * @return
     */
    private Map<String, RegionalDailyData> parseRegionData(LocalDate date) {
        Map<String, RegionalDailyData> dataMap = new HashMap<>();

        /*
         * Inserting the date into the URL
         */
        String uriFile = String.format(url, date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        List<String> listRows = getCsvRows(uriFile);

        // @formatter:off
        listRows.stream().map(s -> {
            List<String> columns = Arrays.asList(s.split(","));
            RegionalDailyData data = new RegionalDailyData();
            
            if(columns.size() < 2) {
                return data;
            }
            
            data.setDate(date);
            data.setRegionCode(columns.get(2));
            data.setNewCasualties(Integer.parseInt(columns.get(14)));
            data.setNewTests(Integer.parseInt(columns.get(18)));
            data.setNewInfections(Integer.parseInt(columns.get(12)));
            data.setNewHospitalized(Integer.parseInt(columns.get(8)));
            data.setNewIntensiveTherapy(Integer.parseInt(columns.get(7)));
            data.setNewRecovered(Integer.parseInt(columns.get(13)));
            return data;
            })
            .filter(d -> d.getDate() != null)
            .forEach(e -> dataMap.put(e.getRegionCode() , e));
        // @formatter:on

        return dataMap;
    }

    /**
     * It calculates the regional daily data
     * 
     * @param current
     * @param previous
     * @return
     */
    private RegionalDailyData calculateRegionalDailyDataDelta(RegionalDailyData current, RegionalDailyData previous,
            int newInfectionOneWeekAgo) {
        RegionalDailyData newDailyData = new RegionalDailyData();
        newDailyData.setDate(current.getDate());
        newDailyData.setRegionCode(current.getRegionCode());
        newDailyData.setNewInfections(current.getNewInfections());

        float percTmp = infectedPercentage(current.getNewTests(), previous.getNewTests(), current.getNewInfections());
        BigDecimal infectionPercentage = Float.isInfinite(percTmp) ? BigDecimal.ZERO
                : BigDecimal.valueOf(percTmp).setScale(2, RoundingMode.DOWN);

        newDailyData.setInfectionPercentage(infectionPercentage);
        newDailyData.setNewCasualties(current.getNewCasualties() - previous.getNewCasualties());
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

        if (newDailyData.getCasualtiesPercentage().compareTo(BigDecimal.ZERO) < 0) {
            newDailyData.setCasualtiesPercentage(BigDecimal.ZERO);
        }

        /*
         * Do some math
         */
        percTmp = casualtiesPercentage(newDailyData.getNewCasualties(), newInfectionOneWeekAgo);
        BigDecimal casualtiesPercentage = Float.isInfinite(percTmp) ? BigDecimal.ZERO
                : BigDecimal.valueOf(percTmp).setScale(2, RoundingMode.DOWN);
        newDailyData.setCasualtiesPercentage(casualtiesPercentage);

        if (newDailyData.getCasualtiesPercentage().compareTo(BigDecimal.ZERO) < 0) {
            newDailyData.setCasualtiesPercentage(BigDecimal.ZERO);
        }

        return newDailyData;
    }

}
