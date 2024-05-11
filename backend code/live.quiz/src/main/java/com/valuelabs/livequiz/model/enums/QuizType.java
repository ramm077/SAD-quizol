package com.valuelabs.livequiz.model.enums;

import lombok.Getter;

@Getter
public enum QuizType {
    TEST("TEST"),
    OPEN_ENDED("OPEN_ENDED"),
    POLL("POLL"),
    OTHER("OTHER");
    private final String value;

    QuizType(String value) {
        this.value = value;
    }


    public static QuizType fromString(String value) {
        for (QuizType quizType : QuizType.values()) {
            if (quizType.value.equalsIgnoreCase(value)) return quizType;
        }
        throw new IllegalArgumentException("Invalid QuizType: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}

