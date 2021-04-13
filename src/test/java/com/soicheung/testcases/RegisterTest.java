package com.soicheung.testcases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soicheung.base.BaseCase;
import com.soicheung.enviromentdatas.GlobalEnvironment;
import com.soicheung.pojo.CaseInfo;
import com.soicheung.util.JDBCUtils;
import com.soicheung.util.PhoneRandom;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegisterTest extends BaseCase {
    List<CaseInfo> caseInfoList ;

    @BeforeClass
    public void setup() {

        //读取到用例数据
        caseInfoList =getCaseDataFromExcel(0);

    }

    @DataProvider
    public Object[] getRegisterDates() {

        return caseInfoList.toArray();

    }

    @Test(dataProvider = "getRegisterDates")
    public void testRegister(CaseInfo caseInfo) throws JsonProcessingException, FileNotFoundException {
        //随机生成三个没有注册过的手机号码
        if (caseInfo.getCaseId() == 1){         //存到环境变量中
            String randomPhone1 = PhoneRandom.getRandomPhone();
            GlobalEnvironment.envDatas.put("mobile_phone1", randomPhone1);
        }else if (caseInfo.getCaseId() == 2){        //存到环境变量中
            String randomPhone2 = PhoneRandom.getRandomPhone();
            GlobalEnvironment.envDatas.put("mobile_phone2", randomPhone2);
        }else if (caseInfo.getCaseId() == 3){        //存到环境变量中
            String randomPhone3 = PhoneRandom.getRandomPhone();
            GlobalEnvironment.envDatas.put("mobile_phone3", randomPhone3);
        }
        //参数化替换 --对当前的case数据进行替换
        caseInfo = paramsReplacesCaseInfo(caseInfo);

        //日志
        String logFilePath = addLogFile(caseInfo);

        //请求头由json转成
        Map headersMap = fromJsonToMap(caseInfo.getRequestHeader());
        Response res = RestAssured
                .given()
                            .log().all()
                            .headers(headersMap)
                            .body(caseInfo.getInputParams())
                .when()
                             .post(caseInfo.getUrl())
                .then()
                            .log().all()
                            .extract().response();
        //接口请求结束之后,把请求和响应的信息添加到Allure中(附件的形式)
        //第一个参数:附件的名字 第二个参数 FileInputStream
        addLogToAllure(logFilePath);
        //断言
        //1.获取断言响应结果
        assertExpected(caseInfo, res);
        //2.断言数据库
        assertSQL(caseInfo);

        String inputParams = caseInfo.getInputParams();
        ObjectMapper objectMapper2 = new ObjectMapper();
        Map inputsParams = objectMapper2.readValue(inputParams, Map.class);
        Object pwd = inputsParams.get("pwd");

        if (caseInfo.getCaseId() == 1) {
            //保存到环境变量中
            GlobalEnvironment.envDatas.put("mobile_phone1", res.path("data.mobile_phone"));
            GlobalEnvironment.envDatas.put("member_id1", res.path("data.id"));
            GlobalEnvironment.envDatas.put("pwd1", pwd+"");
        }else  if (caseInfo.getCaseId() == 2) {
            //保存到环境变量中
            GlobalEnvironment.envDatas.put("mobile_phone2", res.path("data.mobile_phone"));
            GlobalEnvironment.envDatas.put("member_id2", res.path("data.id"));
            GlobalEnvironment.envDatas.put("pwd2", pwd+"");
        }else  if (caseInfo.getCaseId() == 3) {
            //保存到环境变量中
            GlobalEnvironment.envDatas.put("mobile_phone3", res.path("data.mobile_phone"));
            GlobalEnvironment.envDatas.put("member_id3", res.path("data.id"));
            GlobalEnvironment.envDatas.put("pwd3", pwd+"");
        }

    }

}


