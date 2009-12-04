package org.ariadne_eu.oai.server.ibmdb2.catalog;


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.ariadne_eu.oai.server.catalog.AbstractRecordFactory;
import org.ariadne_eu.oai.utils.TargetUtils;
import org.ariadne_eu.utils.config.RepositoryConstants;


public class IbmDb2LomRecordFactory extends AbstractRecordFactory{
	protected String column_id = "";
	protected String column_datestamp = "";

	public IbmDb2LomRecordFactory(Properties properties)	throws IllegalArgumentException {
		super(properties);
		column_id = properties.getProperty(RepositoryConstants.MD_DB_XMLDB_SQL_IDCOLUMNNAME);
		if (column_id == null) {
			throw new IllegalArgumentException(RepositoryConstants.MD_DB_XMLDB_SQL_IDCOLUMNNAME + " is missing from the properties file");
		}
		column_datestamp = properties.getProperty(RepositoryConstants.OAICAT_SERVER_CATALOG_DATECOLUMN);
		if (column_datestamp == null) {
			throw new IllegalArgumentException(RepositoryConstants.OAICAT_SERVER_CATALOG_DATECOLUMN + " is missing from the properties file");
		}
	}

	/**
	 * Extract the local identifier from the native item
	 *
	 * @param nativeItem native Item object
	 * @return local identifier
	 */
	public String getLocalIdentifier(Object nativeItem) {
		return (String) ((HashMap)nativeItem).get(column_id);
	}

	/**
	 * get the datestamp from the item
	 *
	 * @param nativeItem a native item presumably containing a datestamp somewhere
	 * @return a String containing the datestamp for the item
	 * @exception IllegalArgumentException Something is wrong with the argument.
	 */
	public String getDatestamp(Object nativeItem)
	throws IllegalArgumentException  {
		try {
		String date = ((Timestamp)((HashMap)nativeItem).get(column_datestamp)).toString();
		//String newDate = date.replaceFirst(" ", "T");
		//newDate = newDate.replaceFirst("\\..*", "Z");
		return TargetUtils.convertLocaleIbmDB2DateTimeToUTC(date);
		}
		catch(Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}



	/**
	 * get the setspec from the item
	 *
	 * @param nativeItem a native item presumably containing a setspec somewhere
	 * @return a String containing the setspec for the item
	 * @exception IllegalArgumentException Something is wrong with the argument.
	 */
	public Iterator getSetSpecs(Object nativeItem)
	throws IllegalArgumentException  {
		return null;
	}

	/**
	 * Get the about elements from the item
	 *
	 * @param nativeItem a native item presumably containing about information somewhere
	 * @return a Iterator of Strings containing &lt;about&gt;s for the item
	 * @exception IllegalArgumentException Something is wrong with the argument.
	 */
	public Iterator getAbouts(Object nativeItem) throws IllegalArgumentException {
		return null;
	}

	/**
	 * Is the record deleted?
	 *
	 * @param nativeItem a native item presumably containing a possible delete indicator
	 * @return true if record is deleted, false if not
	 * @exception IllegalArgumentException Something is wrong with the argument.
	 */
	public boolean isDeleted(Object nativeItem)
	throws IllegalArgumentException {
		return false;
	}

	/**
	 * Allows classes that implement RecordFactory to override the default create() method.
	 * This is useful, for example, if the entire &lt;record&gt; is already packaged as the native
	 * record. Return null if you want the default handler to create it by calling the methods
	 * above individually.
	 * 
	 * @param nativeItem the native record
	 * @param schemaURL the schemaURL desired for the response
	 * @param the metadataPrefix from the request
	 * @return a String containing the OAI &lt;record&gt; or null if the default method should be
	 * used.
	 */
	public String quickCreate(Object nativeItem, String schemaLocation, String metadataPrefix) {
		// Don't perform quick creates
		return null;
	}
}

