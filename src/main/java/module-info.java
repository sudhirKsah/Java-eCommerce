module com.ecommerce {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires io.github.cdimascio.dotenv.java;

    opens com.ecommerce to javafx.fxml;
    opens com.ecommerce.controllers to javafx.fxml;
    opens com.ecommerce.models to javafx.base;
    exports com.ecommerce.controllers;
    exports com.ecommerce.models;
    exports com.ecommerce;
}
