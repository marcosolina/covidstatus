package com.marco.javacovidstatus.services.implementations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.marco.javacovidstatus.model.dto.VaccinesDelivered;
import com.marco.javacovidstatus.model.dto.VaccinesGiven;
import com.marco.javacovidstatus.model.entitites.DailySumGivenVaccines;
import com.marco.javacovidstatus.model.entitites.EntitySomministrazioneVaccini;
import com.marco.javacovidstatus.model.entitites.EntityVacciniConsegne;
import com.marco.javacovidstatus.repositories.interfaces.GivenVaccinesRepo;
import com.marco.javacovidstatus.repositories.interfaces.VeccinesDeliveredRepo;
import com.marco.javacovidstatus.services.interfaces.VaccineDateService;
import com.marco.javacovidstatus.utils.Constants;

public class VaccineDateServiceMarco implements VaccineDateService {

	@Autowired
	private VeccinesDeliveredRepo repoDelivered;
	@Autowired
	private GivenVaccinesRepo repoGiven;

	@Override
	public Map<String, List<VaccinesDelivered>> getDeliveredVaccinesBetweenDatesPerRegion(LocalDate start,
			LocalDate end) {

		Map<String, List<VaccinesDelivered>> data = new HashMap<>();
		repoDelivered.getDeliveredVaccinesBetween(start, end).stream()
				.forEach(entity -> data.computeIfAbsent(entity.getId().getRegionCode(), k -> new ArrayList<>())
						.add(fromEntityVacciniConsegneToVaccinesDelivered(entity)));

		return data;
	}

	@Override
	public Map<String, Integer> getDeliveredVaccinesBetweenDatesPerSupplier(LocalDate start, LocalDate end) {
		Map<String, Integer> data = new HashMap<>();
		repoDelivered.getDeliveredVaccinesBetween(start, end).stream()
				.forEach(entity -> data.compute(entity.getId().getSupplier(),
						(k, v) -> v == null ? entity.getDosesDelivered() : v + entity.getDosesDelivered()));

		return data;
	}

	@Override
	public Map<String, List<Long>> getVaccinatedPeopleBetweenDates(LocalDate start, LocalDate end) {
		List<DailySumGivenVaccines> list = repoGiven.getDailySumGivenVaccinesBetween(start, end);

		Map<String, List<Long>> dataMap = new HashMap<>();
		list.forEach(dto -> {
			dataMap.computeIfAbsent(Constants.VACCINES_GIVEN_MEN, k -> new ArrayList<>()).add(dto.getMenCounter());
			dataMap.computeIfAbsent(Constants.VACCINES_GIVEN_WOMEN, k -> new ArrayList<>()).add(dto.getWomenCounter());
			dataMap.computeIfAbsent(Constants.VACCINES_GIVEN_NHS, k -> new ArrayList<>())
					.add(dto.getNhsPeopleCounter());
			dataMap.computeIfAbsent(Constants.VACCINES_GIVEN_NO_NHS, k -> new ArrayList<>())
					.add(dto.getNonNhsPeopleCounter());
			dataMap.computeIfAbsent(Constants.VACCINES_GIVEN_NURSING, k -> new ArrayList<>())
					.add(dto.getNursingHomeCounter());
			dataMap.computeIfAbsent(Constants.VACCINES_GIVEN_OVER_80, k -> new ArrayList<>())
					.add(dto.getOver80Counter());
			dataMap.computeIfAbsent(Constants.VACCINES_GIVEN_PUBLIC, k -> new ArrayList<>())
					.add(dto.getPublicOrderCounter());
			dataMap.computeIfAbsent(Constants.VACCINES_GIVEN_SCHOOLS, k -> new ArrayList<>())
					.add(dto.getSchoolStaffCounter());
		});

		return dataMap;
	}

	@Override
	public Map<String, Integer> getGiveShotNumberBetweenDates(LocalDate start, LocalDate end) {
		List<VaccinesGiven> list = repoGiven.getGivenVaccinesBetween(start, end).stream()
				.map(this::fromEntitySomministrazioneVacciniToVaccinesGiven).collect(Collectors.toList());

		Map<String, Integer> map = new HashMap<>();
		list.forEach(dto -> {
			int firstShot = dto.getFirstDoseCounter();
			int secondShot = dto.getSecondDoseCounter();
			map.compute(Constants.VACCINES_GIVEN_FIRST_SHOT, (k, v) -> v == null ? firstShot : v + firstShot);
			map.compute(Constants.VACCINES_GIVEN_SECOND_SHOT, (k, v) -> v == null ? secondShot : v + secondShot);
		});

		return map;
	}

	@Override
	public Map<String, Integer> getVaccinatedAgeRangeBetweenDates(LocalDate start, LocalDate end) {
		List<VaccinesGiven> list = repoGiven.getGivenVaccinesBetween(start, end).stream()
				.map(this::fromEntitySomministrazioneVacciniToVaccinesGiven).collect(Collectors.toList());

		Map<String, Integer> map = new HashMap<>();
		list.forEach(dto -> {
			int counter = dto.getMenCounter() + dto.getWomenCounter();
			map.compute(dto.getAgeRange(), (k, v) -> v == null ? counter : v + counter);
		});

		return map;
	}

	private VaccinesGiven fromEntitySomministrazioneVacciniToVaccinesGiven(EntitySomministrazioneVaccini entity) {
		VaccinesGiven dto = new VaccinesGiven();
		dto.setAgeRange(entity.getId().getAgeRange());
		dto.setDate(entity.getId().getDate());
		dto.setFirstDoseCounter(entity.getFirstDoseCounter());
		dto.setMenCounter(entity.getMenCounter());
		dto.setNhsPeopleCounter(entity.getNhsPeopleCounter());
		dto.setNonNhsPeopleCounter(entity.getNonNhsPeopleCounter());
		dto.setNursingHomeCounter(entity.getNursingHomeCounter());
		dto.setOver80Counter(entity.getOver80Counter());
		dto.setPublicOrderCounter(entity.getPublicOrderCounter());
		dto.setRegionCode(entity.getId().getRegionCode());
		dto.setSchoolStaffCounter(entity.getSchoolStaffCounter());
		dto.setSecondDoseCounter(entity.getSecondDoseCounter());
		dto.setSupplier(entity.getId().getSupplier());
		dto.setWomenCounter(entity.getWomenCounter());
		return dto;
	}

	private VaccinesDelivered fromEntityVacciniConsegneToVaccinesDelivered(EntityVacciniConsegne entity) {
		VaccinesDelivered dto = new VaccinesDelivered();
		dto.setDate(entity.getId().getDate());
		dto.setDosesDelivered(entity.getDosesDelivered());
		return dto;
	}

}
