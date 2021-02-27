package com.marco.javacovidstatus.services.implementations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.marco.javacovidstatus.model.dto.VaccinesDelivered;
import com.marco.javacovidstatus.model.entitites.EntityVacciniConsegne;
import com.marco.javacovidstatus.repositories.interfaces.VeccinesDeliveredRepo;
import com.marco.javacovidstatus.services.interfaces.VaccineDateService;

public class VaccineDateServiceMarco implements VaccineDateService {

	@Autowired
	private VeccinesDeliveredRepo repo;

	@Override
	public Map<String, List<VaccinesDelivered>> getDeliveredVaccinesBetweenDatesPerRegion(LocalDate start,
			LocalDate end) {

		Map<String, List<VaccinesDelivered>> data = new HashMap<>();
		repo.getDeliveredVaccinesBetween(start, end).stream()
			.forEach(entity -> data.computeIfAbsent(entity.getId().getRegionCode(), k -> new ArrayList<>()).add(fromEntityVacciniConsegneToVaccinesDelivered(entity)));

		return data;
	}

	@Override
	public Map<String, Integer> getDeliveredVaccinesBetweenDatesPerSupplier(LocalDate start,
			LocalDate end) {
		Map<String, Integer> data = new HashMap<>();
		repo.getDeliveredVaccinesBetween(start, end).stream()
			.forEach(entity -> data.compute(entity.getId().getSupplier(), (k, v) -> v == null ? 0 : v + entity.getDosesDelivered()));

		return data;
	}

	private VaccinesDelivered fromEntityVacciniConsegneToVaccinesDelivered(EntityVacciniConsegne entity) {
		VaccinesDelivered dto = new VaccinesDelivered();
		dto.setDate(entity.getId().getDate());
		dto.setDosesDelivered(entity.getDosesDelivered());
		return dto;
	}

}
