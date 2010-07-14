package org.ariadne_eu.utils.rest;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
//import java.util.regex.Pattern;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

//import IndexBuilder.Index;

public class InMemoryEngine implements SearchEnginePlugIn {
	/*private class MetadataElement {
		String[] elementNames;
		String delimiter;
		ElementHandler elementHandler;
		Pattern[] pattern;
		
		public MetadataElement(String[] elementNam){
			elementNames = elementNam;
			pattern = new Pattern[elementNam.length];
			for (int i = 0; i<elementNam.length;i++)
				pattern[i] = Pattern.compile("<"+elementNam[i]+".*?>(.*?)</"+elementNam[i]+">");
		}
	}*/

	//private static InMemoryEngine myInstance;
	private int nrOfNodes = 0;
	private int nodeSize = 0;
	private int BENCHMARK_OFFSET;
	private int[] idOffset;
	ArrayList<Integer> idOffsetList = new ArrayList<Integer>();
	private SearchNode[] nodes;
	//private String[] searchTerms; //All the search terms such as "sbj=mathematics", "lng=nl", etc	
	private String[] facets;
	private String loadPath = "";
	private String[] parameters = new String[0];
	//private int fstNode = -1;
	//private int lstNode = -1;
	//private HashMap<String,MetadataElement> metadataMap = new HashMap<String,MetadataElement>();
	/**
	 * 
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException(); 
	}
	/* (non-Javadoc)
	 * @see SearchEngine#initialise()
	 */
	public String[] getParameters(){
		return parameters;
	}
	
	public void initialise(String[] arg) throws Exception {
		parameters = arg;
		nodeSize = 16384;
		BENCHMARK_OFFSET = 1000000;
		loadPath = arg[0];
		int fstNode = Integer.parseInt(arg[1].trim());
		int lstNode = Integer.parseInt(arg[2].trim());
		int benchmarkMultiplier = Integer.parseInt(arg[3].trim());
		if (benchmarkMultiplier < 1) benchmarkMultiplier = 1;
		nrOfNodes = 0;
		//nodeSize = 0;
		//searchTerms = new String[0];
		facets = null;
		nodes = new SearchNode[nrOfNodes];
		//metadataMap.put("title", new MetadataElement(new String[] {"(lom:)?general","(lom:)?title","(lom:)?string"}));
		//metadataMap.put("description", new MetadataElement(new String[] {"(lom:)?general","(lom:)?description","(lom:)?string"}));
		//metadataMap.put("keywords", new MetadataElement(new String[] {"(lom:)?general","(lom:)?keyword","(lom:)?string"}));
		//metadataMap.put("location", new MetadataElement(new String[] {"(lom:)?technical","(lom:)?location"}));
		int nrOfInputNodes = (lstNode - fstNode + 1) ;
		nrOfNodes = nrOfInputNodes*benchmarkMultiplier ;
		nodes = new SearchNode[nrOfNodes];
		int idOffset = 0;
		for (int i=0;i<nrOfInputNodes;i++){
			nodes[i] = new SearchNode(i);
			String fileNam = "part"+(new DecimalFormat("00").format(i+fstNode));
			System.out.println("Loading node: "+(i+fstNode));
			idOffsetList.add(idOffset);
			idOffset = nodes[i].loadTerms(loadPath,fileNam,idOffset);
			nodes[i].loadPointers(loadPath,fileNam);
		}
		for (int j=1; j<benchmarkMultiplier ;j++){
			for (int i=0;i<nrOfInputNodes;i++){
				System.out.println("Copy into node: "+(i+(lstNode + 1)*j));
				nodes[i+(lstNode + 1)*j].copyFrom(nodes[i],j*BENCHMARK_OFFSET);
			}
		}
	}
	/**
	 * 
	 * @param dir
	 * @param firstNode
	 * @param lastNode
	 * @throws Exception
	 */
	/*public void load(String dir,int firstNode,int lastNode) throws Exception{
		
		if (loadPath != dir ||fstNode!=firstNode ||lstNode!=lastNode){
			//nrOfNodes = lastNode - firstNode + 1;
			nrOfNodes = lastNode+1;
			nodes = new SearchNode[nrOfNodes];
			for (int i=0;i<nodes.length;i++){
				nodes[i] = new SearchNode(i);
			}
			for (int i=firstNode;i<=lastNode;i++){
				String NodeNr = new DecimalFormat("00").format(i);
				System.out.println("Loading node: "+i);
				nodes[i].loadTerms(dir);
				nodes[i].loadPointers(dir);
			}			
		}
	}*/
	/**
	 * 
	 * @param dir
	 * @param firstNode
	 * @param lastNode
	 * @throws Exception
	 */
	/*public void loadBenchmark(String dir,int firstNode,int lastNode, int benchmarkMultiplier ) throws Exception{
		//nrOfNodes = (lastNode - firstNode + 1)*3;
		nrOfNodes = (lastNode + 1)*benchmarkMultiplier ;
		nodes = new SearchNode[nrOfNodes];
		for (int i=0;i<nodes.length;i++){
			nodes[i] = new SearchNode(i);
		}
		for (int i=firstNode;i<=lastNode;i++){
			//String NodeNr = new DecimalFormat("00").format(firstNode+i);
			System.out.println("Loading node: "+i);
			nodes[i].loadTerms(dir);
			nodes[i].loadPointers(dir);
			for (int j=1; j<benchmarkMultiplier ;j++){
				System.out.println("Copy into node: "+(i+(lastNode + 1)*j));
				nodes[i+(lastNode + 1)*j].copyFrom(nodes[i],j*BENCHMARK_OFFSET);
			}
		}
	}*/
	/**
	 * 
	 * @param facets
	 */
	public void setFacets(String [] facets){
		this.facets = facets;
	}
	
