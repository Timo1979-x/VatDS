package by.gto.helpers

import by.gto.controllers.BaseController
import by.gto.vatds.gui.Main
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.IOException


object FXHelpers {
    fun showErrorMessage(title: String?, message: String?) {
        val a = Alert(Alert.AlertType.ERROR, message, ButtonType.CLOSE)
        a.title = title
        a.headerText = null
        a.showAndWait()
    }

    fun showErrorMessage(message: String?) {
        showErrorMessage("Ошибка", message)
    }

    fun showInfoMessage(message: String?) {
        showInfoMessage("Info", message)
    }
    fun showInfoMessage(title: String, message: String?) {
        val a = Alert(Alert.AlertType.INFORMATION, message, ButtonType.CLOSE)
        a.title = title
        a.headerText = null
        a.showAndWait()
    }

//    fun showLargeMessageBox(htmlContent: String?) {
//        showLargeMessageBox("", htmlContent)
//    }
//
//    fun showLargeMessageBox(title: String, htmlContent: String?) {
//        val newStage = Stage()
//        val loader = FXMLLoader(javaClass.classLoader.getResource("fxml/messageBox.fxml"))
//        try {
//            val root: Parent = loader.load()
//            var controller: MessageBoxController = loader.getController()
//            newStage.initModality(Modality.APPLICATION_MODAL)
//            newStage.setTitle(title)
//            val scene = Scene(root, 800.0, 600.0)
//            newStage.setScene(scene)
//            newStage.setResizable(true)
//            controller = loader.getController() // ??
//
//            controller.loadContent(htmlContent)
//
//            val i = Image(javaClass.classLoader.getResourceAsStream("mainIcon.png"))
//            newStage.getIcons().add(i)
//            newStage.show()
//        } catch (t: Throwable) {
//            t.printStackTrace()
//        }
//
//    }

    @Throws(IOException::class)
    fun <T, R> openChildWindow(main: T, fxml: String, icon: String?, title: String): R {
        val loader = FXMLLoader(FXHelpers.javaClass.getClassLoader().getResource(fxml))
        val pane: AnchorPane = loader.load()
        val ctrl: BaseController<T, R> = loader.getController()
        val s = Stage()
        if (icon!=null) {
            val i = Image(FXHelpers.javaClass.classLoader.getResourceAsStream(icon))
            s.icons.add(i);
        }
        ctrl.setInitialData(null, s)
        val scene = Scene(pane)
        s.initModality(Modality.APPLICATION_MODAL)

//        val resource = Main::class.java!!.getClassLoader().getResource("style.css")!!.toExternalForm()
//        scene.stylesheets.add(resource)

        s.title = title
        s.scene = scene
        s.showAndWait()
        return ctrl.result;
    }
}
