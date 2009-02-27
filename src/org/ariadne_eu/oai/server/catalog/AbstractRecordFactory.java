package org.ariadne_eu.oai.server.catalog;


import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import org.ariadne.config.PropertiesManager;
import org.oclc.oai.server.catalog.RecordFactory;


public abstract class AbstractRecordFactory extends RecordFactory{
	protected String repositoryIdentifier = null;
	protected boolean useOaiIdScheme = true;


	public AbstractRecordFactory(Properties properties)	throws IllegalArgumentException {
		super(properties);
		repositoryIdentifier = properties.getProperty("Identify.repositoryIdentifier");
		if (repositoryIdentifier == null) {
			throw new IllegalArgumentException("Identify.repositoryIdentifier is missing from the properties file");
		}
		String property = PropertiesManager.getProperty("OAIHandler.useOaiIdScheme");
		if (property == null) {
			throw new IllegalArgumentException("OAIHandler.useOaiIdScheme is missing from the properties file");
		}
		else {
			useOaiIdScheme = Boolean.getBoolean(property);
		}
	}



	/**
	 * Utility method to parse the 'local identifier' from the OAI identifier
	 *
	 * @param identifier OAI identifier (e.g. oai:oaicat.oclc.org:ID/12345)
	 * @return local identifier (e.g. ID/12345).
	 */
	public String fromOAIIdentifier(String identifier) {
		try {
			StringTokenizer tokenizer = new StringTokenizer(identifier, ":");
			tokenizer.nextToken();
			tokenizer.nextToken();
			String result = tokenizer.nextToken();
			while(tokenizer.hasMoreElements()){
				result = result.concat(":" + tokenizer.nextToken());
			}
			return result;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Construct an OAI identifier from the native item
	 *
	 * @param nativeItem native Item object
	 * @return OAI identifier
	 */
	public String getOAIIdentifier(Object nativeItem) {
		StringBuffer stringb = new StringBuffer();
		if(useOaiIdScheme) {
			stringb.append("oai:");
			stringb.append(repositoryIdentifier);
			stringb.append(":");
		}
		stringb.append(getLocalIdentifier(nativeItem));
		return stringb.toString();
	}

	/**
	 * Extract the local identifier from the native item
	 *
	 * @param nativeItem native Item object
	 * @return local identifier
	 */
	public abstract String getLocalIdentifier(Object nativeItem);
	
	/**
	 * get the datestamp from the item
	 *
	 * @param nativeItem a native item presumably containing a datestamp somewhere
	 * @return a String containing the datestamp for the item
	 * @exception IllegalArgumentException Something is wrong with the argument.
	 */
	public abstract String getDatestamp(Object nativeItem);

	/**
	 * get the setspec from the item
	 *
	 * @param nativeItem a native item presumably containing a setspec somewhere
	 * @return a String containing the setspec for the item
	 * @exception IllegalArgumentException Something is wrong with the argument.
	 */
	public abstract Iterator getSetSpecs(Object nativeItem);

	/**
	 * Get the about elements from the item
	 *
	 * @param nativeItem a native item presumably containing about information somewhere
	 * @return a Iterator of Strings containing &lt;about&gt;s for the item
	 * @exception IllegalArgumentException Something is wrong with the argument.
	 */
	public abstract Iterator getAbouts(Object nativeItem) throws IllegalArgumentException;

	/**
	 * Is the record deleted?
	 *
	 * @param nativeItem a native item presumably containing a possible delete indicator
	 * @return true if record is deleted, false if not
	 * @exception IllegalArgumentException Something is wrong with the argument.
	 */
	public abstract boolean isDeleted(Object nativeItem);

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
	public abstract String quickCreate(Object nativeItem, String schemaLocation, String metadataPrefix);
}

