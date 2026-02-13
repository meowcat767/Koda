package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import syntax.Java;
import org.fxmisc.richtext.LineNumberFactory;

public class EditorApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        CodeArea codeArea = new CodeArea();
        codeArea.getStyleClass().add("codeArea");
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        VirtualizedScrollPane<CodeArea> scrollPane = new VirtualizedScrollPane<>(codeArea);
        codeArea.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String ch = event.getCharacter();
            if (ch.equals("\r") || ch.equals("\n")) {
                int pos = codeArea.getCaretPosition();
                int currentParagraph = codeArea.getCurrentParagraph();
                String textBeforeCaret = codeArea.getParagraph(currentParagraph).getText().substring(0, codeArea.getCaretColumn());

                StringBuilder indent = new StringBuilder();
                for (char c : textBeforeCaret.toCharArray()) {
                    if (Character.isWhitespace(c)) {
                        indent.append(c);
                    } else {
                        break;
                    }
                }

                boolean extraIndent = textBeforeCaret.trim().endsWith("{");
                String textAfterCaret = codeArea.getParagraph(currentParagraph).getText().substring(codeArea.getCaretColumn());
                boolean betweenBraces = extraIndent && textAfterCaret.trim().startsWith("}");

                if (betweenBraces) {
                    codeArea.insertText(pos, "\n" + indent.toString() + "    \n" + indent.toString());
                    codeArea.moveTo(pos + indent.length() + 5);
                } else {
                    String newIndent = indent.toString() + (extraIndent ? "    " : "");
                    codeArea.insertText(pos, "\n" + newIndent);
                }
                event.consume();
                return;
            }
            switch (ch) {
                case "(" -> {
                    pair(codeArea, "(", ")");
                    event.consume();
                }
                case "[" -> {
                    pair(codeArea, "[", "]");
                    event.consume();
                }
                case "{" -> {
                    pair(codeArea, "{", "}");
                    event.consume();
                }
            }
        });
        Java jsyntax = new Java();
        codeArea.textProperty().addListener((obs, oldText, newText) -> {
            codeArea.setStyleSpans(0, jsyntax.computeHighlighting(newText));
        });

        Scene scene = new Scene(scrollPane, 800, 600);
        var resource = EditorApp.class.getResource("/styles.css");
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        } else {
            System.err.println("Warning: styles.css not found");
        }
        stage.setTitle("Koda");
        stage.setScene(scene);
        stage.setMaximized(true);
        System.out.println("[DEBUG] Showing stage...");
        stage.show();
        System.out.println("[DEBUG] Stage shown.");
    }

    private void pair(CodeArea area, String open, String close) {
        int pos = area.getCaretPosition();
        area.insertText(pos, open + close);
        area.moveTo(pos + 1);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
