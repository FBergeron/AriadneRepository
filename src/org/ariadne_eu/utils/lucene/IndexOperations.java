package org.ariadne_eu.utils.lucene;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
import org.ariadne_eu.metadata.insert.InsertMetadataImpl;
import org.ariadne_eu.metadata.insert.InsertMetadataFactory;
import org.ariadne_eu.metadata.insert.InsertMetadataLuceneImpl;
import org.ariadne_eu.metadata.query.QueryMetadataException;
import org.ariadne_eu.metadata.query.QueryMetadataFactory;
import org.ariadne_eu.metadata.query.QueryMetadataImpl;
import org.ariadne_eu.metadata.query.language.QueryTranslationException;
import org.ariadne_eu.metadata.resultsformat.TranslateResultsformat;
import org.ariadne_eu.utils.config.ConfigManager;
import org.ariadne_eu.utils.config.RepositoryConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Created by ben
 * Date: 25-aug-2007
 * Time: 18:50:33
 * To change this template use File | Settings | File Templates.
 */
public class IndexOperations {
    private static String xquery;
    private static String xmlns ;
    private static String collection ;
    private static int startResult = 1 ;
    private static int nbResults = 25 ;
    private static Vector xpathQueries;

    private static Logger log = Logger.getLogger(IndexOperations.class);

    static {
        try {
            collection = ConfigManager.getProperty(RepositoryConstants.MD_DB_XMLDB_LOC);
            if(collection == null) {
                collection = "collection(\"metadatastore\")";
                log.warn("initialize:property \""+ RepositoryConstants.MD_DB_XMLDB_LOC +"\" not defined");
            }
            xmlns = ConfigManager.getProperty(RepositoryConstants.MD_INSERT_XMLNS_XSD); //XMLNS is not query-language dependent
            xpathQueries = new Vector();
            if (ConfigManager.getProperty(RepositoryConstants.SR_XPATH_QRY_ID + ".1") == null)
                xpathQueries.add("general/identifier/entry/text()");
            else {
                int i = 1;
                while(ConfigManager.getProperty(RepositoryConstants.SR_XPATH_QRY_ID + "." + i) != null) {
                    xpathQueries.add(ConfigManager.getProperty(RepositoryConstants.SR_XPATH_QRY_ID + "." + i));
                    i++;
                }
            }
            if (collection.contains("db2-fn")){
            	createXQuery2();
            } else {
            	createXQuery();
            }
        } catch (Throwable t) {
            log.error("initialize: ", t);
        }
    }
    
    public static void optimize () {
		InsertMetadataImpl[] insertImpls = InsertMetadataFactory.getInsertImpl();
		InsertMetadataLuceneImpl luceneImpl = null;
		for (int i = 0; i < insertImpls.length; i++) {
			InsertMetadataImpl insertImpl = insertImpls[i];
			if (insertImpl instanceof InsertMetadataLuceneImpl)
				luceneImpl = (InsertMetadataLuceneImpl) insertImpl;
		}

		if (luceneImpl == null)
			return;

		luceneImpl.optimizeLuceneIndex();
    }

