package com.soicheung.testcases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.soicheung.base.BaseCase;
import com.soicheung.enviromentdatas.GlobalEnvironment;
import com.soicheung.pojo.CaseInfo;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;


public class AddLoanTest extends BaseCase {
    List<CaseInfo> caseInfoList ;

    @BeforeClass
    public void setup() {

        //读取到用例数据
        caseInfoList =getCaseDataFromExcel(4);
        //参数化替换 --对当前的case数据进行替换
        caseInfoList = paramsReplaces(caseInfoList);

    }

    @DataProvider
    public Object[] getAddLoanDates() {

        return caseInfoList.toArray();

    }


    @Test(dataProvider = "getAddLoanDates")
        public void testAddLoan(CaseInfo caseInfo) throws JsonProcessingException, FileNotFoundException {
        //日志
        String logFilePath = addLogFile(caseInfo);
        //请求头由json转成
        Map headersMap = fromJsonToMap(caseInfo.getRequestHeader());
        //发起接口请求
        Response res = RestAssured
                .given().log().all()
                //rest-assured 返回json小数的时候,使用BIGDECIMAL类型来存储小数(默认是float存储)
                            .headers(headersMap)
                            .body(caseInfo.getInputParams())
                .when()
                            .post(caseInfo.getUrl())
                .then()
                            .log().all()
                            .extract().response();
        addLogToAllure(logFilePath);
        //断言
        assertExpected(caseInfo, res);
        //获取项目的id
        //保存到环境变量中去
        if (res.path("data.id" ) != null){
            GlobalEnvironment.envDatas.put("loan_id", res.path("data.id"));
        }
    }
}
