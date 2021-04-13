package com.soicheung.base;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soicheung.enviromentdatas.Constants;
import com.soicheung.pojo.CaseInfo;
import com.soicheung.enviromentdatas.GlobalEnvironment;
import com.soicheung.util.JDBCUtils;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

/**
 * 测试用例的父类
 */
public class BaseCase {

    @BeforeTest
    public void globalSetup(){
        //整体全局性前置配置/初始化
        //1.设置项目的baseUrl
        RestAssured.baseURI = "http://api.lemonban.com/futureloan";
        //2.设置接口响应的结果如果是Json返回的小数类型,使用BigDecimal类型来存储
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        //3.设置项目的日志存储到本地文件中
       /* PrintStream fileOutPutStream = null;
        try {
            fileOutPutStream = new PrintStream(new File("log/test_all.log"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        RestAssured.filters(new RequestLoggingFilter(fileOutPutStream), new ResponseLoggingFilter(fileOutPutStream));*/
    }

    /**
     * 将日志重定向到单独的文件中
     * @param caseInfo 用例信息
     */
    public String addLogFile(CaseInfo caseInfo){
        //日志 创建以及日志存放位置
        String loadFilePath ="";
        if (!Constants.IS_DEBUG){String dirPath = "target/log/"+caseInfo.getInterfaceName();
            File dirFile = new File(dirPath);
            if (!dirFile.isDirectory()){
                dirFile.mkdirs();
            }
            loadFilePath = dirPath+"/"+caseInfo.getInterfaceName()+"_"+caseInfo.getCaseId()+".log";
            PrintStream fileOutPutStream = null;
            try {
                fileOutPutStream = new PrintStream(new File(loadFilePath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            RestAssured.config = RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(fileOutPutStream));
        }
        return loadFilePath;
    }

    /**
     * 将日志作为附件添加到allure报告中
     * @param logFilePath 日志文件的路径
     */
    public void addLogToAllure(String logFilePath){
        if (!Constants.IS_DEBUG){
            try {
                Allure.addAttachment("接口请求的响应信息", new FileInputStream(logFilePath) );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从Excel中读取用例数据
     * @param index sheet的索引,从0开始
     * @return 所有的测试用例
     */
    public List<CaseInfo> getCaseDataFromExcel(int index) {
        File file = new File(Constants.EXCEL_PATH);
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(index);

        List<CaseInfo> caseInfos = ExcelImportUtil.importExcel(file, CaseInfo.class, importParams);
        return caseInfos;
    }

    /**
     * 参数化替换单条信息
     *
     * @param caseInfo 当前测试类中的所用测试用例数据
     * @return 替换之后的用例数据
     */
    public CaseInfo paramsReplacesCaseInfo(CaseInfo caseInfo) {

        //参数化替换请求头
        String requestHeader = regexReplace(caseInfo.getRequestHeader());
        caseInfo.setRequestHeader(requestHeader);

        //参数化替换请求地址
        String url = regexReplace(caseInfo.getUrl());
        caseInfo.setUrl(url);

        //参数化替换输入参数
        String inputParams = regexReplace(caseInfo.getInputParams());
        caseInfo.setInputParams(inputParams);

        //参数化替换期望值
        String expected = regexReplace(caseInfo.getExpected());
        caseInfo.setExpected(expected);

        //参数化替换数据库校验
        String checkSQL = regexReplace(caseInfo.getCheckSQL());
        caseInfo.setCheckSQL(checkSQL);

        return caseInfo;

    }

    /**
     * 参数化替换
     *
     * @param caseInfoList 当前测试类中的所用测试用例数据
     * @return 替换之后的用例数据
     */
    public List<CaseInfo> paramsReplaces(List<CaseInfo> caseInfoList) {
        //参数化处理
        for (CaseInfo caseInfo : caseInfoList) {

            //参数化替换请求头
            String requestHeader = regexReplace(caseInfo.getRequestHeader());
            caseInfo.setRequestHeader(requestHeader);

            //参数化替换请求地址
            String url = regexReplace(caseInfo.getUrl());
            caseInfo.setUrl(url);

            //参数化替换输入参数
            String inputParams = regexReplace(caseInfo.getInputParams());
            caseInfo.setInputParams(inputParams);

            //参数化替换期望值
            String expected = regexReplace(caseInfo.getExpected());
            caseInfo.setExpected(expected);

            //参数化替换数据库校验
            String checkSQL = regexReplace(caseInfo.getCheckSQL());
            caseInfo.setCheckSQL(checkSQL);
        }

        return caseInfoList;

    }

    /**
     * 正则替换
     *
     * @param sourceStr 原始的字符串
     * @return 查找匹配的内容
     */
    public String regexReplace(String sourceStr) {
        //如果参数化的源字符串为null的话就不需要进行参数化的替换过程
        if (sourceStr == null) {
            return sourceStr;
        }

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

            //5.先找到环境变量里面的值
            Object replaceSrt = GlobalEnvironment.envDatas.get(singleStr);
            //6.替换原始的字符串内容
            sourceStr = sourceStr.replace(findStr, replaceSrt + "");
        }

        //返回原样
        return sourceStr;
    }

    /**
     * 用例的公共断言方法,断言期望值和实际值
     * @param caseInfo 用例信息
     * @param res 接口返回的响应结果
     */
    public void assertExpected(CaseInfo caseInfo, Response  res){
        //断言
        //获取断言信息
        //1.把json字符串转换成map
        ObjectMapper objectMapper1 = new ObjectMapper();
        Map expectedMap = null;
        try {
            expectedMap = objectMapper1.readValue(caseInfo.getExpected(), Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //2.循环遍历到map里面的每一对键值对
        Set<Map.Entry<String, Object>> set = expectedMap.entrySet();
        for (Map.Entry<String, Object> map : set) {
            Object expected = map.getValue();
            if (expected instanceof Float  || expected instanceof Double){
                System.out.println("将小数转换成BigDecimal类型");
                BigDecimal bigDecimalData = new BigDecimal(expected.toString());
                Assert.assertEquals(res.path(map.getKey()), bigDecimalData,"接口响应信息断言失败");
            }else {
                Assert.assertEquals(res.path(map.getKey()), expected,"接口响应信息断言失败");
            }
        }
    }

    /**
     * 断言数据库 公共方法的封装
     * @param caseInfo 用例信息
     */
    public void assertSQL(CaseInfo caseInfo){
        String checkSQL = caseInfo.getCheckSQL();
        if (checkSQL != null){
            Map checkSQLMap = fromJsonToMap(checkSQL);
            Set<Map.Entry<String,Object>> set = checkSQLMap.entrySet();
            for (Map.Entry<String, Object> mapEntry : set) {
                String sql = mapEntry.getKey();
                //查询数据库
                Object actual = JDBCUtils.querySingle(sql);
                if (actual instanceof Long){
                    Long excepted = new Long(mapEntry.getValue().toString());
                    System.out.println("Long类型和Integer类型去断言");
                    Assert.assertEquals(actual, excepted,"数据库断言失败");
                }else if (actual instanceof BigDecimal){
                    BigDecimal expected = new BigDecimal(mapEntry.getValue().toString());
                    System.out.println("BigDecimal类型和Double类型去断言");
                    Assert.assertEquals(actual, expected,"数据库断言失败");
                }else {
                    System.out.println("字符串类型的断言");
                    Assert.assertEquals(actual, mapEntry.getValue(),"数据库断言失败");
                }
            }
        }
    }

    /**
     * 把json字符串转成map
     * @param jsonStr 需要转换的json字符串
     * @return map
     */
    public Map fromJsonToMap(String jsonStr){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonStr, Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
