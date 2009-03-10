/**
 * 
 */
package org.ariadne_eu.service;

import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.ariadne_eu.mace.AddRelation;
import org.ariadne_eu.mace.CreateLOM;
import org.ariadne_eu.mace.CreateRWO;
import org.ariadne_eu.mace.EnrichFromAloe;
import org.ariadne_eu.mace.GetRelations;
import org.ariadne_eu.mace.GetRelationsResponse;
import org.ariadne_eu.mace.MACEFault;
import org.ariadne_eu.mace.MACEFaultCodeType;
import org.ariadne_eu.mace.MACEFaultException;
import org.ariadne_eu.mace.MACESkeleton;
import org.ariadne_eu.mace.RemoveRelation;
import org.ariadne_eu.metadata.insert.InsertMetadataFactory;
import org.ariadne_eu.metadata.query.QueryMetadataException;
import org.ariadne_eu.metadata.query.QueryMetadataFactory;
import org.ariadne_eu.utils.config.ConfigManager;
import org.ariadne_eu.utils.config.RepositoryConstants;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import be.cenorm.www.SessionExpiredException;
import be.cenorm.www.Ticket;

/**
 * @author gonzalo
 *
 */
public class MACEImplementation extends MACESkeleton {
	
	
	private static Logger log = Logger.getLogger(MACEImplementation.class);
	

	public void createRWO(CreateRWO createRWO) throws MACEFaultException{
		try {
			log.info("createRWO:resourceID="+createRWO.getResourceId()+",sessionID="+createRWO.getSessionId());
			Ticket ticket = Ticket.getTicket(createRWO.getSessionId()); //throws exception if no valid ticket exists
			checkValidTicket(ticket);
			
			Namespace lomNS = Namespace.getNamespace("","http://ltsc.ieee.org/xsd/LOM");
			Namespace lomNSM = Namespace.getNamespace("mace","http://www.mace-project.org/xsd/LOM");
			Namespace lomxsiNS = Namespace.getNamespace("xsi" , "http://www.w3.org/2001/XMLSchema-instance");
			Element root = new Element("lom", lomNS);
			root.addNamespaceDeclaration(lomNSM);
			root.addNamespaceDeclaration(lomxsiNS);
			Document doc = new Document();
			
			Element general = new Element("general", lomNS);
			Element identifier = new Element("identifier", lomNS);
			Element catalog = new Element("catalog", lomNS).setText("mace:external");
			Element entry1 = new Element("entry", lomNS).setText(createRWO.getResourceId());
			identifier.addContent(catalog);
			identifier.addContent(entry1);
			general.addContent(identifier);
			Element tit = new Element("title", lomNS);
			Element str1 = new Element("string", lomNS).setText(createRWO.getResourceTitle());
			tit.addContent(str1);
			general.addContent(tit);
			Element desc = new Element("description", lomNS);
			Element str2 = new Element("string", lomNS).setText(createRWO.getResourceDescription());
			desc.addContent(str2);
			general.addContent(desc);
			Element lok = new Element("learningObjectKind", lomNSM);
			Element src = new Element("source", lomNSM).setText("MACEv1.0");
			Element str3 = new Element("value", lomNSM).setText("real object");
			lok.addContent(src);
			lok.addContent(str3);
			general.addContent(lok);
			root.addContent(general);
			
			Element metametadata = new Element("metaMetadata", lomNS);
			Element identifierM = new Element("identifier", lomNS);
			Element entry2 = new Element("entry", lomNS).setText(createRWO.getResourceId()+"MD");
			identifierM.addContent(entry2);
			metametadata.addContent(identifierM);
			root.addContent(metametadata);

			Element educational = new Element("educational", lomNS);
			Element lrt = new Element("learningResourceType", lomNS);
			Element value = new Element("value", lomNS).setText(createRWO.getResourceType());
			lrt.addContent(value);
			educational.addContent(lrt);
			root.addContent(educational);

			doc.setRootElement(root);
			
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			outputter.setFormat(format);
			String output = outputter.outputString(doc);
	
			
			InsertMetadataFactory.insertMetadata(createRWO.getResourceId()+"MD", output);
		} catch (SessionExpiredException e) {
			log.debug("createRWO: ", e);
            MACEFault fault = new MACEFault();
            fault.setMaceFaultCode(MACEFaultCodeType.value1);
            fault.setMessage("The given session ID is invalid");
            MACEFaultException exception = new MACEFaultException();
            exception.setFaultMessage(fault);
            throw exception;
			
		} 
	}
	
