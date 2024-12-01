package api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.qameta.allure.Description;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.taskmanager.model.Task;

class UpdateTodoTest extends BaseTest {
    private Task oldTask;

    @BeforeAll
    public void setup() {
        oldTask = createTask();
    }

    @Tag("Smoke")
    @DisplayName("[positive] Update todo")
    @Description("Update todo")
    @Test
    void normalUpdateTodoTest() {
        //var oldTask = taskList.getFirst();
        var newTask = buildTask(getRandomAndNotExistId(), RandomStringUtils.randomAlphanumeric(10), true);

        given()
                .spec(getSpecificationWithAuth())
                .body(newTask)
                .when()
                .put(oldTask.getId().toString())
                .then().log().all()
                .statusCode(HttpStatus.SC_OK);

        var actualTask = getFullTaskList().stream().filter(i -> i.getId().equals(oldTask.getId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Task with ID " + newTask.getId() + " not found."));
        //TODO: oldTask.id or newTask.id ? ID field should not be changed

        MatcherAssert.assertThat("Error: actual task do not equal new task",
                actualTask, equalTo(newTask));
    }

    @DisplayName("[negative] Update not exist todo")
    @Description("Update not exist todo")
    @ParameterizedTest(name = "Task ID: {0}")
    @MethodSource("invalidIdValueProvider")
    void failUpdateNotExistTodoTest(Object taskId, int statusCode, String message) {
        var newTask = buildTask(getRandomAndNotExistId(), RandomStringUtils.randomAlphanumeric(10), true);

        var actualResponse = given()
                .spec(getSpecificationWithAuth())
                .body(newTask)
                .when()
                .put(taskId.toString())
                .then().log().all()
                .statusCode(statusCode)
                .extract().response().asString();

        //todo: expect to receive an error "Not found"?
        MatcherAssert.assertThat("Error: actual message does not match the expected message",
                actualResponse, Matchers.containsStringIgnoringCase(message));
    }

    public Stream<Arguments> invalidIdValueProvider() {
        return Stream.of(
                Arguments.of(getRandomAndNotExistId(), HttpStatus.SC_NOT_FOUND, ""),
                Arguments.of("invalidId", HttpStatus.SC_NOT_FOUND, ""),
                Arguments.of("", HttpStatus.SC_METHOD_NOT_ALLOWED, "HTTP method not allowed")
        );
    }
}
