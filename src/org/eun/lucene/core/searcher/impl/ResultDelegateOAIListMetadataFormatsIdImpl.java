package org.eun.lucene.core.searcher.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.minor.lucene.core.searcher.IndexSearchDelegate;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;
import org.eun.lucene.core.utils.FormatMapping;

public class ResultDelegateOAIListMetadataFormatsIdImpl implements IndexSearchDelegate {
	
	private String format;
	
	public ResultDelegateOAIListMetadataFormatsIdImpl(String _format){
		this.format = _format;
	}
	
	public String result(Hits hits) throws Exception {
		Document doc;
	    String ns, schemaLocation, metadataPrefix;
	    StringBuilder sResult = new StringBuilder();
		
	    sResult.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
				"<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\" "+
				         "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
				         "xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/ "+
				         "http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">"+
				  "<responseDate>"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"</responseDate>"+
				  "<request verb=\"ListMetadataFormats\"></request>"+
				  "<ListMetadataFormats>");
		
	    
	    for (int i = 0; i < hits.length(); i++) {
	    	doc = hits.doc(i);
	    	schemaLocation = doc.get("xsi:schemaLocation");;
	    	ns = doc.get("xmlns");
	    	metadataPrefix = FormatMapping.getText(ns);
	    	
	    	sResult.append("<metadataFormat>"+
			        "<metadataPrefix>"+metadataPrefix+"</metadataPrefix>"+
			        "<schema>"+schemaLocation+
			          "</schema>"+
			        "<metadataNamespace>"+ns+
			          "</metadataNamespace>"+
			      "</metadataFormat>");
	    }
		  
	    sResult.append("</ListMetadataFormats>"+
				  		"</OAI-PMH>");
	    
	    return sResult.toString();
	}

}
