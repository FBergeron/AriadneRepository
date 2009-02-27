package org.eun.lucene.core.searcher.impl;


import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.minor.lucene.core.searcher.IndexSearchDelegate;
import net.sourceforge.minor.lucene.core.utils.Formatter;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;

public class ResultDelegateOAIListIdentifiersImpl implements IndexSearchDelegate {
	
	private String format;
	
	public ResultDelegateOAIListIdentifiersImpl(String _format){
		this.format = _format;
	}
	
	public String result(Hits hits) throws Exception {
	    Document doc;
	    
	    StringBuilder sBuild = new StringBuilder();
	    sBuild.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	    		"<OAI-PMH>"+
	    "<responseDate>"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"</responseDate>"+
	    "<request "+format+"/>"+
	    "<ListRecords>");
	    
	    String identifier, sDate;
	    
	    for (int i = 0; i < hits.length(); i++) {
	    	doc = hits.doc(i);
	    	identifier = doc.get("key");
	    	sDate = doc.get("date.insert") == null ? doc.get("date.update") : doc.get("date.insert");
	    	
	    	sBuild.append("<record>"+
					    "<header>"+
					    "<identifier>"+identifier+"</identifier>"+
					    "<datestamp>"+Formatter.formatDate(sDate)+"</datestamp>"+
					    "</header>"+
					    "</record>");
	    }
	    
	    sBuild.append("</ListRecords>"+
	    			"</OAI-PMH>");
	    
    	return sBuild.toString();
	}

}
