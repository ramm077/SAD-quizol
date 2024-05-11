package com.valuelabs.livequiz.model.constants;
/**
 * Declaring App specific constants;
 */
public class AppConstants {
    public static final Integer REMINDER_TIME=45;//Reminder Time for Scheduled quizzes & polls
    public static final String QUIZ_URL="http://10.10.73.97:8083"; //The URL for accessing quiz
    public static final Long TOKEN_EXPIRATION_TIME= (long) (1000 * 60 * 30); //Expiration Time for the jwt-token
    public static final long OTP_EXPIRATION_MILLIS = 60 * 1000; //Expiration Time for OTP
}

