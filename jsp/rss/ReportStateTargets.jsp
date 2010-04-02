<?xml version="1.0" encoding="UTF-8" ?>
<%@ page contentType="text/xml; charset=UTF-8"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="uiuc.oai.*" import="java.util.StringTokenizer" import="java.util.Vector"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.net.URLEncoder"%>
<%@page import="org.ariadne.exception.IllegalArgException"%>
<%@page import="java.util.HashMap"%>
<%@page import="org.ariadne_eu.utils.registry.query.Query"%>
<%@page import="org.ariadne_eu.utils.registry.Results"%>
<%@page import="java.util.List"%>
<%@page import="org.ariadne_eu.utils.registry.MetadataCollection"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.io.FileWriter"%>
<%@page import="java.io.BufferedWriter"%>
<%@page import="java.io.File"%>
<%// Create file 
	//File rssReport = new File("report"+Calendar.getInstance().get(Calendar.YEAR)+Calendar.getInstance().get(Calendar.MONTH)+Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+".xml");
	/*if (new File(application.getRealPath("rss/"+"report"+Calendar.getInstance().get(Calendar.YEAR)+Calendar.getInstance().get(Calendar.MONTH)+Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+".xml")).exists()){
		response.sendRedirect("report"+Calendar.getInstance().get(Calendar.YEAR)+Calendar.getInstance().get(Calendar.MONTH)+Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+".xml");
	}else{
		out.println("NotExists");*/
		//SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");  
	    FileWriter fstream = new FileWriter(application.getRealPath("rss/"+"report"+Calendar.getInstance().get(Calendar.YEAR)+Calendar.getInstance().get(Calendar.MONTH)+Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+".xml"));
	    BufferedWriter rss = new BufferedWriter(fstream);
	    rss.write("<rss version=\"2.0\">");
	    rss.write("<channel>");
	    rss.write("<title>Availability of OAI-targets</title>");
	    rss.write("<link>"+request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()+"/rss/ReportStateTargets.jsp</link>");
	    rss.write("<ttl>1</ttl>");   
	    rss.write("<description>This webfeed notifies the last state of the targets in the registry.</description>");
	
	    response.setContentType("text/xml");
	    String query_string="http";
	    String axis2_url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
	    String result = Query.doQueryLucene();
	    Results results = new Results();
	    results.parseXMLResults(result);
		List<MetadataCollection> list = results.getResults();
		for (int i = 0; i < list.size(); i++)
	    {
	        MetadataCollection metadataCollection = list.get(i);
	        boolean contentOAI = false;
			for (int iterator=0;iterator<metadataCollection.getTarget().size();iterator++){
				if (metadataCollection.getTarget().get(iterator).getProtocolIdentifier().getEntry().contains("oai-pmh")){
					contentOAI = true;
					try{
						OAIRepository oairepository = new OAIRepository();
						Calendar timeInit = Calendar.getInstance();
						int minuteInit= timeInit.get(Calendar.MINUTE);
						int secondInit= timeInit.get(Calendar.SECOND);
						rss.write("<item>");					
					    rss.write("<title>"+metadataCollection.getIdentifier().getEntry()+"</title>");
					    rss.write("<link>"+axis2_url+"/search/index.jsp?query=\""+metadataCollection.getIdentifier().getEntry()+"\""+"</link>");
						rss.write("<pubDate>"+sdf.format(Calendar.getInstance().getTime())+"</pubDate>");					
						oairepository.setBaseURL(metadataCollection.getTarget().get(iterator).getLocation());
						Calendar timeEnd = Calendar.getInstance();
						int minuteEnd = timeEnd.get(Calendar.MINUTE);
						int secondEnd= timeEnd.get(Calendar.SECOND);					
					    rss.write("<description>OK - Time Response = "+((minuteEnd-minuteInit)*60)+(secondEnd-secondInit)+" seconds</description></item>");
					}catch(Exception e){					
						rss.write("<description>Exception: "+e.getMessage()+"</description></item>");					
					}	
					
				}
			}
	    
	    }
		rss.write("</channel></rss>");
		rss.close();
		response.sendRedirect("report"+Calendar.getInstance().get(Calendar.YEAR)+Calendar.getInstance().get(Calendar.MONTH)+Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+".xml");
	//}

%>
