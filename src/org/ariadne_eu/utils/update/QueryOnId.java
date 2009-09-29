package org.ariadne_eu.utils.update;

import java.util.List;

import org.apache.log4j.Logger;
import org.ariadne_eu.metadata.query.QueryMetadataFactory;
import org.ariadne_eu.metadata.query.language.TranslateLanguage;
import org.ariadne_eu.metadata.resultsformat.TranslateResultsformat;
import org.ariadne_eu.oai.utils.OaiUtils;
import org.ariadne_eu.utils.Stopwatch;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;




public class QueryOnId {

//	//MACE-dev-Enrich
//	private String target = "";
//	private String session = "";
//	private String enrichTarget = "";
//	private String enrichSession = "";

	private static Logger logger = Logger.getLogger(PublishMetadata.class);

//	private SqiSessionManagementStub sqiSessionStub = null;
//	private String sessionId = "";
	private Stopwatch watch = new Stopwatch();
	
	private static QueryOnId instance = null;

//	public static void main(String[] args) {
//		try {
//			System.out.println(getMACEquery().getMaceInstance("mace:rwo:8df13927-2067-11de-8a38-254c6b51af9f"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	private QueryOnId(){
//		session = PropertiesManager.getProperty("admin.sqi.ws.session.url");
//		target = PropertiesManager.getProperty("admin.sqi.ws.target.url");
//		enrichSession = PropertiesManager.getProperty("admin.sqi.enrich.session.url");
//		enrichTarget = PropertiesManager.getProperty("admin.sqi.enrich.target.url");

	}
	
	public static QueryOnId getMACEquery() {
		if(instance == null) {
			instance = new QueryOnId();
		}
		return instance;
	}

	public String getMaceInstance(String metadataIdentifier) throws Exception {

		try {
//			String sqiSessionManagement = sessionString;
//			sqiSessionStub = new SqiSessionManagementStub(session);
//			System.out.println(sqiSessionManagement);

//			CreateAnonymousSessionResponse session = sqiSessionStub.createAnonymousSession(new CreateAnonymousSession());
//			sessionId = session.getCreateAnonymousSessionReturn();
//			SqiTargetStub sqiStub = new SqiTargetStub(target);
//			//Set the Query language
//			SetQueryLanguage queryLanguage = new SetQueryLanguage();
//			queryLanguage.setQueryLanguageID("plql1");
//			queryLanguage.setTargetSessionID(session.getCreateAnonymousSessionReturn());
//			sqiStub.setQueryLanguage(queryLanguage);


//			SetResultsSetSize resultsSetSize = new SetResultsSetSize();
//			resultsSetSize.setResultsSetSize(100);
//			resultsSetSize.setTargetSessionID(session.getCreateAnonymousSessionReturn());
//			sqiStub.setResultsSetSize(resultsSetSize);

			//					FileInputStream fileInputStream = new FileInputStream(inputFileIds);
			//					InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF8");
			//					BufferedReader in = new BufferedReader(inputStreamReader);
			//					String line = null;
			XMLOutputter outputter = new XMLOutputter();
			Namespace ns = Namespace.getNamespace("lom","http://ltsc.ieee.org/xsd/LOM");
			XPath oaiIds = XPath.newInstance("//lom:lom/lom:metaMetadata/lom:identifier/lom:catalog[text()=\"oai\"]/parent::*/lom:entry");
			oaiIds.addNamespace(ns);
			XPath ids = XPath.newInstance("//lom:lom/lom:metaMetadata/lom:identifier/lom:entry");
			ids.addNamespace(ns);

//			SynchronousQuery query = new SynchronousQuery();
			//query.setQueryStatement("lom.metaMetadata.contribute.date.(dateTime > 2006-03-22T12:00:00Z and dateTime < 2007-02-10T12:00:00Z)");
			//query.setQueryStatement("lom.metaMetadata.contribute.date.dateTime > 1000-03-22T12:00:00Z and lom.metaMetadata.contribute.date.dateTime < 2007-02-10T12:00:00Z");
			String query = "lom.metaMetadata.identifier.entry = \"" + metadataIdentifier + "\"";
			//query.setQueryStatement("lom.general.title.string = \"tree\"");
//			query.setStartResult(1);
//			query.setTargetSessionID(sessionId);
			//SynchronousQueryResponse queryResponse = sqiStub.synchronousQuery(query);
			watch.start();
//			SynchronousQueryResponse result = sqiStub.synchronousQuery(query);
			logger.info("Requesting : " + metadataIdentifier);

			String resultString = QueryMetadataFactory.getQueryImpl(TranslateLanguage.PLQL1).query(query, 1, 12, TranslateResultsformat.LOM);
			
//			String synchronousQueryReturn = result.getSynchronousQueryReturn();
			watch.stopWPrint();
			logger.debug(resultString);
			Document doc = OaiUtils.parseXmlString2Lom(resultString);

			List results = doc.getRootElement().getChildren();
//			System.out.println(results.size());
			if(results.size() == 1) {
				
					Element el = (Element)results.get(0);
					el.detach();
					logger.info("Successfully Requested : " + metadataIdentifier);
					return outputter.outputString(el);
					
			}else if (results.size() < 1) {
				String msg = "No records found, please check the identifier";
//				logger.error(msg +  " : " + metadataIdentifier);
				throw new Exception(msg);
			}else if (results.size() > 1) {
				String msg = "Too many records found, please check the identifier";
//				logger.error(msg +  " : " + metadataIdentifier);
				throw new Exception(msg);
			}

			//        					OAIRecord record = repos.getRecord("oai:ariadne.cs.kuleuven.be:" + line.trim(),prefix);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(metadataIdentifier + " : " + e.getMessage());
			throw e;
		}
//		finally {
//			DestroySession destroySession = new DestroySession();
//			destroySession.setSessionID(sessionId);
//			try {
//				sqiSessionStub.destroySession(destroySession);
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (_SQIFaultException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		return null;
	}
	
//    private String query(String query) throws Exception {
//        try {
//            return QueryMetadataFactory.getQueryImpl(2).query(query, TranslateResultsformat.LOM, 12, TranslateLanguage.PLQL1);
////            result = TranslateResultsformat.processResults(result, TranslateResultsformat.LOM, TranslateLanguage.PLQL1, query);
////            SynchronousQueryResponse response = new SynchronousQueryResponse();
////            response.setSynchronousQueryReturn(result);
////            return response;
//        } catch (Exception e) {
////            log.error("synchronousQuery:query="+query+",sessionID="+synchronousQuery.getTargetSessionID()+",queryLanguage="+queryLanguage+",startResult="+startResult+",nbResults="+nbResults, e);
////            _SQIFault fault = new _SQIFault();
////            fault.setSqiFaultCode(FaultCodeType.SQI_00001);
////            fault.setMessage("Database exception");
////            _SQIFaultException exception = new _SQIFaultException();
////            exception.setFaultMessage(fault);
//        	logger.error(e.getMessage());
//            throw e;
//        }
//    }


//	private static Calendar starttime = null;
//
//	public static void begin(){
//		starttime = new GregorianCalendar();
//	}
//
//	public static void end(){
//		GregorianCalendar endtime = new GregorianCalendar();
//		long difference = endtime.getTimeInMillis() - starttime.getTimeInMillis();
//		int mins = (int)Math.floor(difference/60000.0);
//		long secs = (long)(difference/1000.0 - mins*60.0); 
//		logger.info(mins + " m " + secs + " s");
//	}

}
