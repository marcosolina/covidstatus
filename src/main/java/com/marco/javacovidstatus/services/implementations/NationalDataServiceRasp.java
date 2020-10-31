package com.marco.javacovidstatus.services.implementations;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marco.javacovidstatus.model.DailyData;
import com.marco.javacovidstatus.services.interfaces.NationalDataService;

public class NationalDataServiceRasp implements NationalDataService{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NationalDataServiceRasp.class);
    
    private static final List<DailyData> data = new ArrayList<>();

    @Override
    public List<DailyData> getAllDataDescending() {
        LOGGER.trace("NationalDataServiceRasp.getAllDataDescending");
        data.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()) * -1);
        return data;
    }

    @Override
    public List<DailyData> getAllDataAscending() {
        LOGGER.trace("NationalDataServiceRasp.getAllDataAscending");
        data.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
        return data;
    }

    @Override
    public boolean storeData(DailyData dto) {
        LOGGER.trace("NationalDataServiceRasp.storeData");
        return data.add(dto);
    }

    @Override
    public boolean deleteAllData() {
        LOGGER.trace("NationalDataServiceRasp.deleteAllData");
        data.clear();
        return true;
    }

}
