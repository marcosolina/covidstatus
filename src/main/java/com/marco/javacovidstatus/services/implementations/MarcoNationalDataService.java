package com.marco.javacovidstatus.services.implementations;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.marco.javacovidstatus.model.dto.NationalDailyDataDto;
import com.marco.javacovidstatus.model.dto.ProvinceDailyDataDto;
import com.marco.javacovidstatus.model.dto.RegionDto;
import com.marco.javacovidstatus.model.dto.RegionalDailyDataDto;
import com.marco.javacovidstatus.model.entitites.EntityRegionCode;
import com.marco.javacovidstatus.model.entitites.infections.EntityNationalData;
import com.marco.javacovidstatus.model.entitites.infections.EntityProvinceData;
import com.marco.javacovidstatus.model.entitites.infections.EntityProvinceDataPk;
import com.marco.javacovidstatus.model.entitites.infections.EntityRegionalData;
import com.marco.javacovidstatus.model.entitites.infections.EntityRegionalDataPk;
import com.marco.javacovidstatus.repositories.interfaces.DataRepository;
import com.marco.javacovidstatus.repositories.interfaces.NationalInfectionDataRepository;
import com.marco.javacovidstatus.repositories.interfaces.ProvinceInfectionDataRepo;
import com.marco.javacovidstatus.repositories.interfaces.RegionalInfectionDataRepository;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;
import com.marco.utils.MarcoException;

/**
 * My implementation of the {@link CovidDataService}
 * 
 * @author Marco
 *
 */
