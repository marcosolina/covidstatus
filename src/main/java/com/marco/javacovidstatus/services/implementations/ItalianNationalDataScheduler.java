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
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.model.NationalDailyData;
import com.marco.javacovidstatus.model.ProvinceDailyData;
import com.marco.javacovidstatus.model.RegionalDailyData;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;
import com.marco.javacovidstatus.services.interfaces.GovermentDataRetrieverScheduler;
import com.marco.javacovidstatus.services.interfaces.NotificationSenderInterface;

/**
 * This implementations uses the Italian national data
 * 
 * @author Marco
 *
 */
@Component
@EnableScheduling
public class ItalianNationalDataScheduler implements GovermentDataRetrieverScheduler {
    private static final Logger logger = LoggerFactory.getLogger(ItalianNationalDataScheduler.class);

    @Autowired
    private WebClient webClient;
    @Autowired
    private CovidDataService dataService;
    @Autowired
    private NotificationSenderInterface notificationService;

    private String url = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-andamento-nazionale/dpc-covid19-ita-andamento-nazionale-%s.csv";
    private String urlProvince = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-province/dpc-covid19-ita-province-%s.csv";
    private String urlRegions = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-regioni/dpc-covid19-ita-regioni-%s.csv";

    @Scheduled(cron = "0 0 * * * *")
    @Override
    public synchronized void updateNationalData() {

        logger.info("Updating Data");

        LocalDate end = LocalDate.now();
        LocalDate start = dataService.getMaxDate();
        if (start == null) {
            start = LocalDate.of(2020, 2, 24);
        }

        loadRegionalData(start, end);
        loadNationalData(start, end);
        loadProvinceData(start, end);
        notificationService.sendMessage("marcosolina@gmail.com", "Marco Solina - Covid Status", "Dati aggiornati");
    }

