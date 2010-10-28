package org.ariadne_eu.metadata.resultsformat.researchfm;

import net.sourceforge.minor.lucene.core.searcher.IndexSearchDelegate;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.codehaus.jettison.json.JSONObject;

public class ResultDelegatePublications implements IndexSearchDelegate {
	private static Logger log = Logger.getLogger(ResultDelegatePublications.class);

    private int start;
    private int max;

    public ResultDelegatePublications(int start, int max) {
        this.start = start;
        this.max = max;
    }

    public String result(TopDocs topDocs, IndexSearcher searcher) throws Exception {
	    Document doc;
	    JSONObject json = new JSONObject();
	    
		/*for (int i = start-1; i < hits.length() && (max < 0 || i < start-1+max); i++) {
			doc = hits.doc(i);
			try {
				json.put("fullname", doc.get("person.name.firstname") + " " + doc.get("person.name.lastname"));
				json.put("mail", doc.get("person.email"));
				json.put("institution", doc.get("person.affiliation"));
				json.put("photo", doc.get("person.picture.rdf:resource"));
			} catch (JSONException e) {
				log.debug("result :: id=" + doc.get("key"), e);
				log.error(e);
			}
	    	log.debug(doc.get("key") + " = " + hits.score(i));
	    }*/
		return json.toString();
    	
    	//return full xml
//    	Document doc;
//		for (int i = start-1; i < hits.length() && (max < 0 || i < start-1+max); i++) {
//			doc = hits.doc(i);
//			return doc.get("md");
//	    }
//		return "";
    	
	}
}
