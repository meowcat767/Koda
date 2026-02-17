package ui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;

public class TopBar extends MenuBar {

    public TopBar(Stage stage, CodeArea editor) {

        Menu fileMenu = new Menu("File");

        MenuItem open = new MenuItem("Open");
        MenuItem save = new MenuItem("Save");

        fileMenu.getItems().addAll(open, save);

        // add menu to THIS menu bar
        getMenus().add(fileMenu);
    }
}
