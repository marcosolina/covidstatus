package com.marco.javacovidstatus.unittest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marco.javacovidstatus.model.DailyData;
import com.marco.javacovidstatus.services.implementations.NationalDataServiceRasp;

@ExtendWith(MockitoExtension.class)
class NationalDataServiceRaspTest {

    private NationalDataServiceRasp service = new NationalDataServiceRasp();

    @Test
    void deleteAllDataTest() {
        boolean result = service.deleteAllData(); 
        assertTrue(result);
        assertEquals( 0, service.getAllDataAscending().size());
    }
    
    @Test
    void getAllDataDescendingTest() {
        boolean result = service.deleteAllData(); 
        assertTrue(result);
        
        DailyData one = new DailyData();
        DailyData two = new DailyData();
        
        one.setDate(LocalDate.of(2020, 5, 5));
        two.setDate(LocalDate.of(2020, 2, 5));
        
        service.storeData(two);
        service.storeData(one);
        
        List<DailyData> list = service.getAllDataDescending();
        
        assertEquals(2, list.size());
        assertEquals(LocalDate.of(2020, 5, 5), list.get(0).getDate());
        assertEquals(LocalDate.of(2020, 2, 5), list.get(1).getDate());
    }
    
    @Test
    void getAllDataAscendingTest() {
        boolean result = service.deleteAllData(); 
        assertTrue(result);
        
        DailyData one = new DailyData();
        DailyData two = new DailyData();
        
        one.setDate(LocalDate.of(2020, 5, 5));
        two.setDate(LocalDate.of(2020, 2, 5));
        
        service.storeData(one);
        service.storeData(two);
        
        List<DailyData> list = service.getAllDataAscending();
        
        assertEquals(2, list.size());
        assertEquals(LocalDate.of(2020, 2, 5), list.get(0).getDate());
        assertEquals(LocalDate.of(2020, 5, 5), list.get(1).getDate());
    }
    
    @Test
    void storeDataTest() {
        boolean result = service.deleteAllData(); 
        assertTrue(result);
        
        DailyData one = new DailyData();
        DailyData two = new DailyData();
        
        one.setDate(LocalDate.of(2020, 5, 5));
        two.setDate(LocalDate.of(2020, 2, 5));
        
        service.storeData(one);
        service.storeData(two);
        
        List<DailyData> list = service.getAllDataAscending();
        
        assertEquals(2, list.size());
    }
    
    @Test
    void getDatesInRangeAscendingTest() {
        boolean result = service.deleteAllData(); 
        assertTrue(result);
        
        for(int i = 0; i < 100; i++) {
            DailyData data = new DailyData();
            data.setDate(LocalDate.now().plusDays(i));
            service.storeData(data);
        }

        /*
         * Check that I actually have 100 elements
         */
        List<DailyData> list = service.getAllDataAscending();
        assertEquals(100, list.size());
        
        LocalDate start = LocalDate.now().plusDays(20);
        LocalDate end = LocalDate.now().plusDays(29);
        
        list = service.getDatesInRangeAscending(start, end);
        
        assertEquals(10, list.size());
        assertEquals(start, list.get(0).getDate());
        assertEquals(end, list.get(list.size() - 1).getDate());
    }

}
