package com.marco.javacovidstatus.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.EnumMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.marco.javacovidstatus.model.dto.CovidDataType;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;
import com.marco.javacovidstatus.services.interfaces.downloaders.RegionMapDownloader;
import com.marco.javacovidstatus.utils.CovidUtils;

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
		model.addAttribute("regions", service.getRegionsListOrderedByDescription());

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

}
