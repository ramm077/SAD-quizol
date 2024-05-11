package com.valuelabs.livequiz.model.dto.request;

/**
 * DTO for validating OTP
 * @param emailId of the user for whom the otp is to be validated
 * @param enteredOTP the entered otp by the user
 */

public record ValidateOTPDTO(String emailId,String enteredOTP) {
}
