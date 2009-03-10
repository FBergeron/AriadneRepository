package org.eun.lucene.core.searcher.impl;


import net.sourceforge.minor.lucene.core.searcher.IndexSearchDelegate;

import org.apache.lucene.search.Hits;

public class ResultDelegatePLQL_0_LomImpl implements IndexSearchDelegate {

	public String result(Hits hits) throws Exception {
	    return String.valueOf(hits.length());
	}

}
