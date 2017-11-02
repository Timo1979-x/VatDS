package by.gto.vatds.gui;

import by.gto.controllers.BaseController;
import by.gto.model.ApplicationInfo;
import javafx.scene.web.WebView;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

public final class AboutController extends BaseController<Void, Void> implements javafx.fxml.Initializable {

    public WebView other;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ApplicationInfo ai = ApplicationInfo.getInstance();

        other.getEngine().loadContent(String.format(
                "<html>" +
                        "<div align='center'><b>%s</b></div>" +
                        "<div align='center'><b>%s</b></div>" +
                        "<div align='center'>Версия %s от %s</div>" +
                        "<div align='center'>Автор: Лукашевич Тимофей Викентьевич<br/>" +
                        "Ведущий специалист информационно-аналитического отдела<br/>" +
                        "Если у Вас возникнут замечания, предложения или вопросы,<br/>" +
                        "пишите на <a href='mailto:ltv@gto.by'>ltv@gto.by</a></div>" +
                        "</html>", ai.getDescriptiveName(), ai.getVendor(), ai.getVersion(), ai.getBuildDate()));
    }
}
