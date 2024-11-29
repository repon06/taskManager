package api;

import static io.restassured.RestAssured.given;

import io.qameta.allure.Description;
import io.restassured.module.jsv.JsonSchemaValidator;
import java.util.Arrays;
import java.util.List;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taskmanager.model.Task;

class GetTodoListTest extends BaseTest {
    private List<Task> taskList;
    private final int defaultLimit = 2;
    private final int defaultOffset = 0;

    @BeforeAll
    public void setup() {
        taskList = getFullTaskList();
    }

    @DisplayName("[positive] Get todo list")
    @Description("Get todo list")
    @Test
    void normalGetTodoTest() {

        var response = Arrays.stream(given()
                .spec(getSpecification())
                .param("offset", defaultOffset)
                .param("limit", defaultLimit)
                .when()
                .get("/todos")
                .then().log().all()
                .statusCode(HttpStatus.SC_OK)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(checkSchema("getTodoListSchema.json")))
                .extract().body()
                .as(Task[].class)).toList();
        var expectedTodoList = taskList.subList(defaultOffset, Math.min(defaultOffset + defaultLimit, taskList.size()));

        MatcherAssert.assertThat("Error: actual list do not contains limit task list",
                response, Matchers.equalTo(expectedTodoList));
    }
}
