package com.marco.javacovidstatus.services.implementations;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.services.interfaces.CovidDataDownloader;

/**
 * This class will download the details about the Italian population. The data
 * are retrieved using the ISTA web services
 * 
 * @author Marco
 *
 */
public class PopulationDownloader extends CovidDataDownloader {
    private static final Logger _LOGGER = LoggerFactory.getLogger(PopulationDownloader.class);

    public PopulationDownloader(WebClient webClient) {
        super(webClient);
    }

    @Override
    public void downloadData() {

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/vnd.sdmx.data+csv;version=1.0.0");
        
        StringBuilder sb2 = new StringBuilder();
        for(int i = 0; i < 100; i++) {
            sb2.append(String.format("Y%d+", + i));
        }
        
        String url = String.format("http://sdmx.istat.it/SDMXWS/rest/data/22_289/.%sY_GE100.IT.1+2.99.JAN/", sb2.toString());
        _LOGGER.debug(url);
    }

    @Override
    public LocalDate getStartDate() {
        // TODO Auto-generated method stub
        return null;
    }

}
