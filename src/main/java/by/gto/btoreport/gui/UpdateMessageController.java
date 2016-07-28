package by.gto.btoreport.gui;

import by.gto.jasperprintmysql.Version;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public final class UpdateMessageController {
    @FXML
    private WebView webContent;
    private ChangeListener<String> listener = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldLoc, String loc) {
            try {
                Desktop d = Desktop.getDesktop();
                URI address = new URI(loc);
                d.browse(address);
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        engine.load(oldLoc);
//                    }
//                });
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        }
    };

    public void loadContent(String content) {
        final WebEngine engine = webContent.getEngine();
        engine.locationProperty().removeListener(listener);
        engine.loadContent(content);
        engine.locationProperty().addListener(listener);
    }
}
