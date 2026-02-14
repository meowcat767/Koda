package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import syntax.Java;
import org.fxmisc.richtext.LineNumberFactory;

import java.util.Collection;

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
                    autoOutdent(codeArea);
                }
            }
        });
        Java jsyntax = new Java();
        codeArea.textProperty().addListener((obs, oldText, newText) -> {
            var spans = jsyntax.computeHighlighting(newText);
            codeArea.setStyleSpans(0, addDefaultStyle(spans));
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

    private void autoOutdent(CodeArea area) {
        int paragraph = area.getCurrentParagraph();
        int caret = area.getCaretPosition();
        String line = area.getParagraph(paragraph).getText();
        if(!line.trim().isEmpty()) {
            area.insertText(caret, "");
            return;
        }
        int leading = countLeadingSpaces(line);
        int remove = Math.min(4, leading);
        int lineStart = area.getAbsolutePosition(paragraph, 0);
        if (remove > 0) {
            area.deleteText(lineStart, lineStart + remove);
            caret -= remove;
        }
    }

    private int countLeadingSpaces(String s) {
        int i = 0;
        while (i < s.length() && s.charAt(i) == ' ') i++;
        return i;
    }

    private StyleSpans<Collection<String>> addDefaultStyle(
            StyleSpans<Collection<String>> original) {

        StyleSpansBuilder<Collection<String>> builder =
                new StyleSpansBuilder<>();

        original.forEach(span -> {
            var styles = span.getStyle().isEmpty()
                    ? java.util.List.of("plain")
                    : span.getStyle();

            builder.add(styles, span.getLength());
        });

        return builder.create();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
