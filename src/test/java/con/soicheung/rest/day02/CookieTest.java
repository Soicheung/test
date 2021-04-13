package con.soicheung.rest.day02;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class CookieTest {
    Map<String, String> cookiesMap = new HashMap<>();
    @Test
    public void testAuthenticationWithSession(){
        //post表单请求
        Response response = RestAssured.
                given().
                header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8").
                param("loginame", "admin").
                param("password", "e10adc3949ba59abbe56e057f20f883e").
                when().
                post("http://erp.lemfix.com/user/login").
                then().
                log().all()
                .extract().response();

//        System.out.println(response.header("Set-Cookie"));

        cookiesMap =response.getCookies();

    }

    @Test(dependsOnMethods ={"testAuthenticationWithSession"} )
    public void testAAA(){
        RestAssured.
                given().
                cookies(cookiesMap).
                when().
                get("http://erp.lemfix.com/user/getUserSession").
                then().
                log().all();
    }
}
