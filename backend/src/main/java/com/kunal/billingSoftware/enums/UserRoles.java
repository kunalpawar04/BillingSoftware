package com.kunal.billingSoftware.enums;

import lombok.Getter;

@Getter
public enum UserRoles {
        ADMIN("ADMIN"),
        USER("USER");

    private final String value;

    UserRoles(String name) {
        this.value = name;
    }
}
