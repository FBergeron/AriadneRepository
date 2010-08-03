<%@page import="org.jdom.output.XMLOutputter"%>
<%@page import="org.jdom.output.Format"%>
<%@page import="org.jdom.input.SAXBuilder"%>
<%@page import="java.io.StringReader"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.net.URL"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="org.jdom.*"%>
<%@page import="org.jdom.xpath.XPath"%>
<%@page import="java.util.TreeSet"%>
<%@page import="org.ariadne.config.PropertiesManager"%>
<%@page import="org.ariadne_eu.utils.Stopwatch"%>
<%@page import="org.ariadne_eu.utils.update.QueryOnId"%>
<%@page import="org.ariadne_eu.utils.update.UpdateMetadataCollection"%>
<%@page import="java.net.URLConnection"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="org.ariadne.util.ClientHttpRequest"%>

<%@page import="org.apache.commons.httpclient.HttpClient"%>
<%@page import="org.apache.commons.httpclient.methods.PostMethod"%>
<%@page import="org.apache.commons.httpclient.HttpStatus"%><html>

	<script language="Javascript" type="text/javascript" src="../includes/editarea/edit_area/edit_area_full.js"></script>
	<script language="Javascript" type="text/javascript">
		// initialisation
		editAreaLoader.init({
			id: "metadata"	// id of the textarea to transform		
			,start_highlight: true	// if start with highlight
			,allow_resize: "both"
			,font_size: "8"  
			,font_family: "verdana, monospace"   
			,syntax_selection_allow: "xml"
			,allow_toggle: true
			//,display: "later"
			,cursor_position: "begin"
			,word_wrap: true
			,language: "en"
			,syntax: "xml"
		});
	
	</script>
	
<head>
<script type="text/javascript">
 
function feature(getOrPublish)
  {
    	document.getElementById("getOrPublishField").value=getOrPublish;
    	document.getElementById('metadata').value=editAreaLoader.getValue('metadata');
      	document.forms[0].submit();
  }
  </script>

<title>Online ASPECT Admin Tool</title>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%
	//request.setCharacterEncoding("UTF-8");
String time = "";
Stopwatch watch = new Stopwatch();
String id = request.getParameter("id");
String metadata = request.getParameter("metadata");
String chooseTargetString = request.getParameter("chooseTarget");

String error = "";
String result = "";

String getOrPublishString = "";
getOrPublishString = request.getParameter("getOrPublishField");
if(getOrPublishString == null){
	getOrPublishString = "";
}

if(getOrPublishString.equalsIgnoreCase("clear")){
	metadata = null;
	id = null;
}

if(id == null){
	id = "";
}else if(getOrPublishString.equalsIgnoreCase("get")){
	try{
		metadata = QueryOnId.getMACEquery().getMetadataCollectionInstance(id);
	}catch(Exception e){
		error = e.getMessage();
		metadata = "";
	}
}

if (metadata != null){
	//metadata = new String(metadata.getBytes("ISO-8859-1"),"UTF-8");
	//metadata = metadata.replaceAll("&","&amp;");
	if(getOrPublishString.equalsIgnoreCase("publish")){
		try{
			StringBuilder sb = new StringBuilder();
			
			HttpClient client = new HttpClient();
			client.getParams().setParameter("http.useragent", "Test Client");

			BufferedReader br = null;

			PostMethod method = new PostMethod("http://ariadne.cs.kuleuven.be/validationService/api/validate");
			method.addParameter("metadata", metadata);
			method.addParameter("scheme", "http://www.imsglobal.org/services/lode/validation/Registry");
			try{
				int returnCode = client.executeMethod(method);

				if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
					System.err.println("The Post method is not implemented by this URI");
					// still consume the response body
					result = method.getResponseBodyAsString();
				} else {
					br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
					String readLine;
					while(((readLine = br.readLine()) != null)) {
						System.out.println(readLine);
						sb.append(readLine + "\n");
					}
				}
			} catch (Exception e) {
				System.err.println(e);
			} finally {
				method.releaseConnection();
				if(br != null) try { br.close(); } catch (Exception fe) {}
			}
			if (!sb.toString().contains("Error")){
				UpdateMetadataCollection.getInstance().publishMetadata(metadata);
				result += "Publishing successful ! " +sb.toString();
			}else{
				result += sb.toString().length();
			}
				
		}catch(Exception e){
			error = e.getMessage();
		}
	} else if (getOrPublishString.equalsIgnoreCase("delete")) {
		try{
	UpdateMetadataCollection.getInstance().deleteMetadata(id);
	result = "Deleting successful !";
		}catch(Exception e){
	error = e.getMessage();
		}
	} 
} else{
	metadata = "";
}

	//metadata = metadata.replaceAll("&amp;","&amp;amp;");
%>
<link media="all" href="<%=request.getContextPath()%>/style.css" type="text/css" rel="stylesheet"> 
<%@ include file="/layout/headLinks.jsp"%>
<%@ include file="/layout/header.jsp"%>
<br>
<center>
<form method="post" action="updateMetadata.jsp">

<br>
<table border="1" bgcolor="lightgrey" width="80%">
	<tr>
		<TD><br>
		Retrieve the MetadataCollection or protocol record with this identifier :<br>
		<br>
		<input style="width: 30%;" name="id" value="<%=id%>"/>
		
		<br>
		
			<%
			//if(id.equalsIgnoreCase("") || metadata.equalsIgnoreCase("")){
				out.print("<input type=\"button\" onClick=\"feature('get');\" value=\"Get Metadata\">");
			//}else{
				out.print("<input type=\"button\" onClick=\"feature('clear');\" value=\"Clear\">");
			//}
			%>
			
	</tr>
	<tr>
		<TD><br>
		Metadata :<br>
		<br>
		<textarea id="metadata" style="width: 100%;" rows="20" name="metadata"><%=metadata%></textarea>
		<br>
		<br>
		<input type="button" onClick="feature('publish');"
			value="Publish !"
			<%
			/*if(metadata.equalsIgnoreCase("")){
				out.print("disabled=\"disabled\"");
			}*/
			%>
			>
			<input type="button" onClick="feature('delete');"
			value="Delete !"
			<%
			/*if(metadata.equalsIgnoreCase("")){
				out.print("disabled=\"disabled\"");
			}*/
			%>
			>
	</tr>
</table>
<br>

<h2><font color="FF0000"><%=error%></font></h2>

<h3><%=result%></h3>

<input type="hidden" id="getOrPublishField" name="getOrPublishField" value="" />
</form>
</center>
<br>

</body>
</html>
