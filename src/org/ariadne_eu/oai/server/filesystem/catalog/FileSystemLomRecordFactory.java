package org.ariadne_eu.oai.server.filesystem.catalog;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Properties;
import java.util.TimeZone;

import org.ariadne_eu.oai.server.catalog.AbstractRecordFactory;


public class FileSystemLomRecordFactory extends AbstractRecordFactory{
    

    public FileSystemLomRecordFactory(Properties properties) throws IllegalArgumentException {
		super(properties);
		// TODO Auto-generated constructor stub
	}

    /**
     * Extract the local identifier from the native item
     *
     * @param nativeItem native Item object
     * @return local identifier
     */
    public String getLocalIdentifier(Object nativeItem) {
    	File doc = new File((String)nativeItem);

    	return doc.getName().substring(0,doc.getName().lastIndexOf(".")).replaceAll("_", ":").replaceAll(".s.", "/");
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
    	File doc = new File((String)nativeItem);
    	Calendar lastMod = GregorianCalendar.getInstance();
		lastMod.setTimeInMillis(doc.lastModified());
		String pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
    	return format.format(lastMod.getTime());
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

