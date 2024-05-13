package uns.ac.rs.integration;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import uns.ac.rs.controller.AuthController;
import static org.hamcrest.Matchers.*;

import java.net.URL;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class AuthControllerTests {

    @TestHTTPEndpoint(AuthController.class)
    @TestHTTPResource("login")
    URL loginEndpoint;

    @Test
    public void whenLoginWithCorrectCredentials_thenReturnOkWithJwtToken() {
        String requestBody = "{\"username\":\"admin\", \"password\":\"admin123\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post(loginEndpoint)
        .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("message", equalTo("Successfully logged in"));
    }

    @Test
    public void whenLoginWithIncorrectCredentials_thenReturnUnauthorised() {
        String requestBody = "{\"username\":\"incorrect\", \"password\":\"incorrect123\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post(loginEndpoint)
        .then()
                .statusCode(401)
                .body("data", equalTo(""))
                .body("message", equalTo("Credentials aren't valid"));
    }
}
