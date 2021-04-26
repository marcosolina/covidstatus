package com.marco.javacovidstatus.services.implementations;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.marco.javacovidstatus.enums.Gender;
import com.marco.javacovidstatus.model.dto.PeopleVaccinated;
import com.marco.javacovidstatus.model.dto.VaccinatedPeopleDto;
import com.marco.javacovidstatus.model.dto.VaccinatedPeopleTypeDto;
import com.marco.javacovidstatus.model.dto.VaccinesDeliveredDto;
import com.marco.javacovidstatus.model.dto.VaccinesDeliveredPerDayDto;
import com.marco.javacovidstatus.model.dto.VaccinesReceivedUsedDto;
import com.marco.javacovidstatus.model.dto.VacinesTotalDeliveredGivenPerRegionDto;
import com.marco.javacovidstatus.model.entitites.vaccines.AgeRangeGivenVaccines;
import com.marco.javacovidstatus.model.entitites.vaccines.DailySumGivenVaccines;
import com.marco.javacovidstatus.model.entitites.vaccines.DoseCounter;
import com.marco.javacovidstatus.model.entitites.vaccines.EntitySomministrazioneVaccini;
import com.marco.javacovidstatus.model.entitites.vaccines.EntitySomministrazioneVacciniPk;
import com.marco.javacovidstatus.model.entitites.vaccines.EntityVacciniConsegne;
import com.marco.javacovidstatus.model.entitites.vaccines.EntityVacciniConsegnePk;
import com.marco.javacovidstatus.model.entitites.vaccines.TotalVaccineDeliveredPerRegion;
import com.marco.javacovidstatus.model.entitites.vaccines.TotalVaccineGivenPerRegion;
import com.marco.javacovidstatus.model.entitites.vaccines.VacciniConsegne;
import com.marco.javacovidstatus.repositories.interfaces.GivenVaccinesRepo;
import com.marco.javacovidstatus.repositories.interfaces.VeccinesDeliveredRepo;
import com.marco.javacovidstatus.services.interfaces.PopulationDataService;
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
    @Autowired
    private MessageSource msgSource;
    @Autowired
    private PopulationDataService populationService;
    @Value("${covidstatus.istat.population.year}")
    private int populationStatisticYear;

    @Override
    public Map<String, List<VaccinesDeliveredPerDayDto>> getDeliveredVaccinesPerRegionBetweenDatesPerRegion(
            LocalDate start, LocalDate end) throws MarcoException {
        checkDates(start, end);

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
    public Map<String, Long> getDeliveredVaccinesBetweenDatesPerSupplier(LocalDate start, LocalDate end)
            throws MarcoException {
        checkDates(start, end);

        Map<String, Long> data = new HashMap<>();
        repoDelivered.getDeliveredVaccinesPerSupplierBetween(start, end).stream()
                .forEach(entity -> data.compute(entity.getSupplier(),
                        (k, v) -> v == null ? entity.getDosesDelivered() : v + entity.getDosesDelivered()));

        return data;
    }

    @Override
    public VaccinatedPeopleTypeDto getVaccinatedPeopleBetweenDates(LocalDate start, LocalDate end)
            throws MarcoException {
        checkDates(start, end);

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
			dataMap.compute(Constants.VACCINES_GIVEN_AGE_60_69, getAttToArrayBiFunction(DailySumGivenVaccines::getAge6069counter, dto));
			dataMap.compute(Constants.VACCINES_GIVEN_AGE_70_79, getAttToArrayBiFunction(DailySumGivenVaccines::getAge7079counter, dto));
			dataMap.compute(Constants.VACCINES_GIVEN_OVER_80,	getAttToArrayBiFunction(DailySumGivenVaccines::getOver80Counter, dto));
			dataMap.compute(Constants.VACCINES_GIVEN_PUBLIC,	getAttToArrayBiFunction(DailySumGivenVaccines::getPublicOrderCounter, dto));
			dataMap.compute(Constants.VACCINES_GIVEN_SCHOOLS,	getAttToArrayBiFunction(DailySumGivenVaccines::getSchoolStaffCounter, dto));
			dataMap.compute(Constants.VACCINES_GIVEN_FRAGILE,   getAttToArrayBiFunction(DailySumGivenVaccines::getFragilePeopleCounter, dto));
			dataMap.compute(Constants.VACCINES_GIVEN_OTHER,		getAttToArrayBiFunction(DailySumGivenVaccines::getOtherPeopleCounter, dto));
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
        checkDates(start, end);

        List<DoseCounter> list = repoGiven.getDosesCounterVaccinesBetween(start, end);
        if (list.size() != 1) {
            throw new MarcoException("Errore calcolo dosi");
        }

        Map<String, Long> map = new HashMap<>();
        map.put(Constants.VACCINES_GIVEN_FIRST_SHOT, list.get(0).getFirstDoseCounter());
        map.put(Constants.VACCINES_GIVEN_SECOND_SHOT, list.get(0).getSecondDoseCounter());
        map.put(Constants.VACCINES_GIVEN_MONO_SHOT, list.get(0).getMonoDoseCounter());

        return map;
    }

    @Override
    public Map<String, PeopleVaccinated> getVaccinatedAgeRangeBetweenDates(LocalDate start, LocalDate end) throws MarcoException {
        checkDates(start, end);

        List<AgeRangeGivenVaccines> list = repoGiven.getDeliveredVaccinesPerAgeRange(start, end);

        return parseListAgeRangeGivenVaccines(list);
    }

    @Override
    public Map<String, PeopleVaccinated> getVaccinatedAgeRangeTotals() {
        List<AgeRangeGivenVaccines> list = repoGiven.getTotalAgeRangeGivenVaccines();
        return parseListAgeRangeGivenVaccines(list);
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
        entity.setAge6069counter(dto.getAge6069counter());
        entity.setAge7079counter(dto.getAge7079counter());
        entity.setOver80Counter(dto.getOver80Counter());
        entity.setPublicOrderCounter(dto.getPublicOrderCounter());
        entity.setSchoolStaffCounter(dto.getSchoolStaffCounter());
        entity.setFragilePeopleCounter(dto.getFragilePeopleCounter());
        entity.setOtherPeopleCounter(dto.getOtherPeopleCounter());
        entity.setFirstDoseCounter(dto.getFirstDoseCounter());
        entity.setSecondDoseCounter(dto.getSecondDoseCounter());
        entity.setMonoDoseCounter(dto.getMonoDoseCounter());

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

    private Map<String, PeopleVaccinated> parseListAgeRangeGivenVaccines(List<AgeRangeGivenVaccines> list) {
        Map<String, PeopleVaccinated> map = new HashMap<>();

        // @formatter:off
        list.parallelStream().forEach(argv -> {
            String ageRange = argv.getAgeRange();

            Long men = 0L;
            Long women = 0L;
            
            String[] ages = ageRange.split("-");
            if (ages.length > 1) {
                men = populationService.getSumForAgesAndYear(Integer.parseInt(ages[0]), Integer.parseInt(ages[1]), Gender.MEN, populationStatisticYear);
                women = populationService.getSumForAgesAndYear(Integer.parseInt(ages[0]), Integer.parseInt(ages[1]), Gender.WOMEN, populationStatisticYear);
            } else {
                men = populationService.getSumForAgesAndYear(Integer.parseInt(ages[0].replace('+', ' ').trim()), 100, Gender.MEN, populationStatisticYear);
                women = populationService.getSumForAgesAndYear(Integer.parseInt(ages[0].replace('+', ' ').trim()), 100, Gender.WOMEN, populationStatisticYear);
            }
            
            PeopleVaccinated dto = new PeopleVaccinated();
            dto.setAgeRange(ageRange);
            dto.setPopulation(men + women);
            dto.setFirstDose(argv.getFirstDose());
            dto.setSecondDose(argv.getSecondDose());
            dto.setMonoDose(argv.getMonoDose());
            
            // @formatter:off
            BigDecimal first = BigDecimal.valueOf(dto.getFirstDose())
                                .divide(BigDecimal.valueOf(dto.getPopulation()), 4, RoundingMode.DOWN)
                                .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.DOWN);
            BigDecimal vaccinadted = BigDecimal.valueOf(dto.getSecondDose())
                                        .add(BigDecimal.valueOf(dto.getMonoDose()))
                                        .divide(BigDecimal.valueOf(dto.getPopulation()), 4, RoundingMode.DOWN)
                                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.DOWN);
            // @formatter:on
            
            dto.setFirstDosePerc(first);
            dto.setVaccinatedPerc(vaccinadted);

            map.put(dto.getAgeRange(), dto);
        });
        // @formatter:on

        return map;
    }

    @Override
    public Map<String, Long> getTotalDeliveredVaccinesPerSupplier() {
        Map<String, Long> data = new HashMap<>();
        repoDelivered.getTotalDeliveredVaccinesPerSupplier().stream()
                .forEach(entity -> data.compute(entity.getSupplier(),
                        (k, v) -> v == null ? entity.getDosesDelivered() : v + entity.getDosesDelivered()));

        return data;
    }

    private void checkDates(LocalDate from, LocalDate to) throws MarcoException {
        if (from == null || to == null) {
            throw new MarcoException(msgSource.getMessage("COVID00002", null, LocaleContextHolder.getLocale()));
        }
    }

}
