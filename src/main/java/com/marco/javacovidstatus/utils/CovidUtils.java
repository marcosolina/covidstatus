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
	public static final String MAPPING_REFRESH_STATUS = "/refreshStatus";
	public static final String MAPPING_REGION_DATA = "/data/regions";
	public static final String MAPPING_NATIONAL_DATA = "/data/national";
	public static final String MAPPING_NATIONAL_DATA_ALL = "/data/national/all";
	public static final String MAPPING_PROVINCE_DATA = "/data/provinces";

	/*
	 * Vaccines Data controller
	 */
	public static final String MAPPING_VACCINE_DELIVERED_PER_REGION = "/vaccines/delivered/per/region";
	public static final String MAPPING_VACCINE_DELIVERED_PER_SUPPLIER = "/vaccines/delivered/per/supplier";
	public static final String MAPPING_VACCINE_VACCINATED_PEOPLE = "/vaccinated/people/per/type";
	public static final String MAPPING_VACCINE_VACCINATED_PER_AGE = "/vaccinated/people/per/age";
	public static final String MAPPING_VACCINE_VACCINATED_PER_REGION = "/vaccinated/people/per/region";
	public static final String MAPPING_VACCINE_DOSES_DATA = "/vaccinated/given/doses";
	
	public static final String MAPPING_VACCINE_TOTAL_DATA = "/vaccines/total/delivered/used";
	public static final String MAPPING_VACCINE_TOTAL_DATA_PER_REGION = "/vaccines/total/delivered/used/per/region";
	public static final String MAPPING_VACCINE_TOTAL_DATA_PER_AGE = "/vaccinated/total/people/per/age";
	public static final String MAPPING_VACCINE_TOTAL_DELIVERED_PER_SUPPLIER = "/vaccines/total/delivered/per/supplier";

	/**
	 * It returns a map of the available Covid Endpoints
	 * 
	 * @return
	 */
	public Map<String, Map<String, String>> getEndPoints() {
		Map<String, Map<String, String>> map = new HashMap<>();

		Map<String, String> utilsEndPoints = new HashMap<>();
		utilsEndPoints.put("REFRESH_STATUS", contextPath + MAPPING_REFRESH_STATUS);
		map.put("UTILS", utilsEndPoints);
		
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
		
		Map<String, String> totals = new HashMap<>();
		totals.put("TOTALS_DELIVER_USED", contextPath + MAPPING_VACCINE_TOTAL_DATA);
		totals.put("TOTALS_REGION", contextPath + MAPPING_VACCINE_TOTAL_DATA_PER_REGION);
		totals.put("TOTALS_AGE", contextPath + MAPPING_VACCINE_TOTAL_DATA_PER_AGE);
		totals.put("TOTALS_VACCINATED_PER_REGION", contextPath + MAPPING_VACCINE_VACCINATED_PER_REGION);
		totals.put("TOTALS_DELIVERED", contextPath + MAPPING_VACCINE_TOTAL_DELIVERED_PER_SUPPLIER);
		totals.put("TOTALS_INFECTIONS_DATA", contextPath + MAPPING_NATIONAL_DATA_ALL);
		map.put("TOTALS", totals);
		

		return map;
	}
	
	public static Map<String, Integer> getColumnsIndex(String columnsRow){
        Map<String, Integer> map = new HashMap<>();
        String [] columnsNames = columnsRow.split(",");
        
        for(int i = 0; i < columnsNames.length; i++) {
            map.put(columnsNames[i], i);
        }
        
        return map;
    }

}
