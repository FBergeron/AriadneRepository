package org.ariadne_eu.oai.server.filesystem.catalog;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.ariadne.config.PropertiesManager;
import org.ariadne_eu.utils.config.RepositoryConstants;
import org.oclc.oai.server.catalog.AbstractCatalog;
import org.oclc.oai.server.verb.BadArgumentException;
import org.oclc.oai.server.verb.BadResumptionTokenException;
import org.oclc.oai.server.verb.CannotDisseminateFormatException;
import org.oclc.oai.server.verb.IdDoesNotExistException;
import org.oclc.oai.server.verb.NoItemsMatchException;
import org.oclc.oai.server.verb.NoMetadataFormatsException;
import org.oclc.oai.server.verb.NoSetHierarchyException;
import org.oclc.oai.server.verb.OAIInternalServerError;
import org.oclc.oai.util.OAIUtil;

/**
 * Created by IntelliJ IDEA.
 * User: stefaan
 * Date: 11-nov-2006
 * Time: 16:50:33
 * To change this template use File | Settings | File Templates.
 */
public class FileSystemLomCatalog extends AbstractCatalog {

	/**
	 * pending resumption tokens
	 */
	private HashMap resumptionResults = new HashMap();

	private static int maxListSize;

	private static String basePath;

	private static String ext;

	private static HashMap<String, String> sets = new HashMap<String, String>();

	public FileSystemLomCatalog(Properties properties) {
		String maxListSize = properties.getProperty(RepositoryConstants.OAICAT_SERVER_CATALOG_MAXLSTSIZE);
		if (maxListSize == null) {
			throw new IllegalArgumentException(RepositoryConstants.OAICAT_SERVER_CATALOG_MAXLSTSIZE + " is missing from the properties file");
		} else {
			FileSystemLomCatalog.maxListSize = Integer.parseInt(maxListSize);
		}
		String basePath = properties.getProperty(RepositoryConstants.MD_SPIFS_DIR);
		if (basePath == null) {
			throw new IllegalArgumentException(RepositoryConstants.OAICAT_SERVER_CATALOG_MAXLSTSIZE + " is missing from the properties file");
		} else {
			FileSystemLomCatalog.basePath = basePath;
		}
		String ext = properties.getProperty(RepositoryConstants.OAICAT_SERVER_CATALOG_FS_EXT);
		if (ext == null) {
			throw new IllegalArgumentException(RepositoryConstants.OAICAT_SERVER_CATALOG_FS_EXT + " is missing from the properties file");
		} else {
			FileSystemLomCatalog.ext = ext;
		}
		try {

			Hashtable setKeys = PropertiesManager.getInstance().getPropertyStartingWith(RepositoryConstants.OAICAT_SETS);
			String[] keys = (String[]) setKeys.keySet().toArray(new String[0]);
			String reposIdentifier = "";
			for(String key : keys) {
				String setSpec = key.replace(RepositoryConstants.OAICAT_SETS + ".", "").replace("."+RepositoryConstants.OAICAT_SETS_ID,"");
				reposIdentifier = PropertiesManager.getInstance().getProperty(key);
				sets.put(setSpec, reposIdentifier);
			}
		} catch (Exception e) {
			//NOOP
		}
	}

	public Map listSets() throws NoSetHierarchyException, OAIInternalServerError {
		Hashtable setKeys = PropertiesManager.getInstance().getPropertyStartingWith(RepositoryConstants.OAICAT_SETS);
		String[] keys = (String[]) setKeys.keySet().toArray(new String[0]);
		if(keys.length == 0) {
			throw new NoSetHierarchyException();
		}
		else {
			purge(); // clean out old resumptionTokens
			Map listSetsMap = new HashMap();
			ArrayList sets = new ArrayList();

			for(String key : keys) {
				String setSpec = key.replace(RepositoryConstants.OAICAT_SETS + ".", "").replace("."+RepositoryConstants.OAICAT_SETS_ID,"");
				sets.add(getSetXML(key,setSpec));
			}

			listSetsMap.put("sets", sets.iterator());
			return listSetsMap;  
		}
	}

