package com.marco.javacovidstatus.services.implementations;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.marco.javacovidstatus.model.DailyData;
import com.marco.javacovidstatus.repositories.model.EntityNationalData;
import com.marco.javacovidstatus.repositories.sql.NationallDataSqlRepository;
import com.marco.javacovidstatus.services.interfaces.NationalDataService;

public class MarcoNationalDataService implements NationalDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarcoNationalDataService.class);

    @Autowired
    private NationallDataSqlRepository repo;

    @Override
    public List<DailyData> getAllDataDescending() {
        LOGGER.debug("Reading data from the repository");
        List<EntityNationalData> listEntity = repo.findAllByOrderByDateDesc();
        return listEntity.stream().map(this::fromEntityNationalDataToDailyData).collect(Collectors.toList());
    }

    @Override
    public List<DailyData> getAllDataAscending() {
        LOGGER.debug("Reading data from the repository");
        List<EntityNationalData> listEntity = repo.findAllByOrderByDateAsc();
        return listEntity.stream().map(this::fromEntityNationalDataToDailyData).collect(Collectors.toList());
    }

    @Override
    public boolean storeData(DailyData dto) {
        repo.save(fromDailyData(dto));
        return true;
    }

    @Override
    public boolean deleteAllData() {
        repo.deleteAll();
        return true;
    }

    private DailyData fromEntityNationalDataToDailyData(EntityNationalData entity) {
        DailyData dailyData = new DailyData();
        dailyData.setDate(entity.getDate());
        dailyData.setInfectionPercentage(entity.getInfectionPercentage());
        dailyData.setNewCasualties(entity.getNewCasualties());
        dailyData.setNewInfections(entity.getNewInfections());
        dailyData.setNewTests(entity.getNewTests());
        dailyData.setCaualtiesPercentage(entity.getCaualtiesPercentage());
        return dailyData;
    }
    
    private EntityNationalData fromDailyData(DailyData data) {
        EntityNationalData entity = new EntityNationalData();
        entity.setDate(data.getDate());
        entity.setInfectionPercentage(data.getInfectionPercentage());
        entity.setNewCasualties(data.getNewCasualties());
        entity.setNewInfections(data.getNewInfections());
        entity.setNewTests(data.getNewTests());
        entity.setCasualtiesPercentage(data.getCasualtiesPercentage());
        return entity;
    }

}
