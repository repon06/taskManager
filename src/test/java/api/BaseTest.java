package api;

import static com.google.common.base.Strings.isNullOrEmpty;
import static io.restassured.RestAssured.given;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.specification.RequestSpecification;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.taskmanager.ConfigLoader;
import org.taskmanager.ContainerInitializing;
import org.taskmanager.model.InvalidTask;
import org.taskmanager.model.Task;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BaseTest {
    private static int containerPort;
    private static int hostPort;
    private static String baseUrl;
    private static String username;
    private static String password;

    private final ContainerInitializing container = new ContainerInitializing();
    protected List<Task> taskList;

    @BeforeAll
    public void initialize() throws Exception {
        Configuration config = ConfigLoader.loadConfig("config.json");
        String imagePath = config.getString("imagePath");
        containerPort = config.getInt("ports.container");
        hostPort = config.getInt("ports.host");
        baseUrl = config.getString("urls.base");
        username = config.getString("credentials.username");
        password = config.getString("credentials.password");

        container.initialize(imagePath, containerPort, hostPort);
    }

    @AfterAll
    void tearDown() {
        container.stopContainer();
    }

    @Test
    void testContainer() {
        Assertions.assertTrue(container.isRunning(), "Container is not running");

        var port = container.getFirstMappedPort();
        Assertions.assertEquals(hostPort, port, "Port is not initialized.");
    }

    protected RequestSpecification getSpecification() {
        return new RequestSpecBuilder()
                .addFilter(new AllureRestAssured())
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }

    protected RequestSpecification getSpecificationWithAuth() {
        return getSpecification()
                .auth().preemptive().basic(username, password);
    }

    protected String checkSchema(String fileName) {
        String SCHEMA_PATH = "schemas/";
        String EMPTY_SCHEMA = "emptySchema.json";
        return isNullOrEmpty(fileName) ? SCHEMA_PATH + EMPTY_SCHEMA : SCHEMA_PATH + fileName;
    }

    protected List<Task> getFullTaskList() {
        return Arrays.stream(given()
                        .spec(getSpecification())
                        .when()
                        .get()
                        .then().log().all()
                        .statusCode(HttpStatus.SC_OK)
                        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(checkSchema("getTodoListSchema.json")))
                        .extract()
                        .body().as(Task[].class))
                .toList();
    }

    protected Task createTask() {
        var newTask = buildTask(getRandomAndNotExistId(), RandomStringUtils.randomAlphanumeric(10), true);
        given()
                .spec(getSpecification())
                .body(newTask)
                .when()
                .post()
                .then().log().all()
                .statusCode(HttpStatus.SC_CREATED);
        return newTask;
    }

    protected Long getRandomAndNotExistId() {
        long id;
        Random random = new Random();
        if (taskList == null) {
            return random.nextLong(0, Long.MAX_VALUE);
        }
        Set<Long> idList = taskList.stream().map(Task::getId).collect(Collectors.toSet());
        do {
            id = random.nextLong(0, Long.MAX_VALUE);
        } while (idList.contains(id));
        return id;
    }

    protected Task buildTask(long id, String text, boolean completed) {
        return Task.builder()
                .id(id)
                .text(text)
                .completed(completed)
                .build();
    }

    protected InvalidTask buildInvalidTask(Object id, Object text, Object completed) {
        return InvalidTask.builder()
                .id(id)
                .text(text)
                .completed(completed)
                .build();
    }
}