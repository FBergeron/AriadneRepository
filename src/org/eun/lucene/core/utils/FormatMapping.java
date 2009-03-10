package org.eun.lucene.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FormatMapping {
	
    private static Properties format = null;
    
    //"format.properties"
    public static void load(String formatPath) throws IOException {
    	format = new Properties();
    	InputStream in = FormatMapping.class.getClassLoader().getResourceAsStream(formatPath);
    	format.load(in);
    }
  
    public static String getText(String key) {
    	return (format.getProperty(key));
    }
    
    
}
