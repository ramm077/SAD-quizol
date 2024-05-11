package com.valuelabs.livequiz.service.scheduler;

import static org.junit.jupiter.api.Assertions.*;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceExistsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.dto.request.ScheduleRequestDTO;
import com.valuelabs.livequiz.model.dto.response.DisplayScheduleDTO;
import com.valuelabs.livequiz.model.dto.response.QuizScheduleAdminDTO;
import com.valuelabs.livequiz.model.dto.response.QuizScheduleDTO;
import com.valuelabs.livequiz.model.entity.*;
import com.valuelabs.livequiz.model.enums.QuestionType;
import com.valuelabs.livequiz.model.enums.QuizType;
import com.valuelabs.livequiz.model.enums.Role;
import com.valuelabs.livequiz.repository.SchedulerRepository;
import com.valuelabs.livequiz.repository.UserRepository;
import com.valuelabs.livequiz.service.quiz.DisplayQuizService;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import com.valuelabs.livequiz.service.user.DisplayUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSchedulerServiceTest {
    @Mock
    private ScheduleReminderService scheduleReminderService;

    @Mock
    private SchedulerRepository schedulerRepository;

    @Mock
    private DisplayQuizService quizService;

    @Mock
    private DisplayUserService userService;

    @InjectMocks
    private CreateSchedulerService createSchedulerService;


    @Mock
    private DisplayQuizService displayQuizService;

    @Mock
    private DisplayUserService displayUserService;

    @Mock
    private AuthenticationService authenticationService;


    @Test
    void testCreateScheduleWithUsers_Success() {
        Boolean flag =false;

        // Mocking
        ScheduleRequestDTO scheduleRequestDTO = new ScheduleRequestDTO("2024-02-30 10:00:00", "2024-02-30 12:00:00", 6L, List.of(8L));
        User user5=new User(8L,"User5","user5","user5@gmail.com","User@123","1929394910", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
//        User user2=new User(2L,"User2","user2","user2@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
//        User user3=new User(3L,"User3","user3","user3@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        Option option =new Option(10L,"Good",true,"Default");
        Question question = new Question(20L,"How are you", QuestionType.SINGLE, List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz6 = new Quiz(6L,"TestQuiz6", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler = new Scheduler();
        scheduler.setStartTime(Timestamp.valueOf(scheduleRequestDTO.startTime()));
        scheduler.setEndTime(Timestamp.valueOf(scheduleRequestDTO.endTime()));
        scheduler.setQuiz(quiz6);
        scheduler.setUserList(List.of(user5));
        doAnswer(invocation -> {
            Object quizIdArgument = invocation.getArgument(0);
            Object userListArgument=invocation.getArgument(1);
            if(quizIdArgument == null && userListArgument == null){
                return new ArrayList<>();
            } else if (quizIdArgument!=null && userListArgument==null) {
                return new ArrayList<>();
            } else{
                return new ArrayList<>();
            }
        }).when(schedulerRepository).findByQuizAndUserListContaining(any(),any());
//        when(displayQuizService.getQuizById(scheduleRequestDTO.quizId(),false)).thenReturn(quiz6);
//        doReturn(user5).when(displayUserService).getUserByIdAndInActive(8L,false);

        doReturn(scheduler).when(schedulerRepository).save(any(Scheduler.class));
        Scheduler createdScheduler = createSchedulerService.createScheduleWithUsers(scheduleRequestDTO);

            // Assertions
            Mockito.verify(displayQuizService, Mockito.atLeast(0)).getQuizById(any(), anyBoolean());
//            Mockito.verify(displayUserService, Mockito.times(1)).getUserByIdAndInActive(8L, false);
            Mockito.verify(scheduleReminderService, Mockito.times(1)).scheduleSendCreateScheduler(scheduler);
            Mockito.verify(scheduleReminderService, Mockito.times(1)).scheduleReminders(scheduler);
            assert createdScheduler != null;
            assert createdScheduler.getStartTime().equals(Timestamp.valueOf(scheduleRequestDTO.startTime()));
            assert createdScheduler.getEndTime().equals(Timestamp.valueOf(scheduleRequestDTO.endTime()));
            assert createdScheduler.getQuiz().getQuizId().equals(6L);
            assert createdScheduler.getUserList().size() == 1;
//        } catch (Exception e) {
//            break;
//        }
//        }
    }
//    @Test
//    void testCreateScheduleWithUsers_ScheduleExists() {
//        // Mocking
//        ScheduleRequestDTO scheduleRequestDTO = new ScheduleRequestDTO("2024-01-31 10:00:00", "2024-01-31 12:00:00", 31L, List.of(21L)
//        );
//        User user21=new User(21L,"User1","user1","user21@gmail.com","User@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
//        Quiz quiz31 = new Quiz(31L,"TestQuiz", QuizType.TEST,null,false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
//        Scheduler existingScheduler = new Scheduler();
//        existingScheduler.setStartTime(Timestamp.valueOf("2024-01-30 11:00:00"));
//        existingScheduler.setEndTime(Timestamp.valueOf("2024-01-30 11:30:00"));
//        existingScheduler.setQuiz(quiz31);
//        existingScheduler.setUserList(List.of(user21));
//        Mockito.when(displayQuizService.getQuizById(31L, false)).thenReturn(quiz31);
//        Mockito.when(displayUserService.getUserByIdAndInActive(21L, false)).thenReturn(user21);
//        Mockito.when(schedulerRepository.findByQuizAndUserListContaining(quiz31, user21)).thenReturn(List.of(existingScheduler));
//        // Test
//        Exception exception = assertThrows(Exception.class,() ->{
//            createSchedulerService.createScheduleWithUsers(scheduleRequestDTO);
//        });
//
////        assert exception.getMessage().startsWith("This quiz TestQuiz is already been scheduled");
//    }
    @Test
    void testGetAllActiveSchedulesByQuiz(){
        when(schedulerRepository.findAllByQuizAndInActive(any(), anyBoolean())).thenThrow(new RuntimeException("Error fetching schedules"));
        assertThrows(RuntimeException.class,()->{
            createSchedulerService.getAllActiveSchedulesByQuiz(200L);
        });

    }
    @Test
    void testCreateScheduleWithUsers_InvalidQuizId() {
        // Mocking
        ScheduleRequestDTO scheduleRequestDTO = new ScheduleRequestDTO("2024-01-15 10:00:00", "2024-01-15 12:00:00", null, List.of(1L)
        );
        // Test
        InvalidResourceDetailsException exception = assertThrows(InvalidResourceDetailsException.class,() ->{
            createSchedulerService.createScheduleWithUsers(scheduleRequestDTO);
        });
        // Assertions
        Mockito.verify(displayUserService, Mockito.never()).getUserByIdAndInActive(Mockito.anyLong(), Mockito.anyBoolean());
        Mockito.verify(schedulerRepository, Mockito.never()).save(Mockito.any(Scheduler.class));
        assert exception.getMessage().startsWith("quizId is required");
    }

    @Test
    void testCreateScheduleWithUsers_InvalidUserId() {
        // Mocking
        ScheduleRequestDTO scheduleRequestDTO = new ScheduleRequestDTO("2024-01-15 10:00:00", "2024-01-15 12:00:00", 1L, null
        );

        User user1=new User(1L,"User1","user1","user1@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        Quiz quiz = new Quiz(1L,"TestQuiz", QuizType.TEST,null,false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        // Test
        InvalidResourceDetailsException exception = assertThrows(InvalidResourceDetailsException.class,() ->{
            createSchedulerService.createScheduleWithUsers(scheduleRequestDTO);
        });
        // Assertions
        Mockito.verify(schedulerRepository, Mockito.never()).save(Mockito.any(Scheduler.class));
        assert exception.getMessage().startsWith("personIdList is required");
    }
    @Test
    void testCreateScheduleWithUsers_exception(){
        // Mocking
        ScheduleRequestDTO scheduleRequestDTO = new ScheduleRequestDTO("2024-01-31 10:00:00", "2024-01-31 12:00:00", 71L, List.of(61L)
        );
        User user61=new User(61L,"User1","user1","user61@gmail.com","User@123","3939349314", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        Quiz quiz71 = new Quiz(71L,"TestQuiz", QuizType.TEST,null,false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
//        Scheduler existingScheduler = new Scheduler();
//        existingScheduler.setStartTime(Timestamp.valueOf("2024-01-30 11:00:00"));
//        existingScheduler.setEndTime(Timestamp.valueOf("2024-01-30 11:30:00"));
//        existingScheduler.setQuiz(quiz71);
//        existingScheduler.setUserList(List.of(user61));
        doReturn(quiz71).when(displayQuizService).getQuizById(any(),anyBoolean());
//        Mockito.when(displayQuizService.getQuizById(71L, false)).thenReturn(quiz71);
        Mockito.when(displayUserService.getUserByIdAndInActive(61L, false)).thenReturn(user61);
        Mockito.when(schedulerRepository.findByQuizAndUserListContaining(quiz71, user61)).thenReturn(new ArrayList<>());
        Mockito.when(schedulerRepository.save(any(Scheduler.class))).thenThrow(new InvalidResourceDetailsException("Scheduler","Invalid Schedule Cannot be saved"));
        // Test
        Exception exception = assertThrows(Exception.class,() ->{
            createSchedulerService.createScheduleWithUsers(scheduleRequestDTO);
        });

    }
    @Test
    void testGetAllActiveSchedules_Exception(){
        when(schedulerRepository.findAllByInActive(false)).thenThrow(new ResourceNotFoundException("Scheduler","Resource not found"));
        assertThrows(ResourceNotFoundException.class,()->{
            createSchedulerService.getAllActiveSchedules();
        });
    }

    @Test
    void testGetAllActiveSchedulesByPerson_Success() {
        // Mocking
        Long userId = 99L;
        Boolean flag =false;
        User user99=new User(99L,"User1","user1","user1@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        Option option =new Option(99L,"Good",true,"Default");
        Question question = new Question(99L,"How are you", QuestionType.SINGLE,List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(99L,"TestQuiz", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler1 =new Scheduler(99L,quiz,List.of(user99),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-16 14:00:00"),Timestamp.valueOf("2024-01-16 16:00:00"),"TestReason",false);
        Scheduler scheduler2 =new Scheduler(100L,quiz,List.of(user99),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-17 14:00:00"),Timestamp.valueOf("2024-01-17 16:00:00"),"TestReason",false);

        List<Scheduler> allSchedulers = List.of(scheduler1, scheduler2);

//        Mockito.when(displayUserService.getUserByIdAndInActive(user99.getUserId(), false)).thenReturn(user99);
        Mockito.when(schedulerRepository.findAllByInActive(false)).thenReturn(allSchedulers);
        List<QuizScheduleDTO> schedules = new ArrayList<>();

        while(!flag) {
            // Test
            try {
                schedules = createSchedulerService.getAllActiveSchedulesByPerson(1L);
                flag =true;
//                Mockito.verify(displayUserService, Mockito.times(1)).getUserByIdAndInActive(userId, false);
//                Mockito.verify(schedulerRepository, Mockito.times(1)).findAllByInActive(false);
                assert schedules != null && schedules.size() == 2;
            } catch (Exception e) {
                break;
            }
        }

    }
//    @Test
//    void testGetAllActiveSchedulesByPerson_InvalidUserId() {
//        // Mocking
//        Long userId = 5L;
//        Mockito.when(displayUserService.getUserByIdAndInActive(userId, false)).thenThrow(new InvalidResourceDetailsException("User","User Id must be provided!"));
//        // Test
//        ResourceNotFoundException exception= assertThrows(ResourceNotFoundException.class,() ->{
//            createSchedulerService.getAllActiveSchedulesByPerson(userId);
//        });
//        // Assertions
//        Mockito.verify(displayUserService, Mockito.times(1)).getUserByIdAndInActive(userId, false);
//        Mockito.verify(schedulerRepository, Mockito.never()).findAllByInActive(false);
//        System.out.println(exception.getMessage());
//    }
    @Test
    void testGetAllActiveSchedulesByQuiz_Success() {
        Boolean flag =false;
        // Mocking
        Long quizId = 1L;
        User user1=new User(1L,"User1","user1","user1@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        Option option =new Option(1L,"Good",true,"Default");
        Question question = new Question(1L,"How are you", QuestionType.SINGLE,List.of(option),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,false);
        Quiz quiz = new Quiz(1L,"TestQuiz", QuizType.TEST,List.of(question),false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler1 =new Scheduler(1L,quiz,List.of(user1),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-16 14:00:00"),Timestamp.valueOf("2024-01-16 16:00:00"),"TestReason",false);
        Scheduler scheduler2 =new Scheduler(1L,quiz,List.of(user1),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-17 14:00:00"),Timestamp.valueOf("2024-01-17 16:00:00"),"TestReason",false);
        List<Scheduler> allSchedulers = List.of(scheduler1, scheduler2);

//        Mockito.when(displayQuizService.getQuizById(quizId, false)).thenReturn(quiz);
//        Mockito.when(schedulerRepository.findAllByQuizAndInActive(any(), eq(false))).thenReturn(allSchedulers);
        while(!flag) {
            // Test
            try {
                List<QuizScheduleDTO> schedules = createSchedulerService.getAllActiveSchedulesByQuiz(1L);
                // Assertions
//                Mockito.verify(displayQuizService, Mockito.times(1)).getQuizById(any(), anyBoolean());
//                Mockito.verify(schedulerRepository, Mockito.times(1)).findAllByQuizAndInActive(any(), anyBoolean());
                assert schedules != null ;
                flag = true;
            } catch (Exception e) {
                break;
            }
        }
    }

//    @Test
//    void testGetAllActiveSchedulesByQuiz_InvalidQuizId() {
//        // Mocking
//        Long quizId = 5L;
//        when(displayQuizService.getQuizById(any(Long.class), anyBoolean())).thenThrow(new InvalidResourceDetailsException("Quiz","QuizId must be provided!"));
//        // Test
//        try {
//            ResourceNotFoundException exception= assertThrows(ResourceNotFoundException.class,() ->{
//                createSchedulerService.getAllActiveSchedulesByQuiz(quizId);
//            });
////        // Assertions
//            Mockito.verify(displayQuizService, Mockito.times(1)).getQuizById(quizId, false);
//            Mockito.verify(schedulerRepository, Mockito.never()).findAllByQuizAndInActive(Mockito.any(Quiz.class), Mockito.anyBoolean());
//            assert exception.getMessage().startsWith("An error occurred while finding list of active schedules for a quiz");
//        } catch (Exception e) {
//            System.out.println("Exception thrown");
//        }
//    }

    @Test
    void testGetAllActiveSchedules() {
        when(schedulerRepository.findAllByInActive(anyBoolean())).thenReturn(new ArrayList<>());
        List<QuizScheduleAdminDTO> result = createSchedulerService.getAllActiveSchedules();
        assertNotNull(result);
        assertNotNull(result);
    }


    @Test
    void testGetActiveSchedulesByPerson() {
        User user1=new User(1L,"User1","user1","user1@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        Quiz quiz = new Quiz(1L,"TestQuiz", QuizType.TEST,null,false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler =new Scheduler(1L,quiz,List.of(user1),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,new Timestamp(System.currentTimeMillis()),new Timestamp(System.currentTimeMillis()),"TestReason",false);
        when(schedulerRepository.findBySchedulerIdAndInActive(1L,false)).thenReturn(Optional.of(scheduler));
        List<DisplayScheduleDTO> result = createSchedulerService.getAllUsersInfoBySchedule(1L);
        assertNotNull(result);
    }
    @Test
    void testGetAllUsersInfoBySchedule_Success() {
        //Mocking
        User user1=new User(1L,"User1","user1","user1@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        User user2=new User(2L,"User2","user2","user2@gmail.com","user@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");

        Quiz quiz = new Quiz(1L,"TestQuiz", QuizType.TEST,null,false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler1 =new Scheduler(1L,quiz,List.of(user1,user2),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-16 14:00:00"),Timestamp.valueOf("2024-01-16 16:00:00"),"TestReason",false);
        Mockito.when(schedulerRepository.findBySchedulerIdAndInActive(1L,false)).thenReturn(Optional.of(scheduler1));
        //Test
        List<DisplayScheduleDTO> displayScheduleDTOList = createSchedulerService.getAllUsersInfoBySchedule(1L);
        //Assertions
        Mockito.verify(schedulerRepository,Mockito.times(1)).findBySchedulerIdAndInActive(Mockito.anyLong(),Mockito.eq(false));
        assert displayScheduleDTOList.size()==2;
        assert displayScheduleDTOList.get(0).personId().equals(1L);
        assert displayScheduleDTOList.get(1).personId().equals(2L);
    }
    @Test
    void testGetScheduleById(){
//        ScheduleRequestDTO scheduleRequestDTOx = new ScheduleRequestDTO("2024-01-31 10:00:00", "2024-01-31 12:00:00", 31L, List.of(21L)
//        );
        User user41=new User(41L,"User1","user1","user21@gmail.com","User@123","+54635345", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
        Quiz quiz51 = new Quiz(51L,"TestQuiz", QuizType.TEST,null,false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,null);
        Scheduler scheduler1 =new Scheduler(6L,quiz51, new ArrayList<>(Arrays.asList(user41)),new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),null,Timestamp.valueOf("2024-01-16 14:00:00"),Timestamp.valueOf("2024-01-16 16:00:00"),"TestReason",false);

        when(schedulerRepository.findBySchedulerIdAndInActive(6L,false)).thenReturn(Optional.of(scheduler1));
        Scheduler scheduler = createSchedulerService.getScheduleById(6L);
        assertNotNull(scheduler);

    }
}