	/**
	 * Extract &lt;set&gt; XML string from setItem object
	 *
	 * @param setItem individual set instance in native format
	 * @return an XML String containing the XML &lt;set&gt; content
	 */
	public String getSetXML(String key, String setSpec)
	throws IllegalArgumentException {
		String setName = "Metadata originating from " + setSpec;
		String setDescription = "RepositoryIdentifier is " + PropertiesManager.getInstance().getProperty(key);

		StringBuffer sb = new StringBuffer();
		sb.append("<set>");
		sb.append("<setSpec>");
		sb.append(OAIUtil.xmlEncode(setSpec));
		sb.append("</setSpec>");
		sb.append("<setName>");
		sb.append(OAIUtil.xmlEncode(setName));
		sb.append("</setName>");
		if (setDescription != null) {
			sb.append("<setDescription>");
			sb.append(OAIUtil.xmlEncode(setDescription));
			sb.append("</setDescription>");
		}
		sb.append("</set>");
		return sb.toString();
	}

	public Map listSets(String resumptionToken) throws BadResumptionTokenException, OAIInternalServerError {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public Vector getSchemaLocations(String identifier) throws IdDoesNotExistException, NoMetadataFormatsException, OAIInternalServerError {
		Object nativeItem = null;
		/*
		 * Let your recordFactory decide which schemaLocations
		 * (i.e. metadataFormats) it can produce from the record.
		 * Doing so will preserve the separation of database access
		 * (which happens here) from the record content interpretation
		 * (which is the responsibility of the RecordFactory implementation).
		 */
		if (nativeItem == null) {
			throw new IdDoesNotExistException(identifier);
		} else {
			return getRecordFactory().getSchemaLocations(nativeItem);
		}
	}

	@SuppressWarnings("unchecked")
	public Map listIdentifiers(String from, String until, String set, String metadataPrefix) throws BadArgumentException, CannotDisseminateFormatException, NoItemsMatchException, NoSetHierarchyException, OAIInternalServerError {
		purge(); // clean out old resumptionTokens
		Map listIdentifiersMap = new HashMap();
		ArrayList headers = new ArrayList();
		ArrayList identifiers = new ArrayList();

		Query query = null;
		String fromDate = from.replaceAll("-", "");
		fromDate = fromDate.replaceAll(":", "");
		fromDate = fromDate.replaceAll("T", "");
		fromDate = fromDate.replaceAll("Z", "");
		String untilDate = until.replaceAll("-", "");
		untilDate = untilDate.replaceAll(":", "");
		untilDate = untilDate.replaceAll("T", "");
		untilDate = untilDate.replaceAll("Z", "");
		Term termFrom = new Term("lastModDate", fromDate);
		Term termUntil = new Term("lastModDate", untilDate);
		RangeQuery rangeQuery = new RangeQuery(termFrom,termUntil,true);
		Hits hits = null;
		if(hits.length() == 0) throw new NoItemsMatchException();

		/* Get some records from your database */
		int count = 0;
		/* load the records ArrayList */
		Object[] nativeItem = new Object[hits.length()];
		for (int i = 0; i < hits.length(); i++) {
			try {
				nativeItem[i] = hits.doc(i);
			} catch (IOException e) {

				e.printStackTrace();

			}
		}	
		for (count=0; count < maxListSize && count < hits.length(); ++count) {
			//record = constructRecord(nativeItem[count], metadataPrefix);
			//records.add(record);
			/* Use the RecordFactory to extract header/identifier pairs for each item */
			String[] header = getRecordFactory().createHeader(nativeItem[count]);
			headers.add(header[0]);
			identifiers.add(header[1]);
		}

		/* decide if you're done */
		if (count < hits.length()) {
			String resumptionId = getResumptionId();

			/*****************************************************************
			 * Store an object appropriate for your database API in the
			 * resumptionResults Map in place of nativeItems. This object
			 * should probably encapsulate the information necessary to
			 * perform the next resumption of ListIdentifiers. It might even
			 * be possible to encode everything you need in the
			 * resumptionToken, in which case you won't need the
			 * resumptionResults Map. Here, I've done a silly combination
			 * of the two. Stateless resumptionTokens have some advantages.
			 *****************************************************************/
			resumptionResults.put(resumptionId, nativeItem);

			/*****************************************************************
			 * Construct the resumptionToken String however you see fit.
			 *****************************************************************/
			StringBuffer resumptionTokenSb = new StringBuffer();
			resumptionTokenSb.append(resumptionId);
			resumptionTokenSb.append(":");
			resumptionTokenSb.append(Integer.toString(count));
			resumptionTokenSb.append(":");
			resumptionTokenSb.append(metadataPrefix);

			listIdentifiersMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(),
					hits.length(),
					0));
		}

