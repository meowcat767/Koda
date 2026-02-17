module koda {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires org.fxmisc.undo;
    requires reactfx;
    requires wellbehavedfx;
    requires java.desktop;

    exports ui;
    exports syntax;

    opens ui to javafx.graphics, javafx.fxml;
}