package org.celstec.ariadne.app;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


//Implement!!

public class SpiGateway {

	public String submitMetadata(String authorizationToken, String metadataIdentifier, String resourceIdentifier, Element metadataInstance, String metadataSchemaId, String collection) {
		System.out.println("following md is submitted to repository");
		System.out.println(authorizationToken);
		System.out.println(metadataIdentifier);
		XMLOutputter out = new XMLOutputter();
		out.setFormat(Format.getPrettyFormat());
		System.out.println(out.outputString(metadataInstance));
		System.out.println(collection);
		return "aGeneratedMetadataIdentifier";
	}

	public String submitResource(String authorizationToken, String resourceIdentifier, Object resource, String packageType, String contentType, String collection, String filename) {
		System.out.println("following resource is submitted to repository");
		System.out.println("authorizationToken: "+authorizationToken);
		System.out.println("resourceIdentifier: "+resourceIdentifier);
		System.out.println("package type: "+packageType);
		System.out.println("content type: "+contentType);
		System.out.println("collection: "+collection);
		System.out.println("filename: "+filename);

		

		return null;
	}
	
	public static String generateIdentifier(){
		return "identifier";
	}

}
