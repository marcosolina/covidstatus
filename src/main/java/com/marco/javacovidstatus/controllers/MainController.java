package com.marco.javacovidstatus.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import com.marco.javacovidstatus.services.interfaces.downloaders.RegionMapDownloader;
import com.marco.javacovidstatus.utils.CovidUtils;
import com.marco.utils.MarcoException;
import com.marco.utils.http.MarcoResponse;

import io.swagger.annotations.ApiOperation;

/**
 * This controller provides the information related to the infections
 * 
 * @author Marco
 *
 */
@Controller
public class MainController {
	private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

	@Value("${covidstatus.version}")
	private String appVersion;

	@Autowired
    private MessageSource msgSource;
	
	@Autowired
	private CovidUtils covidUtils;

	@Autowired
	private CovidDataService service;

	@Autowired
	private RegionMapDownloader regionMapDownloader;

	/**
	 * Home page Endpoint. It will return the html file to load and the initial set
	 * of properties to us in the model.
	 * 
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping(value = CovidUtils.MAPPING_HOME)
	@ApiOperation(value = "It returns the HTML page")
	public String homePage(Model model) {
		LOGGER.info("Inside MainController.homePage");

		/*
		 * By default I will show the data of the last month to the user
		 */
		LocalDate today = LocalDate.now();
		model.addAttribute("from", today.minusMonths(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		model.addAttribute("to", today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

		/*
		 * Preparing the data for the 2 drop down
		 */
		EnumMap<CovidDataType, String> mapCovidDataType = new EnumMap<>(CovidDataType.class);
		Arrays.asList(CovidDataType.values()).stream().forEach(c -> mapCovidDataType.put(c, c.getDescription()));
		model.addAttribute("covidDataType", mapCovidDataType);
		model.addAttribute("regions", service.getRegionsList());

		/*
		 * Adding the SVG map
		 */
		model.addAttribute("svgmap", regionMapDownloader.createHtmlMap());

		/*
		 * Passing to the front end the map of available end points
		 */
		model.addAttribute("urls", covidUtils.getEndPoints());

		model.addAttribute("appVersion", appVersion);

		return "index";
	}

	/**
	 * It returns the data per Region
	 * 
	 * @param request
	 * @return
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
			CovidDataType dataType) {
		// @formatter:on
		RespGetRegionData resp = new RespGetRegionData();
		try {
		
			List<RegionalDailyDataDto> listData = service.getRegionalDatesInRangeAscending(from, to);
			List<RegionDto> listRegions = service.getRegionsList();

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
		} catch (MarcoException e) {
			resp.addError(e);
		} catch (Exception e) {
		    LOGGER.error(e.getMessage());
			if(LOGGER.isTraceEnabled()) {
				e.printStackTrace();
			}
			addGenericErro(resp);
		}
		
		return resp;
	}

	/**
	 * It returns the data per Province
	 * 
	 * @param request
	 * @return
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
			String regionCode) {
		// @formatter:on
		RespGetProvinceData resp = new RespGetProvinceData();
		try {
			/*
			 * Get all the data for the provinces of the specific region
			 */
			List<ProvinceDailyDataDto> listData = service.getProvinceDataInRangeAscending(from, to, regionCode);
			List<String> provinces = service.getProfinceListForRegion(regionCode);

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
		} catch (MarcoException e) {
			resp.addError(e);
		} catch (Exception e) {
		    LOGGER.error(e.getMessage());
			if(LOGGER.isTraceEnabled()) {
				e.printStackTrace();
			}
			addGenericErro(resp);
		}
		
		return resp;
	}

	/**
	 * It returns the National data
	 * 
	 * @param request
	 * @return
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
			LocalDate to) {
		// @formatter:on
		RespGetNationalData resp = new RespGetNationalData();
		try {
			List<NationalDailyDataDto> listData = service.getDatesInRangeAscending(from, to);
			// @formatter:off
			resp.setArrDates(				listData.stream().map(NationalDailyDataDto::getDate)				.collect(Collectors.toList()));
			resp.setArrNewCasualties(		listData.stream().map(NationalDailyDataDto::getNewCasualties)		.collect(Collectors.toList()));
			resp.setArrNewHospitalized(		listData.stream().map(NationalDailyDataDto::getNewHospitalized)		.collect(Collectors.toList()));
			resp.setArrNewInfections(		listData.stream().map(NationalDailyDataDto::getNewInfections)		.collect(Collectors.toList()));
			resp.setArrNewIntensiveTherapy(	listData.stream().map(NationalDailyDataDto::getNewIntensiveTherapy)	.collect(Collectors.toList()));
			resp.setArrNewRecovered(		listData.stream().map(NationalDailyDataDto::getNewRecovered)		.collect(Collectors.toList()));
			resp.setArrNewTests(			listData.stream().map(NationalDailyDataDto::getNewTests)			.collect(Collectors.toList()));
			resp.setArrPercCasualties(		listData.stream().map(NationalDailyDataDto::getCasualtiesPercentage).collect(Collectors.toList()));
			resp.setArrPercInfections(		listData.stream().map(NationalDailyDataDto::getInfectionPercentage)	.collect(Collectors.toList()));
			// @formatter:on

			resp.setStatus(true);
		} catch (MarcoException e) {
			resp.addError(e);
		} catch (Exception e) {
		    LOGGER.error(e.getMessage());
			if(LOGGER.isTraceEnabled()) {
				e.printStackTrace();
			}
			addGenericErro(resp);
		}
		
		return resp;
	}
	
	private void addGenericErro(MarcoResponse resp) {
		resp.addError(new MarcoException(msgSource.getMessage("COVID00001", null, LocaleContextHolder.getLocale())));
	}
}
