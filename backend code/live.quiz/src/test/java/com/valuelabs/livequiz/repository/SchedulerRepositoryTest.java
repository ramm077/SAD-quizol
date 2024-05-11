//package com.valuelabs.livequiz.repository;
//
//import com.valuelabs.livequiz.model.entity.Scheduler;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//@DataJpaTest
//class SchedulerRepositoryTest {
//    @Mock
//    private SchedulerRepository schedulerRepository;
//    @Test
//    void testFindAllByInActive() {
//        boolean inActive = false;
//        List<Scheduler> expectedSchedulers = Arrays.asList(new Scheduler(), new Scheduler());
//
//        when(schedulerRepository.findAllByInActive(inActive)).thenReturn(expectedSchedulers);
//
//        List<Scheduler> result = schedulerRepository.findAllByInActive(inActive);
//
//        assertEquals(expectedSchedulers, result);
//    }
//
//}