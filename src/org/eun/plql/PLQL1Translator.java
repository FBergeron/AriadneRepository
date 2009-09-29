/*
Copyright (C) 2006  David Massart and Chea Sereyvath, European Schoolnet

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

package org.eun.plql ;

import java.io.Reader;
import java.io.StringReader;

import org.eun.plql.layer1.PlqlLayer1Analyzer;

public class PLQL1Translator 
{

//	private String plqlQuery = "hello and \" my name is /// ) \\\"sereyvath\" and dc.title = \"SQL\" and lom.generate.title = \"SQL\" and lom.title = 1299 " ;
//	private String plqlQuery = "lom.generate.title = \"SQL dgddd )(fhfh\" and lom.general = 123 and lom.test = .0121 and test " ;
//	private String plqlQuery = " E and ((A and B) and (C and D) )";
//  private String plqlQuery = "test and ((lom.title = abc) and (lom.language=\"fr\")) " ;
// s2ql equivalent  
//private static String plqlQuery = "keyword1 and keyword2 and (lom.geNeral.lanGuage = \"fr\" ) and (lom.educaTional.ageRange=10-12) " ;
	
	//MACE
//	private static String plqlQuery = "lom.general.identifier.entry = \"14698\"";
//	private static String plqlQuery = "lom.general.identifier.entry = \"oai:winds.gmd.de:14698\"";
//	private static String plqlQuery = "villa and lom.general.identifier.entry=\"oai\"";
//	private static String plqlQuery =  "lom.general.identifier.entry=\"oai:dynamo.asro.kuleuven.be\" and lom.general.identifier.entry=\"oai:winds.gmd.de\"";
//	private static String plqlQuery =  "lom.general.identifier.entry=\"oai:dynamo.asro.kuleuven.be\"";
//	private static String plqlQuery =  "lom.classification.purpose.value=\"d.2.4 materials\" and lom.classification.taxonpath.taxon.entry.string=\"wood\"";
//	private static String plqlQuery =  "lom.educational.learningResourceType.value=\"narrative text\"";//12416
//	private static String plqlQuery =  "lom.classification.purpose.architecturalandartistictrends = \"Architectural trends . contemporary architecture\"";
//	private static String plqlQuery = "lom.technical.format = \"application/msword\"";
//	private static String plqlQuery = "lom.classification.taxonPath.source.string.ARIADNE = \"medicina\"";
//	private static String plqlQuery = "lom.general.identifier.entry=\"CumincadWorks.id\" AND lom.educational.language=\"it\"";
//	private static String plqlQuery = "xmlns = \"http://ltsc.ieee.org/xsd/lom\"";
	private static String plqlQuery = "lom.description.metadata.lom.general.language=\"nl\"";

    public static void main(String[] args) throws Exception
    {
        System.out.println(new PLQL1Translator().transformQueryToLuceneQL(plqlQuery));
    }
    
    /**
     * 
     */ 
    public String transformQueryToLuceneQL(String plqlQuery) {
        Reader r = new StringReader( plqlQuery ) ;
        PlqlLayer1Analyzer parser = new PlqlLayer1Analyzer( r ) ;
        parser.parse() ;
		return parser.getQuery();	        
    }

}
