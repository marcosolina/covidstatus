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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.marco.javacovidstatus.model.dto.VaccinatedPeopleTypeDto;
import com.marco.javacovidstatus.model.dto.VaccinesDeliveredPerDayDto;
import com.marco.javacovidstatus.model.dto.VaccinesReceivedUsedDto;
import com.marco.javacovidstatus.model.rest.vaccines.ReqGetVaccinesData;
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

	@Autowired
	private MessageSource msgSource;

	/**
	 * It returns the delivered vaccines per Region
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping(value = CovidUtils.MAPPING_VACCINE_DELIVERED_PER_REGION)
	@ResponseBody
	public RespGetVaccinesDeliveredPerRegion getVaccinesDeliveredPerRegionData(
			@RequestBody ReqGetVaccinesData request) {
		LOGGER.trace("Inside VaccinesController.getVaccinesDeliveredPerRegionData");

		RespGetVaccinesDeliveredPerRegion resp = new RespGetVaccinesDeliveredPerRegion();
		try {
			/*
			 * The Key of the map is the Region Code
			 */
			Map<String, List<VaccinesDeliveredPerDayDto>> regionData = service
					.getDeliveredVaccinesPerRegionBetweenDatesPerRegion(request.getFrom(), request.getTo());

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
		} catch (Exception e) {
			if (LOGGER.isTraceEnabled()) {
				e.printStackTrace();
			}
			resp.addError(
					new MarcoException(msgSource.getMessage("COVID00001", null, LocaleContextHolder.getLocale())));
		}
		return resp;
	}

	/**
	 * It returns the vaccines delivered by every supplier
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping(value = CovidUtils.MAPPING_VACCINE_DELIVERED_PER_SUPPLIER)
	@ResponseBody
	public RespGetVaccinesDeliveredPerSupplier getVaccinesDeliveredPerSupplier(
			@RequestBody ReqGetVaccinesData request) {
		LOGGER.trace("Inside VaccinesController.getVaccinesDeliveredPerSupplier");

		RespGetVaccinesDeliveredPerSupplier resp = new RespGetVaccinesDeliveredPerSupplier();
		try {
			Map<String, Long> supplierData = service.getDeliveredVaccinesBetweenDatesPerSupplier(request.getFrom(),
					request.getTo());

			resp.setDeliveredPerSupplier(supplierData);
			resp.setStatus(true);
		} catch (Exception e) {
			if (LOGGER.isTraceEnabled()) {
				e.printStackTrace();
			}
			resp.addError(
					new MarcoException(msgSource.getMessage("COVID00001", null, LocaleContextHolder.getLocale())));
		}

		return resp;
	}

	/**
	 * It returns the number of persons who has received either the first or second
	 * vaccine dose
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping(value = CovidUtils.MAPPING_VACCINE_DOSES_DATA)
	@ResponseBody
	public RespGetVaccinesDosesData getVaccinesDosesData(@RequestBody ReqGetVaccinesData request) {
		LOGGER.trace("Inside VaccinesController.getVaccinesDosesData");

		RespGetVaccinesDosesData resp = new RespGetVaccinesDosesData();

		try {
			Map<String, Long> dataShotNumber = service.getGiveShotNumberBetweenDates(request.getFrom(),
					request.getTo());
			resp.setDataShotNumber(dataShotNumber);
			resp.setStatus(true);

		} catch (MarcoException e) {
			if (LOGGER.isTraceEnabled()) {
				e.printStackTrace();
			}
			resp.addError(e);
		}
		return resp;
	}

	/**
	 * It returns the data of the persons vaccinated
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping(value = CovidUtils.MAPPING_VACCINE_VACCINATED_PEOPLE)
	@ResponseBody
	public RespGetVaccinatedPeopleData getVaccinatedPeople(@RequestBody ReqGetVaccinesData request) {
		LOGGER.trace("Inside VaccinesController.getVaccinatedPeople");

		RespGetVaccinatedPeopleData resp = new RespGetVaccinatedPeopleData();
		try {
			VaccinatedPeopleTypeDto dataVaccinatedPeople = service.getVaccinatedPeopleBetweenDates(request.getFrom(),
					request.getTo());

			resp.setDataVaccinatedPeople(dataVaccinatedPeople.getDataVaccinatedPeople());
			resp.setArrDates(dataVaccinatedPeople.getDates());
			resp.setStatus(true);
		} catch (Exception e) {
			if (LOGGER.isTraceEnabled()) {
				e.printStackTrace();
			}
			resp.addError(
					new MarcoException(msgSource.getMessage("COVID00001", null, LocaleContextHolder.getLocale())));
		}
		return resp;
	}

	/**
	 * It returns the number of people vaccinated grouped per age
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping(value = CovidUtils.MAPPING_VACCINE_VACCINATED_PER_AGE)
	@ResponseBody
	public RespGetVaccinatedPeoplePerAgeData getVaccinesPerAgeData(@RequestBody ReqGetVaccinesData request) {
		LOGGER.trace("Inside VaccinesController.getVaccinesPerAgeData");

		RespGetVaccinatedPeoplePerAgeData resp = new RespGetVaccinatedPeoplePerAgeData();
		try {
			Map<String, Long> dataVaccinatedPerAge = service.getVaccinatedAgeRangeBetweenDates(request.getFrom(),
					request.getTo());

			resp.setDataVaccinatedPerAge(dataVaccinatedPerAge);
			resp.setStatus(true);
		} catch (Exception e) {
			if (LOGGER.isTraceEnabled()) {
				e.printStackTrace();
			}
			resp.addError(
					new MarcoException(msgSource.getMessage("COVID00001", null, LocaleContextHolder.getLocale())));
		}
		return resp;
	}

	/**
	 * It returns the total data of delivered vaccines and total number of used
	 * vaccines
	 * 
	 * @return
	 */
	@GetMapping(value = CovidUtils.MAPPING_VACCINE_TOTAL_DATA)
	@ResponseBody
	public RespGetTotalDelivereUsedVaccineData getTotlaVaccinesDeliveredUsed() {
		LOGGER.trace("Inside VaccinesController.getTotlaVaccinesDeliveredUsed");

		RespGetTotalDelivereUsedVaccineData resp = new RespGetTotalDelivereUsedVaccineData();
		try {
			VaccinesReceivedUsedDto dto = service.getTotlalVaccinesDeliveredUsed();
			resp.setTotalDeliveredVaccines(dto.getTotalVaccinesReceived());
			resp.setTotalUsedVaccines(dto.getTotalVaccinesUsed());
			resp.setStatus(true);
		} catch (Exception e) {
			if (LOGGER.isTraceEnabled()) {
				e.printStackTrace();
			}
			resp.addError(
					new MarcoException(msgSource.getMessage("COVID00001", null, LocaleContextHolder.getLocale())));
		}

		return resp;
	}

	/**
	 * It returns the total number of delivered and used vaccines per region
	 * 
	 * @return
	 */
	@GetMapping(value = CovidUtils.MAPPING_VACCINE_TOTAL_DATA_PER_REGION)
	@ResponseBody
	public RespGetTotalDelivereUsedVaccineDataPerRegion getTotlaVaccinesDeliveredUsedPerRegion() {
		LOGGER.trace("Inside VaccinesController.getTotlaVaccinesDeliveredUsedPerRegion");

		RespGetTotalDelivereUsedVaccineDataPerRegion resp = new RespGetTotalDelivereUsedVaccineDataPerRegion();
		try {
			resp.setData(service.getVacinesTotalDeliveredGivenPerRegion());
			resp.setStatus(true);
		} catch (Exception e) {
			if (LOGGER.isTraceEnabled()) {
				e.printStackTrace();
			}
			resp.addError(
					new MarcoException(msgSource.getMessage("COVID00001", null, LocaleContextHolder.getLocale())));
		}
		return resp;
	}

	/**
	 * It returns the total number of vaccinated people per age range
	 * 
	 * @return
	 */
	@GetMapping(value = CovidUtils.MAPPING_VACCINE_TOTAL_DATA_PER_AGE)
	@ResponseBody
	public RespGetVaccinatedPeoplePerAgeData getTotalVaccinesPerAgeData() {
		LOGGER.trace("Inside VaccinesController.getVaccinesPerAgeData");

		RespGetVaccinatedPeoplePerAgeData resp = new RespGetVaccinatedPeoplePerAgeData();
		try {
			Map<String, Long> dataVaccinatedPerAge = service.getVaccinatedAgeRangeTotals();

			resp.setDataVaccinatedPerAge(dataVaccinatedPerAge);
			resp.setStatus(true);
		} catch (Exception e) {
			if (LOGGER.isTraceEnabled()) {
				e.printStackTrace();
			}
			resp.addError(
					new MarcoException(msgSource.getMessage("COVID00001", null, LocaleContextHolder.getLocale())));
		}
		return resp;
	}
}
