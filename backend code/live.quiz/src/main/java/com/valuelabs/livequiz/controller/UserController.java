package com.valuelabs.livequiz.controller;

import com.valuelabs.livequiz.model.dto.request.UpdateUserDTO;
import com.valuelabs.livequiz.model.dto.request.UserCreationDTO;
import com.valuelabs.livequiz.model.dto.response.DisplayUserDTO;
import com.valuelabs.livequiz.model.enums.Role;
import com.valuelabs.livequiz.service.user.DisplayUserService;
import com.valuelabs.livequiz.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for handling user-related operations.
 */
@RestController
@CrossOrigin
@RequestMapping("/api")
@Slf4j
public class UserController {
    private final UserService userService;
    private final DisplayUserService displayUserService;
    /**
     * Constructor to inject dependencies.
     */
    @Autowired
    public UserController(UserService userService, DisplayUserService displayUserService) {
        this.userService = userService;
        this.displayUserService = displayUserService;
    }
    /**
     * Endpoint to get details of all active users with a specific role (e.g., RESPONDER).
     * @return ResponseEntity with a list of active users or an error status.
     */
    @GetMapping("/user/responders")
    public ResponseEntity<?> getAllActiveUsers(){
        log.info("Fetching all active responders");
        List<DisplayUserDTO> userList = displayUserService.displayAllUsers(Role.RESPONDER,false);
        log.debug("Successfully fetched all the responders list!");
            return new ResponseEntity<>(userList, HttpStatus.OK);
    }
    /**
     * Endpoint to get details of a user by their ID.
     * @param userId ID of the user whose details are to be retrieved.
     * @return ResponseEntity with the user details or an error status.
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUserDetailsById(@RequestParam Long userId){
        log.info("Inside UserController!, getUserDetailsById method!");
        DisplayUserDTO userDetails = displayUserService.displayUserDetails(userId,false);
        log.debug("Successfully fetched user details of user with id: "+userId);
        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }
    /**
     * Endpoint for updating person information based on the provided personId.
     * @param userId The unique identifier of the person to be updated.
     * @param updateUserDTO The Data Transfer Object (DTO) containing the updated information for the person.
     * @return ResponseEntity with an appropriate message indicating the result of the update operation.
     *         Returns HTTP 200 OK status if update is successful, HTTP 404 Not Found status if the person is not found,
     *         or HTTP 400 Bad Request status with an error message if an exception occurs during the update process.
     */
    @PutMapping("/user/update")
    public ResponseEntity<?> updatePersonInfo(@RequestParam Long userId, @RequestBody UpdateUserDTO updateUserDTO){
        log.info("Updating the user details based on it's id"+userId);
        try{
            Boolean isUpdated = userService.updateUserById(userId,updateUserDTO);
            if (isUpdated){
                log.debug("The user updated with given details");
                return ResponseEntity.ok("Person updated with the given details");
            }
            log.debug("User not found to be updated");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person Not found with given details");
        }catch (Exception e) {
            log.error("An error occurred while updating user details");
            return ResponseEntity.badRequest().body("An error occurred while updating person : "+e);
        }
    }
    /**
     * Endpoint for deleting a person based on the provided personId.
     * @param userId The unique identifier of the person to be deleted.
     * @return ResponseEntity with an appropriate message indicating the result of the delete operation.
     *         Returns HTTP 200 OK status if deletion is successful, HTTP 404 Not Found status if the person is not found,
     *         or HTTP 400 Bad Request status with an error message if an exception occurs during the delete process.
     */
    @DeleteMapping("/user/delete")
    public ResponseEntity<?> deletePerson(@RequestParam Long userId) {
        log.info("Deleting the User with id"+userId);
        try {
            log.debug("Try to delete the user by id");
            Boolean isDeleted = userService.deleteActivePersonById(userId);
            if (isDeleted) {
                log.debug("The user deleted successfully");
                return ResponseEntity.ok("Person Deleted with the id" + userId);
            }
            log.debug("The user Not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person Not found with given details");
        } catch (Exception e) {
            log.error("An error occurred while deleting user:"+e);
            return ResponseEntity.badRequest().body("An error occurred while deleting person : " + e);
        }
    }
    /**
     * Endpoint to create a new user.
     * @param userCreationDTO DTO containing user creation details.
     * @return ResponseEntity with the created user or an error status.
     */
    @PostMapping("/addUser")
    public ResponseEntity<?> createPerson(@RequestBody UserCreationDTO userCreationDTO){
        log.info("Registering new or inactive user");
        log.debug("User created successfully");
        return new ResponseEntity<>(userService.addUser(userCreationDTO), HttpStatus.CREATED);
    }
}