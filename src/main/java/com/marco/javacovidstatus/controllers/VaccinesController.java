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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.marco.javacovidstatus.model.dto.VaccinatedPeople;
import com.marco.javacovidstatus.model.dto.VaccinesDelivered;
import com.marco.javacovidstatus.model.rest.vaccines.ReqGetVaccinesData;
import com.marco.javacovidstatus.model.rest.vaccines.RespGetVaccinatedPeopleData;
import com.marco.javacovidstatus.model.rest.vaccines.RespGetVaccinatedPeoplePerAgeData;
import com.marco.javacovidstatus.model.rest.vaccines.RespGetVaccinesDeliveredPerRegion;
import com.marco.javacovidstatus.model.rest.vaccines.RespGetVaccinesDeliveredPerSupplier;
import com.marco.javacovidstatus.model.rest.vaccines.RespGetVaccinesDosesData;
import com.marco.javacovidstatus.services.interfaces.VaccineDateService;
import com.marco.javacovidstatus.utils.CovidUtils;
import com.marco.utils.MarcoException;

/**
 * This controller returns the data used to create the Vaccines charts
 * 
 * @author Marco
 *
 */
@Controller
public class VaccinesController {
	private static final Logger LOGGER = LoggerFactory.getLogger(VaccinesController.class);

	@Autowired
	private VaccineDateService service;

	@PostMapping(value = CovidUtils.MAPPING_VACCINE_DELIVERED_PER_REGION)
	@ResponseBody
	public RespGetVaccinesDeliveredPerRegion getVaccinesDeliveredPerRegionData(@RequestBody ReqGetVaccinesData request) {
		LOGGER.trace("Inside VaccinesController.getVaccinesDeliveredPerRegionData");
		
		RespGetVaccinesDeliveredPerRegion resp = new RespGetVaccinesDeliveredPerRegion();

		Map<String, List<VaccinesDelivered>> regionData = service.getDeliveredVaccinesPerRegionBetweenDatesPerRegion(request.getFrom(), request.getTo());

		Map<String, List<Long>> dataPerRegion = new HashMap<>();
		regionData.forEach((k, v) -> dataPerRegion.put(k,
				v.stream().map(VaccinesDelivered::getDosesDelivered).collect(Collectors.toList())));

		Set<LocalDate> dates = new HashSet<>();
		regionData.forEach((k, v) -> v.stream().forEach(o -> dates.add(o.getDate())));

		List<LocalDate> list = Arrays.asList(dates.toArray(new LocalDate[0]));
		Collections.sort(list);

		resp.setDeliveredPerRegion(dataPerRegion);
		resp.setArrDates(list);
		resp.setStatus(true);
		return resp;
	}

	@PostMapping(value = CovidUtils.MAPPING_VACCINE_DELIVERED_PER_SUPPLIER)
	@ResponseBody
	public RespGetVaccinesDeliveredPerSupplier getVaccinesDeliveredPerSupplier(@RequestBody ReqGetVaccinesData request) {
		LOGGER.trace("Inside VaccinesController.getVaccinesDeliveredPerSupplier");
		
		RespGetVaccinesDeliveredPerSupplier resp = new RespGetVaccinesDeliveredPerSupplier();

		Map<String, Long> supplierData = service.getDeliveredVaccinesBetweenDatesPerSupplier(request.getFrom(),
				request.getTo());

		resp.setDeliveredPerSupplier(supplierData);
		resp.setStatus(true);

		return resp;
	}
	
	@PostMapping(value = CovidUtils.MAPPING_VACCINE_DOSES_DATA)
	@ResponseBody
	public RespGetVaccinesDosesData getVaccinesDosesData(@RequestBody ReqGetVaccinesData request) {
		LOGGER.trace("Inside VaccinesController.getVaccinesDosesData");
		
		RespGetVaccinesDosesData resp = new RespGetVaccinesDosesData();

		try {
			Map<String, Long> dataShotNumber = service.getGiveShotNumberBetweenDates(request.getFrom(), request.getTo());
			resp.setDataShotNumber(dataShotNumber);
			resp.setStatus(true);

		} catch (MarcoException e) {
			e.printStackTrace();
			resp.addError(e);
		}
		return resp;
	}
	
	@PostMapping(value = CovidUtils.MAPPING_VACCINE_VACCINATED_PEOPLE)
	@ResponseBody
	public RespGetVaccinatedPeopleData getVaccinatedPeople(@RequestBody ReqGetVaccinesData request) {
		LOGGER.trace("Inside VaccinesController.getVaccinatedPeople");
		
		RespGetVaccinatedPeopleData resp = new RespGetVaccinatedPeopleData();

		VaccinatedPeople dataVaccinatedPeople = service.getVaccinatedPeopleBetweenDates(request.getFrom(), request.getTo());

		resp.setDataVaccinatedPeople(dataVaccinatedPeople.getDataVaccinatedPeople());
		resp.setArrDates(dataVaccinatedPeople.getDates());
		resp.setStatus(true);

		return resp;
	}
	
	@PostMapping(value = CovidUtils.MAPPING_VACCINE_VACCINATED_PER_AGE)
	@ResponseBody
	public RespGetVaccinatedPeoplePerAgeData getVaccinesPerAgeData(@RequestBody ReqGetVaccinesData request) {
		LOGGER.trace("Inside VaccinesController.getVaccinesPerAgeData");
		
		RespGetVaccinatedPeoplePerAgeData resp = new RespGetVaccinatedPeoplePerAgeData();

		Map<String, Long> dataVaccinatedPerAge = service.getVaccinatedAgeRangeBetweenDates(request.getFrom(), request.getTo());

		resp.setDataVaccinatedPerAge(dataVaccinatedPerAge);
		resp.setStatus(true);

		return resp;
	}
}
