package com.marco.javacovidstatus.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.marco.javacovidstatus.model.dto.CovidDataType;
import com.marco.javacovidstatus.model.dto.NationalDailyDataDto;
import com.marco.javacovidstatus.model.dto.ProvinceDailyDataDto;
import com.marco.javacovidstatus.model.dto.RegionDto;
import com.marco.javacovidstatus.model.dto.RegionalDailyDataDto;
import com.marco.javacovidstatus.model.rest.infections.RespGetNationalData;
import com.marco.javacovidstatus.model.rest.infections.RespGetProvinceData;
import com.marco.javacovidstatus.model.rest.infections.RespGetRegionData;
import com.marco.javacovidstatus.model.rest.infections.RespProvinceChartData;
import com.marco.javacovidstatus.model.rest.infections.RespRegionChartData;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;
import com.marco.javacovidstatus.utils.CovidUtils;
import com.marco.utils.MarcoException;

import io.swagger.annotations.ApiOperation;

@Controller
public class InfectionsController {
	private static final Logger LOGGER = LoggerFactory.getLogger(InfectionsController.class);

	@Autowired
	private CovidDataService service;

	/**
	 * It returns the data per Region
	 * 
	 * @param request
	 * @return
	 * @throws MarcoException 
	 */
	@GetMapping(value = CovidUtils.MAPPING_REGION_DATA)
	@ResponseBody
	@ApiOperation(value = "It returns the selected data type group by region")
	// @formatter:off
	public RespGetRegionData getRegionalData(
			@RequestParam("from")
			@DateTimeFormat(iso = ISO.DATE)
			LocalDate from, 
			@RequestParam("to")
			@DateTimeFormat(iso = ISO.DATE)
			LocalDate to,
			@RequestParam("dataType")
			CovidDataType dataType) throws MarcoException {
		// @formatter:on
		LOGGER.trace("Inside InfectionsController.getRegionalData");
		RespGetRegionData resp = new RespGetRegionData();
		
		List<RegionalDailyDataDto> listData = service.getListRegionalDailyDataBetweenDatesOrderByDateAscending(from, to);
		List<RegionDto> listRegions = service.getRegionsListOrderedByDescription();

		if (!listData.isEmpty()) {
			listRegions.stream().forEach(r -> {
				RespRegionChartData chartData = new RespRegionChartData();
				chartData.setLabel(r.getDesc());

				Function<RegionalDailyDataDto, Object> mapper = null;

				switch (dataType) {
				case NEW_TESTS:
					mapper = RegionalDailyDataDto::getNewTests;
					break;
				case CASUALTIES:
					mapper = RegionalDailyDataDto::getNewCasualties;
					break;
				case NEW_HOSPITALISED:
					mapper = RegionalDailyDataDto::getNewHospitalized;
					break;
				case NEW_INFECTIONS:
					mapper = RegionalDailyDataDto::getNewInfections;
					break;
				case NEW_INTENSIVE_THERAPY:
					mapper = RegionalDailyDataDto::getNewIntensiveTherapy;
					break;
				case NEW_RECOVER:
					mapper = RegionalDailyDataDto::getNewRecovered;
					break;
				case PERC_CASUALTIES:
					mapper = RegionalDailyDataDto::getCasualtiesPercentage;
					break;
				case PERC_INFECTIONS:
					mapper = RegionalDailyDataDto::getInfectionPercentage;
					break;
				}

				chartData.setData(listData.stream().filter(data -> data.getRegionCode().equals(r.getCode()))
						.map(mapper).collect(Collectors.toList()));
				resp.getRegionData().put(r.getCode(), chartData);
			});

			RegionDto region = listRegions.get(0);
			resp.setArrDates(listData.stream().filter(r -> r.getRegionCode().equals(region.getCode()))
					.map(RegionalDailyDataDto::getDate).collect(Collectors.toList()));
		}

		resp.setStatus(true);
		
		return resp;
	}

