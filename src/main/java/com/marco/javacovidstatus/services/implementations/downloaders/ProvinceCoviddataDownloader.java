package com.marco.javacovidstatus.services.implementations.downloaders;

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

import com.marco.javacovidstatus.model.dto.ProvinceDailyDataDto;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;
import com.marco.javacovidstatus.services.interfaces.downloaders.CovidDataDownloader;

/**
 * It downloads and process the Provinces data
 * 
 * @author Marco
 *
 */
public class ProvinceCoviddataDownloader extends CovidDataDownloader {

    private static final Logger _LOGGER = LoggerFactory.getLogger(ProvinceCoviddataDownloader.class);

    @Autowired
    private CovidDataService dataService;
    private String url = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-province/dpc-covid19-ita-province-%s.csv";
    
    private static final int COL_PROVINCE_CODE = 4;
    private static final int COL_REGION_CODE = 2;
    private static final int COL_REGION_DESC = 3;
    private static final int COL_DESC = 5;
    private static final int COL_NEW_INFECTIONS = 9;
    private static final int COL_SHORT_NAME = 6;
    
    public ProvinceCoviddataDownloader(WebClient webClient) {
        super(webClient);
    }

    @Override
    public boolean downloadData() {
        _LOGGER.info("Downloading Province Data");

        LocalDate end = LocalDate.now();
        LocalDate start = getStartDate();
        if (start == null) {
            start = this.defaultStartData;
        }
        
        boolean error = false;

        try {
            while (start.isBefore(end)) {
                start = start.plusDays(1);

                if (_LOGGER.isDebugEnabled()) {
                    _LOGGER.debug(String.format("Looking for province data at date: %s", start.toString()));
                }

                Map<String, ProvinceDailyDataDto> precedente = parseProvinceData(start.minusDays(1));
                Map<String, ProvinceDailyDataDto> corrente = parseProvinceData(start);

                List<ProvinceDailyDataDto> dataToStore = new ArrayList<>();

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
                }).map(dto -> {
                	if (dto.getNewInfections() < 0) {
                		dto.setNewInfections(0);
                	}
                	return dto;
                }).forEach(dataService::storeProvinceDailyData);
                // @formatter:on
            }
        } catch (Exception e) {
            _LOGGER.error(e.getMessage());
            error = true;
        }
        
        return !error;
    }

    @Override
    public LocalDate getStartDate() {
        return dataService.getLastDateOfAvailableProvinceData();
    }

    /**
     * It parses the Province daily CSV
     * 
     * @param date
     * @return
     */
    private Map<String, ProvinceDailyDataDto> parseProvinceData(LocalDate date) {
        Map<String, ProvinceDailyDataDto> dataMap = new HashMap<>();
        String uriFile = String.format(url, date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        List<String> listRows = getCsvRows(uriFile);

        // @formatter:off
        listRows.stream().map(s -> {
            List<String> columns = Arrays.asList(s.split(","));
            ProvinceDailyDataDto data = new ProvinceDailyDataDto();
            data.setDate(date);
            data.setProvinceCode(columns.get(COL_PROVINCE_CODE));
            data.setRegionCode(columns.get(COL_REGION_CODE));
            
            data.setRegionDesc(columns.get(COL_REGION_DESC));
            data.setDescription(columns.get(COL_DESC));
            data.setNewInfections(Integer.parseInt(columns.get(COL_NEW_INFECTIONS)));
            data.setShortName(columns.get(COL_SHORT_NAME));
            return data;
            })
            .filter(e -> !e.getShortName().trim().isEmpty())
            .forEach(e -> dataMap.put(e.getProvinceCode() , e));
        // @formatter:on

        return dataMap;
    }

    /**
     * It calculates the delta values for the province model
     * 
     * @param current
     * @param previous
     * @return
     */
    private ProvinceDailyDataDto calculateProvinceDailyDelta(ProvinceDailyDataDto current, ProvinceDailyDataDto previous) {
        current.setNewInfections(current.getNewInfections() - previous.getNewInfections());
        if (current.getNewInfections() < 0) {
            current.setNewInfections(0);
        }
        return current;
    }

}
