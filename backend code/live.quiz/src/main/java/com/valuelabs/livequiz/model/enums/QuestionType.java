package com.valuelabs.livequiz.model.enums;

import lombok.Getter;

@Getter
public enum QuestionType {
    TEXT("TEXT"),
    SINGLE("SINGLE"),
    MULTIPLE("MULTIPLE");

    private final String value;

    QuestionType(String value) {
        this.value = value;
    }


    public static QuestionType fromString(String value) {
        for (QuestionType questionType : QuestionType.values()) {
            if (questionType.value.equalsIgnoreCase(value)) return questionType;
        }
        throw new IllegalArgumentException("Invalid QuestionType: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
