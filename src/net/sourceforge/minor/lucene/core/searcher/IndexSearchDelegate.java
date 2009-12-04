package net.sourceforge.minor.lucene.core.searcher;

import org.apache.lucene.search.Hits;

public interface IndexSearchDelegate {
	
	String result(Hits hits) throws Exception;
}
