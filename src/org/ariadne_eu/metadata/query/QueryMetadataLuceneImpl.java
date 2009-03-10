package org.ariadne_eu.metadata.query;

import java.io.File;

import net.sourceforge.minor.lucene.core.searcher.IndexSearchDelegate;
import net.sourceforge.minor.lucene.core.searcher.ReaderManagement;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.ariadne_eu.metadata.query.language.QueryTranslationException;
import org.ariadne_eu.metadata.query.language.TranslateLanguage;
import org.ariadne_eu.metadata.resultsformat.ResultDelegateLomImpl;
import org.ariadne_eu.metadata.resultsformat.ResultDelegateMACEEnrichedLomImpl;
import org.ariadne_eu.metadata.resultsformat.ResultDelegatePlrfImpl;
import org.ariadne_eu.metadata.resultsformat.ResultDelegateRLomImpl;
import org.ariadne_eu.metadata.resultsformat.ResultDelegateSolrImpl;
import org.ariadne_eu.metadata.resultsformat.TranslateResultsformat;
import org.ariadne_eu.utils.config.ConfigManager;
import org.ariadne_eu.utils.config.RepositoryConstants;
import org.ariadne_eu.utils.lucene.analysis.DocumentAnalyzer;
import org.ariadne_eu.utils.lucene.analysis.DocumentAnalyzerFactory;
import org.ariadne_eu.utils.lucene.query.SingletonIndexSearcher;

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
            String indexDirString = ConfigManager.getProperty(RepositoryConstants.MD_LUCENE_INDEXDIR + "." + getLanguage());
            if (indexDirString == null)
        	indexDirString = ConfigManager.getProperty(RepositoryConstants.MD_LUCENE_INDEXDIR);
            if (indexDirString == null)
                log.error("initialize failed: no " + RepositoryConstants.MD_LUCENE_INDEXDIR + " found");
            indexDir = new File(indexDirString);
            if (!indexDir.isDirectory())
                log.error("initialize failed: " + RepositoryConstants.MD_LUCENE_INDEXDIR + " invalid directory");
            //TODO: check for valid lucene index
        } catch (Throwable t) {
            log.error("initialize: ", t);
        }
    }
    
    public String xQuery(String xQuery) throws QueryMetadataException {
    	return null;
    }

    public String query(String query, int start, int max, int resultsFormat) throws QueryTranslationException, QueryMetadataException {
        String lQuery = TranslateLanguage.translateToQuery(query, getLanguage(), TranslateLanguage.LUCENE, start, max, resultsFormat);
        return luceneQuery(lQuery, start, max, resultsFormat);
    }
    

    public int count(String query) throws QueryTranslationException, QueryMetadataException {
        String lQuery = TranslateLanguage.translateToCount(query, getLanguage(), TranslateLanguage.LUCENE);
        return luceneCount(lQuery);
    }

    private String luceneQuery(String lQuery, int start, int max, int resultsFormat) {
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
            } else {
            	//for the VsqlToLucene Implementation, when there is no resultformat defined!!
            	result = new ResultDelegateLomImpl(start, max);
            }
            String searchResult = result.result(hits);

//            String searchResult = "";
//            Document doc;
//            for (int i = start-1; i < hits.length() && (max < 0 || i < start-1+max); i++) {
//    	    	doc = hits.doc(i);
//    	    	searchResult.concat(doc.get("lom")+"\n\n");
//    	    	
//    	    }
            

            return searchResult;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;  //To change body of created methods use File | Settings | File Templates.
        } finally {
			try {
				//GAP: too many open files solution!
//				ReaderManagement.getInstance().unRegister(indexDir, reader);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }

    private int luceneCount(String lQuery) {
        try {
        	reader = null;
        	reader = ReaderManagement.getInstance().getReader(indexDir);
            return getHits(lQuery).length();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return -1;
        }
    }

    private Hits getHits(String lQuery) {
    	
        try {
        	
			//Directory fsDir = FSDirectory.getDirectory(indexDir);
			
			//singleton to have only one instance of IndexSearcher to avoid open too many files!!
			//IndexSearcher is = new IndexSearcher(reader);
			SingletonIndexSearcher sis = SingletonIndexSearcher.getSingletonIndexSearcher(reader);

			//XXX Note that QueryParser is not thread-safe.
			DocumentAnalyzer analyzer = DocumentAnalyzerFactory.getDocumentAnalyzerImpl();
			

			org.apache.lucene.search.Query query = new QueryParser("contents",  analyzer.getAnalyzer()).parse(lQuery);//TODO "contents"

			
			Hits hits = SingletonIndexSearcher.search(query);
			
			//return is.search(query);
			return hits;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
		
		
    }

    
}
