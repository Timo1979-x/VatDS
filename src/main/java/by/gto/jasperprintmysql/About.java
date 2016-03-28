package by.gto.jasperprintmysql;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import javax.swing.ImageIcon;

/**
 *
 * @author Aleks
 */
public final class About extends javax.swing.JFrame {

    /**
     * Creates new form About
     */
    public About() {
        initComponents();
        this.setTitle("О программе");
        URL iconURL = getClass().getResource("/piggy-bank-icon.png");
        ImageIcon icon = new ImageIcon(iconURL);
        setIconImage(icon.getImage());
        //Отоброжение по центру экрана
        setLocationRelativeTo(null);
        getAPIVersion();
    }

    public void getAPIVersion() {
//        String path = "/version.prop";
//        InputStream stream = getClass().getResourceAsStream(path);
//        if (stream == null) {
//            //  return "UNKNOWN";
//        }
//        Properties props = new Properties();
//        try {
//            props.load(stream);
//            stream.close();
//            jLabelVer.setText("Версия сборки: " + (String) props.get("version") + ".0 от " + (String) props.get("dateBuild"));
            //Version.getVERSION()
            jLabelVer.setText("Версия сборки: " + Version.getVERSION() + ".0 от " + Version.getDATEBUILD());
//            jLabelName.setText((String) props.get("title"));
//            jLabelCompany.setText((String) props.get("company"));
//        } catch (IOException e) {
//            //   return "UNKNOWN";
//        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelVer = new javax.swing.JLabel();
        jLabelName = new javax.swing.JLabel();
        jLabelEMail = new javax.swing.JLabel();
        jLabelCompany = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jLabelVer.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelVer.setForeground(new java.awt.Color(0, 51, 153));
        jLabelVer.setText("Версия сборки: 0.0.13.0 от 2016.02.23");

        jLabelName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelName.setText("Статистические отчеты");

        jLabelEMail.setText("<html><div align=\"center\">Если у Вас возникнут любые замечания, предложения или вопросы,<br/>пишите на <a href=\"mailto:day_anger@gto.by\">day_anger@gto.by</a></div> ");
        jLabelEMail.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabelEMail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelEMailMouseClicked(evt);
            }
        });

        jLabelCompany.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelCompany.setText("УП \"Белтехосмотр\"");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("<html><div align=\"center\">Автор: Коско Александр Николаевич<br/>Должность: начальник информационно-аналитического отдела</div>");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabelName)
                    .addComponent(jLabelCompany)
                    .addComponent(jLabelVer)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelEMail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelCompany)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelVer, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelEMail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabelEMailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelEMailMouseClicked
        try {
            Desktop.getDesktop().browse(new URI("mailto:day_anger@gto.by?subject=btoReport"));
        } catch (URISyntaxException | IOException ex) {
            //It looks like there's a problem
        }
    }//GEN-LAST:event_jLabelEMailMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelCompany;
    private javax.swing.JLabel jLabelEMail;
    private javax.swing.JLabel jLabelName;
    private javax.swing.JLabel jLabelVer;
    // End of variables declaration//GEN-END:variables
}
