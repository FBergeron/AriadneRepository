package org.ariadne_eu.metadata.insert.lucene.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.ariadne_eu.metadata.insert.InsertMetadataIBMDB2DbImpl;
import org.ariadne_eu.utils.Stopwatch;
import org.ariadne_eu.utils.config.ConfigManager;
import org.ariadne_eu.utils.config.RepositoryConstants;
import org.ariadne_eu.utils.lucene.indexer.MACEEnrichment;
import org.eun.lucene.core.indexer.document.DocumentHandler;
import org.eun.lucene.core.indexer.document.DocumentHandlerException;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class MACELOMHandler extends DocumentHandler {
	
	private static Logger log = Logger.getLogger(MACELOMHandler.class);

	private static final String[] MIN_MAX = { "min", "max" };
	/** A buffer for each XML element */
	private StringBuffer elementBuffer = new StringBuffer();
	private String purpose = "", taxonPathSource, purposeFieldName, tpSourceFieldName, taxonPathId, tpIdFieldName, source, catalog, identifier;
	private HashMap<String, String> attributeMap = new HashMap<String, String>();
	private String branche = "";
	private Document doc;
	private String contents;
	private final String BRANCH_SEPARATOR = ".";
	private final String ATT_SEPARATOR = ".";
	private final String EQUAL_SEPARATOR = "=";
	private HashMap<String, Element> classificationValues;
	private Vector<Element> taxonPath; 

	public Document getDocument(InputStream is) throws DocumentHandlerException {

		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser parser = spf.newSAXParser();
			parser.parse(is, this);
			
		} catch (IOException e) {
			throw new DocumentHandlerException("Cannot parse XML document", e);
		} catch (ParserConfigurationException e) {
			throw new DocumentHandlerException("Cannot parse XML document", e);
		} catch (SAXException e) {
			throw new DocumentHandlerException("Cannot parse XML document", e);
		} 

		return doc;
	}

	public void startDocument() {
		doc = new Document();
		contents = new String();
		
	}

	/*
	 * Save the attribute in a map to reuse it when the element ends (only used
	 * for the last element of a branch) Add an attribute field Incremental
	 * string creation to represent the current branch parsed
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName,Attributes atts) throws SAXException {
		branche += qName.toLowerCase();

		elementBuffer.setLength(0);
		attributeMap.clear();// No need for a map :D

		if (atts.getLength() > 0) {
			attributeMap = new HashMap<String, String>();

			for (int i = 0; i < atts.getLength(); i++) {
				attributeMap.put(atts.getQName(i), atts.getValue(i));

				if (!atts.getQName(i).equals("uniqueElementName")) {
					if (atts.getQName(i).equalsIgnoreCase("xmlns")
							|| atts.getQName(i).equalsIgnoreCase(
									"xsi:schemaLocation")) {
						String fieldName = "untokenized." + atts.getQName(i);
						doc.add(new Field(fieldName.toLowerCase(), atts
								.getValue(i).toLowerCase(), Field.Store.YES,
								Field.Index.UN_TOKENIZED));// XXX
						fieldName = atts.getQName(i);
						doc.add(new Field(fieldName.toLowerCase(), atts
								.getValue(i).toLowerCase(), Field.Store.YES,
								Field.Index.UN_TOKENIZED));// XXX

					} else {
						String fieldName = branche + "" + ATT_SEPARATOR + ""
								+ atts.getQName(i);
						doc.add(new Field(fieldName.toLowerCase(), atts
								.getValue(i).toLowerCase(), Field.Store.YES,
								Field.Index.UN_TOKENIZED));// XXX

					}
				}
			}
		}
		branche += BRANCH_SEPARATOR;
	}

	public void characters(char[] text, int start, int length) {
		elementBuffer.append(text, start, length);
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		String tmpBranche = branche.substring(0, branche.length() - 1);
		String tmp2Branche = "";

		if (branche.endsWith(qName.toLowerCase() + "" + BRANCH_SEPARATOR)) {
			branche = branche.substring(0, branche.length() - qName.length()
					- 1);
			if (!branche.equals(""))
				tmp2Branche = branche.substring(0, branche.length() - 1);
		}

		if (tmpBranche.matches("lom")) {
			doc.add(new Field("contents", contents, Field.Store.YES,Field.Index.TOKENIZED));
		}

		if (elementBuffer.toString().trim().equals("")) {
			return;
		}

		// Attributes for string element ... (ex. Save the field by language)
		if (qName.equalsIgnoreCase("string")) {
			Iterator iter = attributeMap.keySet().iterator();
			while (iter.hasNext()) {
				String attName = ((String) iter.next()).toLowerCase();
				String attValue = ((String) attributeMap.get(attName)).toLowerCase();
				String fieldName = tmpBranche + "" + ATT_SEPARATOR + "" + attName + "" + EQUAL_SEPARATOR + "" + attValue;
				//GAP: elimino esto de los iguales en el field name proq no puedo hacerlo con plql
				//doc.add(new Field(fieldName.toLowerCase(), elementBuffer.toString().toLowerCase(), Field.Store.YES,Field.Index.TOKENIZED));// XXX
			}
		}

		// Hardcoded for LOM XML specifications -->
		// Classification ...
		if (tmpBranche.matches(".*classification\\.((purpose)|(taxonpath)).*")) {
			Namespace lomNS = Namespace.getNamespace("","http://ltsc.ieee.org/xsd/LOM");
			if (tmpBranche.endsWith("classification.purpose.source")) {

			} else if (tmpBranche.endsWith("classification.purpose.value")) {
				purpose = elementBuffer.toString().trim().toLowerCase().replaceAll(" ", "").replaceAll("\\(.*\\)", "").replaceAll("[a-z]\\.[0-9]", "").replaceAll("\\.[0-9]","");
				purposeFieldName = tmpBranche + ATT_SEPARATOR + "" + purpose;
				
				//GAP: lo a–ado para hacer la prueba con solr
				purpose = elementBuffer.toString().toLowerCase().replaceAll("\\(.*\\)", "").replaceAll("[a-z]\\.[0-9]", "").replaceAll("\\.[0-9]","").trim();
				doc.add(new Field(tmpBranche, purpose, Field.Store.YES,Field.Index.TOKENIZED));// XXX
				doc.add(new Field(tmpBranche + ".exact", purpose, Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX
				
			} else if (tmpBranche.endsWith("classification.taxonpath.source.string")) {
				taxonPathSource = elementBuffer.toString().trim().toLowerCase();
				tpSourceFieldName = tmpBranche + ATT_SEPARATOR + "" + taxonPathSource;
				
			} else if (tmpBranche.endsWith("classification.taxonpath.taxon.id")) {


//				taxonPathId = elementBuffer.toString().toLowerCase().replaceAll(" ", "").replaceAll("\\(.*\\)", "").replaceAll("[a-z]\\.[0-9]", "").replaceAll("\\.[0-9]","").trim();
				tpIdFieldName = tmpBranche;
				
				//GAP: for protege
				taxonPathId = elementBuffer.toString();
//				doc.add(new Field(tmpBranche, elementBuffer.toString().trim().toLowerCase(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX
//				doc.add(new Field(tmpBranche + ".exact",elementBuffer.toString().trim().toLowerCase(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX
				
			} else if (tmpBranche.endsWith("classification.taxonpath.taxon.entry.string")) {
				
				if (taxonPathId != null) {
					classificationValues = MACEEnrichment.loadClassification();

					if (classificationValues.containsKey(taxonPathId)) {
						Element classificationValue = classificationValues.get(taxonPathId);
						getMaceClassTaxonPath(classificationValue);
						for (Iterator iterator = taxonPath.iterator(); iterator.hasNext();) {
							Element item = (Element) iterator.next();
							doc.add(new Field(tpIdFieldName, (item.getAttributeValue("id")).trim().toLowerCase(), Field.Store.YES,Field.Index.TOKENIZED));// XXX
							doc.add(new Field(tmpBranche, (item.getChildText("label")).trim().toLowerCase(), Field.Store.YES,Field.Index.TOKENIZED));// XXX
							doc.add(new Field(tmpBranche + ".exact", (item.getChildText("label")).trim().toLowerCase(), Field.Store.YES,Field.Index.UN_TOKENIZED));
							contents = contents.concat(" "+ (item.getChildText("label")).trim().toLowerCase());
						}

					} else {
						log.info("The classification value for the id:'"+ taxonPathId + "'was not found!");
					}
				}
			}
		}
		// Title
		else if (tmpBranche.matches(".*title.*")) {
			if (tmpBranche.endsWith("title.string")) {
				doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer
						.toString().trim(), Field.Store.YES,
						Field.Index.TOKENIZED));// XXX
				doc.add(new Field(tmpBranche.toLowerCase() + ".exact",
						elementBuffer.toString().trim(), Field.Store.YES,
						Field.Index.UN_TOKENIZED));// XXX

			}
		}
		// Contribute
		else if (tmpBranche
				.matches(".*contribute\\.((role)|(entity)|(date)).*")) {
			if (tmpBranche.endsWith("contribute.role.source")) {
				source = elementBuffer.toString().trim();
				doc.add(new Field(tmpBranche.toLowerCase(), source
						.toLowerCase(), Field.Store.YES,
						Field.Index.UN_TOKENIZED));// XXX

			} else if (tmpBranche.endsWith("contribute.role.value")) {
				source += EQUAL_SEPARATOR + ""
						+ elementBuffer.toString().trim();// TODO
				doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer
						.toString().toLowerCase().trim(), Field.Store.YES,
						Field.Index.UN_TOKENIZED));// XXX

			} else if (tmpBranche.endsWith("contribute.entity")) {
				String fieldName = tmp2Branche + "" + EQUAL_SEPARATOR + ""
						+ source;
				doc.add(new Field(fieldName.toLowerCase(), elementBuffer
						.toString().toLowerCase().trim(), Field.Store.YES,
						Field.Index.TOKENIZED));// XXX
				doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer
						.toString().toLowerCase().trim(), Field.Store.YES,
						Field.Index.TOKENIZED));// XXX

			} else if (tmpBranche.endsWith("contribute.date.datetime")) {
				String fieldname = tmp2Branche + "" + EQUAL_SEPARATOR + ""
						+ source;
				doc.add(new Field(fieldname.toLowerCase(), elementBuffer
						.toString().toLowerCase().trim(), Field.Store.YES,
						Field.Index.UN_TOKENIZED));// XXX

				// para poder soportar busquedas con rangos
				String date = elementBuffer.toString().toLowerCase().trim().replaceAll("-", "").replaceAll("t", "").replaceAll(":", "").replaceAll("\\.", "").replaceAll("z","");
				if (date.length() > 15)
					date = date.substring(0, 15);
				//				
				doc.add(new Field(tmp2Branche.toLowerCase(), date,
						Field.Store.YES, Field.Index.UN_TOKENIZED));// XXX

			}
		}
		// Age
		// <educational.typicalAgeRange.string>12-15
		// <string language="en">12-15</string>
		// <string language="x-t-lre">12-15</string>
		// </typicalAgeRange>
		else if (tmpBranche.matches(".*educational.typicalagerange.string")) {
			Iterator iter = attributeMap.keySet().iterator();
			while (iter.hasNext()) {
				String attName = ((String) iter.next()).toLowerCase();
				String attValue = ((String) attributeMap.get(attName))
						.toLowerCase();

				if (!attValue.equalsIgnoreCase("x-t-lre"))
					continue;

				String[] ageRange = elementBuffer.toString().trim().split("-");

				if (ageRange.length <= 2) {

					for (int z = 0; z < ageRange.length; z++) {

						if (ageRange[z].matches("(\\d)*")) {

							if (ageRange[z].length() == 1) {
								ageRange[z] = "00" + ageRange[z];

							} else if (ageRange[z].length() == 2) {
								ageRange[z] = "0" + ageRange[z];
							}
						}
						String fieldName = tmp2Branche + "." + MIN_MAX[z];
						doc.add(new Field(fieldName.toLowerCase(), ageRange[z]
								.toLowerCase(), Field.Store.YES,
								Field.Index.UN_TOKENIZED));// XXX
					}
				}
			}

		}
		// resource. Catalog + entry
		else if (tmpBranche
				.matches(".*resource.identifier\\.((catalog)|(entry))")) {
			if (tmpBranche.endsWith("identifier.catalog")) {
				identifier = "catalog" + EQUAL_SEPARATOR + "" + elementBuffer.toString().trim();
				doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer.toString().toLowerCase().trim(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX

			} else if (tmpBranche.endsWith("identifier.entry")) {
				doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer
						.toString().toLowerCase().trim(), Field.Store.YES,
						Field.Index.UN_TOKENIZED));// XXX
				// doc.add(new
				// Field(tmp2Branche+""+BRANCH_SEPARATOR+""+indentifier+""+BRANCH_SEPARATOR+"entry",elementBuffer.toString().trim(),
				// Field.Store.YES, Field.Index.UN_TOKENIZED));//XXX
				String fieldName = tmp2Branche + "" + BRANCH_SEPARATOR + "" + identifier + "" + BRANCH_SEPARATOR + "entry";
				doc.add(new Field(fieldName.toLowerCase(), elementBuffer.toString().toLowerCase().trim(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX
			}
		}
		// Catalog + entry
		else if (tmpBranche.matches(".*identifier\\.((catalog)|(entry))")) {
			if (tmpBranche.endsWith("identifier.catalog")) {
				// indentifier =
				// "catalog"+EQUAL_SEPARATOR+""+elementBuffer.toString().trim();
				catalog = elementBuffer.toString().trim();
				doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer.toString().trim(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX

			} else if (tmpBranche.endsWith("identifier.entry")) {
				doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer.toString().trim(), Field.Store.YES,Field.Index.UN_TOKENIZED));
				
				doc.add(new Field(tmpBranche.toLowerCase() + BRANCH_SEPARATOR + "exact" , elementBuffer.toString().trim(), Field.Store.YES,Field.Index.UN_TOKENIZED));

				// doc.add(new
				// Field(tmp2Branche+""+BRANCH_SEPARATOR+""+indentifier+""+BRANCH_SEPARATOR+"entry",elementBuffer.toString().trim(),
				// Field.Store.YES, Field.Index.UN_TOKENIZED));//XXX
				String fieldName = tmp2Branche + "" + BRANCH_SEPARATOR+ "catalog" + BRANCH_SEPARATOR + "entry";
				// GAP
				// doc.add(new
				// Field(fieldName.toLowerCase(),indentifier+""+elementBuffer.toString().toLowerCase().trim(),
				// Field.Store.YES, Field.Index.UN_TOKENIZED));//XXX
				doc.add(new Field(fieldName.toLowerCase(), catalog + ":"+ elementBuffer.toString().toLowerCase().trim(),Field.Store.YES, Field.Index.TOKENIZED));
			}
		}
		// technical.format
		else if (tmpBranche.matches(".*technical.format.*")) {
//			String format = elementBuffer.toString().toLowerCase().replace('/', '\\');
			String format = elementBuffer.toString().toLowerCase().trim();
			doc.add(new Field(tmpBranche.toLowerCase(), format, Field.Store.YES, Field.Index.UN_TOKENIZED));// XXX
		}
		// general.description.string
		else if (tmpBranche.matches(".*general.description.string")) {
			String format = elementBuffer.toString().toLowerCase().trim();
			doc.add(new Field(tmpBranche.toLowerCase(), format, Field.Store.YES, Field.Index.TOKENIZED));// XXX
		}
		// general.keyword.string
		else if (tmpBranche.matches(".*general.keyword.string")) {
			String format = elementBuffer.toString().toLowerCase().trim();
			doc.add(new Field(tmpBranche.toLowerCase(), format, Field.Store.YES, Field.Index.TOKENIZED));// XXX
		}
		// rights.description.string
		else if (tmpBranche.matches(".*rights.description.string")) {
			String format = elementBuffer.toString().toLowerCase().trim();
			doc.add(new Field(tmpBranche.toLowerCase(), format, Field.Store.YES, Field.Index.TOKENIZED));// XXX
			doc.add(new Field(tmpBranche.toLowerCase()+".exact", format, Field.Store.YES, Field.Index.UN_TOKENIZED));// XXX
		}
		// LearningResourceType + value
		else if (tmpBranche.matches(".*learningresourcetype.value.*")) {
			doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer
					.toString().toLowerCase(), Field.Store.YES,
					Field.Index.TOKENIZED));
		}
		// Source - value -> more general case so it has to be tested at the end
		// !
		else if (tmpBranche.matches(".*((source)|(value))")) {
			if (tmpBranche.endsWith("source")) {
				source = "source" + EQUAL_SEPARATOR
						+ elementBuffer.toString().trim();
				doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer
						.toString().toLowerCase().trim(), Field.Store.YES,
						Field.Index.UN_TOKENIZED));// XXX

			} else if (tmpBranche.endsWith("value")) {
				doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer
						.toString().toLowerCase().trim(), Field.Store.YES,
						Field.Index.UN_TOKENIZED));// XXX
				String fieldName = tmp2Branche + "" + BRANCH_SEPARATOR + ""
						+ source + BRANCH_SEPARATOR + "value";
				doc.add(new Field(fieldName.toLowerCase(), elementBuffer
						.toString().toLowerCase().trim(), Field.Store.YES,
						Field.Index.UN_TOKENIZED));// XXX
			}
		}
		// In all the other cases add a field !
		else {
			doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer
					.toString().toLowerCase(), Field.Store.YES,
					Field.Index.UN_TOKENIZED));// XXX
		}
		// <---
		// to store the contents without metatags
		contents = contents.concat(" " + elementBuffer.toString().toLowerCase());
		elementBuffer.setLength(0);
	}
	
	private String readFile(String filePath){
		String content = "";
		LineIterator it;
		File file = new File(filePath);
		try {
			it = FileUtils.lineIterator(file, "UTF-8");
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
	
	
	
	private void getMaceClassTaxonPath(Element item) {
		Element parent;
		taxonPath = new Vector();
		taxonPath.add(item);
		while ((item.getParentElement()).getParentElement() != null) {
			parent = item.getParentElement().getParentElement();
			taxonPath.add(parent);
			item = parent;
		}
	}

	public static void main(String args[]) throws Exception {
		MACELOMHandler handler = new MACELOMHandler();
		Document doc = handler.getDocument(new FileInputStream(new File("/Users/gonzalo/Desktop/VocabTest.xml")));
		List fields = doc.getFields();
		for (Iterator iterator = fields.iterator(); iterator.hasNext();) {
			Field field = (Field) iterator.next();
			System.out.println(field.name() + " :: " + field.stringValue());

		}

	}
}
