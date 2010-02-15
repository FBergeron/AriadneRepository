<%@ page import="be.cenorm.www.SqiTargetStub" %>
<%@ page import="be.cenorm.www.SqiSessionManagementStub" %>
<%@ page import="be.cenorm.www.*" %>
<%@ page import="org.xml.sax.InputSource" %>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory" %>
<%@ page import="org.apache.xpath.XPathAPI" %>
<%@ page import="org.ariadne.config.PropertiesManager" %>
<%@ page import="net.sf.vcard4j.parser.DomParser" %>
<%@ page import="net.sf.vcard4j.java.VCard" %>
<%@ page import="net.sf.vcard4j.java.AddressBook" %>
<%@ page import="net.sf.vcard4j.java.type.FN" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.io.*" %>
<%@ page import="org.apache.xerces.dom.DocumentImpl" %>
<%@ page import="net.sf.vcard4j.java.type.N" %>
<%@ page import="org.w3c.dom.*" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="java.rmi.RemoteException" %>
<%@page import="org.ariadne.config.PropertiesManager"%>
<%@page import="java.util.*"%>
<%@page import="java.net.Authenticator"%>
<%@page import="java.net.PasswordAuthentication"%>
<%@page import="java.net.URL"%>
<%@page import="java.net.URLConnection"%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%! static SqiSessionManagementStub sqiSessionStub; %>
<%! static SqiTargetStub sqiStub; %>
<%! String result; %>
<%! String result_protocol; %>
<%! String query; %>
<%! String query_protocol; %>
<%! String sessionId; %>
<%! static GetTotalResultsCountResponse countResponse = null; %>
<%! static SynchronousQueryResponse synchronousQueryResponse = null; %>

<%  
	String logSystemDir = PropertiesManager.getInstance().getProperty("repository.log4j.directory");
	if (logSystemDir.compareTo("")==0){
		response.sendRedirect("../init/index.jsp");
	}
	PropertiesManager harvester = new PropertiesManager(); 
	
	File file = new File(application.getRealPath("registryMgmt/"+"harvesters.properties"));                           
	harvester.init(file);
	String harvesterId = request.getParameter("id");
	final String user = harvester.getProperty(harvesterId+".user");
	final String password = harvester.getProperty(harvesterId+".password");
	String urlBase = harvester.getProperty(harvesterId+".url");

	PropertiesManager properties = new PropertiesManager(); 
	Authenticator.setDefault(new Authenticator() {
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication (user, password.toCharArray());
		}
	});
	// "aspect"  "tomcat4aspect" "http://monet:3781/aspect-harvester/install/ariadneV4.properties"
	File harvesters = new File(application.getRealPath("registryMgmt/"+"harvester.properties"));                           
	harvesters.createNewFile();
	try { 
		URL url = new URL(urlBase+"ariadneV4.properties");
		URLConnection connection = url.openConnection();
		
		InputStream inputStream = connection.getInputStream();
		OutputStream outputStream = new FileOutputStream(harvesters);
	    byte buf[]=new byte[1024];
	    int len;
	    while((len=inputStream.read(buf))>0)
	    	outputStream.write(buf,0,len);
	    outputStream.close();
	    inputStream.close();
	    properties.init(harvesters); 
	    harvesters.delete();
	} catch (IOException e) { 
		System.out.println(e);
	} 
	
	String format, language;
	int resultSize, startResult;
	String query_temp="";
	try{
        if (request.getParameter("query") != null){
        	query_temp = request.getParameter("query");
        	query = query_temp+" and metadataCollection.target.targetDescription.protocolIdentifier.entry=\"oai-pmh-v2\"";query = request.getParameter("query");
        }
        else{ 
                query = null;
                countResponse = null;
                synchronousQueryResponse = null;
        }
	}catch(Exception e){
	        query = null;
	        countResponse = null;
	        synchronousQueryResponse = null; 
	}
	
	resultSize = 10;
    startResult = 1;    
    
    try {
        if (request.getParameter("next") != null)
            startResult = Integer.parseInt(request.getParameter("start_result")) + resultSize;
    } catch (Exception e) {
    }
    
    try {
        if (request.getParameter("previous") != null)
            startResult = Integer.parseInt(request.getParameter("start_result")) - resultSize;
    } catch (Exception e) {
    }
    
    String axis2_url = PropertiesManager.getInstance().getProperty("axis2.url");
    if (axis2_url == null)
        axis2_url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/services";

    if (query != null && query.length() > 0) {
        try {
        	sessionId = createAnonymousSession(axis2_url +  "/SqiSessionManagement");
			sqiStub = new SqiTargetStub(axis2_url + "/SqiTarget");
			language = "plql1";
			setQueryLanguage(sessionId, language);
			setResultSetSize(sessionId, resultSize);
			format = "lom";
			setResultSetFormat(sessionId, format);

			result = query(sessionId, query, startResult);
			
        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            e.printStackTrace(writer);
        }
    }

