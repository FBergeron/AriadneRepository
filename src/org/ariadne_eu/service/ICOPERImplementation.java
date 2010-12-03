/**
 * 
 */
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

/**
 * @author gonzalo
 *
 */
@Singleton
@Path("/icoper")
public class ICOPERImplementation {
	private static Logger log = Logger.getLogger(ICOPERImplementation.class);
	
	//learningOpportunities
	//
	
	@GET @Path("/getLearningOutcomeDefinitions")
    @Produces("application/json")
	public String getLearningOutcomeDefinitions(@QueryParam("q") String query, @QueryParam("pn") int page_number, @QueryParam("ps") int page_size) {
		int start = 1;
		if (query.equalsIgnoreCase("")) {
			query = "learningoutcome.solr = \"all\"";
		}
		if (page_size < 1) {
			page_size = 10;
		}
		
		if (page_number >= 1)
			start = ((page_number-1) * page_size) + 1;
			
		
		log.info("getLearningOutcomeDefinitions: query=" + query + ", start=" + start + ", page_size=" + page_size);
		
		String fullquery = query;
		try {
			String result = QueryMetadataFactory.getQueryImpl(TranslateLanguage.PLQL1).query(fullquery, start, page_size, TranslateResultsformat.ILCJS);
			return result;
		} catch (QueryTranslationException e) {
			log.error("getLearningOutcomeDefinitions: QueryTranslationException", e);
		} catch (QueryMetadataException e) {
			log.error("getLearningOutcomeDefinitions: QueryMetadataException", e);
		}
		return "";
	}
	
	@GET @Path("/getLearningOutcomeDefinition")
    @Produces("application/json")
    public String getLearningOutcomeDefinition(@QueryParam("objId") String objectIdentifier) {
		if (objectIdentifier.equalsIgnoreCase("")) {
			log.error("getMetadata: Not a valid object identifier");
			return "";
		}
		
		log.info("getLearningOutcomeDefinition: objectIdentifier="+objectIdentifier);
		try {
			String fullquery = "learningoutcome.identifier.entry : \"" + objectIdentifier + "\"";
			String result = QueryMetadataFactory.getQueryImpl(TranslateLanguage.LUCENE).query(fullquery, 1, 1, TranslateResultsformat.ILCJS);
			return result;
		} catch (QueryTranslationException e) {
			log.error("getMetadata:QueryTranslationException", e);
		} catch (QueryMetadataException e) {
			log.error("getMetadata:QueryMetadataException", e);
		}
		return "";
    	
    }
	
	@GET @Path("/getTeachingMethods")
    @Produces("application/json")
    public String getTeachingMethods(@QueryParam("q") String query, @QueryParam("p n") int page_number, @QueryParam("ps") int page_size) {
		if (query.equalsIgnoreCase("")) {
			query = "lom.solr = \"all\"";
		}
		if (page_size < 1) {
			page_size = 10;
		}
		int start = 1;
		if (page_number > 1)
			start = ((page_number-1) * page_size) + 1;
		
		
		log.info("getTeachingMethods: query=" + query + ", page_number=" + page_number + ", page_size=" + page_size);
		
		try {
			String lQuery = TranslateLanguage.translateToQuery(query, TranslateLanguage.PLQL1, TranslateLanguage.LUCENE, start, page_size, TranslateResultsformat.ICJS);
			String fullquery = "(lom.educational.learningresourcetype.value : \"teaching method\" OR lom.educational.learningresourcetype.value : \"assessment method\") AND " + lQuery;
			String result = QueryMetadataFactory.getQueryImpl(TranslateLanguage.LUCENE).query(fullquery, start, page_size, TranslateResultsformat.ICJS);
			return result;
		} catch (QueryTranslationException e) {
			log.error("getTeachingMethods:QueryTranslationException", e);
		} catch (QueryMetadataException e) {
			log.error("getTeachingMethods:QueryMetadataException", e);
		}
		return "";
    }
	
	@GET @Path("/getAssessmentMethods")
    @Produces("application/json")
    public String getAssessmentMethods(@QueryParam("q") String query, @QueryParam("pn") int page_number, @QueryParam("ps") int page_size) {
		if (query.equalsIgnoreCase("")) {
			query = "lom.solr = \"all\"";
		}
		if (page_size < 1) {
			page_size = 10;
		}
		int start = 1;
		if (page_number > 1)
			start = ((page_number-1) * page_size) + 1;
		
		
		log.info("getAssessmentMethods: query=" + query + ", page_number=" + page_number + ", page_size=" + page_size);
		
		try {
			String lQuery = TranslateLanguage.translateToQuery(query, TranslateLanguage.PLQL1, TranslateLanguage.LUCENE, start, page_size, TranslateResultsformat.ICJS);
			String fullquery = "lom.educational.learningresourcetype.value : \"assessment method\" AND " + lQuery;
			String result = QueryMetadataFactory.getQueryImpl(TranslateLanguage.LUCENE).query(fullquery, start, page_size, TranslateResultsformat.ICJS);
			return result;
		} catch (QueryTranslationException e) {
			log.error("getAssessmentMethods:QueryTranslationException", e);
		} catch (QueryMetadataException e) {
			log.error("getAssessmentMethods:QueryMetadataException", e);
		}
		return "";
    }
	
