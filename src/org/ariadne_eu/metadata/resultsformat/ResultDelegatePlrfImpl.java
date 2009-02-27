package org.ariadne_eu.metadata.resultsformat;

import net.sourceforge.minor.lucene.core.searcher.IndexSearchDelegate;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;

public class ResultDelegatePlrfImpl implements IndexSearchDelegate {

    private int start;
    private int max;

    public ResultDelegatePlrfImpl(int start, int max) {
        this.start = start;
        this.max = max;
    }

    public String result(Hits hits) throws Exception {
	    Document doc;

	    StringBuilder sBuild = new StringBuilder();
	    sBuild.append("<Record>\n");
		for (int i = start-1; i < hits.length() && (max < 0 || i < start-1+max); i++) {
	    	doc = hits.doc(i);
            //sBuild.append("<Metadata>").append(doc.get("contents")).append("</Metadata>").append("\n\n");//TODO: only return the part reguired for the given plrf level
	    	sBuild.append("<Metadata>").append(doc.get("lom")).append("</Metadata>").append("\n\n");//TODO: only return the part reguired for the given plrf level
	    }
	    sBuild.append("</Record>");

    	return sBuild.toString();
	}

}
