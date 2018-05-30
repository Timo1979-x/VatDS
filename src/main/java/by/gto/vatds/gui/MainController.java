package by.gto.vatds.gui;

import by.gto.helpers.*;
import by.gto.library.db.NamedParameterStatement;
import by.gto.model.*;
import by.gto.tools.ConfigReader;
import by.gto.tools.ConnectionMySql;
import by.gto.tools.Util;
import by.gto.tools.VatTool;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.*;
import javafx.util.Callback;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

@SuppressWarnings("SqlDialectInspection")
public class MainController implements Initializable {

    private static final Logger log = Logger.getLogger(MainController.class);
    private static final int REQUIRED_DB_VERSION = 190;

    @FXML
    public ComboBox comboBoxYear;
    @FXML
    public ComboBox comboBoxMonth;
    @FXML
    public TableColumn<VatData, String> colContractorName;
    @FXML
    public TableColumn colContractorUNP;
    @FXML
    public TableColumn colDate;
    @FXML
    public TableColumn<VatData, BigDecimal> colWithoutVAT;
    @FXML
    public TableColumn<VatData, BigDecimal> colVAT;
    @FXML
    public TableColumn<VatData, BigDecimal> colWithVAT;
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
    public TableColumn<VatData, Integer> colVatState;
    @FXML
    public AnchorPane apMain;
    @FXML
    public GridPane gpMessage;
    @FXML
    public StackPane spRoot;
    @FXML
    public TableColumn<VatData, String> colAgrNumber;
    @FXML
    public Label lMessage1;
    @FXML
    public TableColumn<VatData, Date> colAgrDate;
    @FXML
    public TableColumn<VatData, Integer> colBranch;
    @FXML
    public Menu menuVAT;
    @FXML
    public AnchorPane apVAT;
    @FXML
    public MenuItem miVATSettings;

    private Scene thisScene;

    private ObservableList<VatData> vatData = FXCollections.observableArrayList();
    private ObservableList<Integer> years = FXCollections.observableArrayList(
            2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2018, 2019,
            2020, 2021, 2022, 2024, 2024, 2025, 2026, 2027, 2028, 2029,
            2030, 2031
    );

    public SimpleStringProperty messageText = new SimpleStringProperty();

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

    public static void showInfoMessage(String message) {
        showInfoMessage("", message);
    }

    public static void showInfoMessage(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.CLOSE);
        a.setTitle(title);
        a.setHeaderText(null);
        a.showAndWait();
    }

