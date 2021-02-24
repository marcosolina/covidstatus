package com.marco.javacovidstatus.services.implementations;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.marco.javacovidstatus.model.dto.VaccinesDelivered;
import com.marco.javacovidstatus.model.entitites.EntityVacciniConsegne;
import com.marco.javacovidstatus.repositories.interfaces.VeccinesDeliveredRepo;
import com.marco.javacovidstatus.services.interfaces.VaccineDateService;

public class VaccineDateServiceMarco implements VaccineDateService{

	@Autowired
	private VeccinesDeliveredRepo repo;
	
	@Override
	public List<VaccinesDelivered> getDeliveredVaccinesBetweenDates(LocalDate start, LocalDate end) {
		return repo.getDeliveredVaccinesBetween(start, end).stream().map(this::fromVaccinesDeliveredToEntityVacciniConsegne).collect(Collectors.toList());
	}
	
	private VaccinesDelivered fromVaccinesDeliveredToEntityVacciniConsegne(EntityVacciniConsegne entity) {
		VaccinesDelivered dto = new VaccinesDelivered();
		dto.setDate(entity.getId().getDate());
		dto.setDosesDelivered(entity.getDosesDelivered());
		dto.setRegionCode(entity.getId().getRegionCode());
		dto.setSupplier(entity.getId().getSupplier());
		return dto;
	}

}
