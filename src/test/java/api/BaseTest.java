package api;

import static com.google.common.base.Strings.isNullOrEmpty;
import static io.restassured.RestAssured.given;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.specification.RequestSpecification;
import java.util.Arrays;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.TestInstance;
import org.taskmanager.model.Task;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {
    protected RequestSpecification getSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost:8080")
                //.setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                //.addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:admin".getBytes()))
                //.setAuth(RestAssured.basic("admin", "admin"))
/*                .setAuth(new PreemptiveBasicAuthScheme() {{
                    setUserName("admin");
                    setPassword("admin");
                }})*/
                //.setConfig(new RestAssuredConfig().encoderConfig(new EncoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
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
