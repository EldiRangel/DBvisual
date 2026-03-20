package com.dbcomponent.adapter;

import java.sql.Connection;
import java.sql.SQLException;

public interface IAdapter {
    Connection createConnection(String url, String user, String pass) throws SQLException;
}