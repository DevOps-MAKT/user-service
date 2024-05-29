package uns.ac.rs.integration;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import uns.ac.rs.GeneralResponse;
import uns.ac.rs.controller.AuthController;
import uns.ac.rs.dto.response.JWTResponse;

import static org.hamcrest.Matchers.*;

import java.net.URL;
import java.util.LinkedHashMap;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerTests {

    public static String jwt;
    @TestHTTPEndpoint(AuthController.class)
    @TestHTTPResource("login")
    URL loginEndpoint;

    @TestHTTPEndpoint(AuthController.class)
    @TestHTTPResource("authorize/guest")
    URL authorizeGuestEndpoint;

    @TestHTTPEndpoint(AuthController.class)
    @TestHTTPResource("authorize/admin")
    URL authorizeAdminEndpoint;

    @BeforeEach
    public void login(){
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"admin\", \"password\": \"admin123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");
    }
    @Test
    @Order(1)
    public void whenLoginWithIncorrectCredentials_thenReturnUnauthorised() {
        String requestBody = "{\"username\":\"incorrect\", \"password\":\"incorrect123\"}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post(loginEndpoint)
        .then()
                .statusCode(400)
                .body("data", equalTo(""))
                .body("message", equalTo("Credentials aren't valid or the user has been deleted"));
    }

    @Test
    @Order(2)
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
    @Order(3)
    public void whenAuthorizeWithWrongRole_thenReturnUnauthorized() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .get(authorizeGuestEndpoint)
        .then()
                .statusCode(401)
                .body("data", equalTo(""))
                .body("message", equalTo("Unauthorized access - user does not have the adequate role"));
    }

    @Test
    @Order(4)
    public void whenAuthorizeWithCorrectRole_thenReturnOkAndUserEmail() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .get(authorizeAdminEndpoint)
        .then()
                .statusCode(200)
                .body("data", equalTo("admin@gmail.com"))
                .body("message", equalTo("Authorization successful"));
    }


}
