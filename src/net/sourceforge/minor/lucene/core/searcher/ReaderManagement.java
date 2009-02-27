package net.sourceforge.minor.lucene.core.searcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

public class ReaderManagement {
	private static ReaderManagement instance = null;
	private static Logger log = Logger.getLogger(ReaderManagement.class);
	
	private Map<File, List<ReaderContainer>> mReaders;
	
	private ReaderManagement(){
		mReaders = new HashMap<File, List<ReaderContainer>>();
	}

    public static ReaderManagement getInstance(){
        if ( instance == null )
        {
            synchronized( ReaderManagement.class )
            {
                if ( instance == null )
                {
                    instance = new ReaderManagement();
                }
            }
        }
        return instance;
    }
    
    /*
     * Get the last reader for the index in argument and register (inc a counter for that reader)
     */
    public IndexReader getReader(File indexDir) throws Exception{
    	if (!mReaders.containsKey(indexDir)){
    		setNewReader(indexDir);
    		return getReader(indexDir);
    	}
    	synchronized (mReaders) {
    		try{
    		List<ReaderContainer> lReader = mReaders.get(indexDir);
    		log.debug("lReader size : "+lReader.size()+"  nb index files: "+indexDir.listFiles().length);
    		
        	ReaderContainer readerContainer = lReader.get(lReader.size()-1);
        	readerContainer.incNbSearch();
        	return readerContainer.getReader();
    		} catch(Exception ex){
        		log.fatal("mReaders.containsKey(indexDir)" +mReaders.containsKey(indexDir)+" ERR:"+ex);
        		if (mReaders.containsKey(indexDir)){
        			List<ReaderContainer> lReader = mReaders.get(indexDir);
        			log.fatal("size reader for this index : "+lReader.size() +" index: "+indexDir.getCanonicalPath());
            	}
        		
        		return null;
        	}
		}
    }
    
    /*
     * Before changing the reader, check if the lastest reader is being used by someone, if not close that reader
     */
    public void setNewReader(File indexDir) throws IOException{
    	synchronized (mReaders) {
    		try{
        	List<ReaderContainer> lReader;
        	if (!mReaders.containsKey(indexDir)){
        		lReader = new ArrayList<ReaderContainer>();
        		mReaders.put(indexDir, lReader);
        		
        	} else {
        		lReader = mReaders.get(indexDir);
        		
        		if (lReader.size() > 0){
        			log.debug("lReader.size(): " + lReader.size());
        			ReaderContainer readerContainer = lReader.get(lReader.size()-1);
        			
                	if (readerContainer.isClosable()){
                		log.debug("close : (setNewReader)lreader size= "+lReader.size()+" reader : "+readerContainer);
                		readerContainer.close();
                		lReader.remove(readerContainer);
                	}
            	}
        	}
        	lReader.add(new ReaderContainer(IndexReader.open(FSDirectory.getDirectory(indexDir))));
    		} catch(Exception ex){
        		log.fatal("mReaders.containsKey(indexDir)" +mReaders.containsKey(indexDir)+" indexDir "+indexDir.getCanonicalPath()+" ERR:"+ex);
        		System.out.println("mReaders.containsKey(indexDir)" +mReaders.containsKey(indexDir)+" indexDir "+indexDir.getCanonicalPath()+" ERR:"+ex);
        	}
		}
    }
    
    /*
     * Unregister a reader, if it's the last one using it then close the reader except if it's the only reader for that index!
     */
    public synchronized void unRegister(File indexDir, IndexReader reader) throws Exception{
    	if (!mReaders.containsKey(indexDir)){
    		throw new Exception("Unauthorized operation");
    	}
    	synchronized (mReaders) {
	    	List<ReaderContainer> lReader = mReaders.get(indexDir);
	    	//
	    	log.debug("lReader size : "+lReader.size()+"  reader : "+reader+" index of reader : "+lReader.indexOf(new ReaderContainer(reader)));
	    	//
	    	ReaderContainer readerContainer = lReader.get(lReader.indexOf(new ReaderContainer(reader)));
	    	readerContainer.decNbSearch();
	    	if (lReader.size() > 1 && !lReader.get(lReader.size()-1).equals(reader) && readerContainer.isClosable()){
	    		log.debug("close : (unRegister) size lreader = "+lReader.size()+" reader : "+reader);
	    		readerContainer.close();
	    		lReader.remove(readerContainer);
	    		
	    	}
    	}
    	
    }
    
}