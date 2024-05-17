module org.example.paint {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires java.desktop;


    opens org.example.paint to javafx.fxml;
    exports org.example.paint;
}