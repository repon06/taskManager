package org.taskmanager.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InvalidTask {
    private Object id;
    private Object text;
    private Object completed;
}
