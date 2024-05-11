package com.valuelabs.livequiz.model.enums;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("Admin"),
    RESPONDER("Responder");
    private final String value;
    Role(String value){
        this.value=value;
    }

    public static Role fromString(String value) {
        for (Role role : Role.values()) {
            if (role.value.equalsIgnoreCase(value)) return role;
        }
        throw new IllegalArgumentException("Invalid Role: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}

