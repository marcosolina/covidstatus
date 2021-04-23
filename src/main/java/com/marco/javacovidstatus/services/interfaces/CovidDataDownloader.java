package com.marco.javacovidstatus.services.interfaces;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Abstract class to provide a common set of functionalities to download and
 * process the data from the Ministry of Health repository
 * 
 * @see <a href="https://github.com/pcm-dpc/COVID-19">Ministry of Health
 *      repository</a>
 * @author Marco
 *
 */
public abstract class CovidDataDownloader {

    private WebClient webClient;
    protected final LocalDate defaultStartData = LocalDate.of(2020, 2, 24);

    public CovidDataDownloader(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * It downloads the data
     */
    public abstract void downloadData();

    /**
     * It returns the start date from when to start to download the data. This date
     * should exclude the dates already download
     * 
     * @return
     */
    public abstract LocalDate getStartDate();

    /**
     * It calculates the percentage of the casualties. For the algorithm info kindly
     * refer to the link
     * 
     * @see <a href=
     *      "https://www.focus.it/scienza/salute/tasso-di-mortalita-covid-19">Algorithm</a>
     * @param newCasualties
     * @param newCasesFrom7DaysAgo
     * @return
     */
    protected float casualtiesPercentage(int newCasualties, int newCasesFrom7DaysAgo) {
        if (newCasesFrom7DaysAgo == 0) {
            return 0;
        }
        return ((float) newCasualties / newCasesFrom7DaysAgo) * 100;
    }

    /**
     * It calculates the percentage of new infected people
     * 
     * @param newTestsCorrente
     * @param newTestsPrecedente
     * @param newInfectionsCorrent
     * @return
     */
    protected float infectedPercentage(int todayNewTests, int previousNewTests, int todayNewInfections) {
        if (todayNewInfections == 0) {
            return 0;
        }
        int deltaTests = todayNewTests - previousNewTests;
        int deltaInfections = todayNewInfections;
        return ((float) deltaInfections / deltaTests) * 100;
    }

    /**
     * It connects to the Ministry of Health Repository, reads the CSV file and it
     * returns the list of rows included in the file. It will skip the columns
     * labels row
     * 
     * @param url
     * @return
     */
    protected List<String> getCsvRows(String url) {
        return getCsvRows(url, null);
    }

    protected List<String> getCsvRows(String url, Map<String, String> headers) {
        /*
         * Get the CSV file using an GET HTTP call
         */
        ClientResponse response = null;
        if (headers == null) {
            response = webClient.get().uri(url).exchange().block();
        } else {
            response = webClient.get().uri(url).headers(httpHeaders -> headers.forEach(httpHeaders::set)).exchange().block();
        }

        /*
         * Read the response as a string
         */
        String csv = response.bodyToMono(String.class).block();
        List<String> listRows = new ArrayList<>(Arrays.asList(csv.split("\\n")));
        if (!listRows.isEmpty()) {
            listRows.remove(0);// remove column names
        }

        return listRows;
    }

}
