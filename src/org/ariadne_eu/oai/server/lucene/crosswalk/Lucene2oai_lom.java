package org.ariadne_eu.oai.server.lucene.crosswalk;

import java.util.Properties;

import org.apache.lucene.document.Document;
import org.oclc.oai.server.crosswalk.Crosswalk;
import org.oclc.oai.server.verb.CannotDisseminateFormatException;

public class Lucene2oai_lom extends Crosswalk {

	protected String fullLomField = "";
	
	public Lucene2oai_lom(Properties properties) {
		super("http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd");
		String classname = "Lucene2oai_lom";
		fullLomField = properties.getProperty(classname + ".fullLomField");
		if (fullLomField == null) {
		    throw new IllegalArgumentException(classname + ".fullLomField is missing from the properties file");
		}
	}

	public String createMetadata(Object nativeItem) throws CannotDisseminateFormatException {
		//Cast the nativeItem to your object
		Document doc = (Document)nativeItem; 
		String lom = doc.getField(fullLomField).stringValue();

		return lom;
	}

	public boolean isAvailableFor(Object arg0) {
		return true;
	}

}