	public void createLOM(CreateLOM createLOM) throws MACEFaultException{
		try {
			log.info("createLOM:resourceID="+createLOM.getResourceId()+",sessionID="+createLOM.getSessionId());
			Ticket ticket = Ticket.getTicket(createLOM.getSessionId()); //throws exception if no valid ticket exists
			checkValidTicket(ticket);
			
			Namespace lomNS = Namespace.getNamespace("","http://ltsc.ieee.org/xsd/LOM");
			Namespace lomNSM = Namespace.getNamespace("mace","http://www.mace-project.org/xsd/LOM");
			Namespace lomxsiNS = Namespace.getNamespace("xsi" , "http://www.w3.org/2001/XMLSchema-instance");
			Element root = new Element("lom", lomNS);
			root.addNamespaceDeclaration(lomNSM);
			root.addNamespaceDeclaration(lomxsiNS);
			Document doc = new Document();
			
			Element general = new Element("general", lomNS);
			Element identifier = new Element("identifier", lomNS);
			Element catalog = new Element("catalog", lomNS).setText("mace:external");
			Element entry1 = new Element("entry", lomNS).setText(createLOM.getResourceId());
			identifier.addContent(catalog);
			identifier.addContent(entry1);
			general.addContent(identifier);
			Element tit = new Element("title", lomNS);
			Element str1 = new Element("string", lomNS).setText(createLOM.getResourceTitle());
			tit.addContent(str1);
			general.addContent(tit);
			Element desc = new Element("description", lomNS);
			Element str2 = new Element("string", lomNS).setText(createLOM.getResourceDescription());
			desc.addContent(str2);
			general.addContent(desc);
			Element lok = new Element("learningObjectKind", lomNSM);
			Element src = new Element("source", lomNSM).setText("MACEv1.0");
			Element str3 = new Element("value", lomNSM).setText("media object");
			lok.addContent(src);
			lok.addContent(str3);
			general.addContent(lok);
			root.addContent(general);
			
			Element metametadata = new Element("metaMetadata", lomNS);
			Element identifierM = new Element("identifier", lomNS);
			Element entry2 = new Element("entry", lomNS).setText(createLOM.getResourceId()+"MD");
			identifierM.addContent(entry2);
			metametadata.addContent(identifierM);
			root.addContent(metametadata);

			Element educational = new Element("educational", lomNS);
			Element lrt = new Element("learningResourceType", lomNS);
			Element value = new Element("value", lomNS).setText(createLOM.getResourceType());
			lrt.addContent(value);
			educational.addContent(lrt);
			root.addContent(educational);

			doc.setRootElement(root);
			
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			outputter.setFormat(format);
			String output = outputter.outputString(doc);
			
			InsertMetadataFactory.insertMetadata(createLOM.getResourceId()+"MD", output);
		} catch (SessionExpiredException e) {
			log.debug("createLOM: ", e);
            MACEFault fault = new MACEFault();
            fault.setMaceFaultCode(MACEFaultCodeType.value1);
            fault.setMessage("The given session ID is invalid");
            MACEFaultException exception = new MACEFaultException();
            exception.setFaultMessage(fault);
            throw exception;
		} 
	}
	
