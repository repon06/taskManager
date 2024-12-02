package org.taskmanager.websocket;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.taskmanager.enums.WebSocketMessageType;
import org.taskmanager.model.Task;

@Tags({@Tag("websocket")})
@Epic("WEBSOCKET tests")
@Feature("Check notification ws todo test")
@DisplayName("Check notification ws  todo test")
class WebSocketNotificationTest extends BaseWebSocketTest {
    private Task task;

    @BeforeAll
    public void setup() {
        task = createTask();
    }

    @Tag("create")
    @DisplayName("[positive] Check notification for create todo test")
    @Description("Check notification for create todo test")
    @Test
    void createTaskNotificationTest() throws InterruptedException {
        Task newTask = createTask();
        System.out.println("Created task: " + newTask);

        assertWebSocketMessage(newTask.getId().toString(), WebSocketMessageType.NEW_TODO, 5);
    }

    @Tag("update")
    @DisplayName("[positive] Check notification for update todo test")
    @Description("Check notification for update todo test")
    @Test
    void updateTaskNotificationTest() throws InterruptedException {
        updateTask(task.getId());
        System.out.println("Updates task: " + task.getId());

        assertWebSocketMessage(task.getId().toString(), WebSocketMessageType.UPDATE_TODO, 15);
    }

    @Tag("delete")
    @DisplayName("[positive] Check notification for delete todo test")
    @Description("Check notification for delete todo test")
    @Test
    void deleteTaskNotificationTest() throws InterruptedException {
        deleteTask(task.getId());
        System.out.println("Delete task: " + task.getId());

        assertWebSocketMessage(task.getId().toString(), WebSocketMessageType.DELETE_TODO, 15);
    }

    @Tag("get")
    @DisplayName("[positive] Check notification for get todo list test")
    @Description("Check notification for get todo list test")
    @Test
    void getTaskListNotificationTest() throws InterruptedException {
        var taskList = getFullTaskList();
        System.out.println("Get task list");

        assertWebSocketMessage("???", WebSocketMessageType.GET_TODO, 15);
    }
}
