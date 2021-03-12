package com.marco.javacovidstatus.services.implementations;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.model.entitites.EntitySomministrazioneVaccini;
import com.marco.javacovidstatus.model.entitites.EntitySomministrazioneVacciniPk;
import com.marco.javacovidstatus.repositories.interfaces.GivenVaccinesRepo;
import com.marco.javacovidstatus.services.interfaces.CovidDataDownloader;
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
	private GivenVaccinesRepo repo;

	private static final Logger _LOGGER = LoggerFactory.getLogger(VaccinesGivenDownloader.class);

	private final String csvUrl = "https://raw.githubusercontent.com/italia/covid19-opendata-vaccini/master/dati/somministrazioni-vaccini-latest.csv";

	public VaccinesGivenDownloader(WebClient webClient) {
		super(webClient);
	}

	@Override
	public void downloadData() {
		_LOGGER.info("Downloading Given vaccines data");
		
		_LOGGER.trace("Downloading Given vaccines data");
		List<String> rows = this.getCsvRows(csvUrl);
		_LOGGER.trace("Given vaccines data downloaded");
		
		_LOGGER.trace("Clearing given vaccines table");
		repo.deleteAll();
		rows.stream().forEach(row -> {
			String[] columns = row.split(",");
			
			EntitySomministrazioneVacciniPk key = new EntitySomministrazioneVacciniPk();
			key.setDate(DateUtils.fromStringToLocalDate(columns[0], DateFormats.DB_DATE));
			String regionCode = columns[16];
			
			String areaCode = columns[2];
			if (areaCode.equals("PAB")) {
				regionCode = "21";
            } else if (areaCode.equals("PAT")) {
            	regionCode = "22";
            }
			
			key.setRegionCode(("00" + regionCode).substring(regionCode.length()));
			key.setSupplier(columns[1]);
			key.setAgeRange(columns[3]);
			
			EntitySomministrazioneVaccini data = new EntitySomministrazioneVaccini();
			data.setId(key);
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
			
			_LOGGER.trace(String.format("Storing Given vaccine data date: %s Region: %s AgeRange: %s Supplier: %s", key.getDate(), key.getRegionCode(), key.getAgeRange(), key.getSupplier()));
			repo.saveEntity(data);
		});
		_LOGGER.trace("Given vaccine data stored");
		
		repo.addMissingRowsForNoVaccinationDays();
	}

	@Override
	public LocalDate getStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

}
