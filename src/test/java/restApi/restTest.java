package restApi;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.util.Date;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItems;

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
                        "  \"firstName\": \"autoTestByRest\",\n" +
                        "  \"lastName\": \"autoTestByRest\",\n" +
                        "  \"isSubscribe\": true\n" +
                        "}").
                post(URI + "/Auth/Registration").
                then().
                assertThat().
                statusCode(200).
                body(containsString("token")).
                body(matchesJsonSchemaInClasspath("registrationUserSchema.json")).
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
                body("{\n" +
                        "  \"ids\": [\n" +
                        "    {\n" +
                        "      \"id\": 0,\n" +
                        "      \"updatedAt\": 0\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}").
                post(URI + "/Blog/Get").
                then().
                assertThat().
                body(matchesJsonSchemaInClasspath("allExistedBlogs.json")).
                statusCode(200).
                log().body();
    }

    @Test
    public void getBlogTestNegative(){
        given().
                contentType("application/json").
                header("Cache-Control", "no-cache").
                body("{}").
                post(URI + "/Blog/Get").
                then().
                assertThat().
                body(containsString("\"code\":\"Invalid\",\"message\":\"Ids field is empty\"")).
                statusCode(406).
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
                statusCode(406).
                assertThat().body(containsString("{\"code\":\"Invalid\",\"message\":\"The field Id must be between 1 and 2147483647.\"}")).
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
                body(matchesJsonSchemaInClasspath("getBlogById.json")).
                log().body();
    }

    @Test
    public void getCategories(){
        given().
                contentType("application/json").
                body("{}").
                post(URI +"/Category/Get").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("allExistedCategories.json")).
                body("collection.title", hasItems("Bronchodilators","Cancer","Women's Health")).
                log().body();
    }

    @Test
    public void getServerTime(){
        given().
                post(URI + "/General/GetTime").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("serverTime.json")).
                log().body();
    }

    @Test
    public void getSettings(){
        given().
                post(URI + "/General/GetSetting").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("generalSettings.json")).
                log().body();
    }

    @Test
    public void createOrder(){
        String inputOrderDetails = "{\n" +
                "  \"card\": {\n" +
                "    \"firstName\": \"AutoTest\",\n" +
                "    \"lastName\": \"AutoTest\",\n" +
                "    \"number\": \"4242424242424242\",\n" +
                "    \"month\": 12,\n" +
                "    \"year\": 2033,\n" +
                "    \"cvv\": \"345\"\n" +
                "  },\n" +
                "  \"info\": {\n" +
                "    \"countryCode\": \"string\",\n" +
                "    \"stateCode\": \"string\"\n" +
                "  },\n" +
                "  \"email\": \"user@adm.min\",\n" +
                "  \"shipProfile\": {\n" +
                "    \"firstName\": \"string\",\n" +
                "    \"lastName\": \"string\",\n" +
                "    \"address\": \"string\",\n" +
                "    \"city\": \"string\",\n" +
                "    \"state\": \"string\",\n" +
                "    \"country\": \"string\",\n" +
                "    \"countryId\": \"string\",\n" +
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
                "  \"price\": 20.21,\n" +
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
                header("Authorization", "Bearer "+token).
                body(inputOrderDetails)
                .when()
                .post(URI + "/Order/CreateOrder")
                .then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("newOrder.json")).
                log().body();

    }

    @Test
    public void getAllProducts(){
        given().
                contentType(ContentType.JSON).
                body("{\n" +
                        "  \"ids\": [\n" +
                        "    {\n" +
                        "      \"id\": 1,\n" +
                        "      \"updatedAt\": 0\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}").
                post(URI + "/Product/Get").
                then().
                assertThat().
                contentType(ContentType.JSON).
                statusCode(200).
                body(matchesJsonSchemaInClasspath("allProducts.json")).
                log().body();
    }

    @Test
    public void getProduct(){
        given().
                contentType(ContentType.JSON).
                body("{\n" +
                        "  \"id\": -1,\n" +
                        "  \"name\": \"amoxil-amoxicillin-amoxicillin\"\n" +
                        "}").
                when().
                post(URI + "/Product/GetOne").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("getProductById.json")).
                log().body();
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
                body(matchesJsonSchemaInClasspath("userProfileInfo.json")).
                log().body();
    }

    @Test
    public void updateProfileInfo(){
        given().
                contentType("application/json").
                header("Authorization", "Bearer "+token).
                header("Cache-Control", "no-cache").
                body("{\n" +
                        "  \"email\": \"user@ad.min\",\n" +
                        "  \"firstName\": \"string\",\n" +
                        "  \"lastName\": \"string\"\n" +
                        "}").
                post(URI + "/User/UpdateProfile").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("updateProfile.json")).
                log().body();
    }

    @Test(description = "without AuthToken + status code == 406")
    public void updateProfileInfoNegativeTest(){
        given().
                contentType("application/json").
                header("Cache-Control", "no-cache").
                body("{\n" +
                        "  \"email\": \"user@ad.min\",\n" +
                        "  \"firstName\": \"string\",\n" +
                        "  \"lastName\": \"string\"\n" +
                        "}").
                post(URI + "/User/UpdateProfile").
                then().
                assertThat().
                statusCode(406).
                log().body();
    }

    @Test
    public void updateBillingProfileInfo(){
        given().
                contentType("application/json").
                header("Authorization", "Bearer "+token).
                header("Cache-Control", "no-cache").
                body("{\n" +
                        "  \"address\": \"string\",\n" +
                        "  \"city\": \"string\",\n" +
                        "  \"state\": \"string\",\n" +
                        "  \"country\": \"string\",\n" +
                        "  \"zip\": \"string\"\n" +
                        "}").
                post(URI + "/User/UpdateBillingProfile").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("updateBillingProfile.json")).
                log().body();
    }

    @Test(description = "without AuthToken + status code == 406")
    public void updateBillingProfileInfoNegativeTest(){
        given().
                contentType("application/json").
                header("Cache-Control", "no-cache").
                body("{\n" +
                        "  \"address\": \"string\",\n" +
                        "  \"city\": \"string\",\n" +
                        "  \"state\": \"string\",\n" +
                        "  \"country\": \"string\",\n" +
                        "  \"zip\": \"string\"\n" +
                        "}").
                post(URI + "/User/UpdateBillingProfile").
                then().
                assertThat().
                statusCode(406).
                log().body();
    }

    @Test
    public void updateShipProfileInfo(){
        given().
                contentType("application/json").
                header("Authorization", "Bearer "+token).
                header("Cache-Control", "no-cache").
                body("{\n" +
                        "  \"firstName\": \"string\",\n" +
                        "  \"lastName\": \"string\",\n" +
                        "  \"address\": \"string\",\n" +
                        "  \"city\": \"string\",\n" +
                        "  \"state\": \"string\",\n" +
                        "  \"country\": \"string\",\n" +
                        "  \"zip\": \"string\",\n" +
                        "  \"phone\": \"string\"\n" +
                        "}").
                post(URI + "/User/UpdateShipProfile").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("updateShipProfile.json")).
                log().body();
    }

    @Test(description = "without AuthToken + status code == 406")
    public void updateShipProfileInfoNegativeTest(){
        given().
                contentType("application/json").
                header("Cache-Control", "no-cache").
                body("{\n" +
                        "  \"firstName\": \"string\",\n" +
                        "  \"lastName\": \"string\",\n" +
                        "  \"address\": \"string\",\n" +
                        "  \"city\": \"string\",\n" +
                        "  \"state\": \"string\",\n" +
                        "  \"country\": \"string\",\n" +
                        "  \"zip\": \"string\",\n" +
                        "  \"phone\": \"string\"\n" +
                        "}").
                post(URI + "/User/UpdateShipProfile").
                then().
                assertThat().
                statusCode(406).
                log().all();
    }

    @Test
    public void getUserSubscribe(){
        given().
                contentType("application/json").
                header("Authorization", "Bearer "+token).
                header("Cache-Control", "no-cache").
                post(URI + "/User/GetSubscribe").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("updateSubscribe.json")).
                log().body();
    }

    @Test
    public void updateUserSubscribe(){
        given().
                contentType("application/json").
                header("Authorization", "Bearer "+token).
                header("Cache-Control", "no-cache").
                body("{\n" +
                        "  \"isEnableGeneral\": false,\n" +
                        "  \"isEnableDiscount\": false\n" +
                        "}").
                post(URI + "/User/UpdateSubscribe").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("updateSubscribe.json")).
                log().body();
    }

    @Test(description = "incorrect value + status code == 406")
    public void updateUserSubscribeNegativeTest(){
        given().
                contentType("application/json").
                header("Authorization", "Bearer "+token).
                header("Cache-Control", "no-cache").
                body("{\n" +
                        "  \"isEnableGeneral\": a,\n" +
                        "  \"isEnableDiscount\": false\n" +
                        "}").
                post(URI + "/User/UpdateSubscribe").
                then().
                assertThat().
                statusCode(406).
                log().body();
    }


}

