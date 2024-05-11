package com.valuelabs.livequiz.controller;
import com.valuelabs.livequiz.model.dto.request.ForgotPasswordDTO;
import com.valuelabs.livequiz.model.dto.request.LoginDTO;
import com.valuelabs.livequiz.model.dto.request.UserCreationDTO;
import com.valuelabs.livequiz.model.dto.response.ErrorStatusDTO;
import com.valuelabs.livequiz.model.dto.request.ValidateOTPDTO;
import com.valuelabs.livequiz.service.user.AuthenticationService;
import com.valuelabs.livequiz.service.user.OTPService;
import com.valuelabs.livequiz.service.user.UserService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * Controller responsible for user authentication and password-related operations.
 */
@RestController
@CrossOrigin
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    private final UserService userService;
    private final OTPService otpService;
    private final AuthenticationService authenticationService;
    /**
     * Constructor to inject dependencies.
     */
    @Autowired
    public AuthController(UserService userService, OTPService otpService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.otpService = otpService;
        this.authenticationService = authenticationService;
    }
    /**
     * Endpoint to handle user login.
     * @param loginDTO DTO containing user login details.
     * @return ResponseEntity with authentication result and JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO){
        log.info("Inside AuthController, login method called!");
        return new ResponseEntity<>(authenticationService.login(loginDTO), HttpStatus.CREATED);
    }
    /**
     * Endpoint to create a new user.
     * @param userCreationDTO DTO containing user creation details.
     * @return ResponseEntity with the created user or an error status.
     */
    @PostMapping("/signUp")
    public ResponseEntity<?> createPerson(@RequestBody UserCreationDTO userCreationDTO){
        log.info("Registering new or inactive user");
        Boolean isExistingAndUpdated=userService.ifExistReactivateUser(userCreationDTO);
        if(isExistingAndUpdated){
            log.info("The inactive user is reactivated with new details");
            return ResponseEntity.status(HttpStatus.OK).body("User already exist and is updated");
        }
        log.info("User created successfully");
        return new ResponseEntity<>(authenticationService.defaultUserCreation(userCreationDTO), HttpStatus.CREATED);
    }
    /**
     * Endpoint to send OTP for password recovery.
     * @param forgotPasswordDTO DTO containing user email for OTP generation.
     * @return ResponseEntity indicating the result of OTP sending.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> sendOTP(@RequestBody ForgotPasswordDTO forgotPasswordDTO) throws MessagingException {
        log.info("Inside AuthController, sendOTP method ");
        otpService.generateAndSendOTP(forgotPasswordDTO.emailId());
        log.debug("OTP sent successfully to {}" , forgotPasswordDTO.emailId());
        return new ResponseEntity<>(new ErrorStatusDTO("OTP sent successfully to " + forgotPasswordDTO.emailId()), HttpStatus.CREATED);
    }
    /**
     * Endpoint to validate the entered OTP.
     * @param validateOTPDTO DTO containing user email and entered OTP for validation.
     * @return ResponseEntity indicating the result of OTP validation.
     */
    @PostMapping("/validate-otp")
    public ResponseEntity<?> verifyValidOTP(@RequestBody ValidateOTPDTO validateOTPDTO){
        log.info("Inside AuthController, verifyOTP method ");
        String result = otpService.validateOTP(validateOTPDTO.emailId(), validateOTPDTO.enteredOTP());
        if (result.equals("Valid OTP")) {
            log.debug(result);
            return new ResponseEntity<>(new ErrorStatusDTO(result), HttpStatus.OK);
        } else if (result.equals("Time limit exceeded. Please request a new OTP.")) {
            log.debug(result);
            return new ResponseEntity<>(new ErrorStatusDTO(result), HttpStatus.REQUEST_TIMEOUT);
        } else {
            log.debug(result);
            return new ResponseEntity<>(new ErrorStatusDTO(result), HttpStatus.UNAUTHORIZED);
        }
    }
    /**
     * Endpoint to change user password after OTP validation.
     * @param loginDTO DTO containing user email and new password.
     * @return ResponseEntity indicating the result of password change.
     */
    @PostMapping("/confirm-password")
    public ResponseEntity<?> changePassword(@RequestBody LoginDTO loginDTO){
        log.info("Inside AuthController, changePassword method ");
        userService.changePassword(loginDTO);
        log.info("Successfully Changed the password!");
        return new ResponseEntity<>(new ErrorStatusDTO("changed password"),HttpStatus.OK);
    }
}
