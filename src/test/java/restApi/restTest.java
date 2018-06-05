package restApi;

import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

public class restTest {

    String token = given().
            header("Content-Type", "application/json").
            header("Cache-Control", "no-cache").
            body("{\n  \"email\": \"user@ad.min\",\n  \"password\": \"1q2w3e4r\"\n}").
            post("https://dev.api.meds4sure.com/api/v1/Auth/Login").
            then().
            extract().path("token").toString();


    @Test
    public void testStatusCode(){
        given().
                get("https://med:medSite123@staging.meds4sure.com/").
                then().
                assertThat().
                statusCode(200).
                log().status();
    }

    @Test
    public void testOrderWallpicsExist(){
        given().
                get("https://med:medSite123@staging.meds4sure.com/").
                then().
                assertThat().
                body(containsString("Meds4Sure")).log().all();
    }


    @Test
    public void med4Auth(){
        given().
                contentType("application/json").
                header("Authorization", "Bearer "+token).
                header("Cache-Control", "no-cache").
                post("https://dev.api.meds4sure.com/api/v1/User/Get").
                then().
                assertThat().
                statusCode(200).
                log().all();


    }
}