//    public static String passwordPrompt(String title, int minLength) throws IOException {
//        Stage newStage = new Stage();
//        FXMLLoader loader = new FXMLLoader(MainController.class.getClassLoader().getResource("fxml/passwordPrompt.fxml"));
//        try {
//            Parent root = loader.load();
//
//            PasswordPromptController controller = loader.<PasswordPromptController>getController();
//            newStage.initModality(Modality.APPLICATION_MODAL);
//            newStage.setTitle(title);
//            newStage.setScene(new Scene(root));
//            newStage.setResizable(false);
//            controller = (PasswordPromptController) loader.getController();
//            controller.setCaption(title);
//            String result;
//            do {
//                newStage.showAndWait();
//                result = controller.getResult();
//                if (null != result && result.length() < minLength) {
//                    controller.setMessage("Минимальная длина пароля " + minLength + " символов");
//                } else {
//                    break;
//                }
//            } while (true);
//            return result;
//
//        } catch (Exception e) {
//            if (Main.verbose) {
//                log.error(e.getMessage(), e);
//            }
//            if (Main.debug) {
//                throw e;
//            }
//            return null;
//        }
//    }

    public static Object[] chooseCredentialsFromList(String title) {
        KeyStore ks;
        List<String> items = new ArrayList<>();
        try {
            ks = KeyStore.getInstance("AvPersonal");
            ks.load((InputStream) null, (char[]) null);
            Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                String a = aliases.nextElement();
                items.add(a);
                System.out.println(a);
            }
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        }
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

            final String alias = System.getProperty("by.gto.vatds.avest.alias");
            if (alias != null) {
                int i = items.indexOf(alias);
                if (i != -1) {
                    controller.lList.getSelectionModel().select(i);
                    final String pass = System.getProperty("by.gto.vatds.avest.password");
                    if (pass != null) {
                        controller.setPassword(pass);
                    }
                }
            }
            if (controller.lList.getSelectionModel().getSelectedIndex() == -1 && controller.lList.getItems().size() == 1) {
                controller.lList.getSelectionModel().select(0);
            }
            newStage.showAndWait();
            result[0] = controller.getAlias();
            result[1] = controller.getPassword();
        } catch (IOException e) {
            if (Main.verbose) {
                log.error(e.getMessage(), e);
            }
        }
        return result;
    }

    public void miAboutClick(ActionEvent actionEvent) throws IOException {
        FXHelpers.INSTANCE.openChildWindow("", "fxml/about.fxml", "piggy-bank-icon.png", "О программе");
    }

    public void miSettingsClick(ActionEvent actionEvent) throws IOException {
        FXHelpers.INSTANCE.openChildWindow("", "fxml/settings.fxml", "piggy-bank-icon.png", "Настройки");
    }

    public void miQuitClick(ActionEvent actionEvent) {
        Main.getStage().close();
    }

    public void miCheckUpdatesClick(ActionEvent actionEvent) {
        CheckUpdate(true);
    }

    private void CheckUpdate(boolean showMessage) {
        try {
            URL url = new URL("http://gto.by/api/check.updates.php?name=vatDS");
            ConfigReader config = ConfigReader.getInstance();

            InputStream is = null;
            if (config.isUseProxy()) {
                Authenticator.setDefault(new Authenticator() {
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(config.getProxyUser(), config.getProxyPass().toCharArray());
                    }
                });
                final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.getProxyHost(), config.getProxyPort()));
                is = url.openConnection(proxy).getInputStream();
            } else {
                Authenticator.setDefault(null);
                is = url.openStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String strTemp = "";
            while (null != (strTemp = br.readLine())) {
                ApplicationInfo appInfo = ApplicationInfo.getInstance();
                JSONObject obj = new JSONObject(strTemp);
                String name = obj.getString("name");
                String version = obj.getString("version");
                String url_ = obj.getString("url");
                int verCompere = Util.versionCompare(appInfo.getVersion(), version);
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
                            + appInfo.getVersion() + "<br />Версия обновления: "
                            + version + "<br /><a href=\"" + url_
                            + "\" target=\"_blank\"><strong>Загрузить</strong></a></body></html>");
                    newStage.show();


                } else if (showMessage) {
                    showInfoMessage("", "Установлена последняя версия vatDS");
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String javaVersion = System.getProperty("java.runtime.version");
        String message = null;
        try {
            if (null == javaVersion) {

            } else {
                String[] split = javaVersion.split("[\\._-]");
                if (split.length >= 4) {
                    if (Integer.valueOf(split[0]) >= 1 && Integer.valueOf(split[1]) >= 8
                            && Integer.valueOf(split[2]) >= 0 && Integer.valueOf(split[3]) >= 121) {
                        message = "Версия java = " + javaVersion + ".";
                    }
                } else {
                    throw new Exception("");
                }
            }
        } catch (Exception e) {
            message = "Не смог определить версию Java, на которой работает программа";
        }
        if (null != message) {
            showInfoMessage("Внимание", message + "\nИзвестно, что c версии 1.8.0_121 не работает криптопровайдер Авест");
        }

        try {
            Main.initLogger();
//            ConfigReader.createInstance();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            showErrorMessage("Продолжение работы невозможно", "Невозможно прочитать файл конфигурации или создать лог-файл.\n" +
                    "Программа будет закрыта");
            System.exit(-1);
        }
        ApplicationInfo appInfo = ApplicationInfo.getInstance();
        log.warn("Started " + appInfo.getName() + " + v" + appInfo.getVersion() + " on jre " + System.getProperty("java.runtime.version"));
        if (!AvestHelpers.initAvest()) {
            String errMsg = "Не установлен криптопровайдер Avest! Работа со счет-фактурами будет невозможна";
            log.error(errMsg);
            showErrorMessage("Ошибка", errMsg);
        }

        comboBoxMonth.setItems(months);
        comboBoxYear.setItems(years);

        colContractorName.setCellValueFactory(cellData -> cellData.getValue().contractorNameProperty());
        colContractorName.setCellFactory(column -> {
            return new TableCell<VatData, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setGraphic(null);
                        TableRow<VatData> currentRow = getTableRow();
                        String style = "";
                        VatData currentItem = currentRow.getItem();
                        if (currentItem != null && currentItem.isVatIssued()) {
                            style = "-fx-font-style: italic; -fx-font-weight: bold";
                        }
//                        if (currentRow.getItem().isHasBranches()) {
//                            setText("---- Есть подразделения! ----");
//                        }
//                        if (currentRow.getItem().isDup()) {
//                            if (currentRow.getItem().isVatIssued()) {
//                                style = "-fx-background-color: lightcoral;";
//                            } else {
//                                style = "-fx-background-color: lightsalmon; -fx-text-fill: black";
//                            }
//                        } else {
//                            if (currentRow.getItem().isVatIssued()) {
//                                style = "-fx-background-color: lightcyan; -fx-text-fill: black";
//                            }
//                        }
                        currentRow.setStyle(style);

                    }
                }
            };
        });

//        colContractorName.setCellValueFactory(
//                new PropertyValueFactory<VatData, String>("contractorName"));
        colVATFullNumber.setCellValueFactory(
                new PropertyValueFactory<VatData, String>("vatFullNumber"));
        colDate.setCellValueFactory(
                new PropertyValueFactory<VatData, String>("date"));
        colContractorUNP.setCellValueFactory(
                new PropertyValueFactory<VatData, Integer>("contractorUnp"));

        TableViewHelpers.initBigdecimalColumn(colWithoutVAT, "withoutVAT");
        TableViewHelpers.initBigdecimalColumn(colWithVAT, "withVAT");
        TableViewHelpers.initBigdecimalColumn(colVAT, "VAT");
        colBlankSeries.setCellValueFactory(
                new PropertyValueFactory<VatData, String>("blankSeries"));
        colBlankNumber.setCellValueFactory(
                new PropertyValueFactory<VatData, String>("blankNumber"));

        colVatState.setCellValueFactory(new PropertyValueFactory<VatData, Integer>("vatState"));
        colVatState.setCellFactory(column -> {
            return new TableCell<VatData, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        setText(VatStatusEnum.getByOrdinal(item == null ? 0 : item).toString());
                    }
                }
            };
        });

        colAgrNumber.setCellValueFactory(new PropertyValueFactory<>("agreementNumber"));
        colAgrNumber.setCellFactory(new Callback<TableColumn<VatData, String>, TableCell<VatData, String>>() {
            public TableCell call(TableColumn p) {
                return new AgreementComboBoxCell();
            }
        });

        colAgrDate.setCellValueFactory(new PropertyValueFactory<>("agreementDate"));
