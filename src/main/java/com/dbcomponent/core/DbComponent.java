package com.dbcomponent.core;

import com.dbcomponent.adapter.IAdapter;
import com.dbcomponent.pool.CustomPool;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;
import com.moandjiezana.toml.Toml;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DbComponent {
    private CustomPool pool;
    private Map<String, String> queries = new HashMap<>(); 

    public DbComponent(IAdapter adapter, String url, String user, String pass, int poolSize, String queryFile) throws Exception {
        this.pool = new CustomPool(adapter, url, user, pass, poolSize);
        loadQueries(queryFile); // Carga según la extensión
    }

    private void loadQueries(String file) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(file);
        if (is == null) throw new IllegalArgumentException("No se encontró el archivo: " + file);

        if (file.endsWith(".json")) {
            ObjectMapper mapper = new ObjectMapper();
            queries = mapper.readValue(is, new TypeReference<Map<String, String>>() {});
        } else if (file.endsWith(".yaml") || file.endsWith(".yml")) {
            Yaml yaml = new Yaml();
            queries = yaml.load(is);
        } else if (file.endsWith(".toml")) {
            Toml toml = new Toml().read(is);
            toml.toMap().forEach((k, v) -> queries.put(k, v.toString()));
        } else { // Por defecto asume .properties
            Properties props = new Properties();
            props.load(is);
            for (String key : props.stringPropertyNames()) queries.put(key, props.getProperty(key));
        }
    }
    
    
    private String getSql(String queryKey) {
        String sql = queries.get(queryKey);
        if (sql == null) throw new IllegalArgumentException("Query no encontrada para la llave: " + queryKey);
        return sql;
    }

    public void query(String queryKey, Object... params) throws Exception {
        Connection conn = pool.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(getSql(queryKey))) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            ps.executeUpdate();
        } finally {
            pool.releaseConnection(conn);
        }
    }

    public void transaction(String queryKey, Object... params) throws Exception {
        Connection conn = pool.getConnection();
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(getSql(queryKey));
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            ps.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            pool.releaseConnection(conn);
        }
    }
}