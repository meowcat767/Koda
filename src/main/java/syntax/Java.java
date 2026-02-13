package syntax;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Java {

        // ✅ constants at class level
        public static final String[] KEYWORDS = new String[] {
                "class", "public", "static", "void", "int", "String", "if", "else", "return"
        };

        public static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
        public static final String NUMBER_PATTERN = "\\b\\d+\\b";
        public static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";

        public static final Pattern PATTERN = Pattern.compile(
                "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                        + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
                        + "|(?<STRING>" + STRING_PATTERN + ")"
        );

        // method to compute highlighting
        public StyleSpans<Collection<String>> computeHighlighting(String text) {
                Matcher matcher = PATTERN.matcher(text);
                int lastKwEnd = 0;
                StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

                while (matcher.find()) {
                        String styleClass =
                                matcher.group("KEYWORD") != null ? "keyword" :
                                        matcher.group("NUMBER") != null ? "number" :
                                                matcher.group("STRING") != null ? "string" :
                                                        null;
                        spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
                        spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                        lastKwEnd = matcher.end();
                }

                spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
                return spansBuilder.create();
        }
}
