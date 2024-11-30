package org.taskmanager.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Header {
    LIMIT("limit"),
    OFFSET("offset");

    private final String name;
}