	public void addRelation(AddRelation addRelation) throws MACEFaultException{
		
		try {
			log.info("addRelation:fromResourceID=" + addRelation.getFromResourceId() + "&toResourceID=" + addRelation.getToResourceId());
			Ticket ticket = Ticket.getTicket(addRelation.getSessionId()); //throws exception if no valid ticket exists
			checkValidTicket(ticket);
			
			Namespace lomNS = Namespace.getNamespace("","http://ltsc.ieee.org/xsd/LOM");
			
			int queryLanguage = 2; //plqlLevel1
	        int resultsFormat = 0; //lom
	        int startResult = 1;
	        int nbResults = 1;
	        int fromResultCount = QueryMetadataFactory.getQueryImpl(queryLanguage).count("lom.general.identifier.entry = \""+ addRelation.getFromResourceId() +"\"");

			if (fromResultCount < 1) {
				log.error("No such identifier fromResourceID:" + addRelation.getFromResourceId());
				return;
			} 
			String fromResult = QueryMetadataFactory.getQueryImpl(queryLanguage).query("lom.general.identifier.entry = \""+ addRelation.getFromResourceId() +"\"", startResult, nbResults, resultsFormat);
			
			
			SAXBuilder builder = new SAXBuilder();
			Reader in = new StringReader(fromResult);
			Document doc;
			doc = builder.build(in);
			
			// just to know that it exists!
			int toResultCount = QueryMetadataFactory.getQueryImpl(queryLanguage).count("lom.general.identifier.entry = \""+ addRelation.getToResourceId() +"\"");			
			if (toResultCount < 1) {
				log.error("No such identifier toResourceID:" + addRelation.getToResourceId());
				return;
			}
			
			Namespace lomNSM = Namespace.getNamespace("mace","http://www.mace-project.org/xsd/LOM");
			XPath xp = XPath.newInstance("//mace:learningObjectKind");
			xp.addNamespace(lomNSM);
			Element lokElement = (Element) xp.selectSingleNode(doc);

			if ( lokElement == null ) {
				log.error("This identifier is not a RWO" + addRelation.getFromResourceId());
				return;
			}
			
			Element root = doc.getRootElement().getChild("lom", lomNS);
			Element relationElmt = new Element("relation", lomNS);
			//kind
			Element kindElmt = new Element("kind", lomNS);
			Element sourceElmt = new Element("source", lomNS);
			Element sourceStrElmt = new Element("string", lomNS).setText("MACEv1.0");
			Element valueElmt = new Element("value", lomNS);
			Element valueStrElmt = new Element("string", lomNS).setText(addRelation.getRelationType());
			sourceElmt.addContent(sourceStrElmt);
			kindElmt.addContent(sourceElmt);
			valueElmt.addContent(valueStrElmt);
			kindElmt.addContent(valueElmt);
			relationElmt.addContent(kindElmt);
			//resource
			Element resourceElmt = new Element("resource",lomNS);
			Element identifierElmt = new Element("identifier", lomNS);
			Element catalogElmt = new Element("catalog", lomNS);
			Element catalogStrElmt = new Element("string", lomNS).setText("oai");
			Element entryElmt = new Element("entry", lomNS);
			Element entryStrElmt = new Element("string", lomNS).setText(addRelation.getToResourceId());
			Element descriptionElmt = new Element("description", lomNS);
			Element descriptionStrElmt = new Element("string", lomNS).setText("Reference from a LO to this RWO");
			catalogElmt.addContent(catalogStrElmt);
			identifierElmt.addContent(catalogElmt);
			entryElmt.addContent(entryStrElmt);
			identifierElmt.addContent(entryElmt);
			descriptionElmt.addContent(descriptionStrElmt);
			identifierElmt.addContent(descriptionElmt);
			resourceElmt.addContent(identifierElmt);
			relationElmt.addContent(resourceElmt);
			
			root.addContent(relationElmt);
			Document newDoc = new Document((Element)root.detach());
			
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			outputter.setFormat(format);
			String output = outputter.outputString(newDoc);
			
			InsertMetadataFactory.insertMetadata(addRelation.getFromResourceId()+"MD", output);

		} catch (QueryMetadataException e) {
			log.debug("addRelation: ", e);
            MACEFault fault = new MACEFault();
            fault.setMaceFaultCode(MACEFaultCodeType.value1);
            fault.setMessage("Query Exception");
            MACEFaultException exception = new MACEFaultException();
            exception.setFaultMessage(fault);
            throw exception;
		} catch (SessionExpiredException e) {
			log.debug("addRelation: ", e);
            MACEFault fault = new MACEFault();
            fault.setMaceFaultCode(MACEFaultCodeType.value1);
            fault.setMessage("Session Expired");
            MACEFaultException exception = new MACEFaultException();
            exception.setFaultMessage(fault);
            throw exception;
		} 
		catch (Exception e) {
			log.debug("addRelation: ", e);
	        MACEFault fault = new MACEFault();
	        fault.setMaceFaultCode(MACEFaultCodeType.value1);
	        fault.setMessage("Exception");
	        MACEFaultException exception = new MACEFaultException();
	        exception.setFaultMessage(fault);
	        throw exception;
		}
		
	}
	
