package org.ariadne_eu.metadata.resultsformat;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.Hits;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.core.SolrCore;
import org.apache.solr.servlet.DirectSolrConnection;
import org.ariadne.config.PropertiesManager;
import org.ariadne.util.Stopwatch;
import org.ariadne_eu.utils.config.RepositoryConstants;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import net.sourceforge.minor.lucene.core.searcher.IndexSearchDelegate;

public class ResultDelegateARIADNERFJS implements IndexSearchDelegate {
	private static Logger log = Logger.getLogger(ResultDelegateARIADNERFJS.class);

    private int start;
    private int max;
    private String lQuery;
	private static String instanceDir;
	private static String dataDir;
	private static String loggingPath;
	private DirectSolrConnection conn;
	private static Vector facetFields;
    
    static {
		try {
			instanceDir = (PropertiesManager.getInstance().getPropFile()).replaceAll("install/ariadne.properties", "solr/");
			dataDir = PropertiesManager.getInstance().getProperty(RepositoryConstants.SR_SOLR_DATADIR);
			loggingPath = PropertiesManager.getInstance().getProperty(RepositoryConstants.REPO_LOG4J_DIR);

			facetFields = new Vector();
			int i = 1;

			Collection solrs = PropertiesManager.getInstance().getPropertyStartingWith(RepositoryConstants.SR_SOLR_FACETFIELD + ".").values();
			for (Object object : solrs) {
				facetFields.add((String) object);
			}

			if (instanceDir == null) {
				log.error("Could not load Solr instance dir!");
			} else if (dataDir == null) {
				log.warn("initialize:property \"" + RepositoryConstants.SR_SOLR_DATADIR + "\" not defined");
			} else if (loggingPath == null) {
				log.warn("initialize:property \"" + RepositoryConstants.REPO_LOG4J_DIR + "\" not defined");
			} else if (!(facetFields.size() > 0)) {
				log.error("initialize:property \"" + RepositoryConstants.SR_SOLR_FACETFIELD + ".n\" not defined");
			}

		} catch (Throwable t) {
			log.error("initialize: ", t);
		}
	}

    public ResultDelegateARIADNERFJS(int start, int max, String lQuery) {
        this.start = start;
        this.max = max;
        this.lQuery = lQuery;
        conn = new DirectSolrConnection(instanceDir, dataDir, loggingPath);
    }

    //TODO: fix the returning keywords (it only returns the first one!)
    public String result(Hits hits) throws JSONException, CorruptIndexException, IOException {
	    Document doc;
	    
	    JSONObject resultsJson = new JSONObject();
	    JSONObject resultJson = new JSONObject();
	    JSONArray idArrayJson = new JSONArray();
	    JSONArray metadataArrayJson = new JSONArray();
	    resultJson.put("error", "");
		resultJson.put("errorMessage", "");
		resultJson.put("facets", getFacets());
	    
	    
		for (int i = start; i < hits.length() && (max < 0 || i < start+max); i++) {
			JSONObject json = new JSONObject();
	    	doc = hits.doc(i);
	    	try {
	    		idArrayJson.put(doc.get("lom.general.identifier.entry"));
	    		json.put("title", doc.get("lom.general.title.string"));
	    		json.put("description", doc.get("lom.general.description.string"));
	    		json.put("keywords", doc.get("lom.general.keyword.string"));
	    		json.put("location", doc.get("lom.technical.location"));
	    		json.put("identifier", doc.get("lom.general.identifier.entry"));
			} catch (JSONException ex) {
				log.error(ex);
			}
			metadataArrayJson.put(json);
	    	log.debug(doc.get("key") + " = " + hits.score(i));
	    }
		resultJson.put("id", idArrayJson);
		resultJson.put("metadata", metadataArrayJson);
		resultJson.put("nrOfResults", hits.length());
		
		
		resultsJson.put("result",resultJson);
		return resultsJson.toString();
	}
    
    private JSONArray getFacets() {
    	JSONArray facetsJson = new JSONArray();
    	

		SolrCore core = SolrCore.getSolrCore();
		SolrServer server = new EmbeddedSolrServer(core);

		SolrQuery solrQuery = new SolrQuery().setQuery(lQuery).setFacet(true).setFacetLimit(-1).setFacetMinCount(1).setFacetSort(true);

		ModifiableSolrParams params = new ModifiableSolrParams();
		params.set("queryResultWindowSize", Integer.toString(max));
		solrQuery.add(params);

		for (Iterator iterator = facetFields.iterator(); iterator.hasNext();) {
			String facetField = (String) iterator.next();
			solrQuery.addFacetField(facetField);
		}

		QueryResponse rsp;
		try {
			rsp = server.query(solrQuery);

		List facetsFields = rsp.getFacetFields();
		if (facetsFields.size() > 0) {
			List facetValues;
			FacetField facetField;
			FacetField.Count innerFacetField;
			for (Iterator facetIterator = facetsFields.iterator(); facetIterator.hasNext();) {
				JSONObject facetJson = new JSONObject();
				facetField = (FacetField) facetIterator.next();
				facetJson.put("field", facetField.getName());
				facetValues = facetField.getValues();
				if (facetValues != null) {
					JSONArray valuesJson = new JSONArray();
					for (Iterator ifacetIterator = facetValues.iterator(); ifacetIterator.hasNext();) {
						JSONObject value = new JSONObject();
						innerFacetField = (FacetField.Count) ifacetIterator.next();
						value.put("val", innerFacetField.getName());
						value.put("count", innerFacetField.getCount());
						valuesJson.put(value);
					}
					facetJson.put("numbers", valuesJson);
				}
				facetsJson.put(facetJson);
			}
			
		}
		} catch (SolrServerException e) {
			log.error("getFacets: Solr server error", e);
		} catch (JSONException e) {
			log.error("getFacets: JSON format error", e);
		}

		conn.close();

		return facetsJson;
    }
}
