package org.oclc.oai.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.oclc.oai.server.catalog.AbstractCatalog;
import org.oclc.oai.server.verb.BadVerb;
import org.oclc.oai.server.verb.OAIInternalServerError;
import org.oclc.oai.server.verb.ServerVerb;


public class LomOAIHandler extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String PROPERTIES_SERVLET_CONTEXT_ATTRIBUTE = OAIHandler.class.getName() + ".properties";

	public final String PROPERTIES_INIT_PARAMETER = "properties";
	 
	private static final String VERSION = "1.5.49";
	private static boolean debug = false;
//	private Transformer transformer = null;
//	private boolean serviceUnavailable = false;
//	private boolean monitor = false;
//	private boolean forceRender = false;
	protected HashMap attributesMap = new HashMap();
//	private HashMap serverVerbs = null;
//	private HashMap extensionVerbs = null;
//	private String extensionPath = null;

//	private static Logger logger = Logger.getLogger(OAIHandler.class);
//	static {
//	BasicConfigurator.configure();
//	}

	/**
	 * Get the VERSION number
	 */
	public static String getVERSION() { return VERSION; }

	/**
	 * init is called one time when the Servlet is loaded. This is the
	 * place where one-time initialization is done. Specifically, we
	 * load the properties file for this application, and create the
	 * AbstractCatalog object for subsequent use.
	 *
	 * @param config servlet configuration information
	 * @exception ServletException there was a problem with initialization
	 */
	public void init(ServletConfig config) throws ServletException {
		try{
			super.init(config);
		}
		catch (Exception e) {
			// NOOP : the location of the properties file isnt indicated anymore, but is made below
		}

		try {
			HashMap attributes = null;
			ServletContext context = getServletContext();
			Properties properties = (Properties) context.getAttribute(PROPERTIES_SERVLET_CONTEXT_ATTRIBUTE);
			String fileName = config.getServletContext().getInitParameter(PROPERTIES_INIT_PARAMETER);
			String propertiesFile = config.getServletContext().getRealPath("WEB-INF") + File.separator + fileName;
			InputStream in;
			try {
//				logger.debug("fileName=" + fileName);
				in = new FileInputStream(propertiesFile);
			} catch (FileNotFoundException e) {
				in = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesFile);
//				logger.debug("thread:" + fileName + "=" + in);
			}
			if (in != null) {
				properties = new Properties();
				properties.load(in);
				attributes = getAttributes(properties);
				//if (debug) System.out.println("OAIHandler.init: fileName=" + fileName);
			}

			attributesMap.put("global", attributes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}
	}

	protected HashMap getAttributes(Properties properties)
	throws Throwable {
		HashMap attributes = new HashMap();
		Enumeration attrNames = getServletContext().getAttributeNames();
		while (attrNames.hasMoreElements()) {
			String attrName = (String)attrNames.nextElement();
			attributes.put(attrName, getServletContext().getAttribute(attrName));
		}
		attributes.put("OAIHandler.properties", properties);
//		String temp = properties.getProperty("OAIHandler.debug");
//		if ("true".equals(temp)) debug = true;
		if (!"true".equals(properties.getProperty("OAIHandler.serviceUnavailable"))) {
			attributes.put("OAIHandler.version", VERSION);
			AbstractCatalog abstractCatalog = AbstractCatalog.factory(properties);
			attributes.put("OAIHandler.catalog", abstractCatalog);
		}
		boolean forceRender = false;
		if ("true".equals(properties.getProperty("OAIHandler.forceRender"))) {
			forceRender = true;
		}
		String xsltName = properties.getProperty("OAIHandler.styleSheet");
		String appBase = properties.getProperty("OAIHandler.appBase");
		if (appBase == null) appBase = "webapps";
		if (xsltName != null
				&& ("true".equalsIgnoreCase(properties.getProperty("OAIHandler.renderForOldBrowsers"))
						|| forceRender)) {
			InputStream is;
			try {
				is = new FileInputStream(appBase + "/" + xsltName);
			} catch (FileNotFoundException e) {
				// This is a silly way to skip the context name in the xsltName
				is = new FileInputStream(getServletContext().getRealPath(xsltName.substring(xsltName.indexOf("/", 1)+1)));
			}
			StreamSource xslSource = new StreamSource(is);
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(xslSource);
			attributes.put("OAIHandler.transformer", transformer);
		}
		return attributes;
	}

	protected HashMap getAttributes(String pathInfo) {
		HashMap attributes = null;
		if (pathInfo != null && pathInfo.length() > 0) {
			if (attributesMap.containsKey(pathInfo)) {
				attributes = (HashMap) attributesMap.get(pathInfo);
			} else {
				try {
					String fileName = pathInfo.substring(1) + ".properties";
					InputStream in = Thread.currentThread()
					.getContextClassLoader()
					.getResourceAsStream(fileName);
					if (in != null) {
						Properties properties = new Properties();
						properties.load(in);
						attributes = getAttributes(properties);
					}
					attributesMap.put(pathInfo, attributes);
				} catch (Throwable e) {
					// do nothing
				}
			}
		}
		if (attributes == null)
			attributes = (HashMap) attributesMap.get("global");
		return attributes;
	}

	/**
	 * Peform the http GET action. Note that POST is shunted to here as well.
	 * The verb widget is taken from the request and used to invoke an
	 * OAIVerb object of the corresponding kind to do the actual work of the verb.
	 *
	 * @param request the servlet's request information
	 * @param response the servlet's response information
	 * @exception IOException an I/O error occurred
	 */
	public void doGet(HttpServletRequest request,
			HttpServletResponse response)
	throws IOException {
		if (!filterRequest(request, response)) {
			return;
		}
		HashMap attributes = getAttributes(request.getPathInfo());
		
		Properties properties =
			(Properties) attributes.get("OAIHandler.properties");
		boolean monitor = false;
		if (properties.getProperty("OAIHandler.monitor") != null) {
			monitor = true;
		}
		boolean serviceUnavailable = false;
		if (properties.getProperty("OAIHandler.serviceUnavailable") != null) {
			serviceUnavailable = true;
		}
		String extensionPath = properties.getProperty("OAIHandler.extensionPath", "/extension");

		HashMap serverVerbs = ServerVerb.getVerbs(properties);
		HashMap extensionVerbs = ServerVerb.getExtensionVerbs(properties);

		Transformer transformer =
			(Transformer) attributes.get("OAIHandler.transformer");

		boolean forceRender = false;
		if ("true".equals(properties.getProperty("OAIHandler.forceRender"))) {
			forceRender = true;
		}

//		try {
		request.setCharacterEncoding("UTF-8");
//		} catch (UnsupportedEncodingException e) {
//		e.printStackTrace();
//		throw new IOException(e.getMessage());
//		}
		Date then = null;
		if (monitor) then = new Date();
		if (debug) {
			Enumeration headerNames = request.getHeaderNames();
			System.out.println("OAIHandler.doGet: ");
			while (headerNames.hasMoreElements()) {
				String headerName = (String)headerNames.nextElement();
				System.out.print(headerName);
				System.out.print(": ");
				System.out.println(request.getHeader(headerName));
			}
		}
		if (serviceUnavailable) {
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
					"Sorry. This server is down for maintenance");
		} else {
			try {
				String userAgent = request.getHeader("User-Agent");
				if (userAgent == null) {
					userAgent = "";
				} else {
					userAgent = userAgent.toLowerCase();
				}
				Transformer serverTransformer = null;
				if (transformer != null) {

					// return HTML if the client is an old browser
					if (forceRender
							|| userAgent.indexOf("opera") != -1
							|| (userAgent.startsWith("mozilla")
									&& userAgent.indexOf("msie 6") == -1
							/* && userAgent.indexOf("netscape/7") == -1 */)) {
						serverTransformer = transformer;
					}
				}
				String result = getResult(attributes, request, response, serverTransformer, serverVerbs, extensionVerbs, extensionPath);
//				logger.debug("result=" + result);

//				if (serverTransformer) { // render on the server
//				response.setContentType("text/html; charset=UTF-8");
//				StringReader stringReader = new StringReader(getResult(request));
//				StreamSource streamSource = new StreamSource(stringReader);
//				StringWriter stringWriter = new StringWriter();
//				transformer.transform(streamSource, new StreamResult(stringWriter));
//				result = stringWriter.toString();
//				} else { // render on the client
//				response.setContentType("text/xml; charset=UTF-8");
//				result = getResult(request);
//				}

				Writer out = getWriter(request, response);
				out.write(result);
				out.close();
			} catch (FileNotFoundException e) {
				if (debug) {
					e.printStackTrace();
					System.out.println("SC_NOT_FOUND: " + e.getMessage());
				}
				response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
			} catch (TransformerException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (OAIInternalServerError e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (SocketException e) {
				System.out.println(e.getMessage());
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (Throwable e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		if (monitor) {
			StringBuffer reqUri = new StringBuffer(request.getRequestURI().toString());
			String queryString = request.getQueryString();   // d=789
			if (queryString != null) {
				reqUri.append("?").append(queryString);
			}
			Runtime rt = Runtime.getRuntime();
			System.out.println(rt.freeMemory() + "/" + rt.totalMemory() + " "
					+ ((new Date()).getTime()-then.getTime()) + "ms: "
					+ reqUri.toString());
		}
	}

	/**
	 * Override to do any prequalification; return false if
	 * the response should be returned immediately, without
	 * further action.
	 * 
	 * @param request
	 * @param response
	 * @return false=return immediately, true=continue
	 */
	protected boolean filterRequest(HttpServletRequest request,
			HttpServletResponse response) {
		return true;
	}

	public static String getResult(HashMap attributes,
			HttpServletRequest request,
			HttpServletResponse response,
			Transformer serverTransformer,
			HashMap serverVerbs,
			HashMap extensionVerbs,
			String extensionPath)
	throws Throwable {
		try {
			boolean isExtensionVerb = extensionPath.equals(request.getPathInfo());
			String verb = request.getParameter("verb");
			if (debug) {
				System.out.println("OAIHandler.getResult: verb=>" + verb + "<");
			}
			String result;
			Class verbClass = null;
			if (isExtensionVerb) {
				verbClass = (Class)extensionVerbs.get(verb);
			} else {
				verbClass = (Class)serverVerbs.get(verb);
			}
			if (verbClass == null) {
				if (debug) {
					System.out.println("verb not found among:");
					java.util.Iterator keySet = null;
					if (isExtensionVerb) {
						keySet = extensionVerbs.keySet().iterator();
					} else {
						keySet = serverVerbs.keySet().iterator();
					}
					while(keySet.hasNext()) {
						System.out.println(keySet.next());
					}
				}
				result=BadVerb.construct(attributes, request, response, serverTransformer);
			} else {
				Method construct = verbClass.getMethod("construct",
						new Class[] {HashMap.class,
						HttpServletRequest.class,
						HttpServletResponse.class,
						Transformer.class});
				try {
					result = (String)construct.invoke(null,
							new Object[] {attributes,
							request,
							response,
							serverTransformer});
				} catch (InvocationTargetException e) {
					throw e.getTargetException();
				}
			}
			if (debug) {
				System.out.println(result);
			}
			return result;
		} catch (NoSuchMethodException e) {
			throw new OAIInternalServerError(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new OAIInternalServerError(e.getMessage());
		}
	}
	
	/**
	 * Get a response Writer depending on acceptable encodings
	 * @param request the servlet's request information
	 * @param response the servlet's response information
	 * @exception IOException an I/O error occurred
	 */
	public static Writer getWriter(HttpServletRequest request, HttpServletResponse response)
	throws IOException {
		Writer out;
		String encodings = request.getHeader("Accept-Encoding");
		if (debug) {
			System.out.println("encodings=" + encodings);
		}
		if (encodings != null && encodings.indexOf("gzip") != -1) {
//			System.out.println("using gzip encoding");
//			logger.debug("using gzip encoding");
			response.setHeader("Content-Encoding", "gzip");
			out = new OutputStreamWriter(new GZIPOutputStream(response.getOutputStream()),
			"UTF-8");
//			} else if (encodings != null && encodings.indexOf("compress") != -1) {
//			//  	    System.out.println("using compress encoding");
//			response.setHeader("Content-Encoding", "compress");
//			ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
//			zos.putNextEntry(new ZipEntry("dummy name"));
//			out = new OutputStreamWriter(zos, "UTF-8");
		} else if (encodings != null && encodings.indexOf("deflate") != -1) {
//			System.out.println("using deflate encoding");
//			logger.debug("using deflate encoding");
			response.setHeader("Content-Encoding", "deflate");
			out = new OutputStreamWriter(new DeflaterOutputStream(response.getOutputStream()),
			"UTF-8");
		} else {
//			logger.debug("using no encoding");
			out = response.getWriter();
		}
		return out;
	}

	/**
	 * Peform a POST action. Actually this gets shunted to GET
	 *
	 * @param request the servlet's request information
	 * @param response the servlet's response information
	 * @exception IOException an I/O error occurred
	 */
	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
	throws IOException {
		doGet(request, response);
	}
}

