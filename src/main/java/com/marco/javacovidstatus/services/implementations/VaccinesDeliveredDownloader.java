package com.marco.javacovidstatus.services.implementations;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.model.dto.VaccinesDeliveredDto;
import com.marco.javacovidstatus.services.interfaces.CovidDataDownloader;
import com.marco.javacovidstatus.services.interfaces.VaccineDateService;
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
	private VaccineDateService dataService;

	private static final Logger _LOGGER = LoggerFactory.getLogger(VaccinesDeliveredDownloader.class);

	private static final String CVS_URL = "https://raw.githubusercontent.com/italia/covid19-opendata-vaccini/master/dati/consegne-vaccini-latest.csv";

	public VaccinesDeliveredDownloader(WebClient webClient) {
		super(webClient);
	}

	@Override
	public void downloadData() {
		_LOGGER.info("Downloading delivered vaccines data");

		LocalDate startDate = getStartDate();

		List<String> rows = this.getCsvRows(CVS_URL);
		rows.stream().forEach(row -> {
			String[] columns = row.split(",");

			String regionCode = columns[6];
			String areaCode = columns[0];
			if (areaCode.equals("PAB")) {
				regionCode = "21";
			} else if (areaCode.equals("PAT")) {
				regionCode = "22";
			}

			LocalDate date = DateUtils.fromStringToLocalDate(columns[3], DateFormats.DB_DATE);
			if (!date.isAfter(startDate)) {
				return;
			}

			VaccinesDeliveredDto data = new VaccinesDeliveredDto();
			data.setDate(date);
			data.setDosesDelivered(Integer.parseInt(columns[2]));
			data.setRegionCode(("00" + regionCode).substring(regionCode.length()));
			data.setSupplier(columns[1]);

			_LOGGER.debug(String.format("Storing Delivered date for Date: %s Region: %s Supplier: %s", data.getDate(), data.getRegionCode(), data.getSupplier()));
			dataService.saveVaccinesDeliveredData(data);
		});

		dataService.addMissingRowsForNoDeliveryDays();
	}

	@Override
	public LocalDate getStartDate() {
		LocalDate date = dataService.getVaccineDeliveredLastUpdateDate();
		if (date == null) {
			date = this.defaultStartData;
		}
		return date;
	}

}
