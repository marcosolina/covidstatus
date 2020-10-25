package com.marco.javacovidstatus.services.implementations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.services.interfaces.CronServiceInterface;


@Configuration
@EnableScheduling
public class CronServiceGit implements CronServiceInterface{
	private static final Logger logger = LoggerFactory.getLogger(CronServiceGit.class);
	
	@Autowired
	private WebClient webClient;
	private String url = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-andamento-nazionale/dpc-covid19-ita-andamento-nazionale-%s.csv";
	
	
	@Scheduled(cron = "0 * * * * *")
	@Override
	public void getCovidNationalData() {
		
		logger.debug("Recupero i dati di oggi");
		LocalDate oggi = LocalDate.now();
		DatiNazionali datiDiOggi = getDatiAllaData(oggi);
		
		logger.debug("Recupero i dati di ieri");
		DatiNazionali datiDiIeri = getDatiAllaData(oggi.minusDays(1));
		
		
		int deltaTamponi = datiDiOggi.nuoviTamponi - datiDiIeri.nuoviTamponi;
		int deltaInfetti = datiDiOggi.nuoviInfetti - datiDiIeri.nuoviInfetti;
		
		
		logger.info("Oggi ci sono stati:");
		logger.info(String.format("%d nuovi decessi", datiDiOggi.nuoviDecessi - datiDiIeri.nuoviDecessi));
		logger.info(String.format("%d nuovi infetti", deltaInfetti));
		logger.info(String.format("%d nuovi tamponi", deltaTamponi));
		
		LocalDate start = LocalDate.of(2020, 2, 24);
		LocalDate end = oggi;
		while(start.isBefore(end)) {
			start = start.plusDays(1);
			DatiNazionali precedente = getDatiAllaData(start.minusDays(1));
			DatiNazionali corrente = getDatiAllaData(start);
			
			BigDecimal result = new BigDecimal(percentualeInfetti(corrente, precedente)).setScale(2, RoundingMode.DOWN);
			
			logger.info(String.format("Percentuale degli infetti alla data: %s e' di: %.2f%%", start.toString(), result.floatValue()));
		}
	}
	
	

	
	private DatiNazionali getDatiAllaData(LocalDate data) {
		logger.trace("Sono dentro CronServiceGit.getDatiAllaData");
		
		String uriFile = String.format(url, data.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
		ClientResponse response = webClient.get().uri(uriFile).exchange().block();
		
		String csv = response.bodyToMono(String.class).block();
		String [] tmp = csv.split("\\n");
		String [] values = tmp[1].split(",");
		
		
		DatiNazionali dati = new DatiNazionali();
		dati.nuoviDecessi = Integer.parseInt(values[10]);
		dati.nuoviTamponi = Integer.parseInt(values[14]);
		dati.nuoviInfetti = Integer.parseInt(values[13]);
		
		return dati;
	}
	
	
	private double percentualeInfetti(DatiNazionali corrente, DatiNazionali precedente) {
		int deltaTamponi = corrente.nuoviTamponi - precedente.nuoviTamponi;
		int deltaInfetti = corrente.nuoviInfetti - precedente.nuoviInfetti;
		return ((double)deltaInfetti / deltaTamponi) * 100;
	}
	
	class DatiNazionali {
		int nuoviInfetti;
		int nuoviTamponi;
		int nuoviDecessi;
	}

}
