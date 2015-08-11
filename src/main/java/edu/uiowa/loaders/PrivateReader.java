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

import java.io.*;
import java.util.zip.*;
import java.net.*;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PrivateReader extends Reader {
	protected static boolean debug = false;
	protected Reader internal = null;
	boolean first = true;
	boolean last = false;
	String header = null;
	String footer = null;
	
	
	private static final Log log = LogFactory.getLog(PrivateReader.class);

	public PrivateReader(String file, String head, String foot) throws MalformedURLException, IOException {
		header = head;
		footer = foot;
		if (file.indexOf("://") < 0) {
		    internal = new BufferedReader(new FileReader(file));
        } else if (file.startsWith("https:")) {
            log.debug("trying ssl connection...");
            System.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
            HttpsURLConnection conn = (HttpsURLConnection)(new URL(file)).openConnection();
            internal = new BufferedReader(new InputStreamReader(conn.getInputStream()), 200000);
        } else if (file.endsWith(".z") || file.endsWith(".gz")) {
			internal = new InputStreamReader(new GZIPInputStream((new URL(file)).openConnection().getInputStream()));
		} else
			internal = new InputStreamReader((new URL(file)).openConnection().getInputStream());
	}
	
	public int read(char[] theChars, int offset, int length) throws IOException {
		if (log.isDebugEnabled()){
			log.debug("read called: offset = " + offset + ", length = " + length);
		}
		if (first) {
			if (log.isDebugEnabled()){
				log.debug(header);
			}
			first = false;
			for (int i = 0; i < header.length(); i++){
				theChars[i] = header.charAt(i);
			}
			return header.length();
		} else {
			if (last) return -1;
			
			int cnt = internal.read(theChars, offset, length);
			if (log.isDebugEnabled()) {
				log.debug(theChars);
			}
			if (cnt == -1) {
				for (int i = offset; i < footer.length() + offset; i++){
					theChars[i] = footer.charAt(i - offset);
				}
				cnt = footer.length();
				last = true;
			}
			
			return cnt;
		}
	}
	
	public void close() throws IOException {
		internal.close();
	}
}
