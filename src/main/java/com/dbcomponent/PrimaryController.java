package com.dbcomponent;

import com.dbcomponent.adapter.IAdapter;
import com.dbcomponent.adapter.MySQLAdapter;
import com.dbcomponent.adapter.PostgresAdapter;
import com.dbcomponent.core.DbComponent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class PrimaryController {
    @FXML private ComboBox<String> adapterCombo;
    @FXML private TextArea logArea;
    private DbComponent db;

    @FXML
    public void initialize() {
        adapterCombo.getItems().addAll("PostgreSQL", "MySQL");
        adapterCombo.getSelectionModel().selectFirst();
    }

    private void initDB() throws Exception {
        IAdapter adapter;
        String url, user, pass;

        if (adapterCombo.getValue().equals("PostgreSQL")) {
            adapter = new PostgresAdapter();
            url = "jdbc:postgresql://localhost:5432/tu_base"; // Cambia esto
            user = "postgres"; pass = "tu_clave";
        } else {
            adapter = new MySQLAdapter();
            url = "jdbc:mysql://localhost:3306/tu_base"; // Cambia esto
            user = "root"; pass = "tu_clave";
        }

       // Puede ser queries.json, queries.yaml.
        db = new DbComponent(adapter, url, user, pass, 5, "queries.json"); 
    }

 
    @FXML
    private void handleQuery() { 
        try {
            initDB();
            db.query("insert_test", "Prueba Query Normal");
            logArea.appendText("Query Normal Exitosa.\n");
        } catch (Exception e) {
            logArea.appendText("Error Query: " + e.getMessage() + "\n");
        }
    }

    
    @FXML
    private void handleTransaction() {
        try {
            initDB();
            db.transaction("insert_test", "Prueba Transaccion");
            logArea.appendText("Transacción Exitosa.\n");
        } catch (Exception e) {
            logArea.appendText("Error Transacción: " + e.getMessage() + "\n");
        }
    }
}