public class MarcoNationalDataService implements CovidDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarcoNationalDataService.class);

    @Autowired
    private NationalInfectionDataRepository repoNationalData;
    @Autowired
    private RegionalInfectionDataRepository repoRegionalData;
    @Autowired
    private ProvinceInfectionDataRepo repoProvince;
    @Autowired
    private DataRepository repoCovidCustom;
	@Autowired
	private MessageSource msgSource;

    @Override
    public List<NationalDailyDataDto> getListAllNationalDailyDataOrderByDateDesc() {
        LOGGER.trace("Reading data from the repository");
        List<EntityNationalData> listEntity = repoNationalData.findAllByOrderByDateDesc();
        return listEntity.stream().map(this::fromEntityNationalDataToDailyData).collect(Collectors.toList());
    }

    @Override
    public List<NationalDailyDataDto> getListAllNationalDailyDataOrderByDateAscending() {
        LOGGER.trace("Reading data from the repository");
        List<EntityNationalData> listEntity = repoNationalData.findAllByOrderByDateAsc();
        return listEntity.stream().map(this::fromEntityNationalDataToDailyData).collect(Collectors.toList());
    }

    @Override
    public boolean saveNationalDailyData(NationalDailyDataDto dto) {
        repoNationalData.save(fromDailyData(dto));
        return true;
    }

    @Override
    public boolean deleteAllNationalRegionalProvinceData() {
        repoNationalData.deleteAll();
        repoProvince.deleteAll();
        repoRegionalData.deleteAll();
        return true;
    }

    @Override
    public List<NationalDailyDataDto> getListNationalDailyDataBetweenDatesOrderByDateAscending(LocalDate from, LocalDate to) throws MarcoException {
    	checkDates(from, to);
    	
        List<EntityNationalData> listEntity = repoNationalData.findByDateBetweenOrderByDateAsc(from, to);
        return listEntity.stream().map(this::fromEntityNationalDataToDailyData).collect(Collectors.toList());
    }
    
    @Override
    public boolean storeProvinceDailyData(ProvinceDailyDataDto data) {
        repoProvince.save(fromProvinceDailyDataToEntityProvinceData(data));
        return true;
    }

    @Override
    public List<RegionDto> getRegionsListOrderedByDescription() {
        List<EntityRegionCode> list = repoCovidCustom.getRegionListOrderedByDescription(); 
        return list.stream().map(rd -> {
        	RegionDto dto = new RegionDto();
        	dto.setArea(rd.getArea());
        	dto.setCode(rd.getRegionCode());
        	dto.setDesc(rd.getDesc());
        	return dto;
        	}).collect(Collectors.toList());
    }

    @Override
    public List<ProvinceDailyDataDto> getListProvicesDailyDataForRegionBetweenDatesOrderByDateAscending(LocalDate from, LocalDate to, String regionCode) throws MarcoException {
    	checkDates(from, to);
    	if(regionCode == null || regionCode.isBlank()) {
    		throw new MarcoException(msgSource.getMessage("COVID00003", null, LocaleContextHolder.getLocale()));
    	}
    	
        List<EntityProvinceData> list = repoCovidCustom.getProvinceDataBetweenDatesOrderByDateAscending(from, to,
                regionCode);
        return list.stream().map(this::fromEntityProvinceDataToProvinceDailyData).collect(Collectors.toList());
    }

    @Override
    public List<String> getListOfProvincesForRegion(String region) {
        return repoCovidCustom.getProvincesListForRegion(region);
    }

    @Override
    public LocalDate getLastDateOfAvailableNationalData() {
        return repoCovidCustom.getLastDateAvailableForNationalData();
    }

    @Override
    public LocalDate getLastDateOfAvailableRegionalData() {
        return repoCovidCustom.getLastDateAvailableFroRegionalData();
    }

    @Override
    public LocalDate getLastDateOfAvailableProvinceData() {
        return repoCovidCustom.getLastDateAvailableForProvincesData();
    }

    @Override
    public List<RegionalDailyDataDto> getListRegionalDailyDataBetweenDatesOrderByDateAscending(LocalDate from, LocalDate to) throws MarcoException {
    	checkDates(from, to);
    	
        List<EntityRegionalData> regianlData = repoCovidCustom.getRegionalDataListOrderedByDateAsc(from, to);
        return regianlData.stream().map(this::fromEntityRegionalDataToRegionalDailyData).collect(Collectors.toList());
    }

    @Override
    public boolean saveRegionalDailyData(RegionalDailyDataDto data) {
        repoRegionalData.save(this.fromRegionalDailyDataToEntityRegionalData(data));
        return true;
    }

    /*
     * ####################################################### 
     * UTILS METHODS
     * #######################################################
     */

    private RegionalDailyDataDto fromEntityRegionalDataToRegionalDailyData(EntityRegionalData entity) {
        RegionalDailyDataDto data = new RegionalDailyDataDto();
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

    private EntityRegionalData fromRegionalDailyDataToEntityRegionalData(RegionalDailyDataDto data) {
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

    private EntityProvinceData fromProvinceDailyDataToEntityProvinceData(ProvinceDailyDataDto data) {
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

    private ProvinceDailyDataDto fromEntityProvinceDataToProvinceDailyData(EntityProvinceData entity) {
        ProvinceDailyDataDto p = new ProvinceDailyDataDto();
        p.setRegionDesc(entity.getRegionDesc());
        p.setDescription(entity.getDescription());
        p.setNewInfections(entity.getNewInfections());
        p.setShortName(entity.getShortName());
        p.setDate(entity.getId().getDate());
        p.setProvinceCode(entity.getId().getProvinceCode());
        p.setRegionCode(entity.getId().getRegionCode());
        return p;
    }

    private NationalDailyDataDto fromEntityNationalDataToDailyData(EntityNationalData entity) {
        NationalDailyDataDto dailyData = new NationalDailyDataDto();
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

    private EntityNationalData fromDailyData(NationalDailyDataDto data) {
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
    
    private void checkDates(LocalDate from, LocalDate to) throws MarcoException {
		if(from == null || to == null) {
			throw new MarcoException(msgSource.getMessage("COVID00002", null, LocaleContextHolder.getLocale()));
		}
	}

}
