package com.dbcomponent.adapter;
import java.sql.*;

public class MySQLAdapter implements IAdapter {
    @Override
    public Connection createConnection(String url, String user, String pass) throws SQLException {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); } 
        catch (ClassNotFoundException e) { throw new SQLException(e); }
        return DriverManager.getConnection(url, user, pass);
    }
}