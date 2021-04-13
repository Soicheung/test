package com.soicheung.util;


import com.soicheung.enviromentdatas.Constants;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class JDBCUtils {

    public static void main(String[] args) {
//        String sql = "update member set  mobile_phone = 133101020211 where id =3" ;
//        String sql = "update member set  mobile_phone = 133101020211 where id =3" ;
//        String sql = "select * from member limit 5" ;
        String sql = "select reg_name from member where  mobile_phone = 133101020211 " ;
//        update(sql);
//        List<Map<String, Object>> maps = queryAll(sql);
        Object querySingle = querySingle(sql);

        System.out.println(querySingle);
    }

    public static Connection getConnection() {
        //定义数据库连接
        //Oracle：jdbc:oracle:thin:@localhost:1521:DBName
        //SqlServer：jdbc:microsoft:sqlserver://localhost:1433; DatabaseName=DBName
        //MySql：jdbc:mysql://localhost:3306/DBName
        String url = Constants.jdbcURL;
        String user = Constants.jdbcUsername;
        String password = Constants.jdbcPassword;
        //定义数据库连接对象
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }


    public static void update(String sql) {

        Connection connection = getConnection();

        QueryRunner queryRunner = new QueryRunner();

        try {
            queryRunner.update(connection, sql);

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    /**
     * 查询所有的结果集
     * @param sql 要执行的sql语句
     * @return 表中的所有结果集
     */
    public static List<Map<String, Object>> queryAll(String sql){

        Connection connection = getConnection();

        QueryRunner queryRunner = new QueryRunner();

        try {
            List<Map<String, Object>> result = queryRunner.query(connection, sql, new MapListHandler());
            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 查询结果集中的第一条数据
     * @param sql SQL语句
     * @return 查询到一条结果集
     */
    public static Map<String, Object> queryOne(String sql){

        Connection connection = getConnection();

        QueryRunner queryRunner = new QueryRunner();

        try {
            Map<String, Object> result = queryRunner.query(connection, sql, new MapHandler());
            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return null;
    }

    /**
     *查询结果集中的中的单个数据
     * @param sql 要执行的sql语句
     * @return 结果
     */
    public static Object querySingle(String sql){

        Connection connection = getConnection();

        QueryRunner queryRunner = new QueryRunner();

        try {
            Object result = queryRunner.query(connection, sql, new ScalarHandler<Object>());
            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return null;
    }
}