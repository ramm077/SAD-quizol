package com.valuelabs.livequiz.service.user;

import com.valuelabs.livequiz.model.dto.request.LoginDTO;
import com.valuelabs.livequiz.model.dto.request.UserCreationDTO;
import com.valuelabs.livequiz.model.dto.response.JWTAuthResponse;
import com.valuelabs.livequiz.model.entity.User;
import com.valuelabs.livequiz.model.enums.Role;
import com.valuelabs.livequiz.repository.UserRepository;
import com.valuelabs.livequiz.security.CustomUserDetails;
import com.valuelabs.livequiz.security.JWTServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
    class AuthenticationServiceTest {

        @Mock
        private DisplayUserService displayUserService;

        @Mock
        private AuthenticationManager authenticationManager;

        @Mock
        private JWTServiceImpl jwtService;

        @Mock
        private UserRepository userRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @InjectMocks
        private AuthenticationService authenticationService;

        @Test
        void testLogin() {
            LoginDTO loginDTO = new LoginDTO("test@example.com", "password");

            User user = new User(1L, "John", "Doe", "test@example.com", "encodedPassword", "+123456789", Role.RESPONDER, false,
                    new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");

            Mockito.when(displayUserService.getUserByEmailIdAndInActive(Mockito.anyString(), Mockito.eq(false))).thenReturn(user);
            Mockito.when(jwtService.generateToken(Mockito.any(CustomUserDetails.class))).thenReturn("generatedToken");

            JWTAuthResponse jwtAuthResponse = authenticationService.login(loginDTO);

            Assertions.assertNotNull(jwtAuthResponse);
            Assertions.assertEquals("generatedToken", jwtAuthResponse.token());
            Assertions.assertEquals(1L, jwtAuthResponse.userId());
            Assertions.assertEquals("John", jwtAuthResponse.firstName());
            Assertions.assertEquals("Doe", jwtAuthResponse.lastName());
            Assertions.assertEquals("test@example.com", jwtAuthResponse.emailId());
            Assertions.assertEquals(Role.RESPONDER, jwtAuthResponse.role());

            Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));
        }
        @Test
        void testLogin_failure(){
            LoginDTO loginDTO = new LoginDTO("test@example.com", "@Password123");
            User user = new User(1L, "John", "Doe", "test@example.com", "encodedPassword", "+123456789", Role.RESPONDER, false,
            new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");
            Exception exception= assertThrows(Exception.class,() ->{
                authenticationService.login(loginDTO);
        });
            System.out.println("Hi is am here: "+exception.getMessage());

        }

        @Test
        void testCreateUser() {
            UserCreationDTO userCreationDTO = new UserCreationDTO("John", "Doe", "test@example.com", "@Password123", "+123456789",Role.RESPONDER);
            User user1=new User(1L,"John","Doe","test@example.com","@Password123","+123456789", Role.RESPONDER, false,new Timestamp(System.currentTimeMillis()),"Default",new Timestamp(System.currentTimeMillis()),"Default");
            Mockito.when(userRepository.findByEmailIdAndInActive(Mockito.anyString(), Mockito.eq(false))).thenReturn(null);
            Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");
            Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);
            User createdUser = authenticationService.defaultUserCreation(userCreationDTO);

            Assertions.assertNotNull(createdUser);
            Assertions.assertEquals("John", createdUser.getFirstName());
            Assertions.assertEquals("Doe", createdUser.getLastName());
            Assertions.assertEquals("test@example.com", createdUser.getEmailId());
            Assertions.assertEquals("+123456789", createdUser.getPhoneNumber());

            Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        }

        @Test
        void testSaveUser() {
            User user = new User(1L, "John", "Doe", "test@example.com", "encodedPassword", "+123456789", Role.RESPONDER, false,
                    new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");

            Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

            User savedUser = authenticationService.saveUser(user);

            Assertions.assertNotNull(savedUser);
            Assertions.assertEquals(1L, savedUser.getUserId());
            Assertions.assertEquals("John", savedUser.getFirstName());
            Assertions.assertEquals("Doe", savedUser.getLastName());
            Assertions.assertEquals("test@example.com", savedUser.getEmailId());
            Assertions.assertEquals("encodedPassword", savedUser.getPassword());
            Assertions.assertEquals("+123456789", savedUser.getPhoneNumber());
            Assertions.assertEquals(Role.RESPONDER, savedUser.getRole());
        }

        @Test
        void testGetCurrentUserName() {
            Authentication authentication = Mockito.mock(Authentication.class);
            SecurityContextHolder.setContext(new SecurityContextImpl(authentication));

            Mockito.when(authentication.isAuthenticated()).thenReturn(true);
            Mockito.when(authentication.getName()).thenReturn("test@example.com");

            User user = new User(1L, "John", "Doe", "test@example.com", "encodedPassword", "+123456789", Role.RESPONDER, false,
                    new Timestamp(System.currentTimeMillis()), "Default", new Timestamp(System.currentTimeMillis()), "Default");

            Mockito.when(userRepository.findByEmailIdAndInActive(Mockito.anyString(), Mockito.eq(false))).thenReturn(user);

            String currentUserName = authenticationService.getCurrentUserName();

            Assertions.assertEquals("JohnDoe", currentUserName);

            SecurityContextHolder.clearContext();
        }
    }
