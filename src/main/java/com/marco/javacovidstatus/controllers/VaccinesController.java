package com.marco.javacovidstatus.controllers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import com.marco.javacovidstatus.model.dto.PeopleVaccinated;
import com.marco.javacovidstatus.model.dto.VaccinatedPeopleTypeDto;
import com.marco.javacovidstatus.model.dto.VaccinesDeliveredPerDayDto;
import com.marco.javacovidstatus.model.dto.VaccinesReceivedUsedDto;
import com.marco.javacovidstatus.model.rest.vaccines.RespGetTotalDelivereUsedVaccineData;
import com.marco.javacovidstatus.model.rest.vaccines.RespGetTotalDelivereUsedVaccineDataPerRegion;
import com.marco.javacovidstatus.model.rest.vaccines.RespGetVaccinatedPeopleData;
import com.marco.javacovidstatus.model.rest.vaccines.RespGetVaccinatedPeoplePerAgeData;
import com.marco.javacovidstatus.model.rest.vaccines.RespGetVaccinesDeliveredPerRegion;
import com.marco.javacovidstatus.model.rest.vaccines.RespGetVaccinesDeliveredPerSupplier;
import com.marco.javacovidstatus.model.rest.vaccines.RespGetVaccinesDosesData;
import com.marco.javacovidstatus.services.interfaces.VaccineDataService;
import com.marco.javacovidstatus.utils.CovidUtils;
import com.marco.utils.MarcoException;

import io.swagger.annotations.ApiOperation;

/**
 * This controller returns the data related to the Vaccines
 * 
 * @author Marco
 *
 */
@Controller
public class VaccinesController {
	private static final Logger LOGGER = LoggerFactory.getLogger(VaccinesController.class);

	@Autowired
	private VaccineDataService service;

	/**
	 * It returns the delivered vaccines per Region
	 * 
	 * @param request
	 * @return
	 * @throws MarcoException 
	 */
	@GetMapping(value = CovidUtils.MAPPING_VACCINE_DELIVERED_PER_REGION)
	@ResponseBody
	@ApiOperation(value = "It returns the number of vaccines delivered in every region")
	// @formatter:off
	public RespGetVaccinesDeliveredPerRegion getVaccinesDeliveredPerRegionData(
			@RequestParam("from")
			@DateTimeFormat(iso = ISO.DATE)
			LocalDate from, 
			@RequestParam("to")
			@DateTimeFormat(iso = ISO.DATE)
			LocalDate to) throws MarcoException {
		// @formatter:on

		LOGGER.trace("Inside VaccinesController.getVaccinesDeliveredPerRegionData");

		RespGetVaccinesDeliveredPerRegion resp = new RespGetVaccinesDeliveredPerRegion();
		/*
		 * The Key of the map is the Region Code
		 */
		Map<String, List<VaccinesDeliveredPerDayDto>> regionData = service
				.getDeliveredVaccinesPerRegionBetweenDatesPerRegion(from, to);

		/*
		 * Createing a map with: Key -> Region Code Value -> List of number of delivered
		 * vaccines
		 */
		Map<String, List<Long>> dataPerRegion = new HashMap<>();
		regionData.forEach((k, v) -> dataPerRegion.put(k,
				v.stream().map(VaccinesDeliveredPerDayDto::getDosesDelivered).collect(Collectors.toList())));

		/*
		 * Creating the list of Dates for which I have the data
		 */
		Set<LocalDate> dates = new HashSet<>();
		regionData.forEach((k, v) -> v.stream().forEach(o -> dates.add(o.getDate())));
		List<LocalDate> list = Arrays.asList(dates.toArray(new LocalDate[0]));
		Collections.sort(list);

		resp.setDeliveredPerRegion(dataPerRegion);
		resp.setArrDates(list);
		resp.setStatus(true);
		
		return resp;
	}

