package org.ariadne_eu.metadata.insert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import net.sourceforge.minor.lucene.core.indexer.IndexInserterDelegate;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.ariadne_eu.utils.lucene.indexer.MACEEnrichment;
import org.eun.lucene.core.indexer.document.DocumentHandler;
import org.eun.lucene.core.indexer.document.DocumentHandlerException;
import org.eun.lucene.core.indexer.document.HandlerFactory;

public class InsertDelegateSingleStringImpl implements IndexInserterDelegate {
	private String metadata;
	private String key;

	public InsertDelegateSingleStringImpl(String _key, String _metadata){
		this.metadata = _metadata;
		this.key = _key;
	}
	
	public void insert(IndexWriter writer) throws IOException {
		
		DocumentHandler handler = HandlerFactory.getDocumentHandlerImpl();		
		Document doc=null;
		try {
			
			String insertMetadata = metadata;
            if (metadata.startsWith("<?")) {
                insertMetadata = metadata.substring(metadata.indexOf("?>")+2);
            }
            
            String exml = MACEEnrichment.enrichWClassification(insertMetadata);
			
			doc = handler.getDocument(new ByteArrayInputStream(metadata.getBytes("UTF-8")));
			
			doc.add(new Field("key", key, Field.Store.YES, Field.Index.UN_TOKENIZED ));
			doc.add(new Field("date.insert", DateTools.dateToString(new Date(), DateTools.Resolution.MILLISECOND), Field.Store.YES, Field.Index.UN_TOKENIZED));
            doc.add(new Field("lom", insertMetadata, Field.Store.YES, Field.Index.UN_TOKENIZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
            doc.add(new Field("maceenrichedlom", exml, Field.Store.YES, Field.Index.UN_TOKENIZED, Field.TermVector.WITH_POSITIONS_OFFSETS));

//            Term term = new Term("key", key);
//            writer.deleteDocuments(term);
			writer.addDocument(doc);
		} catch (DocumentHandlerException e) {
			e.printStackTrace();
		}
	}

}
