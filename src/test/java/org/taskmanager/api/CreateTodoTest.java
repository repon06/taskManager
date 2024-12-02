package org.taskmanager.api;

import static io.restassured.RestAssured.given;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
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

@Tags({@Tag("api"), @Tag("create")})
@Epic("API tests")
@Feature("Create todo test")
@DisplayName("Create todo test")
class CreateTodoTest extends BaseTest {
    private Task newTask;
    private Task firstTask;

    @BeforeAll
    public void setup() {
        firstTask = createTask();
    }

    @AfterEach
    public void clean() {
        //TODO: delete new task: deleteTask(newTask.getId());
    }

    @Tag("Smoke")
    @DisplayName("[positive] Create todo test")
    @Description("Create todo test")
    @Test
    void normalCreateTodoTest() {
        newTask = buildTask(getRandomAndNotExistId(), RandomStringUtils.randomAlphanumeric(10), true);

        given()
                .spec(getSpecification())
                .body(newTask)
                .log().all()
                .when()
                .post()
                .then().log().all()
                .statusCode(HttpStatus.SC_CREATED);

        taskList = getFullTaskList();

        MatcherAssert.assertThat("Error: actual list do not contains new task", taskList, Matchers.hasItem(newTask));
    }

    @DisplayName("[positive] Create todo test")
    @Description("Create todo test")
    @ParameterizedTest(name = "id: {0} text:{1} completed:{2}")
    @MethodSource("normalFieldsProvider")
    void normalParametrizedCreateTodoTest(Long id, String text, Boolean completed) {
        newTask = buildTask(id, text, completed);
        given()
                .spec(getSpecification())
                .body(newTask)
                .when()
                .post()
                .then().log().all()
                .statusCode(HttpStatus.SC_CREATED);

        taskList = getFullTaskList();

        MatcherAssert.assertThat("Error: actual list do not contains new task", taskList, Matchers.hasItem(newTask));
    }

    @DisplayName("[negative] Create todo with invalid value field test")
    @Description("Create todo with invalid value field test")
    @ParameterizedTest(name = "id:{0} text:{1} completed:{2}")
    @MethodSource("invalidFieldsProvider")
    void failCreateTodoTest(Object id, Object text, Object completed, int code, String message) {
        var newTask = buildInvalidTask(id, text, completed);

        var actualResponse = given()
                .spec(getSpecification())
                .body(newTask)
                .when()
                .post()
                .then().log().all()
                .statusCode(code)
                .extract().response().asString();

        MatcherAssert.assertThat("Error: actual message does not match the expected message",
                actualResponse, Matchers.containsStringIgnoringCase(message));
    }

    public Stream<Arguments> normalFieldsProvider() {
        var uniqueId = getRandomAndNotExistId();
        var description = RandomStringUtils.randomAlphanumeric(10);
        return Stream.of(
                Arguments.of(0L, description, true),
                Arguments.of(Long.MAX_VALUE, description, true),
                Arguments.of(uniqueId, description, true),

                Arguments.of(uniqueId, description, false),
                Arguments.of(uniqueId, RandomStringUtils.randomAlphanumeric(16331), true),
                Arguments.of(uniqueId, RandomStringUtils.random(10), true),

                Arguments.of(uniqueId, "; DROP TABLE users; ", true),
                Arguments.of(uniqueId, " OR '1'='1 ", true),
                Arguments.of(uniqueId, "%_\\", true),
                Arguments.of(uniqueId, "<iframe src=\"http://google.com\"></iframe>", true),
                Arguments.of(uniqueId, "<script>alert('XSS');</script>", true),
                Arguments.of(uniqueId, "alert('XSS')", true),
                Arguments.of(uniqueId, "", true)
        );
    }

    public Stream<Arguments> invalidFieldsProvider() {
        var uniqueId = getRandomAndNotExistId();
        var description = RandomStringUtils.randomAlphanumeric(10);
        return Stream.of(
                Arguments.of(firstTask.getId(), description, true,
                        HttpStatus.SC_BAD_REQUEST, ""),
                Arguments.of(uniqueId, firstTask.getText(), null,
                        HttpStatus.SC_BAD_REQUEST, "invalid type: null, expected a boolean at line 1"),

                Arguments.of(null, description, true,
                        HttpStatus.SC_BAD_REQUEST, "invalid type: null, expected u64 at line 1"),
                Arguments.of(Long.MAX_VALUE + 1, description, true,
                        HttpStatus.SC_BAD_REQUEST,
                        String.format("invalid value: integer `%s`, expected u64 at line 1", Long.MAX_VALUE + 1)),
                Arguments.of(-1L, description, true,
                        HttpStatus.SC_BAD_REQUEST, "invalid value: integer `-1`, expected u64 at line 1"),

                Arguments.of(uniqueId, RandomStringUtils.randomAlphanumeric(16332), true,
                        HttpStatus.SC_REQUEST_TOO_LONG, "The request payload is too large"),
                Arguments.of(uniqueId.toString(), description, true,
                        HttpStatus.SC_BAD_REQUEST,
                        String.format("invalid type: string \"%s\", expected u64 at line 1", uniqueId)),
                Arguments.of(uniqueId, null, true,
                        HttpStatus.SC_BAD_REQUEST, "invalid type: null, expected a string at line 1"),

                Arguments.of(uniqueId, description, null,
                        HttpStatus.SC_BAD_REQUEST, "invalid type: null, expected a boolean at line 1"),
                Arguments.of(uniqueId, description, "true",
                        HttpStatus.SC_BAD_REQUEST, "invalid type: string \"true\", expected a boolean at line 1")
        );
    }
}