	/**
	 * It returns the vaccines delivered by every supplier
	 * 
	 * @param request
	 * @return
	 * @throws MarcoException 
	 */
	@GetMapping(value = CovidUtils.MAPPING_VACCINE_DELIVERED_PER_SUPPLIER)
	@ApiOperation(value = "It returns the number of vaccines delivered by the different suppliers")
	@ResponseBody
	// @formatter:off
	public RespGetVaccinesDeliveredPerSupplier getVaccinesDeliveredPerSupplier(
			@RequestParam("from")
			@DateTimeFormat(iso = ISO.DATE)
			LocalDate from, 
			@RequestParam("to")
			@DateTimeFormat(iso = ISO.DATE)
			LocalDate to) throws MarcoException {
		// @formatter:on
		LOGGER.trace("Inside VaccinesController.getVaccinesDeliveredPerSupplier");

		RespGetVaccinesDeliveredPerSupplier resp = new RespGetVaccinesDeliveredPerSupplier();
		Map<String, Long> supplierData = service.getDeliveredVaccinesBetweenDatesPerSupplier(from, to);

		resp.setDeliveredPerSupplier(supplierData);
		resp.setStatus(true);
		
		return resp;
	}

	/**
	 * It returns the number of persons who has received either the first or second
	 * vaccine dose
	 * 
	 * @param request
	 * @return
	 * @throws MarcoException 
	 */
	@GetMapping(value = CovidUtils.MAPPING_VACCINE_DOSES_DATA)
	@ApiOperation(value = "It returns the number of given doses group by \"Shot\" (First or Second)")
	@ResponseBody
	// @formatter:off
	public RespGetVaccinesDosesData getVaccinesDosesData(
			@RequestParam("from")
			@DateTimeFormat(iso = ISO.DATE)
			LocalDate from, 
			@RequestParam("to")
			@DateTimeFormat(iso = ISO.DATE)
			LocalDate to) throws MarcoException {
		// @formatter:on
		LOGGER.trace("Inside VaccinesController.getVaccinesDosesData");

		RespGetVaccinesDosesData resp = new RespGetVaccinesDosesData();

		Map<String, Long> dataShotNumber = service.getGiveShotNumberBetweenDates(from, to);
		resp.setDataShotNumber(dataShotNumber);
		resp.setStatus(true);
		
		return resp;
	}

	/**
	 * It returns the data of the persons vaccinated
	 * 
	 * @param request
	 * @return
	 * @throws MarcoException 
	 */
	@GetMapping(value = CovidUtils.MAPPING_VACCINE_VACCINATED_PEOPLE)
	@ApiOperation(value = "It returns the number of vaccinated people group by category")
	@ResponseBody
	// @formatter:off
	public RespGetVaccinatedPeopleData getVaccinatedPeople(
			@RequestParam("from")
			@DateTimeFormat(iso = ISO.DATE)
			LocalDate from, 
			@RequestParam("to")
			@DateTimeFormat(iso = ISO.DATE)
			LocalDate to) throws MarcoException {
		// @formatter:on
		LOGGER.trace("Inside VaccinesController.getVaccinatedPeople");

		RespGetVaccinatedPeopleData resp = new RespGetVaccinatedPeopleData();
		VaccinatedPeopleTypeDto dataVaccinatedPeople = service.getVaccinatedPeopleBetweenDates(from, to);

		resp.setDataVaccinatedPeople(dataVaccinatedPeople.getDataVaccinatedPeople());
		resp.setArrDates(dataVaccinatedPeople.getDates());
		resp.setStatus(true);
		
		return resp;
	}

	/**
	 * It returns the number of people vaccinated grouped per age
	 * 
	 * @param request
	 * @return
	 * @throws MarcoException 
	 */
	@GetMapping(value = CovidUtils.MAPPING_VACCINE_VACCINATED_PER_AGE)
	@ApiOperation(value = "It returns the number of vaccinated people grouped by age")
	@ResponseBody
	// @formatter:off
	public RespGetVaccinatedPeoplePerAgeData getVaccinesPerAgeData(
			@RequestParam("from")
			@DateTimeFormat(iso = ISO.DATE)
			LocalDate from, 
			@RequestParam("to")
			@DateTimeFormat(iso = ISO.DATE)
			LocalDate to) throws MarcoException {
		// @formatter:on
		LOGGER.trace("Inside VaccinesController.getVaccinesPerAgeData");

		RespGetVaccinatedPeoplePerAgeData resp = new RespGetVaccinatedPeoplePerAgeData();
		Map<String, PeopleVaccinated> dataVaccinatedPerAge = service.getVaccinatedAgeRangeBetweenDates(from, to);

		resp.setDataVaccinatedPerAge(dataVaccinatedPerAge);
		resp.setStatus(true);
		
		return resp;
	}

