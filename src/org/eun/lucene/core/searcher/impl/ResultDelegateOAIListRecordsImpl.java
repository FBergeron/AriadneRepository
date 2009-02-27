package org.eun.lucene.core.searcher.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.minor.lucene.core.searcher.IndexSearchDelegate;
import net.sourceforge.minor.lucene.core.utils.Formatter;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;

public class ResultDelegateOAIListRecordsImpl implements IndexSearchDelegate {
	
	private String format;
	
	public ResultDelegateOAIListRecordsImpl(String _format){
		this.format = _format;
	}
	
	public String result(Hits hits) throws Exception {
	    Document doc;
	    
	    StringBuilder sBuild = new StringBuilder();
	    sBuild.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	    		"<OAI-PMH>"+
	    "<responseDate>"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"</responseDate>"+
	    "<request "+format+"/>"+
	    "<ListRecords>");
	    
	    String identifier, sDate;
	    
	    for (int i = 0; i < hits.length(); i++) {
	    	doc = hits.doc(i);
	    	identifier = doc.get("key");
	    	sDate = doc.get("date.insert") == null ? doc.get("date.update") : doc.get("date.insert");
	    	
	    	sBuild.append("<record>"+
					    "<header>"+
					    "<identifier>"+identifier+"</identifier>"+
					    "<datestamp>"+Formatter.formatDate(sDate)+"</datestamp>"+
					    "</header>"+
					    "<metadata>");
//		    sBuild.append(doc.get("contents")+"\n\n");
		    sBuild.append(doc.get("lom")+"\n\n");
		    
		    sBuild.append("</metadata>"+
						"</record>");
	    }
	    
	    sBuild.append("</ListRecords>"+
	    			"</OAI-PMH>");
	    
    	return sBuild.toString();
	}

	
	/*
	 
<Results xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
xsi:schemaLocation="http://www.prolearn-project.org/PLQLRES/ 
http://www.cs.kuleuven.be/~stefaan/plql/plql.xsd  
http://ltsc.ieee.org/xsd/LOM 
http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd" xmlns="http://www.prolearn-project.org/PLQLRES/">
  <QuerySetting>
     <Query>http://www.prolearn-project.org/PLQLRES/1/lom/NrOfDownloads</Query>
  </QuerySetting>
  <Record position="1" rankingValue="80">
      <Metadata>
          <lom xmlns="http://ltsc.ieee.org/xsd/LOM">
              <general>
                  <identifier>
                      <entry>ARID43_12395</entry>
                      <catalog>ARIADNE</catalog>
                  </identifier>
                  <title>
                      <string language="en">The history of art theft</string>
                  </title>
                  <language>en</language>
              </general>
              <technical>
                  <location>arttheft.jpg</location>
              </technical>
          </lom>
      </Metadata>
  </Record>
</Results>
	 */
}
