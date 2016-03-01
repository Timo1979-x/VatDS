package by.gto.jasperprintmysql;

import by.gto.jasperprintmysql.data.OwnerDataSW;
import by.gto.tools.ConnectionMySql;
import by.gto.tools.ModalFrameUtil;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author kan
 */
public class MainView extends javax.swing.JFrame {

    private static JFrame f;
    private static final Logger log = LogManager.getLogger(MainView.class);
    private static final long serialVersionUID = 1L;
    private static Date date;

    public static JFrame getF() {
        return f;
    }

    /**
     * Creates new form MainView
     */
    public MainView() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Minsk"));
        LocalDate localDate = LocalDate.now();
        date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        initComponents();
        URL iconURL = getClass().getResource("/piggy-bank-icon.png");
        ImageIcon icon = new ImageIcon(iconURL);
        setIconImage(icon.getImage());
        //Отоброжение по центру экрана
        setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGroupTypeReport = new javax.swing.ButtonGroup();
        btnGroupBankTransfer = new javax.swing.ButtonGroup();
        jPnlDate = new javax.swing.JPanel();
        jChBoxPeriod = new javax.swing.JCheckBox();
        jLOver = new javax.swing.JLabel();
        datePickerStart = new com.michaelbaranov.microba.calendar.DatePicker();
        jLBefore = new javax.swing.JLabel();
        datePickerEnd = new com.michaelbaranov.microba.calendar.DatePicker();
        jPnlTypeReport = new javax.swing.JPanel();
        jRBforBTO = new javax.swing.JRadioButton();
        jRBforSlutsk = new javax.swing.JRadioButton();
        jRBRecordBook = new javax.swing.JRadioButton();
        jRBIndividual = new javax.swing.JRadioButton();
        jRBCorparate = new javax.swing.JRadioButton();
        jRBActiv = new javax.swing.JRadioButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jPnlOptions = new javax.swing.JPanel();
        jChBoxCorparate = new javax.swing.JCheckBox();
        jChBoxIndividual = new javax.swing.JCheckBox();
        jChBoxOwner = new javax.swing.JCheckBox();
        jCBoxOwner = new javax.swing.JComboBox<>();
        jLabUNP = new javax.swing.JLabel();
        jCBoxUNP = new javax.swing.JComboBox<>();
        jChBoxBankTransfer = new javax.swing.JCheckBox();
        jRBtnBBankTransferAll = new javax.swing.JRadioButton();
        jRBtnBBankTransferFalse = new javax.swing.JRadioButton();
        jRBtnBBankTransferTrue = new javax.swing.JRadioButton();
        jBtnShowReport = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuISettings = new javax.swing.JMenuItem();
        jMenuIExit = new javax.swing.JMenuItem();
        jMenuAbout = new javax.swing.JMenu();
        jMenuItemUpdate = new javax.swing.JMenuItem();
        jMenuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Отчеты для ДС");
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(345, 326));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                winExit(evt);
            }
        });

        jPnlDate.setBorder(javax.swing.BorderFactory.createTitledBorder("Дата"));

        jChBoxPeriod.setText("за период");
        jChBoxPeriod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jChBoxPeriodActionPerformed(evt);
            }
        });

        jLOver.setText("за");

        try {
            datePickerStart.setDate(date);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }

        jLBefore.setText("по");
        jLBefore.setEnabled(false);

        try {
            datePickerEnd.setDate(date);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        datePickerEnd.setEnabled(false);

        javax.swing.GroupLayout jPnlDateLayout = new javax.swing.GroupLayout(jPnlDate);
        jPnlDate.setLayout(jPnlDateLayout);
        jPnlDateLayout.setHorizontalGroup(
            jPnlDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlDateLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPnlDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jChBoxPeriod)
                    .addComponent(jLOver)
                    .addComponent(datePickerStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLBefore)
                    .addComponent(datePickerEnd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPnlDateLayout.setVerticalGroup(
            jPnlDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlDateLayout.createSequentialGroup()
                .addComponent(jChBoxPeriod)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLOver)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(datePickerStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLBefore)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(datePickerEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPnlTypeReport.setBorder(javax.swing.BorderFactory.createTitledBorder("Тип отчета"));

        btnGroupTypeReport.add(jRBforBTO);
        jRBforBTO.setText("для БТО");
        jRBforBTO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBforBTOActionPerformed(evt);
            }
        });

        btnGroupTypeReport.add(jRBforSlutsk);
        jRBforSlutsk.setText("для Слуцка");
        jRBforSlutsk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBforSlutskActionPerformed(evt);
            }
        });

        btnGroupTypeReport.add(jRBRecordBook);
        jRBRecordBook.setSelected(true);
        jRBRecordBook.setText("Журнал");
        jRBRecordBook.setToolTipText("");
        jRBRecordBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBRecordBookActionPerformed(evt);
            }
        });

        btnGroupTypeReport.add(jRBIndividual);
        jRBIndividual.setText("Физ. лица");
        jRBIndividual.setToolTipText("");
        jRBIndividual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBIndividualActionPerformed(evt);
            }
        });

        btnGroupTypeReport.add(jRBCorparate);
        jRBCorparate.setText("Юр. лица");
        jRBCorparate.setToolTipText("");
        jRBCorparate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBCorparateActionPerformed(evt);
            }
        });

        btnGroupTypeReport.add(jRBActiv);
        jRBActiv.setText("Активные");
        jRBActiv.setToolTipText("");
        jRBActiv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBActivActionPerformed(evt);
            }
        });

        btnGroupTypeReport.add(jRadioButton1);
        jRadioButton1.setText("по ДК");
        jRadioButton1.setEnabled(false);

        btnGroupTypeReport.add(jRadioButton3);
        jRadioButton3.setText("для БТО??");
        jRadioButton3.setEnabled(false);

        btnGroupTypeReport.add(jRadioButton5);
        jRadioButton5.setText("для БТО");
        jRadioButton5.setEnabled(false);

        javax.swing.GroupLayout jPnlTypeReportLayout = new javax.swing.GroupLayout(jPnlTypeReport);
        jPnlTypeReport.setLayout(jPnlTypeReportLayout);
        jPnlTypeReportLayout.setHorizontalGroup(
            jPnlTypeReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlTypeReportLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPnlTypeReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRBforBTO)
                    .addComponent(jRadioButton3)
                    .addComponent(jRBforSlutsk)
                    .addComponent(jRadioButton5)
                    .addComponent(jRadioButton1))
                .addGap(18, 18, 18)
                .addGroup(jPnlTypeReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRBCorparate)
                    .addComponent(jRBRecordBook)
                    .addComponent(jRBIndividual)
                    .addComponent(jRBActiv))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        jPnlTypeReportLayout.setVerticalGroup(
            jPnlTypeReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlTypeReportLayout.createSequentialGroup()
                .addGroup(jPnlTypeReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRBRecordBook)
                    .addComponent(jRBforBTO, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPnlTypeReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRBIndividual)
                    .addComponent(jRBforSlutsk))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPnlTypeReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRBCorparate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPnlTypeReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRBActiv, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton5)
                .addGap(0, 9, Short.MAX_VALUE))
        );

        jPnlOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Параметры"));

        jChBoxCorparate.setSelected(true);
        jChBoxCorparate.setText("Юр. лица");

        jChBoxIndividual.setSelected(true);
        jChBoxIndividual.setText("Физ. лица");

        jChBoxOwner.setText("Собственник");
        jChBoxOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jChBoxOwnerActionPerformed(evt);
            }
        });

        jCBoxOwner.setEnabled(false);
        jCBoxOwner.setMaximumSize(new java.awt.Dimension(256, 20));
        jCBoxOwner.setMinimumSize(new java.awt.Dimension(256, 20));
        jCBoxOwner.setPreferredSize(new java.awt.Dimension(256, 20));

        jLabUNP.setText("УНП");
        jLabUNP.setEnabled(false);

        jCBoxUNP.setToolTipText("");
        jCBoxUNP.setEnabled(false);
        jCBoxUNP.setMaximumSize(new java.awt.Dimension(256, 20));
        jCBoxUNP.setMinimumSize(new java.awt.Dimension(256, 20));
        jCBoxUNP.setPreferredSize(new java.awt.Dimension(256, 20));

        jChBoxBankTransfer.setText("Безнал");

        btnGroupBankTransfer.add(jRBtnBBankTransferAll);
        jRBtnBBankTransferAll.setSelected(true);
        jRBtnBBankTransferAll.setText("Нал/Безнал");

        btnGroupBankTransfer.add(jRBtnBBankTransferFalse);
        jRBtnBBankTransferFalse.setText("Нал");

        btnGroupBankTransfer.add(jRBtnBBankTransferTrue);
        jRBtnBBankTransferTrue.setText("Безнал");

        javax.swing.GroupLayout jPnlOptionsLayout = new javax.swing.GroupLayout(jPnlOptions);
        jPnlOptions.setLayout(jPnlOptionsLayout);
        jPnlOptionsLayout.setHorizontalGroup(
            jPnlOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPnlOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPnlOptionsLayout.createSequentialGroup()
                        .addComponent(jChBoxCorparate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jChBoxIndividual)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jChBoxBankTransfer))
                    .addComponent(jChBoxOwner)
                    .addComponent(jCBoxOwner, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabUNP)
                    .addComponent(jCBoxUNP, 0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPnlOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRBtnBBankTransferFalse)
                    .addComponent(jRBtnBBankTransferAll)
                    .addComponent(jRBtnBBankTransferTrue))
                .addContainerGap())
        );
        jPnlOptionsLayout.setVerticalGroup(
            jPnlOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlOptionsLayout.createSequentialGroup()
                .addGroup(jPnlOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jChBoxCorparate)
                    .addComponent(jChBoxIndividual)
                    .addComponent(jChBoxBankTransfer)
                    .addComponent(jRBtnBBankTransferAll))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPnlOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jChBoxOwner)
                    .addComponent(jRBtnBBankTransferFalse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPnlOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCBoxOwner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRBtnBBankTransferTrue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabUNP)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCBoxUNP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jBtnShowReport.setText("Просмотр");
        jBtnShowReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnShowReportActionPerformed(evt);
            }
        });

        jMenuFile.setText("Файл");

        jMenuISettings.setText("Настройки");
        jMenuISettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuISettingsActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuISettings);

        jMenuIExit.setText("Выход");
        jMenuIExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuIExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuIExit);

        jMenuBar1.add(jMenuFile);

        jMenuAbout.setText("?");

        jMenuItemUpdate.setText("Проверить обновления");
        jMenuItemUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUpdateActionPerformed(evt);
            }
        });
        jMenuAbout.add(jMenuItemUpdate);

        jMenuItemAbout.setText("О программе");
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuAbout.add(jMenuItemAbout);

        jMenuBar1.add(jMenuAbout);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPnlDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPnlTypeReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jBtnShowReport)
                    .addComponent(jPnlOptions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPnlDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPnlTypeReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPnlOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBtnShowReport)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private String report = "recordBook";

    private void jBtnShowReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnShowReportActionPerformed
        jBtnShowReport.setEnabled(false);

        List<Integer> ownerType = new ArrayList<>();
        if (jChBoxCorparate.isSelected()) {
            ownerType.add(2);
            ownerType.add(3);
        }
        if (jChBoxIndividual.isSelected()) {
            ownerType.add(1);
        }
        if (ownerType.isEmpty()) {
            ownerType.add(1);
            ownerType.add(2);
            ownerType.add(3);
        }
        String owner = null;
        String ownerUNP = null;
        
        if (jChBoxOwner.isSelected()) {
            if (jCBoxOwner.getSelectedItem() != null) {
                // int ownerNum = jCBoxOwner.getSelectedIndex();//   getSelectedItem().toString();//selectedItemReminder
                owner = (String) jCBoxOwner.getSelectedItem();//listOwner.get(ownerNum);
                owner = owner.trim().replaceAll("(^.*\")(.+)(\".*$)", "$2");
                owner = owner.replaceAll("\"", "_");
                owner = owner.replaceAll("\\s+", "%");
                System.out.println(String.format("----------owner--------%s", owner));
            }
            if (jCBoxUNP.getSelectedItem() != null) {
                // int ownerNumUNP = jCBoxUNP.getSelectedIndex();
                ownerUNP = (String) jCBoxUNP.getSelectedItem(); //listOwnerUNP.get(ownerNumUNP);
                ownerUNP = ownerUNP.trim();
                System.out.println(String.format("--------ownerUNP----------%s", ownerUNP));
            }
        }
