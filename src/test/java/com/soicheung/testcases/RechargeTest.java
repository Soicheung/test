package com.soicheung.testcases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.soicheung.base.BaseCase;
import com.soicheung.pojo.CaseInfo;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public class RechargeTest extends BaseCase {
    List<CaseInfo> caseInfoList ;

    @BeforeClass
    public void setup() {

        //读取到用例数据
        caseInfoList =getCaseDataFromExcel(3);

    }

    @DataProvider
    public Object[] getRechargeDates() {

        return caseInfoList.toArray();

    }

@Test(dataProvider = "getRechargeDates")
    public void testRecharge(CaseInfo caseInfo) throws JsonProcessingException, FileNotFoundException {

        //参数化替换 --对当前的case数据进行替换
        caseInfo = paramsReplacesCaseInfo(caseInfo);

        //日志
    String logFilePath = addLogFile(caseInfo);

    //请求头由json转成
        Map headersMap = fromJsonToMap(caseInfo.getRequestHeader());

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
        //接口响应断言
        assertExpected(caseInfo, res);
        //数据库断言
        assertSQL(caseInfo);

    }

    public static void main(String[] args) {

        Double a = 0.01;
        Float b = 0.01F;

        BigDecimal bigDecimala = new BigDecimal(a.toString());
        BigDecimal bigDecimalb = new BigDecimal(b.toString());

        Assert.assertEquals(bigDecimala, bigDecimalb);
    }
}
