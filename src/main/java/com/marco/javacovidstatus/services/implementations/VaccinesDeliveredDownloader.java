package com.marco.javacovidstatus.services.implementations;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.model.entitites.EntityVacciniConsegne;
import com.marco.javacovidstatus.model.entitites.EntityVacciniConsegnePk;
import com.marco.javacovidstatus.repositories.interfaces.VeccinesDeliveredRepo;
import com.marco.javacovidstatus.services.interfaces.CovidDataDownloader;
import com.marco.utils.DateUtils;
import com.marco.utils.enums.DateFormats;

/**
 * This class will update the data regarding the delivered vaccines
 * 
 * @author Marco
 *
 */
public class VaccinesDeliveredDownloader extends CovidDataDownloader {
	@Autowired
	private VeccinesDeliveredRepo repo;

	private static final Logger _LOGGER = LoggerFactory.getLogger(VaccinesDeliveredDownloader.class);

	private final String csvUrl = "https://raw.githubusercontent.com/italia/covid19-opendata-vaccini/master/dati/consegne-vaccini-latest.csv";

	public VaccinesDeliveredDownloader(WebClient webClient) {
		super(webClient);
	}

	@Override
	public void downloadData() {
		_LOGGER.info("Downloading delivered vaccines data");
		List<String> rows = this.getCsvRows(csvUrl);
		repo.deleteAll();
		rows.stream().forEach(row -> {
			String[] columns = row.split(",");
			
			EntityVacciniConsegnePk key = new EntityVacciniConsegnePk();
			key.setDate(DateUtils.fromStringToLocalDate(columns[3], DateFormats.DB_DATE));
			String regionCode = columns[6];
			
			String areaCode = columns[0];
			if (areaCode.equals("PAB")) {
				regionCode = "21";
            } else if (areaCode.equals("PAT")) {
            	regionCode = "22";
            }
			
			key.setRegionCode(("00" + regionCode).substring(regionCode.length()));
			key.setSupplier(columns[1]);
			
			EntityVacciniConsegne data = new EntityVacciniConsegne();
			data.setId(key);
			data.setDosesDelivered(Integer.parseInt(columns[2]));
			
			repo.saveEntity(data);
		});
		repo.addMissingRowsForNoDeliveryDays();
	}

	@Override
	public LocalDate getStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

}