%>


<html>


  <head>
      <link media="all" href="<%=request.getContextPath()%>/style.css" type="text/css" rel="stylesheet">
      <title>Search page</title>
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
      <form action="search.jsp">


        <table align="center">
            <tr>
                <td>
                    <div class="box">
                        <div>

                              <table>
                                  <tr>
                                      <td><p>Enter search query:</p></td>
                                  </tr>
                                  <tr>
                                      <td><input type="text" name="query" value="<%=query_temp != null ? StringEscapeUtils.escapeHtml(query_temp) : ""%>" /></td>
                                  </tr>
                                  <tr>
                                      <td><input type="submit" name="search" value="search" /></td>
                                  </tr>              
                                  <input type="hidden" name="id" value="<%=harvesterId%>"/>                    
                                  <tr>
                                      <td align="center"><A href="search.jsp?query=http&search=search&id=<%=harvesterId%>">Show all the content</A></td>
                                  </tr>
                              </table>


                            <p class="last">&nbsp;</p>

                        </div>
                    </div>
                </td>
            </tr>
        </table>

          

      </form>
  </center>





<%
    if (synchronousQueryResponse != null) {
%>



  <center>
  
<%
        int nbResults = countResults(sessionId, query);
        if (nbResults > 0)
        {
%>





<div class="container" style="width:85%;">
    <div>
        <center>
            <p>Showing results <%=startResult%> to <%=Math.min(resultSize + startResult - 1, countResponse.getGetTotalResultsCountReturn())%> of <%=countResponse.getGetTotalResultsCountReturn()%>.</p>
            <form action="search.jsp">
                <input type="hidden" name="start_result" value="<%=startResult%>" />
                <input type="hidden" name="id" value="<%=harvesterId%>"/>  
                <input type="hidden" name="query" value="<%=query != null ? StringEscapeUtils.escapeHtml(query) : ""%>" />
                <%if(startResult>1){ %>
                <input type="submit" name="previous" value="<< previous" />
                <%}
                if (startResult<nbResults-resultSize){%>
                <input type="submit" name="next" value="next >>" />                
                <%}%>
            </form>
        </center>


        <table class="searchResults" cellpadding="0" cellspacing="0">

<%

    StringReader stringReader = new StringReader(result);
    InputSource input = new InputSource(stringReader);

    try {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);

        Node result = doc.getFirstChild();
        NodeList nl = result.getChildNodes();
        int currentResultCounter = 0;
        for (int i = 0; i < nl.getLength(); i++)
        {
            try {
                Element theNode = ((Element) nl.item(i));

                NodeList nl2;
               

                String title = "Untitled";
                try {
                    nl2 = XPathAPI.selectNodeList(theNode, "identifier/entry/text()");
                    title = nl2.item(0).getNodeValue();
                } catch (Exception e) {
                }

                String description = "";
                try {
                    nl2 = XPathAPI.selectNodeList(theNode, "description/string/text()");
                    description = nl2.item(0).getNodeValue();
                } catch (Exception e) {
                }

%>



			
			
            <tr class="searchResultsRow<%=(currentResultCounter%2==1) ? "Odd" : "Even"%>">
                <td>

                    <b>Identifier: </b><%=title%>

                </td><td/>                 
            </tr>
<%
    if (description != null && description.length()>0)
    {
%>
            <tr class="searchResultsRow<%=(currentResultCounter%2==1) ? "Odd" : "Even"%>">
                <td colspan="2" class="searchResultsDescription"><b>Description:</b><br /><%=description%></td>
            </tr>
<%
    }
%>


<%
				String target_id = "";
				String target_catalog = "";
				String target_protocol_identifier = "";
				String target_protocol_catalog = "";
				String target_location = "";
                try {
				NodeList nl_target_entry = XPathAPI.selectNodeList(theNode, "target/targetDescription/identifier/entry/text()");
				NodeList nl_target_catalog = XPathAPI.selectNodeList(theNode, "target/targetDescription/identifier/catalog/text()");
				NodeList nl_target_protocol_identifier = XPathAPI.selectNodeList(theNode, "target/targetDescription/protocolIdentifier/entry/text()");
				NodeList nl_target_protocol_catalog = XPathAPI.selectNodeList(theNode, "target/targetDescription/protocolIdentifier/catalog/text()");
				NodeList nl_target_location = XPathAPI.selectNodeList(theNode, "target/targetDescription/location/text()");
				NodeList nl_oai_prefix = XPathAPI.selectNodeList(theNode, "target/targetDescription/protocolImplementationDescription/oai-pmh/metadataFormat/metadataPrefix/text()");
				NodeList nl_oai_sets = XPathAPI.selectNodeList(theNode, "target/targetDescription/protocolImplementationDescription/oai-pmh/set/text()");
				NodeList nl_oai_granularity = XPathAPI.selectNodeList(theNode, "target/targetDescription/protocolImplementationDescription/oai-pmh/granularity/text()");
				NodeList nl_oai_location = XPathAPI.selectNodeList(theNode, "target/targetDescription/location/text()");
                for (int node=0;node<nl_target_entry.getLength();node++){
					target_id = nl_target_entry.item(node).getNodeValue();
					target_catalog = nl_target_catalog.item(node).getNodeValue();
					target_protocol_identifier = nl_target_protocol_identifier.item(node).getNodeValue();
					target_protocol_catalog = nl_target_protocol_catalog.item(node).getNodeValue();
					target_location = nl_target_location.item(node).getNodeValue();
					
					if (target_protocol_identifier.compareTo("oai-pmh-v2")==0){
	%>
	<%
						if (target_id != null && target_id.length()>0)
						{				
	%>
						<tr class="searchResultsRow<%=(currentResultCounter%2==1) ? "Odd" : "Even"%>">
							<td colspan="1" class="searchResultsDescription" align="right"><b>Target <%=node%>:</b>
						</td><td/></tr>
						<tr class="searchResultsRow<%=(currentResultCounter%2==1) ? "Odd" : "Even"%>">
							<td colspan="1"/><td colspan="1" class="searchResultsDescription"><form action="harvest.jsp"><b>Entry: </b><%=target_id%>				
	<%					}%>
	<%
						if (target_catalog != null && target_catalog.length()>0)
						{				
	%>					
							<br/><b>Catalog :</b> <%=target_catalog%>				
	<%					}%>
	<%
						if ((target_protocol_identifier != null && target_protocol_identifier.length()>0) && (target_protocol_catalog != null && target_protocol_catalog.length()>0))
						{				
							
							
							query_protocol = "(protocol.identifier.entry = \""+target_protocol_identifier+"\") and (protocol.identifier.catalog= \""+target_protocol_catalog+"\")";	
							sessionId = createAnonymousSession(axis2_url +  "/SqiSessionManagement");
							sqiStub = new SqiTargetStub(axis2_url + "/SqiTarget");
							int startResult_protocol=1;
							language = "plql1";
							setQueryLanguage(sessionId, language);
							setResultSetSize(sessionId, resultSize);
							format = "lom";
							setResultSetFormat(sessionId, format);
	
							result_protocol = query(sessionId, query_protocol, startResult_protocol);
							StringReader stringReader_protocol = new StringReader(result_protocol);
						    InputSource input_protocol = new InputSource(stringReader_protocol);
					        Document doc_protocol = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input_protocol);
	
					        Node result_protocol = doc_protocol.getFirstChild();
					        NodeList nl_protocol = result_protocol.getChildNodes();
					        int currentResultCounter_protocol = 0;
					        for (int i_protocol = 0; i_protocol < nl_protocol.getLength(); i_protocol++)
					        {
					            try {
					                Element theNode_protocol = ((Element) nl_protocol.item(i_protocol));
	
					                NodeList nl2_protocol;
					                String title_protocol = "";
					                try {
					                    nl2_protocol = XPathAPI.selectNodeList(theNode_protocol, "name/text()");
					                    title_protocol = nl2_protocol.item(0).getNodeValue();
					                } catch (Exception e) {
					                }
					                String binding_space_protocol = "";
					                try {
					                    nl2_protocol = XPathAPI.selectNodeList(theNode_protocol, "protocolDescriptionBindingNamespace/text()");
					                    binding_space_protocol = nl2_protocol.item(0).getNodeValue();
					                } catch (Exception e) {
					                }
					                String binding_location_protocol = "";
					                try {
					                    nl2_protocol = XPathAPI.selectNodeList(theNode_protocol, "protocolDescriptionBindingLocation/text()");
					                    binding_location_protocol = nl2_protocol.item(0).getNodeValue();
					                } catch (Exception e) {
					                }
									if (title_protocol != null && title_protocol.length()>0)
									{%>					
										<br/><b>Protocol Name :</b> <%=title_protocol%>				
	<%								}
									
									if (binding_space_protocol != null && binding_space_protocol.length()>0)
									{%>					
										<br/><b>Protocol Description Binding Name Space :</b> <%=binding_space_protocol%>	
	<%								}	
									
									if (binding_location_protocol != null && binding_location_protocol.length()>0)
									{%>					
										<br/><b>Protocol Description Binding Location :</b> <%=binding_location_protocol%>	
	<%								}
					            } catch (Exception e) {}			            
					        }
					        
						}
						for (int node_prefix=0;node_prefix<nl_oai_prefix.getLength();node_prefix++){
							String oai_prefix = nl_oai_prefix.item(node_prefix).getNodeValue();
							%><br/><b>Metadata prefix :</b> <input  type="radio" value="<%=oai_prefix%>" name="metadata_prefix" <%
							String prefix = properties.getProperty(title+".metadataPrefix");
							if (prefix!=null){
								if ((prefix.compareTo(oai_prefix)==0)||(node_prefix==0)) out.println("checked");
							}else{
								if (node_prefix==0) out.println("checked");
							}
							%>/><%=oai_prefix%>	<% 
						}
						%><input  type="hidden" value="<%=nl_oai_sets.getLength()%>" name="numberSets"/>
						<input  type="hidden" value="<%=nl_oai_granularity.item(0).getNodeValue()%>" name="granularity"/>
						<input  type="hidden" value="<%=nl_oai_location.item(0).getNodeValue()%>" name="location"/>
						<input  type="hidden" value="<%=target_id%>" name="target_id"/>
						<input type="hidden" name="id" value="<%=harvesterId%>"/>  
						<input  type="hidden" value="<%=title%>" name="registry_entry"/>
						<input  type="hidden" value="<%=XPathAPI.selectNodeList(theNode, "identifier/catalog/text()").item(0).getNodeValue()%>" name="registry_catalog"/>
						<input type="hidden" name="query" value="<%=query != null ? StringEscapeUtils.escapeHtml(query) : ""%>" />
						<% 
						for (int node_set=0;node_set<nl_oai_sets.getLength();node_set++){
							String oai_set = nl_oai_sets.item(node_set).getNodeValue();
							%><br/><b>Sets :</b><input  type="checkbox" value="<%=oai_set%>" name="set<%=node_set%>" 
								<%String listSets = properties.getProperty(title+".harvestingSet"); 
								  if (listSets!=null){
									  if ((listSets.lastIndexOf(";"+oai_set)>0)||(listSets.lastIndexOf(oai_set+";")>=0)) out.println("checked");
								  }
								  %>/> 
							<%=oai_set%>	<% 
						}
						
					
						
					}
				}
                
			    if (properties.getProperty(title+".active")!=null){
					out.println("<br/><input type=\"submit\" name=\"next\" value=\"Upload to harvester >>\" />");
					out.println("<input type=\"submit\" name=\"next\" value=\"Delete from the harvester >>\" /></form></form></td></tr>");
			    }else{
			    	out.println("<br/><input type=\"submit\" name=\"next\" value=\"Add to harvester >>\" /></form></form></td></tr>");
			    }
                } catch (Exception e) {
                	if (properties.getProperty(title+".active")!=null){
    					out.println("<br/><input type=\"submit\" name=\"next\" value=\"Upload to harvester >>\" />");
    					out.println("<input type=\"submit\" name=\"next\" value=\"Delete from the harvester >>\" /></form></form></td></tr>");
    			    }else{
    			    	out.println("<br/><input type=\"submit\" name=\"next\" value=\"Add to harvester >>\" /></form></form></td></tr>");
    			    }    
    			    
                }
%>




<%
            currentResultCounter++;
            } catch (Exception e) {}
        }
    } catch (Exception e) {}
