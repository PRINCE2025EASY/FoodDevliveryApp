module com.example.onlinefooddeliverysystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.example.onlinefooddeliverysystem to javafx.fxml;
    opens com.delivery to javafx.graphics;
    exports com.example.onlinefooddeliverysystem;
}