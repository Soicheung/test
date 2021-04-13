package con.soicheung.rest.day01;

import static io.restassured.RestAssured.*;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import con.soicheung.rest.day02.pojo.CaseInfo;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;


public class Futureloantest {

    @Test
    public  void testRegister(){
        String jsonStr ="{\"mobile_phone\":\"13381638391\",\"pwd\":\"12345678\",\"type\":1}";
        given()
//                .contentType("application/json;charset=utf-8")
                .header("Content-Type", "application/json;charset=utf-8")
                .header("X-Lemonban-Media-Type","lemonban.v1")
                .body(jsonStr)
        .when()
                .post("http://api.lemonban.com/futureloan/member/register")
        .then()
                .log().all();
    }

    @Test
    public  void testLogin(){
        String jsonStr ="{\"mobile_phone\":\"13381638393\",\"pwd\":\"12345678\"}";
        Response response = given()
//                .contentType("application/json;charset=utf-8")
                .header("Content-Type", "application/json;charset=utf-8")
                .header("X-Lemonban-Media-Type", "lemonban.v1")
                .body(jsonStr)
                .when()
                .post("http://api.lemonban.com/futureloan/member/login")
                .then()
                .log().all()
                .extract().response();

        int code = response.path("code");
        String msg = response.path("msg");
        String mobilePhone = response.path("data.mobile_phone");

       /* Assert.assertEquals(msg, "OK");
        Assert.assertEquals(mobilePhone, "13381638393");
        Assert.assertEquals(code, 0);*/

//        Assert.assertTrue(msg .equals("OK"));
//        Assert.assertFalse(msg.equals("OK"));
        Assert.assertEquals(mobilePhone, "1338163839","断言失败");
    }

    public static void main(String[] args) {

        File file = new File("src/test/resources/api_testcases_futureloan_v1.xls");
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(1);

        List<CaseInfo> caseInfoList = ExcelImportUtil.importExcel(file, CaseInfo.class, importParams);

        for (CaseInfo caseInfo : caseInfoList) {
            System.out.println(caseInfo.getRequestHeader());
        }
    }
}
