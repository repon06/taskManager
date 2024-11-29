package api;

import static com.google.common.base.Strings.isNullOrEmpty;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.specification.RequestSpecification;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.taskmanager.ConfigLoader;
import org.taskmanager.model.Task;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {
    private static GenericContainer<?> todoAppContainer;
    private static int containerPort;
    private static int hostPort;
    private static String baseUrl;

    @BeforeAll
    public static void setUp() throws IOException, InterruptedException, ConfigurationException {
        Configuration config = ConfigLoader.loadConfig("config.json");

        String imageName;
        String imagePath = config.getString("imagePath");
        containerPort = config.getInt("ports.container");
        hostPort = config.getInt("ports.host");
        baseUrl = config.getString("urls.base");

        ProcessBuilder processBuilder = new ProcessBuilder("docker", "load", "--input", imagePath);
        Process process = processBuilder.start();
        boolean exitCode = process.waitFor(10, TimeUnit.SECONDS);
        if (!exitCode) {
            throw new RuntimeException("The todo-app.tar image could not be uploaded to docker.");
        }
        System.out.println("The todo-app.tar image successfully uploaded to docker.");

        todoAppContainer = new GenericContainer<>(DockerImageName.parse("todo-app"))
                .withExposedPorts(containerPort)
                .withCreateContainerCmdModifier(cmd ->
                        cmd.getHostConfig().withPortBindings(
                                new PortBinding(Ports.Binding.bindPort(hostPort), new ExposedPort(containerPort))
                        )
                );
        todoAppContainer.start();
    }

    @Test
    void testTodoApp() {
        var port = todoAppContainer.getFirstMappedPort();
        assertEquals(hostPort, port, "Port do not initialized.");
    }

    protected RequestSpecification getSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build().auth().preemptive().basic("admin", "admin");
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
                .get("/todos")
                .then().log().all()
                .statusCode(HttpStatus.SC_OK)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(checkSchema("getTodoListSchema.json")))
                .extract()
                .body().as(Task[].class)).toList(); //.collect(Collectors.toSet());
    }
}