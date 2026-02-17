package ui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class AppMenuBar {

    public static MenuBar create() {
        MenuBar bar = new MenuBar();

        Menu file = new Menu("File");
        MenuItem exit = new MenuItem("Exit");

        file.getItems().add(exit);
        bar.getMenus().add(file);

        return bar;
    }
}
