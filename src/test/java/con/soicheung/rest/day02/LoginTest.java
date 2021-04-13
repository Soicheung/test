package con.soicheung.rest.day02;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import con.soicheung.rest.day02.pojo.CaseInfo;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;


public class LoginTest {

    @Test(dataProvider = "getLoginDates02")
    public void testLogin(CaseInfo caseInfo) throws JsonProcessingException {

        String PATH = "http://api.lemonban.com/futureloan";
        String requestHeader = caseInfo.getRequestHeader();

        //fastjson
        //Map map = JSONObject.parseObject(requestHeader, Map.class);

        //jackson
        ObjectMapper objectMapper = new ObjectMapper();
        Map headersMap = objectMapper.readValue(requestHeader, Map.class);

        String header1 = (String) headersMap.get("X-Lemonban-Media-Type");
        String header2 = (String) headersMap.get("Content-Type");

        RestAssured
                .given()
                            .header("X-Lemonban-Media-Type", header1)
                            .header("Content-Type", header2)
                            .body(caseInfo.getInputParams())
                .when()
                             .post(PATH + caseInfo.getUrl())
                .then()
                .log().body();
        //d断言
    }

    @DataProvider
    public Object[][] getLoginDates01() {
        Object[][] datas = {
                //1.请求的接口地址 //2.请求方式//3.请求头//4.请求方式
                {"http://api.lemonban.com/futureloan/member/login", "post", "application/json;charset=utf-8", "lemonban.v1", "{\"mobile_phone\":\"13381638393\",\"pwd\":\"12345678\"}"},
                {"http://api.lemonban.com/futureloan/member/login", "post", "application/json;charset=utf-8", "lemonban.v1", "{\"mobile_phone\":\"133816383931\",\"pwd\":\"12345678\"}"},
                {"http://api.lemonban.com/futureloan/member/login", "post", "application/json;charset=utf-8", "lemonban.v1", "{\"mobile_phone\":\"1338163839a\",\"pwd\":\"12345678\"}"},
                {"http://api.lemonban.com/futureloan/member/login", "post", "application/json;charset=utf-8", "lemonban.v1", "{\"mobile_phone\":\"11181638393\",\"pwd\":\"12345678\"}"},
        };
        return datas;
    }

    @DataProvider
    public Object[] getLoginDates02() {

        File file = new File("src/test/resources/api_testcases_futureloan_v1.xls");
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(1);

        List<CaseInfo> caseInfoList = ExcelImportUtil.importExcel(file, CaseInfo.class, importParams);
        Object[] datas = caseInfoList.toArray();

        return datas;
    }


}
