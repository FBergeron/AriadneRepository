package org.celstec.ariadne.app;

import org.purl.sword.base.Collection;
import org.purl.sword.base.Service;
import org.purl.sword.base.ServiceDocument;
import org.purl.sword.base.Workspace;
import org.ariadne.config.PropertiesManager;


public class AriadneServiceDocument extends ServiceDocument{
	
	public AriadneServiceDocument(){
		Service service = new Service("1.3",true, true);
		service.addWorkspace(getWorkspace());
		setService(service);
		
	}
	
	private Workspace getWorkspace(){
		Workspace workspace = new Workspace();
		workspace.setTitle(PropertiesManager.getProperty("app.workspace.title"));
		workspace.addCollection(getCollection());

		return workspace;
	}
	
	private Collection getCollection(){
		SpiCollection collection = new SpiCollection();
		collection.setTitle(PropertiesManager.getProperty("app.collection.title"));
		String swordBaseURL = PropertiesManager.getProperty("app.baseURL");
		while (swordBaseURL.endsWith("/"))
			swordBaseURL = swordBaseURL.substring(0, swordBaseURL.length() - 1);
		collection.setLocation(swordBaseURL + "/deposit");
		 if(PropertiesManager.getProperty("app.publishMetadata").equalsIgnoreCase("yes")) collection.setPublishMetadata(true);
			if (PropertiesManager.getProperty("app.publishMetadata").equalsIgnoreCase("no")) collection.setPublishMetadata(false);
			collection.addMetadataschema("http://ltsc.ieee.org/xsd/LOM/loose");
			collection.addMetadataschema("http://www.share-tec.eu/validation/ShareTEC/minimal");
				
		return collection;
	}
	
	

}
