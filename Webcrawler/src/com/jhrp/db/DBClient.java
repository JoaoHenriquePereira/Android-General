package com.jhrp.db;

import java.sql.*;

/**
 * Created by joaopereira on 01/10/14.
 */
public class DBClient {

    private static final String DATABASE_NAME = "d_web_crawler";
    private static final String DATABASE_USER = "crawler";
    private static final String DATABASE_PASSWORD = "crawler";

    public Connection conn = null;

    public DBClient(){
        try {
            //Everything simple and no dependency injection for now
            Class.forName("com.mysql.jdbc.Driver");
            //String url for simplicity
            String url = "jdbc:mysql://localhost:3306/"+DATABASE_NAME+"?user="+DATABASE_USER+"&password="+DATABASE_PASSWORD;
            conn = DriverManager.getConnection(url);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        assert conn != null;
        conn.close();
    }
}
