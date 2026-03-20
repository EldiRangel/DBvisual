package com.dbcomponent.pool;

import com.dbcomponent.adapter.IAdapter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CustomPool {
    private final BlockingQueue<Connection> pool;

    public CustomPool(IAdapter adapter, String url, String user, String pass, int maxSize) throws SQLException {
        this.pool = new LinkedBlockingQueue<>(maxSize);
        for (int i = 0; i < maxSize; i++) {
            pool.add(adapter.createConnection(url, user, pass));
        }
    }

    public Connection getConnection() throws InterruptedException {
        return pool.take(); // Pide conexión y espera si no hay
    }

    public void releaseConnection(Connection conn) {
        if (conn != null) {
            pool.offer(conn); // La devuelve al pool
        }
    }
}