package org.ariadne_eu.service;

import org.ariadne.util.Stopwatch;
import org.ariadne_eu.utils.rest.Query;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.ariadne_eu.metadata.query.QueryMetadataException;
import org.ariadne_eu.metadata.query.QueryMetadataFactory;
import org.ariadne_eu.metadata.query.language.QueryTranslationException;
import org.ariadne_eu.metadata.query.language.TranslateLanguage;
import org.ariadne_eu.metadata.resultsformat.TranslateResultsformat;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path("/ariadne")
public class ARIADNEImplementation {
	
	private static Logger log = Logger.getLogger(ARIADNEImplementation.class);
	
	/*@GET
	@Produces("application/json")
	public String synchronousQuery(@QueryParam("query") String query, @QueryParam("start") String start, @QueryParam("size") String size) {

		int startResult = 1;
		int nbResults = 12;

		if (start != null) { 
			try {
				startResult = Integer.parseInt(start);
				if(startResult < 1) throw new Exception();
			} catch (Exception e) {
				log.error("setStartResult:Invalid Start Result", e);
			}
		}
		
		if (size != null) { 
			try {
				nbResults = Integer.parseInt(size);
				if(nbResults < 1) throw new Exception();
			} catch (Exception e) {
				log.error("setNumberOfResults:Invalid Results Set Size", e);
			}
		}
		
		try {
			String result = QueryMetadataFactory.getQueryImpl(TranslateLanguage.PLQL1).query(query, startResult, nbResults, TranslateResultsformat.ARFJS);
			return result;
		} catch (QueryTranslationException e) {
			log.error("synchronousQuery: QueryTranslationException", e);
		} catch (QueryMetadataException e) {
			log.error("synchronousQuery: QueryMetadataException", e);
		}
		return "";
	}*/
	
	@GET
	@Produces("application/json")
	public String query(@QueryParam("json") String json, @QueryParam("engine") String engine) {
		Stopwatch sw = new Stopwatch();
		sw.start();
		String query = "";
		Query qry = new Query();
		JSONObject jo;
		log.info("query:json=" + json);
		try {
			jo = new JSONObject(json);
			qry.parseJson(jo);
			//CNF :: ((a OR b) AND (c OR d))
			if (qry.searchTerms != null) {
				for (int i = 0; i < qry.searchTerms.length; i++) {
					if (i > 0)
						query = query.concat(" AND ");
					for (int j = 0; j < qry.searchTerms[i].length; j++) {						
						if (j > 0)
							query = query.concat(" OR ");
						if (qry.searchTerms[i].length > 1 && j == 0 )
							query = query.concat(" ( ");
						query = query.concat(qry.searchTerms[i][j]);
						if (qry.searchTerms[i].length > 1 && j == (qry.searchTerms[i].length - 1) )
							query = query.concat(" ) ");
					}
					
				}
			}
			if (qry.exclusionTerms != null) {
				for (int i = 0; i < qry.exclusionTerms.length; i++) {
//					System.out.println(qry.exclusionTerms[i]);
				}
			}
			if (qry.facets != null) {
				for (int i = 0; i < qry.facets.length; i++) {
//					System.out.println(qry.facets[i]);
				}
			}
			
//			System.out.println(qry.idListOffset);
//			System.out.println(qry.idListSize);
//			System.out.println(qry.maxCntFacets);
//			System.out.println(qry.sortKey);
			
			if (qry.rankingTerms != null) {
				for (int i = 0; i < qry.rankingTerms.length; i++) {
//					System.out.println(qry.rankingTerms[i]);
				}
			}
			
			String result = QueryMetadataFactory.getQueryImpl(TranslateLanguage.LUCENE).query(query, qry.resultListOffset, qry.resultListSize, TranslateResultsformat.ARFJS);
			JSONObject jResults = new JSONObject(result);
			JSONObject jResult = jResults.getJSONObject("result");
			jResult.put("processingTime", sw.stop());
			
			return jResults.toString();
			
		} catch (JSONException e) {
			log.error("synchronousQuery: QueryTranslationException", e);
		}
		catch (QueryTranslationException e) {
			log.error("synchronousQuery: QueryTranslationException", e);
		} catch (QueryMetadataException e) {
			log.error("synchronousQuery: QueryMetadataException", e);
		}

		return "";
	}
	
}
