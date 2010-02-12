<%@page import="java.util.Properties" %>
<%@page import="java.io.*" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@page import="java.net.URL"%>
<%@page import="java.net.URLConnection"%>
<%@page import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="org.ariadne.config.PropertiesManager"%>
<%@page import="java.net.Authenticator"%>
<%@page import="java.net.PasswordAuthentication"%>
<%@page import="org.ariadne.util.ClientHttpRequest"%>



<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<% 
	PropertiesManager properties = new PropertiesManager(); 
	
	File harvestersProperties = new File(application.getRealPath("registryMgmt/"+"harvesters.properties"));                           
	properties.init(harvestersProperties);
	String harvesterId = request.getParameter("id");
	final String user = properties.getProperty(harvesterId+".user");
	final String password = properties.getProperty(harvesterId+".password");
	String urlBase = properties.getProperty(harvesterId+".url");
	out.println(urlBase+harvesterId);
	
	Authenticator.setDefault(new Authenticator() {
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication (user, password.toCharArray());
		}
	});
	PropertiesManager harvester = new PropertiesManager();
	try { 
		URL url = new URL(urlBase+"ariadneV4.properties");
		URLConnection connection = url.openConnection();
		File file = new File(application.getRealPath("registryMgmt/"+"harvester.properties"));                           
		file.createNewFile();
		InputStream inputStream = connection.getInputStream();
		OutputStream outputStream = new FileOutputStream(file);
	    byte buf[]=new byte[1024];
	    int len;
	    while((len=inputStream.read(buf))>0)
	    	outputStream.write(buf,0,len);
	    outputStream.close();
	    inputStream.close();
		harvester.init(file); 		
	} catch (IOException e) {}
		
 
	
	if (request.getParameter("next").compareTo("Delete from the harvester >>")==0){
		harvester.removeKeyFromPropertiesFile(request.getParameter("registry_entry")+".active");
		harvester.removeKeyFromPropertiesFile(request.getParameter("registry_entry")+".latestHarvestedDatestamp");
		harvester.removeKeyFromPropertiesFile(request.getParameter("registry_entry")+".validationUri");
		harvester.removeKeyFromPropertiesFile(request.getParameter("registry_entry")+".autoReset");
		harvester.removeKeyFromPropertiesFile(request.getParameter("registry_entry")+".granularity");
		harvester.removeKeyFromPropertiesFile(request.getParameter("registry_entry")+".providerName");
		harvester.removeKeyFromPropertiesFile(request.getParameter("registry_entry")+".metadataPrefix");
		harvester.removeKeyFromPropertiesFile(request.getParameter("registry_entry")+".baseURL");
		harvester.removeKeyFromPropertiesFile(request.getParameter("registry_entry")+".harvestingSet");
		harvester.removeKeyFromPropertiesFile(request.getParameter("registry_entry")+".metadataFormat");
		harvester.removeKeyFromPropertiesFile(request.getParameter("registry_entry")+".repositoryIdentifier");
		harvester.removeKeyFromPropertiesFile(request.getParameter("registry_entry")+".repositoryName");
		harvester.removeKeyFromPropertiesFile(request.getParameter("registry_entry")+".statusLastHarvest");
		harvester.removeKeyFromPropertiesFile(request.getParameter("registry_entry")+".registryIdentifier.entry");
		harvester.removeKeyFromPropertiesFile(request.getParameter("registry_entry")+".registryIdentifier.catalog");
		harvester.removeKeyFromPropertiesFile(request.getParameter("registry_entry")+".registryTarget");
		String list = harvester.getProperty("AllTargets.list");
		if (list.endsWith(request.getParameter("registry_entry")))
			list.replaceAll(request.getParameter("registry_entry"),"");
		else
			list.replaceAll(request.getParameter("registry_entry")+";","");
		harvester.saveProperty("AllTargets.list",list);
	}else{
		harvester.saveProperty(request.getParameter("registry_entry")+".active","");
		harvester.saveProperty(request.getParameter("registry_entry")+".latestHarvestedDatestamp","");
		harvester.saveProperty(request.getParameter("registry_entry")+".validationUri","");
		harvester.saveProperty(request.getParameter("registry_entry")+".autoReset","");
		harvester.saveProperty(request.getParameter("registry_entry")+".granularity",request.getParameter("granularity"));
		harvester.saveProperty(request.getParameter("registry_entry")+".providerName","");
		harvester.saveProperty(request.getParameter("registry_entry")+".metadataPrefix",request.getParameter("metadata_prefix"));
		harvester.saveProperty(request.getParameter("registry_entry")+".baseURL",request.getParameter("location"));
		String sets="";
		for (int i=0;i<Integer.parseInt(request.getParameter("numberSets"));i++){
			if (request.getParameter("set"+i)!=null) sets += request.getParameter("set"+i)+";";		
		}
		harvester.saveProperty(request.getParameter("registry_entry")+".harvestingSet",sets);
		harvester.saveProperty(request.getParameter("registry_entry")+".metadataFormat","");
		harvester.saveProperty(request.getParameter("registry_entry")+".repositoryIdentifier",request.getParameter("registry_entry"));
		harvester.saveProperty(request.getParameter("registry_entry")+".repositoryName","");
		harvester.saveProperty(request.getParameter("registry_entry")+".statusLastHarvest","");
		harvester.saveProperty(request.getParameter("registry_entry")+".registryIdentifier.entry",request.getParameter("registry_entry"));
		harvester.saveProperty(request.getParameter("registry_entry")+".registryIdentifier.catalog",request.getParameter("registry_catalog"));
		harvester.saveProperty(request.getParameter("registry_entry")+".registryTarget","true");
		harvester.saveProperty("AllTargets.list",harvester.getProperty("AllTargets.list")+";"+request.getParameter("registry_entry"));
	}
	
	try {

		ClientHttpRequest sendFileToHarvester = new ClientHttpRequest(urlBase+"uploadServlet.jsp");
		sendFileToHarvester.setParameter("content", new File(application.getRealPath("registryMgmt/"+"harvester.properties")));
		InputStream s = sendFileToHarvester.post();
		/*StringBuilder sb = new StringBuilder();
		String line;
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(s, "UTF-8"));
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} finally {
			s.close();
			}
		out.println("Response"+sb);*/
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		out.println(e);
	}

	String query = request.getParameter("query");
	//pageContext.forward("index.jsp?"+query);
	
%>
</body>
</html>
