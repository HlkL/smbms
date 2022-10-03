package com.hg.dao;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @program: smbms
 * @description: 操作数据库的公共类
 * @author: hougen
 * @create: 2022-09-22 22:43
 */
public class BaseDao {
    private static String driver;
    private static String url;
    private static String userName;
    private static String password;

    //静态代码块,类加载的时候就初始化了
    static {
        Properties properties = new Properties();
        //通过类加载器读取对应的资源
        InputStream is = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");
        try {
            properties.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        userName = properties.getProperty("userName");
        password = properties.getProperty("password");
    }

    /**
    * @Description:  获取数据库的连接
    * @Author: hougen
    */
    public static Connection getConnection(){
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url,userName,password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    /**
    * @Description:  查询公共类
    * @Author: hougen
    */
    public static ResultSet execute(Connection connection, PreparedStatement pstm, ResultSet rs, String sql, Object[] params){
        try {
            pstm = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                pstm.setObject(i+1,params[i]);
            }
            rs=pstm.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    /** 
    * @Description:  编写增删改公共类
    * @Author: hougen
    */
    public static int executeUpdate(Connection connection,PreparedStatement pstm,String sql,Object[] params){
        int updateRows = 0;
        try {
            pstm = connection.prepareStatement(sql);
            for(int i = 0; i < params.length; i++){
                pstm.setObject(i+1, params[i]);
            }
            updateRows = pstm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updateRows;
    }
    /**
    * @Description:  释放资源
    * @Author: hougen
    */
    public static boolean closeResource(Connection connection,PreparedStatement pstm,ResultSet rs){
        boolean flag = true;
        if(rs != null){
            try {
                rs.close();
                //GC回收
                System.gc();
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }
        if(pstm != null){
            try {
                pstm.close();
                System.gc();
                pstm = null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }
        if(connection != null){
            try {
                connection.close();
                System.gc();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
                flag = false;
            }
        }
        return flag;
    }
}


