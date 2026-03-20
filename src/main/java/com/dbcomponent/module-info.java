module com.dbcomponent {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.dbcomponent to javafx.fxml;
    exports com.dbcomponent;
    exports com.dbcomponent.adapter;
    exports com.dbcomponent.core;
    exports com.dbcomponent.pool;
}