package com.dbcomponent;

import com.dbcomponent.adapter.*;
import com.dbcomponent.core.DbComponent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.PreparedStatement;

public class PrimaryController {
    @FXML private ComboBox<String> dbSelector;
    @FXML private TextArea logArea;
    private DbComponent db;

    @FXML
    public void initialize() {
        dbSelector.getItems().addAll("PostgreSQL", "MySQL");
        dbSelector.getSelectionModel().selectFirst();
        logArea.appendText("Sistema Listo. Desacoplamiento Activo.\n");
    }

    private void initComponent() throws Exception {
        IAdapter adapter;
        String url;
        String user;
        String pass;

        String seleccion = dbSelector.getValue();

        if ("PostgreSQL".equals(seleccion)) {
            adapter = new PostgresAdapter();
            url = "jdbc:postgresql://localhost:5432/pool_conexiones?sslmode=disable";
            user = "postgres";
            pass = "123456789";
        } else {
            adapter = new MySQLAdapter();
            url = "jdbc:mysql://localhost:3306/pool_conexiones";
            user = "root";
            pass = "";
        }

        db = new DbComponent(adapter, url, user, pass, 5, "queries.properties");
    }

    @FXML
    private void handleQuery() {
        try {
            initComponent();
            db.query("insertar_prueba", "Alumno " + System.currentTimeMillis());
            logArea.appendText(" Query Inserción exitosa usando " + dbSelector.getValue() + "\n");
        } catch (Exception e) {
            logArea.appendText(" [Error Query] " + e.getMessage() + "\n");
        }
    }

    @FXML
    private void handleTransaction() {
        try {
            initComponent();
            db.transaction(conn -> {
                logArea.appendText("Iniciando Transacción...\n");
                try (PreparedStatement st = conn.prepareStatement("INSERT INTO alumnos (nombre) VALUES ('TX JavaFX')")) {
                    st.executeUpdate();
                }
                // Si pones un error intencional aquí, verás cómo hace Rollback automáticamente
            });
            logArea.appendText("Transacción Completada y guardada.\n");
        } catch (Exception e) {
            logArea.appendText("No completada " + e.getMessage() + "\n");
        }
    }
}