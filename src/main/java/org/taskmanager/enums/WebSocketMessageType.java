package org.taskmanager.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WebSocketMessageType {
    //TODO: is no description of the ws notification
    DELETE_TODO("delete_todo"),
    GET_TODO("get_todo"),
    NEW_TODO("new_todo"),
    UPDATE_TODO("update_todo");

    private final String name;
}
