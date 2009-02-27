package org.ariadne_eu.oai.server.sqi.crosswalk;
//package org.ariadne.oai.server.sqi.crosswalk;
//
//import java.util.Properties;
//
//import org.apache.crimson.tree.XmlDocument;
//import org.oclc.oai.server.crosswalk.Crosswalk;
//import org.oclc.oai.server.verb.CannotDisseminateFormatException;
//import org.w3c.dom.Node;
//
//public class Sqi2oai_lom extends Crosswalk {
//
//	public Sqi2oai_lom(Properties properties) {
//		super("http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd");
//
//	}
//
//	public String createMetadata(Object nativeItem) throws CannotDisseminateFormatException {
//		
//		String result = "";
//		result = ((XmlDocument)nativeItem).getFirstChild().toString();
//		//result = result.replaceFirst("\\<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?\\>", "");
//		return result;//*/
//	}
//
//	public boolean isAvailableFor(Object arg0) {
//		return true;
//	}
//
//}
