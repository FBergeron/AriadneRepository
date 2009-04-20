package org.ariadne_eu.content.retrieve;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.transform.OutputKeys;

import org.apache.log4j.Logger;
import org.ariadne_eu.utils.config.ConfigManager;
import org.ariadne_eu.utils.config.RepositoryConstants;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

/**
 * Created by ben
 * Date: 3-mrt-2007
 * Time: 15:23:30
 * To change this template use File | Settings | File Templates.
 */
public class RetrieveContentExistDbImpl extends RetrieveContentImpl {

    private static Logger log = Logger.getLogger(RetrieveContentExistDbImpl.class);

    private Collection collection;

    public RetrieveContentExistDbImpl() {
        initialize();
    }

    void initialize() {
        super.initialize();
        try {
            String URI = ConfigManager.getProperty(RepositoryConstants.CNT_DB_URI);
            try {
//                String driver = ConfigManager.getProperty(RepositoryConstants.CNT_DB_DRIVER);
//                Class cl = Class.forName(driver);
            	Class cl = Class.forName("org.exist.xmldb.DatabaseImpl");
                Database database = (Database)cl.newInstance();
                DatabaseManager.registerDatabase(database);

                String username = ConfigManager.getProperty(RepositoryConstants.CNT_DB_USERNAME);
                String password = ConfigManager.getProperty(RepositoryConstants.CNT_DB_PASSWORD);

                collection = DatabaseManager.getCollection(URI, username, password);
                //TODO: auto generate?
//                if(collection == null)
//                    generateCollection(URI, collectionString, username, password);
            } catch (ClassNotFoundException e) {
                log.error("initialize: ", e);
            } catch (InstantiationException e) {
                log.error("initialize: ", e);
            } catch (IllegalAccessException e) {
                log.error("initialize: ", e);
            } catch (XMLDBException e) {
                //TODO: auto generate?
//                generateCollection(URI, collectionString, username, password);
            }
        } catch (Throwable t) {
            log.error("initialize: ", t);
        }
    }

//    private static void generateCollection(String URI, String collectionString, String username, String password) {
//        //TODO: auto generate?
//        try {
//            Collection root = DatabaseManager.getCollection(URI + "/db", username, password);
//            CollectionManagementService mgtService = (CollectionManagementService)
//                root.getService("CollectionManagementService", "1.0");
//            collection = mgtService.createCollection(collectionString.substring("/db".length()));
//            collection = DatabaseManager.getCollection(URI + collectionString, username, password);
//        } catch (XMLDBException e1) {
//            e1.printStackTrace();
//        }
//    }


    public DataHandler retrieveContent(String identifier) {
    	initialize();
        String metadata = getMetadataForID(identifier);
        if (metadata == null)
            return null;
        File file = getFileFromMetadata(metadata);
        if (file == null || !file.exists()) {
            return null;
        }
        return new DataHandler(new FileDataSource(file));
    }
    
//    public String retrieveFileName(String identifier) {
//    	String metadata = getMetadataForID(identifier);
//        if (metadata == null)
//            return null;
//        String fileName = getFileNameFromMetadata(metadata);
//        return fileName;
//    }
//    
//    public String retrieveFileType(String identifier) {
//    	String metadata = getMetadataForID(identifier);
//        if (metadata == null)
//            return null;
//        String fileType = getFileTypeFromMetadata(metadata);
//        return fileType;
//    }

    private String getMetadataForID(String identifier) {
        String metadata = null;
        try {
            //retrieve document with given ID
            collection.setProperty(OutputKeys.INDENT, "no");
            XMLResource res = (XMLResource)collection.getResource(identifier);
            if (res != null) {
                metadata = (String) res.getContent();
            }
        } catch (XMLDBException e) {
            e.printStackTrace();
        }
        return metadata;
    }

    private static File getFileFromMetadata(String metadata) {
        int start = metadata.indexOf("<fullpath>") + "<fullpath>".length();
        int end = metadata.indexOf("</fullpath>");
        String filename = metadata.substring(start, end);
        return new File(filename);
    }
    
//    private static String getFileNameFromMetadata(String metadata) {
//        int start = metadata.indexOf("<filename>") + "<filename>".length();
//        int end = metadata.indexOf("</filename>");
//        String fileName = metadata.substring(start, end);
//        
//        return fileName;
//    }
//    
//    private static String getFileTypeFromMetadata(String metadata) {
//        int start = metadata.indexOf("<filetype>") + "<filetype>".length();
//        int end = metadata.indexOf("</filetype>");
//        String fileType = metadata.substring(start, end);
//        
//        return fileType;
//    }
}
