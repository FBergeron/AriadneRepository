package org.ariadne_eu.oai.server.filesystem.crosswalk;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.ariadne_eu.oai.utils.OaiUtils;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.oclc.oai.server.crosswalk.Crosswalk;
import org.oclc.oai.server.verb.CannotDisseminateFormatException;

public class FileSystem2oai_lom extends Crosswalk {
	
	protected static Namespace lomns = Namespace.getNamespace("http://ltsc.ieee.org/xsd/LOM");
	protected static Namespace xsi = Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");

	public FileSystem2oai_lom(Properties properties) {
		super("http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd");

	}

	public String createMetadata(Object nativeItem) throws CannotDisseminateFormatException {
		//Cast the nativeItem to your object
		try {
	    	SAXBuilder builder = new SAXBuilder();
			org.jdom.Document jdom = builder.build(new File((String)nativeItem));
			return OaiUtils.parseLom2XmlstringNoXmlHeader(jdom.getRootElement());
		} catch (JDOMException e) {
			throw new CannotDisseminateFormatException(e.getMessage());
			} catch (IOException e) {
				throw new CannotDisseminateFormatException(e.getMessage());
		}
	}

	public boolean isAvailableFor(Object arg0) {
		return true;
	}

}