		listIdentifiersMap.put("headers", headers.iterator());
		listIdentifiersMap.put("identifiers", identifiers.iterator());
		return listIdentifiersMap;
	}

	private String constructRecord(Object nativeItem, String metadataPrefix)
	throws CannotDisseminateFormatException {
		String schemaURL = null;

		if (metadataPrefix != null) {
			if ((schemaURL = getCrosswalks().getSchemaURL(metadataPrefix)) == null)
				throw new CannotDisseminateFormatException(metadataPrefix);
		}
		return getRecordFactory().create(nativeItem, schemaURL, metadataPrefix);
	}

	/**
	 * Use the current date as the basis for the resumptiontoken
	 *
	 * @return a String version of the current time
	 */
	private synchronized static String getResumptionId() {
		Date now = new Date();
		return Long.toString(now.getTime());
	}

	/**
	 * Purge tokens that are older than the configured time-to-live.
	 */
	private void purge() {
		ArrayList old = new ArrayList();
		Date now = new Date();
		Iterator keySet = resumptionResults.keySet().iterator();
		while (keySet.hasNext()) {
			String key = (String)keySet.next();
			Date then = new Date(Long.parseLong(key) + getMillisecondsToLive());
			if (now.after(then)) {
				old.add(key);
			}
		}
		Iterator iterator = old.iterator();
		while (iterator.hasNext()) {
			String key = (String)iterator.next();
			resumptionResults.remove(key);
		}
	}

	/**
	 * Retrieve a list of records that satisfy the specified criteria. Note, though,
	 * that unlike the other OAI verb type methods implemented here, both of the
	 * listRecords methods are already implemented in AbstractCatalog rather than
	 * abstracted. This is because it is possible to implement ListRecords as a
	 * combination of ListIdentifiers and GetRecord combinations. Nevertheless,
	 * I suggest that you override both the AbstractCatalog.listRecords methods
	 * here since it will probably improve the performance if you create the
	 * response in one fell swoop rather than construct it one GetRecord at a time.
	 *
	 * @param from beginning date using the proper granularity
	 * @param until ending date using the proper granularity
	 * @param set the set name or null if no such limit is requested
	 * @param metadataPrefix the OAI metadataPrefix or null if no such limit is requested
	 * @return a Map object containing entries for a "records" Iterator object
	 * (containing XML <record/> Strings) and an optional "resumptionMap" Map.
	 * @exception CannotDisseminateFormatException the metadataPrefix isn't
	 * supported by the item.
	 */
	@SuppressWarnings("unchecked")
	public Map listRecords(String from, String until, String set, String metadataPrefix)
	throws CannotDisseminateFormatException {

		if(sets.get(set) != null) {
			set = sets.get(set);
		}
		if (set == null)set = "";
		
		return listRecords(from, until, set, metadataPrefix, 0, new Vector<Integer>());
	}

	private Map listRecords(String from, String until, String set,
			String metadataPrefix, int start, Vector<Integer> pointerVector) throws CannotDisseminateFormatException {
		purge(); // clean out old resumptionTokens
		Map listRecordsMap = new HashMap();
		ArrayList records = new ArrayList();

		/************************************************************************************
		 * perform the query on your DB according to the given parameters from, until and set
		 ************************************************************************************/
		File dir = new File(basePath + File.separator + set);

		/** End Query **/

		int count = 0;

		/************************************************************************************
		 * create an Object[] nativeItem that contains all the results in your DB-format 
		 ************************************************************************************/

		String pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		format.setTimeZone(TimeZone.getTimeZone("UTC"));

		Date fromParsed = null;
		Date untilParsed = null;
		try {
			untilParsed = format.parse(until);
			fromParsed = format.parse(from);
		} catch (ParseException e) {
			throw new CannotDisseminateFormatException(e.getMessage());
		}
		Vector nativeItem = getMatchingFiles(dir, fromParsed, untilParsed, start, maxListSize, pointerVector);
		if(pointerVector.size() > 0)pointerVector.remove(0);

		/** End Create **/
		String record;
		for (count=0; count < maxListSize && count < nativeItem.size(); ++count) {
			record = constructRecord(nativeItem.get(count), metadataPrefix);
			records.add(record);
		}

		/* decide if you're done */
		if (nativeItem.size() == maxListSize) {
			String resumptionId = getResumptionId();

			/*****************************************************************
			 * Store an object appropriate for your database API in the
			 * resumptionResults Map in place of nativeItems. This object
			 * should probably encapsulate the information necessary to
			 * perform the next resumption of ListIdentifiers. It might even
			 * be possible to encode everything you need in the
			 * resumptionToken, in which case you won't need the
			 * resumptionResults Map. Here, I've done a silly combination
			 * of the two. Stateless resumptionTokens have some advantages.
			 *****************************************************************/
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("from", from);
			params.put("until", until);
			params.put("set", set);
			params.put("pointerVector", pointerVector);
			resumptionResults.put(resumptionId, params);

			/*****************************************************************
			 * Construct the resumptionToken String however you see fit.
			 *****************************************************************/
			StringBuffer resumptionTokenSb = new StringBuffer();
			resumptionTokenSb.append(resumptionId);
			resumptionTokenSb.append(":");
			resumptionTokenSb.append(start+maxListSize);
			resumptionTokenSb.append(":");
			resumptionTokenSb.append(metadataPrefix);

			listRecordsMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(),
					dir.list().length,
					start+maxListSize));
		}

		listRecordsMap.put("records", records.iterator());
		return listRecordsMap;
	}

	@SuppressWarnings("unchecked")
	/**
	 * param pointerVector : first element is the pointer in the query, the rest of the elements are part of the pointerVector in the whole fileList
	 */
	private Vector getMatchingFiles(File dir, Date date, Date date2, int start, int size, Vector<Integer> pointerVector) {
		Vector nativeItem = new Vector();
		if(dir.exists()) {

			String[] files = dir.list();
			long fromLong = date.getTime();
			long untilLong = date2.getTime();
			int pointer = 0;
			int previous = 0;
			int i = 0;
			if(pointerVector != null && pointerVector.size() > 0) {
				previous = pointerVector.remove(0).intValue();
				pointer = start;
			}

			for(i = previous; i< files.length; i++) {
				String fileString = files[i];
				if(nativeItem.size() >= size) {
					break;
				}
				String pathname = dir.toString() + File.separator + fileString;
				File file = new File(pathname);
				if(file.isFile()) {
					//String name = file.getName();
					if(fileString.substring(fileString.lastIndexOf(".")+1).equalsIgnoreCase(ext)) {
						long lastmod = file.lastModified();
						if(lastmod >= fromLong && lastmod <= untilLong) {
							if(pointer++ >= start)	nativeItem.add(pathname);
						}
					}
				}
				else {
					int diff = start - pointer;
					int subStart = diff;
					int subSize = size - nativeItem.size();
					if(diff < 0) {
						//subSize += diff;
						subStart = 0;
					}
					Vector matchingFiles = getMatchingFiles(file, date, date2,subStart, subSize, pointerVector);
					pointer += pointerVector.remove(0);
					nativeItem.addAll(matchingFiles);
					//pointer += subStart + matchingFiles.size();
					if(nativeItem.size() >= size) {
						break;
					}
				}
			}
			if(i == files.length) {
				//pointerVector.insertElementAt(-1, 0);
			}else {
				pointerVector.insertElementAt(i, 0);	
			}

			pointerVector.insertElementAt(pointer, 0);
			//nativeItem.insertElementAt(pointerVector, 0);
		}
		return nativeItem;
	}

	private String getMatchingFile(File dir, String identifier) {
		if(dir.exists()) {
			String[] files = dir.list();
			for(int i = 0; i< files.length; i++) {
				String fileString = files[i];
				String pathname = dir.toString() + File.separator + fileString;
				File file = new File(pathname);
				if(file.isFile()) {
					//String name = file.getName();
					if(fileString.substring(fileString.lastIndexOf(".")+1).equalsIgnoreCase(ext)) {
						if(fileString.substring(0,fileString.lastIndexOf(".")).equals(identifier)) {
							return pathname;
						}
					}
				}
				else {
					String matchingFile = getMatchingFile(file, identifier);
					if(!matchingFile.equals("")) {
						return matchingFile;
					}
				}
			}			
		}
		return "";
	}

	/**
	 * Retrieve the next set of records associated with the resumptionToken
	 *
	 * @param resumptionToken implementation-dependent format taken from the
	 * previous listRecords() Map result.
	 * @return a Map object containing entries for "headers" and "identifiers" Iterators
	 * (both containing Strings) as well as an optional "resumptionMap" Map.
	 * @exception BadResumptionTokenException the value of the resumptionToken argument
	 * is invalid or expired.
	 */
	@SuppressWarnings("unchecked")
	public Map listRecords(String resumptionToken)
	throws BadResumptionTokenException {
		StringTokenizer tokenizer = new StringTokenizer(resumptionToken, ":");
		String resumptionId;
		int oldCount;
		String metadataPrefix;
		try {
			resumptionId = tokenizer.nextToken();
			oldCount = Integer.parseInt(tokenizer.nextToken());
			metadataPrefix = tokenizer.nextToken();
		} catch (NoSuchElementException e) {
			throw new BadResumptionTokenException();
		}
		HashMap<String, Object> params = (HashMap<String, Object>)resumptionResults.remove(resumptionId);
		if (params == null) {
			throw new BadResumptionTokenException();
		}
		try {
			return listRecords((String)params.get("from"), (String)params.get("until"), (String)params.get("set"), metadataPrefix, oldCount, (Vector<Integer>)params.get("pointerVector"));
		} catch (CannotDisseminateFormatException e) {
			throw new BadResumptionTokenException();
		}
	}

	@SuppressWarnings("unchecked")
	public Map listIdentifiers(String resumptionToken) throws BadResumptionTokenException, OAIInternalServerError {
		purge(); // clean out old resumptionTokens
		Map listIdentifiersMap = new HashMap();
		ArrayList headers = new ArrayList();
		ArrayList identifiers = new ArrayList();

		StringTokenizer tokenizer = new StringTokenizer(resumptionToken, ":");
		String resumptionId;
		int oldCount;
		String metadataPrefix;
		try {
			resumptionId = tokenizer.nextToken();
			oldCount = Integer.parseInt(tokenizer.nextToken());
			metadataPrefix = tokenizer.nextToken();
		} catch (NoSuchElementException e) {
			throw new BadResumptionTokenException();
		}

		/* Get some more records from your database */
		Object[] nativeItems = (Object[])resumptionResults.remove(resumptionId);
		if (nativeItems == null) {
			throw new BadResumptionTokenException();
		}
		int count;

		/* load the headers and identifiers ArrayLists. */
		for (count = 0; count < maxListSize && count+oldCount < nativeItems.length; ++count) {
			/* Use the RecordFactory to extract header/identifier pairs for each item */
			String[] header = getRecordFactory().createHeader(nativeItems[count+oldCount]);
			headers.add(header[0]);
			identifiers.add(header[1]);
		}

		/* decide if you're done. */
		if (count+oldCount < nativeItems.length) {
			resumptionId = getResumptionId();

			resumptionResults.put(resumptionId, nativeItems);

			/*****************************************************************
			 * Construct the resumptionToken String however you see fit.
			 *****************************************************************/
			StringBuffer resumptionTokenSb = new StringBuffer();
			resumptionTokenSb.append(resumptionId);
			resumptionTokenSb.append(":");
			resumptionTokenSb.append(Integer.toString(oldCount + count));
			resumptionTokenSb.append(":");
			resumptionTokenSb.append(metadataPrefix);

			listIdentifiersMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(),
					nativeItems.length,
					oldCount));
		}
		listIdentifiersMap.put("headers", headers.iterator());
		listIdentifiersMap.put("identifiers", identifiers.iterator());
		return listIdentifiersMap;
	}

	public String getRecord(String oaiIdentifier, String metadataPrefix) throws IdDoesNotExistException, CannotDisseminateFormatException, OAIInternalServerError {
		String localIdentifier = getRecordFactory().fromOAIIdentifier(oaiIdentifier);

		String file = getMatchingFile(new File(basePath), localIdentifier.replaceAll(":", "_").replaceAll("/", ".s."));

		return constructRecord(file, metadataPrefix);
	}

	public void close() {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
