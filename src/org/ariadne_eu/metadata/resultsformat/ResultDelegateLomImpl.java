package org.ariadne_eu.metadata.resultsformat;

import net.sourceforge.minor.lucene.core.searcher.IndexSearchDelegate;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;

public class ResultDelegateLomImpl implements IndexSearchDelegate {

    private int start;
    private int max;

    public ResultDelegateLomImpl(int start, int max) {
        this.start = start;
        this.max = max;
    }

    public String result(Hits hits) throws Exception {
	    Document doc;

	    StringBuilder sBuild = new StringBuilder();
	    sBuild.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<results>\n");
		for (int i = start-1; i < hits.length() && (max < 0 || i < start-1+max); i++) {
	    	doc = hits.doc(i);
	    	//sBuild.append(doc.get("contents")+"\n\n");
	    	sBuild.append(doc.get("lom")+"\n\n");
//	    	sBuild.append(doc.get("key"));
	    	
	    }
	    sBuild.append("</results>");

    	return sBuild.toString();
	}

}
