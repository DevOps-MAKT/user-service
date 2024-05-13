package uns.ac.rs.integration;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.annotation.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uns.ac.rs.controller.AuthController;
import uns.ac.rs.controller.UserController;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class UserControllerTests {

    public static String jwt;

    @TestHTTPEndpoint(AuthController.class)
    @TestHTTPResource("login")
    URL loginEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("create")
    URL createUserEndpoint;

    @BeforeEach
    public void login(){
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"admin\", \"password\": \"admin123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        jwt = response.getBody().jsonPath().getString("data");

    }

    @Test
    @Priority(1)
    public void whenCreateUserWithValidInfo_thenReturnCreated() {
        String requestBody = "{" +
                "\"email\": \"some.email@gmail.com\"," +
                "\"username\": \"some-username\"," +
                "\"password\": \"password123\"," +
                "\"firstName\": \"Someone\"," +
                "\"lastName\": \"Something\"," +
                "\"role\": \"guest\"," +
                "\"location\": {" +
                "\"country\": \"Serbia\"," +
                "\"city\": \"Subotica\"" +
                "}" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
                .body(requestBody)
        .when()
                .post(createUserEndpoint)
        .then()
                .statusCode(201)
                .body("data", notNullValue())
                .body("message", equalTo("User successfully registered"));
    }

    @Test
    @Priority(2)
        public void whenCreateUserWithSameEmailOrUsername_thenReturnBadRequest() {
        String requestBody = "{" +
                "\"email\": \"some.email@gmail.com\"," +
                "\"username\": \"some-username\"," +
                "\"password\": \"password123\"," +
                "\"firstName\": \"Someone\"," +
                "\"lastName\": \"Something\"," +
                "\"role\": \"guest\"," +
                "\"location\": {" +
                "\"country\": \"Serbia\"," +
                "\"city\": \"Subotica\"" +
                "}" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
                .body(requestBody)
                .when()
                .post(createUserEndpoint)
                .then()
                .statusCode(400)
                .body("data", equalTo(""))
                .body("message", equalTo("Username or email already exists"));
    }
}
