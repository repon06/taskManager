package org.taskmanager.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.taskmanager.BaseTest;
import org.taskmanager.model.Task;

@Tags({@Tag("api"), @Tag("update")})
@Epic("API tests")
@Feature("UPDATE todo test")
@DisplayName("UPDATE todo test")
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
        var newTask = buildTask(oldTask.getId(), RandomStringUtils.randomAlphanumeric(10), true);

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

        MatcherAssert.assertThat("Error: actual task do not equal new task",
                actualTask, equalTo(newTask));
    }

    @DisplayName("[negative] Update with id todo")
    @Description("Update with id todo")
    @Test
    void normalUpdateTodoWithIdTest() {
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

        //TODO: expect to receive an error "Not found"?
        MatcherAssert.assertThat("Error: actual message does not match the expected message",
                actualResponse, Matchers.containsStringIgnoringCase(message));
    }

    //TODO: all requests should be auth??? + invalid auth tests
    @DisplayName("[negative] Update without auth todo")
    @Description("Update without auth todo")
    @Test
    void failUpdateWithoutAuthTodoTest() {
        var newTask = buildTask(oldTask.getId(), RandomStringUtils.randomAlphanumeric(10), true);

        given()
                .spec(getSpecification())
                .body(newTask)
                .when()
                .put(oldTask.getId().toString())
                .then().log().all()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .extract().response().asString();

        //TODO: check not changed?
        var actualTask = getFullTaskList().stream().filter(i -> i.getId().equals(oldTask.getId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Task with ID " + newTask.getId() + " not found."));

        MatcherAssert.assertThat("Error: actual task do not equal new task",
                actualTask, equalTo(newTask));
    }

    @DisplayName("[positive] Update todo test")
    @Description("Update todo test")
    @ParameterizedTest(name = "text:{0} completed:{1}")
    @MethodSource("normalFieldsProvider")
    void normalParametrizedUpdateTodoTest(String text, Boolean completed) {
        var newTask = buildTask(oldTask.getId(), text, completed);

        given()
                .spec(getSpecificationWithAuth())
                .body(newTask)
                .when()
                .put(oldTask.getId().toString())
                .then().log().all()
                .statusCode(HttpStatus.SC_OK);

        taskList = getFullTaskList();

        MatcherAssert.assertThat("Error: actual list do not contains new task", taskList, Matchers.hasItem(newTask));
    }

    @DisplayName("[negative] Update todo with invalid value field test")
    @Description("Update todo with invalid value field test")
    @ParameterizedTest(name = "text:{0} completed:{1}")
    @MethodSource("invalidFieldsProvider")
    void failUpdateTodoTest(Object text, Object completed, int code, String message) {
        var newTask = buildInvalidTask(oldTask.getId(), text, completed);

        var actualResponse = given()
                .spec(getSpecificationWithAuth())
                .body(newTask)
                .when()
                .put(oldTask.getId().toString())
                .then().log().all()
                .statusCode(code)
                .extract().response().asString();

        MatcherAssert.assertThat("Error: actual message does not match the expected message",
                actualResponse, Matchers.containsStringIgnoringCase(message));
    }

    public Stream<Arguments> normalFieldsProvider() {
        var description = RandomStringUtils.randomAlphanumeric(10);
        return Stream.of(
                Arguments.of(description, false),
                Arguments.of(RandomStringUtils.randomAlphanumeric(16331), true),
                Arguments.of(RandomStringUtils.random(10), true),

                Arguments.of("; DROP TABLE users; ", true),
                Arguments.of(" OR '1'='1 ", true),
                Arguments.of("%_\\", true),
                Arguments.of("<iframe src=\"http://google.com\"></iframe>", true),
                Arguments.of("<script>alert('XSS');</script>", true),
                Arguments.of("alert('XSS')", true),
                Arguments.of("", true)
        );
    }

    public Stream<Arguments> invalidFieldsProvider() {
        var description = RandomStringUtils.randomAlphanumeric(10);
        return Stream.of(
                Arguments.of(RandomStringUtils.randomAlphanumeric(16333), true,
                        HttpStatus.SC_REQUEST_TOO_LONG, "The request payload is too large"),

                Arguments.of(null, true,
                        HttpStatus.SC_BAD_REQUEST, "invalid type: null, expected a string at line 1"),

                Arguments.of(description, null,
                        HttpStatus.SC_BAD_REQUEST, "invalid type: null, expected a boolean at line 1"),
                Arguments.of(description, "true",
                        HttpStatus.SC_BAD_REQUEST, "invalid type: string \"true\", expected a boolean at line 1")
        );
    }

    public Stream<Arguments> invalidIdValueProvider() {
        return Stream.of(
                Arguments.of(getRandomAndNotExistId(), HttpStatus.SC_NOT_FOUND, ""),
                Arguments.of("invalidId", HttpStatus.SC_NOT_FOUND, ""),
                Arguments.of("", HttpStatus.SC_METHOD_NOT_ALLOWED, "HTTP method not allowed")
        );
    }
}
