package api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import io.qameta.allure.Description;
import java.util.List;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taskmanager.model.Task;

class DeleteTodoTest extends BaseTest {
    private List<Task> taskList;

    @BeforeAll
    public void setup() {
        taskList = getFullTaskList();
    }

    @DisplayName("[positive] Delete todo")
    @Description("Delete todo list")
    @Test
    void normalDeleteTodoTest() {
        var task = taskList.getFirst();

        given()
                .spec(getSpecification())
                //.auth().preemptive().basic("admin", "admin")
                .when()
                .delete("/todos/" + task.getId())
                .then().log().all()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        taskList = getFullTaskList();

        MatcherAssert.assertThat("Error: actual list do not contains new task", taskList, not(hasItem(task)));
    }
}
