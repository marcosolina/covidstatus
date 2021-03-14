package com.marco.javacovidstatus.services.implementations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.ToLongFunction;

import org.springframework.beans.factory.annotation.Autowired;

import com.marco.javacovidstatus.model.dto.VaccinatedPeopleTypeDto;
import com.marco.javacovidstatus.model.dto.VaccinatedPeopleDto;
import com.marco.javacovidstatus.model.dto.VaccinesDeliveredPerDayDto;
import com.marco.javacovidstatus.model.dto.VaccinesDeliveredDto;
import com.marco.javacovidstatus.model.dto.VaccinesReceivedUsedDto;
import com.marco.javacovidstatus.model.dto.VacinesTotalDeliveredGivenPerRegionDto;
import com.marco.javacovidstatus.model.entitites.AgeRangeGivenVaccines;
import com.marco.javacovidstatus.model.entitites.DailySumGivenVaccines;
import com.marco.javacovidstatus.model.entitites.DoseCounter;
import com.marco.javacovidstatus.model.entitites.EntitySomministrazioneVaccini;
import com.marco.javacovidstatus.model.entitites.EntitySomministrazioneVacciniPk;
import com.marco.javacovidstatus.model.entitites.EntityVacciniConsegne;
import com.marco.javacovidstatus.model.entitites.EntityVacciniConsegnePk;
import com.marco.javacovidstatus.model.entitites.TotalVaccineDeliveredPerRegion;
import com.marco.javacovidstatus.model.entitites.TotalVaccineGivenPerRegion;
import com.marco.javacovidstatus.model.entitites.VacciniConsegne;
import com.marco.javacovidstatus.repositories.interfaces.GivenVaccinesRepo;
import com.marco.javacovidstatus.repositories.interfaces.VeccinesDeliveredRepo;
import com.marco.javacovidstatus.services.interfaces.VaccineDataService;
import com.marco.javacovidstatus.utils.Constants;
import com.marco.utils.MarcoException;

/**
 * My implementation of the interface
 * 
 * @author Marco
 *
 */
public class VaccineDataServiceMarco implements VaccineDataService {

	@Autowired
	private VeccinesDeliveredRepo repoDelivered;
	@Autowired
	private GivenVaccinesRepo repoGiven;

