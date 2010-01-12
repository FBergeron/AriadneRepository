package org.ariadne_eu.utils.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class CollectionDocumentAnalyzer extends DocumentAnalyzer{
	
private static PerFieldAnalyzerWrapper pfanalyzer;
	
	public CollectionDocumentAnalyzer() {
		pfanalyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());
		pfanalyzer.addAnalyzer("key", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("date.insert", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("untokenized.xmlns", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("metadatacollection.identifier.catalog", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("metadatacollection.identifier.entry", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("metadatacollection.desciption.language", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("metadatacollection.identifier.target.targetDescription.identifier.catalog", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("metadatacollection.identifier.target.targetDescription.identifier.entry", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("metadatacollection.identifier.target.targetDescription.protocolIdentifier.entry", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("metadatacollection.identifier.target.targetDescription.protocolIdentifier.entry", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("metadatacollection.identifier.target.targetDescription.location", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("protocol.identifier.catalog", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("protocol.identifier.entry", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("protocol.name", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("protocol.version", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("protocol.protocolDescriptionBindingNamespace", new KeywordAnalyzer());
		pfanalyzer.addAnalyzer("protocol.protocolDescriptionBindingLocation", new KeywordAnalyzer());
	}

	public PerFieldAnalyzerWrapper getAnalyzer() {
		return pfanalyzer;
	}

	@Override
	public TokenStream tokenStream(String arg0, Reader arg1) {
		return null;
	}

}
