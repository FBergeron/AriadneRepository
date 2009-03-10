package org.ariadne_eu.oai.server.sqi.catalog;
//package org.ariadne.oai.server.sqi.catalog;
//
//import java.rmi.RemoteException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.NoSuchElementException;
//import java.util.Properties;
//import java.util.StringTokenizer;
//import java.util.Vector;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.FactoryConfigurationError;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.TransformerException;
//
//import org.apache.axis2.AxisFault;
//import org.apache.crimson.jaxp.DocumentBuilderFactoryImpl;
//import org.apache.crimson.tree.ElementNode;
//import org.apache.xpath.XPathAPI;
//import org.ariadne.oai.utils.TargetUtils;
//import org.ariadne_eu.service.SqiSessionManagementBindingServiceStub;
//import org.ariadne_eu.service.SqiTargetBindingServiceStub;
//import org.ariadne_eu.service._SQIFaultException;
//import org.oclc.oai.server.catalog.AbstractCatalog;
//import org.oclc.oai.server.verb.BadArgumentException;
//import org.oclc.oai.server.verb.BadResumptionTokenException;
//import org.oclc.oai.server.verb.CannotDisseminateFormatException;
//import org.oclc.oai.server.verb.IdDoesNotExistException;
//import org.oclc.oai.server.verb.NoItemsMatchException;
//import org.oclc.oai.server.verb.NoMetadataFormatsException;
//import org.oclc.oai.server.verb.NoSetHierarchyException;
//import org.oclc.oai.server.verb.OAIInternalServerError;
//import org.w3c.dom.Document;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//
//import be.cenorm.www.CreateAnonymousSession;
//import be.cenorm.www.CreateAnonymousSessionResponse;
//import be.cenorm.www.DestroySession;
//import be.cenorm.www.GetTotalResultsCount;
//import be.cenorm.www.GetTotalResultsCountResponse;
//import be.cenorm.www.SetQueryLanguage;
//import be.cenorm.www.SetResultsSetSize;
//import be.cenorm.www.SynchronousQuery;
//import be.cenorm.www.SynchronousQueryResponse;
//
///**
// * Created by IntelliJ IDEA.
// * User: stefaan
// * Date: 11-nov-2006
// * Time: 16:50:33
// * To change this template use File | Settings | File Templates.
// */
//public class SqiLomCatalog extends AbstractCatalog {
//
//	/**
//	 * pending resumption tokens
//	 */
//	private HashMap resumptionResults = new HashMap();
//
//	private static String url;
//
//	private SqiTargetBindingServiceStub sqiTarget = null;
//
//	private static int maxListSize;
//
//	private static String sessionUrl;
//
//	private String sessionId = "";
//
//	private SqiSessionManagementBindingServiceStub sqiSession = null;
//
//	public SqiLomCatalog(Properties properties) {
//		String classname = "SqiLomCatalog";
//		String maxListSize = properties.getProperty(classname + ".maxListSize");
//		if (maxListSize == null) {
//			throw new IllegalArgumentException(classname + ".maxListSize is missing from the properties file");
//		} else {
//			SqiLomCatalog.maxListSize = Integer.parseInt(maxListSize);
//		}
//		String url = properties.getProperty(classname + ".url");
//		if (url == null) {
//			throw new IllegalArgumentException(classname + ".url is missing from the properties file");
//		} else {
//			SqiLomCatalog.url = url;
//		}
//		String sessionUrl = properties.getProperty(classname + ".session.url");
//		if (url == null) {
//			throw new IllegalArgumentException(classname + ".session.url is missing from the properties file");
//		} else {
//			SqiLomCatalog.sessionUrl = sessionUrl;
//		}
//	}
//
//	public Map listSets() throws NoSetHierarchyException, OAIInternalServerError {
//		return null;  //To change body of implemented methods use File | Settings | File Templates.
//	}
//
//	public Map listSets(String resumptionToken) throws BadResumptionTokenException, OAIInternalServerError {
//		return null;  //To change body of implemented methods use File | Settings | File Templates.
//	}
//
//	public Vector getSchemaLocations(String identifier) throws IdDoesNotExistException, NoMetadataFormatsException, OAIInternalServerError {
//		return null;
//	}
//
//	private Map listIdentifiers(String from, String until, String set, String metadataPrefix,int start)
//	throws CannotDisseminateFormatException, OAIInternalServerError {
//		Map listIdentifiersMap = new HashMap();
//		ArrayList headers = new ArrayList();
//		ArrayList identifiers = new ArrayList();
//
//		connect("plql2");
//		NodeList results = null;
//		SynchronousQueryResponse response = null;
//		int count = start;
//		
//		int size = 0;
//        try {
//            GetTotalResultsCount getTotalResultsCount = new GetTotalResultsCount();
//            getTotalResultsCount.setQueryStatement(getDateQuery(from,until));
//            getTotalResultsCount.setTargetSessionID(sessionId);
//			GetTotalResultsCountResponse countResponse = sqiTarget.getTotalResultsCount(getTotalResultsCount);
//			size = countResponse.getGetTotalResultsCountReturn();
//
//			response = query(getDateQuery(from,until),start);
//			
//			Document doc = TargetUtils.getDomFromString(response.getSynchronousQueryReturn());
//			results = XPathAPI.selectNodeList((Node)doc.getFirstChild(),"//lom");
//			
//			/* load the records ArrayList */
//			for (count=0; count < maxListSize && count+start < size; count++) {
//				Node nativeItem = (Node)results.item(count);
//				String[] header = getRecordFactory().createHeader(nativeItem);
//				headers.add(header[0]);
//				identifiers.add(header[1]);
//			}
//		} catch (RemoteException e) {
//			throw new OAIInternalServerError(e.getMessage());
//		} catch (_SQIFaultException e) {
//			throw new OAIInternalServerError(e.getMessage());
//		} catch (TransformerException e) {
//			throw new OAIInternalServerError(e.getMessage());
//		}
//		finally {
//			disconnect();
//		}
//		disconnect();
//		/* decide if you're done */
//		if (count+start < size) {
//			String resumptionId = getResumptionId();
//
//			Vector resumption = new Vector();
//			resumption.add(from);
//			resumption.add(until);
//			resumption.add(set);
//			resumptionResults.put(resumptionId, resumption);
//
//			StringBuffer resumptionTokenSb = new StringBuffer();
//			resumptionTokenSb.append(resumptionId);
//			resumptionTokenSb.append(":");
//			resumptionTokenSb.append(Integer.toString(count+start));
//			resumptionTokenSb.append(":");
//			resumptionTokenSb.append(metadataPrefix);
//
//			listIdentifiersMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(),
//					size,
//					start));
//		}
//		else{
//			
//		}
//
//		listIdentifiersMap.put("headers", headers.iterator());
//		listIdentifiersMap.put("identifiers", identifiers.iterator());
//		return listIdentifiersMap;
//	}
//
//	public Map listIdentifiers(String from, String until, String set, String metadataPrefix) throws BadArgumentException, CannotDisseminateFormatException, NoItemsMatchException, NoSetHierarchyException, OAIInternalServerError {
//		purge(); // clean out old resumptionTokens
//		return listIdentifiers(from, until, set, metadataPrefix,0);
//	}
//
//	private String constructRecord(Object nativeItem, String metadataPrefix)
//	throws CannotDisseminateFormatException {
//		String schemaURL = null;
//
//		if (metadataPrefix != null) {
//			if ((schemaURL = getCrosswalks().getSchemaURL(metadataPrefix)) == null)
//				throw new CannotDisseminateFormatException(metadataPrefix);
//		}
//		return getRecordFactory().create(nativeItem, schemaURL, metadataPrefix);
//	}
//
//	/**
//	 * Use the current date as the basis for the resumptiontoken
//	 *
//	 * @return a String version of the current time
//	 */
//	private synchronized static String getResumptionId() {
//		Date now = new Date();
//		return Long.toString(now.getTime());
//	}
//
//	/**
//	 * Purge tokens that are older than the configured time-to-live.
//	 */
//	private void purge() {
//		ArrayList old = new ArrayList();
//		Date now = new Date();
//		Iterator keySet = resumptionResults.keySet().iterator();
//		while (keySet.hasNext()) {
//			String key = (String)keySet.next();
//			Date then = new Date(Long.parseLong(key) + getMillisecondsToLive());
//			if (now.after(then)) {
//				old.add(key);
//			}
//		}
//		Iterator iterator = old.iterator();
//		while (iterator.hasNext()) {
//			String key = (String)iterator.next();
//			resumptionResults.remove(key);
//		}
//	}
//
//	private SynchronousQueryResponse query(String queryString, int start) throws OAIInternalServerError{
//		SynchronousQuery query = new SynchronousQuery();
//		query.setQueryStatement(queryString);
//		query.setStartResult(start + 1);
//		query.setTargetSessionID(sessionId);
//		SynchronousQueryResponse queryResponse = null;
//		try {
//			queryResponse = sqiTarget.synchronousQuery(query);
//		} catch (_SQIFaultException e) {
//			throw new OAIInternalServerError(e.getMessage());
//		} catch (RemoteException e) {
//			throw new OAIInternalServerError(e.getMessage());
//		}
//		return queryResponse;
//	}
//
//	private void connect(String level){
//		try {
//				if(sqiSession == null)sqiSession = new SqiSessionManagementBindingServiceStub(sessionUrl);
//				CreateAnonymousSessionResponse session = sqiSession.createAnonymousSession(new CreateAnonymousSession());
//				sessionId = session.getCreateAnonymousSessionReturn();
//			if(sessionId != ""){
//
//				 if(sqiTarget == null)sqiTarget = new SqiTargetBindingServiceStub(url);
//				
//				//Set the Query language
//				SetQueryLanguage queryLanguage = new SetQueryLanguage();
//				queryLanguage.setQueryLanguageID(level);
//				queryLanguage.setTargetSessionID(sessionId);
//				sqiTarget.setQueryLanguage(queryLanguage);
//				//Set result size            
//				SetResultsSetSize resultsSetSize = new SetResultsSetSize();
//				resultsSetSize.setResultsSetSize(maxListSize);
//				resultsSetSize.setTargetSessionID(sessionId);
//				sqiTarget.setResultsSetSize(resultsSetSize);
//			}
//		} catch (AxisFault e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (_SQIFaultException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	private void disconnect() {
//		try {
//			DestroySession destroySession = new DestroySession();
//			destroySession.setSessionID(sessionId);
//			sqiSession.destroySession(destroySession);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (_SQIFaultException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		sessionId = "";
//		sqiTarget = null;
//	}
//
//	/**
//	 * Retrieve a list of records that satisfy the specified criteria. Note, though,
//	 * that unlike the other OAI verb type methods implemented here, both of the
//	 * listRecords methods are already implemented in AbstractCatalog rather than
//	 * abstracted. This is because it is possible to implement ListRecords as a
//	 * combination of ListIdentifiers and GetRecord combinations. Nevertheless,
//	 * I suggest that you override both the AbstractCatalog.listRecords methods
//	 * here since it will probably improve the performance if you create the
//	 * response in one fell swoop rather than construct it one GetRecord at a time.
//	 *
//	 * @param from beginning date using the proper granularity
//	 * @param until ending date using the proper granularity
//	 * @param set the set name or null if no such limit is requested
//	 * @param metadataPrefix the OAI metadataPrefix or null if no such limit is requested
//	 * @return a Map object containing entries for a "records" Iterator object
//	 * (containing XML <record/> Strings) and an optional "resumptionMap" Map.
//	 * @exception CannotDisseminateFormatException the metadataPrefix isn't
//	 * supported by the item.
//	 * @throws OAIInternalServerError 
//	 */
//	public Map listRecords(String from, String until, String set, String metadataPrefix)
//	throws CannotDisseminateFormatException, OAIInternalServerError {
//		purge(); // clean out old resumptionTokens
//		return listRecords(from, until, set, metadataPrefix,0);
//	}
//
//	private Map listRecords(String from, String until, String set, String metadataPrefix,int start)
//	throws CannotDisseminateFormatException, OAIInternalServerError {
//		Map listRecordsMap = new HashMap();
//		ArrayList records = new ArrayList();
//
//		connect("plql2");
//		NodeList results = null;
//		SynchronousQueryResponse response = null;
//		int count = start;
//		
//		int size = 0;
//        try {
//            GetTotalResultsCount getTotalResultsCount = new GetTotalResultsCount();
//            getTotalResultsCount.setQueryStatement(getDateQuery(from,until));
//            getTotalResultsCount.setTargetSessionID(sessionId);
//			GetTotalResultsCountResponse countResponse = sqiTarget.getTotalResultsCount(getTotalResultsCount);
//			size = countResponse.getGetTotalResultsCountReturn();
//
//			response = query(getDateQuery(from,until),start);
//
//			String synchronousQueryReturn = response.getSynchronousQueryReturn();
//			Document doc = TargetUtils.getDomFromString(synchronousQueryReturn);
//			Node firstChild = doc.getFirstChild();
//			results = XPathAPI.selectNodeList(firstChild,"//lom");
//
//			int resultsSize = results.getLength();
//			if(resultsSize != maxListSize && resultsSize + start < size){
//				throw new OAIInternalServerError("SQI error : the returned document count ( " + resultsSize + " ) doesnt match with the GetTotalResultsCountResponse ");
//			}
//			DocumentBuilder builder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();
//			/* load the records ArrayList */
//			for (count=0; count < maxListSize && count+start < size; count++) {
//				ElementNode nativeItem = (ElementNode)results.item(count);
//				Document nativeDocument = builder.newDocument();
//				Node importNode = nativeDocument.importNode(nativeItem, true);
//				nativeDocument.appendChild(importNode);
//				String record = constructRecord(nativeDocument, metadataPrefix);
//				records.add(record);
//			}
//		} catch (RemoteException e) {
//			throw new OAIInternalServerError(e.getMessage());
//		} catch (_SQIFaultException e) {
//			throw new OAIInternalServerError(e.getMessage());
//		} catch (TransformerException e) {
//			throw new OAIInternalServerError(e.getMessage());
//		} catch (ParserConfigurationException e) {
//			throw new OAIInternalServerError(e.getMessage());
//		} catch (FactoryConfigurationError e) {
//			throw new OAIInternalServerError(e.getMessage());
//		}
//		finally {
//			disconnect();
//		}
//		disconnect();
//		/* decide if you're done */
//		if (count+start < size) {
//			String resumptionId = getResumptionId();
//
//			Vector resumption = new Vector();
//			resumption.add(from);
//			resumption.add(until);
//			resumption.add(set);
//			resumptionResults.put(resumptionId,resumption);
//
//			/*****************************************************************
//			 * Construct the resumptionToken String however you see fit.
//			 *****************************************************************/
//			StringBuffer resumptionTokenSb = new StringBuffer();
//			resumptionTokenSb.append(resumptionId);
//			resumptionTokenSb.append(":");
//			resumptionTokenSb.append(Integer.toString(count+start));
//			resumptionTokenSb.append(":");
//			resumptionTokenSb.append(metadataPrefix);
//
//			listRecordsMap.put("resumptionMap", getResumptionMap(resumptionTokenSb.toString(),
//					size,
//					start));
//		}
//		else{
//			
//		}
//
//		listRecordsMap.put("records", records.iterator());
//		return listRecordsMap;
//	}
//	
//	/**
//	 * Retrieve the next set of records associated with the resumptionToken
//	 *
//	 * @param resumptionToken implementation-dependent format taken from the
//	 * previous listRecords() Map result.
//	 * @return a Map object containing entries for "headers" and "identifiers" Iterators
//	 * (both containing Strings) as well as an optional "resumptionMap" Map.
//	 * @exception BadResumptionTokenException the value of the resumptionToken argument
//	 * is invalid or expired.
//	 */
//	public Map listRecords(String resumptionToken)
//	throws BadResumptionTokenException {
//		
//		StringTokenizer tokenizer = new StringTokenizer(resumptionToken, ":");
//		String resumptionId;
//		int oldCount;
//		String metadataPrefix;
//		
//		try {
//			resumptionId = tokenizer.nextToken();
//			oldCount = Integer.parseInt(tokenizer.nextToken());
//			metadataPrefix = tokenizer.nextToken();
//		} catch (NoSuchElementException e) {
//			throw new BadResumptionTokenException();
//		}
//		Vector arguments = (Vector)resumptionResults.remove(resumptionId);
//		Map recordsList = null;
//		try {
//			recordsList = listRecords((String)arguments.elementAt(0), (String)arguments.elementAt(1), (String)arguments.elementAt(2), metadataPrefix, oldCount);
//			
//		} catch (CannotDisseminateFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (OAIInternalServerError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return recordsList;
//	}
//
//	public Map listIdentifiers(String resumptionToken) throws BadResumptionTokenException, OAIInternalServerError {
//		
//		StringTokenizer tokenizer = new StringTokenizer(resumptionToken, ":");
//		String resumptionId;
//		int oldCount;
//		String metadataPrefix;
//		
//		try {
//			resumptionId = tokenizer.nextToken();
//			oldCount = Integer.parseInt(tokenizer.nextToken());
//			metadataPrefix = tokenizer.nextToken();
//		} catch (NoSuchElementException e) {
//			throw new BadResumptionTokenException();
//		}
//		Vector arguments = (Vector)resumptionResults.remove(resumptionId);
//		Map identifierList = null;
//		try {
//			identifierList = listIdentifiers((String)arguments.elementAt(0), (String)arguments.elementAt(1), (String)arguments.elementAt(2), metadataPrefix, oldCount);
//		} catch (CannotDisseminateFormatException e) {
//			throw new OAIInternalServerError(e.getMessage());
//		} catch (OAIInternalServerError e) {
//			throw new OAIInternalServerError(e.getMessage());
//		}
//		return identifierList;
//	}
//
//	private String getDateQuery(String from, String until){
//		return "lom.metaMetadata.contribute.date.dateTime > " + from + " and lom.metaMetadata.contribute.date.dateTime < " + until ;
//	}
//
//	private String getIdentifierQuery(String identifier){
//		return "lom.metaMetadata.identifier.entry = \"" + identifier + "\""  ;
//	}
//
//	public String getRecord(String oaiIdentifier, String metadataPrefix) throws IdDoesNotExistException, CannotDisseminateFormatException, OAIInternalServerError {
//		//String identifier = getRecordFactory().fromOAIIdentifier(oaiIdentifier);
//		Document nativeDocument = null;
//
//		connect("plql1");
//		NodeList results = null;
//		SynchronousQueryResponse response = null;
//		int size = 0;
//
//		try {
//			//response = query("lom.metaMetadata.identifier = " + identifier );
//			response = query(getIdentifierQuery(oaiIdentifier),0);
//			
//			disconnect();
//			
//			Document doc = TargetUtils.getDomFromString(response.getSynchronousQueryReturn());
//			results = XPathAPI.selectNodeList(doc.getFirstChild(),"//lom");
//
//			size = results.getLength();
//			if (size > 0){
//				DocumentBuilder builder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();
//				Node nativeItem = (Node)results.item(0);
//				nativeDocument = builder.newDocument();
//				Node importNode = nativeDocument.importNode(nativeItem, true);
//				nativeDocument.appendChild(importNode);
//			}
//			else throw new IdDoesNotExistException(oaiIdentifier);
//		} catch (TransformerException e) {
//			disconnect();
//			throw new OAIInternalServerError(e.getMessage());
//		} catch (ParserConfigurationException e) {
//			disconnect();
//			throw new OAIInternalServerError(e.getMessage());
//		} catch (FactoryConfigurationError e) {
//			disconnect();
//			throw new OAIInternalServerError(e.getMessage());
//		}     
//		return constructRecord(nativeDocument, metadataPrefix);
//	}
//
//	public void close() {
//		// TODO Auto-generated method stub
//		
//	}
//}
