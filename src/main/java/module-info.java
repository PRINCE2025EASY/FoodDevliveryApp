module com.example.onlinefooddeliverysystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.example.onlinefooddeliverysystem to javafx.fxml;
    // javafx.graphics needed to launch these Application subclasses;
    // javafx.base needed for PropertyValueFactory's reflective access to
    // Order's getters when populating the AdminDashboard TableView.
    opens com.delivery to javafx.graphics, javafx.base;
    exports com.example.onlinefooddeliverysystem;
}