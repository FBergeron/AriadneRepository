<%@ page session="true" %>
<%@ page import="org.ariadne.config.*" %>

<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>

<html>
  <head>
      <link rel="stylesheet" href="<%=request.getContextPath()%>/style.css" type="text/css" />
      <title>Change the Configuration</title>
<%
      pageContext.include("/layout/headLinks.jsp");
%>
  </head>
  <body>
<%
    pageContext.include("/layout/header.jsp");
%>
<div class="page">
    <center>

<form ACTION="config.jsp" METHOD=post name="conf" id="conf">
<INPUT TYPE=hidden NAME="usrname" VALUE="<%=request.getParameter("usrname") %>">
<INPUT TYPE=hidden NAME="password" VALUE="<%=request.getParameter("password") %>">

<%
    
    //PropertiesManager.setPropertiesFile(application.getRealPath("install")+File.separator+"ariadne.properties");
    //if (!PropertiesManager.getPropertiesFile().exists())
    //out.println("Could not find ariadneV4.properties template at '"+PropertiesManager.getPropertiesFile()+"'");
    //PropertiesManager.init();
    Iterator it = null;

	it = PropertiesManager.getTypes().iterator();
         %>
 Location of config file: <%= PropertiesManager.getPropFile()%>
 

	<table >
 <%
 	
    while ((it !=null)&&(it.hasNext())){
	String fam = (String) it.next();
	%>

		<tr>

							<th colspan="2"><%= fam%></th>
						</tr>
		
	<%
	  Iterator iter = PropertiesManager.getPropertyStartingWith(fam).entrySet().iterator();
	  while (iter.hasNext()) {
		Map.Entry entry = (Map.Entry) iter.next();
		if (request.getParameter((String) entry.getKey())!= null) {
			//out.println((String) entry.getKey()+" "+ request.getParameter((String) entry.getKey())+" "+PropertiesManager.getProperty((String) entry.getKey())+"<br>");
			PropertiesManager.saveProperty((String) entry.getKey(), request.getParameter((String) entry.getKey()));
			//out.println((String) entry.getKey()+" "+ request.getParameter((String) entry.getKey())+" "+PropertiesManager.getProperty((String) entry.getKey())+"<br>");
		}
		%>
	  	<tr>
    	  <td  class="quote"><label><%= entry.getKey()%></label></td>
    	  <td width="70%" class="quote"><input TYPE="text" NAME="<%= entry.getKey()%>" class="inputbox" value="<%= PropertiesManager.getProperty((String) entry.getKey())%>" style="width:97%; padding:0; margin:0;"></td>
		</tr>
		<%
	  }
	}
	%>
  </table>
		<br>

	<input TYPE="submit" NAME="Request" VALUE="Submit ">
   <input TYPE="reset" NAME="Clear" VALUE="Clear"><br><br>
   <a href="../configuration/testConfiguration.jsp">validate configuration</a><br>
   <a href="../start">home</a><br>

</form>
      </center>
    </div>
<%
    pageContext.include("/layout/footer.jsp");
%>
  </body>
</html>