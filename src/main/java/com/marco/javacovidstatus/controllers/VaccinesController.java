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

import com.marco.javacovidstatus.model.dto.VaccinesDelivered;
import com.marco.javacovidstatus.model.rest.ReqGetVaccinesDelivered;
import com.marco.javacovidstatus.model.rest.RespGetVaccinesDelivered;
import com.marco.javacovidstatus.services.interfaces.VaccineDateService;

/**
 * This controller returns the data used to create the Vaccines charts
 * 
 * @author Marco
 *
 */
@Controller
public class VaccinesController {
	private static final Logger LOGGER = LoggerFactory.getLogger(VaccinesController.class);

	public static final String MAPPING_VACCINE_DELIVERED_DATA = "/vaccinesDelivered";

	@Autowired
	private VaccineDateService service;

	@PostMapping(value = MAPPING_VACCINE_DELIVERED_DATA)
	@ResponseBody
	public RespGetVaccinesDelivered getVaccinesDeliveredData(@RequestBody ReqGetVaccinesDelivered request) {
		LOGGER.trace("Inside VaccinesController.getVaccinesDeliveredData");
		RespGetVaccinesDelivered resp = new RespGetVaccinesDelivered();

		
		Map<String, List<VaccinesDelivered>> regionData = service.getDeliveredVaccinesBetweenDatesPerRegion(request.getFrom(), request.getTo());
		Map<String, Integer> supplierData = service.getDeliveredVaccinesBetweenDatesPerSupplier(request.getFrom(), request.getTo()); 
		
		Map<String, List<Integer>> dataPerRegion = new HashMap<>();
		regionData.forEach((k, v) -> dataPerRegion.put(k, v.stream().map(VaccinesDelivered::getDosesDelivered).collect(Collectors.toList())));
		
		
		Set<LocalDate> dates = new HashSet<>();
		regionData.forEach((k, v) -> v.stream().forEach(o -> dates.add(o.getDate())));
		
		
		List<LocalDate> list = Arrays.asList(dates.toArray(new LocalDate[0]));
		Collections.sort(list);
		
		resp.setDataPerRegion(dataPerRegion);
		resp.setDataPerSupplier(supplierData);
		resp.setArrDates(list);
		resp.setStatus(true);

		return resp;
	}
}
