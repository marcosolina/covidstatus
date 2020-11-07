package com.marco.javacovidstatus.unittest;

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

import com.marco.javacovidstatus.model.DailyData;
import com.marco.javacovidstatus.repositories.model.EntityNationalData;
import com.marco.javacovidstatus.repositories.sql.EntityProvinceDataRepo;
import com.marco.javacovidstatus.repositories.sql.NationallDataSqlRepository;
import com.marco.javacovidstatus.services.implementations.MarcoNationalDataService;

@ExtendWith(MockitoExtension.class)
class MarcoNationalDataServiceTest {

    @Mock
    private NationallDataSqlRepository mockRepo;
    @Mock
    private EntityProvinceDataRepo mockEntityProvRepo;

    @InjectMocks
    private MarcoNationalDataService service = new MarcoNationalDataService();

    @Test
    void deleteAllDataTest() {
        boolean result = service.deleteAllData();
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

        List<DailyData> respList = service.getAllDataDescending();
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

        List<DailyData> respList = service.getAllDataAscending();
        assertEquals(11, respList.size());
        assertEquals(LocalDate.now().minusDays(10), respList.get(0).getDate());
        assertEquals(LocalDate.now(), respList.get(respList.size() - 1).getDate());
    }
    
    @Test
    void storeDataTest() {
        boolean result = service.storeData(new DailyData());
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
        
        List<DailyData> respList = service.getDatesInRangeAscending(start, end);
        assertEquals(11, respList.size());
        assertEquals(start, respList.get(0).getDate());
        assertEquals(end, respList.get(respList.size() - 1).getDate());
    }

}
