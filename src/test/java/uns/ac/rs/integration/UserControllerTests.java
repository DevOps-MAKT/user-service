package uns.ac.rs.integration;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import uns.ac.rs.GeneralResponse;
import uns.ac.rs.MicroserviceCommunicator;
import uns.ac.rs.config.IntegrationConfig;
import uns.ac.rs.controller.AuthController;
import uns.ac.rs.controller.UserController;
import uns.ac.rs.dto.AccommodationReviewDTO;
import uns.ac.rs.dto.HostReviewDTO;
import uns.ac.rs.dto.MinAccommodationDTO;
import uns.ac.rs.dto.PasswordDTO;
import uns.ac.rs.model.AccommodationReview;
import uns.ac.rs.model.HostReview;

import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doReturn;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTests {

    public static String jwt;

    @InjectMock
    private MicroserviceCommunicator microserviceCommunicator;

    @Autowired
    private IntegrationConfig config;

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

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("change-automatic-reservation-acceptance-status")
    URL changeAutomaticReservationAcceptanceStatusEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("get-automatic-reservation-acceptance-status")
    URL getAutomaticReservationAcceptanceStatusEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("append-cancellation")
    URL appendCancellationEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("no-cancellations/gost@gmail.com")
    URL getNOCancellationsEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("terminate-guest")
    URL terminateGuestEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("terminate-host")
    URL terminateHostEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("host-reviews")
    URL getHostReviewsEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("add-host-review")
    URL addHostReviewEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("delete-host-review/pera@gmail.com")
    URL deleteHostReviewEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("host-reviews-info")
    URL hostReviewsInfoEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("accommodation-reviews")
    URL getAccommodationReviewsEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("add-accommodation-review")
    URL addAccommodationReviewEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("delete-accommodation-review/some-accommodation")
    URL deleteAccommodationReviewEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("accommodation-reviews-info/some-accommodation")
    URL accommodationReviewsInfoEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("avg-rating/some-accommodation")
    URL getAvgRatingEndpoint;

    @TestHTTPEndpoint(UserController.class)
    @TestHTTPResource("update-password")
    URL updatePasswordEndpoint;

    @BeforeEach
    public void login(){
        Response response = given()
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
    public void whenCreateUserWithValidInfo_thenReturnCreated() {
        String requestBody = "{" +
                "\"email\": \"some.email@gmail.com\"," +
                "\"username\": \"some-username\"," +
                "\"password\": \"password123\"," +
                "\"firstName\": \"Someone\"," +
                "\"lastName\": \"Something\"," +
                "\"role\": \"guest\"," +
                "\"country\": \"Serbia\"," +
                "\"city\": \"Subotica\"" +
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
                "\"country\": \"Serbia\"," +
                "\"city\": \"Subotica\"" +
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
                "\"firstName\": \"new-Someone\"," +
                "\"lastName\": \"new-Something\"," +
                "\"country\": \"Serbia\"," +
                "\"city\": \"Novi Sad\"" +
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
                .body("data.city", equalTo("Novi Sad"))
                .body("data.country", equalTo("Serbia"))
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
                .body("data.city", equalTo("Novi Sad"))
                .body("data.country", equalTo("Serbia"))
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

    @Test
    @Order(6)
    public void whenChangeAutomaticReservationAcceptanceStatus_thenReturnUserWithUpdatedInfo() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"pera\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .patch(changeAutomaticReservationAcceptanceStatusEndpoint)
        .then()
                .statusCode(200)
                .body("data.automaticReservationAcceptance", equalTo(true));
    }

    @Test
    @Order(7)
    public void whenGetAutomaticReservationAcceptanceStatus_thenReturnBooleanValue() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"pera\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .get(getAutomaticReservationAcceptanceStatusEndpoint)
        .then()
                .statusCode(200)
                .body("data", equalTo(true));
    }

    @Test
    @Order(8)
    public void whenAppendCancellation_thenNOCancellationsIsIncremented() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"gost\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .get(appendCancellationEndpoint)
        .then()
                .statusCode(200)
                .body("data", equalTo(1))
                .body("message", equalTo("Successfully appended number of cancellations"));
    }

    @Test
    @Order(9)
    public void whenRetrieveNOCancellations_thenReturnNOCancellations() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"pera\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .get(getNOCancellationsEndpoint)
        .then()
                .statusCode(200)
                .body("data", equalTo(1))
                .body("message", equalTo("Successfully retrieved number of cancellations"));
    }

    @Test
    @Order(10)
    public void whenTerminateGuestWithReservations_thenCantTerminate() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"gost\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        doReturn(new GeneralResponse(true, "200"))
                .when(microserviceCommunicator)
                .processResponse(config.reservationServiceAPI() + "/reservation/are-reservations-active/gost@gmail.com",
                        "GET",
                        "Bearer " + jwt);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .patch(terminateGuestEndpoint)
        .then()
                .statusCode(400)
                .body("data", equalTo(false))
                .body("message", equalTo("There are active reservations"));
    }

    @Test
    @Order(11)
    public void whenTerminateHostWithActiveReservations_thenCantTerminate() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"pera\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        doReturn(new GeneralResponse(true, "200"))
                .when(microserviceCommunicator)
                .processResponse(config.reservationServiceAPI() + "/reservation/do-active-reservations-exist/pera@gmail.com",
                        "GET",
                        "Bearer " + jwt);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .patch(terminateHostEndpoint)
        .then()
                .statusCode(400)
                .body("data", equalTo(false))
                .body("message", equalTo("There are active reservations"));
    }

    @Test
    @Order(12)
    public void whenGetOneHostWithoutSentReview_thenReturnNewEmptyReview() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"gost\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        List<String> hostEmails = new ArrayList<>();
        hostEmails.add("pera@gmail.com");
        doReturn(new GeneralResponse(hostEmails, "200"))
                .when(microserviceCommunicator)
                .processResponse(config.reservationServiceAPI() + "/retrieve-reservation-hosts/gost@gmail.com",
                        "GET",
                        "");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .get(getHostReviewsEndpoint)
        .then()
                .statusCode(200)
                .body("data.size()", equalTo(1))
                .body("message", equalTo("Successfully retrieved reviews"));
    }

    @Test
    @Order(13)
    public void whenCreateReview_thenReturnCreatedReview() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"gost\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        HostReview hostReview = new HostReview();
        hostReview.setHostEmail("pera@gmail.com");
        hostReview.setGuestEmail("gost@gmail.com");
        hostReview.setRating(3);
        long now = Instant.now().toEpochMilli();
        hostReview.setTimestamp(now);
        HostReviewDTO hostReviewDTO = new HostReviewDTO(hostReview);

        given()
                .contentType(ContentType.JSON)
                .body(hostReviewDTO)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .put(addHostReviewEndpoint)
        .then()
                .statusCode(200)
                .body("data.hostEmail", equalTo("pera@gmail.com"))
                .body("data.timestamp", equalTo(now))
                .body("data.rating", equalTo(3))
                .body("message", equalTo("Successfully added/updated host review"));
    }

    @Test
    @Order(14)
    public void whenUpdateReview_thenReturnUpdatedReview() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"gost\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        HostReview hostReview = new HostReview();
        hostReview.setHostEmail("pera@gmail.com");
        hostReview.setGuestEmail("gost@gmail.com");
        hostReview.setRating(5);
        long now = Instant.now().toEpochMilli();
        hostReview.setTimestamp(now);
        HostReviewDTO hostReviewDTO = new HostReviewDTO(hostReview);

        given()
                .contentType(ContentType.JSON)
                .body(hostReviewDTO)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .put(addHostReviewEndpoint)
        .then()
                .statusCode(200)
                .body("data.hostEmail", equalTo("pera@gmail.com"))
                .body("data.timestamp", equalTo(now))
                .body("data.rating", equalTo(5))
                .body("message", equalTo("Successfully added/updated host review"));
    }

    @Test
    @Order(15)
    public void whenRetrieveHostsReviews_thenReturnReviewInfo() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"pera\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .get(hostReviewsInfoEndpoint)
        .then()
                .statusCode(200)
                .body("data.reviews.size()", equalTo(1))
                .body("data.avgRating", equalTo(5.0F))
                .body("message", equalTo("Successfully retrieved host reviews info"));
    }

    @Test
    @Order(16)
    public void whenDeleteHostReview_thenReturnReviewInfo() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"gost\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .delete(deleteHostReviewEndpoint)
        .then()
                .statusCode(200)
                .body("data.deleted", equalTo(true))
                .body("message", equalTo("Successfully deleted host review"));
    }

    @Test
    @Order(17)
    public void whenGetOneAccommodationWithoutSentReview_thenReturnNewEmptyReview() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"gost\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        List<Long> accommodationIds = new ArrayList<>();
        accommodationIds.add(1L);

        List<MinAccommodationDTO> minAccommodationDTOS = new ArrayList<>();
        minAccommodationDTOS.add(new MinAccommodationDTO(1L, "some-accommodation"));

        doReturn(new GeneralResponse(accommodationIds, "200"))
                .when(microserviceCommunicator)
                .processResponse(config.reservationServiceAPI() + "/retrieve-reservation-accommodations/gost@gmail.com",
                        "GET",
                        "");

        doReturn(new GeneralResponse(minAccommodationDTOS, "200"))
                .when(microserviceCommunicator)
                .processResponse(config.accommodationServiceAPI() + "/retrieve-min-accommodations",
                        "GET",
                        "");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .get(getAccommodationReviewsEndpoint)
        .then()
                .statusCode(200)
                .body("data.size()", equalTo(1))
                .body("message", equalTo("Successfully retrieved reviews"));
    }

    @Test
    @Order(18)
    public void whenCreateAccommodationReview_thenReturnCreatedReview() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"gost\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        AccommodationReview accommodationReview = new AccommodationReview();
        accommodationReview.setAccommodationName("some-accommodation");
        accommodationReview.setGuestEmail("gost@gmail.com");
        accommodationReview.setRating(3);
        long now = Instant.now().toEpochMilli();
        accommodationReview.setTimestamp(now);
        AccommodationReviewDTO accommodationReviewDTO = new AccommodationReviewDTO(accommodationReview);

        given()
                .contentType(ContentType.JSON)
                .body(accommodationReviewDTO)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .put(addAccommodationReviewEndpoint)
        .then()
                .statusCode(200)
                .body("data.accommodationName", equalTo("some-accommodation"))
                .body("data.timestamp", equalTo(now))
                .body("data.rating", equalTo(3))
                .body("message", equalTo("Successfully added/updated accommodation review"));
    }

    @Test
    @Order(19)
    public void whenUpdateAccommodationReview_thenReturnUpdatedReview() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"gost\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        AccommodationReview accommodationReview = new AccommodationReview();
        accommodationReview.setAccommodationName("some-accommodation");
        accommodationReview.setGuestEmail("gost@gmail.com");
        accommodationReview.setRating(5);
        long now = Instant.now().toEpochMilli();
        accommodationReview.setTimestamp(now);
        AccommodationReviewDTO accommodationReviewDTO = new AccommodationReviewDTO(accommodationReview);

        given()
                .contentType(ContentType.JSON)
                .body(accommodationReviewDTO)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .put(addAccommodationReviewEndpoint)
        .then()
                .statusCode(200)
                .body("data.accommodationName", equalTo("some-accommodation"))
                .body("data.timestamp", equalTo(now))
                .body("data.rating", equalTo(5))
                .body("message", equalTo("Successfully added/updated accommodation review"));
    }

    @Test
    @Order(20)
    public void whenRetrieveAccommodationsReviews_thenReturnReviewInfo() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"pera\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .get(accommodationReviewsInfoEndpoint)
        .then()
                .statusCode(200)
                .body("data.reviews.size()", equalTo(1))
                .body("data.avgRating", equalTo(5.0F))
                .body("message", equalTo("Successfully retrieved accommodation reviews info"));
    }

    @Test
    @Order(21)
    public void whenDeleteAccommodationReview_thenReturnReviewInfo() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"gost\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .delete(deleteAccommodationReviewEndpoint)
        .then()
                .statusCode(200)
                .body("data.deleted", equalTo(true))
                .body("message", equalTo("Successfully deleted accommodation review"));
    }

    @Test
    @Order(22)
    public void whenGetAvgRatingAccommodation_thenReturnAvgRating() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"gost\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .get(getAvgRatingEndpoint)
        .then()
                .statusCode(200)
                .body("data", equalTo(0f))
                .body("message", equalTo("Successfully retrieved accommodation rating"));
    }

    @Test
    @Order(23)
    public void whenUpdateUserPassword_thenReturnUserWithUpdatedCredentials() {
        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setPassword("admin123");
        passwordDTO.setConfirmationPassword("admin123");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
                .body(passwordDTO)
        .when()
                .patch(updatePasswordEndpoint)
        .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("message", equalTo("Password successfully changed"));
    }


    @Test
    @Order(24)
    public void whenTerminateGuestWithoutReservations_thenCanTerminate() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"gost\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        doReturn(new GeneralResponse(false, "200"))
                .when(microserviceCommunicator)
                .processResponse(config.reservationServiceAPI() + "/reservation/are-reservations-active/gost@gmail.com",
                        "GET",
                        "Bearer " + jwt);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
                .when()
                .patch(terminateGuestEndpoint)
                .then()
                .statusCode(200)
                .body("data", equalTo(true))
                .body("message", equalTo("Successfully terminated account"));
    }

    @Test
    @Order(25)
    public void whenTerminateHostWithNoActiveReservations_thenCanTerminate() {
        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\": \"pera\", \"password\": \"pera123\"}")
                .when().post(loginEndpoint)
                .then().extract().response();

        GeneralResponse generalResponse = response.as(GeneralResponse.class);
        LinkedHashMap data = (LinkedHashMap) generalResponse.getData();
        jwt = (String) data.get("jwt");

        doReturn(new GeneralResponse(false, "200"))
                .when(microserviceCommunicator)
                .processResponse(config.reservationServiceAPI() + "/reservation/do-active-reservations-exist/pera@gmail.com",
                        "GET",
                        "Bearer " + jwt);

        doReturn(new GeneralResponse(true, "200"))
                .when(microserviceCommunicator)
                .processResponse(config.accommodationServiceAPI() + "/accommodation/deactivate-hosts-accommodations/pera@gmail.com",
                        "DELETE",
                        "Bearer " + jwt);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
        .when()
                .patch(terminateHostEndpoint)
        .then()
                .statusCode(200)
                .body("data", equalTo(true))
                .body("message", equalTo("Successfully terminated account"));
    }
}

