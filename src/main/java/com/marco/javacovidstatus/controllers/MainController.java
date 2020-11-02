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
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.marco.javacovidstatus.model.DailyData;
import com.marco.javacovidstatus.services.interfaces.NationalDataService;

@Controller
public class MainController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private NationalDataService service;

    @GetMapping(value = "/")
    public String homePage(Model model, @Param("from") String from, @Param("to") String to) {
        LOGGER.trace("Inside MainController.homePage");
        
        
        
        List<DailyData> listData = null;
        
        if(from != null && to != null) {
            LocalDate start = LocalDate.parse(from, DateTimeFormatter.ofPattern("yyyyMMdd"));
            LocalDate end = LocalDate.parse(to, DateTimeFormatter.ofPattern("yyyyMMdd"));
            listData = service.getDatesInRangeAscending(start, end);
            model.addAttribute("from", start.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            model.addAttribute("to", end.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }else {
            listData = service.getAllDataAscending();
            model.addAttribute("from", "");
            model.addAttribute("to", "");
        }
        
        
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("arrDates",                 listData.stream().map(DailyData::getDate).collect(Collectors.toList()));
        dataMap.put("arrPercInfections",        listData.stream().map(DailyData::getInfectionPercentage).collect(Collectors.toList()));
        dataMap.put("arrNewTests",              listData.stream().map(DailyData::getNewTests).collect(Collectors.toList()));
        dataMap.put("arrNewInfections",         listData.stream().map(DailyData::getNewInfections).collect(Collectors.toList()));
        dataMap.put("arrNewCasualties",         listData.stream().map(DailyData::getNewCasualties).collect(Collectors.toList()));
        dataMap.put("arrPercCasualties",        listData.stream().map(DailyData::getCasualtiesPercentage).collect(Collectors.toList()));
        dataMap.put("arrNewHospitalized",       listData.stream().map(DailyData::getNewHospitalized).collect(Collectors.toList()));
        dataMap.put("arrNewIntensiveTherapy",   listData.stream().map(DailyData::getNewIntensiveTherapy).collect(Collectors.toList()));
        dataMap.put("arrNewRecovered",          listData.stream().map(DailyData::getNewRecovered).collect(Collectors.toList()));
        
        model.addAttribute("data", dataMap);
        
        return "index";
    }
}