	/**
	 * @param request is an HttpServletRequest
	 * @returns response is a HttpServletResponse
	 */
	public void search(HttpServletRequest request, HttpServletResponse response) {
		long timeInMillis = 0;
		long startTimeInMillis = System.currentTimeMillis();
		String js = request.getParameter("json");
		Query qry = new Query();
		QueryResult qryResult = new QueryResult(qry);
    	try {
			if (js!=null) {
				JSONObject jo = new JSONObject(js);
				qry.parseJson(jo);
			} else {
				qry.parseURL(request);
			}
			//Search 
			qryResult = search(qry);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
    		ex.printStackTrace ();
    	}
		String result = qryResult.toJson();
		timeInMillis = System.currentTimeMillis() - startTimeInMillis;
		//float timeInSeconds =  (new Float(timeInMillis).floatValue())/1000;
		if (result.endsWith("}}")){
			result = result.substring(0, result.length() - 2) + ",\"processingTime\":\"" + timeInMillis + "\"}}";			
		} else {
			result = "{\"result\":{\"processingTime\":"+timeInMillis+"}}";
		}
		PrintWriter out = null;
    	try {
	    	response.setContentType("text/plain; charset=UTF-8");
		    out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF8"), true);
		    out.println(result);
			out.close();
    	} catch (Exception ex) {
			System.out.println(ex.getMessage());
    		ex.printStackTrace ();
    	}
		if (out!=null) out.close();	
	}

