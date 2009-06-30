/**
 * 
 */
package org.ariadne_eu.metadata.insert;

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
public class InsertMetadataFSImpl extends InsertMetadataImpl {
	
	private static Logger log = Logger.getLogger(InsertMetadataFSImpl.class);
	private String dirString;

	
	void initialize() {
		super.initialize();
		try {
			dirString = ConfigManager.getProperty(RepositoryConstants.MD_SPIFS_DIR + "." + getLanguage());
			if (dirString == null)
				dirString = ConfigManager.getProperty(RepositoryConstants.MD_SPIFS_DIR);
			if (dirString == null)
				log.error("initialize failed: no " + RepositoryConstants.MD_SPIFS_DIR + " found");
			File dir = new File(dirString);
			if (!dir.isDirectory())
				log.error("initialize failed: " + RepositoryConstants.MD_SPIFS_DIR + " invalid directory");
			//TODO: check for valid lucene index
		} catch (Throwable t) {
			log.error("initialize: ", t);
		}
	}
	
	@Override
	public synchronized void insertMetadata(String identifier, String metadata) {
		Reader in = new StringReader(metadata);
		SAXBuilder builder = new SAXBuilder();
		
		try {
			Document doc = builder.build(in);
			
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
		    outputter.setFormat(format);
		    String output = outputter.outputString(doc);
		    
		    String name = identifier.replaceAll(":", "_");
            name = name.replaceAll("/", ".s.");
		    writeStringToFileInEncodingUTF8(output, dirString + name + ".xml");

		} catch (JDOMException e) {
			log.error("insertMetadata:id=" + identifier, e);
		} catch (IOException e) {
			log.error("insertMetadata:id=" + identifier, e);
		}
		
	}
	
	public static void writeStringToFileInEncodingUTF8(String inputText,String outputFileName) throws IOException {
		writeStringToFileInEncoding(inputText, outputFileName, "UTF-8");
	}
	
	public static void writeStringToFileInEncoding(String inputText, String outputFileName, String encoding) throws IOException {
		try {
			File result = new File(outputFileName);
			FileOutputStream fos = new FileOutputStream(result);
			Writer out = new OutputStreamWriter(fos, encoding);
			out.write(inputText);
			out.close();
			fos.close();
		} catch (IOException e) {
			System.out.flush();
			throw e;
		}
	}

}
