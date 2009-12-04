/*package org.ariadne_eu.utils;

import java.util.Properties;
import java.io.*;
import java.net.URL;

*//**
 * Created by ben
 * Date: 11-feb-2007
 * Time: 13:51:00
 * To change this template use File | Settings | File Templates.
 *//*
public class ConfigReader {
    private static final String DEFAULT_FILE = "metadatastore.properties";
    private static Properties props = new Properties();

    static {
        readConfigFile(null);
    }

    public static InputStream getResource(String fileName) throws IOException {
        URL url = ConfigReader.class.getResource("/" + fileName);
        return url != null ? url.openStream(): null;
    }

    public static OutputStream getResourceOutputStream(String fileName) throws IOException {
        URL url = ConfigReader.class.getResource("/" + fileName);
        return url != null ? new FileOutputStream(url.getPath()): null;
    }

    public static void readConfigFile(String path) {
        try {
            InputStream inputStream;
            if (path == null || path.length() == 0)
                inputStream = getResource(DEFAULT_FILE);
            else
                inputStream = new FileInputStream(path);
            props.load(inputStream);
            Initialize.initializeLogging();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeConfigFile(String path) {
        try {
            OutputStream outputStream;
            if (path == null || path.length() == 0)
                outputStream = getResourceOutputStream(DEFAULT_FILE);
            else
                outputStream = new FileOutputStream(path);
            props.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return (String) props.get(key);
    }
    public static void setProperty(String key, String value) {
        if (value != null) {
            props.put(key, value);
        } else {
            props.remove(key);
        }
    }
}
*/