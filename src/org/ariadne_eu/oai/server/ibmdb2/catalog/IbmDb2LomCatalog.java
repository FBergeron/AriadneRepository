package org.ariadne_eu.oai.server.ibmdb2.catalog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.ariadne.config.PropertiesManager;
import org.ariadne_eu.oai.utils.TargetUtils;
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

import com.ibm.db2.jcc.DB2Xml;

/**
 * Created by IntelliJ IDEA. User: stefaan Date: 11-nov-2006 Time: 16:50:33 To
 * change this template use File | Settings | File Templates.
 */
public class IbmDb2LomCatalog extends AbstractCatalog {

	/**
	 * pending resumption tokens
	 */
	private HashMap resumptionResults = new HashMap();
	private static HashMap<String, String> sets = new HashMap<String, String>();
	private static int maxListSize;
	private static Connection connection;
	private static String url;
	private static String user;
	private static String passwd;
	private static String table;
	private static String column_xml;
	private static String column_id;
	private static String column_datestamp;

	public IbmDb2LomCatalog(Properties properties) {
		String maxListSize = properties.getProperty(RepositoryConstants.OAICAT_SERVER_CATALOG_MAXLSTSIZE);
		if (maxListSize == null) {
			throw new IllegalArgumentException(RepositoryConstants.OAICAT_SERVER_CATALOG_MAXLSTSIZE + " is missing from the properties file");
		} else {
			IbmDb2LomCatalog.maxListSize = Integer.parseInt(maxListSize);
		}
		String url = properties.getProperty(RepositoryConstants.MD_DB_URI);
		if (url == null) {
			throw new IllegalArgumentException(RepositoryConstants.MD_DB_URI + " is missing from the properties file");
		} else {
			IbmDb2LomCatalog.url = url;
		}
		String user = properties.getProperty(RepositoryConstants.MD_DB_USERNAME);
		if (user == null) {
			throw new IllegalArgumentException(RepositoryConstants.MD_DB_USERNAME + " is missing from the properties file");
		} else {
			IbmDb2LomCatalog.user = user;
		}
		String passwd = properties.getProperty(RepositoryConstants.MD_DB_PASSWORD);
		if (passwd == null) {
			throw new IllegalArgumentException(RepositoryConstants.MD_DB_PASSWORD + " is missing from the properties file");
		} else {
			IbmDb2LomCatalog.passwd = passwd;
		}
		String table = properties.getProperty(RepositoryConstants.MD_DB_XMLDB_SQL_TABLENAME);
		if (table == null) {
			throw new IllegalArgumentException(RepositoryConstants.MD_DB_XMLDB_SQL_TABLENAME + " is missing from the properties file");
		} else {
			IbmDb2LomCatalog.table = table;
		}
		String column_xml = properties.getProperty(RepositoryConstants.MD_DB_XMLDB_SQL_COLUMNNAME);
		if (column_xml == null) {
			throw new IllegalArgumentException(RepositoryConstants.MD_DB_XMLDB_SQL_COLUMNNAME + " is missing from the properties file");
		} else {
			IbmDb2LomCatalog.column_xml = column_xml;
		}
		String column_id = properties.getProperty(RepositoryConstants.MD_DB_XMLDB_SQL_IDCOLUMNNAME);
		if (column_id == null) {
			throw new IllegalArgumentException(RepositoryConstants.MD_DB_XMLDB_SQL_IDCOLUMNNAME + " is missing from the properties file");
		} else {
			IbmDb2LomCatalog.column_id = column_id;
		}
//		String column_datestamp = properties.getProperty(RepositoryConstants.OAICAT_SERVER_CATALOG_DATECOLUMN);
//		if (column_datestamp == null) {
//			throw new IllegalArgumentException(RepositoryConstants.OAICAT_SERVER_CATALOG_DATECOLUMN + " is missing from the properties file");
//		} else {
//			IbmDb2LomCatalog.column_datestamp = column_datestamp;
//		}

		try {

			Hashtable setKeys = PropertiesManager.getInstance().getPropertyStartingWith(RepositoryConstants.OAICAT_SETS);
			String[] keys = (String[]) sets.keySet().toArray(new String[0]);
			String reposIdentifier = "";
			for (String key : keys) {
				String setSpec = key.replace(RepositoryConstants.OAICAT_SETS + ".", "").replace("." + RepositoryConstants.OAICAT_SETS_ID, "");
				reposIdentifier = PropertiesManager.getInstance().getProperty(key);
				sets.put(setSpec, reposIdentifier);
			}
		} catch (Exception e) {
			// NOOP
		}

	}

