package com.marco.javacovidstatus.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
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
		
		List<VaccinesDelivered> data = service.getDeliveredVaccinesBetweenDates(request.getFrom(), request.getTo());
		
		Set<LocalDate> dates = new HashSet<>();
		data.stream().map(VaccinesDelivered::getDate).forEach(dates::add);
		resp.setArrDates(dates.stream().sorted().collect(Collectors.toList()));
		
		Map<String, List<VaccinesDelivered>> dataMap = new HashMap<>();
		data.stream().forEach(d -> dataMap.computeIfAbsent(d.getSupplier(), k -> new ArrayList<>()).add(d));
		resp.setDataMap(dataMap);
		
		resp.setStatus(true);
		
		return resp;
	}
}
