/**
 * 
 */
package org.ariadne_eu.utils.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * @author gonzalo
 *
 */
public class CAMDocumentAnalyzer extends DocumentAnalyzer{
	
	private static PerFieldAnalyzerWrapper pfanalyzer;
	
	public CAMDocumentAnalyzer() {
		pfanalyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());
		pfanalyzer.addAnalyzer("key", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("date.insert", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("untokenized.xmlns", new KeywordAnalyzer());
	}

	public PerFieldAnalyzerWrapper getAnalyzer() {
		return pfanalyzer;
	}

	@Override
	public TokenStream tokenStream(String arg0, Reader arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
