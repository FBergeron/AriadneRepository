package org.ariadne_eu.metadata.delete;

import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import oracle.sql.CLOB;

import org.apache.log4j.Logger;
import org.ariadne_eu.utils.config.ConfigManager;
import org.ariadne_eu.utils.config.RepositoryConstants;

/**
 * Created by ben
 * Date: 5-mei-2007
 * Time: 19:05:44
 * To change this template use File | Settings | File Templates.
 */
public class InsertMetadataOracleDbImpl extends DeleteMetadataImpl {
    private static Logger log = Logger.getLogger(InsertMetadataOracleDbImpl.class);

    private String tableName;
    private String columnName;
    private String identifierColumnName;

    public InsertMetadataOracleDbImpl() {
    }

    public InsertMetadataOracleDbImpl(int implementation) {
        setImplementation(implementation);
        initialize();
    }

    void initialize() {
        super.initialize();
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            tableName = ConfigManager.getProperty(RepositoryConstants.MD_DB_XMLDB_SQL_TABLENAME);
            if (tableName == null)
                tableName = "Metadatastore";
            columnName = ConfigManager.getProperty(RepositoryConstants.MD_DB_XMLDB_SQL_COLUMNNAME);
            if (columnName == null)
                columnName = "lomxml";
            identifierColumnName = ConfigManager.getProperty(RepositoryConstants.MD_DB_XMLDB_SQL_IDCOLUMNNAME);
            if (identifierColumnName == null)
                identifierColumnName = "GLOBAL_IDENTIFIER";
        } 
        catch (Throwable t) {
            log.error("initialize: ", t);
        }
    }

    private Connection getConnection() throws SQLException {
        String URI = ConfigManager.getProperty(RepositoryConstants.MD_DB_URI + "." + getImplementation());
        if (URI == null)
            URI = ConfigManager.getProperty(RepositoryConstants.MD_DB_URI);
        String username = ConfigManager.getProperty(RepositoryConstants.MD_DB_USERNAME + "." + getImplementation());
        if (username == null)
            username = ConfigManager.getProperty(RepositoryConstants.MD_DB_USERNAME);
        String password = ConfigManager.getProperty(RepositoryConstants.MD_DB_PASSWORD + "." + getImplementation());
        if (password == null)
            password = ConfigManager.getProperty(RepositoryConstants.MD_DB_PASSWORD);
        return DriverManager.getConnection(URI,username, password);
    }

    public synchronized void deleteMetadata(String identifier) {
        try {
            Connection con = getConnection();
            PreparedStatement pstmt;
            
            pstmt = con.prepareStatement("DELETE FROM " + tableName + " WHERE "+identifierColumnName+" = ?");
            pstmt.setString(1, identifier);
            pstmt.execute();
            pstmt.close();

            con.close();
            log.info("deleteMetadata:identifier:\""+identifier+"\"");
        } catch (SQLException e) {
            log.error("deleteMetadata:identifier:\""+identifier+"\" ", e);
        } 
    }
}
