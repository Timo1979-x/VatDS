package by.gto.vatds.gui;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public final class MessageBoxController {
    @FXML
    public WebView webView;

    public void loadContent(String content) {
        final WebEngine engine = webView.getEngine();
        engine.loadContent(content);
    }

}
