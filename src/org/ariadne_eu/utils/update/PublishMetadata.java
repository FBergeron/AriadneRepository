package org.ariadne_eu.utils.update;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.ariadne_eu.metadata.insert.InsertMetadataFactory;
import org.ariadne_eu.oai.utils.OaiUtils;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;


public class PublishMetadata {
//	private String session = "";
//	private String target = "";
//	private String user = "";
//	private String pass = "";


	private static PublishMetadata instance = null;
	
	private static Logger logger = Logger.getLogger(PublishMetadata.class);


//	private PublishMetadata(){
//		session = PropertiesManager.getProperty("admin.spi.session.url");
//		target = PropertiesManager.getProperty("admin.spi.target.url");
//		user = PropertiesManager.getProperty(RepositoryConstants.REPO_USERNAME);
//		pass = PropertiesManager.getProperty(RepositoryConstants.REPO_PASSWORD);
//	}

	public static PublishMetadata getMACEPublish() {
		if(instance == null) {
			instance = new PublishMetadata();
		}
		return instance;
	}

	public void publishMetadata(String metadata) throws Exception {

		
		
//		SqiSessionManagementStub sqiSessionStub = null;
//		String sessionId = null;
		SAXBuilder builder = new SAXBuilder();
		try {
//			sqiSessionStub = new SqiSessionManagementStub(session);
//			XMLOutputter outputter = new XMLOutputter();
			Namespace ns = Namespace.getNamespace("lom","http://ltsc.ieee.org/xsd/LOM");
			XPath oaiIds = XPath.newInstance("//lom:lom/lom:metaMetadata/lom:identifier/lom:catalog[text()=\"oai\"]/parent::*/lom:entry");
			oaiIds.addNamespace(ns);
			XPath ids = XPath.newInstance("//lom:lom/lom:metaMetadata/lom:identifier/lom:entry");
			ids.addNamespace(ns);

//			CreateAnonymousSession test = new CreateAnonymousSession();
//			CreateSession createSession = new CreateSession();
//			createSession.setUserID(user);
//			createSession.setPassword(pass);
//			//			CreateAnonymousSessionResponse sessionResponse = sqiSessionStub.createAnonymousSession(test);
//			CreateSessionResponse sessionResponse = sqiSessionStub.createSession(createSession);
//
//			sessionId = sessionResponse.getCreateSessionReturn();
//
//			SPIStub spiStub = new SPIStub(target);


			org.jdom.Document xmlDoc = builder.build(new StringReader(metadata));

			String id = null;

			Element foundOaiId = (Element) oaiIds.selectSingleNode(xmlDoc);
			if(foundOaiId != null) {
				id = foundOaiId.getTextTrim();
			} else {
				Element foundId = (Element) ids.selectSingleNode(xmlDoc);
				if(foundId != null) {
					id = foundId.getTextTrim();
				}
			}
			if (id == null) {
				throw new Exception("No Id found !!!");
			}

			
//			SubmitMetadataRecord submitRecord = new SubmitMetadataRecord();
//			submitRecord.setGlobalIdentifier(id);
			logger.info("Pushing " + id);

			InsertMetadataFactory.insertMetadata(id, OaiUtils.parseLom2XmlstringNoXmlHeader(xmlDoc.getRootElement()));
						
//			submitRecord.setMetadata(OaiUtils.parseLom2XmlstringNoXmlHeader(xmlDoc.getRootElement()));
//			submitRecord.setTargetSessionID(sessionId);
//			spiStub.submitMetadataRecord(submitRecord);
			logger.info("Successfully pushed : " + id);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
//		}	finally {
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
		}
	}
}
