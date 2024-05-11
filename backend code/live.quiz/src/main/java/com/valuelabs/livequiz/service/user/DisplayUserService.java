package com.valuelabs.livequiz.service.user;

import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.exception.ResourceNotFoundException;
import com.valuelabs.livequiz.model.dto.response.DisplayUserDTO;
import com.valuelabs.livequiz.model.entity.User;
import com.valuelabs.livequiz.model.enums.Role;
import com.valuelabs.livequiz.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.valuelabs.livequiz.exception.ExceptionUtility.throwInvalidResourceDetailsException;
import static com.valuelabs.livequiz.exception.ExceptionUtility.throwResourceNotFoundException;
import static com.valuelabs.livequiz.utils.ResourceValidator.validateResource;
/**
 * Service class for displaying user information.
 * @Service
 */
@Lazy
@Service
@Slf4j
public class DisplayUserService {
    private final UserRepository userRepository;
    /**
     * Constructs a DisplayUserService with the specified UserRepository.
     * @param userRepository Repository for accessing user data.
     */
    @Lazy
    @Autowired
    public DisplayUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    /**
     * Retrieves a user by ID and active status.
     * @param userId   The ID of the user to retrieve.
     * @param inActive Flag indicating whether to include inactive users.
     * @return The user with the specified ID.
     * @throws ResourceNotFoundException if no user is found.
     * @throws InvalidResourceDetailsException if the user ID is not provided.
     */
    public User getUserByIdAndInActive(Long userId, Boolean inActive){
        log.info("Inside DisplayUserService, getUserByIdAndInActive method!");
        if(userId != null) {
            log.debug("Retrieving user with id {} and active status {}", userId, inActive);
            User user =  userRepository.findByUserIdAndInActive(userId,inActive).orElseThrow(() ->{
                log.error("No user found with id {} and active status {}", userId, inActive);
                throwResourceNotFoundException("User","No " + (inActive ? "inactive" : "active")+ " user found with userId "+ userId); return null;});
            log.info("User retrieved successfully");
            return user;
        }
        else {
            log.error("User Id must be provided!");
            throwInvalidResourceDetailsException("User","User Id must be provided!");
        } return null;
    }
    /**
     * Retrieves a user by email ID and active status.
     * @param emailId  The email ID of the user to retrieve.
     * @param inActive Flag indicating whether to include inactive users.
     * @return The user with the specified email ID.
     * @throws ResourceNotFoundException if no user is found.
     * @throws InvalidResourceDetailsException if the email ID is not provided.
     */
    public User getUserByEmailIdAndInActive(String emailId, Boolean inActive){
        log.info("Inside DisplayUserService, getUserByEmailIdAndInActive method!");
        if(emailId != null) {
            log.debug("Retrieving user with email id {} and active status {}", emailId, inActive);
            if (userRepository.findByEmailIdAndInActive(emailId, inActive) != null) {
                log.info("User retrieved successfully");
                return userRepository.findByEmailIdAndInActive(emailId, inActive);
            } else{
                log.error("No user found with email id {} and active status {}", emailId, inActive);
                throwResourceNotFoundException("User","No " + (inActive ? "inactive" : "active") + " user found with emailId : " + emailId);
            }
        } log.error("Email Id must be provided!");
        throwInvalidResourceDetailsException("User","EmailId should be provided!");
        return null;
    }
    /**
     * Retrieves all users with the specified role and active status.
     * @param role     The role of the users to retrieve.
     * @param inActive Flag indicating whether to include inactive users.
     * @return List of users with the specified role.
     * @throws ResourceNotFoundException if no users are found.
     */
    public List<User> findAllUsers(Role role, Boolean inActive) {
        log.info("Inside DisplayUserService, findAllUsers method!");
        log.debug("Retrieving all users with role {} and active status {}", role, inActive);
        validateResource(List.of("User","Responder's",userRepository.findAllByRoleAndInActive(role,inActive)),false);
        log.debug("{} users retrieved successfully", userRepository.findAllByRoleAndInActive(role,inActive).size());
        return userRepository.findAllByRoleAndInActive(role, inActive);
    }
    /**
     * Displays details of all users with the specified role and active status.
     * @param role     The role of the users to display.
     * @param inActive Flag indicating whether to include inactive users.
     * @return List of user details.
     */
    public List<DisplayUserDTO> displayAllUsers(Role role, Boolean inActive){
        log.info("Inside DisplayUserService, displayAllUsers method!");
        log.debug("Displaying all users with role {} and active status {}", role, inActive);
        List<DisplayUserDTO> displayUsers =  findAllUsers(role, inActive).stream().map(user -> new DisplayUserDTO(user.getUserId(),
                user.getFirstName(), user.getLastName(), user.getEmailId(), user.getPhoneNumber(),
                user.getRole())).toList();
        log.debug("{} users displayed successfully", displayUsers.size());
        return displayUsers;
    }
    /**
     * Displays details of a specific user by ID and active status.
     * @param userId   The ID of the user to display.
     * @param inActive Flag indicating whether to include inactive users.
     * @return Details of the specified user.
     */
    public DisplayUserDTO displayUserDetails(Long userId,Boolean inActive){
        log.info("Inside DisplayUserService, displayUserDetails method!");
        log.debug("Displaying user details for user with id {} and active status {}", userId, inActive);
        User user = getUserByIdAndInActive(userId,inActive);
        DisplayUserDTO displayUser = new DisplayUserDTO(user.getUserId(), user.getFirstName(), user.getLastName(), user.getEmailId(), user.getPhoneNumber(),user.getRole());
        log.info("User details displayed successfully");
        return displayUser;
    }
    /**
     * Retrieves the List of active users based on the userIdList list given
     * @param userIdList is the List of Long which has the userId's
     * @param inActive is the value which is used for active or inactive
     * @return userList which has a List of users.
     */
    public List<User> getUserListByIdsAndInactive(List<Long> userIdList, Boolean inActive){
        log.info("Inside DisplayUserService, getUserListByIdsAndInactive method!");
        log.debug("Retrieving list of users with ids {} and active status {}", userIdList, inActive);
        List<User> userList = new ArrayList<>();
        for(Long userId: userIdList){
            User user1 = getUserByIdAndInActive(userId,inActive);
            userList.add(user1);
        }
        log.debug("{} users retrieved successfully", userList.size());
        return userList;
    }
}
