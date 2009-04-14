package org.ariadne_eu.oai.server.oracle.crosswalk;

import java.util.HashMap;
import java.util.Properties;

import org.ariadne_eu.utils.config.RepositoryConstants;
import org.oclc.oai.server.crosswalk.Crosswalk;
import org.oclc.oai.server.verb.CannotDisseminateFormatException;

public class Oracle2oai_lom extends Crosswalk {

	private String column_xml = "";

	public Oracle2oai_lom(Properties properties) {
		super("http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd");
//		String catalogClassName = "IbmDb2LomCatalog";
//		column_xml  = properties.getProperty(catalogClassName + ".db.column.xml");
		column_xml = properties.getProperty(RepositoryConstants.MD_DB_XMLDB_SQL_COLUMNNAME);
		if (column_xml == null) {
//			throw new IllegalArgumentException(catalogClassName + ".db.column.xml is missing from the properties file");
			throw new IllegalArgumentException(RepositoryConstants.MD_DB_XMLDB_SQL_COLUMNNAME + " is missing from the properties file");
		}
	}

	public String createMetadata(Object nativeItem) throws CannotDisseminateFormatException {
		
		String result = "";
		result = (String)((HashMap)nativeItem).get(column_xml);
        // Create a pattern to match cat
        result = result.replaceFirst("\\<\\?xml version=\"1.0\" encoding=\".*\"\\?>","");
		return result;//*/
	}

	public boolean isAvailableFor(Object arg0) {
		return true;
	}

}