	public void removeRelation(RemoveRelation removeRelation) throws MACEFaultException {
		
		try {
			log.info("removeRelation:identifier=" + removeRelation.getFromResourceId());
			Ticket ticket = Ticket.getTicket(removeRelation.getSessionId()); //throws exception if no valid ticket exists
			checkValidTicket(ticket);
			
			Namespace lomNS = Namespace.getNamespace("","http://ltsc.ieee.org/xsd/LOM");
			boolean flag = true;
			int queryLanguage = 2; //plqlLevel1
	        int resultsFormat = 0; //lom
	        int startResult = 1;
	        int nbResults = 1;
	        int count = QueryMetadataFactory.getQueryImpl(queryLanguage).count("lom.general.identifier.entry = \""+ removeRelation.getFromResourceId() +"\"");
	        
			if (count < 1) {
				log.error("No such identifier for RWO:" + removeRelation.getFromResourceId());
				return;
			}
			String result = QueryMetadataFactory.getQueryImpl(queryLanguage).query("lom.general.identifier.entry = \""+ removeRelation.getFromResourceId() +"\"", startResult, nbResults, resultsFormat);
			
			SAXBuilder builder = new SAXBuilder();
			Reader in = new StringReader(result);
			Document doc;
			doc = builder.build(in);
			
			Namespace ns = Namespace.getNamespace("lom","http://ltsc.ieee.org/xsd/LOM");
			XPath xpRelation = XPath.newInstance("//lom:relation");
			xpRelation.addNamespace(ns);
			List relations = xpRelation.selectNodes(doc);
			
			Element tempElmt;
			if (relations.size() == 0){
				log.error("The RWO doesnt have a relation:" + removeRelation.getFromResourceId());
				return;
			}
			
			XPath xpKind = XPath.newInstance("//lom:relation/lom:kind/lom:value/lom:string");
			xpKind.addNamespace(ns);
			XPath xpResource = XPath.newInstance("//lom:relation/lom:resource/lom:identifier/lom:entry/lom:string");
			xpResource.addNamespace(ns);
				
			Element root; 
			for (Iterator iterator = relations.iterator(); iterator.hasNext();) {
				Element relationElmt = (Element) iterator.next();
				tempElmt = (Element) xpKind.selectSingleNode(relationElmt);
				if (!removeRelation.getRelationType().equalsIgnoreCase(tempElmt.getText()))
					flag = false;
				tempElmt = (Element) xpResource.selectSingleNode(relationElmt);
				if (!removeRelation.getToResourceId().equalsIgnoreCase(tempElmt.getText()))
					flag = false;
				if (flag) {
					root = doc.getRootElement().getChild("lom", lomNS);
					boolean temp = root.removeContent(relationElmt);
					log.debug("removeRelation: " + temp);
					root.detach();
					Document newDoc = new Document(root);
					XMLOutputter outputter = new XMLOutputter();
					Format format = Format.getPrettyFormat();
					outputter.setFormat(format);
					String output = outputter.outputString(newDoc);
					InsertMetadataFactory.insertMetadata(removeRelation.getFromResourceId()+"MD", output);
				}
			}	
			
		} catch (QueryMetadataException e) {
			log.debug("removeRelation: ", e);
            MACEFault fault = new MACEFault();
            fault.setMaceFaultCode(MACEFaultCodeType.value1);
            fault.setMessage("Query Exception");
            MACEFaultException exception = new MACEFaultException();
            exception.setFaultMessage(fault);
            throw exception;
		} catch (SessionExpiredException e) {
			log.debug("removeRelation: ", e);
            MACEFault fault = new MACEFault();
            fault.setMaceFaultCode(MACEFaultCodeType.value1);
            fault.setMessage("Session Expired");
            MACEFaultException exception = new MACEFaultException();
            exception.setFaultMessage(fault);
            throw exception;
		} catch (Exception e) {
			log.debug("removeRelation: ", e);
	        MACEFault fault = new MACEFault();
	        fault.setMaceFaultCode(MACEFaultCodeType.value1);
	        fault.setMessage("Exception");
	        MACEFaultException exception = new MACEFaultException();
	        exception.setFaultMessage(fault);
	        throw exception;
		}
	}
	
