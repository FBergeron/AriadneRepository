/*package org.ariadne_eu.oai.server.mysql.catalog;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.oclc.oai.server.catalog.AbstractCatalog;
import org.oclc.oai.server.verb.BadArgumentException;
import org.oclc.oai.server.verb.BadResumptionTokenException;
import org.oclc.oai.server.verb.CannotDisseminateFormatException;
import org.oclc.oai.server.verb.IdDoesNotExistException;
import org.oclc.oai.server.verb.NoItemsMatchException;
import org.oclc.oai.server.verb.NoMetadataFormatsException;
import org.oclc.oai.server.verb.NoSetHierarchyException;
import org.oclc.oai.server.verb.OAIInternalServerError;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

*//**
 * Created by IntelliJ IDEA.
 * User: stefaan
 * Date: 11-nov-2006
 * Time: 16:50:33
 * To change this template use File | Settings | File Templates.
 *//*
public class MySqlLomCatalog extends AbstractCatalog {

	*//**
	 * pending resumption tokens
	 *//*
	private HashMap resumptionResults = new HashMap();

	private static String url;

	private Connection con;

	private static int maxListSize;

	public MySqlLomCatalog(Properties properties) {
		String classname = "MySqlLomCatalog";
		String maxListSize = properties.getProperty(classname + ".maxListSize");
		if (maxListSize == null) {
			throw new IllegalArgumentException(classname + ".maxListSize is missing from the properties file");
		} else {
			MySqlLomCatalog.maxListSize = Integer.parseInt(maxListSize);
		}
		String url = properties.getProperty(classname + ".url");
		if (url == null) {
			throw new IllegalArgumentException(classname + ".url is missing from the properties file");
		} else {
			MySqlLomCatalog.url = url;
		}

	}

	public Map listSets() throws NoSetHierarchyException, OAIInternalServerError {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public Map listSets(String resumptionToken) throws BadResumptionTokenException, OAIInternalServerError {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public Vector getSchemaLocations(String identifier) throws IdDoesNotExistException, NoMetadataFormatsException, OAIInternalServerError {
		return null;
	}
	public Map listIdentifiers(String from, String until, String set, String metadataPrefix) throws BadArgumentException, CannotDisseminateFormatException, NoItemsMatchException, NoSetHierarchyException, OAIInternalServerError {
		//purge(); // clean out old resumptionTokens
		Map listIdentifiersMap = new HashMap();
        ArrayList headers = new ArrayList();
        ArrayList identifiers = new ArrayList();

		Statement stmt = connect();
		ResultSet rs = null;
		int count = 0;
		int numRows = 0;
		try {
			rs = stmt.executeQuery("SELECT * " +"FROM learning_object");
			rs.last();
			numRows = rs.getRow();
			if (numRows == 0) {
				disconnect();
			}
			rs.beforeFirst();

			 load the records ArrayList 
			for (count=0; count < maxListSize && rs.next(); ++count) {
				HashMap nativeItem = getColumnValues(rs);
	            String[] header = getRecordFactory().createHeader(nativeItem);
	            headers.add(header[0]);
	            identifiers.add(header[1]);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		 decide if you're done 
		if (count < numRows) {
			String resumptionId = getResumptionId();

			*//*****************************************************************
			 * Store an object appropriate for your database API in the
			 * resumptionResults Map in place of nativeItems. This object
			 * should probably encapsulate the information necessary to
			 * perform the next resumption of ListIdentifiers. It might even
			 * be possible to encode everything you need in the
			 * resumptionToken, in which case you won't need the
			 * resumptionResults Map. Here, I've done a silly combination
			 * of the two. Stateless resumptionTokens have some advantages.
			 *****************************************************************//*
			resumptionResults.put(resumptionId, stmt);

			*//*****************************************************************
			 * Construct the resumptionToken String however you see fit.
			 *****************************************************************//*
			StringBuffer resumptionTokenSb = new StringBuffer();
			resumptionTokenSb.append(resumptionId);
			resumptionTokenSb.append(":");
			resumptionTokenSb.append(Integer.toString(count));
			resumptionTokenSb.append(":");
			resumptionTokenSb.append(metadataPrefix);

			*//*****************************************************************
			 * Use the following line if you wish to include the optional
			 * resumptionToken attributes in the response. Otherwise, use the
			 * line after it that I've commented out.
			 *****************************************************************//*
			listIdentifiersMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(),
					numRows,
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

	*//**
	 * Use the current date as the basis for the resumptiontoken
	 *
	 * @return a String version of the current time
	 *//*
	private synchronized static String getResumptionId() {
		Date now = new Date();
		return Long.toString(now.getTime());
	}

	*//**
	 * Purge tokens that are older than the configured time-to-live.
	 *//*
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

	private Statement connect(){
		Statement stmt = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");

			con = (Connection) DriverManager.getConnection(url,"root", "indigo");
			stmt = (Statement) con.createStatement();
			//Display URL and connection information
			System.out.println("URL: " + url);
			System.out.println("Connection: " + con);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stmt;
	}

	*//**
	 * Since the columns should only be read once, copy them into a
	 * HashMap and consider that to be the "record"
	 * @param rs The ResultSet row
	 * @return a HashMap mapping column names with values
	 *//*
	private HashMap getColumnValues(ResultSet rs) throws SQLException {
		ResultSetMetaData mdata = rs.getMetaData();
		int count = mdata.getColumnCount();
		HashMap nativeItem = new HashMap(count);
		for (int i=1; i<=count; ++i) {
			String fieldName = new StringBuffer().append(mdata.getTableName(i) + ".").append(mdata.getColumnName(i)).toString();
			nativeItem.put(fieldName, rs.getObject(i));
		}
		return nativeItem;
	}

	*//**
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
	 *//*
	public Map listRecords(String from, String until, String set, String metadataPrefix)
	throws CannotDisseminateFormatException {
		//purge(); // clean out old resumptionTokens
		Map listRecordsMap = new HashMap();
		ArrayList records = new ArrayList();

		*//************************************************************************************
		 * perform the query on your DB according to the given parameters from, until and set
		 ************************************************************************************//*
		Statement stmt = connect();
		ResultSet rs = null;
		int count = 0;
		int numRows = 0;
		try {
			rs = stmt.executeQuery("SELECT * " +"FROM learning_object");
			rs.last();
			numRows = rs.getRow();
			if (numRows == 0) {
				disconnect();
			}
			rs.beforeFirst();

			 load the records ArrayList 
			for (count=0; count < maxListSize && rs.next(); ++count) {
				HashMap nativeItem = getColumnValues(rs);
				String record = constructRecord(nativeItem, metadataPrefix);
				records.add(record);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 decide if you're done 
		if (count < numRows) {
			String resumptionId = getResumptionId();

			*//*****************************************************************
			 * Store an object appropriate for your database API in the
			 * resumptionResults Map in place of nativeItems. This object
			 * should probably encapsulate the information necessary to
			 * perform the next resumption of ListIdentifiers. It might even
			 * be possible to encode everything you need in the
			 * resumptionToken, in which case you won't need the
			 * resumptionResults Map. Here, I've done a silly combination
			 * of the two. Stateless resumptionTokens have some advantages.
			 *****************************************************************//*
			resumptionResults.put(resumptionId, stmt);

			*//*****************************************************************
			 * Construct the resumptionToken String however you see fit.
			 *****************************************************************//*
			StringBuffer resumptionTokenSb = new StringBuffer();
			resumptionTokenSb.append(resumptionId);
			resumptionTokenSb.append(":");
			resumptionTokenSb.append(Integer.toString(count));
			resumptionTokenSb.append(":");
			resumptionTokenSb.append(metadataPrefix);

			*//*****************************************************************
			 * Use the following line if you wish to include the optional
			 * resumptionToken attributes in the response. Otherwise, use the
			 * line after it that I've commented out.
			 *****************************************************************//*
			listRecordsMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(),
					numRows,
					0));
		}
		
		listRecordsMap.put("records", records.iterator());
		return listRecordsMap;
	}

	private void disconnect() {
		try {
			con.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	*//**
	 * Retrieve the next set of records associated with the resumptionToken
	 *
	 * @param resumptionToken implementation-dependent format taken from the
	 * previous listRecords() Map result.
	 * @return a Map object containing entries for "headers" and "identifiers" Iterators
	 * (both containing Strings) as well as an optional "resumptionMap" Map.
	 * @exception BadResumptionTokenException the value of the resumptionToken argument
	 * is invalid or expired.
	 *//*
	public Map listRecords(String resumptionToken)
	throws BadResumptionTokenException {
		Map listRecordsMap = new HashMap();
		ArrayList records = new ArrayList();
		//purge(); // clean out old resumptionTokens

		*//**********************************************************************
		 * YOUR CODE GOES HERE
		 **********************************************************************//*
		*//**********************************************************************
		 * parse your resumptionToken and look it up in the resumptionResults,
		 * if necessary
		 **********************************************************************//*
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

		 Get some more records from your database 
		Object[] nativeItem = (Object[])resumptionResults.remove(resumptionId);
		if (nativeItem == null) {
			throw new BadResumptionTokenException();
		}
		int count;

		 load the headers and identifiers ArrayLists. 
		for (count = 0; count < maxListSize && count+oldCount < nativeItem.length; ++count) {
			try {
				String record = constructRecord(nativeItem[count+oldCount], metadataPrefix);
				records.add(record);
			} catch (CannotDisseminateFormatException e) {
				 the client hacked the resumptionToken beyond repair 
				throw new BadResumptionTokenException();
			}
		}

		 decide if you're done 
		if (count+oldCount < nativeItem.length) {
			resumptionId = getResumptionId();

			*//*****************************************************************
			 * Store an object appropriate for your database API in the
			 * resumptionResults Map in place of nativeItems. This object
			 * should probably encapsulate the information necessary to
			 * perform the next resumption of ListIdentifiers. It might even
			 * be possible to encode everything you need in the
			 * resumptionToken, in which case you won't need the
			 * resumptionResults Map. Here, I've done a silly combination
			 * of the two. Stateless resumptionTokens have some advantages.
			 *****************************************************************//*
			resumptionResults.put(resumptionId, nativeItem);

			*//*****************************************************************
			 * Construct the resumptionToken String however you see fit.
			 *****************************************************************//*
			StringBuffer resumptionTokenSb = new StringBuffer();
			resumptionTokenSb.append(resumptionId);
			resumptionTokenSb.append(":");
			resumptionTokenSb.append(Integer.toString(oldCount + count));
			resumptionTokenSb.append(":");
			resumptionTokenSb.append(metadataPrefix);

			*//*****************************************************************
			 * Use the following line if you wish to include the optional
			 * resumptionToken attributes in the response. Otherwise, use the
			 * line after it that I've commented out.
			 *****************************************************************//*
			listRecordsMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(),
					nativeItem.length,
					oldCount));
			//          listRecordsMap.put("resumptionMap",
			//                                 getResumptionMap(resumptionTokenSb.toString()));
		}
		*//***********************************************************************
		 * END OF CUSTOM CODE SECTION
		 ***********************************************************************//*
		listRecordsMap.put("records", records.iterator());
		return listRecordsMap;
	}

	public Map listIdentifiers(String resumptionToken) throws BadResumptionTokenException, OAIInternalServerError {
		purge(); // clean out old resumptionTokens
		Map listIdentifiersMap = new HashMap();
		ArrayList headers = new ArrayList();
		ArrayList identifiers = new ArrayList();

		*//**********************************************************************
		 * YOUR CODE GOES HERE
		 **********************************************************************//*
		*//**********************************************************************
		 * parse your resumptionToken and look it up in the resumptionResults,
		 * if necessary
		 **********************************************************************//*
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

		 Get some more records from your database 
		Object[] nativeItems = (Object[])resumptionResults.remove(resumptionId);
		if (nativeItems == null) {
			throw new BadResumptionTokenException();
		}
		int count;

		 load the headers and identifiers ArrayLists. 
		for (count = 0; count < maxListSize && count+oldCount < nativeItems.length; ++count) {
			 Use the RecordFactory to extract header/identifier pairs for each item 
			String[] header = getRecordFactory().createHeader(nativeItems[count+oldCount]);
			headers.add(header[0]);
			identifiers.add(header[1]);
		}

		 decide if you're done. 
		if (count+oldCount < nativeItems.length) {
			resumptionId = getResumptionId();

			*//*****************************************************************
			 * Store an object appropriate for your database API in the
			 * resumptionResults Map in place of nativeItems. This object
			 * should probably encapsulate the information necessary to
			 * perform the next resumption of ListIdentifiers. It might even
			 * be possible to encode everything you need in the
			 * resumptionToken, in which case you won't need the
			 * resumptionResults Map. Here, I've done a silly combination
			 * of the two. Stateless resumptionTokens have some advantages.
			 *****************************************************************//*
			resumptionResults.put(resumptionId, nativeItems);

			*//*****************************************************************
			 * Construct the resumptionToken String however you see fit.
			 *****************************************************************//*
			StringBuffer resumptionTokenSb = new StringBuffer();
			resumptionTokenSb.append(resumptionId);
			resumptionTokenSb.append(":");
			resumptionTokenSb.append(Integer.toString(oldCount + count));
			resumptionTokenSb.append(":");
			resumptionTokenSb.append(metadataPrefix);

			*//*****************************************************************
			 * Use the following line if you wish to include the optional
			 * resumptionToken attributes in the response. Otherwise, use the
			 * line after it that I've commented out.
			 *****************************************************************//*
			listIdentifiersMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(),
					nativeItems.length,
					oldCount));
			//          listIdentifiersMap.put("resumptionMap",
			//                                 getResumptionMap(resumptionTokenSb.toString()));
		}
		*//***********************************************************************
		 * END OF CUSTOM CODE SECTION
		 ***********************************************************************//*
		listIdentifiersMap.put("headers", headers.iterator());
		listIdentifiersMap.put("identifiers", identifiers.iterator());
		return listIdentifiersMap;
	}


	protected String parseToLuceneQuery(String query){
		try {
			StringTokenizer tokenizer = new StringTokenizer(query, ":");
			String result = tokenizer.nextToken();
			while(tokenizer.hasMoreElements()){
				result = result.concat("\\:" + tokenizer.nextToken());
			}
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	public String getRecord(String oaiIdentifier, String metadataPrefix) throws IdDoesNotExistException, CannotDisseminateFormatException, OAIInternalServerError {
		String identifier = getRecordFactory().fromOAIIdentifier(oaiIdentifier);
        HashMap nativeItem = new HashMap();

		Statement stmt = connect();
		ResultSet rs = null;
		
		try {
			rs = stmt.executeQuery("SELECT * FROM learning_object WHERE identifier="+ identifier);
			if (rs.next())nativeItem = getColumnValues(rs);
			else new IdDoesNotExistException(identifier);
		} catch (SQLException e) {
			throw new OAIInternalServerError(e.getMessage());
		}        
//        //Retrieve the following from your database...
//        nativeItem.put("learning_object.identifier", identifier);
//        nativeItem.put("learning_object.title", "test titel");
//        nativeItem.put("learning_object.last_mod_date", new java.sql.Date(100239));
        
        return constructRecord(nativeItem, metadataPrefix);
	}

	public void close() {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
*/