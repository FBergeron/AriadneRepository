<%@page import="java.util.Properties" %>
<%@page import="java.io.File" %>
<%@page import="java.io.FileOutputStream" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<% 
	Properties harvester = new Properties();
	harvester.setProperty(request.getParameter("target_id")+".active","");
	harvester.setProperty(request.getParameter("target_id")+".latestHarvestedDatestamp","");
	harvester.setProperty(request.getParameter("target_id")+".validationUri","");
	harvester.setProperty(request.getParameter("target_id")+".autoReset","");
	harvester.setProperty(request.getParameter("target_id")+".granularity",request.getParameter("granularity"));
	harvester.setProperty(request.getParameter("target_id")+".providerName","");
	harvester.setProperty(request.getParameter("target_id")+".metadataPrefix",request.getParameter("metadata_prefix"));
	harvester.setProperty(request.getParameter("target_id")+".baseURL",request.getParameter("location"));
	String sets="";
	for (int i=0;i<Integer.parseInt(request.getParameter("numberSets"));i++){
		if (request.getParameter("set"+i)!=null) sets += request.getParameter("set"+i)+";";		
	}
	harvester.setProperty(request.getParameter("target_id")+".harvestingSet",sets);
	harvester.setProperty(request.getParameter("target_id")+".metadataFormat","");
	harvester.setProperty(request.getParameter("target_id")+".repositoryIdentifier",request.getParameter("target_id"));
	harvester.setProperty(request.getParameter("target_id")+".repositoryName","");
	harvester.setProperty(request.getParameter("target_id")+".statusLastHarvest","");
	harvester.setProperty(request.getParameter("target_id")+".registryIdentifier.entry",request.getParameter("registry_entry"));
	harvester.setProperty(request.getParameter("target_id")+".registryIdentifier.catalog",request.getParameter("registry_catalog"));
	harvester.setProperty(request.getParameter("target_id")+".registryTarget","true");
	
	File file = new File(application.getRealPath("registryMgmt/"+"harvester.properties"));                           
	file.createNewFile();
	harvester.store(new FileOutputStream(file), "");  
	String query = request.getParameter("query");
	pageContext.forward("index.jsp?"+query);
	
%>
</body>
</html>

<%/*openlearn_open_ac_uk.active = Yes
openlearn_open_ac_uk.latestHarvestedDatestamp = 2010-01-19T11:38:40Z*/
//openlearn_open_ac_uk.validationUri = http://ltsc.ieee.org/xsd/LOM/loose
//openlearn_open_ac_uk.autoReset = true
//openlearn_open_ac_uk.granularity = YYYY-MM-DDThh:mm:ssZ
//openlearn_open_ac_uk.providerName =
//openlearn_open_ac_uk.metadataPrefix = oai_lre
//openlearn_open_ac_uk.baseURL = http://openlearn.open.ac.uk/local/oai/oai2.php
/*openlearn_open_ac_uk.harvestingSet =
openlearn_open_ac_uk.metadataFormat = ILOX
openlearn_open_ac_uk.repositoryIdentifier = openlearn.open.ac.uk
openlearn_open_ac_uk.repositoryName = OpenLearn LearningSpace
openlearn_open_ac_uk.statusLastHarvest = 0
openlearn_open_ac_uk.registryIdentifier.entry =
openlearn_open_ac_uk.registryIdentifier.catalog =
openlearn_open_ac_uk.registryTarget = false */ %>