	/**
	 * It returns the data per Province
	 * 
	 * @param request
	 * @return
	 * @throws MarcoException 
	 */
	@GetMapping(value = CovidUtils.MAPPING_PROVINCE_DATA)
	@ResponseBody
	@ApiOperation(value = "It returns the number of new infections finded in the selected Region group by Province")
	// @formatter:off
	public RespGetProvinceData getProvinceData(
			@RequestParam("from")
			@DateTimeFormat(iso = ISO.DATE)
			LocalDate from, 
			@RequestParam("to")
			@DateTimeFormat(iso = ISO.DATE)
			LocalDate to,
			@RequestParam("regionCode")
			String regionCode) throws MarcoException {
		// @formatter:on
		LOGGER.trace("Inside InfectionsController.getProvinceData");
		RespGetProvinceData resp = new RespGetProvinceData();
		/*
		 * Get all the data for the provinces of the specific region
		 */
		List<ProvinceDailyDataDto> listData = service.getListProvicesDailyDataForRegionBetweenDatesOrderByDateAscending(from, to, regionCode);
		List<String> provinces = service.getListOfProvincesForRegion(regionCode);

		Map<String, RespProvinceChartData> map = new HashMap<>();
		if (!listData.isEmpty()) {
		// @formatter:off
        
        provinces.stream().forEach(codiceProvincia -> {
            /*
             * Get the data for the specific province
             */
            List<ProvinceDailyDataDto> provinceData = listData.stream().filter(data -> data.getProvinceCode().equals(codiceProvincia)).collect(Collectors.toList());
            
            RespProvinceChartData data = new RespProvinceChartData();
            data.setLabel(provinceData.get(0).getDescription());
            data.setNewInfections(provinceData.stream().map(ProvinceDailyDataDto::getNewInfections).collect(Collectors.toList()));
            
            map.put(codiceProvincia, data);
        });
        // @formatter:on
			String province = provinces.get(0);
			resp.setArrDates(listData.stream().filter(p -> p.getProvinceCode().equals(province))
					.map(ProvinceDailyDataDto::getDate).collect(Collectors.toList()));
			resp.setProvinceData(map);
		}
		resp.setStatus(true);
		
		return resp;
	}

	/**
	 * It returns the National data
	 * 
	 * @param request
	 * @return
	 * @throws MarcoException 
	 */
	@GetMapping(value = CovidUtils.MAPPING_NATIONAL_DATA)
	@ApiOperation(value = "It returns the National situation")
	@ResponseBody
	// @formatter:off
	public RespGetNationalData getNationalData(
			@RequestParam("from")
			@DateTimeFormat(iso = ISO.DATE)
			LocalDate from, 
			@RequestParam("to")
			@DateTimeFormat(iso = ISO.DATE)
			LocalDate to) throws MarcoException {
		// @formatter:on
		LOGGER.trace("Inside InfectionsController.getNationalData");
		List<NationalDailyDataDto> listData = service.getListNationalDailyDataBetweenDatesOrderByDateAscending(from, to);
		RespGetNationalData resp = fromListNationaDailyDataDtoToRespGetNationalData(listData);
        resp.setStatus(true);
		
		return resp;
	}
	
	/**
     * It returns the National data
     * 
     * @param request
     * @return
     * @throws MarcoException 
     */
    @GetMapping(value = CovidUtils.MAPPING_NATIONAL_DATA_ALL)
    @ApiOperation(value = "It returns the National situation from the beginning of the C-19")
    @ResponseBody
    // @formatter:off
    public RespGetNationalData getAllNationalData() {
        // @formatter:on
        LOGGER.trace("Inside InfectionsController.getAllNationalData");
        List<NationalDailyDataDto> listData = service.getListAllNationalDailyDataOrderByDateAscending();
        RespGetNationalData resp = fromListNationaDailyDataDtoToRespGetNationalData(listData);
        resp.setStatus(true);
        return resp;
    }
    
    private RespGetNationalData fromListNationaDailyDataDtoToRespGetNationalData(List<NationalDailyDataDto> listData) {
        RespGetNationalData resp = new RespGetNationalData();
        // @formatter:off
        resp.setArrDates(               listData.stream().map(NationalDailyDataDto::getDate)                .collect(Collectors.toList()));
        resp.setArrNewCasualties(       listData.stream().map(NationalDailyDataDto::getNewCasualties)       .collect(Collectors.toList()));
        resp.setArrNewHospitalized(     listData.stream().map(NationalDailyDataDto::getNewHospitalized)     .collect(Collectors.toList()));
        resp.setArrNewInfections(       listData.stream().map(NationalDailyDataDto::getNewInfections)       .collect(Collectors.toList()));
        resp.setArrNewIntensiveTherapy( listData.stream().map(NationalDailyDataDto::getNewIntensiveTherapy) .collect(Collectors.toList()));
        resp.setArrNewRecovered(        listData.stream().map(NationalDailyDataDto::getNewRecovered)        .collect(Collectors.toList()));
        resp.setArrNewTests(            listData.stream().map(NationalDailyDataDto::getNewTests)            .collect(Collectors.toList()));
        resp.setArrPercCasualties(      listData.stream().map(NationalDailyDataDto::getCasualtiesPercentage).collect(Collectors.toList()));
        resp.setArrPercInfections(      listData.stream().map(NationalDailyDataDto::getInfectionPercentage) .collect(Collectors.toList()));
        // @formatter:on
        return resp;
    }
}
