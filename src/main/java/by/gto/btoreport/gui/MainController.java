package by.gto.btoreport.gui;

import by.avest.crypto.ocsp.client.protocol.util.*;
import by.avest.crypto.ocsp.client.protocol.util.Base64;
import by.gto.helpers.VatHelpers;
import by.gto.jasperprintmysql.App;
import by.gto.jasperprintmysql.Version;
import by.gto.jasperprintmysql.data.OwnerDataSW2;
import by.gto.tools.ConfigReader;
import by.gto.tools.ConnectionMySql;
import by.gto.tools.Util;
import by.gto.tools.VatTool;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("SqlDialectInspection")
public class MainController implements Initializable {

    private static final Logger log = Logger.getLogger(MainController.class);

    @FXML
    public CheckBox cbPeriod;
    @FXML
    public Label lOver;
    @FXML
    public DatePicker dtpEnd;
    @FXML
    public DatePicker dtpStart;
    @FXML
    public Label lBefore;
    @FXML
    public CheckBox cbCorporate;
    @FXML
    public CheckBox cbIndividual;
    @FXML
    public ComboBox comboBoxOwner;
    @FXML
    public ComboBox comboBoxUNP;
    @FXML
    public Label lUNP;
    @FXML
    public CheckBox cbOwner;
    @FXML
    public Button bShowReport;
    @FXML
    public ComboBox comboBoxYear;
    @FXML
    public ComboBox comboBoxMonth;
    @FXML
    public TableColumn colContractorName;
    @FXML
    public TableColumn colContractorUNP;
    @FXML
    public TableColumn colDate;
    @FXML
    public TableColumn colWithoutVAT;
    @FXML
    public TableColumn colVAT;
    @FXML
    public TableColumn colWithVAT;
    @FXML
    public TableView vatTableView;
    @FXML
    public TableColumn colVATFullNumber;
    @FXML
    public Label lMessage;
    @FXML
    public TableColumn colBlankSeries;
    @FXML
    public TableColumn colBlankNumber;

    private String report = "recordBook";
    private byte bankTransfer = 2;

    private ObservableList<VatData> vatData = FXCollections.observableArrayList();
    private ObservableList<Integer> years = FXCollections.observableArrayList(
            2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2018, 2019,
            2020, 2021, 2022, 2024, 2024, 2025, 2026, 2027, 2028, 2029,
            2030, 2031
    );
    private int selectedYear;
    private int selectedMonth;

    private ObservableList<String> months = FXCollections.observableArrayList(
            "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    );

    public static void showErrorMessage(String title, String message) {
        Alert a = new Alert(Alert.AlertType.ERROR, message, ButtonType.CLOSE);
        a.setTitle(title);
        a.setHeaderText(null);
        a.showAndWait();
    }

    public static void showInfoMessage(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.CLOSE);
        a.setTitle(title);
        a.setHeaderText(null);
        a.showAndWait();
    }

    public static String passwordPrompt(String title, int minLength) throws IOException {
        Stage newStage = new Stage();
        FXMLLoader loader = new FXMLLoader(MainController.class.getClassLoader().getResource("fxml/passwordPrompt.fxml"));
        try {
            Parent root = loader.load();

            PasswordPromptController controller = loader.<PasswordPromptController>getController();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setTitle(title);
            newStage.setScene(new Scene(root));
            newStage.setResizable(false);
            controller = (PasswordPromptController) loader.getController();
            controller.setCaption(title);
            String result;
            do {
                newStage.showAndWait();
                result = controller.getResult();
                if (null != result && result.length() < minLength) {
                    controller.setMessage("Минимальная длина пароля " + minLength + " символов");
                } else {
                    break;
                }
            } while (true);
            return result;

        } catch (Exception e) {
            if (Main.verbose) {
                log.error(e.getMessage(), e);
            }
            if (Main.debug) {
                throw e;
            }
            return null;
        }
    }

    public static Object[] chooseFromList(String title, String[] items) throws IOException {
        Stage newStage = new Stage();
        FXMLLoader loader = new FXMLLoader(MainController.class.getClassLoader().getResource("fxml/chooseFromList.fxml"));
        Object[] result = new Object[]{-1, null};
        try {
            Parent root = loader.load();

            ChooseFromListController controller = loader.<ChooseFromListController>getController();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setTitle(title);
            newStage.setScene(new Scene(root));
            newStage.setResizable(true);
            controller = loader.getController();
            controller.setListItems(items);
            final String pass = System.getProperty("by.gto.btoreport.avest.password");
            if (pass != null) {
                controller.setPassword(pass);
            }
            log.info("chooseFromList: before newStage.showAndWait();");
            newStage.showAndWait();
            log.info("chooseFromList: after newStage.showAndWait();");
            result[0] = controller.getKeyIndex();
            result[1] = controller.getPassword();
        } catch (IOException e) {
            if (Main.verbose) {
                log.error(e.getMessage(), e);
            }
            if (Main.debug) {
                throw e;
            }
        }
        return result;
    }

