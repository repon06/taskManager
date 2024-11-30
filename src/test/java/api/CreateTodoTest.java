package api;

import static io.restassured.RestAssured.given;

import io.qameta.allure.Description;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.taskmanager.model.Task;

class CreateTodoTest extends BaseTest {
    private Task newTask;

    @BeforeAll
    public void setup() {
        createTask();
        taskList = getFullTaskList();
    }

    @AfterEach
    public void clean() {
        //todo: очистить
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
                .when()
                .post()
                .then().log().all()
                .statusCode(HttpStatus.SC_CREATED);

        taskList = getFullTaskList();

        MatcherAssert.assertThat("Error: actual list do not contains new task", taskList, Matchers.hasItem(newTask));
    }

    @DisplayName("[positive] Create todo test")
    @Description("Create todo test")
    @ParameterizedTest(name = "{0} {1} {2}")
    @MethodSource("normalFieldsProvider")
    void normalCreateTodoTest(Long id, String text, Boolean completed) {
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
    @ParameterizedTest(name = "{0} {1} {2}")
    @MethodSource("invalidFieldsProvider")
    void failCreateTodoTest(Object id, Object text, Object completed, int code) {
        var newTask = buildInvalidTask(id, text, completed);

        given()
                .spec(getSpecification())
                .body(newTask)
                .when()
                .post()
                .then().log().all()
                .statusCode(code);

        Assertions.assertTrue(true);
    }

    public Stream<Arguments> normalFieldsProvider() {
        return Stream.of(
                Arguments.of(0L, RandomStringUtils.randomAlphanumeric(10), true),
                Arguments.of(Long.MAX_VALUE, RandomStringUtils.randomAlphanumeric(10), true),
                Arguments.of(getRandomAndNotExistId(), RandomStringUtils.randomAlphanumeric(10), true),

                Arguments.of(getRandomAndNotExistId(), RandomStringUtils.randomAlphanumeric(10), false),
                Arguments.of(getRandomAndNotExistId(), RandomStringUtils.randomAlphanumeric(16331), true),
                Arguments.of(getRandomAndNotExistId(), RandomStringUtils.random(10), true),

                Arguments.of(getRandomAndNotExistId(), "; DROP TABLE users; ", true),
                Arguments.of(getRandomAndNotExistId(), " OR '1'='1 ", true),
                Arguments.of(getRandomAndNotExistId(), "%_\\", true),
                Arguments.of(getRandomAndNotExistId(), "<iframe src=\"http://google.com\"></iframe>", true),
                Arguments.of(getRandomAndNotExistId(), "<script>alert('XSS');</script>", true),
                Arguments.of(getRandomAndNotExistId(), "alert('XSS')", true),
                Arguments.of(getRandomAndNotExistId(), "", true)
        );
    }

    public Stream<Arguments> invalidFieldsProvider() {
        return Stream.of(
                Arguments.of(taskList.getFirst().getId(), RandomStringUtils.randomAlphanumeric(10), true, HttpStatus.SC_BAD_REQUEST),

                Arguments.of(null, RandomStringUtils.randomAlphanumeric(10), true, HttpStatus.SC_BAD_REQUEST),
                Arguments.of(Long.MAX_VALUE + 1, RandomStringUtils.randomAlphanumeric(10), true, HttpStatus.SC_BAD_REQUEST),
                Arguments.of(-1L, RandomStringUtils.randomAlphanumeric(10), true, HttpStatus.SC_BAD_REQUEST),

                Arguments.of(getRandomAndNotExistId(), RandomStringUtils.randomAlphanumeric(16332), true, HttpStatus.SC_REQUEST_TOO_LONG),
                Arguments.of(getRandomAndNotExistId().toString(), RandomStringUtils.randomAlphanumeric(10), true, HttpStatus.SC_BAD_REQUEST),
                Arguments.of(getRandomAndNotExistId(), null, true),
                //
                Arguments.of(getRandomAndNotExistId(), RandomStringUtils.randomAlphanumeric(10), null, HttpStatus.SC_BAD_REQUEST),
                Arguments.of(getRandomAndNotExistId(), RandomStringUtils.randomAlphanumeric(10), "true", HttpStatus.SC_BAD_REQUEST),

                Arguments.of(taskList.getFirst().getId(), RandomStringUtils.randomAlphanumeric(10), null, HttpStatus.SC_BAD_REQUEST),
                Arguments.of(getRandomAndNotExistId(), taskList.getFirst().getText(), null, HttpStatus.SC_BAD_REQUEST)
        );
    }
}