//        System.out.println(String.format("Date time: %s", dt));
//        System.out.println(String.format("Start: %s", startMonth));
//        System.out.println(String.format("End: %s", endMonth));
//        String monthName = dt.monthOfYear().getAsText();
//        String frenchShortName = dt.monthOfYear().getAsShortText(Locale.ROOT);
//        boolean isLeapYear = dt.year().isLeap();
//        DateTime rounded = dt.dayOfMonth().roundFloorCopy();
//        System.out.println(String.format("monthName: %s", monthName));
//        System.out.println(String.format("frenchShortName: %s", frenchShortName));
//        System.out.println(String.format("isLeapYear: %s", isLeapYear));
//        System.out.println(String.format("rounded: %s", rounded));
//        try {
//            datePicker1.setDate(startMonth.toDate());
//            datePicker2.setDate(endMonth.toDate());
//        } catch (PropertyVetoException ex) {
//            Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
//        }

        byte bankTransfer = (byte) (this.jChBoxBankTransfer.isSelected() ? 1 : 0);

        LocalDateTime localDateStart = LocalDateTime.ofInstant(datePickerStart.getDate().toInstant(), ZoneId.systemDefault());
        LocalDateTime localDateStop = LocalDateTime.ofInstant(datePickerEnd.getDate().toInstant(), ZoneId.systemDefault());
        //  DateTime dtStart = new DateTime(datePickerStart.getDate()).withZone(DateTimeZone.forID("Europe/Minsk"));
        // DateTime dtEnd = new DateTime(datePickerEnd.getDate()).withZone(DateTimeZone.forID("Europe/Minsk"));
        //System.out.println(String.format("dtStart: %s", dtStart.millisOfDay().setCopy(1).plusDays(1).minusSeconds(1)));

        if (datePickerEnd.isEnabled()) {
            App.print(localDateStart, localDateStop, report, ownerType, owner, ownerUNP, bankTransfer);
        } else {
            App.print(localDateStart, localDateStart, report, ownerType, owner, ownerUNP, bankTransfer);
        }
        jBtnShowReport.setEnabled(true);
    }//GEN-LAST:event_jBtnShowReportActionPerformed

    private void jChBoxPeriodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jChBoxPeriodActionPerformed
        if (jChBoxPeriod.isSelected()) {
            jLOver.setText("c");
            datePickerEnd.setEnabled(true);
            jLBefore.setEnabled(true);
        } else {
            jLOver.setText("за");
            datePickerEnd.setEnabled(false);
            jLBefore.setEnabled(false);
        }
    }//GEN-LAST:event_jChBoxPeriodActionPerformed
    //  List<String> listOwner = new ArrayList<>();
    //  List<String> listOwnerUNP = new ArrayList<>();