    public void miAboutClick(ActionEvent actionEvent) throws IOException {
        Stage newStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(Main.class.getClassLoader().getResource("fxml/about.fxml"));
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle("О программе");
        newStage.setScene(new Scene(root));
        newStage.setResizable(false);
        Image i = new Image(Main.class.getClassLoader().getResourceAsStream("piggy-bank-icon.png"));
        newStage.getIcons().add(i);
        newStage.show();
    }

    public void miSettingsClick(ActionEvent actionEvent) throws IOException {
        Stage newStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(Main.class.getClassLoader().getResource("fxml/settings.fxml"));
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle("Настройки");
        newStage.setScene(new Scene(root));
        newStage.setResizable(false);
        Image i = new Image(Main.class.getClassLoader().getResourceAsStream("piggy-bank-icon.png"));
        newStage.getIcons().add(i);
        newStage.show();
    }

    public void miQuitClick(ActionEvent actionEvent) {

    }

    public void miCheckUpdatesClick(ActionEvent actionEvent) {
        CheckUpdate(true);
    }

    private void CheckUpdate(boolean showMessage) {
        try {
            URL url = new URL("http://gto.by/api/check.updates.php?name=btoReportNG");
            ConfigReader config = ConfigReader.getInstance();
            InputStream is = null;
            if(config.isUseProxy()) {
                final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.getProxyHost(), config.getProxyPort()));
                is = url.openConnection(proxy).getInputStream();
            } else {
                is = url.openStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String strTemp = "";
            while (null != (strTemp = br.readLine())) {
                JSONObject obj = new JSONObject(strTemp);
                String name = obj.getString("name");
                String version = obj.getString("version");
                String url_ = obj.getString("url");
                int verCompere = Util.versionCompare(Version.getVERSION(), version);
                if (verCompere < 0) {
                    Stage newStage = new Stage();
                    FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("fxml/UpdateMessage.fxml"));
                    Parent root = loader.load();

                    UpdateMessageController controller = loader.<UpdateMessageController>getController();
                    newStage.initModality(Modality.APPLICATION_MODAL);
                    newStage.setTitle("Новая версия");
                    newStage.setScene(new Scene(root));
                    newStage.setResizable(false);
//                    Image i = new Image(Main.class.getClassLoader().getResourceAsStream("piggy-bank-icon.png"));
//                    newStage.getIcons().add(i);
                    controller = (UpdateMessageController) loader.getController();
                    controller.loadContent("<html><body>Доступно обновление для  "
                            + name + ".<br />Установлена версия: "
                            + Version.getVERSION() + "<br />Версия обновления: "
                            + version + "<br /><a href=\"" + url_
                            + "\" target=\"_blank\"><strong>Загрузить</strong></a></body></html>");
                    newStage.show();


                } else if (showMessage) {
                    showInfoMessage("", "Установлена последняя версия btoReportNG");
                }
                log.info(strTemp);
            }
        } catch (Exception ex) {
            if (showMessage) {
                showErrorMessage("", "Ошибка проверки версии");
            }

            log.error(ex.getMessage(), ex);
        }
    }

    public void cbPeriodAction(ActionEvent actionEvent) {
        if (cbPeriod.isSelected()) {
            lOver.setText("c");
            dtpEnd.setDisable(false);
            lBefore.setDisable(false);

        } else {
            lOver.setText("за");
            dtpEnd.setDisable(true);
            lBefore.setDisable(true);
        }
    }

    public void rbForBtoAction(ActionEvent actionEvent) {
        cbCorporate.setSelected(true);
        cbIndividual.setSelected(true);
        report = "forBTO";
    }

    public void rbForSlutskAction(ActionEvent actionEvent) {
        cbCorporate.setSelected(true);
        cbIndividual.setSelected(true);
        report = "forSlutsk";
    }

    public void rbOrderbyTariffAction(ActionEvent actionEvent) {
        report = "OrderByTariff";
        cbCorporate.setSelected(true);
        cbIndividual.setSelected(true);
    }

    public void rbRecordBookAction(ActionEvent actionEvent) {
        cbCorporate.setSelected(true);
        cbIndividual.setSelected(true);
        report = "recordBook";
    }

    public void rbIndividualAction(ActionEvent actionEvent) {
        cbCorporate.setSelected(false);
        cbIndividual.setSelected(true);
        report = "listIndividual";

    }

    public void rbCorporateAction(ActionEvent actionEvent) {
        cbIndividual.setSelected(false);
        cbCorporate.setSelected(true);
        report = "corporatePerson";

    }

    public void rbActiveAction(ActionEvent actionEvent) {
        report = "forDS210";
        cbCorporate.setSelected(true);
        cbIndividual.setSelected(true);

    }

    public void rbBankTransferAllAction(ActionEvent actionEvent) {
        bankTransfer = 2;
    }

    public void rbBankTransferFalseAction(ActionEvent actionEvent) {
        bankTransfer = 0;
    }

    public void rbBankTransferTrueAction(ActionEvent actionEvent) {
        bankTransfer = 1;
    }

    public void cbOwnerAction(ActionEvent actionEvent) {
        OwnerDataSW2 ownerDataSW2 = new OwnerDataSW2(comboBoxOwner, comboBoxUNP, cbOwner.isSelected(), lUNP);
        ownerDataSW2.execute();
    }

    public void bShowReportClick(ActionEvent actionEvent) {
        bShowReport.setDisable(true);

        List<Integer> ownerType = new ArrayList<>();
        if (cbCorporate.isSelected()) {
            ownerType.add(2);
            ownerType.add(3);
        }
        if (cbIndividual.isSelected()) {
            ownerType.add(1);
        }
        if (ownerType.isEmpty()) {
            ownerType.add(1);
            ownerType.add(2);
            ownerType.add(3);
        }
        String owner = null;
        String ownerUNP = null;
        if (cbOwner.isSelected()) {
            owner = (String) comboBoxOwner.getSelectionModel().getSelectedItem();
            if (!StringUtils.isEmpty(owner)) {
                owner = owner.trim().replaceAll("(^.*\")(.+)(\".*$)", "$2");
                owner = owner.replaceAll("\"", "_");
                owner = owner.replaceAll("\\s+", "%");
                log.info(String.format("----------owner--------%s", owner));
            }
            ownerUNP = (String) comboBoxUNP.getSelectionModel().getSelectedItem();
            if (!StringUtils.isEmpty(ownerUNP)) {
                ownerUNP = ownerUNP.trim();
                log.info(String.format("--------ownerUNP----------%s", ownerUNP));
            }
        }

        LocalDateTime localDateStart = LocalDateTime.of(dtpStart.getValue(), LocalTime.of(0, 0, 0, 0));
        LocalDateTime localDateStop = LocalDateTime.of(dtpEnd.getValue(), LocalTime.of(23, 59, 59, 999999999));
        try {

            if (!dtpEnd.isDisable()) {
                App.print(localDateStart, localDateStop, report, ownerType, owner, ownerUNP, bankTransfer);
            } else {
                App.print(localDateStart, localDateStart, report, ownerType, owner, ownerUNP, bankTransfer);
            }
        } catch (Exception e) {
            log.fatal(e.getMessage());

            showErrorMessage("Ошибка", e.getMessage());
        }
        bShowReport.setDisable(false);
    }

    private ScrollBar findScrollBar(TableView<?> table, Orientation orientation) {

        // this would be the preferred solution, but it doesn't work. it always gives back the vertical scrollbar
        //      return (ScrollBar) table.lookup(".scroll-bar:horizontal");
        //
        // => we have to search all scrollbars and return the one with the proper orientation

        Set<Node> set = table.lookupAll(".scroll-bar");
        for (Node node : set) {
            ScrollBar bar = (ScrollBar) node;
            if (bar.getOrientation() == orientation) {
                return bar;
            }
        }
        return null;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        ScrollBar table1HorizontalScrollBar = findScrollBar( vatTableView, Orientation.HORIZONTAL);
//        ScrollBar table1VerticalScrollBar = findScrollBar( table, Orientation.VERTICAL);
//        if(table1HorizontalScrollBar != null) {
//            table1HorizontalScrollBar.setVisible(true);
//        }
//        if(table1VerticalScrollBar != null) {
//            table1VerticalScrollBar.setVisible(true);
//        }

//        for (Object c : vatTableView.getColumns()) {
//            ((TableColumn)c).setResizable(true);
//            vatTableView.setColumnResizePolicy();
//            System.out.println(c);
//        }
        dtpEnd.setValue(LocalDate.now());
        dtpStart.setValue(LocalDate.now());


        comboBoxMonth.setItems(months);
        comboBoxYear.setItems(years);


        colContractorName.setCellValueFactory(
                new PropertyValueFactory<VatData, String>("contractorName"));
        colVATFullNumber.setCellValueFactory(
                new PropertyValueFactory<VatData, String>("vatFullNumber"));
        colDate.setCellValueFactory(
                new PropertyValueFactory<VatData, String>("date"));
        colContractorUNP.setCellValueFactory(
                new PropertyValueFactory<VatData, Integer>("contractorUnp"));
        colWithoutVAT.setCellValueFactory(
                new PropertyValueFactory<VatData, String>("withoutVAT"));
        colWithVAT.setCellValueFactory(
                new PropertyValueFactory<VatData, String>("withVAT"));
        colVAT.setCellValueFactory(
                new PropertyValueFactory<VatData, String>("VAT"));
        colBlankSeries.setCellValueFactory(
                new PropertyValueFactory<VatData, String>("blankSeries"));
        colBlankNumber.setCellValueFactory(
                new PropertyValueFactory<VatData, String>("blankNumber"));

//        vatData.add(new VatData(1, 1, (short) 200, 10l, new Date(), 123123123, " Рога и",
//                new BigDecimal("10.1"), new BigDecimal("12.2"), new BigDecimal("2.05")));
//        vatData.add(new VatData(1, 1, (short) 200, 10l, new Date(), 123123123, " Рога и хвосты",
//                new BigDecimal("10.1"), new BigDecimal("12.2"), new BigDecimal("2.05")));
        vatTableView.setItems(vatData);
        vatTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        vatTableView.setItems(vatData);


        // загрузим данные за прошлый месяц
        Date d = new Date();
        int y = d.getYear() + 1900;
        int m = d.getMonth();


        comboBoxMonth.getSelectionModel().select(m);
        comboBoxYear.getSelectionModel().select(years.indexOf(y));
        try {
            refreshVats();
        } catch (Exception e) {
        }

//        comboBoxOwner.setCellFactory(
//                new Callback<ListView<String>, ListCell<String>>() {
//                    @Override
//                    public ListCell<String> call(ListView<String> param) {
//                        final ListCell<String> cell = new ListCell<String>() {
//                            {
//                                super.setPrefWidth(100);
//                            }
//
//                            @Override
//                            public void updateItem(String item,
//                                                   boolean empty) {
//                                super.updateItem(item, empty);
//                                if (item != null) {
//                                    setText(item);
//                                    if (item.contains("КОСКО")) {
//                                        empty = true;
//                                        setTextFill(Color.RED);
//                                    } else if (item.contains("ОСИПОВИ")) {
//                                        setTextFill(Color.GREEN);
//                                    } else {
//                                        setTextFill(Color.BLACK);
//                                    }
//                                } else {
//                                    setText(null);
//                                }
//                            }
//                        };
//                        return cell;
//                    }
//                });

        new AutoCompleteComboBoxListener(comboBoxOwner);
        //new ComboBoxAutoComplete<String>(comboBoxOwner);
    }

    public void miVATSettingsAction(ActionEvent actionEvent) throws IOException {
        Stage newStage = new Stage();
        FXMLLoader loader = new FXMLLoader(MainController.class.getClassLoader().getResource("fxml/settingsVAT.fxml"));
        Parent root = loader.load();
        final SettingsVATController controller = loader.<SettingsVATController>getController();
        //controller = (SettingsVATController) loader.getController();
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setTitle("Настройка диапазонов номеров ЭСЧФ");
        newStage.setScene(new Scene(root));
        newStage.setResizable(false);
        Image i = new Image(Main.class.getClassLoader().getResourceAsStream("piggy-bank-icon.png"));
        newStage.getIcons().add(i);
        newStage.show();
        newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (controller.isModified()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Подтвердите");
                    alert.setHeaderText("Подтвердите");
                    alert.setContentText("Настройки были изменены. Действительно хотите закрыть окно без сохранения настроек?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() != ButtonType.OK) {
                        event.consume();
                    }
                }
            }
        });
    }

    private void refreshVats() {
        int idxYear = comboBoxYear.getSelectionModel().getSelectedIndex();
        int idxMonth = comboBoxMonth.getSelectionModel().getSelectedIndex();
        if (idxYear < 0 || idxMonth < 0) {
            return;
        }
        selectedMonth = idxMonth + 1;
        selectedYear = years.get(idxYear);

        String query = "SELECT\n" +
                "  bti.id_blanc_ts_info bti_id,\n" +
                "  vats.id vats_id,\n" +
                "  vats.unp v_unp,\n" +
                "  vats.year v_year,\n" +
                "  vats.number v_number,\n" +
                "  bti.date_ot date1,\n" +
                "  oi.name,\n" +
                "  oi.unp,\n" +
                "  stti.summa_no_tax withoutVAT,\n" +
                "  stti.summa_oplaty withVAT,\n" +
                "  stti.summa_oplaty - stti.summa_no_tax VAT,\n" +
                "  b.seria blankSeries,\n" +
                "  b.number blankNumber\n" +
                "FROM ei_vats vats\n" +
                "  RIGHT JOIN blanc_ts_info bti\n" +
                "    ON bti.id_blanc_ts_info = vats.id_blank_ts_info\n" +
                "  INNER JOIN ts_info ti\n" +
                "    ON bti.id_ts_info = ti.id_ts_info\n" +
                "\n" +
                "  INNER JOIN owner_info oi\n" +
                "    ON ti.`id_owner_zakazch` = oi.id_owner\n" +
                "  INNER JOIN sd_tarifs_ts_info stti\n" +
                "    ON (ti.id_ts_info = stti.id_ts_info\n" +
                "    AND bti.id_blanc = stti.id_blanc)\n" +
                "  INNER JOIN blanc b\n" +
                "    ON bti.id_blanc = b.id_blanc\n" +
                "\n" +
                "WHERE oi.id_owner_type IN (2, 3)\n" +
                "AND EXTRACT(MONTH FROM bti.date_ot) = ?\n" +
                "AND EXTRACT(year FROM bti.date_ot) = ?\n" +
                "AND b.id_blanc_type = 1\n" +
                "AND b.id_blanc_status = 2";
        try (Connection conn = ConnectionMySql.getInstance().getConn();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, selectedMonth);
            ps.setInt(2, selectedYear);
            try (ResultSet rs = ps.executeQuery()) {
                vatData.clear();
                while (rs.next()) {
                    Integer vatUNP;
                    Integer vatId;
                    Integer blankNumber;

                    Short vatYear;
                    Long vatNumber;
                    int iTemp;

                    iTemp = rs.getInt("v_unp");
                    vatUNP = (rs.wasNull() ? null : iTemp);

                    iTemp = rs.getInt("blankNumber");
                    blankNumber = (rs.wasNull() ? null : iTemp);

                    iTemp = rs.getInt("vats_id");
                    vatId = (rs.wasNull() ? null : iTemp);

                    short sTemp = rs.getShort("v_year");
                    vatYear = (rs.wasNull() ? null : sTemp);

                    long lTemp = rs.getLong("v_number");
                    vatNumber = (rs.wasNull() ? null : lTemp);
                    vatData.add(
                            new VatData(
                                    rs.getInt("bti_id"), vatId,
                                    rs.getString("blankSeries"),
                                    blankNumber,
                                    vatUNP, vatYear,
                                    vatNumber, rs.getDate("date1"),
                                    rs.getInt("unp"), rs.getString("name"),
                                    rs.getBigDecimal("withoutVAT"),
                                    rs.getBigDecimal("withVAT"),
                                    rs.getBigDecimal("VAT")
                            )
                    );
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void comboBoxYearAction(ActionEvent actionEvent) {
        try {
            refreshVats();
        } catch (Exception e) {
            MainController.showErrorMessage("", e.getMessage());
        }

    }

    public void comboBoxMonthAction(ActionEvent actionEvent) {
        try {
            refreshVats();
        } catch (Exception e) {
            MainController.showErrorMessage("", e.getMessage());
        }
    }

    private void issueVATS() {
        final ConfigReader config = ConfigReader.getInstance();
        if (config.isUseProxy()) {
            Authenticator.setDefault(new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getProxyUser(), config.getProxyPass().toCharArray());
                }
            });
            System.setProperty("https.proxyHost", config.getProxyHost());
            System.setProperty("https.proxyPort", String.valueOf(config.getProxyPort()));
            System.setProperty("https.proxyUser", config.getProxyUser());
            System.setProperty("https.proxyPass", config.getProxyPass());
        } else {
            Authenticator.setDefault(new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(null, null);
                }
            });
            System.clearProperty("https.proxyHost");
            System.clearProperty("https.proxyPort");
            System.clearProperty("https.proxyUser");
            System.clearProperty("https.proxyPass");
        }
        String proxyHost = System.getProperty("https.proxyHost");
        String proxyPort = System.getProperty("https.proxyPort");
        String proxyUser = System.getProperty("https.proxyUser");
        String proxyPass = System.getProperty("https.proxyPass");

        if (vatTableView.getSelectionModel().getSelectedIndex() == -1) {
            MainController.showInfoMessage("", "Не выделено ни одной строки");
            return;
        }
        ObservableList<Integer> selectedIndices = vatTableView.getSelectionModel().getSelectedIndices();

        List<VatData> selectedRows = selectedIndices.stream().map(ind -> vatData.get(ind)).collect(Collectors.toList());
        //int year = 1900 + vatData.get((int) selectedIndices.get(0)).get_date().getYear();
        short year = (short) Calendar.getInstance().get(Calendar.YEAR);
        int unp = config.getUNP();
        long numberBegin, numberEnd;
        List<String> numbersUsed = new ArrayList<>();
        String qGetNumberRange = "SELECT ovs.`begin`, ovs.`end` FROM ei_vat_settings ovs WHERE ovs.`year` = ?";
        String qUsedVatNumbers = "SELECT ov.`number` FROM ei_vats ov\n" +
                "  WHERE ov.`number` >= ? AND ov.`number` <= ? AND ov.unp = ?  AND ov.`year` = ?\n" +
                "\n" +
                "  ORDER BY ov.`number`";
        String qIssueVat = "INSERT ei_vats(id_blank_ts_info, unp, `year`, `number`)\n" +
                "  VALUES (?, ?, ?, ?);";
        try (Connection conn = ConnectionMySql.getInstance().getConn();
             PreparedStatement psGetNumberRange = conn.prepareStatement(qGetNumberRange);
             PreparedStatement psUsedVatNumbers = conn.prepareStatement(qUsedVatNumbers);
             PreparedStatement psIssueVat = conn.prepareStatement(qIssueVat);
        ) {
            conn.setAutoCommit(false);
            psGetNumberRange.setInt(1, year);
            try (ResultSet rs = psGetNumberRange.executeQuery()) {
                if (!rs.next()) {
                    throw new Exception("Не заданы номера счетов-фактур на требуемый год");
                } else {
                    numberBegin = rs.getLong(1);
                    numberEnd = rs.getLong(2);
                }
            }

            psUsedVatNumbers.setLong(1, numberBegin);
            psUsedVatNumbers.setLong(2, numberEnd);
            psUsedVatNumbers.setInt(3, unp);
            psUsedVatNumbers.setInt(4, year);
            try (ResultSet rs = psUsedVatNumbers.executeQuery()) {
                while (rs.next()) {
                    numbersUsed.add(VatHelpers.vatNumber(unp, year, rs.getLong(1)));
                }
            }

            // отправка:
            String dir = config.getVatPath();
            new File(dir).mkdirs();

            long counter = numberBegin;
            try (VatTool vt = new VatTool()) {
                final List<String> unps = selectedRows.stream().map(vd -> String.valueOf(vd.getContractorUnp())).distinct().collect(Collectors.toList());
                String unpResult = vt.checkUNPs(unps);
                if (StringUtils.isNotEmpty(unpResult)) {
                    showErrorMessage("Ошибка проверки УНП", unpResult);
                    return;
                }
                for (VatData vd : selectedRows) {
                    String number = null;
                    final boolean issued = vd.isVatIssued();
                    if (!issued) {
                        // подобрать номер из настроенных, но еще не задействованный:
                        for (; counter <= numberEnd; counter++) {
                            number = VatHelpers.vatNumber(unp, year, counter);
                            if (numbersUsed.contains(number)) {
                                continue;
                            }
                            final byte status = vt.isNumberSpare(number);
                            if (status == 3) {
                                throw new Exception("Ошибка получения статуса ЭСЧФ.\n" +
                                        "Проверьте настройки, подключение к\n" +
                                        "интернету и попробуйте позже");
                            }
                            if (status == 2) {
                                // пометим номер как занятый
                                psUsedVatNumbers.setLong(1, counter);
                                psUsedVatNumbers.setLong(2, counter);
                                psUsedVatNumbers.setInt(3, unp);
                                psUsedVatNumbers.setInt(4, year);
                                try (ResultSet rs = psUsedVatNumbers.executeQuery()) {
                                    if (!rs.next()) {
                                        psIssueVat.setNull(1, Types.INTEGER);
                                        psIssueVat.setInt(2, unp);
                                        psIssueVat.setShort(3, year);
                                        psIssueVat.setLong(4, counter);
                                        psIssueVat.executeUpdate();
                                    }
                                }

                                conn.commit();
                            }

                            if (status == 0) {
                                break;
                            }
                        }

                        if (counter > numberEnd) {
                            throw new Exception(String.format("Не осталось свободных настроенных номеров счет-фактур"));
                        }
                        vd.setVatYear(year);
                        vd.setVatUnp(unp);
                        vd.setVatNumber(counter++);

                    }
                    final String vatXml = makeVATXml(vd);
                    if (!issued) {
                        // записать в базу:
                        // INSERT ei_vats(id_blank_ts_info, unp, `year`, `number`)
                        psIssueVat.setInt(1, vd.getBlancTsInfoId());
                        psIssueVat.setInt(2, vd.getVatUnp());
                        psIssueVat.setShort(3, vd.getVatYear());
                        psIssueVat.setLong(4, vd.getVatNumber());
                        psIssueVat.executeUpdate();
                    }
                    vt.doSignAndUploadString(vatXml);
                    try (FileOutputStream fos = new FileOutputStream(dir + File.separator + number + ".xml");
                         OutputStreamWriter fw = new OutputStreamWriter(fos, "UTF-8");
                         BufferedWriter bw = new BufferedWriter(fw)) {
                        bw.write(vatXml, 0, vatXml.length());
                    }
                    conn.commit();
                }
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

            final String resultMessage = "Загрузка на портал ЭСЧФ завершена успешно";
            MainController.showInfoMessage("", resultMessage);
            lMessage.setText(resultMessage);
        } catch (Exception e) {

            MainController.showErrorMessage("", e.getMessage());

            if (Main.verbose) {
                log.error(e.getMessage(), e);
            } else {
                log.error("[ОШИБКА] " + e.getMessage());
            }
            lMessage.setText("Возникла ошибка");
        }
        refreshVats();
    }

