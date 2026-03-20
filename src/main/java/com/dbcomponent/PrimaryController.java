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
        IAdapter adapter = dbSelector.getValue().equals("PostgreSQL") ? new PostgresAdapter() : new MySQLAdapter();
        //Pon aquí el nombre de la base de datos y la contraseña luis
        db = new DbComponent(adapter, "jdbc:postgresql://localhost:5432/pool_conexiones?sslmode=disable", 
                             "postgres", "123456789", 5, "queries.properties");
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
                try (PreparedStatement st = conn.prepareStatement("INSERT INTO prueba (nombre) VALUES ('TX JavaFX')")) {
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