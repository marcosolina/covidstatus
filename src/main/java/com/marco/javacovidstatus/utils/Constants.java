package com.marco.javacovidstatus.utils;

/**
 * Constants used in this project
 * 
 * @author Marco
 *
 */
public class Constants {

	private Constants() {
	}

	/**
	 * Constants used to provide the key of the map used to return the data of
	 * vaccinated people
	 */
	public static final String VACCINES_GIVEN_MEN = "uomini";
	public static final String VACCINES_GIVEN_WOMEN = "donne";
	public static final String VACCINES_GIVEN_NHS = "ospedali";
	public static final String VACCINES_GIVEN_NO_NHS = "nonospedali";
	public static final String VACCINES_GIVEN_NURSING = "riposo";
	public static final String VACCINES_GIVEN_AGE_60_69 = "altri6069";
	public static final String VACCINES_GIVEN_AGE_70_79 = "altri7079";
	public static final String VACCINES_GIVEN_OVER_80 = "over";
	public static final String VACCINES_GIVEN_PUBLIC = "polizia";
	public static final String VACCINES_GIVEN_SCHOOLS = "scuole";
	public static final String VACCINES_GIVEN_FRAGILE = "soggettiFragili";
	public static final String VACCINES_GIVEN_OTHER = "altri";
	public static final String VACCINES_GIVEN_FIRST_SHOT = "Prima";
	public static final String VACCINES_GIVEN_SECOND_SHOT = "Seconda";
}
