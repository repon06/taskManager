package api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import io.qameta.allure.Description;
import java.util.List;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.taskmanager.model.Task;

class DeleteTodoTest extends BaseTest {
    private List<Task> taskList;

    @BeforeAll
    public void setup() {
        createTask();
        createTask();
        taskList = getFullTaskList();
    }

    @Tag("Smoke")
    @DisplayName("[positive] Delete todo")
    @Description("Delete todo")
    @Test
    void normalDeleteTodoTest() {
        var task = taskList.getFirst();

        given()
                .spec(getSpecificationWithAuth())
                .when()
                .delete(task.getId().toString())
                .then().log().all()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        taskList = getFullTaskList();

        MatcherAssert.assertThat("Error: actual list contains new task",
                taskList, not(hasItem(task)));
    }

    @DisplayName("[negative] Delete todo without authorization")
    @Description("Delete todo without authorization")
    @Test
    void failDeleteTodoWithoutAuthTest() {
        var task = taskList.getFirst();

        given()
                .spec(getSpecification())
                .when()
                .delete(task.getId().toString())
                .then().log().all()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);

        taskList = getFullTaskList();

        MatcherAssert.assertThat("Error: actual list do not contains new task",
                taskList, hasItem(task));
    }

    @DisplayName("[negative] Delete not exist todo")
    @Description("Delete not exist todo")
    @Test
    void failDeleteNotExistTodoTest() {
        var taskId = getRandomAndNotExistId();

        var actualResponse = given()
                .spec(getSpecificationWithAuth())
                .when()
                .delete(taskId.toString())
                .then().log().all()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .extract().response().asString();

        //todo: expect to receive an error "Not found"?
        MatcherAssert.assertThat("Error: actual error message should contain 'Not found'",
                actualResponse, Matchers.emptyOrNullString());
    }
}
