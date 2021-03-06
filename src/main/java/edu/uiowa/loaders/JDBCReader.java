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

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class JDBCReader extends Reader {
	static boolean debug = true;
	Connection conn = null;
	ResultSet rs = null;
	boolean first = true;
	boolean last = false;
	String header = null;
	String footer = null;

	private static final Log log = LogFactory.getLog(JDBCReader.class);

	
	public JDBCReader(String className, String jdbcURL, String userID, String password) throws IOException, ClassNotFoundException, SQLException {
		Class.forName(className); 
		conn =  DriverManager.getConnection(jdbcURL, userID, password);
		rs = queryDatabase();
	}
	
	abstract ResultSet queryDatabase();
	
	public int read(char[] theChars, int offset, int length) throws IOException {
		if (log.isDebugEnabled()) {
			log.debug("read called: offset = " + offset + ", length = " + length);
		}
		try {
			if (rs.next()) {
				String buffer = rs.getString(1);
				if (log.isDebugEnabled()){
					log.debug(buffer);
				}
				theChars = buffer.toCharArray();
				return buffer.length();
			} else {
				return -1;
			}
		} catch (SQLException e) {
			throw new IOException(e.toString());
		}
	}
	
	public void close() throws IOException {
		try {
			rs.close();
			conn.close();
		} catch (SQLException e) {
			throw new IOException(e.toString());
		}
	}
}
