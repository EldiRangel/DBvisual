module com.dbcomponent {
   
   requires javafx.controls;
   requires javafx.fxml;
   requires java.sql;
   requires org.postgresql.jdbc;
   requires mysql.connector.j;
   requires com.fasterxml.jackson.databind; 
   requires org.yaml.snakeyaml;            
   requires toml4j;                        
opens com.dbcomponent to javafx.fxml;
    
    
    exports com.dbcomponent;
    exports com.dbcomponent.core;
    exports com.dbcomponent.adapter;
}