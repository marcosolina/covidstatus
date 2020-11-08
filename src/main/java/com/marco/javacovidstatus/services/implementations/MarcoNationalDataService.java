package com.marco.javacovidstatus.services.implementations;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.marco.javacovidstatus.model.DailyData;
import com.marco.javacovidstatus.model.ProvinceDailyData;
import com.marco.javacovidstatus.model.Region;
import com.marco.javacovidstatus.repositories.model.EntityNationalData;
import com.marco.javacovidstatus.repositories.model.EntityProvinceData;
import com.marco.javacovidstatus.repositories.model.EntityProvinceDataPk;
import com.marco.javacovidstatus.repositories.sql.CovidRepository;
import com.marco.javacovidstatus.repositories.sql.EntityProvinceDataRepo;
import com.marco.javacovidstatus.repositories.sql.NationallDataSqlRepository;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;

public class MarcoNationalDataService implements CovidDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarcoNationalDataService.class);

    @Autowired
    private NationallDataSqlRepository repoNationalData;
    @Autowired
    private EntityProvinceDataRepo repoProvince;
    @Autowired
    private CovidRepository repoCovidCustom;

    @Override
    public List<DailyData> getAllDataDescending() {
        LOGGER.trace("Reading data from the repository");
        List<EntityNationalData> listEntity = repoNationalData.findAllByOrderByDateDesc();
        return listEntity.stream().map(this::fromEntityNationalDataToDailyData).collect(Collectors.toList());
    }

    @Override
    public List<DailyData> getAllDataAscending() {
        LOGGER.trace("Reading data from the repository");
        List<EntityNationalData> listEntity = repoNationalData.findAllByOrderByDateAsc();
        return listEntity.stream().map(this::fromEntityNationalDataToDailyData).collect(Collectors.toList());
    }

    @Override
    public boolean storeData(DailyData dto) {
        repoNationalData.save(fromDailyData(dto));
        return true;
    }

    @Override
    public boolean deleteAllData() {
        repoNationalData.deleteAll();
        repoProvince.deleteAll();
        return true;
    }

    @Override
    public List<DailyData> getDatesInRangeAscending(LocalDate from, LocalDate to) {
        List<EntityNationalData> listEntity = repoNationalData.findByDateBetweenOrderByDateAsc(from, to);
        return listEntity.stream().map(this::fromEntityNationalDataToDailyData).collect(Collectors.toList());
    }

    @Override
    public boolean storeProvinceDailyData(ProvinceDailyData data) {
        repoProvince.save(fromProvinceDailyDataToEntityProvinceData(data));
        return true;
    }

    @Override
    public List<Region> getRegionsList() {
        return repoCovidCustom.getRegionList();
    }

    @Override
    public List<ProvinceDailyData> getProvinceDataInRangeAscending(LocalDate from, LocalDate to, String regionCode) {
        List<EntityProvinceData> list = repoCovidCustom.getProvinceDataBetweenDatesOrderByDateAscending(from, to, regionCode);
        return list.stream().map(this::fromEntityProvinceDataToProvinceDailyData).collect(Collectors.toList());
    }

    @Override
    public List<String> getProfinceListForRegion(String region) {
        return repoCovidCustom.getProvincesForRegion(region);
    }

    /*#######################################################
     * UTILS METHODS 
     #######################################################*/
    private EntityProvinceData fromProvinceDailyDataToEntityProvinceData(ProvinceDailyData data) {
        EntityProvinceDataPk pk = new EntityProvinceDataPk();
        pk.setDate(data.getDate());
        pk.setProvinceCode(data.getProvinceCode());
        pk.setRegionCode(data.getRegionCode());

        EntityProvinceData e = new EntityProvinceData();
        e.setId(pk);
        e.setRegionDesc(data.getRegionDesc());
        e.setDescription(data.getDescription());
        e.setNewInfections(data.getNewInfections());
        e.setShortName(data.getShortName());

        return e;
    }

    private ProvinceDailyData fromEntityProvinceDataToProvinceDailyData(EntityProvinceData entity) {
        ProvinceDailyData p = new ProvinceDailyData();
        p.setRegionDesc(entity.getRegionDesc());
        p.setDescription(entity.getDescription());
        p.setNewInfections(entity.getNewInfections());
        p.setShortName(entity.getShortName());
        p.setDate(entity.getId().getDate());
        p.setProvinceCode(entity.getId().getProvinceCode());
        p.setRegionCode(entity.getId().getRegionCode());
        return p;
    }

    private DailyData fromEntityNationalDataToDailyData(EntityNationalData entity) {
        DailyData dailyData = new DailyData();
        dailyData.setDate(entity.getDate());
        dailyData.setInfectionPercentage(entity.getInfectionPercentage());
        dailyData.setNewCasualties(entity.getNewCasualties());
        dailyData.setNewInfections(entity.getNewInfections());
        dailyData.setNewTests(entity.getNewTests());
        dailyData.setCasualtiesPercentage(entity.getCasualtiesPercentage());
        dailyData.setNewHospitalized(entity.getNewHospitalized());
        dailyData.setNewIntensiveTherapy(entity.getNewIntensiveTherapy());
        dailyData.setNewRecovered(entity.getNewRecovered());
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
        entity.setNewHospitalized(data.getNewHospitalized());
        entity.setNewIntensiveTherapy(data.getNewIntensiveTherapy());
        entity.setNewRecovered(data.getNewRecovered());
        return entity;
    }

    @Override
    public LocalDate getMaxDate() {
        return repoCovidCustom.getMaxDate();
    }

}
