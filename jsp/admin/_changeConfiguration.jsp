<%@ page import="org.ariadne_eu.utils.config.ConfigManager" %>
<%@ page import="java.io.IOException" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.ariadne_eu.utils.config.servlets.InitServlet" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    Property[] metadataProperties = new Property[7];
    metadataProperties[0] = new Property("db.querymetadata.implementation", "Metadata store query implementation", "org.ariadne_eu.metadata.query.QueryXmlDbImpl OR org.ariadne_eu.metadata.query.XQuerySqlDbImpl");
    metadataProperties[1] = new Property("db.insertmetadata.implementation", "Metadata store insert implementation", "org.ariadne_eu.metadata.insert.InsertMetadataXmlDbImpl OR org.ariadne_eu.metadata.insert.InsertMetadataXQuerySqlDbImpl");
    metadataProperties[2] = new Property("db.metadata.uri", "Metadata storage location", "e.g. xmldb:exist://localhost:8080/exist/xmlrpc/db/metadatastore");
    metadataProperties[3] = new Property("db.metadata.driver", "Storage driver", "db.metadata.driver OR com.ibm.db2.jcc.DB2Driver");
    metadataProperties[4] = new Property("db.metadata.username", "Storage username", "The user must be able to insert and search");
    metadataProperties[5] = new Property("db.metadata.password", "Storage password", "The user must be able to insert and search");
    metadataProperties[6] = new Property("xmldb.metadata.loc", "Metadata Storage Location", "Location where the metadata will be stored within the xml store.");

    Property[] contentProperties = new Property[6];
    contentProperties[0] = new Property("xmldb.content.uri", "Storage location", "e.g. xmldb:exist://localhost:8080/exist/xmlrpc/db/contentstore");
    contentProperties[1] = new Property("xmldb.content.driver", "Storage driver", "e.g. org.exist.xmldb.DatabaseImpl");
    contentProperties[2] = new Property("xmldb.content.username", "Storage username", "The user must be able to insert and search");
    contentProperties[3] = new Property("xmldb.content.password", "Storage password", "The user must be able to insert and search");
    contentProperties[4] = new Property("xmldb.content.loc", "Content Description Location", "Location where the metadata will be stored within the xml store.");
    contentProperties[5] = new Property("dr.basePath", "Content Storage Location", "Location on the filesystem where the uploaded files will be stored");

    Property[] otherProperties = new Property[4];
    otherProperties[0] = new Property("xmlns.xsd", "Namespace", "Namespace used when inserting lom instances");
    otherProperties[1] = new Property("sqi.username", "SQI Insert username", "Username used for authenticated session allowing inserts");
    otherProperties[2] = new Property("sqi.password", "SQI Insert password", "Password used for authenticated session allowing inserts");
    otherProperties[3] = new Property("log4j.properties", "Logfile", "Location of the logfile");


    String submit = request.getParameter("submit");
//    boolean reloadSuccess = false;
//    String message = null;
    try {
        if (submit != null && submit.length() > 0) {
            for (int i = 0; i < metadataProperties.length; i++) {
                Property property = metadataProperties[i];
                String parameter = request.getParameter(property.key);
                if (parameter == null || parameter.length() == 0)
                    parameter = null;
                ConfigManager.saveProperty(property.key, parameter);
            }
            for (int i = 0; i < contentProperties.length; i++) {
                Property property = contentProperties[i];
                String parameter = request.getParameter(property.key);
                if (parameter == null || parameter.length() == 0)
                    parameter = null;
                ConfigManager.saveProperty(property.key, parameter);
            }
            for (int i = 0; i < otherProperties.length; i++) {
                Property property = otherProperties[i];
                String parameter = request.getParameter(property.key);
                if (parameter == null || parameter.length() == 0)
                    parameter = null;
                ConfigManager.saveProperty(property.key, parameter);
            }
            InitServlet.initializePropertiesManager();
            InitServlet.initializeServices();
//            reloadSuccess = true;
        }
    } catch (Exception e) {
//        reloadSuccess = false;
//        message = e.getMessage();
    }
%>


<html>
  <head>
      <link media="all" href="<%=request.getContextPath()%>/style.css" type="text/css" rel="stylesheet">
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
          <form action="" method="post">


            <table align="center">
                <tr>
					<th colspan="2">Metadata store</th>
				</tr>
                <%
                    for (int i = 0; i < metadataProperties.length; i++) {
                        Property property = metadataProperties[i];
                        showLine(property, out);
                    }
                %>
                <tr>
					<th colspan="2">Content store</th>
				</tr>
                <%
                    for (int i = 0; i < contentProperties.length; i++) {
                        Property property = contentProperties[i];
                        showLine(property, out);
                    }
                %>
                <tr>
					<th colspan="2">General Properties</th>
				</tr>
                <%
                    for (int i = 0; i < otherProperties.length; i++) {
                        Property property = otherProperties[i];
                        showLine(property, out);
                    }
                %>
            </table>


            <center><input type="submit" value="Modify and Reload" name="submit" /></center>
          </form>
      </center>
    </div>
<%
    pageContext.include("/layout/footer.jsp");
%>
  </body>
</html>

<%!
    void showLine(String key, String description, String tooltip, JspWriter out) throws IOException {
        String value = ConfigManager.getProperty(key);
        if (value == null) {
            value = "";
        }
        key = StringEscapeUtils.escapeHtml(key);
        description = StringEscapeUtils.escapeHtml(description);
        tooltip = StringEscapeUtils.escapeHtml(tooltip);
        value = StringEscapeUtils.escapeHtml(value);

        out.println("\n" +
                "                <tr>\n" +
                "                    <td align=\"center\" class=\"quote\"><label for=\"" + key + "\"" + (tooltip != null ? (" alt=\"" + tooltip + "\" title=\"" + tooltip + "\"") : "") + ">" + description + "</label></td>\n" +
                "                    <td width=\"80%\" class=\"quote\"><input type=\"text\" value=\"" + value + "\" name=\"" + key + "\" id=\"" + key + "\" class=\"inputbox\" size=\"50\" style=\"width:97%; padding:0; margin:0;\"/></td>\n" +
                "                </tr>");

    }

    void showLine(Property property, JspWriter out) throws IOException {
        showLine(property.key, property.description, property.tooltip, out);
    }

    class Property {
        String key;
        String description;
        String tooltip;

        public Property(String key, String description, String tooltip) {
            this.key = key;
            this.description = description;
            this.tooltip = tooltip;
        }
    }
%>
