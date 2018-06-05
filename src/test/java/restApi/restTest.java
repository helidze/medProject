package restApi;

import org.testng.annotations.Test;

import java.util.Date;

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
    public void testMed4SureExist(){
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
                header("Content-Type", "application/json").
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
                header("Content-Type", "application/json").
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
                header("Content-Type", "application/json").
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

//    @Test
//    public void newUserRegistration(){
//        Date date = new Date();
//        given().
//                header("Content-Type", "application/json").
//                header("Cache-Control", "no-cache").
//                body("{\n" +
//                        "  \"email\": \"string@string.com\",\n" +
//                        "  \"password\": \"string\",\n" +
//                        "  \"firstName\": \"string\",\n" +
//                        "  \"lastName\": \"string\",\n" +
//                        "  \"isSubscribe\": true\n" +
//                        "}").
//                post(URI + "/Auth/Registration").
//                then().
//                assertThat().
//                statusCode(406).
//                body(containsString("{\"code\":\"Invalid\",\"message\":\"Email already exist\"}")).
//                log().body();
//    }


}

