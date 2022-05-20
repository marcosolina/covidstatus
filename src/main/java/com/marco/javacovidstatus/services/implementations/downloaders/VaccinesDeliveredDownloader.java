package com.marco.javacovidstatus.services.implementations.downloaders;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.model.dto.VaccinesDeliveredDto;
import com.marco.javacovidstatus.services.interfaces.NotificationSenderInterface;
import com.marco.javacovidstatus.services.interfaces.VaccineDataService;
import com.marco.javacovidstatus.services.interfaces.downloaders.CovidDataDownloader;
import com.marco.javacovidstatus.utils.CovidUtils;
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
	private VaccineDataService dataService;
	@Autowired
    private NotificationSenderInterface notificationService;

	private static final Logger _LOGGER = LoggerFactory.getLogger(VaccinesDeliveredDownloader.class);

	private static final String CVS_URL = "https://raw.githubusercontent.com/italia/covid19-opendata-vaccini/master/dati/consegne-vaccini-latest.csv";
	public static final String COL_DATE = "data_consegna";
	public static final String COL_DELIVERED_DOSES = "numero_dosi";
	public static final String COL_AREA_CODE = "area";
	public static final String COL_REGION_CODE = "ISTAT";
	public static final String COL_SUPPLIER = "forn";

	public VaccinesDeliveredDownloader(WebClient webClient) {
		super(webClient);
	}

	@Override
	public boolean downloadData() {
		_LOGGER.info("Downloading delivered vaccines data");
		
		List<String> rows = this.getCsvRows(CVS_URL, null, false);
		
		if(rows.isEmpty()) {
			notificationService.sendEmailMessage("marcosolina@gmail.com", "Marco Solina - Covid Status", "Non ci sono più i dati nel repository");
			return false;
		}
		
		Map<String, Integer> columnsPositions = CovidUtils.getColumnsIndex(rows.get(0));
		rows.remove(0);
		
		if(columnsPositions.size() != 8) {
			notificationService.sendEmailMessage("marcosolina@gmail.com", "Marco Solina - Covid Status", "La struttura dei dati delle consegne dei vaccini e' stata modificata...");
			return false;
		}
		

		/*
		 * Forcing the re-loading of all the data. I notice that the government updates
		 * the data of the previous days. There is no way for me to understand
		 * "how much back" they go... so I decided to clear the table at every scan
		 */
		dataService.deleteAllVaccinesDeliveredData();
		
		LocalDate startDate = getStartDate();
		AtomicBoolean error = new AtomicBoolean();

		rows.stream().forEach(row -> {
			try {
				String[] columns = row.split(",");

				String regionCode = columns[columnsPositions.get(COL_REGION_CODE)];
				String areaCode = columns[columnsPositions.get(COL_AREA_CODE)];
				if (areaCode.equals("PAB")) {
					regionCode = "21";
				} else if (areaCode.equals("PAT")) {
					regionCode = "22";
				}

				LocalDate date = DateUtils.fromStringToLocalDate(columns[columnsPositions.get(COL_DATE)], DateFormats.DB_DATE);
				if (!date.isAfter(startDate)) {
					return;
				}

				VaccinesDeliveredDto data = new VaccinesDeliveredDto();
				data.setDate(date);
				data.setDosesDelivered(Integer.parseInt(columns[columnsPositions.get(COL_DELIVERED_DOSES)]));
				data.setRegionCode(("00" + regionCode).substring(regionCode.length()));
				data.setSupplier(columns[columnsPositions.get(COL_SUPPLIER)]);

				_LOGGER.trace(String.format("Storing Delivered date for Date: %s Region: %s Supplier: %s",
						data.getDate(), data.getRegionCode(), data.getSupplier()));
				dataService.saveVaccinesDeliveredData(data);
			} catch (Exception e) {
				String message = String.format("Error while saving: %s", row);
				_LOGGER.error(message);
				error.set(true);
			}
		});

		if (error.get()) {
			String message = "There was an error with the data, cleaning everything and retrying at the next cron tick";
			_LOGGER.error(message);
			notificationService.sendEmailMessage("marcosolina@gmail.com", "Marco Solina - Covid Status", message);
			dataService.deleteAllVaccinesDeliveredData();
		}

		dataService.addMissingRowsForNoDeliveryDays();
		return !error.get();
	}

	@Override
	public LocalDate getStartDate() {
		LocalDate date = dataService.getLastDateOfAvailableDataForDeliveredVaccines();
		if (date == null) {
			date = this.defaultStartData;
		}
		return date;
	}
	
	

}
