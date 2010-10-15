package org.ariadne_eu.metadata.resultsformat.researchfm;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;



import net.sourceforge.minor.lucene.core.searcher.IndexSearchDelegate;

public class ResultDelegateAccounts implements IndexSearchDelegate {
	private static Logger log = Logger.getLogger(ResultDelegateAccounts.class);

    private int start;
    private int max;

    public ResultDelegateAccounts(int start, int max) {
        this.start = start;
        this.max = max;
    }

    public String result(Hits hits) throws Exception {
	    Document doc;
	    JSONObject json = new JSONObject();
	    
		for (int i = start-1; i < hits.length() && (max < 0 || i < start-1+max); i++) {
			doc = hits.doc(i);
			try {
				String[] url = doc.getValues("person.holdsaccount.onlineaccount.accounthomepage.rdf:resource");
				String[] services = doc.getValues("person.holdsaccount.onlineaccount.swrc:accountname");
				for (int j = 0; j < services.length; j++) {
					json.put(services[j], url[j]);
				}
				
			} catch (JSONException e) {
				log.debug("result :: id=" + doc.get("key"), e);
				log.error(e);
			}
	    	log.debug(doc.get("key") + " = " + hits.score(i));
	    }
		return json.toString();
    	
	}
}
