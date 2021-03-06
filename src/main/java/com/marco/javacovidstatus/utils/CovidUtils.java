package com.marco.javacovidstatus.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

public class CovidUtils {
	@Value("${server.servlet.context-path}")
	private String contextPath;

	/*
	 * Main controller paths
	 */
	public static final String MAPPING_HOME = "/";
	public static final String MAPPING_REGION_DATA = "/regiondata";
	public static final String MAPPING_NATIONAL_DATA = "/nationaldata";
	public static final String MAPPING_PROVINCE_DATA = "/provincedata";

	/*
	 * Vaccines Data controller
	 */
	public static final String MAPPING_VACCINE_DELIVERED_PER_REGION = "/vaccineperregion";
	public static final String MAPPING_VACCINE_DELIVERED_PER_SUPPLIER = "/vaccinepersupplier";
	public static final String MAPPING_VACCINE_VACCINATED_PEOPLE = "/vaccinepeople";
	public static final String MAPPING_VACCINE_VACCINATED_PER_AGE = "/vaccineperage";
	public static final String MAPPING_VACCINE_DOSES_DATA = "/vaccinesdoses";

	/**
	 * It returns a map of the available Covid Endpoints
	 * 
	 * @return
	 */
	public Map<String, Map<String, String>> getEndPoints() {
		Map<String, Map<String, String>> map = new HashMap<>();

		Map<String, String> infectionEndPoints = new HashMap<>();
		infectionEndPoints.put("REGION_DATA", contextPath + MAPPING_REGION_DATA);
		infectionEndPoints.put("NATIONAL_DATA", contextPath + MAPPING_NATIONAL_DATA);
		infectionEndPoints.put("PROVINCE_DATA", contextPath + MAPPING_PROVINCE_DATA);
		map.put("INFECTIONS", infectionEndPoints);

		Map<String, String> vaccinesEndPoints = new HashMap<>();
		vaccinesEndPoints.put("PER_REGION", contextPath + MAPPING_VACCINE_DELIVERED_PER_REGION);
		vaccinesEndPoints.put("PER_SUPPLIER", contextPath + MAPPING_VACCINE_DELIVERED_PER_SUPPLIER);
		vaccinesEndPoints.put("PEOPlE", contextPath + MAPPING_VACCINE_VACCINATED_PEOPLE);
		vaccinesEndPoints.put("AGE", contextPath + MAPPING_VACCINE_VACCINATED_PER_AGE);
		vaccinesEndPoints.put("DOSE", contextPath + MAPPING_VACCINE_DOSES_DATA);
		map.put("VACCINES", vaccinesEndPoints);

		return map;
	}

}