	public Map listSets() throws NoSetHierarchyException, OAIInternalServerError {
		Hashtable setKeys = PropertiesManager.getInstance().getPropertyStartingWith(RepositoryConstants.OAICAT_SETS);
		String[] keys = (String[]) setKeys.keySet().toArray(new String[0]);
		if (keys.length == 0) {
			throw new NoSetHierarchyException();
		} else {
			purge(); // clean out old resumptionTokens
			Map listSetsMap = new HashMap();
			ArrayList sets = new ArrayList();

			for (String key : keys) {
				String setSpec = key.replace(RepositoryConstants.OAICAT_SETS + ".", "").replace("." + RepositoryConstants.OAICAT_SETS_ID, "");
				sets.add(getSetXML(key, setSpec));
			}

			listSetsMap.put("sets", sets.iterator());
			return listSetsMap;
		}
	}

	public Map listSets(String resumptionToken) throws BadResumptionTokenException, OAIInternalServerError {
		return null; // To change body of implemented methods use File |
		// Settings | File Templates.
	}

	public Vector getSchemaLocations(String identifier) throws IdDoesNotExistException, NoMetadataFormatsException, OAIInternalServerError {
		return null;
	}

	public Map listIdentifiers(String from, String until, String set, String metadataPrefix) throws BadArgumentException, CannotDisseminateFormatException, NoItemsMatchException,
			NoSetHierarchyException, OAIInternalServerError {

		String query;
		try {
			query = createListQuery(from, until, set);
			return listIdentifiers(query, 0, metadataPrefix);
		} catch (ParseException e) {
			throw new BadArgumentException();
		}
	}

