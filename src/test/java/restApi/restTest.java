package restApi;

import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

public class restTest {

    protected String URI = "https://dev.api.meds4sure.com/api/v1";
    protected String URL = "https://med:medSite123@staging.meds4sure.com/";
    String username = "";
    String password = "";


    protected String token = given().
            header("Content-Type", "application/json").
            header("Cache-Control", "no-cache").
            body("{\n  \"email\": \"user@ad.min\",\n  \"password\": \"1q2w3e4r\"\n}").
            post(URI + "/Auth/Login").
            then().
            extract().path("token").toString();


    @Test
    public void testStatusCode(){
        given().
                get(URL).
                then().
                assertThat().
                statusCode(200).
                log().status();
    }

    @Test
    public void testMed4SureTextExist(){
        given().
                get(URL).
                then().
                assertThat().
                body(containsString("Meds4Sure")).log().all();
    }

    @Test
    public void testUserProfileInfo(){
        given().
                contentType("application/json").
                header("Authorization", "Bearer "+token).
                header("Cache-Control", "no-cache").
                post(URI + "/User/Get").
                then().
                assertThat().
                statusCode(200).
                body(containsString("user")).
                body(containsString("billingProfile")).
                body(containsString("shipProfile")).
                log().all();
    }

    @Test
    public void loginWithIncorrectEmail(){
        given().
                contentType("application/json").
                header("Cache-Control", "no-cache").
                body("{\n  \"email\": \"user@ad.min1\",\n  \"password\": \"1q2w3e4r\"\n}").
                post(URI + "/Auth/Login").
                then().
                assertThat().
                statusCode(406).
                body(containsString("{\"code\":\"NotFound\",\"message\":\"Get User Error. User not exist with email user@ad.min1\"}")).
                log().body();
    }

    @Test
    public void loginWithIncorrectPassword(){
        given().
                contentType("application/json").
                header("Cache-Control", "no-cache").
                body("{\n  \"email\": \"user@ad.min\",\n  \"password\": \"1q2w3e4r1\"\n}").
                post(URI + "/Auth/Login").
                then().
                assertThat().
                statusCode(406).
                body(containsString("{\"code\":\"Invalid\",\"message\":\"Password is invalid\"}")).
                log().body();
    }

    @Test
    public void newUserRegistrationWithExistEmail(){
        given().
                contentType("application/json").
                header("Cache-Control", "no-cache").
                body("{\n" +
                        "  \"email\": \"string@string.com\",\n" +
                        "  \"password\": \"string\",\n" +
                        "  \"firstName\": \"string\",\n" +
                        "  \"lastName\": \"string\",\n" +
                        "  \"isSubscribe\": true\n" +
                        "}").
                post(URI + "/Auth/Registration").
                then().
                assertThat().
                statusCode(406).
                body(containsString("{\"code\":\"Invalid\",\"message\":\"Email already exist\"}")).
                log().body();
    }

    @Test
    public void newUserRegistration(){
        Date date = new Date();
        given().
                contentType("application/json").
                header("Cache-Control", "no-cache").
                body("{\n" +
                        "  \"email\": " + "\""+date.getMinutes() + date.getSeconds() +date.getHours() + "@string.com\",\n" +
                        "  \"password\": \"string\",\n" +
                        "  \"firstName\": \"string\",\n" +
                        "  \"lastName\": \"string\",\n" +
                        "  \"isSubscribe\": true\n" +
                        "}").
                post(URI + "/Auth/Registration").
                then().
                assertThat().
                statusCode(200).
                body(containsString("token")).
                log().body();
    }

    @Test
    public void resetPasswordTest(){
        given().
                contentType("application/json").
                header("Cache-Control", "no-cache").
                body("{\n" +
                        "  \"email\": \"string@string.com\"\n" +
                        "}").
                post(URI + "/Auth/Recover").
                then().
                assertThat().
                statusCode(200).
                log().body();
    }

    @Test
    public void getBlogTest(){
        given().
                contentType("application/json").
                header("Cache-Control", "no-cache").
                body("{}").
                post(URI + "/Blog/Get").
                then().
                assertThat().
                statusCode(200).
                log().body();
    }

    @Test(description = "receiveOneBlogPostWithIncorrectId")
    public void getBlogByIncorrectId(){
        given().
                contentType("application/json").
                header("Cache-Control", "no-cache").
                body("{\"id\": -1}").
                post(URI + "/Blog/GetOne").
                then().
                assertThat().
                statusCode(500).
                assertThat().body(containsString("{\"code\":\"Undefined\",\"message\":\"Get Blog Error. Blog not exist with id -1\"}")).
                log().all();
    }

    @Test(description = "receiveOneBlogPost")
    public void getBlogById(){
        given().
                contentType("application/json").
                header("Cache-Control", "no-cache").
                body("{\"id\": 1}").
                post(URI + "/Blog/GetOne").
                then().
                assertThat().
                statusCode(200).
                log().body();
    }

    @Test
    public void getCategories(){
        given().
                contentType("application/json").
                post(URI +"/Category/Get").
                then().
                assertThat().
                statusCode(200).
                log().body();
    }

    @Test
    public void getServerTime(){
        given().
                post(URI + "/General/GetTime").
                then().
                assertThat().
                statusCode(200).
                log().body();
    }

    @Test
    public void getSettings(){
        given().
                post(URI + "/General/GetSetting").
                then().
                assertThat().
                statusCode(200).
                log().body();
    }

    @Test
    public void createOrder(){
        String inputOrderDetails = "{\n" +
                "  \"email\": \"user@adm.min\",\n" +
                "  \"shipProfile\": {\n" +
                "    \"firstName\": \"string\",\n" +
                "    \"lastName\": \"string\",\n" +
                "    \"address\": \"string\",\n" +
                "    \"city\": \"string\",\n" +
                "    \"state\": \"string\",\n" +
                "    \"country\": \"string\",\n" +
                "    \"zip\": \"string\",\n" +
                "    \"phone\": \"string\"\n" +
                "  },\n" +
                "  \"billingProfile\": {\n" +
                "    \"address\": \"string\",\n" +
                "    \"city\": \"string\",\n" +
                "    \"state\": \"string\",\n" +
                "    \"country\": \"string\",\n" +
                "    \"zip\": \"string\"\n" +
                "  },\n" +
                "  \"price\": 0.21,\n" +
                "  \"products\": [\n" +
                "    {\n" +
                "      \"productId\": 1,\n" +
                "      \"productDetailsId\": 1,\n" +
                "      \"packCount\": 1\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        given().
                contentType(ContentType.JSON).
                header("Authorization", "Bearer "+token)
                .body(inputOrderDetails)
                .when()
                .post(URI + "/Order/CreateOrder")
                .then().
                assertThat().
                statusCode(200).
                log().body();

    }
    
}

