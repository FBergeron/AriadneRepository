package org.ariadne_eu.oai.server.mysql.crosswalk;
//package org.ariadne.oai.server.mysql.crosswalk;
//
//import java.util.HashMap;
//import java.util.Properties;
//
//import javax.xml.bind.JAXBException;
//
//import org.ariadne.oai.utils.OaiUtils;
//import org.ieee.ltsc.lom.impl.LOMImpl;
//import org.oclc.oai.server.crosswalk.Crosswalk;
//import org.oclc.oai.server.verb.CannotDisseminateFormatException;
//
//public class MySql2oai_lom extends Crosswalk {
//
//	public MySql2oai_lom(Properties properties) {
//		super("http://ltsc.ieee.org/xsd/LOM http://standards.ieee.org/reading/ieee/downloads/LOM/lomv1.0/xsd/lom.xsd");
//
//	}
//
//	public String createMetadata(Object nativeItem) throws CannotDisseminateFormatException {
//		//Cast the nativeItem to your object
//        HashMap table = (HashMap)nativeItem;
//		
//		//set the Identifier
//		LOMImpl lom = new LOMImpl();
//		LOMImpl.General.Identifier id = lom.newGeneral().newIdentifier(-1);
//		id.newEntry().setString((String)table.get("learning_object.identifier"));
//		
//		//set the Title
//		org.ieee.ltsc.datatype.impl.LangStringImpl.StringImpl title = lom.newGeneral().newTitle().newString(-1);
//		title.setString((String)table.get("learning_object.title"));
//		title.newLanguage().setValue("en"); //todo: obtain language automatically
//		
//		
//		
//		String result = "";
//		try {
//			result = OaiUtils.parseLom2Xmlstring(lom);
//			result = result.replaceFirst("\\<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?\\>", "");
//		} catch (JAXBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return result;//*/
//	}
//
//	public boolean isAvailableFor(Object arg0) {
//		return true;
//	}
//
//}
