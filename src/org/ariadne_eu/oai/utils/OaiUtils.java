package org.ariadne_eu.oai.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class OaiUtils {
	
	public static final String DEFAULT_ENCODING = "UTF-8";

//	private static LOMMarshaller marshaller = null;
	
//	private static boolean lom2XmlstringparserInited = false;
//	
//	private static boolean XmlString2LomparserInited = false;
//	
//	private static Unmarshaller unmarshaller = null;
	
	private static Calendar starttime = null;

	public static void begin(){
		starttime = new GregorianCalendar();
	}

	public static void end(){
		GregorianCalendar endtime = new GregorianCalendar();
		long difference = endtime.getTimeInMillis() - starttime.getTimeInMillis();
		int mins = (int)Math.floor(difference/60000.0);
		double secs = (difference/1000.0 - mins*60.0); 
		System.out.println(mins + " m " + secs + " s");
	}
	
//    public static LOMImpl parseXmlString2Lom(String lomString) throws JAXBException {
//    	if(!XmlString2LomparserInited)initXmlString2Lomparser();
//    	org.xml.sax.InputSource isource = new org.xml.sax.InputSource (new java.io.StringReader(lomString));
//        return (LOMImpl) unmarshaller.unmarshal(isource);
//    }
    
//    private static void initXmlString2Lomparser(){
//    	try{
//        	JAXBContext jaxbContext = JAXBContext.newInstance("org.ieee.ltsc.lom.jaxb.lomxml");
//    		unmarshaller = jaxbContext.createUnmarshaller ();
//    		XmlString2LomparserInited = true;
//    	}
//    	catch (JAXBException e) {
//			// TODO: handle exception
//		}
//    }
    
//	public static LOM parseNodeToLom(Node n, String oai_id) throws JAXBException {
//		String xmlString = OaiUtils.getXML(n);
//		LOMImpl lom = OaiUtils.parseXmlString2Lom(xmlString);
//		return lom;
//
//	}
    
//    public static Document parseXmlStringToDom(String xml) throws DocumentException {
//        SAXReader reader = new SAXReader();
//        Document document = reader.read(new ByteArrayInputStream(xml.getBytes()));
//        return document;
//    }
//    
//	public static HashMap getKeyValuesOfNode(Element node){
//		HashMap hashmap = new HashMap();
//		List elements = node.content();
//		for(int i =0; i < elements.size(); i++){
//			Element el = (Element) elements.get(i);
//			hashmap.put(el.getName(), el.getStringValue());
//		}
//		return hashmap;
//	}
//    
//	public static List query(Element doc, String xPath){
//		System.out.println(doc.asXML());
//		HashMap map = new HashMap();
//		  map.put( "lom", "http://ltsc.ieee.org/xsd/LOM");
//		  
//		  List nodes = null;
//		  Dom4jXPath xpath;
//		try {
//			
//			xpath = new Dom4jXPath(xPath.replaceAll("/", "/lom:"));
//			xpath.setNamespaceContext( new SimpleNamespaceContext( map));
//			
//			nodes = xpath.selectNodes(doc.getDocument());
//			
//		} catch (JaxenException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		return nodes;
//	}
    
//    private static void initLom2Xmlstringparser(){
//    	try{
//            final JAXBContext jaxbContext = JAXBContext.newInstance(System.getProperty(LOM_JAXBContext.CONTEXT_PATH_PROP, LOM_JAXBContext.DEFAULT_CONTEXT_PATH));
//            marshaller = (LOMMarshaller) jaxbContext.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_ENCODING, DEFAULT_ENCODING);
//            marshaller.setPrintSchemaLocation(true);
//            marshaller.setPrintXMLDecl(true);
//            marshaller.setDeclareNamespace(true);
//            lom2XmlstringparserInited = true;
//    	}
//    	catch (JAXBException e) {
//			// TODO: handle exception
//		}
//
//    }
//    
//    public static String parseLom2Xmlstring(LOM lom) throws JAXBException{
//    	if(!lom2XmlstringparserInited)initLom2Xmlstringparser();
//        java.io.StringWriter strW = new java.io.StringWriter();
//        StreamResult strResult = new StreamResult(strW);
//        marshaller.marshal(lom, strResult);
//        String result = null;
//        try {
//            strW.close();
//            result = strW.toString();
//        } catch (IOException e) {
//        }
//        // REMARK: the schema and namespace declarations don't seem to be included after the marshalling (=error in the marshaller?),
//        // so therefore we manually add it...
//        // TODO: find out how to solve it in the marshaller
//        //result = result.replaceFirst("<lom xmlns=\"http://ltsc.ieee.org/xsd/LOM\">", "<lom xmlns:xsd=\"http://ltsc.ieee.org/xsd/LOM\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/20040413/lom.xsd\">");
//        result = result.replaceFirst("<lom>", "<lom xmlns=\"http://ltsc.ieee.org/xsd/LOM\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/20040413/lom.xsd\">");
//        return result;
//    }
    
//    public static String addNs(String xml) {
//    			return xml.replaceFirst("xmlns=", "xmlns:xsd=");
//	}
//    
//    public static String removeNs(String xml) {
//		return xml.replaceFirst("xmlns:xsd=", "xmlns=");
//}
    
    public static String getXML(Node n) {
        String result = null;
        try {
            Writer stringWriter = new StringWriter();
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(n);
            StreamResult streamResult = new StreamResult(stringWriter);
            transformer.transform(source, streamResult);
            result = stringWriter.toString().substring(38);
        } catch (TransformerException ex) {
            ex.printStackTrace();
        } catch (TransformerFactoryConfigurationError ex) {
            ex.printStackTrace();
        }
        return result;
    }
    
    public static java.util.Date parseStringToDate(String dateString) {
        java.text.SimpleDateFormat dfparser;
        dateString = dateString.replaceAll("th", "");
        dateString = dateString.replaceAll ("nd", "");
        dateString = dateString.replaceAll("\\n", " ");
        dateString = dateString.replaceAll("\\r", " ");
        dateString = dateString.replaceAll (System.getProperty("line.separator"), " ");
        // Replace 2 or more spaces by 1 space, because otherwise the parsing will fail.
        dateString = dateString.replaceAll("\\s{2,}", " ");
        try {
            // The one used by the LOM Java API
            dfparser = new java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", java.util.Locale.US );
            return dfparser.parse(dateString);
        }
        catch (ParseException e) {
            //
        }
        try {
            // The one used by java.util.Date toString method
            dfparser = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", java.util.Locale.US);
            return dfparser.parse(dateString);
        }
        catch (ParseException e) {
            //
        }
        try {
            dfparser = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            return dfparser.parse(dateString);
        } catch (ParseException e) {
            //
        }
        try {
            dfparser = new java.text.SimpleDateFormat("yyyy-MM-dd");
            return dfparser.parse(dateString);
        } catch (ParseException e) {
            //
        }
        try {
            dfparser = new java.text.SimpleDateFormat("MMM dd, yyyy");
            return dfparser.parse(dateString);
        } catch (ParseException e) {
            //
        }
        try {
            dfparser = new java.text.SimpleDateFormat("MM-dd.yy");
            return dfparser.parse(dateString);
        } catch (ParseException e) {
            //
        }
        try {
            dfparser = new java.text.SimpleDateFormat("dd MMM yyyy");
            return dfparser.parse(dateString);
        } catch (ParseException e) {
            //
        }
        try {
            dfparser = new java.text.SimpleDateFormat("dd.MM.yy");
            return dfparser.parse(dateString);
        } catch (ParseException e) {
            //
        }
        try {
            dfparser = new java.text.SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            return dfparser.parse(dateString);
        } catch (ParseException e) {
            //
        }
        try {
            dfparser = new java.text.SimpleDateFormat("E M dd hh:mm:ss z yyyy");
            return dfparser.parse(dateString);
        } catch (ParseException e) {
            //
        }
        // If all attempts fail, just return null.
        return null;
    }
	
	private static Validator validator = null;
	
	private static void initLomValidator(){
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

        URL schemaLocation;
		try {
			schemaLocation = new URL("http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd");
	        Schema schema = factory.newSchema(schemaLocation);
	        validator = schema.newValidator();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static String validateLomXml(String xml){
        if(validator == null)initLomValidator();
        Source source = new StreamSource(new StringReader(xml));
        try {
            validator.validate(source);
            return "";
        }
        catch (SAXException ex) {
            return "Document is not valid because " + ex.getMessage();
        } catch (IOException e) {
        	return "Document is not valid because " + e.getMessage();
		}
	}
	
	public static String calcUntil(){

		Calendar today = GregorianCalendar.getInstance();
		int nowYear = today.get(Calendar.YEAR);
		int month = today.get(Calendar.MONTH) + 1;
		String nowMonth = "";
		if(month < 10){
			nowMonth = "0" + month;
		}
		else{
			nowMonth = Integer.toString(month);
		}
		
		int day = today.get(Calendar.DAY_OF_MONTH);
		String nowDay = "";
		if(day < 10){
			nowDay = "0" + day;
		}
		else{
			nowDay = Integer.toString(day);
		}
		
		return nowYear + "-" + nowMonth + "-" + nowDay;

	}
	
}
