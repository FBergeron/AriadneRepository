/*package org.ariadne_eu.utils;

import java.util.Properties;

import org.ariadne_eu.config.ConfigManager;
import org.ariadne_eu.metadata.insert.InsertMetadataFactory;
import org.ariadne_eu.metadata.language.TranslateLanguage;
import org.ariadne_eu.metadata.query.QueryFactory;

*//**
 * Created by ben
 * Date: 24-mrt-2007
 * Time: 14:44:27
 * To change this template use File | Settings | File Templates.
 *//*
public class Initialize {
    public static void initializeConfigFile() {
        ConfigManager.readConfigFile(null);
    }

    public static void initializeServices() {
        //TODO
        TranslateLanguage.initialize();
        InsertMetadataFactory.initialize();
        QueryFactory.initialize();
    }

    public static void initializeLogging() {
        String log4j = ConfigManager.getProperty("log4j.properties");
        if (log4j != null) {
            org.apache.log4j.PropertyConfigurator.configure(log4j);
            return;
        }
        log4j = Initialize.class.getResource("/log4j.properties").toExternalForm();
        if (log4j.startsWith("file:"))
            log4j = log4j.substring(5);
        if (log4j != null)
            org.apache.log4j.PropertyConfigurator.configure(log4j);
        else {
            Properties props = new Properties();
            // Set root category priority to INFO and its only appender to A1.
            props.put("log4j.rootCategory", "INFO, A1");

            // A1 is set to be a RollingFileAppender.
            props.put("log4j.appender.A1", "org.apache.log4j.RollingFileAppender");
            String logfile = Initialize.class.getResource("/").toExternalForm() + "repository.log";
            if (logfile.startsWith("file:"))
                logfile = logfile.substring(5);
            props.put("log4j.appender.A1.File", logfile);

            // Roll when log file size is over 1Mb
            props.put("log4j.appender.A1.MaxFileSize", "1048576");

            // Keep up to 500 files around
            props.put("log4j.appender.A1.MaxBackupIndex", "500");

            // KA1 uses PatternLayout.
            props.put("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
            props.put("log4j.appender.A1.layout.ConversionPattern", "%d %-5p %c @ %m%n");

            org.apache.log4j.PropertyConfigurator.configure(props);
        }
    }
}
*/