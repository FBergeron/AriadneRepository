<%@page import="java.util.Properties" %>
<%@page import="java.io.File" %>
<%@page import="java.io.FileOutputStream" %>
<%!

boolean exists (String dir) {
	File testfile = new File(dir);
    if (testfile.exists() && testfile.isDirectory()) {
        return true;
    }
    else{
    	return false;
    }
}


%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="org.ariadne.config.PropertiesManager,org.ariadne_eu.utils.config.servlets.Log4jInit,org.ariadne_eu.utils.config.ConfigManager"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Ariadne OAI Harvester - Web Installer</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />

<link rel="stylesheet" href="css/install.css" type="text/css" />
<script type="text/javascript">
<!--


//-->
</script>
<%
      pageContext.include("/layout/headLinks.jsp");
%>
  </head>
  <body>
<%
    pageContext.include("/layout/header.jsp");
%>

<div id="ctr" align="center">
<div class="install">
<div id="stepbar">
<div class="step-off">Pre-installation</div>
<div class="step-off">Store selection</div>
<div class="step-off">Connection</div>
<div class="step-off">Logging</div>
<div class="step-off">Options</div>
<div class="step-on">Finish</div>

</div>

<div id="right">

<div id="step">Finish</div>

<div class="far-right">

</div>
<div class="clr"></div>

<%
//(new Log4jInit()).reloadLogging();

