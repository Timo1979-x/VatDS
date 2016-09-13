/**
 * 
 */
package com.mgrecol.jasper.jasperviewerfx;

import java.util.HashMap;


import javafx.application.Application;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.log4j.Logger;

/**
 * @author  Michael Grecol
 *	@project JasperViewerFx
 * @filename JRViewerMain.java
 * @date Mar 23, 2015
 */
public class JRViewerMain extends Application {
	public static String[] prgArgs;
    private final static Logger log = Logger.getLogger(JRViewerMain.class);
	public static void main(String[] args){
		prgArgs = args;
		launch(args);

		
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		JasperPrint jasperPrint=null;
		try {
			jasperPrint = JasperFillManager.fillReport("target/classes/TestReport.jasper", new HashMap(),  new JREmptyDataSource());
		} catch (JRException e) {
            log.error(e.getMessage(), e);
		};
		JRViewerFx viewer = new JRViewerFx(jasperPrint, JRViewerFxMode.REPORT_VIEW,primaryStage);
		viewer.start(primaryStage);
		
	}
}
