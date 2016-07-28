package by.gto.btoreport.gui;

import by.gto.jasperprintmysql.Version;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public final class AboutController implements javafx.fxml.Initializable {
    public WebView other;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        other.getEngine().loadContent(String.format(
                "<html>" +
                        "<div align='center'><b>Статистические отчеты</b></div>" +
                        "<div align='center'><b>УП «Белтехосмотр»</b></div>" +
                        "<div align='center'>Версия сборки: %s.0 от %s</div>" +
                        "<div align='center'>Автор: Коско Александр Николаевич<br/>" +
                        "Должность: начальник информационно-аналитического отдела<br/>" +
                        "Если у Вас возникнут любые замечания, предложения или вопросы,<br/>" +
                        "пишите на <a href='mailto:day_anger@gto.by'>day_anger@gto.by</a></div>" +
                        "" +
                        "</html>", Version.getVERSION(), Version.getDATEBUILD()));


    }
}
