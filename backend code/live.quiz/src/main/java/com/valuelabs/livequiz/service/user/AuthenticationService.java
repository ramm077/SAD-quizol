package com.valuelabs.livequiz.service.user;
import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceExistsException;
import com.valuelabs.livequiz.model.dto.request.LoginDTO;
import com.valuelabs.livequiz.model.dto.request.UserCreationDTO;
import com.valuelabs.livequiz.model.dto.response.JWTAuthResponse;
import com.valuelabs.livequiz.model.entity.User;
import com.valuelabs.livequiz.repository.UserRepository;
import com.valuelabs.livequiz.security.CustomUserDetails;
import com.valuelabs.livequiz.security.JWTServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.valuelabs.livequiz.exception.ExceptionUtility.throwInvalidResourceDetailsException;
import static com.valuelabs.livequiz.exception.ExceptionUtility.throwResourceExistsException;
import static com.valuelabs.livequiz.utils.InputValidator.*;

/**
 * Service for user authentication and authorization.
 */
@Service
@Slf4j
public class AuthenticationService {
    private final DisplayUserService displayUserService;
    private final AuthenticationManager authenticationManager;
    private final JWTServiceImpl jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    /**
     * Constructor for AuthenticationService.
     * @param displayUserService    The DisplayUserService is used for fetching user details.
     * @param authenticationManager The AuthenticationManager is used for authentication of user credentials.
     * @param jwtService            The JWTServiceImpl is used for generation of JWT token.
     * @param userRepository        Repository for User entities.
     * @param passwordEncoder       for encoding normal password.
     */
    @Autowired
    public AuthenticationService(DisplayUserService displayUserService, AuthenticationManager authenticationManager, JWTServiceImpl jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.displayUserService = displayUserService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    /**
     * Perform user login and generate JWT token.
     * @param loginDTO The LoginDTO containing login details.
     * @return JWTAuthResponse with the generated token and user details.
     * Validate the LoginDTO before proceeding with the login operation.
     */
    public JWTAuthResponse login(LoginDTO loginDTO) {
        log.info("Inside AuthenticationService, login method!, validating LoginDTO and trying to authenticate the user!");
        validateDTO(loginDTO);
        log.debug("EmailId: "+ loginDTO.emailId() + " password: "+ loginDTO.password());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.emailId(), loginDTO.password()));
        User user = displayUserService.getUserByEmailIdAndInActive(loginDTO.emailId(), false);
        log.info("User authenticated successfully, trying to generate the JWT-TOKEN!");
        String jwt = jwtService.generateToken(new CustomUserDetails(user));
        log.info("Successfully generated the token, returning JWTAuthResponse!");
        return new JWTAuthResponse(jwt,user.getUserId(), user.getFirstName(), user.getLastName(), user.getEmailId(), user.getRole());
    }
    /**
     * Creates a new user based on the provided UserCreationDTO.
     * @param userCreationDTO DTO containing details for user creation.
     * @return The created User entity.
     * @throws ResourceExistsException if a user with the same email already exists.
     * @throws InvalidResourceDetailsException if validation of the provided DTO fails.
     */
    public User defaultUserCreation(UserCreationDTO userCreationDTO){
        log.info("Creating user with details");
        validateDTO(userCreationDTO);
        validatePassword(userCreationDTO.password());
        validateEmail(userCreationDTO.emailId());
        log.info("DTOs validated successfully");
        log.debug("FirstName: " + userCreationDTO.firstName()+" EmailId: "+userCreationDTO.emailId()+" Password: "+userCreationDTO.password());
        if(userRepository.findByEmailIdAndInActive(userCreationDTO.emailId(), false) == null) {
            log.info("user doesn't exist so will be created, and saved in database!");
            return saveUser(new User(userCreationDTO.firstName(), userCreationDTO.lastName(), userCreationDTO.emailId(), passwordEncoder.encode(userCreationDTO.password()), userCreationDTO.phoneNumber(),userCreationDTO.firstName()+userCreationDTO.lastName()));
        } else {
            log.error("User with emailId: "+ userCreationDTO.emailId() + " is already present in the database!, throwing ResourceExistsException!");
            throwResourceExistsException("User","User with given EmailId: "+userCreationDTO.emailId()+" already exists!");
        }
        return null;
    }
    /**
     * Saves a user entity.
     * @param user  The User entity to be saved.
     * @return The saved User entity.
     * @throws InvalidResourceDetailsException if the provided user is null.
     */
    public User saveUser(User user){
        if(user != null) {
            log.info("User created successfully!");
            return userRepository.save(user);
        }log.error("User not provided can't create user!");
        throwInvalidResourceDetailsException("User","user not provided!");
        return null;
    }
    /**
     * This method is used to fetch the username from the security context.
     * @return String username if the user details are present in security context.
     */
    public String getCurrentUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Inside SecurityContextHolder, trying to fetch username from security context details!");
        if(authentication != null && authentication.isAuthenticated()){
            String name = authentication.getName();
            User user = userRepository.findByEmailIdAndInActive(name,false);
            log.debug("Username: " + user.getUserName());
            log.info("Fetched username returning it!");
            return user.getUserName();
        }
        return null;
    }
}
