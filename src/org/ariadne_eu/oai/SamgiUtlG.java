package org.ariadne_eu.oai;
//package org.ariadne.oai;
//
//import java.io.IOException;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.io.Writer;
//import java.text.ParseException;
//
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Marshaller;
//import javax.xml.bind.Unmarshaller;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.TransformerFactoryConfigurationError;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//
//import org.dom4j.DocumentException;
//import org.dom4j.io.SAXReader;
//import org.ieee.ltsc.lom.LOM;
//import org.ieee.ltsc.lom.impl.LOMImpl;
//import org.ieee.ltsc.lom.jaxb.lomxml.LOMMarshaller;
//import org.ieee.ltsc.lom.jaxb.lomxml.LOM_JAXBContext;
//import org.w3c.dom.Node;
//
//public final class SamgiUtlG {
//
//	public static final String DEFAULT_ENCODING = "UTF-8";
//
//	private static LOMMarshaller marshaller = null;
//	
//	private static boolean inited = false;
//	
//    public static LOMImpl parseXmlString2Lom(String lomString) throws JAXBException {
//        org.xml.sax.InputSource isource = new org.xml.sax.InputSource (new java.io.StringReader(lomString));
//        final JAXBContext jaxbContext = JAXBContext.newInstance("org.ieee.ltsc.lom.jaxb.lomxml");
//        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller ();
//        return (LOMImpl) unmarshaller.unmarshal(isource);
//    }
//
//    private static void initLom2Xmlstringparser() throws JAXBException{
//        final JAXBContext jaxbContext = JAXBContext.newInstance(System.getProperty(LOM_JAXBContext.CONTEXT_PATH_PROP, LOM_JAXBContext.DEFAULT_CONTEXT_PATH));
//        marshaller = (LOMMarshaller) jaxbContext.createMarshaller();
//        marshaller.setProperty(Marshaller.JAXB_ENCODING, DEFAULT_ENCODING);
//        marshaller.setPrintSchemaLocation(true);
//        marshaller.setPrintXMLDecl(true);
//        marshaller.setDeclareNamespace(true);
//        inited = true;
//    }
//    
//    public static String parseLom2Xmlstring(LOM lom) throws JAXBException {
//    	if(!inited)initLom2Xmlstringparser();
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
//        result = result.replaceFirst("<lom>", "<lom xmlns=\"http://ltsc.ieee.org/xsd/LOM\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/20040413/lom.xsd\">");
//        return result;
//    }
//    
//    public static String getXML(Node n) {
//        String result = null;
//        try {
//            Writer stringWriter = new StringWriter();
//            TransformerFactory tFactory = TransformerFactory.newInstance();
//            Transformer transformer = tFactory.newTransformer();
//            DOMSource source = new DOMSource(n);
//            StreamResult streamResult = new StreamResult(stringWriter);
//            transformer.transform(source, streamResult);
//            result = stringWriter.toString().substring(38);
//        } catch (TransformerException ex) {
//            ex.printStackTrace();
//        } catch (TransformerFactoryConfigurationError ex) {
//            ex.printStackTrace();
//        }
//        return result;
//    }
//    
//    public static java.util.Date parseStringToDate(String dateString) {
//        java.text.SimpleDateFormat dfparser;
//        dateString = dateString.replaceAll("th", "");
//        dateString = dateString.replaceAll ("nd", "");
//        dateString = dateString.replaceAll("\\n", " ");
//        dateString = dateString.replaceAll("\\r", " ");
//        dateString = dateString.replaceAll (System.getProperty("line.separator"), " ");
//        // Replace 2 or more spaces by 1 space, because otherwise the parsing will fail.
//        dateString = dateString.replaceAll("\\s{2,}", " ");
//        try {
//            // The one used by the LOM Java API
//            dfparser = new java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", java.util.Locale.US );
//            return dfparser.parse(dateString);
//        }
//        catch (ParseException e) {
//            //
//        }
//        try {
//            // The one used by java.util.Date toString method
//            dfparser = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", java.util.Locale.US);
//            return dfparser.parse(dateString);
//        }
//        catch (ParseException e) {
//            //
//        }
//        try {
//            dfparser = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//            return dfparser.parse(dateString);
//        } catch (ParseException e) {
//            //
//        }
//        try {
//            dfparser = new java.text.SimpleDateFormat("yyyy-MM-dd");
//            return dfparser.parse(dateString);
//        } catch (ParseException e) {
//            //
//        }
//        try {
//            dfparser = new java.text.SimpleDateFormat("MMM dd, yyyy");
//            return dfparser.parse(dateString);
//        } catch (ParseException e) {
//            //
//        }
//        try {
//            dfparser = new java.text.SimpleDateFormat("MM-dd.yy");
//            return dfparser.parse(dateString);
//        } catch (ParseException e) {
//            //
//        }
//        try {
//            dfparser = new java.text.SimpleDateFormat("dd MMM yyyy");
//            return dfparser.parse(dateString);
//        } catch (ParseException e) {
//            //
//        }
//        try {
//            dfparser = new java.text.SimpleDateFormat("dd.MM.yy");
//            return dfparser.parse(dateString);
//        } catch (ParseException e) {
//            //
//        }
//        try {
//            dfparser = new java.text.SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
//            return dfparser.parse(dateString);
//        } catch (ParseException e) {
//            //
//        }
//        try {
//            dfparser = new java.text.SimpleDateFormat("E M dd hh:mm:ss z yyyy");
//            return dfparser.parse(dateString);
//        } catch (ParseException e) {
//            //
//        }
//        // If all attempts fail, just return null.
//        return null;
//    }
//
//	public static org.dom4j.Document parse(String lom) throws DocumentException {
//		SAXReader reader = new SAXReader();
//		org.dom4j.Document document = reader.read(new StringReader(lom));
//		return document;
//	}
//
//}