	public GetRelationsResponse getRelations(GetRelations getRelations) throws MACEFaultException {
		try {
			GetRelationsResponse getRelationsResponse = new GetRelationsResponse();
			getRelationsResponse.set_return("");
			log.info("addRelation:resourceID=" + getRelations.getResourceId() );
			Ticket ticket = Ticket.getTicket(getRelations.getSessionId()); //throws exception if no valid ticket exists
			checkValidTicket(ticket);
			
			int queryLanguage = 2; //plqlLevel1
	        int resultsFormat = 0; //lom
	        int startResult = 1;
	        int nbResults = 1;
	        int count = QueryMetadataFactory.getQueryImpl(queryLanguage).count("lom.general.identifier.entry = \""+ getRelations.getResourceId() +"\"");
	        
	        String result = QueryMetadataFactory.getQueryImpl(queryLanguage).query("lom.general.identifier.entry = \""+ getRelations.getResourceId() +"\"", startResult, nbResults, resultsFormat);
			
			if (count < 1) {
				log.error("No such identifier fromResourceID:" + getRelations.getResourceId());
				return getRelationsResponse;
			}
			
			SAXBuilder builder = new SAXBuilder();
			Reader in = new StringReader(result);
			Document doc;
			doc = builder.build(in);
			
			Element root = new Element("relations");
			Document respDoc = new Document();
			
			Namespace lomNS = Namespace.getNamespace("lom","http://ltsc.ieee.org/xsd/LOM");
			XPath xp = XPath.newInstance("//lom:relation");
			xp.addNamespace(lomNS);
			List relations = xp.selectNodes(doc);

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			outputter.setFormat(format);
			for (int i = 0; i < relations.size(); i++) {
				Element relation = (Element) relations.get(i);
				relation.detach();
				root.addContent(relation);
			}
			respDoc.setRootElement(root);
			getRelationsResponse.set_return(outputter.outputString(respDoc));
			return getRelationsResponse;
		} catch (SessionExpiredException e) {
			log.debug("getRelations: ", e);
            MACEFault fault = new MACEFault();
            fault.setMaceFaultCode(MACEFaultCodeType.value1);
            fault.setMessage("Session Expired");
            MACEFaultException exception = new MACEFaultException();
            exception.setFaultMessage(fault);
            throw exception;
		} catch (QueryMetadataException e) {
			log.debug("getRelations: ", e);
            MACEFault fault = new MACEFault();
            fault.setMaceFaultCode(MACEFaultCodeType.value1);
            fault.setMessage("Query Exception");
            MACEFaultException exception = new MACEFaultException();
            exception.setFaultMessage(fault);
            throw exception;
		} catch (Exception e) {
			log.debug("getRelations: ", e);
            MACEFault fault = new MACEFault();
            fault.setMaceFaultCode(MACEFaultCodeType.value1);
            fault.setMessage("Exception");
            MACEFaultException exception = new MACEFaultException();
            exception.setFaultMessage(fault);
            throw exception;
		}
	}
	
	public void enrichFromAloe(EnrichFromAloe enrichFromAloe) throws MACEFaultException {
		log.info("enrichFromAloe");
		
	}
	
	private static void checkValidTicket(Ticket ticket) throws MACEFaultException {
        if (ticket.getParameter("username") == null ||
            !ticket.getParameter("username").equalsIgnoreCase(ConfigManager.getProperty(RepositoryConstants.REPO_USERNAME)) ||
            ticket.getParameter("password") == null ||
            !ticket.getParameter("password").equalsIgnoreCase(ConfigManager.getProperty(RepositoryConstants.REPO_PASSWORD))) {
        	MACEFault fault = new MACEFault();
            fault.setMaceFaultCode(MACEFaultCodeType.value1);
            fault.setMessage("The given session ID is invalid");
            MACEFaultException exception = new MACEFaultException();
            exception.setFaultMessage(fault);
            throw exception;
        }
    }

}
