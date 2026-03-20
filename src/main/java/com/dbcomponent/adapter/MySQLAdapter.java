package com.dbcomponent.adapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLAdapter implements IAdapter {
    @Override
    public Connection createConnection(String url, String user, String pass) throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
}