package con.soicheung.rest.day01;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;


public class FutureloanTokenTest {


    @Test
    public  void testLogin(){
        String jsonStr ="{\"mobile_phone\":\"13381638393\",\"pwd\":\"12345678\"}";
        Response res =
                RestAssured
                .given()
//                .contentType("application/json;charset=utf-8")
                .header("Content-Type", "application/json;charset=utf-8")
                .header("X-Lemonban-Media-Type", "lemonban.v2")
                .body(jsonStr)
                .when()
                .post("http://api.lemonban.com/futureloan/member/login")
                .then()
                .extract().response();

        //提取响应状态码
        System.out.println(res.getStatusCode());

        //提取响应头
        System.out.println(res.getHeaders());
        System.out.println(res.header("Content-Type"));

        //接口响应时间
        System.out.println(res.getTime());

        //GPath获取元素
        String tokenValue = res.path("data.token_info.token");
        Integer memberId = res.path("data.id");
        System.out.println(tokenValue);
        System.out.println("--------------------------");


        //充值请求
        //把请求数据放到hashmap里面
//        String jsonStr2 ="{\"member_id\":\""+memberId+"\",\"amount\":\"12345678\"}";

        Map<String,Object> map = new HashMap<>();
        map.put("member_id", memberId);
        map.put("amount", "10000");
        RestAssured
                .given()
//                .contentType("application/json;charset=utf-8")
                    .header("Content-Type", "application/json;charset=utf-8")
                    .header("X-Lemonban-Media-Type", "lemonban.v2")
                    .header("Authorization","Bearer "+tokenValue)
                    .body(map)
                .when()
                    .post("http://api.lemonban.com/futureloan/member/recharge")
                .then()
                    .log(). body();
    }
}
