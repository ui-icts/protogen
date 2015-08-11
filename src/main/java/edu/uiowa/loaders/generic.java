package edu.uiowa.loaders;

/*
 * #%L
 * Protogen
 * %%
 * Copyright (C) 2009 - 2015 University of Iowa Institute for Clinical and Translational Science (ICTS)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.io.*;
import java.util.zip.*;
import java.net.*;

import javax.net.ssl.HttpsURLConnection;

public class generic extends DefaultHandler {
    protected static boolean debug = true;
    protected static boolean verbose = false;
    protected static boolean terse = false;
    protected static boolean load = false;

	protected StringBuffer buffer = new StringBuffer();
    boolean wrapWithTag = false;
    String wrapperTag = null;

	protected static XMLReader xr = null;

	public static void main (String args[]) throws Exception {
		generic handler = new generic();
		handler.run(args);
	}
	
	private static final Log log = LogFactory.getLog(generic.class);

	public void run(String args[]) throws Exception {
		xr = XMLReaderFactory.createXMLReader();
		xr.setContentHandler(this);
		xr.setErrorHandler(this);
		xr.setFeature("http://xml.org/sax/features/validation", false);

		if (args.length > 0) {
            if (args[0].equals("-")) {
                processStdin();
                return;
            }
			// Parse each file provided on the command line.
			for (int i = 0; i < args.length; i++) {
                if (args[i].startsWith("-wrapper=")) {
                    wrapWithTag = true;
                    wrapperTag = args[i].substring(args[i].indexOf('=')+1);
                    if (log.isDebugEnabled()){
                    	log.debug("wrapper tag:" + wrapperTag);
                    }
                } else {
                    if (wrapWithTag) {
                        log.debug("Reading with wrapper " + wrapperTag + "  " + args[i]);
                        processFileWithWrapper(args[i]);
                    } else {
                        log.debug("Reading " + args[i]);
                        try {
                            processFile(args[i]);
                        } catch (Exception e) {
                            log.error("Failed Reading", e);
                        }
                    }
                }
			}
		} else {
			// read files from stdin
			BufferedReader IODesc = new BufferedReader(new InputStreamReader(System.in));
			String current = null;
			while ((current = IODesc.readLine()) != null) {
				log.debug("processing : " + current);
				try {
                    processFile(current);
                } catch (Exception e) {
                    log.error("Faild Processing File", e);
                }
			}
		}
    }
    
	public void run(String arg) throws Exception {
		xr = XMLReaderFactory.createXMLReader();
		xr.setContentHandler(this);
		xr.setErrorHandler(this);
		xr.setFeature("http://xml.org/sax/features/validation", false);
		if (verbose){
			log.info("Reading " + arg);
		}
		processFile(arg);
    }
    
	public void processFile(String file) throws Exception {
		BufferedReader IODesc = null;
		if (file.indexOf("://") < 0) {
            IODesc = new BufferedReader(new FileReader(file));
        } else if (file.startsWith("https:")) {
            log.debug("trying ssl connection...");
            System.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
            HttpsURLConnection conn = (HttpsURLConnection)(new URL(file)).openConnection();
            IODesc = new BufferedReader(new InputStreamReader(conn.getInputStream()), 200000);
        } else if (file.endsWith(".z") || file.endsWith(".gz")) {
			IODesc = new BufferedReader(new InputStreamReader(new GZIPInputStream((new URL(file)).openConnection().getInputStream())), 200000);
		} else {
			IODesc = new BufferedReader(new InputStreamReader((new URL(file)).openConnection().getInputStream()), 200000);
		}

	    xr.parse(new InputSource(IODesc));
	    IODesc.close();
    }

    public void processStdin() throws Exception {
        xr.parse(new InputSource(new BufferedReader(new InputStreamReader(System.in))));
    }

    public void processFileWithWrapper(String file) throws Exception {
       BufferedReader IODesc = new BufferedReader(new PrivateReader(file, "<"+wrapperTag+">", "</"+wrapperTag+">"), 20000);
        xr.parse(new InputSource(IODesc));
        IODesc.close();
    }

    public generic () {
		super();
    }


    ////////////////////////////////////////////////////////////////////
    // Event handlers.
    ////////////////////////////////////////////////////////////////////


    public void startDocument () {
		if (log.isDebugEnabled()){
			log.debug("Start document");
		}
    }


    public void endDocument () {
		if (log.isDebugEnabled()){
			log.debug("End document");
		}
    }


    public void startElement (String uri, String name, String qName, Attributes atts) {
		if (log.isDebugEnabled()) {
			if ("".equals (uri)){
				log.debug("Start element: " + qName);
				for (int i = 0; i < atts.getLength(); i++){
					log.debug("\tattribute " + i + ": " + atts.getQName(i) + " > " + atts.getValue(i));
				}
			} else {
				//log.debug("Start element: {" + uri + "}" + name + " " + atts);
			}
		}
    }


    public void endElement (String uri, String name, String qName) {
		if (log.isDebugEnabled()) {
		    log.debug("End element: " + qName);
		}
		buffer = new StringBuffer();
    }


    public void characters (char ch[], int start, int length) {
    	buffer.append(ch,start,length);
		if (log.isDebugEnabled()) {
			//log.debug();
			StringBuffer temp = new StringBuffer("Characters:    \"");
			for (int i = start; i < start + length; i++) {
			    switch (ch[i]) {
				    case '\\':
						temp.append("\\\\");
						break;
				    case '"':
				    	temp.append("\\\"");
						break;
				    case '\n':
				    	temp.append("\\n");
						break;
				    case '\r':
				    	temp.append("\\r");
						break;
				    case '\t':
				    	temp.append("\\t");
						break;
				    default:
				    	temp.append(ch[i]);
						break;
			    }
			}
			log.debug(temp + "\"\n");
		}
    }
    
    public String getAttByName(Attributes atts, String name) {
    	String response = null;
		for (int i = 0; i < atts.getLength(); i++) {
			if (name.equals(atts.getQName(i))){
				return atts.getValue(i);
			}
		}
		return response;
    }
}