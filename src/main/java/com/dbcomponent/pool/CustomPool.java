package com.dbcomponent.pool;
import com.dbcomponent.adapter.IAdapter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CustomPool {
    private final BlockingQueue<Connection> pool;

    public CustomPool(IAdapter adapter, String url, String user, String pass, int size) throws SQLException {
        this.pool = new LinkedBlockingQueue<>(size);
        for (int i = 0; i < size; i++) {
            pool.add(adapter.createConnection(url, user, pass));
        }
    }

    public Connection getConnection() throws InterruptedException {
        return pool.take();
    }

    public void releaseConnection(Connection conn) {
        if (conn != null) pool.offer(conn);
    }
}