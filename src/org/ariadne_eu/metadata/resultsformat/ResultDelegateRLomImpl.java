/**
 * 
 */
package org.ariadne_eu.metadata.resultsformat;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sourceforge.minor.lucene.core.searcher.IndexSearchDelegate;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;
import org.ariadne_eu.metrics.webservices.GeneralGetRankingMetricValues;
import org.ariadne_eu.metrics.webservices.GeneralGetRankingMetricValuesResponse;
import org.ariadne_eu.metrics.webservices.RankingMetricsStub;
import org.ariadne_eu.utils.config.ConfigManager;
import org.ariadne_eu.utils.config.RepositoryConstants;

/**
 * @author gonzalo
 *
 */
public class ResultDelegateRLomImpl implements IndexSearchDelegate {

	private int start;
    private int max;

    public ResultDelegateRLomImpl(int start, int max) {
        this.start = start;
        this.max = max;
    }

    public String result(Hits hits) throws Exception {
	    Document doc;

	    String rankingURL, rankingMetric; 
	    rankingURL = ConfigManager.getProperty(RepositoryConstants.MD_RF_RLOM_URL);
	    rankingMetric = ConfigManager.getProperty(RepositoryConstants.MD_RF_RLOM_RMETRIC);
	    if (rankingURL != null && rankingMetric !=null) {
	    	
	    	//for sorting
	    	Map<String, String> map = new HashMap<String, String>();
	    	RankingMetricsStub stub;
	    	GeneralGetRankingMetricValues params;
	    	GeneralGetRankingMetricValuesResponse response;
	    	
	    	for (int i = start-1; i < hits.length() && (max < 0 || i < start-1+max); i++) {
	    		doc = hits.doc(i);
	    		stub = new RankingMetricsStub(rankingURL);
				params = new GeneralGetRankingMetricValues();
				params.setMetricId(rankingMetric);
				params.setTimePeriod(5);
				params.addParams(doc.get("key"));
				params.setK(10);
				response =  stub.generalGetRankingMetricValues(params);
				
				map.put(""+i, response.get_return());
	    		
	    	}
		    
	    	List<Map.Entry<String, String>> list = new Vector<Map.Entry<String, String>>(map.entrySet());
	    	
	    	java.util.Collections.sort(list, new Comparator<Map.Entry<String, String>>(){
	             public int compare(Map.Entry<String, String> entry, Map.Entry<String, String> entry1)
	             {
	                 // Return 0 for a match, -1 for less than and +1 for more then
	            	 float lentry = Float.parseFloat(entry.getValue());
	            	 float lentry1 = Float.parseFloat(entry1.getValue());
	                 return (entry.getValue().equals(entry1.getValue()) ? 0 : ( lentry > lentry1 ? -1 : 1));
	             }
	         });
	    	
	    	map.clear();
	    	
	    	//build the resultset
	    	StringBuilder sBuild = new StringBuilder();
		    sBuild.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<results>\n");
	    	for (Map.Entry<String, String> entry: list) {
//	    		map.put(entry.getKey(), entry.getValue());
//	    		sBuild.append("<result>\n");
				doc = hits.doc(Integer.parseInt(entry.getKey()));
				//sBuild.append(doc.get("contents")+"\n\n");
		    	sBuild.append(doc.get("lom")+"\n\n");
		    	
//		    	sBuild.append("<score>\n");
		    	
//				sBuild.append(entry.getValue());
//		    	sBuild.append("</score>\n");
//		    	sBuild.append("</result>\n");
	        }
	    	
		    sBuild.append("</results>");
	
	    	return sBuild.toString();
	    } else {
	    	
	    	throw new RuntimeException("");
	    }
	}
}
