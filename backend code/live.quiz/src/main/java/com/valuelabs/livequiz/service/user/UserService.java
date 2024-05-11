package com.valuelabs.livequiz.service.user;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.dto.request.LoginDTO;
import com.valuelabs.livequiz.model.dto.request.UpdateUserDTO;
import com.valuelabs.livequiz.model.dto.request.UserCreationDTO;
import com.valuelabs.livequiz.model.entity.User;
import com.valuelabs.livequiz.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.valuelabs.livequiz.exception.ExceptionUtility.throwResourceExistsException;
import static com.valuelabs.livequiz.utils.InputValidator.*;

/**
 * Service class for managing User entities, including creation, authentication, and password changes.
 */
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final DisplayUserService displayUserService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    /**
     * Constructor for UserService.
     *
     * @param userRepository        Repository for User entities.
     * @param displayUserService    Service for displaying user information.
     * @param passwordEncoder       for encoding normal password.
     * @param authenticationService for getting the user details from security context
     */
    @Autowired
    public UserService(UserRepository userRepository, DisplayUserService displayUserService, PasswordEncoder passwordEncoder, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.displayUserService = displayUserService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationService = authenticationService;
    }
    /**
     * Adds a new user based on the provided UserCreationDTO.
     * @param userCreationDTO The Data Transfer Object (DTO) containing user creation details.
     * @return The newly created user.
     * @throws InvalidResourceDetailsException If validation fails or the user already exists.
     */
    public User addUser(UserCreationDTO userCreationDTO){
        log.info("Creating user with details");
        validateDTO(userCreationDTO);
        validatePassword(userCreationDTO.password());
        validateEmail(userCreationDTO.emailId());
        log.debug("DTOs validated successfully");
        if(userRepository.findByEmailIdAndInActive(userCreationDTO.emailId(), false) == null) {
            log.debug("user doesn't exist so will be created");
            return userRepository.save(new User(userCreationDTO.firstName(), userCreationDTO.lastName(), userCreationDTO.emailId(), passwordEncoder.encode(userCreationDTO.password()), userCreationDTO.phoneNumber(), authenticationService.getCurrentUserName(),userCreationDTO.role()));
        } else throwResourceExistsException("User","User with given EmailId: "+userCreationDTO.emailId()+" already exists!");
        return null;
    }
    /**
     * Changes the password for a user based on the provided login credentials.
     * @param loginDTO  DTO containing user login credentials and new password.
     * @throws InvalidResourceDetailsException if credentials are invalid.
     */
    public void changePassword(LoginDTO loginDTO){
        validateDTO(loginDTO);
        User user = displayUserService.getUserByEmailIdAndInActive(loginDTO.emailId(), false);
        validatePassword(loginDTO.password());
        user.setPassword(passwordEncoder.encode(loginDTO.password()));
        userRepository.save(user);
    }
    /**
     * Updates user details based on the provided user ID and UpdateUserDTO.
     * @param id           The ID of the user to be updated.
     * @param updateUserDTO The Data Transfer Object (DTO) containing updated user details.
     * @return True if the update is successful, false otherwise.
     */
    public Boolean updateUserById(Long id, UpdateUserDTO updateUserDTO) {
        log.info("Updating user details by id"+id);
        try{
            User user = userRepository.findByUserIdAndInActive(id,false).orElseThrow(()-> new ResourceNotFoundException("User","User not found with id: "+id));
            log.info("User exists");
            user.setFirstName(updateUserDTO.firstName());
            user.setLastName(updateUserDTO.lastName());
            user.setPhoneNumber(updateUserDTO.phoneNumber());
            user.setUpdatedBy(authenticationService.getCurrentUserName());//user.setUpdatedBy(getCurrentPersonName());
            userRepository.save(user);
            log.info("User updated successfully");
            return true;
        }catch(Exception e){
            log.error("Updating user cause error:"+e);
            throw new RuntimeException();
        }
    }
    /**
     * Deletes an active user based on the provided user ID.
     * @param id The ID of the user to be deleted.
     * @return True if the deletion is successful, false otherwise.
     * @throws ResourceNotFoundException         If the user is not found.
     * @throws InvalidResourceDetailsException    If an error occurs during deletion.
     */
    public Boolean deleteActivePersonById(Long id) {
        log.info("Deleting an active user by id"+id);
        User user = userRepository.findByUserIdAndInActive(id,false)
                .orElseThrow(() -> new ResourceNotFoundException("User","User not found with id:" + id));
        user.setInActive(true); // Updated field name
        user.setUpdatedBy(authenticationService.getCurrentUserName());//user.setUpdatedBy(getCurrentPersonName());
        try {
            userRepository.save(user);
            log.info("User deleted successfully");
            return true;
        } catch (Exception e) {
            log.error("Deleting user caused error:"+e.getMessage());
            throw new InvalidResourceDetailsException("User","An error occurred while deleting the User: "+e);
        }
    }
    /**
     * Reactivates an inactive user based on the provided UserCreationDTO.
     * @param userCreationDTO The Data Transfer Object (DTO) containing user reactivation details.
     * @return True if the reactivation is successful, false if the user is not found.
     */
    public Boolean ifExistReactivateUser(UserCreationDTO userCreationDTO) {
        log.info("Reactivating inactive user, validating the UserCreationDTO");
        validateDTO(userCreationDTO);
        validatePassword(userCreationDTO.password());
        log.info("DTO & password validated successfully");
        Boolean reactivated=false;
        User user = userRepository.findByEmailIdAndInActive(userCreationDTO.emailId(), true);
        if(user!=null){
            log.debug("User "+ user.getUserName() + " is found");
            user.setFirstName(userCreationDTO.firstName());
            user.setLastName(userCreationDTO.lastName());
            user.setPhoneNumber(userCreationDTO.phoneNumber());
            user.setPassword(passwordEncoder.encode(userCreationDTO.password()));
            user.setInActive(false);
            user.setUpdatedBy(authenticationService.getCurrentUserName());
            userRepository.save(user);
            log.info("User reactivated successfully");
            return true;
        }
        log.info("User not found");
        return false;
    }
}
