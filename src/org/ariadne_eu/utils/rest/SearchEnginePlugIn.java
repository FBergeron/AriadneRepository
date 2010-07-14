package org.ariadne_eu.utils.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SearchEnginePlugIn {

	
	/**
	 * 
	 * @param arg An array of Strings that is used for initialising the Search Engine PlugIn
	 * @throws Exception
	 */
	public abstract void initialise(String[] arg)  throws Exception ;

	public abstract void search(HttpServletRequest request, HttpServletResponse response);

}