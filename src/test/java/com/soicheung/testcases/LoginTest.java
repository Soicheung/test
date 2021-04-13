package com.soicheung.testcases;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soicheung.base.BaseCase;
import com.soicheung.enviromentdatas.Constants;
import com.soicheung.pojo.CaseInfo;
import com.soicheung.enviromentdatas.GlobalEnvironment;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class LoginTest extends BaseCase {
    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setup() {
        caseInfoList = getCaseDataFromExcel(1);
        //参数化替换
        caseInfoList = paramsReplaces(caseInfoList);
    }

    @DataProvider
    public Object[] getLoginDates02() {

        return caseInfoList.toArray();

    }

    @Test(dataProvider = "getLoginDates02")
    public void testLogin(CaseInfo caseInfo) throws JsonProcessingException, FileNotFoundException {
        System.out.println(caseInfo);

        String requestHeader = caseInfo.getRequestHeader();

        //fastjson
        //Map map = JSONObject.parseObject(requestHeader, Map.class);
        //日志
        String logFilePath  = addLogFile(caseInfo);

        //请求头由json转成
        Map headersMap = fromJsonToMap(caseInfo.getRequestHeader());


        Response res = RestAssured
                .given().log().all()
                                .headers(headersMap)
                                .body(caseInfo.getInputParams())
                .when()
                            .post(caseInfo.getUrl())
                .then()
                .log().all()
                        .extract().response();
        addLogToAllure(logFilePath);
        assertExpected(caseInfo, res);
        //保存memberId
        //在登录模块用例执行结束后将memberId保存到环境变量中
        //拿到正向用例响应信息中的memberId
//        System.out.println("memberId:: "+res.path("data.id"));
        //拿到正常用例返回的token值
        if (caseInfo.getCaseId() == 1) {
            GlobalEnvironment.envDatas.put("token1", res.path("data.token_info.token"));
        } else if (caseInfo.getCaseId() == 2) {
            GlobalEnvironment.envDatas.put("token2", res.path("data.token_info.token"));
        } else if (caseInfo.getCaseId() == 3) {
            GlobalEnvironment.envDatas.put("token3", res.path("data.token_info.token"));
        }

    }



    public List<CaseInfo> getCaseDataFromExcel(int index) {
        File file = new File("src/test/resources/api_testcases_futureloan_v2.xls");
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(index);

        List<CaseInfo> caseInfos = ExcelImportUtil.importExcel(file, CaseInfo.class, importParams);
        return caseInfos;
    }


}
