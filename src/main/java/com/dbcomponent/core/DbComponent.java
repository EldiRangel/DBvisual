package com.dbcomponent.core;

import com.dbcomponent.adapter.IAdapter;
import com.dbcomponent.pool.CustomPool;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class DbComponent {
    private final CustomPool pool;
    private final Properties queries = new Properties();

    public DbComponent(IAdapter adapter, String url, String user, String pass, int poolSize, String queriesFile) throws Exception {
        this.pool = new CustomPool(adapter, url, user, pass, poolSize);
        
        // Lee el archivo de propiedades desde la carpeta resources
        try (InputStream is = getClass().getResourceAsStream("/" + queriesFile)) {
            if (is == null) throw new RuntimeException("No se encontró: " + queriesFile);
            queries.load(is);
        }
    }

    public void query(String queryName, Object... params) throws Exception {
        String sql = queries.getProperty(queryName);
        if (sql == null) throw new Exception("Query no existe en el properties: " + queryName);

        Connection conn = null;
        try {
            conn = pool.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                stmt.execute();
            }
        } finally {
            pool.releaseConnection(conn);
        }
    }

    public void transaction(TransactionAction action) throws Exception {
        Connection conn = null;
        try {
            conn = pool.getConnection();
            conn.setAutoCommit(false);
            
            action.execute(conn); // Ejecuta el bloque del usuario
            
            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw new Exception("Rollback ejecutado por error: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                pool.releaseConnection(conn);
            }
        }
    }

    public interface TransactionAction {
        void execute(Connection conn) throws SQLException;
    }
}