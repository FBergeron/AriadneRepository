package org.ariadne_eu.content.retrieve;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.log4j.Logger;
import org.ariadne_eu.utils.config.ConfigManager;
import org.ariadne_eu.utils.config.RepositoryConstants;

import com.ibm.db2.jcc.DB2Xml;

/**
 * Created by ben
 * Date: 3-mrt-2007
 * Time: 15:23:30
 * To change this template use File | Settings | File Templates.
 */
public class RetrieveContentIBMDB2DbImpl extends RetrieveContentImpl {

    private static Logger log = Logger.getLogger(RetrieveContentIBMDB2DbImpl.class);

    private String tableName;
    private String columnName;
    private String identifierColumnName;
    
    private static final int DATA_BLOCK_SIZE = 1024;


    public RetrieveContentIBMDB2DbImpl() {
        initialize();
    }

    void initialize() {
        super.initialize();
        try {
//            String driver = ConfigManager.getProperty(RepositoryConstants.CNT_DB_DRIVER);
//            Class.forName(driver);
        	Class.forName("com.ibm.db2.jcc.DB2Driver");
            //TODO: auto generate?
//                if(collection == null)
//                    generateCollection(URI, collectionString, username, password);
            tableName = ConfigManager.getProperty(RepositoryConstants.CNT_DB_XMLDB_SQL_TABLENAME);
            if (tableName == null)
                tableName = "Contentstore";
            columnName = ConfigManager.getProperty(RepositoryConstants.CNT_DB_XMLDB_SQL_COLUMNNAME);
            if (columnName == null)
                columnName = "contentxml";
            identifierColumnName = ConfigManager.getProperty(RepositoryConstants.CNT_DB_XMLDB_SQL_IDCOLUMNNAME);
            if (identifierColumnName == null)
                identifierColumnName = "GLOBAL_IDENTIFIER";
        } catch (ClassNotFoundException e) {
            log.error("initialize: ", e);
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
        String metadata = getMetadataForID(identifier);
        if (metadata == null)
            return null;
        File file = getFileFromMetadata(metadata);
        if (file == null || !file.exists()) {
            return null;
        }
        return new DataHandler(new FileDataSource(file));
    }
    
    public String retrieveFileName(String identifier) {
    	String metadata = getMetadataForID(identifier);
        if (metadata == null)
            return null;
        String fileName = getFileNameFromMetadata(metadata);
        return fileName;
    }
    
    public String retrieveFileType(String identifier) {
    	String metadata = getMetadataForID(identifier);
        if (metadata == null)
            return null;
        String fileType = getFileTypeFromMetadata(metadata);
        return fileType;
    }

    private String getMetadataForID(String identifier) {
        PreparedStatement pstmt = null;
        Connection con = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement("SELECT "+columnName+" FROM "+tableName+" WHERE "+identifierColumnName+" = ?");
            pstmt.setString(1, identifier);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DB2Xml xml = (DB2Xml) rs.getObject(1);
                return xml.getDB2String();
            }
        } catch (SQLException e) {
            log.error("getMetadataForID:identifier=" + identifier, e);
        } finally {
            try {
                pstmt.close();
                con.close();
            } catch (Exception e) {
                log.error("getMetadataForID:identifier=" + identifier, e);
            }
        }
        return null;
    }

//    private static File getFileFromMetadata(String metadata) {
//        int start = metadata.indexOf("<fullpath>") + "<fullpath>".length();
//        int end = metadata.indexOf("</fullpath>");
//        String filename = metadata.substring(start, end);
//        return new File(filename);
//    }
    
    private static File getFileFromMetadata(String metadata) {
      int start = metadata.indexOf("<fullpath>") + "<fullpath>".length();
      int end = metadata.indexOf("</fullpath>");
      String filename = metadata.substring(start, end);
      
      return new File(filename);
      }
    
    private static String getFileNameFromMetadata(String metadata) {
        int start = metadata.indexOf("<filename>") + "<filename>".length();
        int end = metadata.indexOf("</filename>");
        String fileName = metadata.substring(start, end);
        
        return fileName;
    }
    
    private static String getFileTypeFromMetadata(String metadata) {
        int start = metadata.indexOf("<filetype>") + "<filetype>".length();
        int end = metadata.indexOf("</filetype>");
        String fileType = metadata.substring(start, end);
        
        return fileType;
    }
    
    
//    private static void getFileFromZip(String inFilename) {
//    	FileInputStream fis = null;
//		ZipInputStream zis;
//		FileOutputStream fos = null;
//		BufferedOutputStream bos;
//		ZipEntry zipEntry = null;
//		String entryName = null;
//
//		try {
//			fis = new FileInputStream(inFilename);
//			zis = new ZipInputStream(fis);
//			
//			while ((zipEntry = zis.getNextEntry()) != null) {
//				entryName = zipEntry.getName();
//				try {
//					fos = new FileOutputStream(entryName);
//				} catch (FileNotFoundException e) {
//					// the directory is not created...so let's build it!
//					buildDirectory(entryName);
//					fos = new FileOutputStream(entryName);
//				}
//				bos = new BufferedOutputStream(fos, DATA_BLOCK_SIZE);
//				int byteCount;
//				byte data[] = new byte[DATA_BLOCK_SIZE];
//				while ( (byteCount = zis.read(data, 0, DATA_BLOCK_SIZE)) != -1) {
//					bos.write(data, 0, byteCount);
//				}
//				bos.flush();
//				bos.close();
//			}
//			zis.close();
//		} catch (IOException e) {
//			log.error("getFileFromZip:inFileName=" + inFilename, e);
//		}
//
//	}
    
//    private static void buildDirectory(String entryName) throws IOException {
//      StringTokenizer st = new StringTokenizer(entryName, "/");
//
//      int levels = st.countTokens() - 1;
//      StringBuffer directory = new StringBuffer();
//      File newDir;
//
//      for (int i=0; i < levels; i++) {
//    	  directory.append(st.nextToken() + "/");
//      }
//
//      newDir = new File(directory.toString());
//      newDir.mkdirs();
//
//    }


    private Connection getConnection() throws SQLException {
        String URI = ConfigManager.getProperty(RepositoryConstants.CNT_DB_URI);
        String username = ConfigManager.getProperty(RepositoryConstants.CNT_DB_USERNAME);
        String password = ConfigManager.getProperty(RepositoryConstants.CNT_DB_PASSWORD);
        return DriverManager.getConnection(URI,username, password);
    }
}
