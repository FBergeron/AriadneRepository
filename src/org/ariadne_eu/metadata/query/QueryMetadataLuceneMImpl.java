package org.ariadne_eu.metadata.query;

import java.io.File;

import net.sourceforge.minor.lucene.core.searcher.IndexSearchDelegate;
import net.sourceforge.minor.lucene.core.searcher.MemoryReaderManagement;
import net.sourceforge.minor.lucene.core.searcher.ReaderManagement;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.ariadne.config.PropertiesManager;
import org.ariadne_eu.metadata.query.language.QueryTranslationException;
import org.ariadne_eu.metadata.query.language.TranslateLanguage;
import org.ariadne_eu.metadata.resultsformat.ResultDelegateARIADNERFJS;
import org.ariadne_eu.metadata.resultsformat.ResultDelegateICOPERCompactJS;
import org.ariadne_eu.metadata.resultsformat.ResultDelegateICOPERJS;
import org.ariadne_eu.metadata.resultsformat.ResultDelegateICOPERLODCompactJS;
import org.ariadne_eu.metadata.resultsformat.ResultDelegateLomImpl;
import org.ariadne_eu.metadata.resultsformat.ResultDelegateMACEEnrichedLomImpl;
import org.ariadne_eu.metadata.resultsformat.ResultDelegatePlrfImpl;
import org.ariadne_eu.metadata.resultsformat.ResultDelegateRLomImpl;
import org.ariadne_eu.metadata.resultsformat.ResultDelegateSolrImpl;
import org.ariadne_eu.metadata.resultsformat.TranslateResultsformat;
import org.ariadne_eu.metadata.resultsformat.researchfm.ResultDelegateAccounts;
import org.ariadne_eu.metadata.resultsformat.researchfm.ResultDelegateLatestPublication;
import org.ariadne_eu.metadata.resultsformat.researchfm.ResultDelegatePeople;
import org.ariadne_eu.metadata.resultsformat.researchfm.ResultDelegatePublications;
import org.ariadne_eu.utils.config.RepositoryConstants;
import org.ariadne_eu.utils.lucene.analysis.DocumentAnalyzer;
import org.ariadne_eu.utils.lucene.analysis.DocumentAnalyzerFactory;


/**
 * Created by ben
 * Date: 25-aug-2007
 * Time: 12:57:23
 * To change this template use File | Settings | File Templates.
 */
public class QueryMetadataLuceneMImpl extends QueryMetadataImpl {

    private static Logger log = Logger.getLogger(QueryMetadataLuceneMImpl.class);
    private File indexDir;
    private IndexSearcher searcher;


    void initialize() {
        super.initialize();
    }
    
    public synchronized String xQuery(String xQuery) throws QueryMetadataException {
    	throw new QueryMetadataException(new Exception("Not supported query language"));
    }

    public synchronized String query(String query, int start, int max, int resultsFormat) throws QueryTranslationException, QueryMetadataException {
        String lQuery = TranslateLanguage.translateToQuery(query, getLanguage(), TranslateLanguage.LUCENE, start, max, resultsFormat);
        return luceneQuery(lQuery, start, max, resultsFormat);
    }
    

    public synchronized int count(String query) throws QueryTranslationException, QueryMetadataException {
        String lQuery = TranslateLanguage.translateToCount(query, getLanguage(), TranslateLanguage.LUCENE);
        return luceneCount(lQuery);
    }

    private synchronized String luceneQuery(String lQuery, int start, int max, int resultsFormat) {
        try {
        	
        	Hits hits = getHits(lQuery);


            IndexSearchDelegate result = null;
        	
            if (resultsFormat == TranslateResultsformat.LOM) {
            	result = new ResultDelegateLomImpl(start, max);
            }else if (resultsFormat == TranslateResultsformat.RLOM) {
            	result = new ResultDelegateRLomImpl(start, max);
            } else if (resultsFormat == TranslateResultsformat.PLRF0 ||
                       resultsFormat == TranslateResultsformat.PLRF1 ||
                       resultsFormat == TranslateResultsformat.PLRF2 ||
                       resultsFormat == TranslateResultsformat.PLRF3) {
                result = new ResultDelegatePlrfImpl(start, max);
            } else if (resultsFormat == TranslateResultsformat.SOLR) {
            	result = new ResultDelegateSolrImpl(start,max,lQuery);
            } else if (resultsFormat == TranslateResultsformat.MELOM) {
            	result = new ResultDelegateMACEEnrichedLomImpl(start,max);
            } else if (resultsFormat == TranslateResultsformat.ATOM_LOM) {
            	result = new ResultDelegateLomImpl(start, max);
            } else if (resultsFormat == TranslateResultsformat.ICJS) {
            	result = new ResultDelegateICOPERCompactJS(start,max);
            } else if (resultsFormat == TranslateResultsformat.ILCJS) {
            	result = new ResultDelegateICOPERLODCompactJS(start,max);
            } else if (resultsFormat == TranslateResultsformat.IJS) {
            	result = new ResultDelegateICOPERJS(start,max);
            } else if (resultsFormat == TranslateResultsformat.ARFJS) {
            	result = new ResultDelegateARIADNERFJS(start,max,lQuery);
            } else if (resultsFormat == TranslateResultsformat.PRFM) {
            	result = new ResultDelegatePeople(start,max);
            } else if (resultsFormat == TranslateResultsformat.PARFM) {
            	result = new ResultDelegateAccounts(start,max);
            } else if (resultsFormat == TranslateResultsformat.PPRFM) {
            	result = new ResultDelegatePublications(start,max);
            } else if (resultsFormat == TranslateResultsformat.PLPRFM) {
            	result = new ResultDelegateLatestPublication(start,max);
            } else {
            	//for the VsqlToLucene Implementation, when there is no resultformat defined!!
            	result = new ResultDelegateLomImpl(start, max);
            }
            String searchResult = result.result(hits);

            return searchResult;
        } catch (Exception e) {
        	log.error("Lucene query exception",e);
            return null;
        } 
    }

    private synchronized int luceneCount(String lQuery) {
        try {
        	searcher = MemoryReaderManagement.getInstance().getSearcher();
            return getHits(lQuery).length();
        } catch (Exception e) {
        	log.error("Lucene query exception",e);
            return -1;
        } 
    }

    private synchronized Hits getHits(String lQuery) {
    	
        try {
        	log.info("In Memory solution!");
        	searcher = MemoryReaderManagement.getInstance().getSearcher();

			//XXX Note that QueryParser is not thread-safe.
			DocumentAnalyzer analyzer = DocumentAnalyzerFactory.getDocumentAnalyzerImpl();
			org.apache.lucene.search.Query query = new QueryParser("contents",  analyzer.getAnalyzer()).parse(lQuery);
			
			return searcher.search(query);
		} catch (ParseException e) {
			log.error("Lucene parse exception",e);
		} catch (Exception e) {
			log.error("Lucene query exception",e);
		} 
		return null;
    }

    
}
