package con.soicheung.rest.day01;
//import static io.restassured.RestAssured.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RestApiTest {


    @Test
    public void tsetPost01(){
          //post表单请求
        RestAssured.
                given().
                                formParam("name", "张三").
                                contentType("application/x-www-form-urlencoded;charset=UTF-8").
                when().
                              post("https://httpbin.org/post").
                then().
                             log().all();
    }

    @Test
    public void tsetPost02(){
        String str= "{\"name\":\"张三\",\"age\":20,\"addr\":\"上海\"}";
        //json表单请求
        RestAssured.
                given().
                     contentType("application/json;charset=UTF-8").
                     body(str).
                when().
                    post("https://httpbin.org/post").
                 then().
                    log().all();
    }

    @Test
    public void tsetPost(){
        String strXML= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\">\n" +
                "\n" +
                "\n" +
                "<suite name=\"套件(商城项目)\">\n" +
                "    <test  preserve-order=\"true\" name=\"模块/接口A\">\n" +
                "        <classes>\n" +
                "            <class name=\"com.soicheung.testNG.TestNGDemo\"/>\n" +
                "            <class name=\"com.soicheung.testNG.TestNGDemo1\"/>\n" +
                "        </classes>\n" +
                "    </test>\n" +
                "\n" +
                "    <test  preserve-order=\"true\" name=\"模块/接口B\">\n" +
                "        <classes>\n" +
                "            <class name=\"com.soicheung.testNG.TestNGDemo2\"/>\n" +
                "            <class name=\"com.soicheung.testNG.TestNGDemo3\"/>\n" +
                "        </classes>\n" +
                "    </test>\n" +
                "\n" +
                "</suite>";
        //xml参数类型
        RestAssured.
                given().
                    contentType("text/xml;charset=utf-8").
                    body(strXML).
                when().
                    post("https://httpbin.org/post").
                then().
                    log().all();
    }

    @Test
    public void tsetPost04(){
        //post表单请求
        RestAssured.
                given().
                        contentType("multipart/form-data;charset=utf-8").
                        multiPart(new File("pom.xml")).
                when().
                        post("https://httpbin.org/post").
                then().
                         log().all();
    }


}
