/**
 * 
 */
package org.ariadne_eu.metadata.delete;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.ariadne_eu.utils.config.ConfigManager;
import org.ariadne_eu.utils.config.RepositoryConstants;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * @author gonzalo
 *
 */
public class DeleteMetadataFSImpl extends DeleteMetadataImpl {
	
	private static Logger log = Logger.getLogger(DeleteMetadataFSImpl.class);
	private String dirString;

	
	void initialize() {
		super.initialize();
		try {
			dirString = ConfigManager.getProperty(RepositoryConstants.MD_SPIFS_DIR + "." + getImplementation());
			if (dirString == null)
				dirString = ConfigManager.getProperty(RepositoryConstants.MD_SPIFS_DIR);
			if (dirString == null)
				log.error("initialize failed: no " + RepositoryConstants.MD_SPIFS_DIR + " found");
			File dir = new File(dirString);
			if (!dir.isDirectory())
				log.error("initialize failed: " + RepositoryConstants.MD_SPIFS_DIR + " invalid directory");
		} catch (Throwable t) {
			log.error("initialize: ", t);
		}
	}
	
	@Override
	public synchronized void deleteMetadata(String identifier) {
		String name = identifier.replaceAll(":", "_");
        name = name.replaceAll("/", ".s.");
		File result = new File(dirString + name + ".xml");
		result.delete();
		
	}

}
