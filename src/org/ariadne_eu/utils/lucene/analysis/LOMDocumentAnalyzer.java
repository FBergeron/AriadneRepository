/**
 * 
 */
package org.ariadne_eu.utils.lucene.analysis;

import java.io.Reader;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * @author gonzalo
 *
 */
public class LOMDocumentAnalyzer extends DocumentAnalyzer{
	
	private static PerFieldAnalyzerWrapper pfanalyzer;
	
	public LOMDocumentAnalyzer() {
		pfanalyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());
		pfanalyzer.addAnalyzer("key", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("contents", new SnowballAnalyzer("English"));
		pfanalyzer.addAnalyzer("date.insert", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("untokenized.xmlns", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("lom.technical.format", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("lom.classification.purpose.value.exact", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("lom.classification.taxonpath.taxon.entry.string.exact", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("lom.general.title.string.exact", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("lom.general.title.string", new SnowballAnalyzer("English"));
		pfanalyzer.addAnalyzer("lom.general.keyword.string", new SnowballAnalyzer("English"));
		pfanalyzer.addAnalyzer("lom.general.language", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("lom.rights.description.string.language", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("lom.rights.description.string", new SnowballAnalyzer("English"));
		pfanalyzer.addAnalyzer("lom.general.identifier.entry.exact", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("lom.general.identifier.entry", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("lom.general.identifier.catalog", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("lom.rights.cost.value", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("lom.rights.copyrightandotherrestrictions.value", new KeywordAnalyzer());
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
