package org.ariadne_eu.metadata.resultsformat;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import net.sourceforge.minor.lucene.core.searcher.IndexSearchDelegate;

public class ResultDelegateICOPERJS implements IndexSearchDelegate {
	private static Logger log = Logger.getLogger(ResultDelegateICOPERJS.class);

    private int start;
    private int max;

    public ResultDelegateICOPERJS(int start, int max) {
        this.start = start;
        this.max = max;
    }

    public String result(Hits hits) throws Exception {
	    Document doc;
	    
	    JSONObject resultJson = new JSONObject();
	    JSONArray arrayJson = new JSONArray();
	    
		for (int i = start-1; i < hits.length() && (max < 0 || i < start-1+max); i++) {
			JSONObject json = new JSONObject();
	    	doc = hits.doc(i);
	    	try {
	    		json.put("id", doc.get("lom.general.identifier.entry"));
	    		json.put("md", doc.get("md"));
			} catch (JSONException ex) {
				log.error(ex);
			}
			arrayJson.put(json);
	    	log.debug(doc.get("key") + " = " + hits.score(i));
	    }
		resultJson.put("results",arrayJson);
		return resultJson.toString();
	}
}
