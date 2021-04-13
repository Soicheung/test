package con.soicheung.rest.day03;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class LoginTest {
    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setup() {
        caseInfoList = getCaseDataFromExcel(1);
    }

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

        Response res = RestAssured
                .given()
                    .header("X-Lemonban-Media-Type", header1)
                    .header("Content-Type", header2)
                    .body(caseInfo.getInputParams())
                .when()
                     .post(PATH + caseInfo.getUrl())
                .then()
                     .log().body().extract().response();
        //断言
        //获取断言信息
        String expected = caseInfo.getExpected();
        Map expectedMap = objectMapper.readValue(expected, Map.class);
        Set<Map.Entry<String, Object>> expectedSet = expectedMap.entrySet();
        for (Map.Entry<String, Object> map : expectedSet) {
//            System.out.println(map.getKey());
//            System.out.println(map.getValue());

            //关键点.Gpath获取到接口的实际响应值
            Assert.assertEquals(res.path(map.getKey()), map.getValue());

        }
        //保存memberId
        //在登录模块用例执行结束后将memberId保存到环境变量中
        //拿到正向用例响应信心中的memberId
//        System.out.println("memberId:: "+res.path("data.id"));
        Integer memberId = res.path("data.id");
        if (memberId != null) {
//                GlobalEnvironment.memberId = memberId ;
            GlobalEnvironment.envDatas.put("member_id", memberId);
            //拿到正常用例返回的token值
            String token = res.path("data.token_info.token");
            GlobalEnvironment.envDatas.put("token", token);
        }
    }

    @DataProvider
    public Object[] getLoginDates02() {

        return caseInfoList.toArray();

    }

    public List<CaseInfo> getCaseDataFromExcel(int index) {
        File file = new File("src/test/resources/api_testcases_futureloan_v2.xls");
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(index);

        List<CaseInfo> caseInfos = ExcelImportUtil.importExcel(file, CaseInfo.class, importParams);
        return caseInfos;
    }


}
