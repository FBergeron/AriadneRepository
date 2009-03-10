/**
 * 
 */
package org.ariadne_eu.metadata.insert;

import java.io.ByteArrayInputStream;
import java.util.Date;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.ariadne_eu.utils.lucene.indexer.MACEEnrichment;
import org.eun.lucene.core.indexer.document.DocumentHandlerException;

import net.sourceforge.minor.lucene.core.indexer.IndexDeleterDelegate;

/**
 * @author gonzalo
 *
 */
public class DeleteDelegateSingleStringImpl implements IndexDeleterDelegate {
	
	private String key;

	public DeleteDelegateSingleStringImpl(String _key){
		this.key = _key;
	}
	
	public void delete(IndexWriter writer) throws Exception {
		Term term = new Term("key", key);
		writer.deleteDocuments(term);
	}

}
