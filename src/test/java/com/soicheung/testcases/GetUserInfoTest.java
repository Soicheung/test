package com.soicheung.testcases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soicheung.base.BaseCase;
import com.soicheung.enviromentdatas.Constants;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetUserInfoTest extends BaseCase {

    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setup() {
        //用户信息接口模块
        caseInfoList = getCaseDataFromExcel(2);
        caseInfoList = paramsReplaces(caseInfoList);
    }

    @Test(dataProvider = "getUserInfoDates")
    public void testGetUserInfo(CaseInfo caseInfo) throws JsonProcessingException, FileNotFoundException {

        //fastjson
        //Map map = JSONObject.parseObject(requestHeader, Map.class);
        //日志
        String logFilePath = addLogFile(caseInfo);

        //jackson
        //请求头由json转成
        Map headersMap = fromJsonToMap(caseInfo.getRequestHeader());

        Response res = RestAssured
                .given().log().all()
                .headers(headersMap)
                .when()
                .get( caseInfo.getUrl())
                .then().log().all()
                .extract().response();

        addLogToAllure(logFilePath);

        //断言
        //获取断言信息
        assertExpected(caseInfo, res);


    }


    @DataProvider
    public Object[] getUserInfoDates() {

        return caseInfoList.toArray();

    }


    public static void main(String[] args) {
        Integer memberId = 1111;
        String str1 = "member/{{memberId}}/info{{mobile_phone}}";
        String str2 = "{\n" +
                "    \"code\": 0,\n" +
                "    \"msg\": \"OK\",\n" +
                "    \"data.id\": {{memberId}}, \"data.mobile_phone\":\"13323234545\"\n" +
                "}";
        //参数化的替换功能
        //1. 定义正则表达式
        String regex = "\\{\\{(.*?)\\}\\}";
        //2.通过正则表达式编译匹配器
        Pattern pattern = Pattern.compile(regex);
        //3.开始进行匹配
        Matcher matcher = pattern.matcher(str1);

        String findStr = "";
        //4.连续查找连续匹配
        while (matcher.find()) {
//            输出找到的结果
//            0匹配到整个正则对应的字符串内容
            System.out.println(matcher.group(0));
//            1大括号里面的内容
            System.out.println(matcher.group(1));
            findStr = matcher.group(0);
            //每一次匹配到就去替换
            findStr = str1.replace(findStr, "10010");
            str1 = findStr;

        }
            System.out.println(findStr);



    }

}
