/**
 * 
 */
package org.ariadne_eu.content.retrieve;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.ariadne.config.PropertiesManager;
import org.ariadne_eu.metadata.query.QueryMetadataException;
import org.ariadne_eu.metadata.query.language.QueryTranslationException;
import org.ariadne_eu.utils.config.RepositoryConstants;
import org.ariadne_eu.utils.update.QueryOnId;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author gonzalo
 *
 */
public class RetrieveContentFSImpl extends RetrieveContentImpl {
	private static Logger log = Logger.getLogger(RetrieveContentFSImpl.class);
	
	private static File baseFolder;
	
	private static Vector xpathIdentifiers;
    private static Vector xpathLocations;
    private static String xmlns;
    private static String mdCollection;

    
    private static final int DATA_BLOCK_SIZE = 1024;

    public RetrieveContentFSImpl() {
        initialize();
    }

    void initialize() {
        super.initialize();
        try {
			String basePath = PropertiesManager.getInstance().getProperty(RepositoryConstants.CNT_DR_BASEPATH);
			if (basePath == null)
				log.error("initialize failed: no " + RepositoryConstants.CNT_DR_BASEPATH + " found");
			else
				baseFolder = new File(basePath);
		} catch (Throwable t) {
			log.error("initialize: ", t);
		}        
		// to get the location
        xmlns = PropertiesManager.getInstance().getProperty(RepositoryConstants.MD_INSERT_XMLNS_XSD); //XMLNS is not query-language dependent
        xpathIdentifiers = new Vector();
        if (PropertiesManager.getInstance().getProperty(RepositoryConstants.SR_XPATH_QRY_ID + ".1") == null)
        	xpathIdentifiers.add("general/identifier/entry/text()");
        else {
            int i = 1;
            while(PropertiesManager.getInstance().getProperty(RepositoryConstants.SR_XPATH_QRY_ID + "." + i) != null) {
            	xpathIdentifiers.add(PropertiesManager.getInstance().getProperty(RepositoryConstants.SR_XPATH_QRY_ID + "." + i));
                i++;
            }
        }
        xpathLocations = new Vector();
        if (PropertiesManager.getInstance().getProperty(RepositoryConstants.CNT_MD_XPATHQRY_LOCATION + ".1") == null)
        	xpathLocations.add("technical/location/text()");
        else {
            int i = 1;
            while(PropertiesManager.getInstance().getProperty(RepositoryConstants.CNT_MD_XPATHQRY_LOCATION + "." + i) != null) {
            	xpathLocations.add(PropertiesManager.getInstance().getProperty(RepositoryConstants.CNT_MD_XPATHQRY_LOCATION + "." + i));
                i++;
            }
        }
    }
    
    public DataHandler retrieveContent(String identifier) {
    	initialize();
    	
    	String location = null;
		File file = new File(identifier);
    	try {
			String cntMetadata = getMetadataForID(identifier);
			if (cntMetadata == null) {
				location = retrieveMetadataLocation(identifier);	       		
				if (location != null) {
					BufferedInputStream in = new BufferedInputStream(new URL(location).openStream());
					FileOutputStream fos = new FileOutputStream(file);
					BufferedOutputStream bout = new BufferedOutputStream(fos,DATA_BLOCK_SIZE);
					byte data[] = new byte[DATA_BLOCK_SIZE];
					while(in.read(data,0,DATA_BLOCK_SIZE)>=0)
						bout.write(data);
					bout.close();
					in.close();
				}
			} else {
				file = getFileFromMetadata(identifier,cntMetadata);
			}
			
			
		} catch (MalformedURLException e) {
			log.error("retrieveContent:identifier=" + identifier, e);
		} catch (FileNotFoundException e) {
			log.error("retrieveContent:identifier=" + identifier, e);
		} catch (IOException e) {
			log.error("retrieveContent:identifier=" + identifier, e);
		}
    	
        if (file == null || !file.exists()) {
            return null;
        }
        return new DataHandler(new FileDataSource(file));
    }
    
    private String retrieveMetadataLocation (String identifier) {
    	
    	try {
    		String xml = QueryOnId.getMACEquery().getMaceInstance(identifier);
    		Document doc = getDoc(xml);
    		return getTechnicalLocation(doc);
			
    	} catch (QueryTranslationException e) {
			log.error("retrieveContent:identifier=" + identifier, e);
		} catch (QueryMetadataException e) {
			log.error("retrieveContent:identifier=" + identifier, e);
		} catch (Exception e) {
			log.error("retrieveContent:identifier=" + identifier, e);
		}
		return null;
    }

	@Override
	public String retrieveFileName(String identifier) {
		String metadata = null;
        String name = identifier.replaceAll(":", "_");
        name = name.replaceAll("/", ".s.");
		File idFolder = new File(baseFolder.getAbsolutePath() + File.separator + name);
		if (idFolder.exists()) {
			File[] subFiles = idFolder.listFiles(); 
			if (subFiles.length > 0) {
				for (int i = 0; i < subFiles.length; i++) {
					File file = subFiles[i];
					return file.getName();
				}
			}
		}
		return identifier;
	}

	@Override
	public String retrieveFileType(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String getMetadataForID(String identifier) {
        String metadata = null;
        String name = identifier.replaceAll(":", "_");
        name = name.replaceAll("/", ".s.");
		File idFolder = new File(baseFolder.getAbsolutePath() + File.separator + name);
		if (idFolder.exists()) {
			File[] subFiles = idFolder.listFiles(); 
			if (subFiles.length > 0) {
				for (int i = 0; i < subFiles.length; i++) {
					File file = subFiles[i];
					return file.getName();
				}
			}
		}
        return metadata;
    }

    private static File getFileFromMetadata(String identifier, String metadata) {
    	String name = identifier.replaceAll(":", "_");
        name = name.replaceAll("/", ".s.");
    	return new File(baseFolder.getAbsolutePath() + File.separator + name + File.separator + metadata);	
    }
    
    private static Document getDoc (String xml) {
		Document doc = null;
		StringReader stringReader = new StringReader(xml);
		InputSource input = new InputSource(stringReader);
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			doc = factory.newDocumentBuilder().parse(input);
		} catch (Exception e) {
			log.error("reIndexMetadata:",e);
		}
		return doc;
	}
    
    private static String getTechnicalLocation (Document doc) {
    	String location = null;
		
		for (int i = 0; i < xpathIdentifiers.size() && location == null; i++) {
			String xpathIdentifier = (String) xpathIdentifiers.elementAt(i);
			for (int j = 0; j < xpathLocations.size() && location == null; j++) {
				String xpathLocation = (String) xpathLocations.elementAt(j);
				try {
					location = XPathAPI.selectSingleNode(doc.getFirstChild(),xpathLocation).getNodeValue();
				} catch (Exception e) {}
				
			}
		} 
		return location;
	}

}
