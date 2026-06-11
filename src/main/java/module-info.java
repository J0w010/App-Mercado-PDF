module com.example.mercadopdf {
    requires javafx.controls;
    requires javafx.fxml;
    requires kernel;
    requires layout;
    requires io;


    opens com.example.mercadopdf to javafx.fxml;
    exports com.example.mercadopdf;
}