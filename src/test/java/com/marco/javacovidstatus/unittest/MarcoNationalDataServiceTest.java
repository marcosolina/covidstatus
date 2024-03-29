package com.marco.javacovidstatus.unittest;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marco.javacovidstatus.model.dto.NationalDailyDataDto;
import com.marco.javacovidstatus.model.entitites.infections.EntityNationalData;
import com.marco.javacovidstatus.repositories.interfaces.ProvinceInfectionDataRepo;
import com.marco.javacovidstatus.repositories.interfaces.RegionalInfectionDataRepository;
import com.marco.javacovidstatus.repositories.interfaces.NationalInfectionDataRepository;
import com.marco.javacovidstatus.services.implementations.MarcoNationalDataService;
import com.marco.utils.MarcoException;

@ExtendWith(MockitoExtension.class)
class MarcoNationalDataServiceTest {

    @Mock
    private NationalInfectionDataRepository mockRepo;
    @Mock
    private ProvinceInfectionDataRepo mockEntityProvRepo;
    @Mock
    private RegionalInfectionDataRepository mockRegionalData;

    @InjectMocks
    private MarcoNationalDataService service = new MarcoNationalDataService();

    @Test
    void deleteAllDataTest() {
        boolean result = service.deleteAllNationalRegionalProvinceData();
        assertTrue(result);
    }

    @Test
    void getAllDataDescendingTest() {
        List<EntityNationalData> list = new ArrayList<EntityNationalData>();
        for (int i = 0; i < 10; i++) {
            EntityNationalData entity = new EntityNationalData();
            entity.setDate(LocalDate.now().minusDays(i));
            list.add(entity);
        }

        when(mockRepo.findAllByOrderByDateDesc()).thenReturn(list);

        List<NationalDailyDataDto> respList = service.getListAllNationalDailyDataOrderByDateDesc();
        assertEquals(10, respList.size());
        assertEquals(LocalDate.now(), respList.get(0).getDate());
        assertEquals(LocalDate.now().minusDays(9), respList.get(respList.size() - 1).getDate());

    }
    
    @Test
    void getAllDataAscendingTest() {
        List<EntityNationalData> list = new ArrayList<EntityNationalData>();
        for (int i = 10; i > -1; i--) {
            EntityNationalData entity = new EntityNationalData();
            entity.setDate(LocalDate.now().minusDays(i));
            list.add(entity);
        }

        when(mockRepo.findAllByOrderByDateAsc()).thenReturn(list);

        List<NationalDailyDataDto> respList = service.getListAllNationalDailyDataOrderByDateAscending();
        assertEquals(11, respList.size());
        assertEquals(LocalDate.now().minusDays(10), respList.get(0).getDate());
        assertEquals(LocalDate.now(), respList.get(respList.size() - 1).getDate());
    }
    
    @Test
    void storeDataTest() {
        boolean result = service.saveNationalDailyData(new NationalDailyDataDto());
        assertTrue(result);
    }
    
    @Test
    void getDatesInRangeAscendingTest() {
        List<EntityNationalData> list = new ArrayList<EntityNationalData>();
        for (int i = 10; i > -1; i--) {
            EntityNationalData entity = new EntityNationalData();
            entity.setDate(LocalDate.now().minusDays(i));
            list.add(entity);
        }
        
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now();
        when(mockRepo.findByDateBetweenOrderByDateAsc(start, end)).thenReturn(list);
        
		try {
			List<NationalDailyDataDto> respList = service.getListNationalDailyDataBetweenDatesOrderByDateAscending(start, end);
			assertEquals(11, respList.size());
			assertEquals(start, respList.get(0).getDate());
			assertEquals(end, respList.get(respList.size() - 1).getDate());
		} catch (MarcoException e) {
			fail(e.getMessage());
		}
    }

}
