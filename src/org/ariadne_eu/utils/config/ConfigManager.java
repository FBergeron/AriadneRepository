package org.ariadne_eu.utils.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import org.ariadne.config.PropertiesManager;

/**
 * <p>Company: K.U.Leuven</p>
 * @author Stefaan Ternier
 * @version 1.0
 */

public class ConfigManager {

	private static Properties $properties;
//	private static String $propFile = System.getProperty("user.dir") + File.separator + "ariadne.properties";
	private static String $propFile = PropertiesManager.getPropFile();
	static {
		try {
			init();
		} catch (IOException e) {
			// From within a web app default initialisation will not succeed...
			// Because an exception occurred, we will re-initialize the properties
			if ($properties != null) { $properties = null; }
		} finally {
			if ($properties == null) {
				$properties = new Properties();
				$properties = loadPropertiesFromClasspath("ariadne.properties");
			}
		}
	}


//	public static void setPropertiesFile(String file) throws IOException {
//	$propFile = file;
////	File propFile = new File($propFile);
////	if (!propFile.exists()) initNewAriadneV4PropFile(propFile);
//	$properties = new TranslationProperties();
//	init(file);
//	FileInputStream fis = new FileInputStream($propFile);
//	$properties.load(fis);
//	fis.close();
//	}

	public static void setKPSConfiguration(String config) throws IOException {
		init(new ByteArrayInputStream(config.getBytes()));
	}

	public static void init(ByteArrayInputStream inputstream) throws IOException {
		if ($properties == null)
			$properties = new Properties();
		else
			$properties.clear();
		$properties.load(inputstream);
		inputstream.close();
	}

	public static void removeKeyFromPropertiesFile(String key) {
		try {
			File inFile=new File($propFile);
			InputStreamReader inStream=new InputStreamReader(new FileInputStream(inFile),"UTF-8");
			BufferedReader stdin =new BufferedReader(inStream);
			File outFile=new File($propFile+".new");
			FileOutputStream fos= new FileOutputStream(outFile);
			Writer out = new OutputStreamWriter(fos, "UTF-8");
			String line = stdin.readLine();
			while(line != null) {
				if (!line.startsWith(key)) {
					out.write((line + "\n"));
				}
				line = stdin.readLine();
			}
			out.close();
			fos.close();
			stdin.close();
			copyFile(outFile, inFile);
			$properties.remove(key);
		}catch (Exception e){e.printStackTrace();}
	}

//	public static void removeKeyFromPropertiesFile(String key) {
//	try {
//	File inFile=new File($propFile);
//	InputStreamReader inStream=new InputStreamReader(new FileInputStream(inFile),"UTF-8");
//	BufferedReader stdin =new BufferedReader(inStream);
//	File outFile=new File($propFile+".new");
//	FileOutputStream fos= new FileOutputStream(outFile);
//	Writer out = new OutputStreamWriter(fos, "UTF-8");
//	String line ="";
//	while(line != null) {
//	line = stdin.readLine();
//	if  (line!=null){
//	if (line.startsWith(key)) {
//	}
//	else {
//	out.write((line + "\n"));
//	}
//	}
//	}
//	out.close();
//	fos.close();
//	stdin.close();
//	copyFile(outFile, inFile);
//	$properties.remove(key);
//	}catch (Exception e){e.printStackTrace();}
//	}

	/**
	 * This propertiesfile is used but not necessarily loaded.
	 *
	 * @return string representations of location of the properties file that is used
	 */
	public static String getPropFile(){
		return $propFile;
	}

	public static void init() throws FileNotFoundException, IOException {
		init($propFile);
	}

