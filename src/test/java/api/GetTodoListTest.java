package api;

import static io.restassured.RestAssured.given;
import static org.taskmanager.enums.Header.LIMIT;
import static org.taskmanager.enums.Header.OFFSET;

import io.qameta.allure.Description;
import io.restassured.module.jsv.JsonSchemaValidator;
import java.util.Arrays;
import java.util.stream.Stream;
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

class GetTodoListTest extends BaseTest {
    private final int defaultLimit = 2;
    private final int defaultOffset = 0;

    @BeforeAll
    public void setup() {
        for (int i = 0; i < 5; i++){
            createTask();
        }
        taskList = getFullTaskList();
    }

    @Tag("Smoke")
    @DisplayName("[positive] Get todo list")
    @Description("Get todo list")
    @Test
    void normalGetTodoTest() {
        var actualResponse = Arrays.stream(given()
                .spec(getSpecification())
                .param(OFFSET.getName(), defaultOffset)
                .param(LIMIT.getName(), defaultLimit)
                .when()
                .get()
                .then().log().all()
                .statusCode(HttpStatus.SC_OK)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(checkSchema("getTodoListSchema.json")))
                .extract().body()
                .as(Task[].class)).toList();
        var expectedTodoList = taskList.subList(defaultOffset, Math.min(defaultOffset + defaultLimit, taskList.size()));

        MatcherAssert.assertThat("Error: actual list do not contains limit task list",
                actualResponse, Matchers.equalTo(expectedTodoList));
    }

    @DisplayName("[negative] Get todo list with invalid offset/limit values")
    @Description("Get todo list with invalid offset/limit values")
    @ParameterizedTest(name = "offset: {0} limit: {1}")
    @MethodSource("invalidValuesProvider")
    void failGetTodoTest(int offset, int limit) {
        var actualResponse = given()
                .spec(getSpecification())
                .param(OFFSET.getName(), offset)
                .param(LIMIT.getName(), limit)
                .when()
                .get()
                .then().log().all()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .extract().response()
                .asString();

        MatcherAssert.assertThat("Error: actual response do not contain error message",
                actualResponse, Matchers.containsStringIgnoringCase("Invalid query string"));
    }

    @DisplayName("[negative] Get todo list with invalid header values")
    @Description("Get todo list with invalid header values")
    @Test
    void failGetTodoTest() {
        var actualResponse = Arrays.stream(given()
                .spec(getSpecification())
                .param(OFFSET.getName().toUpperCase(), defaultOffset)
                .param(LIMIT.getName().toUpperCase(), defaultLimit)
                .when()
                .get()
                .then().log().all()
                .statusCode(HttpStatus.SC_OK)
                .extract().body()
                .as(Task[].class)).toList();

        var expectedTodoList = taskList.subList(defaultOffset, Math.min(defaultOffset + defaultLimit, taskList.size()));

        MatcherAssert.assertThat("Error: actual list do not contains limit task list",
                actualResponse, Matchers.equalTo(expectedTodoList));
    }

    public Stream<Arguments> invalidValuesProvider() {
        return Stream.of(
                Arguments.of(-1, defaultLimit),
                Arguments.of(defaultOffset, -1),
                Arguments.of(defaultOffset, 0),//TODO: negative offset
                Arguments.of(taskList.size(), defaultLimit)//TODO: negative offset
        );
    }
}
