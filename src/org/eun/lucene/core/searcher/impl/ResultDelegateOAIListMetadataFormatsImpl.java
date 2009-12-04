package org.eun.lucene.core.searcher.impl;


import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.minor.lucene.core.searcher.IndexTermSearchDelegate;

import org.apache.lucene.index.TermEnum;
import org.eun.lucene.core.utils.FormatMapping;

public class ResultDelegateOAIListMetadataFormatsImpl implements IndexTermSearchDelegate {
	
	private String field;
	
	public ResultDelegateOAIListMetadataFormatsImpl(String _field){
		this.field = _field;
	}
	
	public String result(TermEnum enum1) throws Exception {
	    String ns, schemaLocation, metadataPrefix, xmlns;
	    StringBuilder sResult = new StringBuilder();
		
	    sResult.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
				"<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\" "+
				         "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
				         "xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/ "+
				         "http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">"+
				  "<responseDate>"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"</responseDate>"+
				  "<request verb=\"ListMetadataFormats\"></request>"+
				  "<ListMetadataFormats>");
	    
	    while (enum1.term() != null && field.equals(enum1.term().field())) {
	    	ns = enum1.term().text();
	    	xmlns = ns.replaceAll("/", ".");
	    	xmlns = xmlns.replaceAll(":", ".");//Since those char are forbidden in a key field.
	    	
	    	metadataPrefix = FormatMapping.getText(xmlns);
	    	
	    	schemaLocation = FormatMapping.getText(metadataPrefix+".schema");
			
	    	
	    	sResult.append("<metadataFormat>"+
				        "<metadataPrefix>"+metadataPrefix+"</metadataPrefix>"+
				        "<schema>"+schemaLocation+
				          "</schema>"+
				        "<metadataNamespace>"+ns+
				          "</metadataNamespace>"+
				      "</metadataFormat>");
	    	enum1.next();//XXX
	    }
		  
	    sResult.append("</ListMetadataFormats>"+
				  		"</OAI-PMH>");
	    
	    return sResult.toString();
	}
}
