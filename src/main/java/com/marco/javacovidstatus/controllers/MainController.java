package com.marco.javacovidstatus.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.marco.javacovidstatus.services.interfaces.NationalDataService;

@Controller
public class MainController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private NationalDataService service;

    @GetMapping(value = "/")
    public String homePage(Model model, @Param("from") String from, @Param("to") String to) {
        LOGGER.trace("Inside MainController.homePage");
        
        if(from != null && to != null) {
            LocalDate start = LocalDate.parse(from, DateTimeFormatter.ofPattern("yyyyMMdd"));
            LocalDate end = LocalDate.parse(to, DateTimeFormatter.ofPattern("yyyyMMdd"));
            model.addAttribute("data", service.getDatesInRangeAscending(start, end));
            model.addAttribute("from", start.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            model.addAttribute("to", end.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }else {
            model.addAttribute("data", service.getAllDataAscending());
            model.addAttribute("from", "");
            model.addAttribute("to", "");
        }
        
        return "index";
    }
}