	/* (non-Javadoc)
	 * @see SearchEngine#search(Query)
	 */
	public QueryResult search(Query qry) {
		//System.out.println(qry);
		qry.searchEngine = this;
		if (qry.facets == null) qry.facets = facets;
		int dimRes = 0;
		int totalNrOfResults = 0;
		int idsTo = qry.idListOffset+qry.idListSize;
		int[] temp = new int[nodes.length*idsTo];
		HashMap<String,Integer> facetsMap = new HashMap<String,Integer>();
		for (int i=0;i<nodes.length;i++){
			//System.out.println("Searching node: "+i);
			QueryResult res = nodes[i].search(qry);
			//System.out.println("NrOfResults for node "+i+" = "+res.nrOfResults);
			int nrOfResults = res.nrOfResults;
			//TODO We need to merge results differently, using a heap for the ranking
			if (nrOfResults>idsTo) nrOfResults=idsTo;
			for (int j=0;j<nrOfResults;j++) temp[dimRes++]=res.intIDs[j];
			totalNrOfResults = totalNrOfResults + res.nrOfResults;
			for (int j=0;j<res.facetCounts.length;j++){
				if ((res.facetCounts[j]>0)){
					String fk = res.facetKeys[j];
					if (!facetsMap.containsKey(fk)){
						facetsMap.put(fk, res.facetCounts[j]);
					} else {
						facetsMap.put(fk, facetsMap.get(fk)+res.facetCounts[j]);
					}
				}
			}
		}
		
		QueryResult result = new QueryResult(qry);
		
		result.nrOfResults = totalNrOfResults;
		if (dimRes>idsTo) dimRes=idsTo;
		for (int i=qry.idListOffset;i<dimRes;i++) result.intIDs[i - qry.idListOffset]=temp[i];
		result.nrOfIDs = dimRes - qry.idListOffset;
		String[] facetKeys = new String[facetsMap.size()];
		facetsMap.keySet().toArray(facetKeys);
		Arrays.sort(facetKeys);
		result.facetKeys = facetKeys;
		result.facetCounts = new int[facetKeys.length];
		for (int i=0;i<facetKeys.length;i++){
			result.facetCounts[i] = facetsMap.get(result.facetKeys[i]);
		}
		return result;
	}
	/**
	 * 
	 * @param nr
	 * @return
	 */
	public SearchNode getNode(int nr) {
		return nodes[nr];
	}
	
/*	public LOMResult[] getLOMResults(int[] ids)  throws Exception {
		LOMResult[] r = new LOMResult[ids.length];
		for (int i=0;i<ids.length;i++) r[i] = getLOMResult(ids[i]);
		return r;
	}
*/	
	public String getJsonResult(int id)  throws Exception {
		//LOMResult r = new LOMResult();
		int properID = id % BENCHMARK_OFFSET;
		//TODO Change to a variable offset using idOffset
		int node = (properID - 1) / nodeSize;
		int nr = properID - (node * nodeSize);
		return nodes[node].getJsonResult(nr);
		//return nodes[node].getLOMResult(nr);
	}
/*	public LOMResult getLOMResult(String dir,int id) {
		FileInputStream fis = null;
		ZipInputStream zis = null;
		//ZipFile zif = null;
	    LOMResult r = new LOMResult();
		int node = (id - 1) / nodeSize;
		int nr = id - (node * nodeSize);
		String nodeStr = new DecimalFormat("00").format(node);
		String inFile = "part"+nodeStr+".zip";
		try {
			fis = new FileInputStream(dir+inFile);
		    //zif = new ZipFile(dir+inFile);
		    zis = new ZipInputStream(new BufferedInputStream(fis));
		    //ZipEntry entry = null;
		    String content="";
		    for (int i=1;i<nr;i++)zis.getNextEntry();
	    	if ((zis.getNextEntry()) != null) {
	    		Scanner s = new Scanner(zis);
				s.nextLine(); 
				while (s.hasNext()){
					content = content+" "+s.nextLine().trim(); 
				}
				r.title = extractContent(metadataMap.get("title"),content,0);
				r.description = extractContent(metadataMap.get("description"),content,0);
				r.keywords = extractContent(metadataMap.get("keywords"),content,0);
				r.location = extractContent(metadataMap.get("location"),content,0);
			}
	    	
		} catch (Exception ex) {
	    	ex.printStackTrace();
	    }
		
		return r;
	}
*/	

	/*	private String extractContent(MetadataElement el, String content, int elementPos){
		//System.out.println(content);
		//System.out.println("Looking for: "+el.elementNames[elementPos]);
		String res = "";
		Matcher matcher = (el.pattern[elementPos]).matcher(content);
        while (matcher.find()) {
        	if (elementPos<el.pattern.length-1){
				return extractContent(el, matcher.group(2),elementPos+1);
			} else {    
				return matcher.group(2);
			}
        }
        return res;
	}
*/
}