//    AutoCompleteSupport support = null;
//    AutoCompleteSupport UNP = null;

    // OwnerDataSW ownerDataSW = null;
    private void jChBoxOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jChBoxOwnerActionPerformed
        OwnerDataSW ownerDataSW = new OwnerDataSW(jCBoxOwner, jCBoxUNP, jChBoxOwner, jLabUNP);
//        if (ownerDataSW == null) {
//            ownerDataSW = new OwnerDataSW(jComboBox1, jComboBox2, jCheckBox4, jLabel3);
//        }
        ownerDataSW.execute();

        /*
         if (jCheckBox4.isSelected()) {
         //getInputContext().selectInputMethod(new Locale("ru","RU"));
         jComboBox1.setEnabled(true);
         jComboBox2.setEnabled(true);
         jLabel3.setEnabled(true);
         jComboBox1.getInputContext().selectInputMethod(new Locale("ru", "RU"));
            
         String Query = "SELECT owner_info.id_owner, UPPER(owner_info.`name`), owner_info.`unp` FROM owner_info GROUP BY owner_info.`name` ORDER BY owner_info.`name`";
         try {
         try (Connection conn = ConnectionMySql.getInstance().getConn(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(Query)) {
         while (rs.next()) {
         listOwner.add(rs.getString(2).trim());
         if (rs.getString(3) != null) {
         listOwnerUNP.add(rs.getString(3).trim());
         }
         }
         }
         } catch (SQLException ex) {
         log.error(ex);
         }
             

         final EventList<String> owners = GlazedLists.eventList(listOwner);
         final EventList<String> ownersUNP = GlazedLists.eventList(listOwnerUNP);
         support = AutoCompleteSupport.install(jComboBox1, owners);
         UNP = AutoCompleteSupport.install(jComboBox2, ownersUNP);
         support.setFilterMode(TextMatcherEditor.CONTAINS);
         UNP.setFilterMode(TextMatcherEditor.CONTAINS);
             
         } else {
         support.uninstall();
         UNP.uninstall();
         jComboBox1.setEnabled(false);
         jComboBox2.setEnabled(false);
         jLabel3.setEnabled(false);
         }
         */
    }//GEN-LAST:event_jChBoxOwnerActionPerformed
    private void winExit(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_winExit
        ConnectionMySql.getInstance().destroy();
    }//GEN-LAST:event_winExit

    private void jRBCorparateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBCorparateActionPerformed
        jChBoxIndividual.setSelected(false);
        jChBoxCorparate.setSelected(true);
        report = "corporatePerson";
    }//GEN-LAST:event_jRBCorparateActionPerformed

    private void jRBRecordBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBRecordBookActionPerformed
        jChBoxCorparate.setSelected(true);
        jChBoxIndividual.setSelected(true);
        report = "recordBook";
    }//GEN-LAST:event_jRBRecordBookActionPerformed

    private void jRBIndividualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBIndividualActionPerformed
        jChBoxCorparate.setSelected(false);
        jChBoxIndividual.setSelected(true);
        report = "listIndividual";
    }//GEN-LAST:event_jRBIndividualActionPerformed

    private void jRBforSlutskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBforSlutskActionPerformed
        jChBoxCorparate.setSelected(true);
        jChBoxIndividual.setSelected(true);
        report = "forSlutsk";
    }//GEN-LAST:event_jRBforSlutskActionPerformed

    private void jRBforBTOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBforBTOActionPerformed
        jChBoxCorparate.setSelected(true);
        jChBoxIndividual.setSelected(true);
        report = "forBTO";
    }//GEN-LAST:event_jRBforBTOActionPerformed

    private void jRBActivActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBActivActionPerformed
        report = "forDS210";
        jChBoxCorparate.setSelected(true);
        jChBoxIndividual.setSelected(true);
    }//GEN-LAST:event_jRBActivActionPerformed

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
        About about = new About();
        ModalFrameUtil.showAsModal(about, this, JFrame.NORMAL);
        this.toFront();
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    private void jMenuISettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuISettingsActionPerformed
        Settings settings = new Settings();
        ModalFrameUtil.showAsModal(settings, this, JFrame.NORMAL);
        this.toFront();
    }//GEN-LAST:event_jMenuISettingsActionPerformed

    private void jMenuItemUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUpdateActionPerformed
        JOptionPane.showMessageDialog(null, new File(MainView.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent(), "Path", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenuItemUpdateActionPerformed

    private void jMenuIExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuIExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuIExitActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        log.info("Start");
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(() -> {
            f = new MainView();
            getF().setVisible(true);
        });

        log.info("Stop");
    }
    private Task task;

    private class Task extends SwingWorker<Void, Void> {

        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {

//
//            int progress = 0;
//            double progress2;
//            //Initialize progress property.
//            setProgress(progress);
//            String filePathGood = dir + "/Good/";
//            String filePathError = dir + "/Error/";
//            String[] list = ReadFilesName.readFilesName(dir, ".+\\.dxl");
//            if (list.length > 0) {
//                for (int i = 0; list.length > i; i++) {
//                    progress2 = 100.0 / list.length * i;
//                    progress = (int) progress2;
//                    setProgress(progress);
//                    PassEntity test = ValidateXmlDTD.validate(dir + "/" + list[i]);
//                    if (test.isValid()) {
//                        if (test.getDate().size() == 1) {
//                            MoveFileAndDirectory.move(dir + "/" + list[i], filePathGood + test.getDate().first() + "/" + list[i]);
//                            taskOutput.append(list[i] + " - good, " + test.getDate().first() + ", count: " + test.getCount() + ".\n");
//                        } else {
//                            MoveFileAndDirectory.move(dir + "/" + list[i], filePathError + "/несколько месяцев/" + list[i]);
//                            taskOutput.append(list[i] + " - несколько месяцев, count: " + test.getCount() + ".\n");
//                        }
//                    } else {
//                        MoveFileAndDirectory.move(dir + "/" + list[i], filePathError + "/файл поврежден/" + list[i]);
//                        taskOutput.append(list[i] + " - файл поврежден, count: " + test.getCount() + ".\n");
//                    }
//                }
//            } else {
//                taskOutput.append("Указанная директория не содержит файлоы с расширением dxl.\n");
//            }
//            setProgress(100);
            return null;
        }

        /*
         * Executed in event dispatch thread
         */
        @Override
        public void done() {
            //   Toolkit.getDefaultToolkit().beep();
            //  startButton.setEnabled(true);
            // taskOutput.append("Done!\n");
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btnGroupBankTransfer;
    private javax.swing.ButtonGroup btnGroupTypeReport;
    private com.michaelbaranov.microba.calendar.DatePicker datePickerEnd;
    private com.michaelbaranov.microba.calendar.DatePicker datePickerStart;
    private javax.swing.JButton jBtnShowReport;
    private javax.swing.JComboBox<String> jCBoxOwner;
    private javax.swing.JComboBox<Integer> jCBoxUNP;
    private javax.swing.JCheckBox jChBoxBankTransfer;
    private javax.swing.JCheckBox jChBoxCorparate;
    private javax.swing.JCheckBox jChBoxIndividual;
    private javax.swing.JCheckBox jChBoxOwner;
    private javax.swing.JCheckBox jChBoxPeriod;
    private javax.swing.JLabel jLBefore;
    private javax.swing.JLabel jLOver;
    private javax.swing.JLabel jLabUNP;
    private javax.swing.JMenu jMenuAbout;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuIExit;
    private javax.swing.JMenuItem jMenuISettings;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemUpdate;
    private javax.swing.JPanel jPnlDate;
    private javax.swing.JPanel jPnlOptions;
    private javax.swing.JPanel jPnlTypeReport;
    private javax.swing.JRadioButton jRBActiv;
    private javax.swing.JRadioButton jRBCorparate;
    private javax.swing.JRadioButton jRBIndividual;
    private javax.swing.JRadioButton jRBRecordBook;
    private javax.swing.JRadioButton jRBforBTO;
    private javax.swing.JRadioButton jRBforSlutsk;
    private javax.swing.JRadioButton jRBtnBBankTransferAll;
    private javax.swing.JRadioButton jRBtnBBankTransferFalse;
    private javax.swing.JRadioButton jRBtnBBankTransferTrue;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton5;
    // End of variables declaration//GEN-END:variables
}