//        colAgrDate.setCellFactory(new Callback<TableColumn<VatData, String>, TableCell<VatData, String>>() {
//            public TableCell call(TableColumn p) {
//                return new ComboBoxCell();
//            }
//        });

        colBranch.setCellValueFactory(new PropertyValueFactory<VatData, Integer>("branch"));
        colBranch.setCellFactory(new Callback<TableColumn<VatData, Integer>, TableCell<VatData, Integer>>() {
            public TableCell call(TableColumn p) {
                return new BranchComboBoxCell();
            }
        });

        vatTableView.setItems(vatData);
        vatTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        vatTableView.setItems(vatData);

        // загрузим данные за прошлый месяц
        Date d = new Date();
        int y = d.getYear() + 1900;
        int m = d.getMonth();


        comboBoxMonth.getSelectionModel().select(m);
        comboBoxYear.getSelectionModel().select(years.indexOf(y));
        int ver;
        try {
            ver = checkDBVersion();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ver = -1;
        }
        if (ver < 0) {
            MainController.showErrorMessage("Ошибка",
                    "Не удалось проверить версию БД");

        } else if (ver > 0) {
            MainController.showErrorMessage("Неправильная версия БД",
                    String.format("Для работы с ЭСЧФ необходима\n" +
                            "база данных АРМ ДС версии %d\n" +
                            "(поставляется с АРМ ДС версии 3.1.0.0 или более поздней)\n" +
                            "Версия Вашей БД - %d", REQUIRED_DB_VERSION, ver));
        }
        if (ver != 0) {
            disableVATControls(true);
        } else {
            try {
                refreshVats();
            } catch (Exception e) {
                // тут не нужна никакая обработка и сообщения, чтобы не смущать пользователей при старте программы
            }
        }


        apMain.prefWidthProperty().bind(spRoot.widthProperty());
        apMain.prefHeightProperty().bind(spRoot.heightProperty());
