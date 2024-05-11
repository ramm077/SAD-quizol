package com.valuelabs.livequiz.utils;
import com.valuelabs.livequiz.exception.ExceptionUtility;
import com.valuelabs.livequiz.exception.InvalidResourceDetailsException;
import com.valuelabs.livequiz.model.entity.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
/**
 * The InputValidator class provides methods for validating various inputs, such as Data Transfer Objects (DTOs),
 * email addresses, passwords, and generating OTPs. It includes functionality to check mandatory fields in DTOs,
 * validate email addresses and passwords based on specified patterns, and generate OTPs of a specified length.
 */
@Slf4j
public class InputValidator {
    /**
     * Validates mandatory fields in a given DTO object and throws an exception if any required field is null.
     * @param dtoObject The Data Transfer Object (DTO) to be validated.
     * @throws InvalidResourceDetailsException If any required field in the DTO is null.
     */
    public static void validateDTO(Object dtoObject) {
        try {
            Class<?> dtoClass = dtoObject.getClass();
            Field[] fields = dtoClass.getDeclaredFields();
            for (Field field : fields){
                field.setAccessible(true);
                switch (field.getName()) {
                    case "phoneNumber", "lastName", "isTrue", "chosenOptions", "answerText", "role", "quizImageBase64URL" -> {
                        continue;
                    }
                }
                if(field.get(dtoObject) == null)
                    ExceptionUtility.throwInvalidResourceDetailsException(getEntity(field.getName()), field.getName() + " is required!");
            }
        }
        catch (IllegalAccessException exception){
            return;
        }
    }
    /**
     * Returns the entity (class) associated with a given field name by scanning a predefined list of classes.
     * @param fieldName The name of the field for which the entity is sought.
     * @return The name of the entity (class) or "Unknown" if the field is not found in any class.
     */
    public static Object getEntity(Object fieldName) {
        List<Class<?>> classesToScan = List.of(User.class, Quiz.class, Question.class,
                Option.class,Response.class,Scheduler.class); // Add more classes here
        for (Class<?> clazz : classesToScan) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields)
                if (field.getName().equals(fieldName))
                    return clazz.getSimpleName(); // or clazz.getName() for the fully qualified name
        }
        return "Unknown"; // Field not found in any class
    }
    /**
     * Validates an email address based on a specified regex pattern.
     * @param emailId The email address to be validated.
     * @throws InvalidResourceDetailsException If the provided email address does not match the expected pattern.
     */
    public static void validateEmail(String emailId) {
        String regexPattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        if(Pattern.compile(regexPattern).matcher(emailId).matches()) return;
        ExceptionUtility.throwInvalidResourceDetailsException(getEntity("emailId"),"given emailId: " + emailId + " is not a valid emailId");
    }
    /**
     * Validates a password based on a specified regex pattern.
     * @param password The password to be validated.
     * @throws InvalidResourceDetailsException If the provided password does not match the expected pattern.
     */
    public static void validatePassword(String password) {
        String regexPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
        if(Pattern.compile(regexPattern).matcher(password).matches()) return;
        ExceptionUtility.throwInvalidResourceDetailsException(getEntity("password"),"given password: " + password + " is not a valid password");
    }
    /**
     * Generates a random One-Time Password (OTP) of a specified length.
     * @return A randomly generated OTP as a string.
     */
    public static String generateOTP() {
        int length = 6;
        String numbers = "0123456789";
        String stringOTP;
        Random random = new Random();
        stringOTP = IntStream.range(0, length)
                .map(i -> random.nextInt(numbers.length()))
                .mapToObj(index -> String.valueOf(numbers.charAt(index)))
                .collect(Collectors.joining());
        return stringOTP;
    }
}