    /**
     * It retrieves the Regional data from the public repository
     * 
     * @param start
     * @param end
     */
    private void loadRegionalData(LocalDate start, LocalDate end) {
        logger.info("Updating regional data");
        try {
            while (start.isBefore(end)) {
                start = start.plusDays(1);
                logger.debug(String.format("Looking for regional data at date: %s", start.toString()));

                /*
                 * Retrieve the data from the repository
                 */
                Map<String, RegionalDailyData> precedente = parseRegionData(start.minusDays(1));
                Map<String, RegionalDailyData> corrente = parseRegionData(start);

                corrente.forEach((k, v) -> {
                    RegionalDailyData regioneCorrente = v;
                    RegionalDailyData regionePrecedente = precedente.get(k);
                    dataService
                            .saveRegionalDailyData(calculateRegionalDailyDataDelta(regioneCorrente, regionePrecedente));
                });

            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * It retrieves the Regional data from the public repository
     * 
     * @param start
     * @param end
     */
    private void loadNationalData(LocalDate start, LocalDate end) {
        List<Integer> lastWeeknNewInfection = getLastWeeknNewInfection(end);
        logger.info("Updating national data");
        try {
            while (start.isBefore(end)) {
                start = start.plusDays(1);
                logger.debug(String.format("Looking for national data at date: %s", start.toString()));

                /*
                 * Retrieve the data
                 */
                NationalDailyData precedente = getNationalDataAtDate(start.minusDays(1));
                NationalDailyData corrente = getNationalDataAtDate(start);

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
            logger.error(e.getMessage());
        }
    }

    /**
     * It returns the number of the new infections in the last week
     * 
     * @param end
     * @return
     */
    private List<Integer> getLastWeeknNewInfection(LocalDate end) {
        List<NationalDailyData> list = dataService.getDatesInRangeAscending(end.minusDays(7), end);
        return list.stream().map(NationalDailyData::getNewInfections).collect(Collectors.toList());
    }

    /**
     * It process the province data between the dates
     * 
     * @param start
     * @param end
     */
    private void loadProvinceData(LocalDate start, LocalDate end) {
        logger.info("Updating province data");
        try {
            while (start.isBefore(end)) {
                start = start.plusDays(1);

                logger.debug(String.format("Looking for province data at date: %s", start.toString()));

                Map<String, ProvinceDailyData> precedente = parseProvinceData(start.minusDays(1));
                Map<String, ProvinceDailyData> corrente = parseProvinceData(start);

                List<ProvinceDailyData> dataToStore = new ArrayList<>();

                corrente.forEach((k, v) -> dataToStore.add(calculateProvinceDailyDelta(v, precedente.get(k))));
                // @formatter:off
                dataToStore.parallelStream().map(d -> {
                    /*
                     * I had to fix an issue caused by data inconsistency.
                     * From Feb to Jun these to province were in the same region,
                     * but in July they were  assigned to two separate regions.
                     * I force the separate assignment from the beginning so it is consistent
                     */
                    if (d.getRegionDesc().equals("P.A. Bolzano")) {
                        d.setRegionCode("21");
                    } else if (d.getRegionDesc().equals("P.A. Trento")) {
                        d.setRegionCode("22");
                    }
                    return d;
                }).forEach(dataService::storeProvinceDailyData);
                // @formatter:on
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * It calculates the delta values for the province model
     * 
     * @param current
     * @param previous
     * @return
     */
    private ProvinceDailyData calculateProvinceDailyDelta(ProvinceDailyData current, ProvinceDailyData previous) {
        current.setNewInfections(current.getNewInfections() - previous.getNewInfections());
        if (current.getNewInfections() < 0) {
            current.setNewInfections(0);
        }
        return current;
    }

    /**
     * It calculates the nationa daily data
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
                .valueOf(percentualeInfetti(current.getNewTests(), previous.getNewTests(), current.getNewInfections()))
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
                .valueOf(percentualeMorti(newDailyData.getNewCasualties(), newInfectionOneWeekAgo))
                .setScale(2, RoundingMode.DOWN);
        newDailyData.setCasualtiesPercentage(casualtiesPercentage);

        if (newDailyData.getCasualtiesPercentage().compareTo(BigDecimal.ZERO) < 0) {
            newDailyData.setCasualtiesPercentage(BigDecimal.ZERO);
        }

        return newDailyData;
    }

    /**
     * It calculates the regional daily data
     * 
     * @param current
     * @param previous
     * @return
     */
    private RegionalDailyData calculateRegionalDailyDataDelta(RegionalDailyData current, RegionalDailyData previous) {
        RegionalDailyData newDailyData = new RegionalDailyData();
        newDailyData.setDate(current.getDate());
        newDailyData.setRegionCode(current.getRegionCode());
        newDailyData.setNewInfections(current.getNewInfections());

        float percTmp = percentualeInfetti(current.getNewTests(), previous.getNewTests(), current.getNewInfections());
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

        // TODO calcolare la % dei decessi

        return newDailyData;
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
        String uriFile = String.format(urlRegions, date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

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

    /*
     * It returns the list of row available in the CSV file. It remove the first row
     * as it contains the column labels
     */
    private List<String> getCsvRows(String url) {
        /*
         * Get the CSV file using an GET HTTP call
         */
        ClientResponse response = webClient.get().uri(url).exchange().block();

        /*
         * Read the response as a string
         */
        String csv = response.bodyToMono(String.class).block();
        List<String> listRows = new ArrayList<>(Arrays.asList(csv.split("\\n")));
        listRows.remove(0);// remove column names

        return listRows;
    }

    /**
     * It parses the Province daily CSV
     * 
     * @param date
     * @return
     */
    private Map<String, ProvinceDailyData> parseProvinceData(LocalDate date) {
        Map<String, ProvinceDailyData> dataMap = new HashMap<>();
        String uriFile = String.format(urlProvince, date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        List<String> listRows = getCsvRows(uriFile);

        // @formatter:off
        listRows.stream().map(s -> {
            List<String> columns = Arrays.asList(s.split(","));
            ProvinceDailyData data = new ProvinceDailyData();
            data.setDate(date);
            data.setProvinceCode(columns.get(4));
            data.setRegionCode(columns.get(2));
            
            data.setRegionDesc(columns.get(3));
            data.setDescription(columns.get(5));
            data.setNewInfections(Integer.parseInt(columns.get(9)));
            data.setShortName(columns.get(6));
            return data;
            })
            .filter(e -> !e.getShortName().trim().isEmpty())
            .forEach(e -> dataMap.put(e.getProvinceCode() , e));
        // @formatter:on

        return dataMap;
    }

    /**
     * It parses the National daily CSV
     * 
     * @param date
     * @return
     */
    private NationalDailyData getNationalDataAtDate(LocalDate date) {
        logger.trace("Inside CronServiceGit.getNationalDataAtDate");

        /*
         * Inserting the date into the URL
         */
        String uriFile = String.format(url, date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        List<String> listRows = getCsvRows(uriFile);
        List<String> columns = Arrays.asList(listRows.get(0).split(","));

        NationalDailyData data = new NationalDailyData();
        data.setNewCasualties(Integer.parseInt(columns.get(10)));
        data.setNewTests(Integer.parseInt(columns.get(14)));
        data.setNewInfections(Integer.parseInt(columns.get(8)));
        data.setNewHospitalized(Integer.parseInt(columns.get(4)));
        data.setNewIntensiveTherapy(Integer.parseInt(columns.get(3)));
        data.setNewRecovered(Integer.parseInt(columns.get(9)));

        return data;
    }

    /**
     * It caluclates the percentage of new infected people
     * 
     * @param newTestsCorrente
     * @param newTestsPrecedente
     * @param newInfectionsCorrent
     * @return
     */
    private float percentualeInfetti(int newTestsCorrente, int newTestsPrecedente, int newInfectionsCorrent) {
        if (newInfectionsCorrent == 0) {
            return 0;
        }
        int deltaTests = newTestsCorrente - newTestsPrecedente;
        int deltaInfections = newInfectionsCorrent;
        return ((float) deltaInfections / deltaTests) * 100;
    }

    /**
     * @see <a href=
     *      "https://www.focus.it/scienza/salute/tasso-di-mortalita-covid-19">Algorithm</a>
     * @param newCasualties
     * @param newCasesFrom7DaysAgo
     * @return
     */
    private float percentualeMorti(int newCasualties, int newCasesFrom7DaysAgo) {
        return ((float) newCasualties / newCasesFrom7DaysAgo) * 100;
    }

}
