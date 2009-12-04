package org.eun.lucene.core.searcher.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.minor.lucene.core.searcher.IndexSearchDelegate;
import net.sourceforge.minor.lucene.core.utils.Formatter;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;

public class ResultDelegateOAIGetRecordImpl implements IndexSearchDelegate {
	
	private String format;
	
	public ResultDelegateOAIGetRecordImpl(String _format){
		this.format = _format;
	}
	
	public String result(Hits hits) throws Exception {
	    if (hits.length() != 1) throw new Exception("GetRecord returned more than 1 result");
	    
	    Document doc = hits.doc(0);
	    
	    String identifier = doc.get("key");
	    String sDate = doc.get("date.insert") == null ? doc.get("date.update") : doc.get("date.insert");
	    
	    StringBuilder sBuild = new StringBuilder();
	    sBuild.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	    		"<OAI-PMH>"+
	    "<responseDate>"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"</responseDate>"+
	    "<request "+format+" identifier=\""+identifier+"\"/>"+
	    "<GetRecord>"+
	    "<record>"+
	    "<header>"+
	    "<identifier>"+identifier+"</identifier>"+
	    "<datestamp>"+Formatter.formatDate(sDate)+"</datestamp>"+
	    "</header>"+
	    "<metadata>");
	    
	    
//	    sBuild.append(doc.get("contents")+"\n\n");
	    sBuild.append(doc.get("lom")+"\n\n");
	    
	    sBuild.append("</metadata>"+
	    			"</record>"+
	    			"</GetRecord>"+
	    			"</OAI-PMH>");
	    
    	return sBuild.toString();
	}

	
	/*
	 *
	<OAI-PMH>
<responseDate>2007-03-28T08:47:08Z</responseDate>
<request verb="GetRecord" metadataPrefix="http://fire.eun.org/xsd/strictLomResults-1.0" identifier="Td9144c3Ic1ab8c2eH1254e59R81d8ad7e"/>
<GetRecord>
<record>
<header>
<identifier>Td9144c3Ic1ab8c2eH1254e59R81d8ad7e</identifier>
<datestamp>2007-01-25T10:06:44Z</datestamp>
</header>
<metadata>
<lom xmlns:ns3="http://ltsc.ieee.org/xsd/LOM/extend" xmlns:ns2="http://ltsc.ieee.org/xsd/LOM/vocab" xmlns:ns1="http://ltsc.ieee.org/xsd/LOM/unique" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://ltsc.ieee.org/xsd/LOM" xsi:schemaLocation="http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd">
<general>
<identifier>
<catalog>eMappsRep1</catalog>
<entry>Td9144c3Ic1ab8c2eH1254e59R81d8ad7e</entry>
</identifier>
<title>
<string language="en">Church of SS. Johns in Torun</string>
<string language="pl">Koï¿½?ciÃ³ï¿½? ï¿½?ï¿½?. JanÃ³w w Toruniu</string>
</title>
<language>pl</language>
<description>
<string language="en">Church of SS. Johns in Toruï¿½?, gothic building dating back from 14th century, enlarged in 15th, former main parish church of Old Town Toruï¿½?, since 1992 cathedral of Toruï¿½? Diocese.</string>
</description>
<keyword>
<string language="en">church gothic torun</string>
</keyword>
</general>
<metaMetadata>
<identifier>
<catalog>eMapps.com</catalog>
<entry>Td9144c3Ic1ab8c2eH1254e59R81d8ad7e</entry>
</identifier>
<contribute>
<role>
<source>LOMv1.0</source>
<value>creator</value>
</role>
<entity>FN:p;LN:k;ORG:eMapps.com;*ORG:eMapps.com;</entity>
<date>
<dateTime>2007-01-10 20:55:08.68</dateTime>
</date>
</contribute>
<metadataSchema>LOMv1.0</metadataSchema>
<language>pl</language>
</metaMetadata>
<technical>
<format>image/jpeg</format>
<size>140308</size>
<location>http://resources.emapps.eun.org/eMappsRep1-Td9144c3Ic1ab8c2eH1254e59R81d8ad7e.jpg</location>
</technical>
<educational>
<typicalAgeRange>
<string language="x-t-LRE">u-u</string>
</typicalAgeRange>
</educational>
<rights>
<description>
<string language="x-t-cc">http://creativecommons.org/licences/by-nc-sa/2.5</string>
</description>
</rights>
<classification/>
</lom>
</metadata>
</record>
</GetRecord>
</OAI-PMH>
	 */
}
