package com.marco.javacovidstatus.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String homePage(Model model) {
        LOGGER.trace("Inside MainController.homePage");

        model.addAttribute("data", service.getAllDataAscending());
        
        return "index";
    }
}
