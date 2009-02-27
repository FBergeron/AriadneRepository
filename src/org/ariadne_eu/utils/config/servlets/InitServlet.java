/**
 * 
 */
package org.ariadne_eu.utils.config.servlets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ariadne.config.PropertiesManager;
import org.ariadne_eu.metadata.insert.InsertMetadataFactory;
import org.ariadne_eu.metadata.query.QueryMetadataFactory;
import org.ariadne_eu.metadata.query.language.TranslateLanguage;
import org.ariadne_eu.utils.config.RepositoryConstants;

/**
 * @author gonzalo
 * 
 */
public class InitServlet extends HttpServlet {
	
	protected static String dataDir = "";
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException {

	}
	
	public static void initializeServices() {
        //TODO
        TranslateLanguage.initialize();
        InsertMetadataFactory.initialize();
        QueryMetadataFactory.initialize();
    }
	
	public static void initializePropertiesManager(){
		try {
			PropertiesManager.init();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public void init() throws ServletException {
		try {
			System.out.println(getServletContext().getRealPath("install")+ File.separator + "ariadne.properties");
			PropertiesManager.setPropertiesFile(getServletContext().getRealPath("install")+ File.separator + "ariadne.properties");
			if (PropertiesManager.getPropertiesFile().exists()){
				PropertiesManager.init();
//				dataDir = PropertiesManager.getProperty(RepositoryConstants.REPO_DATADIR);
//			    if(!dataDir.equals("")){
//			    	System.setProperty(RepositoryConstants.REPO_DATADIR, dataDir);
//			    }
//			    else {
//			    	throw new ServletException();
//			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
