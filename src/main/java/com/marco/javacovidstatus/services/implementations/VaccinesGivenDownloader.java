package com.marco.javacovidstatus.services.implementations;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.model.dto.VaccinatedPeopleDto;
import com.marco.javacovidstatus.services.interfaces.CovidDataDownloader;
import com.marco.javacovidstatus.services.interfaces.VaccineDataService;
import com.marco.utils.DateUtils;
import com.marco.utils.enums.DateFormats;

/**
 * This class will update the data regarding the given vaccines. (People
 * vaccinated)
 * 
 * @author Marco
 *
 */
public class VaccinesGivenDownloader extends CovidDataDownloader {
	@Autowired
	private VaccineDataService dataService;

	private static final Logger _LOGGER = LoggerFactory.getLogger(VaccinesGivenDownloader.class);

	private static final String CSV_URL = "https://raw.githubusercontent.com/italia/covid19-opendata-vaccini/master/dati/somministrazioni-vaccini-latest.csv";
	public static final int COL_DATE = 0;
	public static final int COL_REGION_CODE = 17;
	public static final int COL_AREA_CODE = 2;
	public static final int COL_SUPPLIER = 1;
	public static final int COL_AGE_RANGE = 3;
	public static final int COL_MEN_COUNTER = 4;
	public static final int COL_WOMEN_COUNTER = 5;
	public static final int COL_NHS_COUNTER = 6;
	public static final int COL_NON_NHS_COUNTER = 7;
	public static final int COL_NURSING_COUNTER = 8;
	public static final int COL_OVER_80_COUNTER = 9;
	public static final int COL_PUBLIC_ORDER_COUNTER = 10;
	public static final int COL_SCHOOL_STAFF_COUNTER = 11;
	public static final int COL_FIRST_DOSE_COUNTER = 13;
	public static final int COL_SECOND_DOSE_COUNTER = 14;

	public VaccinesGivenDownloader(WebClient webClient) {
		super(webClient);
	}

	@Override
	public void downloadData() {
		_LOGGER.info("Downloading Given vaccines data");

		List<String> rows = this.getCsvRows(CSV_URL);
		
		/*
		 * Forcing the re-loading of all the data. I notice that the government updates
		 * the data of the previous days. There is no way for me to understand
		 * "how much back" they go... so I decided to clear the table at every scan
		 */
		dataService.deleteAllGivenVaccineData();

		LocalDate startDate = getStartDate();
		AtomicBoolean error = new AtomicBoolean();

		rows.stream().forEach(row -> {
			try {
				String[] columns = row.split(",");

				String regionCode = columns[COL_REGION_CODE];
				String areaCode = columns[COL_AREA_CODE];
				if (areaCode.equals("PAB")) {
					regionCode = "21";
				} else if (areaCode.equals("PAT")) {
					regionCode = "22";
				}

				LocalDate date = DateUtils.fromStringToLocalDate(columns[COL_DATE], DateFormats.DB_DATE);

				if (!date.isAfter(startDate)) {
					return;
				}

				VaccinatedPeopleDto data = new VaccinatedPeopleDto();
				data.setDate(date);
				data.setRegionCode(("00" + regionCode).substring(regionCode.length()));
				data.setSupplier(columns[COL_SUPPLIER]);
				data.setAgeRange(columns[COL_AGE_RANGE]);
				data.setMenCounter(Integer.parseInt(columns[COL_MEN_COUNTER]));
				data.setWomenCounter(Integer.parseInt(columns[COL_WOMEN_COUNTER]));
				data.setNhsPeopleCounter(Integer.parseInt(columns[COL_NHS_COUNTER]));
				data.setNonNhsPeopleCounter(Integer.parseInt(columns[COL_NON_NHS_COUNTER]));
				data.setNursingHomeCounter(Integer.parseInt(columns[COL_NURSING_COUNTER]));
				data.setOver80Counter(Integer.parseInt(columns[COL_OVER_80_COUNTER]));
				data.setPublicOrderCounter(Integer.parseInt(columns[COL_PUBLIC_ORDER_COUNTER]));
				data.setSchoolStaffCounter(Integer.parseInt(columns[COL_SCHOOL_STAFF_COUNTER]));
				data.setFirstDoseCounter(Integer.parseInt(columns[COL_FIRST_DOSE_COUNTER]));
				data.setSecondDoseCounter(Integer.parseInt(columns[COL_SECOND_DOSE_COUNTER]));

				_LOGGER.trace(String.format("Storing Given vaccine data date: %s Region: %s AgeRange: %s Supplier: %s",
						data.getDate(), data.getRegionCode(), data.getAgeRange(), data.getSupplier()));
				dataService.saveGivenVaccinesData(data);
			} catch (Exception e) {
				_LOGGER.error(String.format("Error while saving: %s", row));
				error.set(true);
			}
		});

		if (error.get()) {
			_LOGGER.error("There was an error with the data, cleaning everything and retrying at the next cron tick");
			dataService.deleteAllGivenVaccineData();
		}

		dataService.addMissingRowsForNoVaccinationDays();
	}

	@Override
	public LocalDate getStartDate() {
		LocalDate date = dataService.getGivenVaccinesLastUpdateDate();
		if (date == null) {
			date = this.defaultStartData;
		}
		return date;
	}

}