	/**
	 * It returns the total data of delivered vaccines and total number of used
	 * vaccines
	 * 
	 * @return
	 */
	@GetMapping(value = CovidUtils.MAPPING_VACCINE_TOTAL_DATA)
	@ApiOperation(value = "It returns the total number of vaccines delivered to Italy and the total number of used vaccines so far")
	@ResponseBody
	public RespGetTotalDelivereUsedVaccineData getTotlaVaccinesDeliveredUsed() {
		LOGGER.trace("Inside VaccinesController.getTotlaVaccinesDeliveredUsed");

		RespGetTotalDelivereUsedVaccineData resp = new RespGetTotalDelivereUsedVaccineData();
		VaccinesReceivedUsedDto dto = service.getTotlalVaccinesDeliveredUsed();
		resp.setTotalDeliveredVaccines(dto.getTotalVaccinesReceived());
		resp.setTotalUsedVaccines(dto.getTotalVaccinesUsed());
		resp.setStatus(true);
		
		return resp;
	}

	/**
	 * It returns the total number of delivered and used vaccines per region
	 * 
	 * @return
	 */
	@GetMapping(value = CovidUtils.MAPPING_VACCINE_TOTAL_DATA_PER_REGION)
	@ApiOperation(value = "It returns the total number of vaccines delivered to the regions and the number of vaccines used so far")
	@ResponseBody
	public RespGetTotalDelivereUsedVaccineDataPerRegion getTotlaVaccinesDeliveredUsedPerRegion() {
		LOGGER.trace("Inside VaccinesController.getTotlaVaccinesDeliveredUsedPerRegion");

		RespGetTotalDelivereUsedVaccineDataPerRegion resp = new RespGetTotalDelivereUsedVaccineDataPerRegion();
		resp.setData(service.getVacinesTotalDeliveredGivenPerRegion());
		resp.setStatus(true);
		
		return resp;
	}

	/**
	 * It returns the total number of vaccinated people per age range
	 * 
	 * @return
	 */
	@GetMapping(value = CovidUtils.MAPPING_VACCINE_TOTAL_DATA_PER_AGE)
	@ApiOperation(value = "It returns the total number of vaccinated people so far, group by age")
	@ResponseBody
	public RespGetVaccinatedPeoplePerAgeData getTotalVaccinesPerAgeData() {
		LOGGER.trace("Inside VaccinesController.getVaccinesPerAgeData");

		RespGetVaccinatedPeoplePerAgeData resp = new RespGetVaccinatedPeoplePerAgeData();
		Map<String, PeopleVaccinated> dataVaccinatedPerAge = service.getVaccinatedAgeRangeTotals();

		resp.setDataVaccinatedPerAge(dataVaccinatedPerAge);
		resp.setStatus(true);
		
		return resp;
	}
	
	/**
	 * It returns the total vaccines delivered group by supplier
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping(value = CovidUtils.MAPPING_VACCINE_TOTAL_DELIVERED_PER_SUPPLIER)
	@ApiOperation(value = "It returns the total number of vaccines delivered group by supplier")
	@ResponseBody
	// @formatter:off
	public RespGetVaccinesDeliveredPerSupplier getTotalVaccinesDeliveredPerSupplier() {
		// @formatter:on
		LOGGER.trace("Inside VaccinesController.getVaccinesDeliveredPerSupplier");

		RespGetVaccinesDeliveredPerSupplier resp = new RespGetVaccinesDeliveredPerSupplier();
		Map<String, Long> supplierData = service.getTotalDeliveredVaccinesPerSupplier();

		resp.setDeliveredPerSupplier(supplierData);
		resp.setStatus(true);
		
		return resp;
	}
}
