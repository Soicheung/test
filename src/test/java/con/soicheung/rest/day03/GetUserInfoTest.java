package con.soicheung.rest.day03;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jdk.nashorn.internal.ir.IfNode;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetUserInfoTest {

    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setup() {
        //用户信息接口模块
        caseInfoList = getCaseDataFromExcel(2);
        caseInfoList = paramsReplaces(caseInfoList);
    }

    @Test(dataProvider = "getUserInfoDates")
    public void testGetUserInfo(CaseInfo caseInfo) throws JsonProcessingException {
        String PATH = "http://api.lemonban.com/futureloan";

        //fastjson
        //Map map = JSONObject.parseObject(requestHeader, Map.class);

        //jackson
        ObjectMapper objectMapper = new ObjectMapper();
        Map headersMap = objectMapper.readValue(caseInfo.getRequestHeader(), Map.class);

        Response res = RestAssured
                .given()
                .headers(headersMap)
                .when()
                .get(PATH + caseInfo.getUrl())
                .then()
                .extract().response();
        //断言
        //获取断言信息
//        ObjectMapper objectMapper2 = new ObjectMapper();
        Map expectedMap = objectMapper.readValue(caseInfo.getExpected(), Map.class);
        Set<Map.Entry<String, Object>> set = expectedMap.entrySet();
        for (Map.Entry<String, Object> map : set) {
            Assert.assertEquals(res.path(map.getKey()), map.getValue());
        }

    }


    @DataProvider
    public Object[] getUserInfoDates() {

        return caseInfoList.toArray();

    }

    public List<CaseInfo> getCaseDataFromExcel(int index) {
        File file = new File("src/test/resources/api_testcases_futureloan_v2.xls");
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(index);

        List<CaseInfo> caseInfos = ExcelImportUtil.importExcel(file, CaseInfo.class, importParams);
        return caseInfos;
    }

    /**
     * 正则替换
     *
     * @param sourceStr 原始的字符串
     * @return 查找匹配的内容
     */
    public String regexReplace(String sourceStr) {

        //1. 定义正则表达式
        String regex = "\\{\\{(.*?)\\}\\}";
        //2.通过正则表达式编译匹配器
        Pattern pattern = Pattern.compile(regex);
        //3.开始进行匹配
        Matcher matcher = pattern.matcher(sourceStr);
        //保存匹配到的这个表达式,比如{{member_id}}
        String findStr = "";
        //保存匹配到的()里面的内容 ,比如 member_id
        String singleStr = "";
        //4.连续查找连续匹配
        while (matcher.find()) {
            //输出找到的结果 匹配整个正则对应的字符串内容
            findStr = matcher.group(0);
            //大括号里面的内容
            singleStr = matcher.group(1);
        }
        //5.先找到环境变量里面的值
        Object replaceSrt = GlobalEnvironment.envDatas.get(singleStr);
        //6.替换原始的字符串内容
        return sourceStr.replace(findStr, replaceSrt + "");
    }

    public List<CaseInfo> paramsReplaces(List<CaseInfo> caseInfoList) {
        //参数化处理
        for (CaseInfo caseInfo : caseInfoList) {

            //重新赋值
            if (caseInfo.getRequestHeader() != null) {
                String requestHeader = regexReplace(caseInfo.getRequestHeader());
                caseInfo.setRequestHeader(requestHeader);
            }

            //重新赋值
            if (caseInfo.getUrl() != null) {
                String url = regexReplace(caseInfo.getUrl());
                caseInfo.setUrl(url);
            }

            //重新赋值
            if (caseInfo.getInputParams() != null) {
                String inputParams = regexReplace(caseInfo.getInputParams());
                caseInfo.setInputParams(inputParams);
            }

            //重新赋值
            if (caseInfo.getExpected() != null) {
                String expected = regexReplace(caseInfo.getExpected());
                caseInfo.setExpected(expected);
            }
        }

        return caseInfoList;

    }

    public static void main(String[] args) {
        Integer memberId = 1111;
        String str1 = "member/{{memberId}}/info";
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
            //输出找到的结果
            //0匹配到整个正则对应的字符串内容
            //System.out.println(matcher.group(0));
            //1大括号里面的内容
            //System.out.println(matcher.group(1));
            findStr = matcher.group(0);

        }

        String outStr = str1.replace(findStr, "10010");
        System.out.println(outStr);


    }

}
