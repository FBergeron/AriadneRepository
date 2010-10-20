package org.ariadne_eu.utils.solr;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.servlet.DirectSolrConnection;
import org.ariadne.config.PropertiesManager;
import org.ariadne_eu.utils.config.RepositoryConstants;

public class SolrServerManagement {
	
	private static SolrServerManagement instance = null;
	private static Logger log = Logger.getLogger(SolrServerManagement.class);
	
	private DirectSolrConnection conn;
	private SolrCore core;
	private SolrServer server;
	
	private static String instanceDir;
	private static String dataDir;
	private static String loggingPath;
	
	private SolrServerManagement(){
		instanceDir = (PropertiesManager.getInstance().getPropFile()).replaceAll("install/ariadne.properties", "solr/");
		dataDir = PropertiesManager.getInstance().getProperty(RepositoryConstants.getInstance().SR_SOLR_DATADIR);
		loggingPath = PropertiesManager.getInstance().getProperty(RepositoryConstants.getInstance().REPO_LOG4J_DIR);
		
		if (instanceDir == null) {
			log.error("Could not load Solr instance dir!");
		} else if (dataDir == null) {
			log.warn("initialize:property \"" + RepositoryConstants.getInstance().SR_SOLR_DATADIR + "\" not defined");
		} else if (loggingPath == null) {
			log.warn("initialize:property \"" + RepositoryConstants.getInstance().REPO_LOG4J_DIR + "\" not defined");
		} 
		
		conn = new DirectSolrConnection(instanceDir, dataDir, loggingPath);
		
		SolrCore core = SolrCore.getSolrCore();
		SolrServer server = new EmbeddedSolrServer(core);
		
	}

    public static SolrServerManagement getInstance(){
        if ( instance == null ){
            synchronized( SolrServerManagement.class ){
                if ( instance == null ){
                    instance = new SolrServerManagement();
                }
            }
        }
        return instance;
    }

	public SolrServer getServer() {
		return server;
	}
	
	public DirectSolrConnection getConnection() {
		return conn;
	}
	

}
