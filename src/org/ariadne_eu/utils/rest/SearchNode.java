package org.ariadne_eu.utils.rest;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class SearchNode {

	private class MinimumHeap { //when sorted it gives descending results
		int keyCapacity;
		int size;
		boolean built = false;
		int[] keys; //
		int[] values; //The value on which is sorted
		
		public void initialise(int[] values){
			initialise(values.length, values);
		}
		public void initialise(int keyCapacity, int[] values){
			this.keyCapacity = keyCapacity;
			keys = new int[keyCapacity];
			this.values = values;
			size = 0;
			built = false;
		}
		/**
		 * if capacity is full then
		 *    if value[key] is greater than value[head] replace head
		 *    else skip
		 * @param key
		 * @return
		 */
		public boolean addWithinCapacity(int key){
			if (size<keyCapacity){
				return add(key);
			} else {
				build();
				if (values[key]>values[keys[0]]){
					keys[0] = key;
					return refreshTop();
				}
			}
			return true;
		}
		public boolean add(int key){
			if (size >= keyCapacity) return false;
			keys[size++] = key;
			built = false;
			return true;
		}
		/**
		 * PRECONDITION: the values are initiated
		 * @return
		 */
		public void buildUsingValues(){
			size = values.length;
			for (int i=0;i<size;i++) keys[i]=i;
			build();
		}
		/**
		 * It is assumed that values & keys are initialised
		 */
		public void build(){
			if (!built && size>0){
				for( int i = size / 2; i >= 0; i-- ){
					//Sift down
				    int child;
			        int tmp = keys[ i ];
			        int j;
			        for( j=i; 2 * j + 1 < size; j = child ) {
			            child = 2 * j + 1; //leftChild( i )
			            if( child != size - 1 && values[keys[ child ]] > ( values[keys[ child + 1 ]] ))
			                child++;
			            if( values[tmp] > values[keys[child ]] )
			            	keys[ j ] = keys[child ];
			            else
			                break;
			        }
			        keys[ j ] = tmp;
				}
				//System.out.print("MyHeap: ");
		        //for( int i=0;i<keys.length;i++)System.out.print(values[keys[i]]+" ");
		        //System.out.println();
			}
			built = true;
		}
		
		public boolean refreshTop(){
			if (size<=0) return false;
			build();
			int child;
            int j;
            int tmp=keys[0];
            for( j=0; 2 * j + 1 < size; j = child ) {
	            child = 2 * j + 1; //leftChild( j )
	            if( child != size - 1 && values[keys[ child ]] > ( values[keys[ child + 1 ]] ))
	                child++;
	            if( values[tmp] > values[keys[child ]] )
	            	keys[ j ] = keys[child ];
	            else
	                break;
	        }
	        keys[ j ] = tmp;
			return true;
		}
		
		public int poll(){
			if (size<=0) return -1;
			build();
			int result = keys[0];
			keys[0] = keys[size];
			size--;
            int child;
            int j;
            int tmp=keys[0];
            for( j=0; 2 * j + 1 < size; j = child ) {
	            child = 2 * j + 1; //leftChild( j )
	            if( child != size - 1 && values[keys[ child ]] > ( values[keys[ child + 1 ]] ))
	                child++;
	            if( values[tmp] > values[keys[child ]] )
	            	keys[ j ] = keys[child ];
	            else
	                break;
	        }
	        keys[ j ] = tmp;
			return result;
		}
		
		public int peek(){
			if (size<=0) return -1;
			build();
			return keys[0];
		}
		public int[] sort(boolean ascending){
			build();
			for( int i = size - 1; i > 0; i-- ) {
	            //Delete max
				int tmp = keys[i];
	            keys[i] = keys[0];
	            keys[0]= tmp;
		        //Sift down
	            int child;
	            int j;
	            for( j=0; 2 * j + 1 < i; j = child ) {
		            child = 2 * j + 1; //leftChild( j )
		            if( child != i - 1 && values[keys[ child ]] > ( values[keys[ child + 1 ]] ))
		                child++;
		            if( values[tmp] > values[keys[child ]] )
		            	keys[ j ] = keys[child ];
		            else
		                break;
		        }
		        keys[ j ] = tmp;
		        }
			return keys;
		}
		public int[] toArray(){
			build();
			int[] result = new int[size];
			for (int i=0;i<size;i++) result[i] = keys[i];
			return result;
		}
	}

	
	private int nodeNr;
	private int nrOfIndexes;
	private int idOffset;
	private int nrOfFiles;
	private String[] indexNames = new String[0];
	private int[] indexOffset = new int[0];
	private String[][] keys = new String[0][]; //the first dimension is the indexNr, the second the keyNr
	private int[][] valuesArray = new int[0][]; //the first dimension is the indexOffset + keyNr, the second the values
	private int[]rating = new int[0];
	private long[]cmrPointer = new long[0];
	private Reader cmrFile;
	//private InputStream cmrFile;
	//private FileInputStream fis;
	//private InputStreamReader in;
	//private BufferedReader cmrJsonFile;
	private RandomAccessFile fcData;
	
    
	public SearchNode(int nr){
		nodeNr = nr;
	}
	public void copyFrom(SearchNode from, int benchMarkOffset) {
		idOffset = from.idOffset + benchMarkOffset;
		nrOfIndexes = from.nrOfIndexes;
		indexNames = new String[from.indexNames.length];
		for (int i=0;i<indexNames.length;i++) indexNames[i] = from.indexNames[i]; 
		indexOffset = new int[from.indexOffset.length];
		for (int i=0;i<indexOffset.length;i++) indexOffset[i] = from.indexOffset[i]; 
		keys = new String[from.keys.length][];
		for (int i=0;i<keys.length;i++){
			keys[i] = new String[from.keys[i].length];
			for (int j=0;j<keys[i].length;j++)keys[i][j] = from.keys[i][j]; 
		}
		valuesArray = new int[from.valuesArray.length][];
		for (int i=0;i<valuesArray.length;i++){
			valuesArray[i] = new int[from.valuesArray[i].length];
			for (int j=0;j<valuesArray[i].length;j++)valuesArray[i][j] = from.valuesArray[i][j]; 
		}
		cmrPointer = new long[from.cmrPointer.length];
		for (int i=0;i<cmrPointer.length;i++)cmrPointer[i] = from.cmrPointer[i];
		cmrFile = from.cmrFile;
	}
	
	public QueryResult search(Query qry) {
		QueryResult result = new QueryResult(qry);
		result.initialise(this);
		final int dimConj = qry.searchTerms.length; //Number of disjunctions in the conjunction
		if (dimConj==0) return result;
		
		int indNr;
		int keyNr=0;
		
		//Initialise the facet counting
		int dimfacets = 0;
		for (int i=0;i< qry.facets.length;i++) 
			dimfacets = dimfacets+getKeysDimension(qry.facets[i]);
		int[] facetPositions = new int[dimfacets];
		int[] facetKeyNumbers = new int[dimfacets];
		int[] facetCurrentValues = new int[dimfacets]; //This is the value (i.e. ID) to which the facetPosition is pointing
		for (int i=0;i< qry.facets.length;i++){
			indNr = getIndexID(qry.facets[i]);
			if (indNr >=0){
				for (int j=indexOffset[indNr];j<indexOffset[indNr+1];j++){
					if (valuesArray[j].length>0){
						facetCurrentValues[keyNr] = valuesArray[j][0];
						facetKeyNumbers[keyNr]=j;
						keyNr++;						
					} else {
						facetCurrentValues[j] = Integer.MAX_VALUE; //Integer.MAX_VALUE is the sentinel value
					}
				}
			}
		}
		MinimumHeap facetHeap = new MinimumHeap();
		facetHeap.initialise(facetCurrentValues);
		facetHeap.buildUsingValues();
		
		//Check for empty disjunctions and find the disjunction with the smallest nr of values		
		
		int[] disjRangeTemp = new int[dimConj+1];
		// indTemp is the ArrayList holding indexNr and keyNr
		ArrayList<Integer> indTemp = new ArrayList<Integer>();
		int minNrOfValInDisj=0;
		int smallestDisjunction = 0;
		for (int i=0;i<dimConj;i++){
			int cntDim = 0;
			disjRangeTemp[i+1] = disjRangeTemp[i];
			String[] disjunction = qry.searchTerms[i];
			for (int j=0;j<disjunction.length;j++){
				//Find the indexNr by name
				String st = qry.searchTerms[i][j];
				//int separatorPos = st.indexOf("=");
				int separatorPos = st.indexOf(":");
				String indNam = st.substring(0, separatorPos);
				for (indNr=0; indNr < indexNames.length;indNr++) if (indexNames[indNr].equalsIgnoreCase(indNam))break;
				//Find the keyNr by name
				String keyNam = st.substring(separatorPos+1);
				keyNr = -1;
				if (keys.length > 0)
					keyNr = Arrays.binarySearch(keys[indNr],keyNam);
				if (keyNr >= 0){
					keyNr = keyNr + indexOffset[indNr];
					if (valuesArray[keyNr].length > 0){
						disjRangeTemp[i+1]++;
						indTemp.add(keyNr);
						cntDim = cntDim + valuesArray[keyNr].length;
					}
				}
			}
			if (cntDim==0) return result;//This disjunction has no elements
			if (i==0) minNrOfValInDisj = cntDim;
			else if (cntDim<minNrOfValInDisj){
				minNrOfValInDisj = cntDim;
				smallestDisjunction = i;
			}
		}
		//Set the disjunction with the least terms in front
		// fill the disjunction ranges and
		// select the values from cache into qryValues
		int nrOfTerms = disjRangeTemp[dimConj];
		int[][] qryValues = new int[nrOfTerms][];
		int[] disjRange = new int[dimConj+1];
		int k=0;
		for (int j=disjRangeTemp[smallestDisjunction];j<disjRangeTemp[smallestDisjunction+1];j++){
			qryValues[k] = valuesArray[indTemp.get(j)];
			k++;
		}
		disjRange[1]=k;
		for (int i=0;i<smallestDisjunction;i++){
			for (int j=disjRangeTemp[i];j<disjRangeTemp[i+1];j++){
				qryValues[j+k] = valuesArray[indTemp.get(j)];
			}
			disjRange[i+2]=disjRangeTemp[i+1]+k;
		}
		for (int i=smallestDisjunction+1;i<dimConj;i++){
			for (int j=disjRangeTemp[i];j<disjRangeTemp[i+1];j++){
				qryValues[j] = valuesArray[indTemp.get(j)];
			}
			disjRange[i+1]=disjRangeTemp[i+1];
		}
		/*for (int i=0;i<dimConj;i++){
			System.out.println("==>"+i);
			for (int j=disjRange[i];j<disjRange[i+1];j++){
				System.out.print(j+": ");
				for (k=0;k<qryValues[j].length;k++) System.out.print(" "+qryValues[j][k]);
				System.out.println();
			}
			System.out.println();
		}*/
		int[] pos = new int[nrOfTerms];//position in the qryValues
		//int cnt = 0;
		boolean found = true;
		//TODO verify why found is used; it is never set to false
		//int candidate = qryValues[0][0];
		int candidate = -1;
		while (found){
			//Find a common value in all disjunctions
			int i=0; //the ith conjunction element
			while (i<dimConj) {
				//find the minimum of the i-th disjunction (i.e. the i-th element in the conjunction)
				int min = -1;
				for (int j=disjRange[i]; j<disjRange[i+1];){
					while((pos[j]<qryValues[j].length) && (qryValues[j][pos[j]]<candidate))pos[j]++;
					if (pos[j]<qryValues[j].length){
						if(min<0){
							min = qryValues[j][pos[j]];
						} else {
							if (qryValues[j][pos[j]]<min) min = qryValues[j][pos[j]];
						}
						j++;
					} else {
						//Here we are at the end of a list
						if (disjRange[i+1]==disjRange[i]+1) return result;//There is a disjunction with only one search term and no values
						for (k=i+1; k<=dimConj; k++)disjRange[k]--;
						for (k=j+1;k<nrOfTerms;k++){
							pos[k-1]=pos[k];
							qryValues[k-1] = qryValues[k];
						}
						nrOfTerms--;
					}
				}
				if (min > candidate){
					candidate = min;
					if(i==0) i = 1; else i=0; //if i=0 there is no need to visit it again; we can move to the next
				} else {
					i++;
				}
			}
			//All conjunctions have been processed
			if (found){
				//System.out.println(candidate);
				//TODO 
				// Check whether candidate is not in the exclusion list,
				// compute here the rank and store in the result heap
				
				//Add to the results list (intIDs)
				if (result.nrOfResults<result.intIDs.length) result.intIDs[result.nrOfResults]=candidate+idOffset;
				result.nrOfResults++;
				//Add to the facets
				if (qry.facets.length > 0) {
					int currentFacetKey = facetHeap.peek();
					while (facetCurrentValues[currentFacetKey]<=candidate){
						int facetPos = facetPositions[currentFacetKey];
						int facetKeyNr = facetKeyNumbers[currentFacetKey];
						//Find a value that is >= the candidate
						while ((facetPos<valuesArray[facetKeyNr].length)
								&&(valuesArray[facetKeyNr][facetPos]<candidate))facetPos++;
						//If there is a match then add 1 to the facet count	& refresh the top of the facetHeap	
						if ((facetPos<valuesArray[facetKeyNr].length)
							&& (valuesArray[facetKeyNr][facetPos]==candidate)) {
								//System.out.println(candidate +" "+currentFacetKey);
							result.facetCounts[currentFacetKey]++;
							facetPos++;	
						}
						if (facetPos<valuesArray[facetKeyNr].length){
							facetCurrentValues[currentFacetKey] = valuesArray[facetKeyNr][facetPos];
							facetPositions[currentFacetKey]=facetPos;	
						} else{
							facetCurrentValues[currentFacetKey] = Integer.MAX_VALUE;
						}
						facetHeap.refreshTop();
						
						currentFacetKey = facetHeap.peek();
					}
					//for (int f=0;f<dimfacets;f++){
					//	int facetPos = facetPositions[f];
					//	int facetKeyNr = facetKeyNumbers[f];
					//	while ((facetPos<valuesArray[facetKeyNr].length)
					//		&&(valuesArray[facetKeyNr][facetPos]<candidate))facetPos++;
					//	if((facetPos<valuesArray[facetKeyNr].length)
					//			&&(valuesArray[facetKeyNr][facetPos]==candidate)){
					//		result.facetCounts[f]++;
					//		facetPos++;
					//	}
					//	facetPositions[f]=facetPos;
					//}
				}
				candidate++;
			}
		}
		return result;
	}
	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public  int countIntersection(int[] a, int[] b)  {
		int i=0, j=0,cnt=0,aLength = a.length,bLength =b.length;
	    while (i<aLength && j<bLength) {
	        if (a[i]<= b[j]){
	        	if (a[i]== b[j]) {
	        		cnt++;	
	        	}
	        	i++;
		    }
	        else j++;
	    }  
	    return cnt;
	}

	/**
	 * PRECONDITION loadTerms must be run first in order to set nrOfFiles
	 * @param inFileNam
	 * @throws Exception
	 */
	public void loadPointers(String inDir,String fileNam) throws Exception {
		cmrPointer = new long[nrOfFiles*4+1];
		InputStream fis = new FileInputStream (inDir+"pre_point/"+fileNam+".txt"); 
		//Reader isr = new InputStreamReader (fis, "UTF-8");
		//Scanner s = new Scanner(isr);
		Scanner s = new Scanner(fis);
		//Get rid of possible garbage in the beginning. Sometimes UTF-8 has this problem
		while (!s.hasNextInt())s.nextLine(); 
		int k = 0;
		while (s.hasNextInt()){
			cmrPointer[k] = s.nextInt();
			if (s.hasNextLine()) s.nextLine();
			k++;
		}
		System.out.println(k);
		//cmrFile = new InputStreamReader (new FileInputStream (inFileNam+"pre/part"+nod+".txt"), "UTF-8"); 
	
		//fcData = new RandomAccessFile(inFileNam+"pre/part"+nod+".data", "r").getChannel();
		fcData = new RandomAccessFile(inDir+"pre/"+fileNam+".data", "r");
		//fcData.read(ByteBuffer dst, long position) 
}

	
	/**
	 * PRECONDITION: the terms must be generated by the IndexBuilder
	 * @throws Exception 
	 */
	public int loadTerms(String inDir, String fileNam, int ios) throws Exception {
		int result = ios;
		String debugStr = "Node: "+nodeNr;
		InputStream fis = new FileInputStream (inDir+"index/"+fileNam+".txt"); 
		Reader isr = new InputStreamReader (fis, "UTF-8");
		Scanner s = new Scanner(isr);
		//Get rid of possible garbage in the beginning. Sometimes UTF-8 has this problem
		while (!s.hasNextInt())s.nextLine(); 
		//Read offSet and nr of files
		//idOffset = s.nextInt();s.nextLine();
		idOffset = ios;
		nrOfFiles = s.nextInt();s.nextLine();
		rating = new int[nrOfFiles];
		//Read nr of indexes and initialise indexNames and keys
		nrOfIndexes = s.nextInt()+1;//+1 because we also make an extra key which is 'all'
		indexNames = new String[nrOfIndexes];
		indexNames[0] = "collection";
		indexOffset = new int[nrOfIndexes+1];
		indexOffset[0] = 0;
		indexOffset[1] = 1;
		keys = new String[nrOfIndexes][];
		keys[0] = new String[]{"all"};
		//Read total nr of keys and initialise valuesArray
		int totalNrOfKeys = s.nextInt()+1;
		valuesArray = new int[totalNrOfKeys][];
		try {
		//For each index read its name, the nr of keys for this index, and {key,{value}*}*
		for (int i=1; i <nrOfIndexes;i++){
			//Read the name of the index
			s.nextLine();//read the rest of the previous line first
			String indexNam = s.nextLine();
			debugStr = "Node: "+nodeNr+" index "+indexNam;
			indexNames[i] = indexNam.substring(1, indexNam.length()-1);
			//Read nr of keys in the index
			int nrOfKeys = s.nextInt();
			keys[i] = new String[nrOfKeys];
			indexOffset[i+1] = indexOffset[i]+nrOfKeys;
			for (int k=0; k <nrOfKeys;k++){
				//Read the key
				s.nextLine();//read the rest of the previous line first
				String key = s.nextLine();
				debugStr = "Node="+nodeNr+" index="+indexNam+" key="+key;
				key = key.substring(1, key.length()-1);
				//Read the number of values for the key
				keys[i][k] = key;
				int nrOfValues = s.nextInt();
				//Read all the values
				valuesArray[indexOffset[i]+k] = new int[nrOfValues];
				for (int v=0;v<nrOfValues;v++)
					valuesArray[indexOffset[i]+k][v] = s.nextInt();
			}
			
		}
		//Read the 'all' index
		int nrOfValues = s.nextInt();
		result = result + nrOfValues;
		System.out.println(nrOfValues);
		valuesArray[0] = new int[nrOfValues];
		s.nextLine();
		for (int i=0;i<nrOfValues;i++){
			valuesArray[0][i] = s.nextInt();
			rating[i] = s.nextInt();
			s.nextLine(); //Read the rest of the line
		}
		} catch (Exception e) {
			System.out.println("Exception in load terms reading "+debugStr+". Error:"+e.getMessage());
			throw e;
		}
		return result;
	}
	public void loadTerms2(String inFileNam) throws Exception {
		String debugStr = "Node: "+nodeNr;
		InputStream fis = new FileInputStream (inFileNam); 
		Reader isr = new InputStreamReader (fis, "UTF-8");
		Scanner s = new Scanner(isr);
		//Get rid of possible garbage in the beginning. Sometimes UTF-8 has this problem
		while (!s.hasNextInt())s.nextLine(); 
		//Read nr of indexes and initialise indexNames and keys
		nrOfIndexes = s.nextInt();
		indexNames = new String[nrOfIndexes];
		indexOffset = new int[nrOfIndexes+1];
		indexOffset[0] = 0;
		keys = new String[nrOfIndexes][];
		//Read total nr of keys and initialise valuesArray
		int totalNrOfKeys = s.nextInt();
		valuesArray = new int[totalNrOfKeys][];
		try {
		//For each index read its name, the nr of keys for this index, and {key,{value}*}*
		for (int i=0; i <nrOfIndexes;i++){
			//Read the name of the index
			s.nextLine();//read the rest of the previous line first
			String indexNam = s.nextLine();
			debugStr = "Node: "+nodeNr+" index "+indexNam;
			indexNames[i] = indexNam.substring(1, indexNam.length()-1);
			//Read nr of keys in the index
			int nrOfKeys = s.nextInt();
			keys[i] = new String[nrOfKeys];
			indexOffset[i+1] = indexOffset[i]+nrOfKeys;
			for (int k=0; k <nrOfKeys;k++){
				//Read the key
				s.nextLine();//read the rest of the previous line first
				String key = s.nextLine();
				debugStr = "Node="+nodeNr+" index="+indexNam+" key="+key;
				key = key.substring(1, key.length()-1);
				//Read the number of values for the key
				keys[i][k] = key;
				int nrOfValues = s.nextInt();
				//Read all the values
				valuesArray[indexOffset[i]+k] = new int[nrOfValues];
				for (int v=0;v<nrOfValues;v++)
					valuesArray[indexOffset[i]+k][v] = s.nextInt();
			}
			
		}
		} catch (Exception e) {
			System.out.println("Exception in load terms reading "+debugStr+". Error:"+e.getMessage());
			throw e;
		}
	}
		
	/**
	 * 
	 * @param indNr
	 * @param keyNr
	 * @return
	 */
	public int[] getvaluesArray(int indNr, int keyNr) {
		return valuesArray[indexOffset[indNr]+keyNr];
	}
	/**
	 * 
	 * @param index
	 * @return
	 */
	public String[] getKeys(String indexName) {
		int keyID = getIndexID(indexName);
		if (keyID==-1)return new String[0];
		return keys[keyID];
	}
	/**
	 * 
	 * @param indexName
	 * @return
	 */
	public int[] getKeyNumbers(String indexName) {
		int indID = getIndexID(indexName);
		int dim = indexOffset[indID+1] - indexOffset[indID];
		int[] result = new int[dim];
		for (int i=0;i<dim;i++)result[i]=i+indexOffset[indID];
		return result;
	}
	/**
	 * 
	 * @param indexName
	 * @return
	 */
	public int getKeysDimension(String indexName) {
		int keyID = getIndexID(indexName);
		if (keyID==-1)return 0;
		return indexOffset[keyID+1] - indexOffset[keyID];
	}
	/**
	 * 
	 * @param indexName
	 * @return
	 */
	private int getIndexID(String indexName){
		for (int i=0;i<indexNames.length;i++) {
			if (indexNames[i].equalsIgnoreCase(indexName)) return i;
		}
		return -1;
	}
	
	public String getJsonResult(int nr)  throws Exception {
		//String result = "";
		fcData.seek(cmrPointer[(nr-1)]);
		return fcData.readUTF();
		//int jsonBegin = cmrPointer[(nr-1)];
		//int jsonEnd = cmrPointer[(nr-1)+1];
		//byte[] jsonBytes = new byte[jsonEnd-jsonBegin];
		//ByteBuffer jsonBuf = ByteBuffer.wrap(jsonBytes);
		//fcData.read(jsonBuf, jsonBegin);
		//return new String(jsonBuf.array());
		//return result;
	}
}

