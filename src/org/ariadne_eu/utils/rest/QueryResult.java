package org.ariadne_eu.utils.rest;

import java.util.Arrays;

public class QueryResult {
	public int[] intIDs;
	public int[] rankValues;
	public String[] facetKeys;
	public int[] facetCounts;
	public int nrOfResults = 0;
	public int nrOfIDs = 0;
	public Query query;
	
	public QueryResult(Query qry){
		query = qry;
		intIDs = new int[qry.idListOffset+qry.idListSize];
		rankValues = new int[qry.idListOffset+qry.idListSize];
	}
	
	public void initialise(SearchNode node){
		int dimfacets = 0;
		for (int i=0;i< query.facets.length;i++) 
			dimfacets = dimfacets+node.getKeysDimension(query.facets[i]);
		facetKeys = new String[dimfacets];
		facetCounts = new int[dimfacets];
		int cnt = 0;
		for (int i=0;i< query.facets.length;i++){
			String[] temp = node.getKeys(query.facets[i]);
			for (int j=0;j<temp.length;j++) 
				facetKeys[cnt++] = query.facets[i]+":"+temp[j];
		}
	}

	public String toJson(){
		int nrToShow = nrOfIDs;
		boolean errorFound = false;
		String error = "";
		String errorMessage = "";
		String jsonResult = "{\"result\":{\"nrOfResults\":"+nrOfResults+",";
		//Add ids
		String ids = "\"id\":[";
		boolean firstID = true;
		//TODO adapt numbers to show
		//nrToShow = nrOfRes;
		//if (nrToShow > maxNrOfIDs) nrToShow = maxNrOfIDs;
		//System.out.println("nrToShow="+nrToShow);
		for (int i=0; i<nrToShow;i++){
			if (firstID) firstID = false;
			else ids = ids +",";
			ids = ids + intIDs[i];
		}
		//ids = ids + "123,456";
		jsonResult = jsonResult + ids + "],";
		//Add metadata
		String metadata = "\"metadata\":[";
		//String metadata = "";
		boolean firstTitle = true;
		//TODO adapt numbers to show
		//We should not show all of them. The array of IDs can be larger than what we need to show
		for (int i=0; i<nrToShow;i++){
			if (firstTitle) firstTitle = false;
			else metadata = metadata +",";
			//LOMResult lr = eng.getLOMResult(result.intIDs[i]);
			//metadata = metadata + "{\"title\":\""+lr.title
			//+"\",\"location\":\""+lr.location+"\"}";
			try {
				metadata = metadata + query.searchEngine.getJsonResult(intIDs[i]);
			} catch (Exception ex) {
				errorFound = true;
				
			}
		}
		jsonResult = jsonResult + metadata + "],";
		//Add facets
		//{"facets":[{"field":"language",[{"value":"fr","count":1045},{"value":"fr","count":1045},{"value":"fr","count":1045}]},{"field":"lrt",[{"value":"animation","count":123},{"value":"experimentation","count":456},{"value":"web page","count":789}]}]}
		
		String facets = "\"facets\":[";
		for (int i=0;i<query.facets.length;i++){
			String curFacetField = query.facets[i];
			if (i>0) 
				facets = facets + ",";
			facets = facets + "{\"field\":\""+curFacetField+ "\",\"numbers\":[";
			boolean found = false;
			int j = -1 - Arrays.binarySearch(facetKeys, curFacetField+":");
			if (j < facetKeys.length){
				String facetField = facetKeys[j].substring(0, facetKeys[j].indexOf(":"));
				found = facetField.equals(curFacetField);	
			}
			boolean nextNumber = false;
			while (found){
				int separatorPos = facetKeys[j].indexOf(":");
				String facetField = facetKeys[j].substring(0, separatorPos);
				String facetValue = facetKeys[j].substring(separatorPos+1);
				if (found = facetField.equals(curFacetField)){
					if (nextNumber) {
						facets = facets + ",";
					} else {
						nextNumber = true;
					}
					facets = facets + "{\"val\":\""+facetValue+"\",\"count\":"+facetCounts[j]+"}";
					j++;
					found = j < facetKeys.length;
				}
			}
			facets = facets + "]}"; 
		}
		/*
		for (int j=0;j<facetCounts.length;j++){
			int separatorPos = facetKeys[j].indexOf(":");
			String facetField = facetKeys[j].substring(0, separatorPos);
			String facetValue = facetKeys[j].substring(separatorPos+1);
			if (!facetField.equals(curFacetField)){
				//A new facetField
				//if (curFacetField.length()>0) facets = facets + "]},";
				if (j>0) facets = facets + "]},";
				facets = facets + "{\"field\":\""+facetField+ "\",\"numbers\":[";
				curFacetField = facetField;
			} else {
				facets = facets + ",";
			}
			facets = facets + "{\"val\":\""+facetValue+"\",\"count\":"+facetCounts[j]+"}";
			//System.out.println(result.facetKeys[i]+"= "+result.facetCounts[i]);
		}
		if (facetCounts.length>0) facets = facets + "]}";*/
		jsonResult = jsonResult + facets + "],";
		// Add error message
		jsonResult = jsonResult + "\"error\":\""+error+"\",\"errorMessage\":\""+errorMessage+"\"";
		//Add end json string
		jsonResult = jsonResult + "}}";
		return jsonResult;
	}
}
