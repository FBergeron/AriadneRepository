package org.ariadne_eu.utils.registry;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;

public class MetadataCollection{
	Identifier _identifier;
	Description _description;
	List<TargetDescription> _target;
	
	public MetadataCollection(){
		_identifier = new Identifier();
		_description = new Description();
		_target=new ArrayList<TargetDescription>();
	}
	
	public MetadataCollection(Identifier identifier, Description description){
		_description=description;
		_identifier=identifier;
	}
	
	public void setIdentifier(Identifier identifier){
		_identifier=identifier;		
	}
	
	public void setDescription(Description description){
		_description=description;
	}
	
	public Identifier getIdentifier(){
		return _identifier;
	}
	
	public Description getDescription(){
		return _description;
	}
	
	public void addTarget(TargetDescription targetDescription){
		_target.add(targetDescription);
	}
	
	public List<TargetDescription> getTarget(){
		return _target;
	}
	
	public void parseXMLMetadataCollection(Element metadataCollection,Namespace ns){
		_identifier.parseXMLIdentifier(metadataCollection.getChild("identifier",ns), ns);
		_description.parseXMLDescription(metadataCollection.getChild("description",ns), ns);
		List<Element> targets = metadataCollection.getChildren("target",ns);
		for (int i=0;i<targets.size();i++){
			TargetDescription targetDescription = new TargetDescription();
			targetDescription.parseXMLTargetDescription(((org.jdom.Element) targets.get(i)).getChild("targetDescription",ns), ns);
			_target.add(targetDescription);
		}
		
	}
}
