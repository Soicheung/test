package con.soicheung.rest.day02;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class AssertTest {
    @Test
    public  void testLogin(){
        String jsonStr ="{\"mobile_phone\":\"13381638393\",\"pwd\":\"12345678\"}";
        given()
//                .contentType("application/json;charset=utf-8")
                .header("Content-Type", "application/json;charset=utf-8")
                .header("X-Lemonban-Media-Type","lemonban.v1")
                .body(jsonStr)
                .when()
                .post("http://api.lemonban.com/futureloan/member/login")
                .then()
                .log().all();
    }
}
