package org.eun.lucene.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.minor.lucene.core.service.IndexService;

import org.eun.lucene.core.indexer.impl.InsertDelegateBatchImpl;
import org.eun.lucene.service.SearchResultFactory;

public class Main {
	
	private static Map<String, File> mFiles;
	
	public static void main(String[] args) throws Exception{
		if (args.length != 2) {
			throw new Exception("Usage: java  <index dir> <data dir>");
	    }
		File indexDir = new File(args[0]);
		
		if (args[1].equals("searcher")) {
			BufferedReader bR = new BufferedReader(new InputStreamReader(new FileInputStream("build/conf/query.txt"), "UTF-8"));
		    String query = bR.readLine();
		    
			System.out.println(IndexService.search(indexDir, query, SearchResultFactory.createResultFormat("verb=\"GetRecord\"")));
			
		} else {
		    File dataDir = new File(args[1]);
		    
		    createMap(dataDir);
			try {
				IndexService.insert(indexDir, new InsertDelegateBatchImpl(mFiles), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	    

	}
	
	private static void createMap(File dataDir) {
		mFiles = new HashMap<String, File>();
		try {
			indexDirectory(dataDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void indexDirectory(File dir) throws IOException {
		
		File[] files = dir.listFiles();
	
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				indexDirectory(f);  // recurse
			} else if (f.getName().endsWith(".xml")) {
				mFiles.put(f.toString(), f);
			}
		}
	}
	
	
	
	

}