	/**
	 * This method initializes the properties from a give file.
	 *
	 * @param fileName The file that contains the properties.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void init(String fileName) throws FileNotFoundException, IOException {
		$propFile = fileName;
		init(new FileInputStream(fileName));
	}

	public static void init(File file) throws FileNotFoundException, IOException {
		$propFile = file.toString();
		FileInputStream fis = new FileInputStream(file);
		init(fis);
		fis.close();
	}

	public static void setPropertiesFile(String fileName){
		$propFile = fileName;
	}

	public static File getPropertiesFile(){
		return new File($propFile);
	}

	public static void init(FileInputStream fis) throws IOException {
		if ($properties == null)
			$properties = new Properties();
		else
			$properties.clear();
		$properties.load(fis);
		fis.close();
	}

//	public static void initAriadneV4fromTemplate(File source) throws Exception {
//	if (source.exists()) copyFile(source, new File($propFile));
//	}

//	private static void copyFile(File source, File dest) {
//	try {
//	DataHandler dh = new DataHandler(new FileDataSource(source));
//	FileOutputStream fos = new FileOutputStream(dest);
//	dh.writeTo(fos);
//	fos.flush();
//	fos.close();
//	} catch (IOException ex) {
//	ex.printStackTrace();
//	}
//	}

	private static void copyFile(File in, File out) throws Exception {
		FileInputStream fis  = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		byte[] buf = new byte[1024];
		int i = 0;
		while((i=fis.read(buf))!=-1) {
			fos.write(buf, 0, i);
		}
		fis.close();
		fos.close();
	}    

	/**
	 * Searches for the property with the specified key in this property list.
	 * If the key is not found in this property list, the default property list,
	 * and its defaults, recursively, are then checked. The method the given defaultValue
	 * if the property is not found.
	 *
	 * @param   key   the property key.
	 * @param   defaultValue   the default value to be returned if there was no other value found
	 * @return  the value in this property list with the specified key value.
	 *
	 */
	public static String getProperty(String key, String defaultValue) {
		try {
			return getProperty(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Searches for the property with the specified key in this property list.
	 * If the key is not found in this property list, the default property list,
	 * and its defaults, recursively, are then checked. The method throws an IllegalArgException
	 * if the property is not found.
	 *
	 * @param   key   the property key.
	 * @return  the value in this property list with the specified key value.
	 *
	 */
	public static String getProperty(String key) {
		if ($properties == null)
			throw new RuntimeException(
					"The properties file is not initialised correctly. Probably "
							+ $propFile + " does not exist.");
		String result = $properties.getProperty(key);
		//gap: I need to return null to make work the loading languages and stuff
		if (result == null)
			return null;
//			throw new IllegalArgumentException("The value of this key(" + key + ") is not available in '" + $propFile + "'");
		return result.trim();
	}

	/**
	 * This method returns a list of all properties that start with the given String.
	 *
	 * @param key a String (e.g. "kps", "gen", "tm5", ...)
	 * @return a Hashtable containing all TranslationProperties that start with a certain String
	 */
	public static Hashtable getPropertyStartingWith(String key) {
		Hashtable result = new Hashtable();
		Iterator it = $properties.keySet().iterator();
		while (it.hasNext()) {
			String k = (String) it.next();
			if (k.startsWith(key)) {
				result.put(k, $properties.getProperty(k));
			}
		}
		return result;
	}

	public synchronized static void saveProperty(String key, String value) {
		try {
			$properties.put(key, value);
			boolean entryExists = updatePropertiesFile(key, value);
			if (!entryExists) addKeyToPropertiesFile(key, value);
			File newFile = new File($propFile + ".new");
			File oldFile = new File($propFile);
			if (newFile.exists()) {
				oldFile.delete();
				newFile.renameTo(oldFile);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static boolean updatePropertiesFile(String key, String value) {
		boolean result = false;
		try {
			FileInputStream fileInputStream = new FileInputStream($propFile);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF8");
			BufferedReader in = new BufferedReader(inputStreamReader);
			File newFile = new File($propFile + ".new");
			if (newFile.exists()) {
				newFile.delete();
			}
			StringBuffer buffer = new StringBuffer();
			String line;
			int i = 0;
			while ((line = in.readLine())!= null) {
				if (line.startsWith(key+" ") ||line.startsWith(key+"=")) {
					buffer.append(key + " = " + value + "\n");
					result = true;
				} else {
					buffer.append(line + "\n");
				}
			}
			in.close();
			inputStreamReader.close();
			fileInputStream.close();
			FileOutputStream fos = new FileOutputStream(newFile);
			Writer out = new OutputStreamWriter(fos, "UTF-8");
			out.write(buffer.toString());
			out.close();
			fos.close();
		} catch (IOException ex) {
			if (getPropFile().indexOf(".jar!") != -1) {
				// This means the ariadneV4.properties was loaded as a resource from a jar in the classpath.
				// Therefore, we will never be able to update this file. Therefore we don't print out an error message
				// as it is expected behaviour.
			}
			else {
				ex.printStackTrace();
			}
		}
		return result;
	}

	private static void addKeyToPropertiesFile(String key, String value) {
		try {
			FileInputStream fileInputStream = new FileInputStream($propFile);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF8");
			BufferedReader in = new BufferedReader(inputStreamReader);
			Vector<String> props = new Vector<String>();
			File newFile = new File($propFile + ".new");
			if (newFile.exists()) {
				boolean result =newFile.delete();
			}
			StringBuffer buffer = new StringBuffer();
			String line;
			int i = 0;
			int lastPosition = -1;
			while ((line = in.readLine())!= null) {
				i++;
				if (getType(line).equals(getType(key))) lastPosition = i;
				props.add(line);
			}
			in.close();
			inputStreamReader.close();
			fileInputStream.close();
			i = 0;
			for(String propLine : props) {
				i++;
				buffer.append(propLine + "\n");
				if (i == lastPosition) buffer.append(key + " = " + value + "\n");
			}
			if (lastPosition == -1) buffer.append(key + " = " + value + "\n");
			FileOutputStream fos = new FileOutputStream(newFile);
			Writer out = new OutputStreamWriter(fos, "UTF-8");
			out.write(buffer.toString());
			out.close();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static String getType(String key) {
		if (key.trim().startsWith("#")) return "comment";
		int indexToekenning = key.indexOf("=");
		int indexPunt = key.indexOf(".");
		if (indexPunt == -1 && indexToekenning != -1) return key.substring(0, indexToekenning);
		int index = Math.max(indexToekenning, indexPunt);
		if (index == -1) return key;
		return key.substring(0, indexPunt);
	}


	public static Set getTypes() {
		Iterator enumer = $properties.entrySet().iterator();
		HashSet types = new HashSet();
		while (enumer.hasNext()) {
			String entry = (String) ((Map.Entry) enumer.next()).getKey();
			if (entry.indexOf(".") != -1) {
				types.add(entry.substring(0, entry.indexOf(".")));
			}
		}
		return types;
	}

//	public static void main(String[] args) throws Exception {
//	//System.out.println("gen.conf " +getProperty(RepositoryConstants.CNT_DR_BASEPATH));
//	init($propFile+"2");
//	System.out.println("gen.conf " +getProperty(RepositoryConstants.CNT_DR_BASEPATH));
//	}


	/**
	 * Looks up a resource named 'name' in the classpath. The resource must map
	 * to a file with one of the extensions in the SUFFIX array of strings.
	 * The name is assumed to be absolute
	 * and can use either "/" or "." for package segment separation with an
	 * optional leading "/" and optional suffix in the SUFFIX array of strings.
	 * Thus, suppose .properties is in the SUFFIX array,
	 * the following names refer to the same resource:
	 * <pre>
	 * some.pkg.Resource
	 * some.pkg.Resource.properties
	 * some/pkg/Resource
	 * some/pkg/Resource.properties
	 * /some/pkg/Resource
	 * /some/pkg/Resource.properties
	 * </pre>
	 * This method is largely based on what can be found at
	 * <a href="http://www.javaworld.com/javaqa/2003-08/01-qa-0808-property_p.html">http://www.javaworld.com/javaqa/2003-08/01-qa-0808-property_p.html</a>
	 * A small adaptation was made, to not only being able to deal with properties files with .properties extension,
	 * but also eg files with .txt extension
	 * Also see <a href="http://www.javaworld.com/columns/jw-qna-index.shtml">Vlad Roubtsov</a>, 2003
	 *
	 * @param name classpath resource name [may not be null]
	 * @return resource converted to java.util.Properties [may be null if the
	 *         resource was not found and THROW_ON_LOAD_FAILURE is false]
	 * @throws IllegalArgumentException if the resource was not found and
	 *                                  THROW_ON_LOAD_FAILURE is true
	 */
	public static Properties loadPropertiesFromClasspath(String name) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		boolean THROW_ON_LOAD_FAILURE = true;
		boolean LOAD_AS_RESOURCE_BUNDLE = false;
		String[] POSSIBLE_SUFFIXES = {".properties"};
		String suffix = "";
		if (name == null)
			throw new IllegalArgumentException("null input: name");
		if (name.startsWith("/"))
			name = name.substring(1);
		for (int i = 0; i < POSSIBLE_SUFFIXES.length; i++) {
			String possibleSuffix = POSSIBLE_SUFFIXES[i];
			if (name.endsWith(possibleSuffix)) {
				suffix = possibleSuffix;
				name = name.substring(0, name.length() - possibleSuffix.length());
				break;
			}
		}
		Properties result = null;
		InputStream in = null;
		try {
			if (loader == null) loader = ClassLoader.getSystemClassLoader();
			if (LOAD_AS_RESOURCE_BUNDLE) {
				name = name.replace('/', '.');
				// throws MissingResourceException on lookup failures:
				final ResourceBundle rb = ResourceBundle.getBundle(name, Locale.getDefault(), loader);
				result = new Properties();
				for (Enumeration keys = rb.getKeys(); keys.hasMoreElements();) {
					final String key = (String) keys.nextElement();
					final String value = rb.getString(key);
					result.put(key, value);
				}
			} else {
				name = name.replace('.', '/');
				if (!name.endsWith(suffix))
					name = name.concat(suffix);
				// returns null on lookup failures:
				java.net.URL resourceUsed = loader.getResource(name);
				setPropertiesFile(resourceUsed.getPath());
				in = loader.getResourceAsStream(name);
				if (in != null) {
					result = new Properties();
					result.load(in); // can throw IOException
				}
			}
		} catch (Exception e) {
			result = null;
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (Throwable ignore) {
				}
		}
		if (THROW_ON_LOAD_FAILURE && (result == null)) {
			//throw new IllegalArgumentException("could not load [" + name + "]" +
			//        " as " + (LOAD_AS_RESOURCE_BUNDLE
			//        ? "a resource bundle"
			//        : "a classloader resource"));
		}
		return result;
	}

}