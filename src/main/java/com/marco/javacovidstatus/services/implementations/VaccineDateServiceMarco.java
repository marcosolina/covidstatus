package com.marco.javacovidstatus.services.implementations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.marco.javacovidstatus.model.dto.VaccinesDelivered;
import com.marco.javacovidstatus.model.entitites.AgeRangeGivenVaccines;
import com.marco.javacovidstatus.model.entitites.DailySumGivenVaccines;
import com.marco.javacovidstatus.model.entitites.DoseCounter;
import com.marco.javacovidstatus.model.entitites.VacciniConsegne;
import com.marco.javacovidstatus.repositories.interfaces.GivenVaccinesRepo;
import com.marco.javacovidstatus.repositories.interfaces.VeccinesDeliveredRepo;
import com.marco.javacovidstatus.services.interfaces.VaccineDateService;
import com.marco.javacovidstatus.utils.Constants;
import com.marco.utils.MarcoException;

public class VaccineDateServiceMarco implements VaccineDateService {

	@Autowired
	private VeccinesDeliveredRepo repoDelivered;
	@Autowired
	private GivenVaccinesRepo repoGiven;

	@Override
	public Map<String, List<VaccinesDelivered>> getDeliveredVaccinesPerRegionBetweenDatesPerRegion(LocalDate start,
			LocalDate end) {

		Map<String, List<VaccinesDelivered>> data = new HashMap<>();
		repoDelivered.getDeliveredVaccinesBetween(start, end).stream()
				.forEach(entity -> data.compute(entity.getRegionCode(), (k, v) -> {
					VaccinesDelivered tmp = fromEntityVacciniConsegneToVaccinesDelivered(entity);
					if (v == null) {
						v = new ArrayList<>();
					}
					v.add(tmp);
					return v;
				}));

		return data;
	}

	@Override
	public Map<String, Long> getDeliveredVaccinesBetweenDatesPerSupplier(LocalDate start, LocalDate end) {
		Map<String, Long> data = new HashMap<>();
		repoDelivered.getDeliveredVaccinesPerSupplierBetween(start, end).stream()
				.forEach(entity -> data.compute(entity.getSupplier(),
						(k, v) -> v == null ? entity.getDosesDelivered() : v + entity.getDosesDelivered()));

		return data;
	}

	@Override
	public Map<String, List<Long>> getVaccinatedPeopleBetweenDates(LocalDate start, LocalDate end) {
		List<DailySumGivenVaccines> list = repoGiven.getDailySumGivenVaccinesBetween(start, end);

		Map<String, List<Long>> dataMap = new HashMap<>();
		list.forEach(dto -> {
			dataMap.compute(Constants.VACCINES_GIVEN_MEN, (k, v) -> {
				if(v == null) {
					v = new ArrayList<>();
				}
				v.add(dto.getMenCounter());
				return v;
			});
			dataMap.compute(Constants.VACCINES_GIVEN_WOMEN, (k, v) -> {
				if(v == null) {
					v = new ArrayList<>();
				}
				v.add(dto.getWomenCounter());
				return v;
			});
			dataMap.compute(Constants.VACCINES_GIVEN_NHS, (k, v) -> {
				if(v == null) {
					v = new ArrayList<>();
				}
				v.add(dto.getNhsPeopleCounter());
				return v;
			});
			dataMap.compute(Constants.VACCINES_GIVEN_NO_NHS, (k, v) -> {
				if(v == null) {
					v = new ArrayList<>();
				}
				v.add(dto.getNonNhsPeopleCounter());
				return v;
			});
			dataMap.compute(Constants.VACCINES_GIVEN_NURSING, (k, v) -> {
				if(v == null) {
					v = new ArrayList<>();
				}
				v.add(dto.getNursingHomeCounter());
				return v;
			});
			dataMap.compute(Constants.VACCINES_GIVEN_OVER_80, (k, v) -> {
				if(v == null) {
					v = new ArrayList<>();
				}
				v.add(dto.getOver80Counter());
				return v;
			});
			dataMap.compute(Constants.VACCINES_GIVEN_PUBLIC, (k, v) -> {
				if(v == null) {
					v = new ArrayList<>();
				}
				v.add(dto.getPublicOrderCounter());
				return v;
			});
			dataMap.compute(Constants.VACCINES_GIVEN_SCHOOLS, (k, v) -> {
				if(v == null) {
					v = new ArrayList<>();
				}
				v.add(dto.getSchoolStaffCounter());
				return v;
			});
		});

		return dataMap;
	}

	@Override
	public Map<String, Long> getGiveShotNumberBetweenDates(LocalDate start, LocalDate end)  throws MarcoException {
		List<DoseCounter> list = repoGiven.getDosesCounterVaccinesBetween(start, end);
		if(list.size() != 1) {
			throw new MarcoException("Errore calcolo dosi");
		}
		
		Map<String, Long> map = new HashMap<>();		
		map.put(Constants.VACCINES_GIVEN_FIRST_SHOT, list.get(0).getFirstDoseCounter());
		map.put(Constants.VACCINES_GIVEN_SECOND_SHOT, list.get(0).getSecondDoseCounter());

		return map;
	}

	@Override
	public Map<String, Long> getVaccinatedAgeRangeBetweenDates(LocalDate start, LocalDate end) {
		List<AgeRangeGivenVaccines> list = repoGiven.getDeliveredVaccinesPerAgeRange(start, end);

		Map<String, Long> map = new HashMap<>();
		list.forEach(dto -> {
			Long counter = dto.getMen() + dto.getWomen();
			map.compute(dto.getAgeRange(), (k, v) -> v == null ? counter : v + counter);
		});

		return map;
	}
	
	private VaccinesDelivered fromEntityVacciniConsegneToVaccinesDelivered(VacciniConsegne entity) {
		VaccinesDelivered dto = new VaccinesDelivered();
		dto.setDate(entity.getDate());
		dto.setDosesDelivered(entity.getDosesDelivered());
		return dto;
	}

}
