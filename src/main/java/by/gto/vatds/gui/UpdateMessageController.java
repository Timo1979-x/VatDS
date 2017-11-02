package by.gto.vatds.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public final class UpdateMessageController {
    private final static Logger log = Logger.getLogger(UpdateMessageController.class);
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
            } catch (URISyntaxException | IOException ex) {
                log.error(ex.getMessage(), ex);
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
