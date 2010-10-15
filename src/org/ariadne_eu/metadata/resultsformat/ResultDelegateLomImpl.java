package org.ariadne_eu.metadata.resultsformat;

import java.io.File;
import java.io.IOException;

import net.sourceforge.minor.lucene.core.searcher.IndexSearchDelegate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Hits;
import org.ariadne.config.PropertiesManager;
import org.ariadne_eu.utils.config.RepositoryConstants;

public class ResultDelegateLomImpl implements IndexSearchDelegate {
	private static Logger log = Logger.getLogger(ResultDelegateLomImpl.class);

    private int start;
    private int max;

    public ResultDelegateLomImpl(int start, int max) {
        this.start = start;
        this.max = max;
    }

    public String result(Hits hits) throws Exception {
	    Document doc;

	    StringBuilder sBuild = new StringBuilder();
	    sBuild.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<results cardinality=\""+hits.length()+"\">\n");
	    
	    String luceneHandler = PropertiesManager.getInstance().getProperty(RepositoryConstants.getInstance().SR_LUCENE_HANDLER);
	    
		for (int i = start-1; i < hits.length() && (max < 0 || i < start-1+max); i++) {
	    	doc = hits.doc(i);
	    	log.debug(doc.get("key") + " = " + hits.score(i));
	    	
	    	if (!luceneHandler.equalsIgnoreCase("org.ariadne_eu.utils.lucene.document.LOMLiteHandler")) {
	    		sBuild.append(doc.get("md"));
            } else {
            	sBuild.append("<lom>")
            		.append("<general>")
            		.append("<identifier><entry>")
            			.append(doc.get("lom.general.identifier.entry"))
            		.append("</entry></identifier>")
            		.append("<title><string>")
            			.append(doc.get("lom.general.title.string"))
            		.append("</string></title>")
            		.append("<description><string>")
            			.append(doc.get("lom.general.description.string"))
            		.append("</string></description>")
            		.append("<keyword><string>")
            			.append(doc.get("lom.general.keyword.string"))
            		.append("</string></keyword>")
            		.append("</general>")
            		.append("</technical></location>")
            			.append(doc.get("lom.technical.location"))
            		.append("</location></technical>")
            		.append("</lom>")
            		.append("\n");
            }
	    	
	    }
	    sBuild.append("</results>");
    	return sBuild.toString();
	}


}