	/**
	 * Extract &lt;set&gt; XML string from setItem object
	 * 
	 * @param setItem
	 *            individual set instance in native format
	 * @return an XML String containing the XML &lt;set&gt; content
	 */
	public String getSetXML(String key, String setSpec) throws IllegalArgumentException {
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

	private Map listIdentifiers(String query, int offset, String metadataPrefix) throws CannotDisseminateFormatException, OAIInternalServerError {
		// purge(); // clean out old resumptionTokens
		Map listIdentifiersMap = new HashMap();
		ArrayList headers = new ArrayList();
		ArrayList identifiers = new ArrayList();

		connect();
		ResultSet rs = null;
		int count = 0;
		int numRows = 0;
		int nbrOfResults = 0;
		try {
			String selectCount = "SELECT COUNT (*) ";
			System.out.println(selectCount + query);// TODO : LOG4J
			rs = connection.createStatement().executeQuery(selectCount + query);
			rs.next();
			nbrOfResults = (int) rs.getFloat(1);
			rs.close();

			String select = "SELECT " + column_xml + "," + column_id + "," + column_datestamp + " ";
			String limitQuery = "WHERE R > " + offset + " and R <= " + (offset + maxListSize);
			System.out.println(select + query + limitQuery);// TODO : LOG4J
			rs = connection.createStatement().executeQuery(select + query + limitQuery);

			/* load the records ArrayList */
			for (count = 0; count < maxListSize && rs.next(); ++count) {
				HashMap nativeItem = constructNativeItemForHeader(rs);
				String[] header = getRecordFactory().createHeader(nativeItem);
				headers.add(header[0]);
				identifiers.add(header[1]);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		numRows = headers.size();
		disconnect();

		/* decide if you're done */
		if (numRows == maxListSize) {
			String resumptionId = getResumptionId();
			resumptionResults.put(resumptionId, query);

			StringBuffer resumptionTokenSb = new StringBuffer();
			resumptionTokenSb.append(resumptionId);
			resumptionTokenSb.append(":");
			resumptionTokenSb.append(Integer.toString(count + offset));
			resumptionTokenSb.append(":");
			resumptionTokenSb.append(metadataPrefix);

			listIdentifiersMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(), nbrOfResults, offset + maxListSize));
		}

		listIdentifiersMap.put("headers", headers.iterator());
		listIdentifiersMap.put("identifiers", identifiers.iterator());
		return listIdentifiersMap;
	}

	private String createListQuery(String from, String until, String set) throws ParseException {
		String reposIdentifier = "";

		if (sets.containsKey(set)) {
			reposIdentifier = sets.get(set);
		}

		String newFrom = TargetUtils.convertToLocaleIbmDB2DateTime(from);
		String newUntil = TargetUtils.convertUNTILToLocaleIbmDB2DateTime(until);

		String query = "FROM ( " + "SELECT ROW_NUMBER() OVER() AS R," + column_xml + "," + column_id + "," + column_datestamp + " " + "FROM " + table + " " + "WHERE (" + column_datestamp + " >='"
				+ newFrom + "' AND " + column_datestamp + " <='" + newUntil + "' ";
		if (!reposIdentifier.equals("")) {
			query += "AND " + column_id + " like '%" + reposIdentifier + "%'";
		} else {
			if (set != null && !set.equals("")) {
				query += "AND " + column_id + " like 'xxxyyyzzz'";
			}
		}
		query += " )) AS N ";
		return query;
	}

	private String constructRecord(Object nativeItem, String metadataPrefix) throws CannotDisseminateFormatException {
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
			String key = (String) keySet.next();
			Date then = new Date(Long.parseLong(key) + getMillisecondsToLive());
			if (now.after(then)) {
				old.add(key);
			}
		}
		Iterator iterator = old.iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			resumptionResults.remove(key);
		}
	}

	private void connect() throws OAIInternalServerError {
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			connection = (Connection) DriverManager.getConnection(url, user, passwd);
		} catch (ClassNotFoundException e) {
			throw new OAIInternalServerError(e.getMessage());
		} catch (SQLException e) {
			throw new OAIInternalServerError(e.getMessage());
		}
	}

	private HashMap constructNativeItem(ResultSet rs) throws OAIInternalServerError, IllegalStateException {
		HashMap nativeItem = new HashMap();
		try {
			Vector names = getColumnNames(rs);
			for (int i = 0; i < names.size(); i++) {
				String name = (String) names.get(i);
				if (name.equals(column_xml)) {
					DB2Xml xml = (DB2Xml) rs.getObject(name);
					nativeItem.put(name, xml.getDB2XmlString());
				} else if (name.equals(column_datestamp)) {
					Object date = rs.getTimestamp(name, new GregorianCalendar());
					nativeItem.put(name, date);
				} else if (name.equals(column_id)) {
					String string = rs.getString(name);
					nativeItem.put(name, string);
				}
			}
		} catch (SQLException e) {
			throw new OAIInternalServerError("SQLException : " + e.getMessage());
		}
		return nativeItem;
	}

	private HashMap constructNativeItemForHeader(ResultSet rs) throws OAIInternalServerError, IllegalStateException {
		HashMap nativeItem = new HashMap();
		try {
			Vector names = getColumnNames(rs);
			for (int i = 0; i < names.size(); i++) {
				String name = (String) names.get(i);
				if (name.equals(column_datestamp)) {
					Object date = rs.getTimestamp(name, new GregorianCalendar());
					nativeItem.put(name, date);
				} else if (name.equals(column_id)) {
					String string = rs.getString(name);
					nativeItem.put(name, string);
				}
			}
		} catch (SQLException e) {
			throw new OAIInternalServerError("SQLException : " + e.getMessage());
		}
		return nativeItem;
	}

	private Vector getColumnNames(ResultSet rs) throws SQLException {
		Vector names = new Vector();
		ResultSetMetaData mdata = rs.getMetaData();
		int count = mdata.getColumnCount();
		for (int i = 1; i <= count; ++i) {
			String fieldName = mdata.getColumnName(i);
			names.add(fieldName);
		}
		return names;
	}

	private Map listRecords(String query, int offset, String metadataPrefix) throws CannotDisseminateFormatException, OAIInternalServerError {
		// purge(); // clean out old resumptionTokens
		Map listRecordsMap = new HashMap();
		ArrayList records = new ArrayList();

		connect();
		ResultSet rs = null;
		int count = 0;
		int numRows = 0;
		int notFound = 0;
		int nbrOfResults = 0;
		Vector nativeItemResults = new Vector();
		try {
			if (query.equalsIgnoreCase("")) {
				String selectCount = "SELECT COUNT (*) ";
				System.out.println(selectCount + query);// TODO : LOG4J
				rs = connection.createStatement().executeQuery(selectCount + query);
				rs.next();
				nbrOfResults = (int) rs.getFloat(1);
				rs.close();

				String select = "SELECT " + column_xml + "," + column_id + "," + column_datestamp + " ";
				String limitQuery = "WHERE R > " + offset + " and R <= " + (offset + maxListSize);
				System.out.println(select + query + limitQuery);// TODO : LOG4J
				rs = connection.createStatement().executeQuery(select + query + limitQuery);
				while (rs.next()) {
					try {
						HashMap item = constructNativeItem(rs);
						nativeItemResults.add(item);
					} catch (IllegalStateException e) {
						notFound++;
						System.out.println(e.getMessage());
					}

				}
				rs.close();
				disconnect();

				numRows = nativeItemResults.size();

				/* load the records ArrayList */
				for (count = 0; count < maxListSize && count < numRows; ++count) {
					String record = constructRecord((HashMap) nativeItemResults.elementAt(count), metadataPrefix);
					records.add(record);
				}
			}
		} catch (SQLException e) {
			throw new OAIInternalServerError(e.getMessage());
		}
		/* decide if you're done */
		if (numRows + notFound == maxListSize) {
			String resumptionId = getResumptionId();
			resumptionResults.put(resumptionId, query);

			StringBuffer resumptionTokenSb = new StringBuffer();
			resumptionTokenSb.append(resumptionId);
			resumptionTokenSb.append(":");
			resumptionTokenSb.append(Integer.toString(count + notFound + offset));
			resumptionTokenSb.append(":");
			resumptionTokenSb.append(metadataPrefix);

			listRecordsMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(), nbrOfResults, offset + maxListSize));
		}

		listRecordsMap.put("records", records.iterator());
		return listRecordsMap;
	}

	/**
	 * Retrieve a list of records that satisfy the specified criteria. Note,
	 * though, that unlike the other OAI verb type methods implemented here,
	 * both of the listRecords methods are already implemented in
	 * AbstractCatalog rather than abstracted. This is because it is possible to
	 * implement ListRecords as a combination of ListIdentifiers and GetRecord
	 * combinations. Nevertheless, I suggest that you override both the
	 * AbstractCatalog.listRecords methods here since it will probably improve
	 * the performance if you create the response in one fell swoop rather than
	 * construct it one GetRecord at a time.
	 * 
	 * @param from
	 *            beginning date using the proper granularity
	 * @param until
	 *            ending date using the proper granularity
	 * @param set
	 *            the set name or null if no such limit is requested
	 * @param metadataPrefix
	 *            the OAI metadataPrefix or null if no such limit is requested
	 * @return a Map object containing entries for a "records" Iterator object
	 *         (containing XML <record/> Strings) and an optional
	 *         "resumptionMap" Map.
	 * @exception CannotDisseminateFormatException
	 *                the metadataPrefix isn't supported by the item.
	 * @throws OAIInternalServerError
	 */
	public Map listRecords(String from, String until, String set, String metadataPrefix) throws CannotDisseminateFormatException, OAIInternalServerError {
		try {

			if (set != null) {
				set = sets.get(set);
				if (set == null) {
					set = "";
				}
			}
			String query = null;
			if (set == null || !set.equalsIgnoreCase("")) {
				query = createListQuery(from, until, set);
			} else {
				query = "";
			}

			return listRecords(query, 0, metadataPrefix);
		} catch (ParseException e) {
			throw new OAIInternalServerError(e.getMessage());
		}
	}

	private void disconnect() {

		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Retrieve the next set of records associated with the resumptionToken
	 * 
	 * @param resumptionToken
	 *            implementation-dependent format taken from the previous
	 *            listRecords() Map result.
	 * @return a Map object containing entries for "headers" and "identifiers"
	 *         Iterators (both containing Strings) as well as an optional
	 *         "resumptionMap" Map.
	 * @exception BadResumptionTokenException
	 *                the value of the resumptionToken argument is invalid or
	 *                expired.
	 * @throws OAIInternalServerError
	 */
	public Map listRecords(String resumptionToken) throws BadResumptionTokenException, OAIInternalServerError {
		StringTokenizer tokenizer = new StringTokenizer(resumptionToken, ":");
		String resumptionId;
		int oldCount;
		String metadataPrefix;
		resumptionId = tokenizer.nextToken();
		oldCount = Integer.parseInt(tokenizer.nextToken());
		metadataPrefix = tokenizer.nextToken();

		/* Get some more records from your database */
		String query = (String) resumptionResults.remove(resumptionId);
		if (query == null) {
			throw new BadResumptionTokenException();
		}
		Map map = null;
		try {
			map = listRecords(query, oldCount, metadataPrefix);
		} catch (CannotDisseminateFormatException e) {
			throw new OAIInternalServerError("CannotDisseminateFormatException thrown : " + e.getMessage());
		}
		return map;
	}

	public Map listIdentifiers(String resumptionToken) throws BadResumptionTokenException, OAIInternalServerError {
		StringTokenizer tokenizer = new StringTokenizer(resumptionToken, ":");
		String resumptionId;
		int oldCount;
		String metadataPrefix;
		resumptionId = tokenizer.nextToken();
		oldCount = Integer.parseInt(tokenizer.nextToken());
		metadataPrefix = tokenizer.nextToken();

		/* Get some more records from your database */
		String query = (String) resumptionResults.remove(resumptionId);
		if (query == null) {
			throw new BadResumptionTokenException();
		}
		Map map = null;
		try {
			map = listIdentifiers(query, oldCount, metadataPrefix);
		} catch (CannotDisseminateFormatException e) {
			throw new OAIInternalServerError("CannotDisseminateFormatException thrown : " + e.getMessage());
		}
		return map;
	}

	protected String parseToLuceneQuery(String query) {
		try {
			StringTokenizer tokenizer = new StringTokenizer(query, ":");
			String result = tokenizer.nextToken();
			while (tokenizer.hasMoreElements()) {
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
		if (identifier.equals("")) {
			identifier = oaiIdentifier;
		}
		connect();
		ResultSet rs = null;

		try {
			String query = "SELECT * FROM " + table + " WHERE " + column_id + "='" + identifier + "'";
			System.out.println(query);// TODO : LOG4J
			rs = connection.createStatement().executeQuery(query);
			if (rs.next())
				nativeItem = constructNativeItem(rs);
			else
				throw new IdDoesNotExistException(identifier);
		} catch (SQLException e) {
			throw new OAIInternalServerError(e.getMessage());
		} finally {
			disconnect();
		}

		return constructRecord(nativeItem, metadataPrefix);
	}

	public void close() {
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}
}