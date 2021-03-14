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

	public VaccinesGivenDownloader(WebClient webClient) {
		super(webClient);
	}

	@Override
	public void downloadData() {
		_LOGGER.info("Downloading Given vaccines data");

		/*
		 * Forcing the refresh of the last available day data as the goverment might
		 * update the last day data multiple times
		 */
		dataService.deleteGivenVaccineInformation(getStartDate());
		LocalDate startDate = getStartDate();

		List<String> rows = this.getCsvRows(CSV_URL);

		AtomicBoolean error = new AtomicBoolean();

		rows.stream().forEach(row -> {
			try {
				String[] columns = row.split(",");

				String regionCode = columns[16];
				String areaCode = columns[2];
				if (areaCode.equals("PAB")) {
					regionCode = "21";
				} else if (areaCode.equals("PAT")) {
					regionCode = "22";
				}

				LocalDate date = DateUtils.fromStringToLocalDate(columns[0], DateFormats.DB_DATE);

				if (!date.isAfter(startDate)) {
					return;
				}

				VaccinatedPeopleDto data = new VaccinatedPeopleDto();
				data.setDate(date);
				data.setRegionCode(("00" + regionCode).substring(regionCode.length()));
				data.setSupplier(columns[1]);
				data.setAgeRange(columns[3]);
				data.setMenCounter(Integer.parseInt(columns[4]));
				data.setWomenCounter(Integer.parseInt(columns[5]));
				data.setNhsPeopleCounter(Integer.parseInt(columns[6]));
				data.setNonNhsPeopleCounter(Integer.parseInt(columns[7]));
				data.setNursingHomeCounter(Integer.parseInt(columns[8]));
				data.setOver80Counter(Integer.parseInt(columns[9]));
				data.setPublicOrderCounter(Integer.parseInt(columns[10]));
				data.setSchoolStaffCounter(Integer.parseInt(columns[11]));
				data.setFirstDoseCounter(Integer.parseInt(columns[12]));
				data.setSecondDoseCounter(Integer.parseInt(columns[13]));

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