/*if(error.equals("")){
	out.println("<h1>Installation Successfull</h1>");
	out.println("The configuration files have been created and the configuration details have been saved.<br>");
	out.println("Please go to <a href=../configuration/testConfiguration.jsp>Test Configuration</a> to fully test the current configuration.");	
	out.println("<div class=\"center\"><br/><br/><br/><br/><br/><br/><br/>");
	out.println("<input name=\"Button2\" type=\"submit\" class=\"button\" value=\"Home\" onclick=\"window.location='../start/index.jsp'\"/></div>");
}
else{
	out.println("<h1>Installation Failed</h1>");
	out.println(error);
}*/
	
	Properties ariadne = new Properties();
	if (request.getParameter("cntstore").contains("fileSystem")){
		PropertiesManager.saveProperty("mdstore.insert.implementation","org.ariadne_eu.metadata.insert.InsertMetadataFSImpl");
		PropertiesManager.saveProperty("mdstore.delete.implementation","org.ariadne_eu.metadata.delete.DeleteMetadataFSImpl");
		PropertiesManager.saveProperty("cntstore.insert.implementation","org.ariadne_eu.content.insert.InsertContentFSImpl");
		PropertiesManager.saveProperty("cntstore.retrieve.implementation","org.ariadne_eu.content.retrieve.RetrieveContentFSImpl");
		PropertiesManager.saveProperty("search.lucene.reindex","org.ariadne_eu.utils.lucene.reindex.ReIndexFSImpl");
	}else if (request.getParameter("cntstore").contains("db2")){
		PropertiesManager.saveProperty("mdstore.insert.implementation","org.ariadne_eu.metadata.insert.InsertMetadataIBMDB2DbImpl");
		PropertiesManager.saveProperty("mdstore.delete.implementation","org.ariadne_eu.metadata.delete.DeleteMetadataIBMDB2DbImpl");
		PropertiesManager.saveProperty("cntstore.insert.implementation","org.ariadne_eu.content.insert.InsertContentIBMDB2DbImpl");
		PropertiesManager.saveProperty("cntstore.retrieve.implementation","org.ariadne_eu.content.retrieve.RetrieveContentIBMDB2DbImpl");
		PropertiesManager.saveProperty("search.lucene.reindex","org.ariadne_eu.utils.lucene.reindex.ReIndexExistDbImpl");
	}else if (request.getParameter("cntstore").contains("existDB")){
		PropertiesManager.saveProperty("mdstore.insert.implementation","org.ariadne_eu.metadata.insert.InsertMetadataExistDbImpl");
		PropertiesManager.saveProperty("mdstore.delete.implementation","org.ariadne_eu.metadata.delete.DeleteMetadataExistDbImpl");
		PropertiesManager.saveProperty("cntstore.insert.implementation","org.ariadne_eu.content.insert.InsertContentExistDbImpl");
		PropertiesManager.saveProperty("cntstore.retrieve.implementation","org.ariadne_eu.content.retrieve.RetrieveContentExistDbImpl");
		PropertiesManager.saveProperty("search.lucene.reindex","org.ariadne_eu.utils.lucene.reindex.ReIndexIBMDB2DbImpl");
	}else if (request.getParameter("cntstore").contains("Oracle")){
		PropertiesManager.saveProperty("mdstore.insert.implementation","org.ariadne_eu.metadata.insert.InsertMetadataOracleDbImpl");
		PropertiesManager.saveProperty("mdstore.delete.implementation","org.ariadne_eu.metadata.delete.DeleteMetadataOracleDbImpl");
		PropertiesManager.saveProperty("cntstore.insert.implementation","org.ariadne_eu.content.insert.InsertContentOracleDbImpl");
		PropertiesManager.saveProperty("cntstore.retrieve.implementation","org.ariadne_eu.content.retrieve.RetrieveContentOracleDbImpl");
	}
	
	PropertiesManager.saveProperty("repository.username",request.getParameter("user"));
	PropertiesManager.saveProperty("repository.password",request.getParameter("pass"));
	PropertiesManager.saveProperty("repository.log4j.directory",request.getParameter("logSystemDir"));
	PropertiesManager.saveProperty("repository.log4j.filename",request.getParameter("nameLogs"));
	PropertiesManager.saveProperty("search.lucene.indexdir",request.getParameter("indexSystemDir"));
	PropertiesManager.saveProperty("mdstore.spifs.dir",request.getParameter("fileSystemDir"));
	PropertiesManager.saveProperty("search.lucene.handler",request.getParameter("handler"));
	PropertiesManager.saveProperty("search.lucene.analyzer",request.getParameter("analyzer"));
	PropertiesManager.saveProperty("search.lucene.reindex.maxqueryresults","50");
	PropertiesManager.saveProperty("search.xpath.query.identifier.1","metaMetadata/identifier/catalog[text()=\"oai\"]/parent::*/entry/text()");
	PropertiesManager.saveProperty("search.xpath.query.identifier.2","general/identifier/catalog[text()=\"oai\"]/parent::*/entry/text()");
	PropertiesManager.saveProperty("search.xpath.query.identifier.3","metaMetadata/identifier/entry/text()");
	PropertiesManager.saveProperty("search.xpath.query.identifier.4","general/identifier/entry/text()");
	PropertiesManager.saveProperty("search.xpath.query.identifier.5","//general/identifier/entry/text()");
	PropertiesManager.saveProperty("search.xpath.query.identifier.6","//identifier/entry/text()");
	PropertiesManager.saveProperty("search.lucene.handler.mace","/Sandbox/app/apache-tomcat-5.5.26/webapps/repository/install/MACE_LOM_Category_9_CLASSIFICATION_v5.xml");
	PropertiesManager.saveProperty("search.solr.dataDir","/Sandbox/temp/AriadneWS/repository/");
	PropertiesManager.saveProperty("search.solr.instancedir","/Sandbox/app/apache-tomcat-5.5.26/webapps/repository/solr/");
	PropertiesManager.saveProperty("search.solr.facetfield.1","lom.general.language");
	PropertiesManager.saveProperty("search.solr.facetfield.2","lom.metametadata.identifier.catalog");
	PropertiesManager.saveProperty("mdstore.insert.implementation.1","org.ariadne_eu.metadata.insert.InsertMetadataLuceneImpl");
	PropertiesManager.saveProperty("mdstore.delete.implementation.1","org.ariadne_eu.metadata.delete.DeleteMetadataLuceneImpl");
	PropertiesManager.saveProperty("mdstore.query.implementation.0","org.ariadne_eu.metadata.query.QueryMetadataLuceneImpl");
	PropertiesManager.saveProperty("mdstore.query.implementation.1","org.ariadne_eu.metadata.query.QueryMetadataLuceneImpl");
	PropertiesManager.saveProperty("mdstore.query.implementation.2","org.ariadne_eu.metadata.query.QueryMetadataLuceneImpl");
	PropertiesManager.saveProperty("mdstore.query.implementation.3","org.ariadne_eu.metadata.query.QueryMetadataLuceneImpl");
	PropertiesManager.saveProperty("mdstore.insert.xmlns.xsd","http://ltsc.ieee.org/xsd/LOM");
	PropertiesManager.saveProperty("mdstore.xquery.wholeword","false");
	PropertiesManager.saveProperty("cntstore.dr.basepath","/Sandbox/temp/AriadneWS/cntstore/");
	PropertiesManager.saveProperty("cntstore.md.xpathquery.location.1","technical/location/text()");
	PropertiesManager.saveProperty("oaicat.server.catalog.seconds2live","360");
	PropertiesManager.saveProperty("oaicat.server.catalog.granularity","YYYY-MM-DDThh:mm:ssZ");
	PropertiesManager.saveProperty("oaicat.server.catalog.maxlistsize","100");
	PropertiesManager.saveProperty("oaicat.identify.email","ariadne@cs.kuleuven.be");
	PropertiesManager.saveProperty("oaicat.identify.reponame","AriadneNext Repository");
	PropertiesManager.saveProperty("oaicat.identify.earliestdatestamp","1000-01-01T00:00:00Z");
	PropertiesManager.saveProperty("oaicat.identify.deletedrecord","no");
	PropertiesManager.saveProperty("oaicat.identify.repoid","oaicat.ariadne.org");
	PropertiesManager.saveProperty("oaicat.identify.description.1","<description><oai-identifier xmlns=\\\"http://www.openarchives.org/OAI/2.0/oai-identifier\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\" xsi:schemaLocation=\\\"http://www.openarchives.org/OAI/2.0/oai-identifier http://www.openarchives.org/OAI/2.0/oai-identifier.xsd\\\"><scheme>oai</scheme><repositoryIdentifier>oaicat.ariadne.org</repositoryIdentifier><delimiter>:</delimiter><sampleIdentifier>oai:oaicat.ariadne.org:hdl:OCLCNo/ocm00000012</sampleIdentifier></oai-identifier></description>");
	PropertiesManager.saveProperty("oaicat.crosswalk.lom","org.ariadne_eu.oai.server.lucene.crosswalk.Lucene2oai_lom");
	PropertiesManager.saveProperty("oaicat.server.catalog.class","org.ariadne_eu.oai.server.lucene.catalog.LuceneLomCatalog");
	PropertiesManager.saveProperty("oaicat.server.catalog.record.class","org.ariadne_eu.oai.server.lucene.catalog.LuceneLomRecordFactory");
	PropertiesManager.saveProperty("oaicat.server.catalog.field.md","lom");
	PropertiesManager.saveProperty("oaicat.server.catalog.field.date","date.insert");
	PropertiesManager.saveProperty("oaicat.server.catalog.field.id","key");
	PropertiesManager.saveProperty("oaicat.server.catalog.repoId","oaicat.ariadne.org");
	PropertiesManager.saveProperty("oaicat.server.catalog.field.set","collection");
	PropertiesManager.saveProperty("oaicat.server.catalog.fs.ext","");
	PropertiesManager.saveProperty("oaicat.sets.ARIADNE.repoid","ARIADNE");
	PropertiesManager.saveProperty("oaicat.handler.useoaischeme","false");
	PropertiesManager.saveProperty("OAIHandler.useOaiIdScheme","false");
	PropertiesManager.saveProperty("AbstractCatalog.oaiCatalogClassName","org.ariadne_eu.oai.server.lucene.catalog.LuceneLomCatalog");
	PropertiesManager.saveProperty("AbstractCatalog.recordFactoryClassName","org.ariadne_eu.oai.server.lucene.catalog.LuceneLomRecordFactory"); 
	PropertiesManager.saveProperty("AbstractCatalog.secondsToLive","3600"); 
	PropertiesManager.saveProperty("AbstractCatalog.granularity","YYYY-MM-DDThh:mm:ssZ"); 
	PropertiesManager.saveProperty("LuceneLomCatalog.maxListSize","100"); 
	PropertiesManager.saveProperty("LuceneLomCatalog.dateField","date.insert"); 
	PropertiesManager.saveProperty("LuceneLomCatalog.identifierField","key"); 
	PropertiesManager.saveProperty("LuceneLomRecordFactory.repositoryIdentifier","oaicat.ariadne.org"); 
	PropertiesManager.saveProperty("Lucene2oai_lom.fullLomField","lom"); 
	PropertiesManager.saveProperty("Identify.adminEmail","ariadne@cs.kuleuven.be"); 
	PropertiesManager.saveProperty("Identify.repositoryName","AriadneNext Repository"); 
	PropertiesManager.saveProperty("Identify.earliestDatestamp","1000-01-01T00:00:00Z"); 
	PropertiesManager.saveProperty("Identify.deletedRecord","no"); 
	PropertiesManager.saveProperty("Identify.repositoryIdentifier","oaicat.ariadne.org"); 
	PropertiesManager.saveProperty("Identify.description.1","<description><oai-identifier xmlns=\\\"http://www.openarchives.org/OAI/2.0/oai-identifier\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\" xsi:schemaLocation=\\\"http://www.openarchives.org/OAI/2.0/oai-identifier http://www.openarchives.org/OAI/2.0/oai-identifier.xsd\\\"><scheme>oai</scheme><repositoryIdentifier>oaicat.ariadne.org</repositoryIdentifier><delimiter>:</delimiter><sampleIdentifier>oai:oaicat.ariadne.org:hdl:OCLCNo/ocm00000012</sampleIdentifier></oai-identifier></description>"); 
	PropertiesManager.saveProperty("Crosswalks.oai_lom","org.ariadne_eu.oai.server.lucene.crosswalk.Lucene2oai_lom"); 
	PropertiesManager.saveProperty("mace.oai.aloe.target","http://mace.dfki.uni-kl.de/cgi-bin/oai_aloe.pl"); 
	PropertiesManager.saveProperty("mace.oai.aloe.mdprefix","mace_lom"); 
	PropertiesManager.saveProperty("app.baseURL","http://localhost:8080/repository/"); 
	PropertiesManager.saveProperty("app.workspace.title","Ariadne SPI workspace"); 
	PropertiesManager.saveProperty("app.collection.title","Ariadne SPI collection"); 
	PropertiesManager.saveProperty("app.metadataSchema.1","http://ltsc.ieee.org/xsd/LOM/loose"); 
	PropertiesManager.saveProperty("app.metadataSchema.2","http://www.share-tec.eu/validation/ShareTEC/minimal"); 
	PropertiesManager.saveProperty("app.publishMetadata","yes");
	if (exists(request.getParameter("logSystemDir"))&&exists(request.getParameter("indexSystemDir"))&&exists(request.getParameter("fileSystemDir"))){
		/*File file = new File(application.getRealPath("install/"+"ariadne.properties"));                           
    	file.createNewFile();
    	ariadne.store(new FileOutputStream(file), "");  */
    	ConfigManager.init();
    	Log4jInit.reloadLive();
    	
    	out.println("<h1>Installation Successfull</h1>");
	}else{		
		out.println("<h1>Installation Failled. Directories specified don't exist</h1>");
		if (!exists(request.getParameter("logSystemDir"))){
			out.println("<br>"+request.getParameter("logSystemDir")+" doesn't exist");
			
		}
		if (!exists(request.getParameter("indexSystemDir"))){
			out.println("<br>"+request.getParameter("indexSystemDir")+" doesn't exist");
			
		}
		if (!exists(request.getParameter("fileSystemDir"))){
			out.println("<br>"+request.getParameter("fileSystemDir")+" doesn't exist");
			
		}
		
	}
	%>

<div class="clr"></div>
</div>
<div class="clr"></div>
</div>
<div class="clr"></div>


<div class="ctr">
Ariadne Foundation<br />
<a href="http://www.ariadne-eu.org/" target="_blank">ARIADNE</a> is an European Association open to the World, for Knowledge Sharing and Reuse.<br> The core of the ARIADNE infrastructure is a distributed network of learning repositories.
</div>

</body>
</html>