	@Override
	public Map<String, List<VaccinesDeliveredPerDayDto>> getDeliveredVaccinesPerRegionBetweenDatesPerRegion(
			LocalDate start, LocalDate end) {

		Map<String, List<VaccinesDeliveredPerDayDto>> data = new HashMap<>();
		repoDelivered.getDeliveredVaccinesBetween(start, end).stream()
				.forEach(entity -> data.compute(entity.getRegionCode(), (k, v) -> {
					VaccinesDeliveredPerDayDto tmp = fromEntityVacciniConsegneToVaccinesDelivered(entity);
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
	public VaccinatedPeopleTypeDto getVaccinatedPeopleBetweenDates(LocalDate start, LocalDate end) {

		VaccinatedPeopleTypeDto vp = new VaccinatedPeopleTypeDto();
		Set<LocalDate> dateSet = new HashSet<>();
		Map<String, List<Long>> dataMap = new HashMap<>();

		List<DailySumGivenVaccines> list = repoGiven.getDailySumGivenVaccinesBetween(start, end);

		// @formatter:off
		list.forEach(dto -> {
			dateSet.add(dto.getDate());
			dataMap.compute(Constants.VACCINES_GIVEN_MEN,		getAttToArrayBiFunction(DailySumGivenVaccines::getMenCounter, dto));
			dataMap.compute(Constants.VACCINES_GIVEN_WOMEN,		getAttToArrayBiFunction(DailySumGivenVaccines::getNhsPeopleCounter, dto));
			dataMap.compute(Constants.VACCINES_GIVEN_NO_NHS,	getAttToArrayBiFunction(DailySumGivenVaccines::getNonNhsPeopleCounter, dto));
			dataMap.compute(Constants.VACCINES_GIVEN_NURSING,	getAttToArrayBiFunction(DailySumGivenVaccines::getNursingHomeCounter, dto));
			dataMap.compute(Constants.VACCINES_GIVEN_OVER_80,	getAttToArrayBiFunction(DailySumGivenVaccines::getOver80Counter, dto));
			dataMap.compute(Constants.VACCINES_GIVEN_PUBLIC,	getAttToArrayBiFunction(DailySumGivenVaccines::getPublicOrderCounter, dto));
			dataMap.compute(Constants.VACCINES_GIVEN_SCHOOLS,	getAttToArrayBiFunction(DailySumGivenVaccines::getSchoolStaffCounter, dto));
		});
		// @formatter:on

		vp.setDataVaccinatedPeople(dataMap);

		List<LocalDate> dateList = Arrays.asList(dateSet.toArray(new LocalDate[0]));
		Collections.sort(dateList);
		vp.setDates(dateList);

		return vp;
	}

	@Override
	public Map<String, Long> getGiveShotNumberBetweenDates(LocalDate start, LocalDate end) throws MarcoException {
		List<DoseCounter> list = repoGiven.getDosesCounterVaccinesBetween(start, end);
		if (list.size() != 1) {
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

	@Override
	public void deleteAllVaccineDeliveredData() {
		repoDelivered.deleteAll();
	}

	@Override
	public boolean saveVaccinesDeliveredData(VaccinesDeliveredDto data) {
		return repoDelivered.saveEntity(fromVaccinesDeliveredDtoToVacciniEntityVacciniConsegne(data));
	}

	@Override
	public void addMissingRowsForNoDeliveryDays() {
		repoDelivered.addMissingRowsForNoDeliveryDays();
	}

	@Override
	public LocalDate getVaccineDeliveredLastUpdateDate() {
		return repoDelivered.getDataAvailableLastDate();
	}

	@Override
	public void deleteAllGivenVaccineData() {
		repoGiven.deleteAll();
	}

	@Override
	public boolean saveGivenVaccinesData(VaccinatedPeopleDto data) {
		return repoGiven.saveEntity(fromVaccinatedPeopleDtoToEntitySomministrazioneVaccini(data));
	}

	@Override
	public void addMissingRowsForNoVaccinationDays() {
		repoGiven.addMissingRowsForNoVaccinationDays();
	}

	@Override
	public LocalDate getGivenVaccinesLastUpdateDate() {
		return repoGiven.getDataAvailableLastDate();
	}

	@Override
	public VaccinesReceivedUsedDto getTotlalVaccinesDeliveredUsed() {
		VaccinesReceivedUsedDto dto = new VaccinesReceivedUsedDto();
		dto.setTotalVaccinesReceived(repoDelivered.getTotalNumberDeliveedVaccines());
		dto.setTotalVaccinesUsed(repoGiven.getTotalPeaopleVaccinated());
		return dto;
	}

	private VaccinesDeliveredPerDayDto fromEntityVacciniConsegneToVaccinesDelivered(VacciniConsegne entity) {
		VaccinesDeliveredPerDayDto dto = new VaccinesDeliveredPerDayDto();
		dto.setDate(entity.getDate());
		dto.setDosesDelivered(entity.getDosesDelivered());
		return dto;
	}

	private EntityVacciniConsegne fromVaccinesDeliveredDtoToVacciniEntityVacciniConsegne(VaccinesDeliveredDto dto) {
		EntityVacciniConsegnePk key = new EntityVacciniConsegnePk();
		key.setDate(dto.getDate());
		key.setRegionCode(dto.getRegionCode());
		key.setSupplier(dto.getSupplier());

		EntityVacciniConsegne entity = new EntityVacciniConsegne();
		entity.setId(key);
		entity.setDosesDelivered(dto.getDosesDelivered());

		return entity;
	}

	private EntitySomministrazioneVaccini fromVaccinatedPeopleDtoToEntitySomministrazioneVaccini(
			VaccinatedPeopleDto dto) {
		EntitySomministrazioneVaccini entity = new EntitySomministrazioneVaccini();
		EntitySomministrazioneVacciniPk key = new EntitySomministrazioneVacciniPk();

		key.setDate(dto.getDate());
		key.setRegionCode(dto.getRegionCode());
		key.setSupplier(dto.getSupplier());
		key.setAgeRange(dto.getAgeRange());

		entity.setId(key);
		entity.setMenCounter(dto.getMenCounter());
		entity.setWomenCounter(dto.getWomenCounter());
		entity.setNhsPeopleCounter(dto.getNhsPeopleCounter());
		entity.setNonNhsPeopleCounter(dto.getNonNhsPeopleCounter());
		entity.setNursingHomeCounter(dto.getNursingHomeCounter());
		entity.setOver80Counter(dto.getOver80Counter());
		entity.setPublicOrderCounter(dto.getPublicOrderCounter());
		entity.setSchoolStaffCounter(dto.getSchoolStaffCounter());
		entity.setFirstDoseCounter(dto.getFirstDoseCounter());
		entity.setSecondDoseCounter(dto.getSecondDoseCounter());

		return entity;
	}

	private BiFunction<String, List<Long>, List<Long>> getAttToArrayBiFunction(
			ToLongFunction<DailySumGivenVaccines> function, DailySumGivenVaccines dto) {
		return (k, v) -> {
			if (v == null) {
				v = new ArrayList<>();
			}
			v.add(function.applyAsLong(dto));
			return v;
		};
	}

	@Override
	public Map<String, VacinesTotalDeliveredGivenPerRegionDto> getVacinesTotalDeliveredGivenPerRegion() {
		Map<String, VacinesTotalDeliveredGivenPerRegionDto> map = new HashMap<>();
		List<TotalVaccineDeliveredPerRegion> list = repoDelivered.getTotalVaccineDeliveredPerRegion();
		list.forEach(el -> {
			VacinesTotalDeliveredGivenPerRegionDto obj = new VacinesTotalDeliveredGivenPerRegionDto();
			obj.setRegionCode(el.getRegionCode());
			obj.setDeliveredVaccines(el.getDosesDelivered());
			map.put(el.getRegionCode(), obj);
		});

		List<TotalVaccineGivenPerRegion> list2 = repoGiven.getTotalPeaopleVaccinatedPerRegion();
		list2.forEach(el -> map.compute(el.getRegionCode(), (k, v) -> {
			if (v == null) {
				v = new VacinesTotalDeliveredGivenPerRegionDto();
				v.setRegionCode(k);
			}
			v.setGivenVaccines(el.getGivenDoses());
			return v;
		}));

		return map;
	}

	@Override
	public void deleteGivenVaccineInformation(LocalDate date) {
		repoGiven.deleteInformationForDate(date);
	}

	@Override
	public void deleteDeliveredVaccineInformation(LocalDate date) {
		repoDelivered.deleteInformationForDate(date);
	}
}
