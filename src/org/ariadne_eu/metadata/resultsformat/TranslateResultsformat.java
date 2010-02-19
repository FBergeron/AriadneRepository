package org.ariadne_eu.metadata.resultsformat;

import org.apache.log4j.Logger;
import org.ariadne_eu.metadata.query.QueryMetadataFactory;
import org.ariadne_eu.metadata.query.language.TranslateLanguage;

/**
 * Created by ben
 * Date: 23-jun-2007
 * Time: 14:43:05
 * To change this template use File | Settings | File Templates.
 */
public class TranslateResultsformat {

    private static Logger log = Logger.getLogger(TranslateResultsformat.class);

    public static final int UNDEFINED = -1;
    public static final int LOM = 0;
    public static final int PLRF0 = 1;
    public static final int PLRF1 = 2;
    public static final int PLRF2 = 3;
    public static final int PLRF3 = 4;
    public static final int SOLR = 5;
    public static final int RLOM = 6;
    public static final int MELOM = 7;
    public static final int DATE = 8;

    public static boolean isPLRF(int rfId) {
        return rfId >= 1 && rfId <= 4;
    }

    public static boolean isValidPLRF(String rf) {
        String[] parts = parsePLRF(rf);
        return (parts != null && parts[1].equalsIgnoreCase("lom"));
    }

    public static int getResultsFormat(String rf) {
        if (rf == null)
            return UNDEFINED;
        if (isValidPLRF(rf))
            return Integer.parseInt(parsePLRF(rf)[0])+1;
        else if (rf.equalsIgnoreCase("lom") || rf.equalsIgnoreCase("http://ltsc.ieee.org/xsd/LOM"))
            return LOM;
        else if (rf.equalsIgnoreCase("solr"))
        	return SOLR;
        else if (rf.equalsIgnoreCase("rlom"))
        	return RLOM;
        else if (rf.equalsIgnoreCase("maceenrichedlom"))
        	return MELOM;
        return UNDEFINED;
    }

    /**
     * Parses the given prolearn resultsformat.
     * The result is a String array containing
     *      the level
     *      the standard
     *      the method (null when not filled in)
     *
     * If an invalid prolearn resultsformat is returned,
     * this method will return null
     */
    public static String[] parsePLRF(String rf) {
        try {
            String plStart = "http://www.prolearn-project.org/PLRF/";
            if (!rf.toLowerCase().startsWith(plStart.toLowerCase()))
                return null;
            rf = rf.substring(plStart.length());
            String[] parts = rf.split("/");
            if (parts.length < 2 || parts.length > 3)
                return null;

            String[] result = new String[3];
            int level = Integer.parseInt(parts[0]);
            if (level < 0 || level > 3)
                return null;
            result[0] = String.valueOf(level);

            String standard = parts[1];
            if (standard.length() == 0)
                return null;
            result[1] = standard;

            if (parts.length == 3) {
                String method = parts[2];
                if (method.length() == 0)
                    return null;
                result[2] = method;
            }

            return result;
        } catch (Exception e) {
        }
        return null;
    }

    public static String processResults(String result, String rf, String ql, String query) {
        int resultsFormat = getResultsFormat(rf);
        if (isPLRF(resultsFormat)) {
            String start = "<Results xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "    xsi:schemaLocation=\"http://www.prolearn-project.org/PLRF/ http://www.cs.kuleuven.be/~stefaan/plql/plql.xsd http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lom.xsd\"\n" +
                    "    xmlns=\"http://www.prolearn-project.org/PLQLRES/\">\n" +
                    "        <ResultInfo>\n" +
                    "            <ResultLevel>" + rf + "</ResultLevel>\n" +
                    "            <QueryMethod>" + ql + "</QueryMethod>\n";
            if (resultsFormat == PLRF0) {
                int count = -1;
                try {
                    count = QueryMetadataFactory.getQueryImpl(TranslateLanguage.getQueryLanguage(ql)).count(query);
                } catch (Exception e) {
                    log.error("Error while counting cardinality for plrf ", e);
                }
                start += "            <Cardinality>" + count + "</Cardinality>\n";
            }
            start +="        </ResultInfo>\n";
            String end = "\n</Results>";
            if (resultsFormat == PLRF0) {
                return start + end;
            } else
                return start + result + end;
        }
        else
            return result;
    }
}
