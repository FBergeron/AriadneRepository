package org.eun.lucene.service;

import net.sourceforge.minor.lucene.core.searcher.IndexSearchDelegate;
import net.sourceforge.minor.lucene.core.searcher.IndexTermSearchDelegate;
import net.sourceforge.minor.lucene.core.utils.Check;

import org.eun.lucene.core.searcher.impl.ResultDelegateOAIGetRecordImpl;
import org.eun.lucene.core.searcher.impl.ResultDelegateOAIListIdentifiersImpl;
import org.eun.lucene.core.searcher.impl.ResultDelegateOAIListMetadataFormatsIdImpl;
import org.eun.lucene.core.searcher.impl.ResultDelegateOAIListMetadataFormatsImpl;
import org.eun.lucene.core.searcher.impl.ResultDelegateOAIListRecordsImpl;
import org.eun.lucene.core.searcher.impl.ResultDelegatePLQL_0_LomImpl;
import org.eun.lucene.core.searcher.impl.ResultDelegatePLQL_1_2_LomImpl;
import org.eun.lucene.core.searcher.impl.ResultDelegateStrictLomImpl;

public class SearchResultFactory {
	
	public static final String STRICT_LOM_RESULT 	= "http://fire.eun.org/xsd/strictLomResults-1.0";
    public static final String PLQLPREFIX			= "http://www.prolearn-project.org/PLRF/";
	
	public static IndexSearchDelegate createResultFormat(String resultFormat) throws ResultFormatException{
		Check.checkString(resultFormat);
		
		if (resultFormat.equals(STRICT_LOM_RESULT)){
			return new ResultDelegateStrictLomImpl();
			
		} else if (resultFormat.startsWith(PLQLPREFIX+"0/lom")){
			return new ResultDelegatePLQL_0_LomImpl();
			
		} else if (resultFormat.startsWith(PLQLPREFIX+"1/lom") || resultFormat.startsWith(PLQLPREFIX+"2/lom")){
			return new ResultDelegatePLQL_1_2_LomImpl(resultFormat);
			
		} else if (resultFormat.startsWith("verb=\"GetRecord\"") ){
			return new ResultDelegateOAIGetRecordImpl(resultFormat);
			
		} else if (resultFormat.startsWith("verb=\"ListRecords\"") ){
			return new ResultDelegateOAIListRecordsImpl(resultFormat);
			
		} else if (resultFormat.startsWith("verb=\"ListIdentifers\"") ){
			return new ResultDelegateOAIListIdentifiersImpl(resultFormat);
			
		} else if (resultFormat.startsWith("verb=\"ListMetadataFormats\"") ){
			return new ResultDelegateOAIListMetadataFormatsIdImpl(resultFormat);
			
		} else {
			throw new ResultFormatException(resultFormat+" isn't supported.");
		}
	}
	
	public static IndexTermSearchDelegate createTermResultFormat(String field, String resultFormat) throws ResultFormatException{
		if (resultFormat.equals("verb=\"ListMetadataFormats\"") ){
			return new ResultDelegateOAIListMetadataFormatsImpl(field);
		} else {
			throw new ResultFormatException(resultFormat+" isn't supported.");
		}
	}
	
}
