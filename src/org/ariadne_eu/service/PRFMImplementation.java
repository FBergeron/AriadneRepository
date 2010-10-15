package org.ariadne_eu.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;
import org.ariadne_eu.metadata.query.QueryMetadataException;
import org.ariadne_eu.metadata.query.QueryMetadataFactory;
import org.ariadne_eu.metadata.query.language.QueryTranslationException;
import org.ariadne_eu.metadata.query.language.TranslateLanguage;
import org.ariadne_eu.metadata.resultsformat.TranslateResultsformat;


@Path("/") 
public class PRFMImplementation {
	
	private static Logger log = Logger.getLogger(PRFMImplementation.class);
	
	String papers = "{\"years\" : [{ \"year\" : \"2009\", \"papers\" : [ {\"title\": \"The integration of a metadata generation framework in a music annotation workflow\", \"url\": \"https://lirias.kuleuven.be/handle/123456789/249124\", \"keywords\": \"metadata\"}, {\"title\": \"A Web-based approach to determine the origin of an artist\", \"url\": \"https://lirias.kuleuven.be/handle/123456789/249123\", \"keywords\": \"metadata\"}]},{ \"year\" : \"2008\", \"papers\" : [ { \"title\": \"Special issue on social information retrieval for technology enhanced learning\", \"url\": \"https://lirias.kuleuven.be/handle/123456789/234787\", \"keywords\": \"metadata\"}, { \"title\": \"Using search engine for classification: does it still work?\", \"url\": \"https://lirias.kuleuven.be/handle/123456789/246659\", \"keywords\": \"metadata\"}]}]}";

	@Path("/person/{author_id}")
	@GET
    @Produces("application/json")
    public String getAuthor(@PathParam("author_id") String id) {
		
		if (id.equalsIgnoreCase("")) {
			log.error("getAuthor: Not a valid author identifier");
			return "";
		}
		
		log.info("getAuthor: author_id=" + id);
		try {
			String fullquery = "person.uri : \"" + id + "\"";
			String result = QueryMetadataFactory.getQueryImpl(TranslateLanguage.LUCENE).query(fullquery, 1, 1, TranslateResultsformat.PRFM);
			return result;
		} catch (QueryTranslationException e) {
			log.error("getMetadata:QueryTranslationException", e);
		} catch (QueryMetadataException e) {
			log.error("getMetadata:QueryMetadataException", e);
		}
		return "";
    }
	
	
	
	@Path("/person/{author_id}/accounts")
	@GET
    @Produces("application/json")
    public String getAuthorAccounts(@PathParam("author_id") String id) {		
		if (id.equalsIgnoreCase("")) {
			log.error("getAuthorAccounts: Not a valid author identifier");
			return "";
		}
		
		log.info("getAuthorAccounts: author_id=" + id);
		try {
			String fullquery = "person.uri : \"" + id + "\"";
			String result = QueryMetadataFactory.getQueryImpl(TranslateLanguage.LUCENE).query(fullquery, 1, 1, TranslateResultsformat.PARFM);
			return result;
		} catch (QueryTranslationException e) {
			log.error("getMetadata:QueryTranslationException", e);
		} catch (QueryMetadataException e) {
			log.error("getMetadata:QueryMetadataException", e);
		}
		return "";
    }
	
	@Path("/person/{author_id}/latestpublication")
	@GET
    @Produces("application/json")
    public String getAuthorLatestPublication(@PathParam("author_id") String id) {
		if (id.equalsIgnoreCase("")) {
			log.error("getAuthorLatestPublication: Not a valid author identifier");
			return "";
		}
		
		log.info("getAuthorLatestPublication: author_id=" + id);
		try {
			String fullquery = "person.uri : \"" + id + "\"";
			String result = QueryMetadataFactory.getQueryImpl(TranslateLanguage.LUCENE).query(fullquery, 1, 1, TranslateResultsformat.PPRFM);
			return result;
		} catch (QueryTranslationException e) {
			log.error("getMetadata:QueryTranslationException", e);
		} catch (QueryMetadataException e) {
			log.error("getMetadata:QueryMetadataException", e);
		}
		return "";
    }
	
	@Path("/person/{author_id}/publications")
	@GET
    @Produces("application/json")
    public String getAuthorListPublicatiosn(@PathParam("author_id") String id) {
		if (id.equalsIgnoreCase("")) {
			log.error("getAuthorListPublicatiosn: Not a valid author identifier");
			return "";
		}
		
		log.info("getAuthorListPublicatiosn: author_id=" + id);
		try {
			String fullquery = "person.uri : \"" + id + "\"";
			String result = QueryMetadataFactory.getQueryImpl(TranslateLanguage.LUCENE).query(fullquery, 1, 1, TranslateResultsformat.PLPRFM);
			return result;
		} catch (QueryTranslationException e) {
			log.error("getMetadata:QueryTranslationException", e);
		} catch (QueryMetadataException e) {
			log.error("getMetadata:QueryMetadataException", e);
		}
		return "";
    }
	
	
	
}
