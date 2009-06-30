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
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.ariadne_eu.utils.mace.MACEUtils;
import org.eun.lucene.core.indexer.document.DocumentHandler;
import org.eun.lucene.core.indexer.document.DocumentHandlerException;
import org.jdom.Element;
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
	private boolean isCompetency = false;
	private String competencyID = "";
	private String domainID = "";
	private int competencyCount = 0;
	private int maxEQF = 0;
	private int minEQF = 0;

	public Document getDocument(InputStream is) throws DocumentHandlerException {

		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser parser = spf.newSAXParser();
			parser.parse(is, this);
			return doc;
		} catch (IOException e) {
			log.error("getDocument: ", e);
			throw new DocumentHandlerException("Cannot parse XML document", e);
		} catch (ParserConfigurationException e) {
			log.error("getDocument: ", e);
			throw new DocumentHandlerException("Cannot parse XML document", e);
		} catch (SAXException e) {
			log.error("getDocument: ", e);
			throw new DocumentHandlerException("Cannot parse XML document", e);
		} 
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
		attributeMap.clear();// No need for a map 

		if (atts.getLength() > 0) {
			attributeMap = new HashMap<String, String>();

			for (int i = 0; i < atts.getLength(); i++) {
				attributeMap.put(atts.getQName(i), atts.getValue(i));

				if (!atts.getQName(i).equals("uniqueElementName")) {
					if (atts.getQName(i).equalsIgnoreCase("xmlns")|| atts.getQName(i).equalsIgnoreCase("xsi:schemaLocation")) {
						String fieldName = "untokenized." + atts.getQName(i);
//						doc.add(new Field(fieldName.toLowerCase(), atts.getValue(i).toLowerCase(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX
						fieldName = atts.getQName(i);
//						doc.add(new Field(fieldName.toLowerCase(), atts.getValue(i).toLowerCase(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX

					} else {
						String fieldName = branche + "" + ATT_SEPARATOR + ""+ atts.getQName(i);
//						doc.add(new Field(fieldName.toLowerCase(), atts.getValue(i).toLowerCase(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX

					}
				}
			}
		}
		branche += BRANCH_SEPARATOR;
	}

	public void characters(char[] text, int start, int length) {
		elementBuffer.append(text, start, length);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		List labels;
		String tmpBranche = branche.substring(0, branche.length() - 1);
		
		//remove the NS+colons on any element		
		if (tmpBranche.contains(":")) {
			tmpBranche = tmpBranche.replaceAll("\\.(\\w+):", ".");
		}
		String tmp2Branche = "";

		if (branche.endsWith(qName.toLowerCase() + "" + BRANCH_SEPARATOR)) {
			branche = branche.substring(0, branche.length() - qName.length() - 1);
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
//				String fieldName = tmpBranche + "" + ATT_SEPARATOR + "" + attName + "" + EQUAL_SEPARATOR + "" + attValue;
				String fieldName = tmpBranche + "" + ATT_SEPARATOR + "" + attValue;
				doc.add(new Field(fieldName.toLowerCase(), elementBuffer.toString().toLowerCase(), Field.Store.YES,Field.Index.TOKENIZED));// XXX
			}
		}

		// Hardcoded for LOM XML specifications -->
		// Classification ...
		if (tmpBranche.matches(".*classification\\.((purpose)|(taxonpath)).*")) {
			if (tmpBranche.endsWith("classification.purpose.source")) {
			} else if (tmpBranche.endsWith("classification.purpose.value")) {
//				purpose = elementBuffer.toString().trim().toLowerCase().replaceAll(" ", "").replaceAll("\\(.*\\)", "").replaceAll("[a-z]\\.[0-9]", "").replaceAll("\\.[0-9]","");
//				purposeFieldName = tmpBranche + ATT_SEPARATOR + "" + purpose;
				//GAP: lo a–ado para hacer la prueba con solr
//				purpose = elementBuffer.toString().toLowerCase().replaceAll("\\(.*\\)", "").replaceAll("[a-z]\\.[0-9]", "").replaceAll("\\.[0-9]","").trim();
				
				purpose = elementBuffer.toString();
				if (purpose.equalsIgnoreCase("competency"))
					isCompetency = true;
				doc.add(new Field(tmpBranche, purpose, Field.Store.YES,Field.Index.TOKENIZED));// XXX
				
			} else if (tmpBranche.endsWith("classification.taxonpath.source.string")) {
//				taxonPathSource = elementBuffer.toString().trim().toLowerCase();
				taxonPathSource = elementBuffer.toString().trim();
				if (taxonPathSource.equalsIgnoreCase("MACE Competence Catalogue"))
					isCompetency = true;
				
			} else if (tmpBranche.endsWith("classification.taxonpath.taxon.id")) {
				tpIdFieldName = tmpBranche;
				taxonPathId = elementBuffer.toString();
				if (isCompetency) {
					if (competencyCount == 0) {
						doc.add(new Field(tmpBranche+".domain", elementBuffer.toString(), Field.Store.YES,Field.Index.UN_TOKENIZED));
						domainID = elementBuffer.toString();
						competencyCount++;
					} else if (competencyCount == 1){
						doc.add(new Field(tmpBranche+".competency", elementBuffer.toString(), Field.Store.YES,Field.Index.UN_TOKENIZED));
						competencyID = elementBuffer.toString();
						competencyCount++;
					}
				} 
//				else{
//					doc.add(new Field(tmpBranche, taxonPathId, Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX
//				}
			} else if (tmpBranche.endsWith("classification.taxonpath.taxon.entry.string")) {
				if (isCompetency) {
					doc.add(new Field(tmpBranche, elementBuffer.toString(), Field.Store.YES,Field.Index.TOKENIZED));
				}
				else if (taxonPathId != null) {
					classificationValues = MACEUtils.getClassification();

					if (classificationValues.containsKey(taxonPathId)) {
						Element classificationValue = classificationValues.get(taxonPathId);
						getMaceClassTaxonPath(classificationValue);
						for (Iterator iterator = taxonPath.iterator(); iterator.hasNext();) {
							Element item = (Element) iterator.next();
							doc.add(new Field(tpIdFieldName, item.getAttributeValue("id"), Field.Store.YES,Field.Index.UN_TOKENIZED));
							labels = item.getChildren("label");
							for (Iterator iterator2 = labels.iterator(); iterator2.hasNext();) {
								Element label = (Element) iterator2.next();
								doc.add(new Field(tmpBranche, label.getText(), Field.Store.YES,Field.Index.TOKENIZED));
//								doc.add(new Field(tmpBranche + ".exact", (label.getText()).trim().toLowerCase(), Field.Store.YES,Field.Index.UN_TOKENIZED));
								contents = contents.concat(" "+ (label.getText()).trim());
							}
						}

					} else {
						log.info("The classification value for the id:'"+ taxonPathId + "'was not found!");
					}
				} 
			} else if(tmpBranche.endsWith(".classification.taxonpath.taxon.mineqf") || tmpBranche.endsWith(".classification.taxonpath.taxon.maxeqf")) {
				if (competencyCount == 2 || competencyCount == 3) {
					if(tmpBranche.endsWith(".classification.taxonpath.taxon.mineqf"))
						minEQF = Integer.parseInt(elementBuffer.toString());
					else if (tmpBranche.endsWith(".classification.taxonpath.taxon.maxeqf"))
						maxEQF = Integer.parseInt(elementBuffer.toString());
					competencyCount++;
				} else if (competencyCount == 4) {
					if(tmpBranche.endsWith(".classification.taxonpath.taxon.mineqf"))
						minEQF = Integer.parseInt(elementBuffer.toString());
					else if (tmpBranche.endsWith(".classification.taxonpath.taxon.maxeqf"))
						maxEQF = Integer.parseInt(elementBuffer.toString());
					doc.add(new Field(tmpBranche.replaceAll("mineqf", "").replaceAll("maxeqf", "") + "eqf.range", minEQF+ "_" + maxEQF, Field.Store.YES,Field.Index.UN_TOKENIZED));
					for (int i = minEQF; i <= maxEQF; i++) {
						doc.add(new Field(tmpBranche.replaceAll("mineqf", "").replaceAll("maxeqf", "") + "eqf", Integer.toString(i), Field.Store.YES,Field.Index.UN_TOKENIZED));
						doc.add(new Field(tmpBranche.replaceAll("mineqf", "").replaceAll("maxeqf", "") + "competency.eqf", competencyID + "_" + Integer.toString(i), Field.Store.YES,Field.Index.UN_TOKENIZED));
						doc.add(new Field(tmpBranche.replaceAll("mineqf", "").replaceAll("maxeqf", "") + "domain.eqf", domainID + "_" + Integer.toString(i), Field.Store.YES,Field.Index.UN_TOKENIZED));
					}
					competencyCount = 0;
				}
				isCompetency = false;
			}
		}
		// Title
		else if (tmpBranche.matches(".*title.*")) {
			if (tmpBranche.endsWith("title.string")) {
				doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer.toString().trim(), Field.Store.YES,Field.Index.TOKENIZED));// XXX
				doc.add(new Field(tmpBranche.toLowerCase() + ".exact",elementBuffer.toString().trim(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX

			}
		}
		// Contribute
		else if (tmpBranche.matches(".*contribute\\.((role)|(entity)|(date)).*")) {
			if (tmpBranche.endsWith("contribute.role.source")) {
				source = elementBuffer.toString().trim();
				doc.add(new Field(tmpBranche.toLowerCase(), source.toLowerCase(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX

			} else if (tmpBranche.endsWith("contribute.role.value")) {
				source += EQUAL_SEPARATOR + "" + elementBuffer.toString().trim();// TODO
				doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer.toString().toLowerCase().trim(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX

			} else if (tmpBranche.endsWith("contribute.entity")) {
				String fieldName = tmp2Branche + "" + EQUAL_SEPARATOR + ""+ source;
				doc.add(new Field(fieldName.toLowerCase(), elementBuffer.toString().toLowerCase().trim(), Field.Store.YES,Field.Index.TOKENIZED));// XXX
				doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer.toString().toLowerCase().trim(), Field.Store.YES,Field.Index.TOKENIZED));// XXX

			} else if (tmpBranche.endsWith("contribute.date.datetime")) {
				String fieldname = tmp2Branche + "" + EQUAL_SEPARATOR + "" + source;
				doc.add(new Field(fieldname.toLowerCase(), elementBuffer.toString().toLowerCase().trim(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX

				// para poder soportar busquedas con rangos
				String date = elementBuffer.toString().toLowerCase().trim().replaceAll("-", "").replaceAll("t", "").replaceAll(":", "").replaceAll("\\.", "").replaceAll("z","");
				if (date.length() > 15)
					date = date.substring(0, 15);
				//				
				doc.add(new Field(tmp2Branche.toLowerCase(), date,Field.Store.YES, Field.Index.UN_TOKENIZED));// XXX

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
				String attValue = ((String) attributeMap.get(attName)).toLowerCase();

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
						doc.add(new Field(fieldName.toLowerCase(), ageRange[z].toLowerCase(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX
					}
				}
			}

		}
		// resource. Catalog + entry
		else if (tmpBranche.matches(".*resource.identifier\\.((catalog)|(entry))")) {
			if (tmpBranche.endsWith("identifier.catalog")) {
				identifier = "catalog" + EQUAL_SEPARATOR + "" + elementBuffer.toString().trim();
				doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer.toString().toLowerCase().trim(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX

			} else if (tmpBranche.endsWith("identifier.entry")) {
				doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer.toString().toLowerCase().trim(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX
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
				catalog = elementBuffer.toString().trim().replace("\n", "");
				doc.add(new Field(tmpBranche.toLowerCase(), catalog, Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX

			} else if (tmpBranche.endsWith("identifier.entry")) {
				doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer.toString().trim(), Field.Store.YES,Field.Index.UN_TOKENIZED));
				doc.add(new Field(tmpBranche.toLowerCase() + BRANCH_SEPARATOR + "exact" , elementBuffer.toString().trim(), Field.Store.YES,Field.Index.UN_TOKENIZED));
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
				source = "source" + EQUAL_SEPARATOR + elementBuffer.toString().trim();
				doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer.toString().toLowerCase().trim(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX

			} else if (tmpBranche.endsWith("value")) {
				doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer.toString().toLowerCase().trim(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX
				String fieldName = tmp2Branche + "" + BRANCH_SEPARATOR + "" + source + BRANCH_SEPARATOR + "value";
				doc.add(new Field(fieldName.toLowerCase(), elementBuffer.toString().toLowerCase().trim(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX
			}
		}
		// In all the other cases add a field !
		else {
			doc.add(new Field(tmpBranche.toLowerCase(), elementBuffer.toString().toLowerCase(), Field.Store.YES,Field.Index.UN_TOKENIZED));// XXX
		}
		// <---
		// to store the contents without metatags
		contents = contents.concat(" " + elementBuffer.toString().toLowerCase());
		elementBuffer.setLength(0);
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
		String filePath = "/Work/MACE/XMLs/14470.lo.1.xml";
//		String filePath = "/Work/MACE/XMLs/2006091001020.xml"; 
//		String filePath = "/Work/MACE/XMLs/katja/problematicXML.xml";
		Document doc = handler.getDocument(new FileInputStream(new File(filePath)));
		List fields = doc.getFields();
		for (Iterator iterator = fields.iterator(); iterator.hasNext();) {
			Field field = (Field) iterator.next();
			System.out.println(field.name() + " :: " + field.stringValue());

		}

	}
}