%>


        </table>
        </div>
    </div>








<%
        }
        else
        {
%>





<div class="container">
    <div>
        <center>
            <p>Nothing found</p>
        </center>
    </div>
</div>







<%
        }
%>
      </center>




<%
    }
%>


</div>



    <%     pageContext.include("/layout/footer.jsp"); %> </body>


</html>


<%!
    public String getVCardFN(String vcardString) {

        try {
            DomParser parser = new DomParser();
            Document document = new DocumentImpl();
            parser.parse(new StringReader(vcardString), document);

            AddressBook addressBook = new AddressBook(document);
            for (Iterator vcards = addressBook.getVCards(); vcards.hasNext();) {
                VCard vcard = (VCard) vcards.next();
                FN fn = (FN) vcard.getTypes("FN").next();
                return fn.get();
//                System.out.println(fn.get() + ":");
//                for (Iterator tels = vcard.getTypes("TEL"); tels.hasNext();) {
//                    TEL tel = (TEL) tels.next();
//                    if (((TEL.Parameters) tel.getParameters()).containsTYPE(TEL.Parameters.TYPE_CELL)) {
//                        System.out.println("  Tel (gsm): " + tel.get());
//                    } else {
//                        System.out.println("  Tel      : " + tel.get());
//                    }
//                }
//                for (Iterator emails = vcard.getTypes("EMAIL"); emails.hasNext();) {
//                    EMAIL email = (EMAIL) emails.next();
//                    System.out.println("  E-Mail   : " + email.get());
//                }
//                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getVCardN(String vcardString) {

        try {
            DomParser parser = new DomParser();
            Document document = new DocumentImpl();
            parser.parse(new StringReader(vcardString), document);

            AddressBook addressBook = new AddressBook(document);
            for (Iterator vcards = addressBook.getVCards(); vcards.hasNext();) {
                VCard vcard = (VCard) vcards.next();
                N n = (N) vcard.getTypes("N").next();
                return n.getFamily();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //Copy of functions from SqiTest
    
    public static String createAnonymousSession(String target){
		try {
			sqiSessionStub = new SqiSessionManagementStub(target);
			CreateAnonymousSession createASession = new CreateAnonymousSession();
			CreateAnonymousSessionResponse sessionResponse = sqiSessionStub.createAnonymousSession(createASession);
			String sessionId = sessionResponse.getCreateAnonymousSessionReturn();
			return sessionId;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		
	}
	
	public static void setQueryLanguage(String sessionId, String language) throws RemoteException, _SQIFaultException {
		SetQueryLanguage queryLanguage = new SetQueryLanguage();
		queryLanguage.setQueryLanguageID(language);
		queryLanguage.setTargetSessionID(sessionId);
		sqiStub.setQueryLanguage(queryLanguage);

	}
	
	public static void setResultSetSize(String sessionId, int resultSize) throws RemoteException, _SQIFaultException {
		SetResultsSetSize resultsSetSize = new SetResultsSetSize();
		resultsSetSize.setResultsSetSize(resultSize);
		resultsSetSize.setTargetSessionID(sessionId);
		sqiStub.setResultsSetSize(resultsSetSize);
	}
	
	public static void setResultSetFormat(String sessionId, String format) throws RemoteException, _SQIFaultException {
		SetResultsFormat resultsSetFormat = new SetResultsFormat();
		resultsSetFormat.setResultsFormat(format);
		resultsSetFormat.setTargetSessionID(sessionId);
		sqiStub.setResultsFormat(resultsSetFormat);
	}
	
	public static String query(String sessionId, String query, int startResult) throws RemoteException, _SQIFaultException {
		SynchronousQuery syncQuery = new SynchronousQuery();
		syncQuery.setQueryStatement(query);
		syncQuery.setStartResult(startResult);
		syncQuery.setTargetSessionID(sessionId);
		
		synchronousQueryResponse = sqiStub.synchronousQuery(syncQuery);
		String synchronousQueryReturn = synchronousQueryResponse.getSynchronousQueryReturn();
		return synchronousQueryReturn;
		
	}
	
	public static int countResults(String sessionId, String query) throws RemoteException, _SQIFaultException {
		GetTotalResultsCount getTotalResultsCount = new GetTotalResultsCount();
		getTotalResultsCount.setQueryStatement(query);
		getTotalResultsCount.setTargetSessionID(sessionId);
		countResponse = sqiStub.getTotalResultsCount(getTotalResultsCount);
		return countResponse.getGetTotalResultsCountReturn();
	}
	
%>







