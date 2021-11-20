package com.marco.javacovidstatus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.marco.javacovidstatus.services.implementations.downloaders.NationalCovidDataDownloader;
import com.marco.javacovidstatus.services.implementations.downloaders.ProvinceCoviddataDownloader;
import com.marco.javacovidstatus.services.implementations.downloaders.RegionCovidDataDownloader;
import com.marco.javacovidstatus.services.implementations.downloaders.RegionMapDownloaderFromNationalWebSite;
import com.marco.javacovidstatus.services.implementations.downloaders.VaccinesDeliveredDownloader;
import com.marco.javacovidstatus.services.implementations.downloaders.VaccinesGivenDownloader;
import com.marco.javacovidstatus.services.implementations.downloaders.population.PopulationAdditionalDoseDownloader;
import com.marco.javacovidstatus.services.implementations.downloaders.population.PopulationBoosterDoseDownloader;
import com.marco.javacovidstatus.services.implementations.downloaders.population.PopulationDownloaderExcel;
import com.marco.javacovidstatus.services.implementations.downloaders.population.PopulationGovernmentDownloader;
import com.marco.javacovidstatus.services.implementations.downloaders.population.PopulationIstatDownloader;
import com.marco.javacovidstatus.services.interfaces.downloaders.CovidDataDownloader;
import com.marco.javacovidstatus.services.interfaces.downloaders.RegionMapDownloader;

@Configuration
public class DownloadersConfig {
    
    @Bean
    public WebClient getWebClient() {
        // @formatter:off
        int megaByteNumber = 50;
        return WebClient.builder()
                .exchangeStrategies(
                    ExchangeStrategies.builder()
                        .codecs(configurer -> 
                            configurer.defaultCodecs().maxInMemorySize(megaByteNumber * 1024 *1024)
                        ).build()
                )
                .build();
        // @formatter:on
    }
    
    @Bean
    public RegionMapDownloader getRegionMapDownloader() {
        return new RegionMapDownloaderFromNationalWebSite();
    }
    
    @Bean(name = "National")
    public CovidDataDownloader getNationalCovidDataDownloader() {
        return new NationalCovidDataDownloader(getWebClient());
    }

    @Bean(name = "Province")
    public CovidDataDownloader getProvinceCoviddataDownloader() {
        return new ProvinceCoviddataDownloader(getWebClient());
    }

    @Bean(name = "Region")
    public CovidDataDownloader getRegionCovidDataDownloader() {
        return new RegionCovidDataDownloader(getWebClient());
    }

    @Bean(name = "GivenVaccines")
    public CovidDataDownloader getGivenVaccinesDownloader() {
        return new VaccinesGivenDownloader(getWebClient());
    }

    @Bean(name = "DeliveredVaccines")
    public CovidDataDownloader getDeliveredVaccinesDownloader() {
        return new VaccinesDeliveredDownloader(getWebClient());
    }

    @Bean(name = "IstatPopulation")
    public CovidDataDownloader getPopulationDownloader() {
        return new PopulationIstatDownloader(getWebClient());
    }
    
    @Bean(name = "CsvPopulation")
    public CovidDataDownloader getPopulationLoader() {
        return new PopulationDownloaderExcel(getWebClient());
    }
    
    @Bean(name = "GovernmentPopulation")
    public CovidDataDownloader getGovernmentPopulation() {
        return new PopulationGovernmentDownloader(getWebClient());
    }
    
    @Bean(name = "AdditionalDosePopulation")
    public CovidDataDownloader getPopulationAdditionalDoseDownloader() {
        return new PopulationAdditionalDoseDownloader(getWebClient());
    }
    
    @Bean(name = "BoosterDosePopulation")
    public CovidDataDownloader getPopulationBoosterDoseDownloader() {
        return new PopulationBoosterDoseDownloader(getWebClient());
    }
}
