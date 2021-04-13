package com.soicheung.util;

import java.util.Random;

public class PhoneRandom {

    /**
     * 随机生成一个手机号码
     *
     * @return
     */
    public static String getPhone() {

        //定义号码的号段
        String phonePrefix = "133";
        //后面8位的话可以随机生成
        Random random = new Random();
        //循环8次把每一次生成的整数拼接上
        for (int i = 0; i < 8; i++) {
            //nextInt 随机生成一个整数,参数可以指定
            int num = random.nextInt(9);
            phonePrefix += num;
        }
        return phonePrefix;

    }


    /**
     * 获取数据库中没有注册过的一个手机号码
     *
     * @return
     */
    public static String getRandomPhone() {
        while (true) {
            String phone = getPhone();
            Object result = JDBCUtils.querySingle("select count(*) from member where mobile_phone =" + phone);
            if ((Long) result == 1) {
                System.out.println("已经被注册");
            } else {
                return phone;
            }

        }
    }

    public static void main(String[] args) {
        String randomPhone = getRandomPhone();
        System.out.println(randomPhone);
    }

}
