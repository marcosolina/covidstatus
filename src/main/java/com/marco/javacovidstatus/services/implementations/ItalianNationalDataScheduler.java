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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.model.DailyData;
import com.marco.javacovidstatus.model.ProvinceDailyData;
import com.marco.javacovidstatus.services.interfaces.GovermentDataRetrieverScheduler;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;
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

    @Scheduled(cron = "0 0 * * * *")
    @Override
    public void updateNationalData() {

        logger.info("Updating Data");
        dataService.deleteAllData();// TODO optimise

        loadProvinceData();
        loadNationalData();
        notificationService.sendMessage("marcosolina@gmail.com", "Marco Solina - Covid Status", "Dati aggiornati");
    }

    private void loadNationalData() {
        LocalDate start = LocalDate.of(2020, 2, 24);
        LocalDate end = LocalDate.now();
        List<Integer> lastWeeknNewInfection = new ArrayList<>();

        try {
            while (start.isBefore(end)) {
                start = start.plusDays(1);
                /*
                 * Retrieve the data of today and yesterday
                 */
                ItalianNationalData precedente = getDatiAllaData(start.minusDays(1));
                ItalianNationalData corrente = getDatiAllaData(start);

                /*
                 * Do some math
                 */
                BigDecimal infectionPercentage = BigDecimal.valueOf(percentualeInfetti(corrente, precedente))
                        .setScale(2, RoundingMode.DOWN);

                /*
                 * Store the data
                 */
                DailyData dto = new DailyData();
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
                logger.debug(String.format("Inserted data for date: %s", start.toString()));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void loadProvinceData() {
        LocalDate start = LocalDate.of(2020, 2, 24);
        LocalDate end = LocalDate.now();
        try {
            while (start.isBefore(end)) {
                start = start.plusDays(1);

                Map<String, ProvinceDailyData> precedente = parseData(start.minusDays(1));
                Map<String, ProvinceDailyData> corrente = parseData(start);

                List<ProvinceDailyData> dataToStore = new ArrayList<>();

                corrente.forEach((k, v) -> dataToStore.add(getDelta(v, precedente.get(k))));
                dataToStore.parallelStream().forEach(dataService::storeProvinceDailyData);
                logger.debug(String.format("Inserted Province data for date: %s", start.toString()));
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

    private Map<String, ProvinceDailyData> parseData(LocalDate date) {
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

    private ItalianNationalData getDatiAllaData(LocalDate date) {
        logger.trace("Inside CronServiceGit.getDatiAllaData");

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

    private float percentualeInfetti(ItalianNationalData corrente, ItalianNationalData precedente) {
        int deltaTests = corrente.newTests - precedente.newTests;
        int deltaInfections = corrente.newInfections;
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