//        gpMessage.prefWidthProperty().bind(spRoot.widthProperty());
//        gpMessage.prefHeightProperty().bind(spRoot.heightProperty());
        lMessage.textProperty().bind(messageText);
        lMessage1.textProperty().bind(messageText);
        lMessage1.prefWidthProperty().bind(gpMessage.widthProperty());
    }

    private void disableVATControls(boolean b) {
        apVAT.setDisable(b);
        menuVAT.setDisable(b);
        miVATSettings.setDisable(b);
    }

    public void miVATSettingsAction(ActionEvent actionEvent) throws IOException {
        FXHelpers.INSTANCE.openChildWindow("", "fxml/settingsVAT.fxml", "piggy-bank-icon.png", "Настройка диапазонов номеров ЭСЧФ");
    }

    private void refreshVats() throws SQLException {
        int idxYear = comboBoxYear.getSelectionModel().getSelectedIndex();
        int idxMonth = comboBoxMonth.getSelectionModel().getSelectedIndex();
        if (idxYear < 0 || idxMonth < 0) {
            return;
        }
        int selectedMonth = idxMonth + 1;
        int selectedYear = years.get(idxYear);

//        Map<Integer, ObservableList<BranchInfo>> unpsForBranches = new HashMap<>();

        String querySelectVats = "SELECT\n" +
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
                "  b.number blankNumber,\n" +
                "--  case when branchesSubquery.unp is null then false else true end hasBranches,\n" +
                "  IFNULL(vats.state, 0) state,\n" +
                "  vats.branch,\n" +
                "  vats.agr_num,\n" +
                "  vats.agr_date\n" +
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
                "-- left join (SELECT DISTINCT bi.unp FROM ei_BRANCHES bi) branchesSubquery\n" +
                "--                  on (branchesSubquery.unp = oi.unp)\n" +
                "WHERE oi.id_owner_type IN (2, 3)\n" +
                "AND EXTRACT(MONTH FROM bti.date_ot) = ?\n" +
                "AND EXTRACT(year FROM bti.date_ot) = ?\n" +
                "AND b.id_blanc_type = 1\n" +
                "AND b.id_blanc_status = 2\n" +
                "AND stti.summa_oplaty > 0";
        try (Connection conn = ConnectionMySql.getInstance().getConn();
             PreparedStatement psSelectVats = conn.prepareStatement(querySelectVats)
        ) {
            psSelectVats.setInt(1, selectedMonth);
            psSelectVats.setInt(2, selectedYear);


            try (ResultSet rs = psSelectVats.executeQuery()) {
                vatData.clear();
                while (rs.next()) {
                    Integer vatUNP;
                    Integer vatId;
                    Integer blankNumber;
                    Integer branch;

                    Short vatYear;
                    Long vatNumber;
                    int iTemp;

                    iTemp = rs.getInt("v_unp");
                    vatUNP = (rs.wasNull() ? null : iTemp);

                    iTemp = rs.getInt("blankNumber");
                    blankNumber = (rs.wasNull() ? null : iTemp);

                    iTemp = rs.getInt("vats_id");
                    vatId = (rs.wasNull() ? null : iTemp);

                    iTemp = rs.getInt("branch");
                    branch = (rs.wasNull() ? null : iTemp);

                    short sTemp = rs.getShort("v_year");
                    vatYear = (rs.wasNull() ? null : sTemp);

                    long lTemp = rs.getLong("v_number");
                    vatNumber = (rs.wasNull() ? null : lTemp);
                    int contractorUnp = rs.getInt("unp");
                    VatData vd = new VatData(
                            rs.getInt("bti_id"), vatId,
                            rs.getString("blankSeries"),
                            blankNumber,
                            vatUNP, vatYear,
                            vatNumber, rs.getDate("date1"),
                            contractorUnp, rs.getString("name"),
                            rs.getBigDecimal("withoutVAT"),
                            rs.getBigDecimal("withVAT"),
                            rs.getBigDecimal("VAT"),
                            rs.getInt("state"),
//                            rs.getBoolean("hasBranches"),
                            branch,
                            rs.getString("agr_num"),
                            rs.getDate("agr_date")
                    );
                    vatData.add(vd);
                }
            }

            for (VatData vd : vatData) {
                if (vd.isVatIssued()) {
                    continue;
                }
//                    ObservableList<BranchInfo> b = unpsForBranches.get(vd.getContractorUnp());
                ObservableList<BranchInfo> b = BranchInfo.mapUnp2BranchList.get(vd.getContractorUnp());
                if (b != null && b.size() > 0) {
                    vd.setBranches(b);
                }
            }
            fillAgreementChoises(conn);

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    private void fillAgreementChoises(Connection conn) throws SQLException {
        Map<Integer, ObservableList<AgreementData>> unpsForAgreements = new HashMap<>();
        for (VatData vd : vatData) {
            if (!vd.isVatIssued()) {
                unpsForAgreements.put(vd.getContractorUnp(), FXCollections.<AgreementData>observableArrayList());
            }
        }
        if (unpsForAgreements.size() > 0) {
            StringBuilder qSelectAgreements = new StringBuilder("select id, unp, arg_num, agr_date FROM ei_agreements where active=1 and unp in (");

            for (Map.Entry<Integer, ObservableList<AgreementData>> pair : unpsForAgreements.entrySet()) {
                qSelectAgreements.append(pair.getKey()).append(',');
            }
            qSelectAgreements.setLength(qSelectAgreements.length() - 1);
            qSelectAgreements.append(") ORDER BY unp, agr_date desc");
            try (Statement sSelect = conn.createStatement();
                 ResultSet rs = sSelect.executeQuery(qSelectAgreements.toString())) {
                while (rs.next()) {
                    Integer unp = rs.getInt("unp");
                    unpsForAgreements.get(unp).add(new AgreementData(rs.getInt("id"), unp, rs.getString("arg_num"), rs.getDate("agr_date")));
                }
            }
        }
        for (VatData vd : vatData) {
            if (vd.isVatIssued()) {
                continue;
            }
            int contractorUnp = vd.getContractorUnp();
            ObservableList<AgreementData> l = unpsForAgreements.get(contractorUnp);
            AgreementData ad;
            switch (l.size()) {
                case 0:
                    vd.setAgreementOptions(null);
                    vd.setAgreementDate(null);
                    vd.setAgreementNumber(null);
                    break;
                case 1:
                    ad = l.get(0);
                    vd.setAgreementOptions(null);
                    vd.setAgreementNumber(ad.getNumber());
                    vd.setAgreementDate(ad.getDate());
                    break;
                default:
                    ad = l.get(0);
                    vd.setAgreementOptions(l);
                    if (vd.getAgreementNumber() == null) {
                        vd.setAgreementNumber(ad.getNumber());
                        vd.setAgreementDate(ad.getDate());
                    }
                    break;
            }
        }
        vatTableView.refresh();
    }

    /**
     * Проверяет, что версия структуры БД подходит этой версии программы
     * Также при необходимости обновляет В БД список организаций с подразделениями
     *
     * @return 0 если версия подходит. Версию БД, если она меньше требуемой
     * @throws SQLException
     */
    private int checkDBVersion() throws SQLException {
        try (Connection conn = ConnectionMySql.getInstance().getConn();
             Statement st = conn.createStatement()) {
            conn.setAutoCommit(false);
            try (ResultSet rs = st.executeQuery("SELECT MAX(v.ver) FROM version v")) {
                rs.next();
                final int ver = rs.getInt(1);
                if (ver < REQUIRED_DB_VERSION) {
                    return ver;
                }

            }
//            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM ei_branches")) {
//                rs.next();
//                final int count = rs.getInt(1);
//                if (count == BranchInfo.data.size()) {
//                    return 0;
//                }
//            }
//
//            st.executeUpdate("DELETE FROM ei_branches");
//            try (PreparedStatement ps1 = conn.prepareStatement("INSERT INTO ei_branches (unp, code, name) VALUES (?,?,?)")) {
//                for (BranchInfo bi : BranchInfo.data) {
//                    ps1.setInt(1, bi.getUnp());
//                    ps1.setInt(2, bi.getBranchCode());
//                    ps1.setString(3, StringUtils.substring(bi.getShortName(), 0, 100));
//                    ps1.addBatch();
//                }
//                ps1.executeBatch();
//            }
//            conn.commit();
        }
        return 0;

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
        StringBuilder sb = new StringBuilder();
        if (vatTableView.getSelectionModel().getSelectedIndex() == -1) {
            MainController.showInfoMessage("", "Не выделено ни одной строки");
            return;
        }
        ObservableList<Integer> selectedIndices = vatTableView.getSelectionModel().getSelectedIndices();

        List<VatData> selectedRows = selectedIndices.stream().map(ind -> vatData.get(ind)).collect(Collectors.toList());

        List<VatData> insufficientInputData;

        insufficientInputData = selectedRows.stream().filter(item -> StringUtils.isEmpty(item.getAgreementNumber()) || item.getAgreementDate() == null).collect(Collectors.toList());
        if (!insufficientInputData.isEmpty()) {
            sb.append("<h3>В выделенных строках присутствуют организации, для которых не прописан договор, " +
                    "или договор не выбран из списка (если заключено несколько договоров с одной организацией:</h3>");
            for (VatData record : insufficientInputData) {
                sb.append("<p>").append(record.getContractorUnp()).append(" ").append(record.getDate()).append("</p>");
            }
        }
        if (sb.length() > 0) {
            MainController.showLargeMessageBox("Незаполненные данные", sb.toString(), Modality.NONE);
            return;
        }

        insufficientInputData = selectedRows.stream().filter(item -> item.isHasBranches() && item.getBranch() == null).collect(Collectors.toList());
        if (!insufficientInputData.isEmpty()) {
            sb.setLength(0);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтвердите");
            alert.setHeaderText("В выделенных строках присутствуют организации с обособленными подразделениями,\n" +
                    "для которых не указан код подразделения. Вы уверены, что хотите выставить ЭСЧФ с пустыми кодами?");
            for (VatData record : insufficientInputData) {
                sb.append(record.getContractorUnp()).append(" ").append(record.getDate()).append("\n");
            }
            alert.setContentText(sb.toString());
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            if (!alert.showAndWait().get().equals(ButtonType.YES)) {
                return;
            }
        }

        short year = (short) Calendar.getInstance().get(Calendar.YEAR);

        Object[] credentials = chooseCredentialsFromList("Выберите ключ и введите пароль");
        if (credentials[0] == null) {
            return;
        }
        beforeStartLongTask("Формирование и отправка счет-фактур");
        final String keyAlias = (String) credentials[0];
        final String password = (String) credentials[1];
        Thread th = new Thread(new Task() {
            @Override
            protected Object call() throws Exception {
                long numberBegin, numberEnd;
                List<String> numbersUsed = new ArrayList<>();
                String qGetNumberRange = "SELECT ovs.`begin`, ovs.`end` FROM ei_vat_settings ovs WHERE ovs.`year` = ?";
                String qUsedVatNumbers = "SELECT ov.`number` FROM ei_vats ov\n" +
                        "  WHERE ov.`number` >= ? AND ov.`number` <= ? AND ov.unp = ?  AND ov.`year` = ?\n" +
                        "\n" +
                        "  ORDER BY ov.`number`";
                String qIssueVat = "INSERT ei_vats(id_blank_ts_info, unp, `year`, `number`, branch, agr_num, agr_date)\n" +
                        "  VALUES (?, ?, ?, ?, ?, ?, ?);";
                try (Connection conn = ConnectionMySql.getInstance().getConn();
                     PreparedStatement psGetNumberRange = conn.prepareStatement(qGetNumberRange);
                     PreparedStatement psUsedVatNumbers = conn.prepareStatement(qUsedVatNumbers);
                     PreparedStatement psIssueVat = conn.prepareStatement(qIssueVat);
                ) {
                    configureProxy();
                    final ConfigReader config = ConfigReader.getInstance();
                    int unp = config.getUNP();
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
                    try (VatTool vt = new VatTool(keyAlias, password)) {
                        final List<String> allUnpsList = selectedRows.stream().map(vd -> String.valueOf(vd.getContractorUnp())).distinct().collect(Collectors.toList());
                        Map<String, CustomerInfo> unpInfo = getUNPsInfo(conn, allUnpsList);
                        final Map<String, CustomerInfo> unpCheckResult = vt.checkUNPsWithAddresses(unpInfo);
                        List<String> badUNPs = unpCheckResult.keySet().stream().filter(s -> unpCheckResult.get(s).getAddress() == null).collect(Collectors.toList());
                        saveCheckedUnps(conn, unpCheckResult);

                        if (badUNPs.size() > 0) {
                            StringBuilder sb = new StringBuilder("<p>");
                            for (String badUnp : badUNPs) {
                                sb.append(badUnp).append(", ");
                            }
                            sb.setLength(sb.length() - 2);
                            sb.append("</p>");
                            Platform.runLater(() -> showLargeMessageBox("Ошибка проверки УНП", sb.toString()));
                            return null;
                        }

                        unpInfo.putAll(unpCheckResult);
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
                                                psIssueVat.setNull(5, Types.INTEGER);
                                                psIssueVat.setString(6, "");
                                                psIssueVat.setDate(7, new java.sql.Date(0));
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

                            } else {
                                number = VatHelpers.vatNumber(vd.getVatUnp(), vd.getVatYear(), vd.getVatNumber());
                            }
                            final String vatXml = makeVATXml(vd, unpInfo);
                            if (!issued) {
                                // записать в базу:
                                // INSERT ei_vats(id_blank_ts_info, unp, `year`, `number`)
                                psIssueVat.setInt(1, vd.getBlancTsInfoId());
                                psIssueVat.setInt(2, vd.getVatUnp());
                                psIssueVat.setShort(3, vd.getVatYear());
                                psIssueVat.setLong(4, vd.getVatNumber());
                                Integer branch = vd.getBranch();
                                if (branch == null) {
                                    psIssueVat.setNull(5, Types.INTEGER);
                                } else {
                                    psIssueVat.setInt(5, vd.getBranch());
                                }
                                psIssueVat.setString(6, vd.getAgreementNumber());
                                java.sql.Date agrDate = null;
                                if (vd.getAgreementDate() != null) {
                                    agrDate = new java.sql.Date(vd.getAgreementDate().getTime());
                                }
                                psIssueVat.setDate(7, agrDate);
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
                        log.error(e.getMessage(), e);
                        conn.rollback();
                        throw e;
                    } finally {
                        // TODO убрать после проверки
//                        System.out.println("xxxxx");
                    }

                    final String resultMessage = "Загрузка на портал ЭСЧФ завершена успешно";
                    Platform.runLater(() -> MainController.showInfoMessage("", resultMessage));
                    Platform.runLater(() -> afterFinishLongTask(resultMessage));
                } catch (Exception e) {
                    Platform.runLater(() -> MainController.showErrorMessage("Ошибка", e.getMessage()));
                    if (Main.verbose) {
                        log.error(e.getMessage(), e);
                    } else {
                        log.error("[ОШИБКА] " + e.getMessage());
                    }
                    Platform.runLater(() -> afterFinishLongTask("Возникла ошибка"));
                } finally {
                    // TODO убрать после проверки
//                    System.out.println("yyy");
                }
                try {
                    refreshVats();
                } catch (Exception e) {
                    Platform.runLater(() -> MainController.showErrorMessage("Ошибка", e.getMessage()));
                }
                return null;
            }
        });
        th.start();

    }

    // TODO: debug
    private void saveCheckedUnps(Connection conn, Map<String, CustomerInfo> goodUNPs) throws SQLException {
        ResultSet rs = null;
        try (PreparedStatement psInsert = conn.prepareStatement("insert into ei_checked_unps (UNP, name, address) values (?,?,?)");
             PreparedStatement psUpdate = conn.prepareStatement("update ei_checked_unps set name = ?, address = ? where UNP=?");
             PreparedStatement psCheck = conn.prepareStatement("select count(*) from ei_checked_unps where unp=?");
        ) {

            for (String unp : goodUNPs.keySet()) {
                CustomerInfo customerInfo = goodUNPs.get(unp);

                psCheck.setString(1, unp);
                rs = psCheck.executeQuery();
                rs.next();
                String address = StringUtils.substring(customerInfo.getAddress(), 0, 150);
                String name = StringUtils.substring(customerInfo.getName(), 0, 150);
                if (rs.getInt(1) == 0) {
                    psInsert.setInt(1, customerInfo.getUnp());
                    psInsert.setString(2, name);
                    psInsert.setString(3, address);
                    psInsert.addBatch();
                } else {
                    psUpdate.setString(1, name);
                    psUpdate.setString(2, address);
                    psUpdate.setInt(3, customerInfo.getUnp());
                    psUpdate.addBatch();
                }

            }
            psInsert.executeBatch();
            psUpdate.executeBatch();
            conn.commit();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
            }
        }
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

    private String makeVATXml(VatData vd, Map<String, CustomerInfo> unpInfo) {
        String template = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<issuance xmlns='http://www.w3schools.com' sender='{ourUNP}'>\n" +
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
                "        <unp>{ourUNP}</unp>\n" +
                "        <name>{ourName}</name>\n" +
                "        <address>{ourAddress}</address>\n" +
                "    </provider>\n" +
                "    <recipient>\n" +
                "        <recipientStatus>CUSTOMER</recipientStatus>\n" +
                "        <dependentPerson>false</dependentPerson>\n" +
                "        <residentsOfOffshore>false</residentsOfOffshore>\n" +
                "        <specialDealGoods>false</specialDealGoods>\n" +
                "        <bigCompany>false</bigCompany>\n" +
                "        <countryCode>112</countryCode>\n" +
                "        <unp>{customerUnp}</unp>\n" +
                (null == vd.getBranch() ? "" : "        <branchCode>{branchCode}</branchCode>\n") +
                "        <name>{customerName}</name>\n" +
                "        <address>{customerAddress}</address>\n" +
                "    </recipient>\n" +
                "    <senderReceiver>\n" +
                "        <consignors/>\n" +
                "        <consignees/>\n" +
                "    </senderReceiver>\n" +
                "    <deliveryCondition>\n" +
                "        <contract>\n" +
                "            <number>{agrNum}</number>\n" +
                "            <date>{agrDate}</date>\n" +
                "            <documents>\n" +
                "                <document>\n" +
                "                    <docType>\n" +
                "                        <code>601</code>\n" +
                "                        <value>Диагностическая карта</value>\n" +
                "                    </docType>\n" +
                "                    <date>{actDate}</date>\n" +
                "                    <blankCode></blankCode>\n" +
                "                    <seria>{actSeries}</seria>\n" +
                "                    <number>{actNumber}</number>\n" +
                "                </document>\n" +
                "            </documents>\n" +
                "        </contract>\n" +
                "    </deliveryCondition>\n" +
                "    <roster totalCostVat='{totalCostVat}' totalExcise='0.00' totalVat='{totalVat}' totalCost='{totalCost}'>\n" +
                "        <rosterItem>\n" +
                "            <number>1</number>\n" +
                "            <name>{serviceName}</name>\n" +
                "            <code></code>\n" +
                "            <units>796</units>\n" +
                "            <count>1</count>\n" +
                "            <price>{totalCost}</price>\n" +
                "            <cost>{totalCost}</cost>\n" +
                "            <summaExcise>0.00</summaExcise>\n" +
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
        String sAgrDate = String.format("%1$tY-%1$tm-%1$td", vd.getAgreementDate());
        String customerUnp = String.format("%09d", vd.getContractorUnp());
        CustomerInfo customerInfo = unpInfo.get(customerUnp);
        String customerAddress = customerInfo.getAddress();
        String customerName = customerInfo.getName();
        if (null == customerAddress) {
            customerAddress = "";
        }
        if (null == customerName) {
            customerName = "";
        }

        final ConfigReader configReader = ConfigReader.getInstance();
        return template
                .replace("{number}", VatHelpers.vatNumber(vd.getVatUnp(), vd.getVatYear(), vd.getVatNumber()))
                .replace("{dateIssuance}", sDate)
                .replace("{dateTransaction}", sDate)
                .replace("{actDate}", sDate)
                .replace("{customerUnp}", customerUnp)
                .replace("{branchCode}", vd.getBranch() != null ? String.format("%04d", vd.getBranch()) : "")
                .replace("{customerName}", XmlHelper.replaceXmlSymbols(customerName))
//                .replace("{customerName}", XmlHelper.replaceXmlSymbols(vd.getContractorName()))
                .replace("{customerAddress}", XmlHelper.replaceXmlSymbols(customerAddress))
                .replace("{agrDate}", sAgrDate)
                .replace("{agrNum}", XmlHelper.replaceXmlSymbols(vd.getAgreementNumber()))
                .replace("{totalCostVat}", vd.getWithVAT().setScale(2).toPlainString())
                .replace("{totalVat}", vd.getVAT().setScale(2).toPlainString())
                .replace("{totalCost}", vd.getWithoutVAT().setScale(2).toPlainString())
                .replace("{actSeries}", vd.getBlankSeries())
                .replace("{actNumber}", StringUtils.leftPad(vd.getBlankNumber(), 7, '0'))
                .replace("{serviceName}", configReader.getServiceName())
                .replace("{ourUNP}", String.format("%09d", configReader.getUNP()))
                .replace("{ourName}", configReader.getOrgName())
                .replace("{ourAddress}", ((ConfigReader) configReader).getOrgAddress());
    }

    public void bUploadAction(ActionEvent actionEvent) {
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

    // TODO: debug
    private Map<String, CustomerInfo> getUNPsInfo(Connection conn, List<String> allUnps) throws SQLException {
        Map<String, CustomerInfo> result = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        sb.append("select t1.u1, t2.name, t2.address from (");
        for (String unp : allUnps) {
            sb.append("select ").append(unp).append(" u1 union ");
        }
        sb.setLength(sb.length() - 6);
        sb.append(") t1 left join ei_checked_unps t2 on (t1.u1 = t2.unp)");
        try (Statement stmtUnp = conn.createStatement();
             ResultSet rs = stmtUnp.executeQuery(sb.toString())) {
            while (rs.next()) {
                result.put(rs.getString(1),
                        new CustomerInfo(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
        }
        return result;
    }


    public static void showLargeMessageBox(String title, String htmlContent, Modality m) {
        Stage newStage = new Stage();
        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("fxml/messageBox.fxml"));
        try {
            Parent root = loader.load();
            MessageBoxController controller = loader.<MessageBoxController>getController();
            newStage.initModality(m);
            newStage.setTitle(title);
            final Scene scene = new Scene(root, 800, 600);
            newStage.setScene(scene);
            newStage.setResizable(true);
            // TODO: пробовать убрать:
            controller = loader.getController(); // ??

            controller.loadContent(htmlContent);

            Image i = new Image(Main.class.getClassLoader().getResourceAsStream("piggy-bank-icon.png"));
            newStage.getIcons().add(i);
            newStage.show();
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    public static void showLargeMessageBox(String title, String htmlContent) {
        showLargeMessageBox(title, htmlContent, Modality.APPLICATION_MODAL);
    }

    private void checkVatStates() {
        if (vatTableView.getSelectionModel().getSelectedIndex() == -1) {
            MainController.showInfoMessage("", "Не выделено ни одной строки");
            return;
        }
        ObservableList<Integer> selectedIndices = vatTableView.getSelectionModel().getSelectedIndices();
        List<VatData> selectedIssuedRows = selectedIndices.stream().map(ind -> vatData.get(ind)).filter(VatData::isVatIssued).collect(Collectors.toList());
        configureProxy();
        Object[] credentials = chooseCredentialsFromList("Выберите ключ и введите пароль");
        if (credentials[0] == null) {
            return;
        }

        beforeStartLongTask("Чтение статусов счет-фактур с портала...");
        final String keyAlias = (String) credentials[0];
        final String password = (String) credentials[1];

        Thread th = new Thread(new Task() {
            @Override
            protected Integer call() throws Exception {
                String query2 = "update ei_vats set state = :state where id = :id";
                try (VatTool vt = new VatTool(keyAlias, password);
                     Connection conn = ConnectionMySql.getInstance().getConn();
                     NamedParameterStatement nps = new NamedParameterStatement(conn, query2)) {
                    for (VatData vd : selectedIssuedRows) {
                        VatStatusEnum vatStatus = vt.getVatStatus(vd.getVatFullNumber());
                        if (vatStatus.ordinal() != vd.getVatState()) {
                            nps.setInt("state", vatStatus.ordinal());
                            nps.setInt("id", vd.getVatId());
                            boolean success = nps.execute();
                            vd.setVatState(vatStatus.ordinal());
                        }
                    }
                    Platform.runLater(() -> afterFinishLongTask("Чтение статусов завершено успешно"));
                } catch (Exception e) {
                    if (Main.verbose) {
                        log.error(e.getMessage(), e);
                    } else {
                        log.error("[ОШИБКА] " + e.getMessage());
                    }
                    Platform.runLater(() -> MainController.showErrorMessage("Ошибка", ExceptionHelpers.extractMessage(e)));
                    messageText.set("Возникла ошибка");
                } finally {
                    afterFinishLongTask(null);
                }
                return null;
            }
        });
        th.start();
    }

    private void beforeStartLongTask(String message) {
        apMain.setCursor(Cursor.WAIT);
        apMain.setDisable(true);
        gpMessage.setDisable(false);
        gpMessage.setVisible(true);
        apMain.setEffect(createBlurEffect());
        if (message != null) {
            messageText.set(message);
        }
    }

    private void afterFinishLongTask(String message) {
        apMain.setCursor(Cursor.DEFAULT);
        apMain.setDisable(false);
        gpMessage.setDisable(true);
        gpMessage.setVisible(false);
        apMain.setEffect(null);
        if (message != null) {
            messageText.set(message);
        }

    }

    public Scene getScene() {
        return thisScene;
    }

    public void setScene(Scene thisScene) {
        this.thisScene = thisScene;
    }

    private void configureProxy() {
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
            Authenticator.setDefault(null);
            System.clearProperty("https.proxyHost");
            System.clearProperty("https.proxyPort");
            System.clearProperty("https.proxyUser");
            System.clearProperty("https.proxyPass");
        }
    }

    public void bCheckStatesAction(ActionEvent actionEvent) {
        checkVatStates();
    }

    public void miTestAction(ActionEvent actionEvent) {
        showInfoMessage(vatData.get(0).getAgreementNumber() + " " + vatData.get(0).getAgreementDate());
    }

    private Effect createBlurEffect() {
        Effect e;
        ColorAdjust adj = new ColorAdjust(0, -0.9, -0.5, 0);
        GaussianBlur blur = new GaussianBlur(2);
        adj.setInput(blur);
        e = adj;
        return e;
    }

    public void miCheckStatesAction(ActionEvent actionEvent) {
        checkVatStates();
    }

    public void miUploadAction(ActionEvent actionEvent) {
        issueVATS();
    }

    public void miImportRegistryAction(ActionEvent actionEvent) {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);

        FileChooser fc = new FileChooser();
        fc.setTitle("Выберите файл с реестром договоров ");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xls", "*.xlsx"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        final String lastImportDir = prefs.get("lastImportDir", null);
        if (lastImportDir != null) {
            fc.setInitialDirectory(new File(lastImportDir));
        }
        File file = fc.showOpenDialog(Main.getStage());
        if (file == null) {
            return;
        }

        prefs.put("lastImportDir", file.getParentFile().getAbsolutePath());
        try {
            importIntoDB(file);
        } catch (Exception e) {
            showErrorMessage("Ошибка", e.getMessage());
        }
    }

    private void importIntoDB(File file) {
        StringBuilder sbMessages = new StringBuilder();
        final List<AgreementData> agreements = ExcelLoader.loadRegistryFile(file, sbMessages);

        if (sbMessages.length() > 0) {
            showErrorMessage("Ошибки загрузки реестра", sbMessages.toString());
            return;
        }

        String insertQuery = "INSERT INTO ei_agreements (unp, arg_num, agr_date) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionMySql.getInstance().getConn();
             Statement stDelete = conn.createStatement();
             PreparedStatement psInsert = conn.prepareStatement(insertQuery)) {
            conn.setAutoCommit(false);
            stDelete.executeUpdate("DELETE FROM ei_agreements");
            for (AgreementData agreement : agreements) {
                psInsert.setInt(1, agreement.getUnp());
                psInsert.setString(2, agreement.getNumber());
                psInsert.setDate(3, new java.sql.Date(agreement.getDate().getTime()));
                psInsert.addBatch();
            }
            psInsert.executeBatch();
            fillAgreementChoises(conn);
            conn.commit();
            showInfoMessage("Загружено " + agreements.size() + " договоров");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            MainController.showErrorMessage("", e.toString());
        }
    }

}
