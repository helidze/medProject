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
                body("{}").
                post(URI + "/Blog/Get").
                then().
                assertThat().
                body(matchesJsonSchemaInClasspath("allExistedBlogs.json")).
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
                body(matchesJsonSchemaInClasspath("getBlogById.json")).
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
                body(matchesJsonSchemaInClasspath("allExistedCategories.json")).
                body("collection.category.title", hasItems("Bronchodilators","Cancer","Women's Health")).
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
                body("{}").
                post(URI + "/Product/Get").
                then().
                assertThat().
                contentType(ContentType.JSON).
                statusCode(200).
                body(matchesJsonSchemaInClasspath("allProducts.json")).
                body("",Matchers.hasToString("{count=250, collection=[{cover=, createdAt=0, activeSubstances=[{name=Progesterone, id=89}], reviews=[], name=Prometrium Generic, rating=0.0, id=250, text=What is for? menopausal (replacement) hormone therapy in case of progesterone deficiency with non-functioning (absent) ovaries (donation of eggs);prevention (prevention) of preterm delivery in women at risk (with shortening of the cervix and / or the presence of anamnestic data of premature birth and / or premature rupture of the membranes);support of the luteal phase during preparation for in vitro fertilization;support of the luteal phase in the spontaneous or induced menstrual cycle;premature menopause;menopausal (replacement) hormone therapy (in combination with estrogen-containing drugs);infertility due to luteal insufficiency;threatening abortion or prevention of habitual abortion due to lack of progesterone, category={name=hormonal_control, parentCategoryId=17, id=35, title=Hormonal Control, updatedAt=1527776756}, productDetails=[{cover=null, form=Cap, strength=[{strength=200.0, id=451, type=Mg, activeSubstanceId=89}], pillPrice=0.404, packType=Pcs, id=113, pack=10.0, detailsDiscount=null}, {cover=null, form=Cap, strength=[{strength=100.0, id=225, type=Mg, activeSubstanceId=89}], pillPrice=0.217, packType=Pcs, id=168, pack=10.0, detailsDiscount=null}], isHot=false, updatedAt=1527776757}, {cover=, createdAt=0, activeSubstances=[{name=Rosuvastatin Calcium, id=253}], reviews=[], name=Crestor Generic, rating=0.0, id=249, text=What is for? Primary hypercholesterolemia according to Fredrickson classification (type IIa, including familial heterozygous hypercholesterolemia) or mixed hypercholesterolemia (type IIb) as a supplement to the diet, when diet and other non-drug treatments (eg exercise, weight loss) are insufficient;family homozygous hypercholesterolemia as a supplement to diet and other lipid-lowering therapy (eg, LDL-apheresis) or in cases where such therapy is not effective enough;hypertriglyceridemia (type IV according to Fredrickson's classification) as a supplement to the diet;to slow the progression of atherosclerosis as a supplement to the diet in patients who are shown therapy to reduce the concentration of total Xc and Xc-LDL;primary prevention of major cardiovascular complications (stroke, heart attack, arterial revascularization) in adult patients without clinical signs of coronary heart disease but with an increased risk of its development (age over 50 for men and over 60 for women, increased concentration of C-reactive protein (more than 2 mg / l) in the presence of at least one of additional risk factors, such as hypertension, low concentration of HDL-C, smoking, family history of early development of coronary heart disease)., category={name=cholesterol, parentCategoryId=null, id=18, title=Cholesterol, updatedAt=1527776756}, productDetails=[{cover=null, form=Tab, strength=[{strength=40.0, id=435, type=Mg, activeSubstanceId=253}], pillPrice=0.362, packType=Pcs, id=403, pack=10.0, detailsDiscount=null}, {cover=null, form=Tab, strength=[{strength=20.0, id=436, type=Mg, activeSubstanceId=253}], pillPrice=0.155, packType=Pcs, id=405, pack=10.0, detailsDiscount=null}, {cover=null, form=Tab, strength=[{strength=5.0, id=438, type=Mg, activeSubstanceId=253}], pillPrice=0.093, packType=Pcs, id=431, pack=10.0, detailsDiscount=null}, {cover=null, form=Tab, strength=[{strength=10.0, id=437, type=Mg, activeSubstanceId=253}], pillPrice=0.135, packType=Pcs, id=438, pack=10.0, detailsDiscount=null}], isHot=false, updatedAt=1527776757}, {cover=, createdAt=0, activeSubstances=[{name=Nateglinide, id=206}], reviews=[], name=Starlix Generic, rating=0.0, id=248, text=What is for? nateglinide is indicated as a monotherapy for lowering blood glucose in patients with type 2 diabetes mellitus (insulin-independent diabetes mellitus) who do not have enough diet and exercise to control glycemia and who have not been treated for a long time with other hypoglycemic agents.  How does it work? Nateglinide is also indicated for therapy in combination with metformin in patients with inadequate glycemic control against metformin (substitution of metformin with nateglinide is not recommended). Nateglinide is an amino acid derivative that lowers blood glucose levels by stimulating insulin secretion in the pancreas. This action depends on the functional state of the beta cells of the islets of the pancreas. Nateglinide interacts with ATP-sensitive potassium channels of the pancreatic beta cells. Subsequent depolarization of beta-cell membranes opens the calcium channels, causing calcium influx and insulin secretion. The volume of released insulin depends on the level of blood glucose and at a low level it decreases. Nateglinide has high tissue selectivity and low affinity for myocardial and skeletal muscle cells., category={name=diabetes, parentCategoryId=null, id=10, title=Diabetes, updatedAt=1527776756}, productDetails=[{cover=null, form=Tab, strength=[{strength=120.0, id=354, type=Mg, activeSubstanceId=206}], pillPrice=0.217, packType=Pcs, id=95, pack=10.0, detailsDiscount=null}], isHot=false, updatedAt=1527776757}, {cover=, createdAt=0, activeSubstances=[{name=Metronidazole, id=205}], reviews=[], name=Flagyl Generic, rating=0.0, id=247, text=What is for? Trichomonas vaginitis and urethritis in women, trichomoniasis urethritis in men, giardiasis, amoebic dysentery; Anaerobic infections caused by microorganisms that are sensitive to the drug. Combined therapy of severe mixed aerobic-anaerobic infections. Prevention of anaerobic infection during surgical interventions (especially on the organs of the abdominal cavity, urinary tract). Chronic alcoholism. Chronic gastritis in the phase of exacerbation, peptic ulcer of the stomach and duodenum in the acute phase, associated with Helicobacter pylori.  How does it work? Has a wide spectrum of action against anaerobic microorganisms: Peptostreptococcus, Clostridium spp., Bacteroides spp., Fusobacterium, Prevotella, Veillonella. Suppress the development of protozoa: Trichomonas vaginalis, Giardia intestinalis (Lamblia intestinalis), Entamoeba histolytica., category={name=diabetes, parentCategoryId=null, id=10, title=Diabetes, updatedAt=1527776756}, productDetails=[{cover=null, form=Tab, strength=[{strength=400.0, id=355, type=Mg, activeSubstanceId=205}], pillPrice=0.021, packType=Pcs, id=96, pack=10.0, detailsDiscount=null}, {cover=null, form=Tab, strength=[{strength=200.0, id=356, type=Mg, activeSubstanceId=205}], pillPrice=0.01, packType=Pcs, id=132, pack=10.0, detailsDiscount=null}], isHot=false, updatedAt=1527776757}, {cover=, createdAt=0, activeSubstances=[{name=Metformin, id=204}], reviews=[], name=Glucophage Generic, rating=0.0, id=246, text=What is for? Type 2 diabetes mellitus, especially in patients with obesity, with ineffective diet and exercise:- in adults as a monotherapy or in combination with other oral hypoglycemic agents or insulin;- in children from 10 years as a monotherapy or in combination with insulin;prevention of type 2 diabetes in patients with prediabetes with additional risk factors for developing type 2 diabetes, in whom lifestyle changes did not allow adequate glycemic control.  How does it work? Metformin reduces hyperglycemia, without leading to the development of hypoglycemia. Unlike derivatives of sulfonylureas, it does not stimulate insulin secretion and does not have a hypoglycemic effect in healthy individuals. Increases the sensitivity of peripheral receptors to insulin and the utilization of glucose by cells. Reduces the production of glucose by the liver by inhibiting gluconeogenesis and glycogenolysis. Delays the absorption of glucose in the intestine. Metformin stimulates the synthesis of glycogen, affecting glycogen synthetase. Increases the transport capacity of all types of membrane glucose transporters. In addition, it has a beneficial effect on the metabolism of lipids: reduces the content of total cholesterol, LDL and triglycerides. On the background of taking metformin, the patient's body weight either remains stable or moderately decreases. Clinical studies have also shown the effectiveness of the preparation Glucophage® for the prevention of diabetes mellitus in patients with prediabetes with additional risk factors for the development of overt diabetes mellitus type 2, in whom lifestyle changes did not allow adequate glycemic control., category={name=diabetes, parentCategoryId=null, id=10, title=Diabetes, updatedAt=1527776756}, productDetails=[{cover=null, form=Tab, strength=[{strength=1000.0, id=357, type=Mg, activeSubstanceId=204}], pillPrice=0.041, packType=Pcs, id=265, pack=10.0, detailsDiscount=null}, {cover=null, form=Tab, strength=[{strength=500.0, id=359, type=Mg, activeSubstanceId=204}], pillPrice=0.01, packType=Pcs, id=267, pack=10.0, detailsDiscount=null}, {cover=null, form=Tab, strength=[{strength=850.0, id=358, type=Mg, activeSubstanceId=204}], pillPrice=0.031, packType=Pcs, id=337, pack=10.0, detailsDiscount=null}], isHot=false, updatedAt=1527776757}, {cover=, createdAt=0, activeSubstances=[{name=Glibenclamide, id=203}], reviews=[], name=Glyburide Generic, rating=0.0, id=245, text=What is for? Diabetes mellitus type 2 with ineffectiveness of diet therapy.  How does it work? Increased secretion of insulin by beta cells of the pancreas., category={name=diabetes, parentCategoryId=null, id=10, title=Diabetes, updatedAt=1527776756}, productDetails=[{cover=null, form=Tab, strength=[{strength=5.0, id=360, type=Mg, activeSubstanceId=203}], pillPrice=0.021, packType=Pcs, id=385, pack=10.0, detailsDiscount=null}], isHot=false, updatedAt=1527776757}, {cover=, createdAt=0, activeSubstances=[{name=Glipizide XL, id=215}], reviews=[], name=Generic, rating=0.0, id=244, text=What is for? Non-insulin-dependent (type II) diabetes mellitus (in addition to diet therapy).  How does it work? Increases the secretion of insulin beta cells of islet pancreatic tissue in response to food intake, increases the sensitivity of tissues to insulin (the number of corresponding receptors increases)., category={name=diabetes, parentCategoryId=null, id=10, title=Diabetes, updatedAt=1527776756}, productDetails=[{cover=null, form=Tab, strength=[{strength=10.0, id=361, type=Mg, activeSubstanceId=215}], pillPrice=0.031, packType=Pcs, id=389, pack=10.0, detailsDiscount=null}], isHot=false, updatedAt=1527776757}, {cover=, createdAt=0, activeSubstances=[{name=Glicazide, id=230}], reviews=[], name=Generic, rating=0.0, id=243, text=What is for? Diabetes mellitus type 2, monotherapy and in combination with insulin or other oral hypoglycemic drugs.  How does it work? Increases the secretion of insulin with beta cells of the pancreas and improves the utilization of glucose. Stimulates the activity of muscle glycogen synthetase. Effective in metabolic, latent diabetes mellitus, in patients with exogenously-constitutional obesity. Normalizes the glycemic profile after several days of treatment. Reduces the time interval from the time of food intake to the onset of insulin secretion, restores the early peak of insulin secretion, and reduces hyperglycemia caused by food intake. It improves hematological parameters, rheological properties of blood, hemostasis system and microcirculation. Prevents development of microvasculitis, incl. defeat the retina of the eye. Suppresses the aggregation of platelets, significantly increases the index of relative disaggregation, increases heparin and fibrinolytic activity, increases tolerance to heparin. It shows antioxidant properties, improves vascularization of the conjunctiva, provides continuous blood flow in microvessels, neutralizes signs of microstock. With diabetic nephropathy, it reduces proteinuria., category={name=diabetes, parentCategoryId=null, id=10, title=Diabetes, updatedAt=1527776756}, productDetails=[{cover=null, form=Tab, strength=[{strength=40.0, id=362, type=Mg, activeSubstanceId=230}], pillPrice=0.062, packType=Pcs, id=391, pack=10.0, detailsDiscount=null}, {cover=null, form=Tab, strength=[{strength=80.0, id=363, type=Mg, activeSubstanceId=230}], pillPrice=0.104, packType=Pcs, id=393, pack=10.0, detailsDiscount=null}], isHot=false, updatedAt=1527776757}, {cover=, createdAt=0, activeSubstances=[{name=Rosiglitazone, id=236}], reviews=[], name=Avandia Generic, rating=0.0, id=242, text=What is for? Non-insulin-dependent diabetes mellitus type 2: monotherapy with ineffectiveness of diet and adequate physical activity or in combination with sulfonylureas and metformin derivatives in order to improve glycemic control., category={name=diabetes, parentCategoryId=null, id=10, title=Diabetes, updatedAt=1527776756}, productDetails=[{cover=null, form=Tab, strength=[{strength=2.0, id=364, type=Mg, activeSubstanceId=236}], pillPrice=0.124, packType=Pcs, id=397, pack=10.0, detailsDiscount=null}, {cover=null, form=Tab, strength=[{strength=4.0, id=366, type=Mg, activeSubstanceId=236}], pillPrice=0.186, packType=Pcs, id=398, pack=10.0, detailsDiscount=null}], isHot=false, updatedAt=1527776757}, {cover=, createdAt=0, activeSubstances=[{name=Glimepiride, id=232}], reviews=[], name=Amaryl Generic, rating=0.0, id=241, text=What is for? Diabetes mellitus type 2 (in monotherapy or as part of combination therapy with metformin or insulin).  How does it work? Glimepiride reduces the concentration of glucose in the blood, mainly by stimulating the release of insulin from the beta cells of the pancreas. Its effect is mainly associated with improving the ability of beta cells of the pancreas to respond to physiological stimulation with glucose. Compared to glibenclamide, taking low doses of glimepiride causes the release of less insulin when the blood glucose concentration is approximately the same. This fact testifies in favor of the presence in glimepiride of extrapancreatic hypoglycemic effects (increase of sensitivity of tissues to insulin and insulinomimetic effect)., category={name=diabetes, parentCategoryId=null, id=10, title=Diabetes, updatedAt=1527776756}, productDetails=[{cover=null, form=Tab, strength=[{strength=1.0, id=395, type=Mg, activeSubstanceId=232}], pillPrice=0.031, packType=Pcs, id=382, pack=10.0, detailsDiscount=null}, {cover=null, form=Tab, strength=[{strength=2.0, id=394, type=Mg, activeSubstanceId=232}], pillPrice=0.041, packType=Pcs, id=401, pack=10.0, detailsDiscount=null}], isHot=false, updatedAt=1527776757}]}")).
                log().body();
    }

    @Test
    public void getProduct(){
        given().
                contentType(ContentType.JSON).
                body("{\n" +
                        "  \"id\": 1\n" +
                        "}").
                when().
                post(URI + "/Product/GetOne").
                then().
                assertThat().
//                body(matchesJsonSchemaInClasspath("getProductById.json")).
                statusCode(200).
                log().body();
    }
}

