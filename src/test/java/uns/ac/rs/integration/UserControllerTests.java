package uns.ac.rs.integration;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import uns.ac.rs.controller.AuthController;
import uns.ac.rs.controller.UserController;

import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTests {

    public static String jwt;

    @TestHTTPEndpoint(AuthController.class)
    @TestHTTPResource("login")
    URL loginEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("create")
    URL createUserEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("update")
    URL updateUserEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("retrieve-current-user-info")
    URL retrieveCurrentUserInfoEndpoint;

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
    @Order(1)
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
    @Order(2)
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

    @Test
    @Order(3)
    public void whenUpdateUser_thenReturnOk() {
        String requestBody = "{" +
                "\"username\": \"admin\"," +
                "\"password\": \"admin123\"," +
                "\"firstName\": \"new-Someone\"," +
                "\"lastName\": \"new-Something\"," +
                "\"location\": {" +
                "\"country\": \"Serbia\"," +
                "\"city\": \"Novi Sad\"" +
                "}" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
                .body(requestBody)
        .when()
                .put(updateUserEndpoint)
        .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("data.email", equalTo("admin@gmail.com"))
                .body("data.username", equalTo("admin"))
                .body("data.firstName", equalTo("new-Someone"))
                .body("data.lastName", equalTo("new-Something"))
                .body("data.location.city", equalTo("Novi Sad"))
                .body("data.location.country", equalTo("Serbia"))
                .body("message", equalTo("User successfully updated"));
    }

    @Test
    @Order(4)
    public void whenRetrieveCurrentUserInfo_thenReturnOk() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .get(retrieveCurrentUserInfoEndpoint)
        .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("data.email", equalTo("admin@gmail.com"))
                .body("data.username", equalTo("admin"))
                .body("data.firstName", equalTo("new-Someone"))
                .body("data.lastName", equalTo("new-Something"))
                .body("data.location.city", equalTo("Novi Sad"))
                .body("data.location.country", equalTo("Serbia"))
                .body("message", equalTo("Info about the logged-in user successfully retrieved"));
    }

    @Test
    @Order(5)
    public void whenRetrieveCurrentUserInfoWithoutJwt_thenReturnUnauthorized() {
        given()
                .contentType(ContentType.JSON)
        .when()
                .get(retrieveCurrentUserInfoEndpoint)
        .then()
                .statusCode(401);
    }
}