	@GET @Path("/getLearningDesigns")
    @Produces("application/json")
    public String getUnitsOfLearning(@QueryParam("q") String query, @QueryParam("pn") int page_number, @QueryParam("ps") int page_size) {
		if (query.equalsIgnoreCase("")) {
			query = "lom.solr = \"all\"";
		}
		if (page_size < 1) {
			page_size = 10;
		}
		int start = 1;
		if (page_number > 1)
			start = ((page_number-1) * page_size) + 1;
		
		log.info("getUnitsOfLearning: query=" + query + ", start=" + start + ", page_size=" + page_size);
		try {
			String lQuery = TranslateLanguage.translateToQuery(query, TranslateLanguage.PLQL1, TranslateLanguage.LUCENE, start, page_size, TranslateResultsformat.ICJS);
			String fullquery = "(lom.educational.learningresourcetype.value : \"unit of learning\" OR lom.educational.learningresourcetype.value : \"learning design\") AND " + lQuery;
			String result = QueryMetadataFactory.getQueryImpl(TranslateLanguage.LUCENE).query(fullquery, start, page_size, TranslateResultsformat.ICJS);
			return result;
		} catch (QueryTranslationException e) {
			log.error("getUnitsOfLearning:QueryTranslationException", e);
		} catch (QueryMetadataException e) {
			log.error("getUnitsOfLearning:QueryMetadataException", e);
		}
		return "";
    }
	
	
	@GET @Path("/getLearningResources")
    @Produces("application/json")
    public String getLearningResources(@QueryParam("q") String query, @QueryParam("pn") int page_number, @QueryParam("ps") int page_size) {
		if (query.equalsIgnoreCase("")) {
			query = "lom.solr = \"all\"";
		}
		if (page_size < 1) {
			page_size = 10;
		}
		int start = 1;
		if (page_number > 1)
			start = ((page_number-1) * page_size) + 1;
		
		log.info("getLearningResources: query=" + query + ", start=" + start + ", page_size=" + page_size);
		try {
			String lQuery = TranslateLanguage.translateToQuery(query, TranslateLanguage.PLQL1, TranslateLanguage.LUCENE, start, page_size, TranslateResultsformat.ICJS);
			String fullquery = "(lom.educational.learningresourcetype.value:\"teaching method\" OR lom.educational.learningresourcetype.value:\"assessment method\"" +
					" OR lom.educational.learningresourcetype.value:\"unit of learning\"" +
					" OR lom.educational.learningresourcetype.value:\"learning design\"" +
					" OR lom.educational.learningresourcetype.value:\"learning assessment\"" +
					" OR lom.educational.learningresourcetype.value:\"other\")" +
					" AND " + lQuery;
			String result = QueryMetadataFactory.getQueryImpl(TranslateLanguage.LUCENE).query(fullquery, start, page_size, TranslateResultsformat.ICJS);
			return result;
		} catch (QueryTranslationException e) {
			log.error("getLearningOutcomeDefinitions: QueryTranslationException", e);
		} catch (QueryMetadataException e) {
			log.error("getLearningOutcomeDefinitions: QueryMetadataException", e);
		}
		return "";
    }
	
	@GET @Path("/getLearnerAssessments")
    @Produces("application/json")
    public String getLearningAssements(@QueryParam("q") String query, @QueryParam("pn") int page_number, @QueryParam("ps") int page_size) {
		if (query.equalsIgnoreCase("")) {
			query = "lom.solr = \"all\"";
		}
		int start = 1;
		if (page_number > 1)
			start = page_number * page_size;
		
		log.info("getLearningAssements: query=" + query + ", start=" + start + ", page_size=" + page_size);
		try {
			String lQuery = TranslateLanguage.translateToQuery(query, TranslateLanguage.PLQL1, TranslateLanguage.LUCENE, start, page_size, TranslateResultsformat.ICJS);
			String fullquery = "(lom.educational.learningresourcetype.value : \"learning assessment\" OR lom.educational.learningresourcetype.value : \"learner assessment\") AND " + lQuery;
			String result = QueryMetadataFactory.getQueryImpl(TranslateLanguage.LUCENE).query(fullquery, start, page_size, TranslateResultsformat.ICJS);
			return result;
		} catch (QueryTranslationException e) {
			log.error("getLearningAssements:QueryTranslationException", e);
		} catch (QueryMetadataException e) {
			log.error("getLearningAssements:QueryMetadataException", e);
		}
		return "";
    }
	
	//TODO: metadataIdentifier not implemented
	@GET @Path("/getMetadata")
    @Produces("application/json")
    public String getMetadata(@QueryParam("objId") String objectIdentifier, @QueryParam("mdId") String metadataIdentifier) {
		if (objectIdentifier.equalsIgnoreCase("")) {
			log.error("getMetadata: Not a valid object identifier");
			return "";
		}
		
		log.info("getMetadata: objectIdentifier="+objectIdentifier+", metadataIdentifier="+metadataIdentifier);
		try {
			String fullquery = "lom.metametadata.identifier.entry : \"" + objectIdentifier + "\" OR key : \"" + objectIdentifier + "\"";
			String result = QueryMetadataFactory.getQueryImpl(TranslateLanguage.LUCENE).query(fullquery, 1, 1, TranslateResultsformat.IJS);
			return result;
		} catch (QueryTranslationException e) {
			log.error("getMetadata:QueryTranslationException", e);
		} catch (QueryMetadataException e) {
			log.error("getMetadata:QueryMetadataException", e);
		}
		return "";
    	
    }
	
	

}
