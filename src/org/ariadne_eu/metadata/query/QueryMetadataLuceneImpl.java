package org.ariadne_eu.metadata.query;

import java.io.File;

import net.sourceforge.minor.lucene.core.searcher.IndexSearchDelegate;
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
import org.ariadne_eu.utils.config.RepositoryConstants;
import org.ariadne_eu.utils.lucene.analysis.DocumentAnalyzer;
import org.ariadne_eu.utils.lucene.analysis.DocumentAnalyzerFactory;


/**
 * Created by ben
 * Date: 25-aug-2007
 * Time: 12:57:23
 * To change this template use File | Settings | File Templates.
 */
public class QueryMetadataLuceneImpl extends QueryMetadataImpl {

    private static Logger log = Logger.getLogger(QueryMetadataLuceneImpl.class);
    private File indexDir;
    private IndexReader reader;


    void initialize() {
        super.initialize();
        try {
            String indexDirString = PropertiesManager.getInstance().getProperty(RepositoryConstants.getInstance().SR_LUCENE_INDEXDIR + "." + getLanguage());
            if (indexDirString == null)
        	indexDirString = PropertiesManager.getInstance().getProperty(RepositoryConstants.getInstance().SR_LUCENE_INDEXDIR);
            if (indexDirString == null)
                log.error("initialize failed: no " + RepositoryConstants.getInstance().SR_LUCENE_INDEXDIR + " found");
            indexDir = new File(indexDirString);
            if (!indexDir.isDirectory())
                log.error("initialize failed: " + RepositoryConstants.getInstance().SR_LUCENE_INDEXDIR + " invalid directory");
            //TODO: check for valid lucene index
        } catch (Throwable t) {
            log.error("initialize: ", t);
        }
    }
    
    public synchronized String xQuery(String xQuery) throws QueryMetadataException {
    	return null;
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
        	reader = null;
        	reader = ReaderManagement.getInstance().getReader(indexDir);
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
            }else {
            	//for the VsqlToLucene Implementation, when there is no resultformat defined!!
            	result = new ResultDelegateLomImpl(start, max);
            }
            String searchResult = result.result(hits);

            return searchResult;
        } catch (Exception e) {
        	log.error("Lucene query exception",e);
            return null;
        } finally {
			try {
				ReaderManagement.getInstance().unRegister(indexDir, reader);
			} catch (Exception e) {
				log.error("Unable to un-register a lucene reader",e);
			}
		}
    }

    private synchronized int luceneCount(String lQuery) {
        try {
        	reader = null;
        	reader = ReaderManagement.getInstance().getReader(indexDir);
            return getHits(lQuery).length();
        } catch (Exception e) {
        	log.error("Lucene query exception",e);
            return -1;
        } finally {
			try {
				ReaderManagement.getInstance().unRegister(indexDir, reader);
			} catch (Exception e) {
				log.error("Unable to un-register a lucene reader",e);
			}
		}
    }

    private synchronized Hits getHits(String lQuery) {
    	
        try {
			IndexSearcher is = new IndexSearcher(reader);

			//XXX Note that QueryParser is not thread-safe.
			DocumentAnalyzer analyzer = DocumentAnalyzerFactory.getDocumentAnalyzerImpl();
			org.apache.lucene.search.Query query = new QueryParser("contents",  analyzer.getAnalyzer()).parse(lQuery);
			
			return is.search(query);
		} catch (ParseException e) {
			log.error("Lucene parse exception",e);
		} catch (Exception e) {
			log.error("Lucene query exception",e);
		} 
		return null;
    }

    
}
