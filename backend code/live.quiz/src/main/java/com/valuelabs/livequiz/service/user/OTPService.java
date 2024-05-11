package com.valuelabs.livequiz.service.user;

import com.valuelabs.livequiz.exception.ExceptionUtility;
import com.valuelabs.livequiz.model.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static com.valuelabs.livequiz.model.constants.AppConstants.OTP_EXPIRATION_MILLIS;
import static com.valuelabs.livequiz.utils.InputValidator.generateOTP;
import static com.valuelabs.livequiz.utils.InputValidator.validateEmail;
/**
 * Service class for handling One-Time Password (OTP) functionality, such as generation, validation, and email sending.
 * @Service
 */
@Service
@Slf4j
public class OTPService {
    private final DisplayUserService displayUserService;
    private final JavaMailSender javaMailSender;
    /**
     * Constructs an OTPService with the specified DisplayUserService and JavaMailSender.
     * @param displayUserService Service for displaying user information.
     * @param javaMailSender     Sender for sending emails.
     */
    @Autowired
    public OTPService(DisplayUserService displayUserService, JavaMailSender javaMailSender) {
        this.displayUserService = displayUserService;
        this.javaMailSender = javaMailSender;
    }
    private final Map<String, String> otpMap = new HashMap<>();
    private final Map<String, Timestamp> otpTimeMap = new HashMap<>();
    /**
     * Generates and sends an OTP to the specified email address.
     * @param emailId The email address to send the OTP to.
     * @throws MessagingException if an error occurs during email sending.
     * @throws MailException      if the email cannot be sent.
     */
    public void generateAndSendOTP(String emailId) throws MessagingException, MailException {
        log.info("Inside DisplayUserService, generateAndSendOTP method!");
        if(emailId != null) {
            validateEmail(emailId);
            String otp = generateOTP();
            otpMap.put(emailId, otp);
            otpTimeMap.put(emailId, new Timestamp(System.currentTimeMillis()));
            sendMail(emailId, otp);
        }
        else ExceptionUtility.throwInvalidResourceDetailsException("User","EmailId not given");
    }
    /**
     * Sends an email containing the OTP to the specified email address.
     * @param emailId The email address to send the OTP to.
     * @param otp     The generated OTP.
     * @throws MessagingException if an error occurs during email sending.
     * @throws MailException      if the email cannot be sent.
     */
    public void sendMail(String emailId, String otp) throws MessagingException, MailException {
        log.info("Inside DisplayUserService, sendMail method!");
        log.debug("emailId: {}",emailId);
        User user = displayUserService.getUserByEmailIdAndInActive(emailId,false);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setTo(emailId);
        mimeMessageHelper.setSubject("Your One Time Password for Password Reset");
        String htmlBody = String.format("<div style=\"width: 700px; font-family: 'Lucida Sans', 'Lucida Sans Regular', 'Lucida Grande', 'Lucida Sans Unicode', Geneva, Verdana, sans-serif; margin: 0 auto; background: #e0e0e0;\n" +
                        "    padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\">\n" +
                        "        <!-- <img src='00-Cover-Image.jpg' alt='Logo' style='width: 120px; height: 100px;'> -->\n" +
                        "        <h1 style=\"color: #2EB6AE; text-align: center;\">OTP Verification</h1>\n" +
                        "        <p style=\"margin-bottom: 20px;     --tw-text-opacity: 1;\n" +
                        "        color: rgb(54 57 64 / var(--tw-text-opacity));\">Dear %s,</p>\n" +
                        "        <p style=\"margin-bottom: 20px;     --tw-text-opacity: 1;\n" +
                        "        color: rgb(54 57 64 / var(--tw-text-opacity));\">Your OTP for password reset is: <strong>%s</strong></p>\n" +
                        "        <p style=\"margin-bottom: 20px;    --tw-text-opacity: 1;\n" +
                        "        color: rgb(54 57 64 / var(--tw-text-opacity));\">Your OTP is valid for <strong>2 minutes</strong>.</p>\n" +
                        "        <div style=\"response: flex; justify-content: space-between;\">\n" +
                        "            <img src='cid:logo' alt='Logo' style='width: 130px; height: 50px;'>\n" +
                        "            <img src='cid:moto' alt='Logo' style='width: 250px; height: 50px;'>\n" +
                        "        </div>         \n" +
                        "    </div>",
                user.getFirstName(), otp);
        mimeMessageHelper.setText(htmlBody, true);
        mimeMessageHelper.addInline("logo", new ClassPathResource("static/ValueLabsLogo.jpg"));
        mimeMessageHelper.addInline("moto", new ClassPathResource("static/Moto.png"));
        log.info("Successfully created the mimeMessage, sending the mail through javaMailSender!");
        javaMailSender.send(mimeMessage);
    }
    /**
     * Validates the entered OTP for a given email address.
     * @param emailId        The email address for which to validate the OTP.
     * @param enteredOTP   The entered OTP.
     * @return A string indicating the result of the OTP validation.
     */
    public String validateOTP(String emailId, String enteredOTP) {
        log.info("Inside DisplayUserService, validateOTP method!");
        if (!otpMap.containsKey(emailId) || !otpMap.get(emailId).equals(enteredOTP)) {
            log.debug("This enteredOTP {} is not a valid one, the valid OTP is {}",enteredOTP,otpMap.get(emailId));
            return "Invalid OTP";
        }
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Timestamp otpTime = otpTimeMap.get(emailId);
        long millisElapsed = currentTime.getTime() - otpTime.getTime();
        if (millisElapsed > OTP_EXPIRATION_MILLIS) {
            log.debug("The time validity for this otp {} is {} and it is exceeded now",enteredOTP,OTP_EXPIRATION_MILLIS);
            return "Time limit exceeded. Please request a new OTP.";
        }
        log.debug("This enteredOTP {} is a valid one",enteredOTP);
        return "Valid OTP";
    }
}
