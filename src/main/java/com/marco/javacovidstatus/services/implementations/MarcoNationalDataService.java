package com.marco.javacovidstatus.services.implementations;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.marco.javacovidstatus.model.dto.NationalDailyData;
import com.marco.javacovidstatus.model.dto.ProvinceDailyData;
import com.marco.javacovidstatus.model.dto.Region;
import com.marco.javacovidstatus.model.dto.RegionalDailyData;
import com.marco.javacovidstatus.model.entitites.EntityNationalData;
import com.marco.javacovidstatus.model.entitites.EntityProvinceData;
import com.marco.javacovidstatus.model.entitites.EntityProvinceDataPk;
import com.marco.javacovidstatus.model.entitites.EntityRegionalData;
import com.marco.javacovidstatus.model.entitites.EntityRegionalDataPk;
import com.marco.javacovidstatus.repositories.interfaces.CovidRepository;
import com.marco.javacovidstatus.repositories.interfaces.EntityProvinceDataRepo;
import com.marco.javacovidstatus.repositories.interfaces.NationallDataSqlRepository;
import com.marco.javacovidstatus.repositories.interfaces.RegionalDataSqlRepository;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;

public class MarcoNationalDataService implements CovidDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarcoNationalDataService.class);

    @Autowired
    private NationallDataSqlRepository repoNationalData;
    @Autowired
    private RegionalDataSqlRepository repoRegionalData;
    @Autowired
    private EntityProvinceDataRepo repoProvince;
    @Autowired
    private CovidRepository repoCovidCustom;

    @Override
    public List<NationalDailyData> getAllDataDescending() {
        LOGGER.trace("Reading data from the repository");
        List<EntityNationalData> listEntity = repoNationalData.findAllByOrderByDateDesc();
        return listEntity.stream().map(this::fromEntityNationalDataToDailyData).collect(Collectors.toList());
    }

    @Override
    public List<NationalDailyData> getAllDataAscending() {
        LOGGER.trace("Reading data from the repository");
        List<EntityNationalData> listEntity = repoNationalData.findAllByOrderByDateAsc();
        return listEntity.stream().map(this::fromEntityNationalDataToDailyData).collect(Collectors.toList());
    }

    @Override
    public boolean storeData(NationalDailyData dto) {
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
    public List<NationalDailyData> getDatesInRangeAscending(LocalDate from, LocalDate to) {
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
    
    @Override
    public LocalDate getNationalMaxDateAvailable() {
        return repoCovidCustom.getNationalMaxDateAvailable();
    }
    
    @Override
    public LocalDate getRegionMaxDateAvailable() {
        return repoCovidCustom.getRegionMaxDateAvailable();
    }

    @Override
    public LocalDate getProvinceMaxDateAvailable() {
        return repoCovidCustom.getProvinceMaxDateAvailable();
    }

    @Override
    public List<RegionalDailyData> getRegionalDatesInRangeAscending(LocalDate from, LocalDate to) {
        List<EntityRegionalData> regianlData = repoCovidCustom.getRegionalDataAscending(from, to);
        return regianlData.stream().map(this::fromEntityRegionalDataToRegionalDailyData).collect(Collectors.toList());
    }
    
    @Override
    public boolean saveRegionalDailyData(RegionalDailyData data) {
        repoRegionalData.save(this.fromRegionalDailyDataToEntityRegionalData(data));
        return true;
    }

    /*#######################################################
     * UTILS METHODS 
     #######################################################*/
    
    private RegionalDailyData fromEntityRegionalDataToRegionalDailyData(EntityRegionalData entity) {
        RegionalDailyData data = new RegionalDailyData();
        data.setDate(entity.getId().getDate());
        data.setRegionCode(entity.getId().getRegionCode());
        data.setInfectionPercentage(entity.getInfectionPercentage());
        data.setNewCasualties(entity.getNewCasualties());
        data.setNewInfections(entity.getNewInfections());
        data.setNewTests(entity.getNewTests());
        data.setCasualtiesPercentage(entity.getCasualtiesPercentage());
        data.setNewHospitalized(entity.getNewHospitalized());
        data.setNewIntensiveTherapy(entity.getNewIntensiveTherapy());
        data.setNewRecovered(entity.getNewRecovered());
        return data;
    }
    
    private EntityRegionalData fromRegionalDailyDataToEntityRegionalData(RegionalDailyData data) {
        EntityRegionalData entity = new EntityRegionalData();
        EntityRegionalDataPk pk = new EntityRegionalDataPk();
        pk.setDate(data.getDate());
        pk.setRegionCode(data.getRegionCode());
        
        entity.setId(pk);
        
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

    private NationalDailyData fromEntityNationalDataToDailyData(EntityNationalData entity) {
        NationalDailyData dailyData = new NationalDailyData();
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

    private EntityNationalData fromDailyData(NationalDailyData data) {
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

}
