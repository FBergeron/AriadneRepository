/**
 * 
 */
package org.ariadne_eu.utils.lucene.reindex;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
import org.ariadne.config.PropertiesManager;
import org.ariadne_eu.metadata.insert.InsertMetadataFactory;
import org.ariadne_eu.metadata.insert.InsertMetadataImpl;
import org.ariadne_eu.metadata.insert.InsertMetadataLuceneImpl;
import org.ariadne_eu.utils.config.RepositoryConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * @author gonzalo
 *
 */
public class ReIndexFSImpl extends ReIndexImpl {
	
	private static Logger log = Logger.getLogger(ReIndexFSImpl.class);
	private String dirString;
	private static Vector xpathQueries;

	
	public ReIndexFSImpl() {
		initialize();
	}
		
	void initialize() {
		super.initialize();

		try {
			dirString = PropertiesManager.getInstance().getProperty(RepositoryConstants.MD_SPIFS_DIR );
			if (dirString == null)
				log.error("initialize failed: no " + RepositoryConstants.MD_SPIFS_DIR + " found");
			File dir = new File(dirString);
			if (!dir.isDirectory())
				log.error("initialize failed: " + RepositoryConstants.MD_SPIFS_DIR + " invalid directory");
			xpathQueries = new Vector();
            if (PropertiesManager.getInstance().getProperty(RepositoryConstants.SR_XPATH_QRY_ID + ".1") == null)
                xpathQueries.add("general/identifier/entry/text()");
            else {
                int i = 1;
                while(PropertiesManager.getInstance().getProperty(RepositoryConstants.SR_XPATH_QRY_ID + "." + i) != null) {
                    xpathQueries.add(PropertiesManager.getInstance().getProperty(RepositoryConstants.SR_XPATH_QRY_ID + "." + i));
                    i++;
                }
            }
			//TODO: check for valid lucene index
			
		} catch (Throwable t) {
			log.error("initialize: ", t);
		}
		
	}
	
	
	public void reIndexMetadata() {
		File mdFile, mdInnerFile;
		File dir = new File(dirString);
		File[] files = dir.listFiles();
		String xml = null;
		// SAXBuilder builder;

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

		String implementation = PropertiesManager.getInstance().getProperty(RepositoryConstants.MD_INSERT_IMPLEMENTATION);
		if (implementation != null) {

			for (int i = 0; i < files.length; i++) {
				mdFile = files[i];
				if (!mdFile.getName().equalsIgnoreCase(".DS_Store")) {

					if (mdFile.isDirectory()) {
						File[] collection = mdFile.listFiles();

						for (int j = 0; j < collection.length; j++) {
							mdInnerFile = collection[j];
							xml = readFile(mdInnerFile, "UTF-8");
							try {
								
								Document doc = getDoc(xml);
								String identifier = getIdentifier(doc);

								StringWriter out = new StringWriter();
								XMLSerializer serializer = new XMLSerializer(out, new OutputFormat(doc));
								serializer.serialize((Element) doc.getFirstChild());
								String lom = out.toString();

								if (identifier != null)
									luceneImpl.insertMetadata(identifier, lom, mdFile.getName());
							} catch (Exception e) {
								log.error("reIndexMetadata", e);
							}

						}
					} else {
						xml = readFile(mdFile, "UTF-8");
						try {

							Document doc = getDoc(xml);
							String identifier = getIdentifier(doc);

							StringWriter out = new StringWriter();
							XMLSerializer serializer = new XMLSerializer(out, new OutputFormat(doc));
							serializer.serialize((Element) doc.getFirstChild());
							String lom = out.toString();

							if (identifier != null)
								luceneImpl.insertMetadata(identifier, lom, "ARIADNE");
						} catch (Exception e) {
							log.error("reIndexMetadata", e);
						}

					}

				}
			}

		}

	}
	
	private static String getIdentifier (Document doc) {
		String identifier = null;
		for (int j = 0; j < xpathQueries.size() && identifier == null; j++) {
			String xpathQuery = (String) xpathQueries.elementAt(j);
			try {
				identifier = XPathAPI.selectSingleNode(doc.getFirstChild(),xpathQuery).getNodeValue();
			} catch (Exception e) {}
		}
		return identifier;
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
	
	public static String readFile(File file, String encoding){
		String content = "";
		LineIterator it;
		try {
			it = FileUtils.lineIterator(file, encoding);
			while (it.hasNext()) {
				String line = it.nextLine();
				content = content + line + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		LineIterator.closeQuietly(it);
		return content;
		
	}

}