//    private void sendVATs(Integer unp, Short year, long numberBegin, long numberEnd,
//                          List<String> numbersUsed,
//                          List<VatData> vats, Callback4<Integer, Short, Long, Integer, Void> callback) throws Exception {
//        String dir = ConfigReader.getInstance().getVatPath();
//        new File(dir).mkdirs();
//
//        long counter = numberBegin;
//        try (VatTool vt = new VatTool()) {
//            for (VatData vd : vats) {
//                if (!vd.isVatIssued()) {
//                    // подобрать номер из настроенных, но еще не задействованный:
//                    String number = null;
//                    for (; counter <= numberEnd; counter++) {
//                        number = VatHelpers.vatNumber(unp, year, counter);
//                        if (!numbersUsed.contains(number) && vt.isNumberSpare(number)) {
//                            break;
//                        }
//                    }
//
//                    if (counter > numberEnd) {
//                        throw new Exception(String.format("Не осталось свободных настроенных номеров счет-фактур"));
//                    }
//                    vd.setVatYear(year);
//                    vd.setVatUnp(unp);
//                    vd.setVatNumber(counter++);
//                }
//                final String vatXml = makeVATXml(vd);
//                vt.doSignAndUploadString(vatXml);
//                // записать в базу:
//            }
//        }
//    }

    private String makeVATXml(VatData vd) {
        String template = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<issuance xmlns='http://www.w3schools.com' sender='190471274'>\n" +
                "    <general>\n" +
                "        <number>{number}</number>\n" +
                "        <!--<dateIssuance>{dateIssuance}</dateIssuance>-->\n" +
                "        <dateTransaction>{dateTransaction}</dateTransaction> <!-- yyyy-MM-dd -->\n" +
                "        <documentType>ORIGINAL</documentType>\n" +
                "    </general>\n" +
                "    <provider>\n" +
                "        <providerStatus>SELLER</providerStatus>\n" +
                "        <dependentPerson>false</dependentPerson>\n" +
                "        <residentsOfOffshore>false</residentsOfOffshore>\n" +
                "        <specialDealGoods>false</specialDealGoods>\n" +
                "        <bigCompany>false</bigCompany>\n" +
                "        <countryCode>112</countryCode>\n" +
                "        <unp>190471274</unp>\n" +
                "        <name>УП 'Белтехосмотр'</name>\n" +
                "        <address>г. Минск,ул. Платонова, д.22а, ком. 312</address>\n" +
                "    </provider>\n" +
                "    <recipient>\n" +
                "        <recipientStatus>CUSTOMER</recipientStatus>\n" +
                "        <dependentPerson>false</dependentPerson>\n" +
                "        <residentsOfOffshore>false</residentsOfOffshore>\n" +
                "        <specialDealGoods>false</specialDealGoods>\n" +
                "        <bigCompany>false</bigCompany>\n" +
                "        <countryCode>112</countryCode>\n" +
                "        <unp>{customerUnp}</unp>\n" +
                "        <name>{customerName}</name>\n" +
                "        <address>{customerAddress}</address>\n" +
                "    </recipient>\n" +
                "    <senderReceiver>\n" +
                "        <consignors/>\n" +
                "        <consignees/>\n" +
                "    </senderReceiver>\n" +
                "    <deliveryCondition>\n" +
                "        <contract>\n" +
                "            <documents>\n" +
                "                <document>\n" +
                "                    <docType>\n" +
                "                        <code>606</code>\n" +
                "                    </docType>\n" +
                "                    <date>{actDate}</date>\n" +
                "                    <blankCode></blankCode>\n" +
                "                    <seria>{actSeries}</seria>\n" +
                "                    <number>{actNumber}</number>\n" +
                "                </document>\n" +
                "            </documents>\n" +
                "        </contract>\n" +
                "    </deliveryCondition>\n" +
                "    <roster totalCostVat='{totalCostVat}' totalExcise='0' totalVat='{totalVat}' totalCost='{totalCost}'>\n" +
                "        <rosterItem>\n" +
                "            <number>0</number>\n" +
                "            <name>{serviceName}</name>\n" +
                "            <code></code>\n" +
                "            <units>796</units>\n" +
                "            <count>1</count>\n" +
                "            <price>{totalCost}</price>\n" +
                "            <cost>{totalCost}</cost>\n" +
                "            <summaExcise>0</summaExcise>\n" +
                "            <vat>\n" +
                "                <rate>20</rate>\n" +
                "                <rateType>DECIMAL</rateType>\n" +
                "                <summaVat>{totalVat}</summaVat>\n" +
                "            </vat>\n" +
                "            <costVat>{totalCostVat}</costVat>\n" +
                "        </rosterItem>\n" +
                "    </roster>\n" +
                "</issuance>";
        String sDate = String.format("%1$tY-%1$tm-%1$td", vd.get_date());
        return template
                .replace("{number}", VatHelpers.vatNumber(vd.getVatUnp(), vd.getVatYear(), vd.getVatNumber()))
                .replace("{dateIssuance}", sDate)
                .replace("{dateTransaction}", sDate)
                .replace("{actDate}", sDate)
                .replace("{customerUnp}", String.format("%09d", vd.getContractorUnp()))
                .replace("{customerName}", vd.getContractorName())
                .replace("{customerAddress}", "")
                .replace("{totalCostVat}", vd.getWithVAT())
                .replace("{totalVat}", vd.getVAT())
                .replace("{totalCost}", vd.getWithoutVAT())
                .replace("{actSeries}", vd.getBlankSeries())
                .replace("{actNumber}", vd.getBlankNumber())
                .replace("{serviceName}", ConfigReader.getInstance().getServiceName());
    }

    public void bIssueUploadAction(ActionEvent actionEvent) {
        issueVATS();
    }

    public void miHelpAction(ActionEvent actionEvent) {
        try {
            String f = new File(MainController.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            Desktop.getDesktop().open(new File(f + "\\vat_manual.doc"));
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static class VatData {
        int blancTsInfoId;
        Integer vatId;
        private SimpleStringProperty vatFullNumber;
        private SimpleStringProperty date;


        private Integer vatUnp;
        private Short vatYear;
        private Long vatNumber;

        private Date _date;
        private SimpleIntegerProperty contractorUnp;
        private SimpleStringProperty contractorName;
        private SimpleStringProperty withoutVAT;
        private SimpleStringProperty withVAT;
        private SimpleStringProperty VAT;

        private SimpleStringProperty blankSeries;
        private SimpleStringProperty blankNumber;

        public VatData(int blancTsInfoId, Integer vatId,
                       String blankSeries, Integer blankNumber,
                       Integer vatUnp, Short vatYear, Long vatNumber,
                       Date date, int contractorUnp, String contractorName, BigDecimal withoutVAT,
                       BigDecimal withVAT, BigDecimal VAT) {
            this.blancTsInfoId = blancTsInfoId;
            this.vatId = vatId;
            this.blankSeries = new SimpleStringProperty(blankSeries);
            this.blankNumber = new SimpleStringProperty(String.valueOf(blankNumber));
            this.vatUnp = vatUnp;
            this.vatYear = vatYear;
            this.vatNumber = vatNumber;
            this.date = new SimpleStringProperty(String.format("%1$td.%1$tm.%1$tY", date));
            this._date = date;
            this.contractorUnp = new SimpleIntegerProperty(contractorUnp);
            this.contractorName = new SimpleStringProperty(contractorName);
            this.withoutVAT = new SimpleStringProperty(withoutVAT.toString());
            this.withVAT = new SimpleStringProperty(withVAT.toString());
            this.VAT = new SimpleStringProperty(VAT.toString());


            if (isVatIssued()) {
                this.vatFullNumber = new SimpleStringProperty(
                        String.format("%09d-%04d-%010d", vatUnp, vatYear, vatNumber));
            } else {
                this.vatFullNumber = new SimpleStringProperty(null);
            }
//            StringBinding concat = Bindings.createStringBinding(() -> {
//                if (VatData.this.getVatUnp() < 0) {
//                    return "";
//                }
//                return String.format("%9d-%4d-%10d", VatData.this.getVatUnp(), VatData.this.getVatYear(), VatData.this.getVatNumber());
//            }, vatUnpProperty(), vatYearProperty(), vatNumberProperty());
//
//            this.vatFullNumberProperty().bind(concat);
        }

        public int getBlancTsInfoId() {
            return blancTsInfoId;
        }

        public void setBlancTsInfoId(int blancTsInfoId) {
            this.blancTsInfoId = blancTsInfoId;
        }


        public String getDate() {
            return date.get();
        }

        public void setDate(String date) {
            this.date.set(date);
        }

        public SimpleStringProperty dateProperty() {
            return date;
        }

        public int getContractorUnp() {
            return contractorUnp.get();
        }

        public void setContractorUnp(int contractorUnp) {
            this.contractorUnp.set(contractorUnp);
        }

        public SimpleIntegerProperty contractorUnpProperty() {
            return contractorUnp;
        }

        public String getContractorName() {
            return contractorName.get();
        }

        public void setContractorName(String contractorName) {
            this.contractorName.set(contractorName);
        }

        public SimpleStringProperty contractorNameProperty() {
            return contractorName;
        }

        public String getWithoutVAT() {
            return withoutVAT.get();
        }

        public void setWithoutVAT(String withoutVAT) {
            this.withoutVAT.set(withoutVAT);
        }

        public SimpleStringProperty withoutVATProperty() {
            return withoutVAT;
        }

        public String getWithVAT() {
            return withVAT.get();
        }

        public void setWithVAT(String withVAT) {
            this.withVAT.set(withVAT);
        }

        public SimpleStringProperty withVATProperty() {
            return withVAT;
        }

        public String getVAT() {
            return VAT.get();
        }

        public void setVAT(String VAT) {
            this.VAT.set(VAT);
        }

        public SimpleStringProperty VATProperty() {
            return VAT;
        }

        public String getVatFullNumber() {
            return vatFullNumber.get();
        }

        public void setVatFullNumber(String vatFullNumber) {
            this.vatFullNumber.set(vatFullNumber);
        }

        public SimpleStringProperty vatFullNumberProperty() {
            return vatFullNumber;
        }

        public Date get_date() {
            return _date;
        }

        public void set_date(Date _date) {
            this._date = _date;
        }

        public boolean isVatIssued() {
            return (vatUnp != null && vatYear != null && vatNumber != null);
        }

        public Integer getVatUnp() {
            return vatUnp;
        }

        public void setVatUnp(Integer vatUnp) {
            this.vatUnp = vatUnp;
        }

        public Short getVatYear() {
            return vatYear;
        }

        public void setVatYear(Short vatYear) {
            this.vatYear = vatYear;
        }

        public Long getVatNumber() {
            return vatNumber;
        }

        public void setVatNumber(Long vatNumber) {
            this.vatNumber = vatNumber;
        }

        public Integer getVatId() {
            return vatId;
        }

        public void setVatId(Integer vatId) {
            this.vatId = vatId;
        }

        public String getBlankSeries() {
            return blankSeries.get();
        }

        public SimpleStringProperty blankSeriesProperty() {
            return blankSeries;
        }

        public void setBlankSeries(String blankSeries) {
            this.blankSeries.set(blankSeries);
        }

        public String getBlankNumber() {
            return blankNumber.get();
        }

        public SimpleStringProperty blankNumberProperty() {
            return blankNumber;
        }

        public void setBlankNumber(String blankNumber) {
            this.blankNumber.set(blankNumber);
        }
    }
}
