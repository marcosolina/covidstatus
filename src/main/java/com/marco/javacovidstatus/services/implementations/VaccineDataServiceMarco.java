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
import com.marco.javacovidstatus.enums.PopulationSource;
import com.marco.javacovidstatus.model.dto.PeopleVaccinated;
import com.marco.javacovidstatus.model.dto.PeopleVaccinatedPerRegion;
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
import com.marco.javacovidstatus.model.entitites.vaccines.VaccinesGivenPerRegion;
import com.marco.javacovidstatus.model.entitites.vaccines.VacciniConsegne;
import com.marco.javacovidstatus.repositories.interfaces.VaccinesGivenRepo;
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
    private VaccinesGivenRepo repoGiven;
    @Autowired
    private MessageSource msgSource;
    @Autowired
    private PopulationDataService populationService;
    @Value("${covidstatus.populationdownloader.istat.population.year}")
    private int populationStatisticYear;
    @Value("${covidstatus.populationdownloader.implementation}")
    private PopulationSource populationDownloader;

    @Override
    public Map<String, List<VaccinesDeliveredPerDayDto>> getDeliveredVaccinesBetweenDatesPerRegion(
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
    public VaccinatedPeopleTypeDto getVaccinatedPeopleBetweenDatesGroupByCategoryDistinctByDate(LocalDate start, LocalDate end)
            throws MarcoException {
        checkDates(start, end);

        VaccinatedPeopleTypeDto vp = new VaccinatedPeopleTypeDto();
        Set<LocalDate> dateSet = new HashSet<>();
        Map<String, List<Long>> dataMap = new HashMap<>();

        List<DailySumGivenVaccines> list = repoGiven.getDailyReportOfGivenVaccinesBetween(start, end);

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
    public Map<String, Long> getGivenShotsCountBetweenDates(LocalDate start, LocalDate end) throws MarcoException {
        checkDates(start, end);

        List<DoseCounter> list = repoGiven.getListOfGivenDosesBetween(start, end);
        if (list.size() != 1) {
            throw new MarcoException("Errore calcolo dosi");
        }

        Map<String, Long> map = new HashMap<>();
        map.put(Constants.VACCINES_GIVEN_FIRST_SHOT, list.get(0).getFirstDoseCounter());
        map.put(Constants.VACCINES_GIVEN_SECOND_SHOT, list.get(0).getSecondDoseCounter());
        map.put(Constants.VACCINES_GIVEN_MONO_SHOT, list.get(0).getMonoDoseCounter());
        map.put(Constants.VACCINES_GIVEN_AFTER_INFECTION, list.get(0).getDoseAfterInfectCounter());
        map.put(Constants.VACCINES_GIVEN_THIRD_SHOT, list.get(0).getThirdDoseCounter());
        map.put(Constants.VACCINES_GIVEN_BOOSTER_SHOT, list.get(0).getBoosterDoseCounter());

        return map;
    }

    @Override
    public Map<String, PeopleVaccinated> getVaccinatedPeopleBetweenDatesGroupByAge(LocalDate start, LocalDate end) throws MarcoException {
        checkDates(start, end);

        List<AgeRangeGivenVaccines> list = repoGiven.getListOfGivenVaccinesBetweenDatesGroupByAgeRange(start, end);

        return parseListAgeRangeGivenVaccines(list);
    }

    @Override
    public Map<String, PeopleVaccinated> getTotalVaccinatedPeopleGroupByAge() {
        List<AgeRangeGivenVaccines> list = repoGiven.getListOfTotalGivenVaccinesGorupByAgeRange();
        list.add(repoGiven.getTotalPeolpleVaccinated());
        return parseListAgeRangeGivenVaccines(list);
    }

    @Override
    public void deleteAllVaccinesDeliveredData() {
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
    public LocalDate getLastDateOfAvailableDataForDeliveredVaccines() {
        return repoDelivered.getDateOfLastAvailableData();
    }

    @Override
    public void deleteAllGivenVaccineData() {
        repoGiven.deleteAllData();
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
    public LocalDate getLastDateOfAvailableDataForGivenVaccines() {
        return repoGiven.getLastDateForAvailableData();
    }

    @Override
    public VaccinesReceivedUsedDto getTotlalVaccinesDeliveredUsed() {
        VaccinesReceivedUsedDto dto = new VaccinesReceivedUsedDto();
        dto.setTotalVaccinesReceived(repoDelivered.getTotalNumberOfDeliveredVaccines());
        dto.setTotalVaccinesUsed(repoGiven.getTotalGivenVaccines());
        return dto;
    }

    @Override
    public Map<String, VacinesTotalDeliveredGivenPerRegionDto> getTotalGivenUsedVaccinesCounterPerRegion() {
        Map<String, VacinesTotalDeliveredGivenPerRegionDto> map = new HashMap<>();
        List<TotalVaccineDeliveredPerRegion> list = repoDelivered.getListOfTotalVaccineDeliveredPerRegion();
        list.forEach(el -> {
            VacinesTotalDeliveredGivenPerRegionDto obj = new VacinesTotalDeliveredGivenPerRegionDto();
            obj.setRegionCode(el.getRegionCode());
            obj.setDeliveredVaccines(el.getDosesDelivered());
            map.put(el.getRegionCode(), obj);
        });

        List<TotalVaccineGivenPerRegion> list2 = repoGiven.getListTotalVaccinatedPeoplePerRegion();
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
        entity.setDoseAfterInfectCounter(dto.getDoseAfterInfectCounter());
        entity.setThirdDoseCounter(dto.getThirdDoseCounter());
        entity.setBoosterDoseCounter(dto.getBoosterDoseCounter());

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
            
            if(!ageRange.equals(Constants.LABEL_VACCINES_GIVEN_TOTAL)) {
                String[] ages = ageRange.split("-");
                if (ages.length > 1) {
                    int start = Integer.parseInt(ages[0]);
                    int end = Integer.parseInt(ages[1]);
                    men = populationService.getTotalPopulationBetweenAgesForSpecificGenderAndYear(start, end, Gender.MEN, populationStatisticYear);
                    women = populationService.getTotalPopulationBetweenAgesForSpecificGenderAndYear(start, end, Gender.WOMEN, populationStatisticYear);
                } else {
                    int start = Integer.parseInt(ages[0].replace('+', ' ').trim());
                    men = populationService.getTotalPopulationBetweenAgesForSpecificGenderAndYear(start, 100, Gender.MEN, populationStatisticYear);
                    women = populationService.getTotalPopulationBetweenAgesForSpecificGenderAndYear(start, 100, Gender.WOMEN, populationStatisticYear);
                }
                
            } else {
                men = populationService.getTotalPopulationForSpecificGenderAndYear(Gender.MEN, populationStatisticYear);
                women = populationService.getTotalPopulationForSpecificGenderAndYear(Gender.WOMEN, populationStatisticYear);
            }
            
            PeopleVaccinated dto = new PeopleVaccinated();
            dto.setAgeRange(ageRange);
            dto.setPopulation(men + women);
            dto.setFirstDose(argv.getFirstDose());
            dto.setSecondDose(argv.getSecondDose());
            dto.setMonoDose(argv.getMonoDose());
            dto.setDoseAfterInfection(argv.getDoseAfterInfection());
            
            Long totalThirdDose = argv.getThirdDoseCounter() + argv.getBoosterDoseCounter(); 
            dto.setThirdDose(totalThirdDose);
            
            // @formatter:off
            BigDecimal first = BigDecimal.ZERO;
            BigDecimal vaccinadted = BigDecimal.ZERO;
            BigDecimal third = BigDecimal.ZERO;
            // TODO cosa fare per la terza doses
            if(men + women > 0) {
                first = BigDecimal.valueOf(dto.getFirstDose())
                        .divide(BigDecimal.valueOf(dto.getPopulation()), 4, RoundingMode.DOWN)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.DOWN);
                vaccinadted = BigDecimal.valueOf(dto.getSecondDose())
                        .add(BigDecimal.valueOf(dto.getMonoDose()))
                        .add(BigDecimal.valueOf(dto.getDoseAfterInfection()))
                        .divide(BigDecimal.valueOf(dto.getPopulation()), 4, RoundingMode.DOWN)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.DOWN);
                third = BigDecimal.valueOf(totalThirdDose)
                        .divide(BigDecimal.valueOf(dto.getPopulation()), 4, RoundingMode.DOWN)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.DOWN);
            }
            // @formatter:on
            
            dto.setFirstDosePerc(first);
            dto.setVaccinatedPerc(vaccinadted);
            dto.setThirdDosePerc(third);

            map.put(dto.getAgeRange(), dto);
        });
        // @formatter:on
        
        if(!map.isEmpty() && populationDownloader == PopulationSource.GOVERNMENT) {
        	String f1 = "80-89";
        	String f2 = "90+";
        	PeopleVaccinated dto = map.get(f1);
        	PeopleVaccinated dto2 = map.get(f2);
        	dto.setPopulation(dto.getPopulation() + dto2.getPopulation());
        	dto.setFirstDose(dto.getFirstDose() + dto2.getFirstDose());
        	dto.setSecondDose(dto.getSecondDose() + dto2.getSecondDose());
        	dto.setMonoDose(dto.getMonoDose() + dto2.getMonoDose());
        	dto.setDoseAfterInfection(dto.getDoseAfterInfection() + dto2.getDoseAfterInfection());
        	dto.setThirdDose(dto.getThirdDose() + dto2.getThirdDose());
        	
        	if(dto.getPopulation() > 0) {
        		BigDecimal first = BigDecimal.valueOf(dto.getFirstDose())
                        .divide(BigDecimal.valueOf(dto.getPopulation()), 4, RoundingMode.DOWN)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.DOWN);
        		BigDecimal vaccinadted = BigDecimal.valueOf(dto.getSecondDose())
                        .add(BigDecimal.valueOf(dto.getMonoDose()))
                        .add(BigDecimal.valueOf(dto.getDoseAfterInfection()))
                        .divide(BigDecimal.valueOf(dto.getPopulation()), 4, RoundingMode.DOWN)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.DOWN);
        		BigDecimal third = BigDecimal.valueOf(dto.getThirdDose())
                        .divide(BigDecimal.valueOf(dto.getPopulation()), 4, RoundingMode.DOWN)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.DOWN);
        		
        		dto.setFirstDosePerc(first);
                dto.setVaccinatedPerc(vaccinadted);
                dto.setThirdDosePerc(third);
        	}
        	
        	map.remove(f1);
        	map.remove(f2);
        	map.put("80+", dto);
        }
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

	@Override
	public Map<String, PeopleVaccinatedPerRegion> getTotalVaccinatedPeopleGroupByRegion() {
		Map<String, PeopleVaccinatedPerRegion> map = new HashMap<>();
		
		List<VaccinesGivenPerRegion> vgpr = repoGiven.getListOfTotalGivenVaccinesGorupByRegion();
		vgpr.stream().forEach(entity -> {
			
			Long men = populationService.getTotalPopulationForSpecificGenderYearAndRegion(Gender.MEN, populationStatisticYear, entity.getRegionCode());
            Long women = populationService.getTotalPopulationForSpecificGenderYearAndRegion(Gender.WOMEN, populationStatisticYear, entity.getRegionCode());
			
			
			PeopleVaccinatedPerRegion dto = new PeopleVaccinatedPerRegion();
			dto.setRegionCode(entity.getRegionCode());
            dto.setPopulation(men + women);
            dto.setFirstDose(entity.getFirstDose());
            dto.setSecondDose(entity.getSecondDose());
            dto.setMonoDose(entity.getMonoDose());
            dto.setDoseAfterInfection(entity.getDoseAfterInfection());
            
            Long totalThirdDose = entity.getThirdDoseCounter() + entity.getBoosterDoseCounter(); 
            dto.setThirdDose(totalThirdDose);
            
            // @formatter:off
            BigDecimal first = BigDecimal.ZERO;
            BigDecimal vaccinadted = BigDecimal.ZERO;
            BigDecimal third = BigDecimal.ZERO;
            
            if(men + women > 0) {
                first = BigDecimal.valueOf(dto.getFirstDose())
                        .divide(BigDecimal.valueOf(dto.getPopulation()), 4, RoundingMode.DOWN)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.DOWN);
                vaccinadted = BigDecimal.valueOf(dto.getSecondDose())
                        .add(BigDecimal.valueOf(dto.getMonoDose()))
                        .add(BigDecimal.valueOf(dto.getDoseAfterInfection()))
                        .divide(BigDecimal.valueOf(dto.getPopulation()), 4, RoundingMode.DOWN)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.DOWN);
                third = BigDecimal.valueOf(dto.getThirdDose())
                        .divide(BigDecimal.valueOf(dto.getPopulation()), 4, RoundingMode.DOWN)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.DOWN);
            }
            // @formatter:on
            
            dto.setFirstDosePerc(first);
            dto.setVaccinatedPerc(vaccinadted);
            dto.setThirdDosePerc(third);
            map.put(dto.getRegionCode(), dto);
		});
		
		return map;
	}

}
