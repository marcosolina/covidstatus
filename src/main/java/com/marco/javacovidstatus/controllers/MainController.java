package com.marco.javacovidstatus.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.marco.javacovidstatus.model.dto.CovidDataType;
import com.marco.javacovidstatus.model.dto.NationalDailyData;
import com.marco.javacovidstatus.model.dto.ProvinceDailyData;
import com.marco.javacovidstatus.model.dto.Region;
import com.marco.javacovidstatus.model.dto.RegionalDailyData;
import com.marco.javacovidstatus.model.rest.ReqGetNationalData;
import com.marco.javacovidstatus.model.rest.ReqGetProvinceData;
import com.marco.javacovidstatus.model.rest.ReqGetRegionData;
import com.marco.javacovidstatus.model.rest.RespGetNationalData;
import com.marco.javacovidstatus.model.rest.RespGetProvinceData;
import com.marco.javacovidstatus.model.rest.RespGetRegionData;
import com.marco.javacovidstatus.model.rest.RespProvinceChartData;
import com.marco.javacovidstatus.model.rest.RespRegionChartData;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;
import com.marco.javacovidstatus.services.interfaces.RegionMapDownloader;

/**
 * This is a standard Spring controller
 * 
 * @author Marco
 *
 */
@Controller
public class MainController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${covidstatus.version}")
    private String appVersion;
    public static final String MAPPING_HOME = "/";
    public static final String MAPPING_REGION_DATA = "/regiondata";
    public static final String MAPPING_NATIONAL_DATA = "/nationaldata";
    public static final String MAPPING_PROVINCE_DATA = "/provincedata";
    
    @Autowired
    private CovidDataService service;
    
    @Autowired
    private RegionMapDownloader regionMapDownloader;

    @GetMapping(value = MAPPING_HOME)
    public String homePage(Model model) {
        LOGGER.info("Inside MainController.homePage");
        
        /*
         * By default I will show the data of the last month to the user
         */
        LocalDate today = LocalDate.now();
        model.addAttribute("from", today.minusMonths(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("to", today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        /*
         * Preparing the data for the 2 drop down
         */
        EnumMap<CovidDataType, String> mapCovidDataType = new EnumMap<>(CovidDataType.class);
        Arrays.asList(CovidDataType.values()).stream().forEach(c -> mapCovidDataType.put(c, c.getDescription()));
        model.addAttribute("covidDataType", mapCovidDataType);
        model.addAttribute("regions", service.getRegionsList());
        
        /*
         * Adding the SVG map
         */
        model.addAttribute("svgmap", regionMapDownloader.createHtmlMap());
        
        /*
         * Passing to the front end the map of available end points
         */
        Map<String, String> mapUrls = new HashMap<>();
        mapUrls.put("URL_REGION_DATA", contextPath + MAPPING_REGION_DATA);
        mapUrls.put("URL_NATIONAL_DATA", contextPath + MAPPING_NATIONAL_DATA);
        mapUrls.put("URL_PROVINCE_DATA", contextPath + MAPPING_PROVINCE_DATA);
        mapUrls.put("URL_VACCINES_DELIVERED_DATA", contextPath + VaccinesController.MAPPING_VACCINE_DELIVERED_DATA);
        model.addAttribute("urls", mapUrls);
        
        model.addAttribute("appVersion", appVersion);
        
        return "index";
    }

    @PostMapping(value = MAPPING_REGION_DATA)
    @ResponseBody
    public RespGetRegionData getRegionalData(@RequestBody ReqGetRegionData request) {
        RespGetRegionData resp = new RespGetRegionData();

        List<RegionalDailyData> listData = service.getRegionalDatesInRangeAscending(request.getFrom(), request.getTo());
        List<Region> listRegions = service.getRegionsList();

        if (!listData.isEmpty()) {
            listRegions.stream().forEach(r -> {
                RespRegionChartData chartData = new RespRegionChartData();
                chartData.setLabel(r.getDesc());

                Function<RegionalDailyData, Object> mapper = null;

                switch (request.getCovidData()) {
                case NEW_TESTS:
                    mapper = RegionalDailyData::getNewTests;
                    break;
                case CASUALTIES:
                    mapper = RegionalDailyData::getNewCasualties;
                    break;
                case NEW_HOSPITALISED:
                    mapper = RegionalDailyData::getNewHospitalized;
                    break;
                case NEW_INFECTIONS:
                    mapper = RegionalDailyData::getNewInfections;
                    break;
                case NEW_INTENSIVE_THERAPY:
                    mapper = RegionalDailyData::getNewIntensiveTherapy;
                    break;
                case NEW_RECOVER:
                    mapper = RegionalDailyData::getNewRecovered;
                    break;
                case PERC_CASUALTIES:
                    mapper = RegionalDailyData::getCasualtiesPercentage;
                    break;
                case PERC_INFECTIONS:
                    mapper = RegionalDailyData::getInfectionPercentage;
                    break;
                }

                chartData.setData(listData.stream().filter(data -> data.getRegionCode().equals(r.getCode())).map(mapper)
                        .collect(Collectors.toList()));
                resp.getRegionData().put(r.getCode(), chartData);
            });

            Region region = listRegions.get(0);
            resp.setArrDates(listData.stream().filter(r -> r.getRegionCode().equals(region.getCode()))
                    .map(RegionalDailyData::getDate).collect(Collectors.toList()));
        }

        resp.setStatus(true);
        return resp;
    }

    @PostMapping(value = MAPPING_PROVINCE_DATA)
    @ResponseBody
    public RespGetProvinceData getNationalData(@RequestBody ReqGetProvinceData request) {
        RespGetProvinceData resp = new RespGetProvinceData();

        /*
         * Get all the data for the provinces of the specific region
         */
        List<ProvinceDailyData> listData = service.getProvinceDataInRangeAscending(request.getFrom(), request.getTo(),
                request.getRegionCode());
        List<String> provinces = service.getProfinceListForRegion(request.getRegionCode());
        
        Map<String, RespProvinceChartData> map = new HashMap<>();
        if (!listData.isEmpty()) {
            // @formatter:off
            
            provinces.stream().forEach(codiceProvincia -> {
                /*
                 * Get the data for the specific province
                 */
                List<ProvinceDailyData> provinceData = listData.stream().filter(data -> data.getProvinceCode().equals(codiceProvincia)).collect(Collectors.toList());
                
                RespProvinceChartData data = new RespProvinceChartData();
                data.setLabel(provinceData.get(0).getDescription());
                data.setNewInfections(provinceData.stream().map(ProvinceDailyData::getNewInfections).collect(Collectors.toList()));
                
                map.put(codiceProvincia, data);
            });
            // @formatter:on
            String province = provinces.get(0);
            resp.setArrDates(listData.stream().filter(p -> p.getProvinceCode().equals(province))
                    .map(ProvinceDailyData::getDate).collect(Collectors.toList()));
            resp.setProvinceData(map);
        }
        resp.setStatus(true);
        return resp;
    }

    @PostMapping(value = MAPPING_NATIONAL_DATA)
    @ResponseBody
    public RespGetNationalData getNationalData(@RequestBody ReqGetNationalData request) {
        RespGetNationalData resp = new RespGetNationalData();

        List<NationalDailyData> listData = service.getDatesInRangeAscending(request.getFrom(), request.getTo());
        resp.setArrDates(listData.stream().map(NationalDailyData::getDate).collect(Collectors.toList()));
        resp.setArrNewCasualties(listData.stream().map(NationalDailyData::getNewCasualties).collect(Collectors.toList()));
        resp.setArrNewHospitalized(listData.stream().map(NationalDailyData::getNewHospitalized).collect(Collectors.toList()));
        resp.setArrNewInfections(listData.stream().map(NationalDailyData::getNewInfections).collect(Collectors.toList()));
        resp.setArrNewIntensiveTherapy(listData.stream().map(NationalDailyData::getNewIntensiveTherapy).collect(Collectors.toList()));
        resp.setArrNewRecovered(listData.stream().map(NationalDailyData::getNewRecovered).collect(Collectors.toList()));
        resp.setArrNewTests(listData.stream().map(NationalDailyData::getNewTests).collect(Collectors.toList()));
        resp.setArrPercCasualties(listData.stream().map(NationalDailyData::getCasualtiesPercentage).collect(Collectors.toList()));
        resp.setArrPercInfections(listData.stream().map(NationalDailyData::getInfectionPercentage).collect(Collectors.toList()));

        resp.setStatus(true);
        return resp;
    }
}
