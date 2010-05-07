package org.ariadne_eu.service;

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

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path("/ariadne")
public class ARIADNEImplementation {
	
	private static Logger log = Logger.getLogger(ARIADNEImplementation.class);
	
	@GET
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
			log.error("getLearningOutcomeDefinitions: QueryTranslationException", e);
		} catch (QueryMetadataException e) {
			log.error("getLearningOutcomeDefinitions: QueryMetadataException", e);
		}
		return "";
	}
	
}
