package com.kunal.billingSoftware.enums;

import lombok.Getter;

@Getter
public enum UserRoles {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String value;

    UserRoles(String name) {
        this.value = name;
    }
}
