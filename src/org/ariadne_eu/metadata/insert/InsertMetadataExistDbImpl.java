package org.ariadne_eu.metadata.insert;

import org.apache.log4j.Logger;
import org.ariadne_eu.utils.config.ConfigManager;
import org.ariadne_eu.utils.config.RepositoryConstants;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

/**
 * Created by ben
 * Date: 5-mei-2007
 * Time: 19:05:44
 * To change this template use File | Settings | File Templates.
 */
public class InsertMetadataExistDbImpl extends InsertMetadataImpl {
    private static Logger log = Logger.getLogger(InsertMetadataExistDbImpl.class);

    private Collection collection;

    public InsertMetadataExistDbImpl() {
        initialize();
    }

    public InsertMetadataExistDbImpl(int language) {
        setLanguage(language);
        initialize();
    }

    void initialize() {
        super.initialize();
        try {
            String URI = ConfigManager.getProperty(RepositoryConstants.MD_DB_URI + "." + getLanguage());
            if (URI == null)
                URI = ConfigManager.getProperty(RepositoryConstants.MD_DB_URI);
            try {
//                String driver = ConfigManager.getProperty(RepositoryConstants.MD_DB_DRIVER + "." + getLanguage());
//                if (driver == null)
//                    driver = ConfigManager.getProperty(RepositoryConstants.MD_DB_DRIVER);
//                Class cl = Class.forName(driver);
            	Class cl = Class.forName("org.exist.xmldb.DatabaseImpl");
                Database database = (Database)cl.newInstance();
                DatabaseManager.registerDatabase(database);

                String username = ConfigManager.getProperty(RepositoryConstants.MD_DB_USERNAME + "." + getLanguage());
                if (username == null)
                    username = ConfigManager.getProperty(RepositoryConstants.MD_DB_USERNAME);
                String password = ConfigManager.getProperty(RepositoryConstants.MD_DB_PASSWORD + "." + getLanguage());
                if (password == null)
                    password = ConfigManager.getProperty(RepositoryConstants.MD_DB_PASSWORD);

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

//    private void generateCollection(String URI, String collectionString, String username, String password) {
//        //TODO: auto generate?
//        try {
//            log.info("generateCollection:collection="+collectionString);
//            Collection root = DatabaseManager.getCollection(URI + "/db", username, password);
//            CollectionManagementService mgtService = (CollectionManagementService)
//                root.getService("CollectionManagementService", "1.0");
//            collection = mgtService.createCollection(collectionString.substring("/db".length()));
//            collection = DatabaseManager.getCollection(URI + collectionString, username, password);
//        } catch (XMLDBException e) {
//            log.error("generateCollection: URI=\"" + URI + "\", collection=\"" + collectionString + "\", username=\"" + username + "\", password=\"" + password + "\" ", e);
//        }
//    }

    public void insertMetadata(String identifier, String metadata) {
        try {
        	//exist cant handle ":" on the identifier
        	identifier = identifier.replaceAll(":", "_");
            XMLResource document = (XMLResource)collection.createResource(identifier, "XMLResource");
            document.setContent(metadata);
            collection.storeResource(document);
            log.info("insertMetadata:identifier:\""+identifier+"\"");
        } catch (XMLDBException e) {
            log.error("insertMetadata:identifier:\""+identifier+"\" ", e);
        }
    }
}
