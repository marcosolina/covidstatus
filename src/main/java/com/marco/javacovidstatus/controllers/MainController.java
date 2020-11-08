package com.marco.javacovidstatus.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.marco.javacovidstatus.model.DailyData;
import com.marco.javacovidstatus.model.ProvinceDailyData;
import com.marco.javacovidstatus.model.rest.ReqGetNationalData;
import com.marco.javacovidstatus.model.rest.ReqGetProvinceData;
import com.marco.javacovidstatus.model.rest.RespGetNationalData;
import com.marco.javacovidstatus.model.rest.RespGetProvinceData;
import com.marco.javacovidstatus.model.rest.RespProvinceChartData;
import com.marco.javacovidstatus.services.interfaces.CovidDataService;

@Controller
public class MainController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private CovidDataService service;

    @GetMapping(value = "/")
    public String homePage(Model model) {
        LOGGER.trace("Inside MainController.homePage");
        LocalDate today = LocalDate.now();
        model.addAttribute("from", today.minusMonths(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("to", today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("regions", service.getRegionsList());
        return "index";
    }
    
    @PostMapping("/getProvinceData")
    @ResponseBody
    public RespGetProvinceData getNationalData(@RequestBody ReqGetProvinceData request) {
        RespGetProvinceData resp = new RespGetProvinceData();
        
        /*
         * Get all the data for the provinces of the specific region
         */
        List<ProvinceDailyData> listData = service.getProvinceDataInRangeAscending(request.getFrom(), request.getTo(), request.getRegionCode());
        
        List<String> provinces = service.getProfinceListForRegion(request.getRegionCode());

        Map<String, RespProvinceChartData> map = new HashMap<>();
        
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
        resp.setArrDates(listData.stream().filter(p -> p.getProvinceCode().equals(province)).map(ProvinceDailyData::getDate).collect(Collectors.toList()));
        resp.setProvinceData(map);
        resp.setStatus(true);
        return resp;
    }
    
    @PostMapping("/getNationalData")
    @ResponseBody
    public RespGetNationalData getNationalData(@RequestBody ReqGetNationalData request) {
        RespGetNationalData resp = new RespGetNationalData();

        List<DailyData> listData = service.getDatesInRangeAscending(request.getFrom(), request.getTo());
        resp.setArrDates(listData.stream().map(DailyData::getDate).collect(Collectors.toList()));
        resp.setArrNewCasualties(listData.stream().map(DailyData::getNewCasualties).collect(Collectors.toList()));
        resp.setArrNewHospitalized(listData.stream().map(DailyData::getNewHospitalized).collect(Collectors.toList()));
        resp.setArrNewInfections(listData.stream().map(DailyData::getNewInfections).collect(Collectors.toList()));
        resp.setArrNewIntensiveTherapy(listData.stream().map(DailyData::getNewIntensiveTherapy).collect(Collectors.toList()));
        resp.setArrNewRecovered(listData.stream().map(DailyData::getNewRecovered).collect(Collectors.toList()));
        resp.setArrNewTests(listData.stream().map(DailyData::getNewTests).collect(Collectors.toList()));
        resp.setArrPercCasualties(listData.stream().map(DailyData::getCasualtiesPercentage).collect(Collectors.toList()));
        resp.setArrPercInfections(listData.stream().map(DailyData::getInfectionPercentage).collect(Collectors.toList()));
        
        resp.setStatus(true);
        return resp;
    }
}
