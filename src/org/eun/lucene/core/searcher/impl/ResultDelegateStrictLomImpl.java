package org.eun.lucene.core.searcher.impl;


import net.sourceforge.minor.lucene.core.searcher.IndexSearchDelegate;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;

public class ResultDelegateStrictLomImpl implements IndexSearchDelegate {

	public String result(Hits hits) throws Exception {
	    Document doc;
	    
	    StringBuilder sBuild = new StringBuilder();
	    sBuild.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				 		"<strictLomResults xmlns=\"http://fire.eun.org/xsd/strictLomResults-1.0\">\n");
		for (int i = 0; i < hits.length(); i++) {
	    	doc = hits.doc(i);
//	    	sBuild.append(doc.get("contents")+"\n\n");
	    	sBuild.append(doc.get("lom")+"\n\n");
	    }
	    sBuild.append("</strictLomResults>");
	    
    	return sBuild.toString();
	}

}
