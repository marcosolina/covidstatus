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
        if(start == null) {
            start = LocalDate.of(2020, 2, 24);
        }
        
        loadRegionalData(start, end);
        loadNationalData(start, end);
        loadProvinceData(start, end);
        notificationService.sendMessage("marcosolina@gmail.com", "Marco Solina - Covid Status", "Dati aggiornati");
    }
    
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
                    logger.debug(String.format("REgion: %s", k));    
                    float percTmp = percentualeInfetti(regioneCorrente.getNewTests(), regionePrecedente.getNewTests(), regioneCorrente.getNewInfections());
                    BigDecimal infectionPercentage = Float.isInfinite(percTmp) ? BigDecimal.ZERO : BigDecimal.valueOf(percTmp).setScale(2, RoundingMode.DOWN);
                    
                    regioneCorrente.setInfectionPercentage(infectionPercentage);
                    regioneCorrente.setNewCasualties(regioneCorrente.getNewCasualties() - regionePrecedente.getNewCasualties());
                    regioneCorrente.setNewTests(regioneCorrente.getNewTests() - regionePrecedente.getNewTests());
                    regioneCorrente.setNewHospitalized(regioneCorrente.getNewHospitalized() - regionePrecedente.getNewHospitalized());
                    regioneCorrente.setNewIntensiveTherapy(regioneCorrente.getNewIntensiveTherapy() - regionePrecedente.getNewIntensiveTherapy());
                    regioneCorrente.setNewRecovered(regioneCorrente.getNewRecovered() - regionePrecedente.getNewRecovered());
                    
                    if (regioneCorrente.getNewIntensiveTherapy() < 0) {
                        regioneCorrente.setNewIntensiveTherapy(0);
                    }
                    if (regioneCorrente.getNewHospitalized() < 0) {
                        regioneCorrente.setNewHospitalized(0);
                    }
                    if (regioneCorrente.getNewRecovered() < 0) {
                        regioneCorrente.setNewRecovered(0);
                    }
                    if (regioneCorrente.getNewCasualties() < 0) {
                        regioneCorrente.setNewCasualties(0);
                    }
                    
                    if (regioneCorrente.getCasualtiesPercentage().compareTo(BigDecimal.ZERO) < 0) {
                        regioneCorrente.setCasualtiesPercentage(BigDecimal.ZERO);
                    }
                    //TODO calcolare la % dei decessi
                    
                    
                    dataService.saveRegionalDailyData(regioneCorrente);
                });
                
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void loadNationalData(LocalDate start, LocalDate end) {
        List<Integer> lastWeeknNewInfection = getLastWeeknNewInfection(end);
        logger.info("Updating national data");
        try {
            while (start.isBefore(end)) {
                start = start.plusDays(1);
                logger.debug(String.format("Looking for national data at date: %s", start.toString()));
                
                /*
                 * Retrieve the data of today and yesterday
                 */
                ItalianNationalData precedente = getNationalDataAtDate(start.minusDays(1));
                ItalianNationalData corrente = getNationalDataAtDate(start);

                /*
                 * Do some math
                 */
                BigDecimal infectionPercentage = BigDecimal.valueOf(percentualeInfetti(corrente.newTests, precedente.newTests, corrente.newInfections))
                        .setScale(2, RoundingMode.DOWN);

                /*
                 * Store the data
                 */
                NationalDailyData dto = new NationalDailyData();
                dto.setDate(start);
                dto.setInfectionPercentage(infectionPercentage);
                dto.setNewCasualties(corrente.newCasualties - precedente.newCasualties);
                dto.setNewInfections(corrente.newInfections);
                dto.setNewTests(corrente.newTests - precedente.newTests);
                dto.setNewHospitalized(corrente.newHospitalized - precedente.newHospitalized);
                dto.setNewIntensiveTherapy(corrente.newIntensiveTherapy - precedente.newIntensiveTherapy);
                dto.setNewRecovered(corrente.newRecovered - precedente.newRecovered);

                if (dto.getNewIntensiveTherapy() < 0) {
                    dto.setNewIntensiveTherapy(0);
                }
                if (dto.getNewHospitalized() < 0) {
                    dto.setNewHospitalized(0);
                }
                if (dto.getNewRecovered() < 0) {
                    dto.setNewRecovered(0);
                }
                if (dto.getNewCasualties() < 0) {
                    dto.setNewCasualties(0);
                }

                lastWeeknNewInfection.add(dto.getNewInfections());

                /*
                 * Do some math
                 */
                BigDecimal casualtiesPercentage = BigDecimal
                        .valueOf(percentualeMorti(dto.getNewCasualties(), lastWeeknNewInfection.get(0)))
                        .setScale(2, RoundingMode.DOWN);
                dto.setCasualtiesPercentage(casualtiesPercentage);

                if (dto.getCasualtiesPercentage().compareTo(BigDecimal.ZERO) < 0) {
                    dto.setCasualtiesPercentage(BigDecimal.ZERO);
                }

                if (lastWeeknNewInfection.size() > 7) {
                    lastWeeknNewInfection.remove(0);
                }

                /*
                 * Store the info
                 */
                dataService.storeData(dto);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
    
    private List<Integer> getLastWeeknNewInfection(LocalDate end){
        List<NationalDailyData> list = dataService.getDatesInRangeAscending(end.minusDays(7), end);
        return list.stream().map(NationalDailyData::getNewInfections).collect(Collectors.toList());
    }

    private void loadProvinceData(LocalDate start, LocalDate end) {
        logger.info("Updating province data");
        try {
            while (start.isBefore(end)) {
                start = start.plusDays(1);

                logger.debug(String.format("Looking for province data at date: %s", start.toString()));
                
                Map<String, ProvinceDailyData> precedente = parseProvinceData(start.minusDays(1));
                Map<String, ProvinceDailyData> corrente = parseProvinceData(start);

                List<ProvinceDailyData> dataToStore = new ArrayList<>();

                corrente.forEach((k, v) -> dataToStore.add(getDelta(v, precedente.get(k))));
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

    private ProvinceDailyData getDelta(ProvinceDailyData current, ProvinceDailyData previous) {
        current.setNewInfections(current.getNewInfections() - previous.getNewInfections());
        if (current.getNewInfections() < 0) {
            current.setNewInfections(0);
        }
        return current;
    }
    
    private Map<String, RegionalDailyData> parseRegionData(LocalDate date) {
        Map<String, RegionalDailyData> dataMap = new HashMap<String, RegionalDailyData>();

        /*
         * Inserting the date into the URL
         */
        String uriFile = String.format(urlRegions, date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        /*
         * Get the CSV file using an GET HTTP call
         */
        ClientResponse response = webClient.get().uri(uriFile).exchange().block();

        /*
         * Read the response as a string
         */
        String csv = response.bodyToMono(String.class).block();
        List<String> listRows = new ArrayList<>(Arrays.asList(csv.split("\\n")));
        listRows.remove(0);// remove column names

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

    private Map<String, ProvinceDailyData> parseProvinceData(LocalDate date) {
        Map<String, ProvinceDailyData> dataMap = new HashMap<String, ProvinceDailyData>();

        /*
         * Inserting the date into the URL
         */
        String uriFile = String.format(urlProvince, date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        /*
         * Get the CSV file using an GET HTTP call
         */
        ClientResponse response = webClient.get().uri(uriFile).exchange().block();

        /*
         * Read the response as a string
         */
        String csv = response.bodyToMono(String.class).block();
        List<String> listRows = new ArrayList<>(Arrays.asList(csv.split("\\n")));
        listRows.remove(0);// remove column names

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

    private ItalianNationalData getNationalDataAtDate(LocalDate date) {
        logger.trace("Inside CronServiceGit.getNationalDataAtDate");

        /*
         * Inserting the date into the URL
         */
        String uriFile = String.format(url, date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        /*
         * Get the CSV file using an GET HTTP call
         */
        ClientResponse response = webClient.get().uri(uriFile).exchange().block();

        /*
         * Read the response as a string
         */
        String csv = response.bodyToMono(String.class).block();

        /*
         * Parsing the string
         */
        String[] tmp = csv.split("\\n");
        String[] values = tmp[1].split(",");

        ItalianNationalData dati = new ItalianNationalData();
        dati.newCasualties = Integer.parseInt(values[10]);
        dati.newTests = Integer.parseInt(values[14]);
        dati.newInfections = Integer.parseInt(values[8]);
        dati.newHospitalized = Integer.parseInt(values[4]);
        dati.newIntensiveTherapy = Integer.parseInt(values[3]);
        dati.newRecovered = Integer.parseInt(values[9]);

        return dati;
    }

    private float percentualeInfetti(int newTestsCorrente, int newTestsPrecedente, int newInfectionsCorrent) {
        if(newInfectionsCorrent == 0) {
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

    private class ItalianNationalData {
        int newInfections;
        int newTests;
        int newCasualties;
        int newHospitalized;
        int newIntensiveTherapy;
        int newRecovered;
    }

}
