package com.dbcomponent.adapter;
import java.sql.*;

public class PostgresAdapter implements IAdapter {
    @Override
    public Connection createConnection(String url, String user, String pass) throws SQLException {
        try { Class.forName("org.postgresql.Driver"); } 
        catch (ClassNotFoundException e) { throw new SQLException(e); }
        return DriverManager.getConnection(url, user, pass);
    }
}