    public static void regenerateIndex() throws QueryMetadataException, QueryTranslationException {
		QueryMetadataImpl xqueryImpl = (QueryMetadataImpl) QueryMetadataFactory.getQueryImpl(-1);
		InsertMetadataImpl[] insertImpls = InsertMetadataFactory.getInsertImpl();
		InsertMetadataLuceneImpl luceneImpl = null;
		for (int i = 0; i < insertImpls.length; i++) {
			InsertMetadataImpl insertImpl = insertImpls[i];
			System.out.println(insertImpl.getClass().getName());
			if (insertImpl instanceof InsertMetadataLuceneImpl)
				luceneImpl = (InsertMetadataLuceneImpl) insertImpl;
		}

		if (luceneImpl == null)
			return;

		luceneImpl.createLuceneIndex();

		String implementation = ConfigManager.getProperty(RepositoryConstants.MD_INSERT_IMPLEMENTATION);
		if (implementation != null) {

			startResult = 1;

			while (true) {
				if (collection.contains("db2-fn")) {
					createXQuery2();
				} else {
					createXQuery();
				}
				
				//this is not working yet!!
				String results = xqueryImpl.xQuery(xquery);

				if (results == null)
					break;

				if (results != null && collection.contains("db2-fn"))
					results = "<results>" + results + "</results>";
				startResult += nbResults;

				StringReader stringReader = new StringReader(results);
				InputSource input = new InputSource(stringReader);
				Document doc = null;
				try {
					doc = DocumentBuilderFactory.newInstance()
							.newDocumentBuilder().parse(input);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Node result = doc.getFirstChild();
				NodeList nl = result.getChildNodes();

				log.info("startResult:" + startResult + "NodeListLength:" + nl.getLength());

				if (nl.getLength() == 0)
					break;

				for (int i = 0; i < nl.getLength(); i++) {
					try {
						Element theNode = ((Element) nl.item(i));
						String identifier = null;
						for (int j = 0; j < xpathQueries.size() && identifier == null; j++) {
							String xpathQuery = (String) xpathQueries.elementAt(j);

							try {
								identifier = XPathAPI.selectSingleNode(theNode,xpathQuery).getNodeValue();
							} catch (Exception e) {
							}
						}

						StringWriter out = new StringWriter();
						XMLSerializer serializer = new XMLSerializer(out,
								new OutputFormat(doc));
						serializer.serialize(theNode);
						String lom = out.toString();

						if (identifier != null) {
							luceneImpl.insertMetadata(identifier, lom);
						}
					} catch (ClassCastException e) {
					} catch (Exception e) {
						e.printStackTrace(); // To change body of catch
												// statement use File | Settings
												// | File Templates.
					}
				}

				// if (results.indexOf("<lom") < 0)
				// break;

				// while (true) {
				// int start = results.indexOf("<lom");
				// if (start < 0)
				// break;
				// int end = results.indexOf("</lom>") + "</lom>".length();
				// String lom = results.substring(start, end);
				// results = results.substring(end);
				// luceneImpl.insertMetadata("", lom); //TODO: identifier
				// }

			}
		}

	}

    // For Exist
    private static void createXQuery() {
        xquery = "xquery version \"1.0\";\n" +
	        (xmlns == null ? "" : "declare namespace lom=\"" + xmlns + "\";\n") +
	        "<results>" +
	        "{for $x at $y in " +
	        "(for $lom in " + collection + "/lom:lom" +
	        "\n  return $lom )" +
	        " where $y >= " + startResult + ( nbResults != 0 ? " and $y < " + (startResult + nbResults) : "") +
	        " return $x }</results>";
    }
    
    // For DB2
    public static void createXQuery2(){
    	xquery = "xquery version \"1.0\";\n" +
    		(xmlns == null ? "" : "declare default element namespace \"" + xmlns + "\"; (:hello:)\n") + 
    		"for $x at $y in (" +
    		"for $lom in " + collection + " " +
    		"return $lom ) " +
    		"where $y >= " + startResult + ( nbResults != 0 ? " and $y < " + (startResult + nbResults) : "") +
    		" return $x";
        
    }
    
    public static void main(String[] args) throws Exception{
//    	ConfigManager.setPropertiesFile("jsp/install/ariadne.properties");
    	xmlns = "http://ltsc.ieee.org/xsd/LOM";
//    	collection = "collection(\"metadatastore\")";
    	collection = "db2-fn:xmlcolumn(\"METADATASTORE_DEV2.LOMXML\")";
    	
//    	createXQuery();
    	createXQuery2();
    	
    	System.out.println(xquery);
    	//XQueryToLuceneIndex sqtli = new XQueryToLuceneIndex();
    	//sqtli.index();
    }
    
    
}
