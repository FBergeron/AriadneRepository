package org.ariadne_eu.utils.registry;

import org.jdom.Element;
import org.jdom.Namespace;

public class ProtocolImplementationDescription {
	
		OaiPmh _oaiPmh;
		
	public ProtocolImplementationDescription(){
		
	}
	
	public ProtocolImplementationDescription(OaiPmh oaiPmh){
		_oaiPmh=oaiPmh;		
	}
	
	public void setOaiPmh(OaiPmh oaiPmh){
		_oaiPmh=oaiPmh;		
	}
	
	public OaiPmh getOaiPmh(){
		return _oaiPmh;		
	}
	
	public void parseXMLOaiPmh(Element protocolDescription, Namespace ns){
		_oaiPmh = new OaiPmh();
		Namespace oai =Namespace.getNamespace("http://www.imsglobal.org/services/lode/imslooaipmh-2p0_v1p0");
		_oaiPmh.parseXMLOaiPmh((Element)protocolDescription.getChildren().get(0),ns);
	}

}
