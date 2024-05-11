package com.valuelabs.livequiz.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.valuelabs.livequiz.model.dto.request.DeleteScheduleRequestDTO;
import com.valuelabs.livequiz.model.dto.request.ScheduleRequestDTO;
import com.valuelabs.livequiz.model.dto.request.UpdateScheduleRequestDTO;
import com.valuelabs.livequiz.model.dto.response.DisplayScheduleDTO;
import com.valuelabs.livequiz.model.dto.response.QuizScheduleAdminDTO;
import com.valuelabs.livequiz.model.dto.response.QuizScheduleDTO;
import com.valuelabs.livequiz.model.entity.Quiz;
import com.valuelabs.livequiz.model.entity.Scheduler;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.model.enums.Role;
import com.valuelabs.livequiz.service.scheduler.CreateSchedulerService;
import com.valuelabs.livequiz.service.scheduler.UpdateSchedulerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class SchedulerControllerTests {
    @Mock
    private CreateSchedulerService createSchedulerService;
    @Mock
    private UpdateSchedulerService updateSchedulerService;
    @InjectMocks
    private SchedulerController schedulerController;
    @Test
    void testCreateSchedule() {
        // Mocking
        ScheduleRequestDTO scheduleRequestDTO = new ScheduleRequestDTO("2024-01-15 12:00:00", "2024-01-15 14:00:00", 1L, List.of(1L, 2L));
        Scheduler scheduler = new Scheduler(1L,new Quiz(),new ArrayList<>(), new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"", Timestamp.valueOf("2024-01-15 12:00:00"), Timestamp.valueOf("2024-01-15 14:00:00"), null, false);
        Mockito.when(createSchedulerService.createScheduleWithUsers(scheduleRequestDTO)).thenReturn(scheduler);

        // Test
        ResponseEntity<?> responseEntity = schedulerController.createSchedule(scheduleRequestDTO);

        // Assertions
        Mockito.verify(createSchedulerService, Mockito.times(1)).createScheduleWithUsers(scheduleRequestDTO);
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody().equals(scheduler);
    }
    @Test
    void testCreateScheduleBadRequest() {
        // Mocking
        ScheduleRequestDTO scheduleRequestDTO = new ScheduleRequestDTO("2024-01-15 12:00:00", "2024-01-15T14:00:00", 1L, List.of(1L, 2L));
        Mockito.when(createSchedulerService.createScheduleWithUsers(scheduleRequestDTO)).thenReturn(null);

        // Test
        ResponseEntity<?> responseEntity = schedulerController.createSchedule(scheduleRequestDTO);

        // Assertions
        Mockito.verify(createSchedulerService, Mockito.times(1)).createScheduleWithUsers(scheduleRequestDTO);
        assert responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert responseEntity.getBody().equals("Schedule can't be created");
    }

    @Test
    void testGetScheduleById() {
        // Mocking
        Long schedulerId = 1L;
        List<DisplayScheduleDTO> displayScheduleDTOList = List.of(new DisplayScheduleDTO(1L,"User1","user1","user1@gmail.com", Role.RESPONDER));
        Mockito.when(createSchedulerService.getAllUsersInfoBySchedule(schedulerId)).thenReturn(displayScheduleDTOList);

        // Test
        ResponseEntity<?> responseEntity = schedulerController.getScheduleById(schedulerId);

        // Assertions
        Mockito.verify(createSchedulerService, Mockito.times(1)).getAllUsersInfoBySchedule(schedulerId);
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody().equals(displayScheduleDTOList);
    }
    @Test
    void testGetScheduleByIdNotFound() {
        // Mocking
        Long schedulerId = 1L;
        DisplayScheduleDTO displayScheduleDTO = new DisplayScheduleDTO(1L,"User1","user1","user1@gmail.com", Role.RESPONDER);
        Mockito.when(createSchedulerService.getAllUsersInfoBySchedule(schedulerId)).thenReturn(null);

        // Test
        ResponseEntity<?> responseEntity = schedulerController.getScheduleById(null);

        // Assertions
        Mockito.verify(createSchedulerService, Mockito.times(1)).getAllUsersInfoBySchedule(null);

        assert responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST;
        String body = responseEntity.getBody().toString();
        assert body.startsWith("Getting the schedule by id caused exception:");
    }


    @Test
    void testGetAllSchedulesByPerson() {
        // Mocking
        Long userId = 1L;
        List<QuizScheduleDTO> quizScheduleDTOList = List.of(new QuizScheduleDTO(1L,"2024-01-15 14:00:00","2024-01-15 14:00:00",1L,"Test Quiz", QuizType.POLL.toString(), 1 ));
        Mockito.when(createSchedulerService.getAllActiveSchedulesByPerson(userId)).thenReturn(quizScheduleDTOList);

        // Test
        ResponseEntity<?> responseEntity = schedulerController.getAllSchedulesByPerson(userId);

        // Assertions
        Mockito.verify(createSchedulerService, Mockito.times(1)).getAllActiveSchedulesByPerson(userId);
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody().equals(quizScheduleDTOList);
    }

    @Test
    void testGetAllSchedules() {
        // Mocking
        List<QuizScheduleAdminDTO> quizScheduleDTOList = List.of(new QuizScheduleAdminDTO(1L,"2024-01-15 14:00:00","2024-01-15 14:00:00",1L,"Test Quiz", QuizType.POLL.toString(), 1 ,1));
        Mockito.when(createSchedulerService.getAllActiveSchedules()).thenReturn(quizScheduleDTOList);

        // Test
        ResponseEntity<?> responseEntity = schedulerController.getAllSchedules();

        // Assertions
        Mockito.verify(createSchedulerService, Mockito.times(1)).getAllActiveSchedules();
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody().equals(quizScheduleDTOList);
    }

    @Test
    void testUpdateScheduleWithReason() {
        // Mocking
        Long schedulerId = 1L;
        UpdateScheduleRequestDTO updateScheduleRequestDTO = new UpdateScheduleRequestDTO("2024-01-15T12:00:00",
                "2024-01-15T14:00:00", List.of(3L), List.of(4L), "Updated for a reason");

        Mockito.when(updateSchedulerService.updateScheduleWithReason(schedulerId, updateScheduleRequestDTO))
                .thenReturn(true);

        // Test
        ResponseEntity<?> responseEntity = schedulerController.updateScheduleWithReason(schedulerId, updateScheduleRequestDTO);

        // Assertions
        Mockito.verify(updateSchedulerService, Mockito.times(1)).updateScheduleWithReason(schedulerId, updateScheduleRequestDTO);
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody().equals("Schedule updated with the reason");
    }


    @Test
    void testUpdateScheduleWithReasonConflict() {
        // Mocking
        Long schedulerId = 1L;
        UpdateScheduleRequestDTO updateScheduleRequestDTO = new UpdateScheduleRequestDTO("2024-01-15T12:00:00",
            "2024-01-15T14:00:00", List.of(3L), List.of(4L), "Updated for a reason");

        Mockito.when(updateSchedulerService.updateScheduleWithReason(schedulerId, updateScheduleRequestDTO))
            .thenReturn(false);

        // Test
        ResponseEntity<?> responseEntity = schedulerController.updateScheduleWithReason(schedulerId, updateScheduleRequestDTO);

        // Assertions
        Mockito.verify(updateSchedulerService, Mockito.times(1)).updateScheduleWithReason(schedulerId, updateScheduleRequestDTO);
        assert responseEntity.getStatusCode() == HttpStatus.CONFLICT;
        assert responseEntity.getBody().equals("Schedule can't be updated since user has already responded");
    }
    @Test
    void testUpdateScheduleWithReason_BadRequest(){
        Long schedulerId = 100L;
        UpdateScheduleRequestDTO updateScheduleRequestDTONull = new UpdateScheduleRequestDTO("2024-01-25 12:00:00",
                "2024-01-25 14:00:00", List.of(3L), List.of(4L), "Updated for a reason");
        Mockito.when(updateSchedulerService.updateScheduleWithReason(100L,updateScheduleRequestDTONull)).thenReturn(null);
        ResponseEntity<?> responseEntity = schedulerController.updateScheduleWithReason(100L, updateScheduleRequestDTONull);

        // Assertions
        Mockito.verify(updateSchedulerService, Mockito.times(1)).updateScheduleWithReason(schedulerId, updateScheduleRequestDTONull);
        assert responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST;

    }
    @Test
    void testDeleteScheduleWithReason() {
        // Mocking
        Long schedulerId = 1L;
        DeleteScheduleRequestDTO deleteScheduleRequestDTO = new DeleteScheduleRequestDTO("Deleted for a reason");

        Mockito.when(updateSchedulerService.deleteScheduleById(schedulerId, deleteScheduleRequestDTO.reason()))
                .thenReturn(true);

        // Test
        ResponseEntity<?> responseEntity = schedulerController.deleteScheduleWithReason(schedulerId, deleteScheduleRequestDTO);

        // Assertions
        Mockito.verify(updateSchedulerService, Mockito.times(1)).deleteScheduleById(schedulerId, deleteScheduleRequestDTO.reason());
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody().equals("Schedule has been deleted");
    }
    @Test
    void testDeleteScheduleWithReasonConflict() {
        // Mocking
        Long schedulerId = 1L;
        DeleteScheduleRequestDTO deleteScheduleRequestDTO = new DeleteScheduleRequestDTO("Deleted for a reason");

        Mockito.when(updateSchedulerService.deleteScheduleById(schedulerId, deleteScheduleRequestDTO.reason()))
                .thenReturn(false);

        // Test
        ResponseEntity<?> responseEntity = schedulerController.deleteScheduleWithReason(schedulerId, deleteScheduleRequestDTO);

        // Assertions
        Mockito.verify(updateSchedulerService, Mockito.times(1)).deleteScheduleById(schedulerId, deleteScheduleRequestDTO.reason());
        assert responseEntity.getStatusCode() == HttpStatus.CONFLICT;
        assert responseEntity.getBody().equals("Schedule can't be Updated since already a user has responded");
    }
    @Test
    void testDeleteScheduleWithReason_BadRequest(){
        Long schedulerId = 200L;
        DeleteScheduleRequestDTO deleteScheduleRequestDTO = new DeleteScheduleRequestDTO("Deleted for other reason");
        Mockito.when(updateSchedulerService.deleteScheduleById(schedulerId, deleteScheduleRequestDTO.reason()))
                .thenReturn(null);
        ResponseEntity<?> responseEntity = schedulerController.deleteScheduleWithReason(schedulerId, deleteScheduleRequestDTO);
        assert responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST;

    }
    @Test
    void testGetAllSchedules_BadRequest(){
        Mockito.when(createSchedulerService.getAllActiveSchedules()).thenThrow(RuntimeException.class);
        ResponseEntity<?> responseEntity = schedulerController.getAllSchedules();
        assert responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST;
    }
    @Test
    void testGetAllSchedulesByPerson_BadRequest(){
        Long userId = 222L;
        Mockito.when(createSchedulerService.getAllActiveSchedulesByPerson(userId)).thenThrow(new RuntimeException());
        ResponseEntity<?> responseEntity = schedulerController.getAllSchedulesByPerson(userId);
        assert responseEntity.getStatusCode()==HttpStatus.BAD_REQUEST;


    }
}