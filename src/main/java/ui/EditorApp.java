package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import syntax.Java;

public class EditorApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        CodeArea codeArea = new CodeArea();
        Java jsyntax = new Java();
        codeArea.textProperty().addListener((obs, oldText, newText) -> {
            codeArea.setStyleSpans(0, jsyntax.computeHighlighting(newText));
        });

        Scene scene = new Scene(codeArea, 800, 600);
        var resource = getClass().getResource("/styles.css");
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        } else {
            System.err.println("Warning: styles.css not found");
        }
        stage.setTitle("Koda");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
