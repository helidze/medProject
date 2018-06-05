package restApi;

import org.testng.annotations.Test;

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
    public void med4Auth(){
        given().
                contentType("application/json").
                header("Authorization", "Bearer "+token).
                header("Cache-Control", "no-cache").
                post(URI + "/User/Get").
                then().
                assertThat().
                statusCode(200).
                log().all();
    }
}

