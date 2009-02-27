/**
 * 
 */
package org.ariadne_eu.metadata.insert.lucene.indexer;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexWriter;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
import org.ariadne_eu.metadata.insert.InsertMetadataFSImpl;
import org.ariadne_eu.metadata.insert.InsertMetadataFactory;
import org.ariadne_eu.metadata.insert.InsertMetadataImpl;
import org.ariadne_eu.metadata.insert.InsertMetadataLuceneImpl;
import org.ariadne_eu.metadata.query.QueryMetadataException;
import org.ariadne_eu.metadata.query.QueryMetadataFactory;
import org.ariadne_eu.metadata.query.QueryMetadataImpl;
import org.ariadne_eu.metadata.query.language.QueryTranslationException;
import org.ariadne_eu.utils.config.ConfigManager;
import org.ariadne_eu.utils.config.RepositoryConstants;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author gonzalo
 *
 */
public class ReIndexFSImpl extends ReIndexImpl {
	
	private static Logger log = Logger.getLogger(ReIndexFSImpl.class);
	private String dirString;

	
	public ReIndexFSImpl() {
		initialize();
	}
		
	void initialize() {
		super.initialize();

		try {
			dirString = ConfigManager.getProperty(RepositoryConstants.MD_SPIFS_DIR );
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
	
	
	public void reIndexMetadata() {
		File mdFile;
		File dir = new File(dirString);
		File[] files = dir.listFiles();
		SAXBuilder builder;

		InsertMetadataImpl[] insertImpls = InsertMetadataFactory.getInsertImpl();
		InsertMetadataLuceneImpl luceneImpl = null;
		for (int i = 0; i < insertImpls.length; i++) {
			InsertMetadataImpl insertImpl = insertImpls[i];
			if (insertImpl instanceof InsertMetadataLuceneImpl)
				luceneImpl = (InsertMetadataLuceneImpl) insertImpl;
		}

		if (luceneImpl == null)
			return;

		luceneImpl.createLuceneIndex();

		String implementation = ConfigManager
				.getProperty(RepositoryConstants.MD_INSERT_IMPLEMENTATION);
		if (implementation != null) {

			for (int i = 0; i < files.length; i++) {
				mdFile = files[i];
				if (!mdFile.isDirectory()) {

					builder = new SAXBuilder();
					Document doc;
					try {
						doc = builder.build(mdFile);
						XMLOutputter outputter = new XMLOutputter();
						Format format = Format.getPrettyFormat();
						outputter.setFormat(format);
						String output = outputter.outputString(doc);

						luceneImpl.insertMetadata(mdFile.getName(), output);
					} catch (JDOMException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

}
