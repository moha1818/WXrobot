package com.example.demo;

import java.sql.*;

/**
 * @description:
 * @author: moha
 * @create: 2020-02-10 11:32
 */
public class JDBCUtil {

    private static String url = "jdbc:postgresql://rm-bp1fe870u7g606ukzyo.ppas.rds.aliyuncs.com:3433/temp";//固定格式
    private static String name = "temp";//数据库用户名
    private static String password = "Olym@2019#";//数据库密码

    /**
     * 该方法用于获取连接对象
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url,name,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 该方法用于释放资源
     */
    public static void release(Connection conn,Statement st,ResultSet rs) {
        closeRs(rs);
        closeSt(st);
        closeConn(conn);
    }
    public static void release(Connection conn,Statement st) {
        closeSt(st);
        closeConn(conn);
    }

    private static void closeRs(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            rs = null;
        }
    }
    private static void closeSt(Statement st) {
        try {
            if (st != null) {
                st.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            st = null;
        }
    }
    private static void closeConn(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn = null;
        }
    }
}