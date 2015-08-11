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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DBConnect {
	
	private static final Log log = LogFactory.getLog(DBConnect.class);
	
    public Connection conn = null;
	private String driver_class;
	private String connectionURL;
	private String userID;
	private String userPassword;
	private boolean ssl;
    
    public DBConnect(String driver_class, String connectionURL,String userID, String userPassword, boolean ssl) throws SQLException{
        this.driver_class = driver_class;
        this.connectionURL = connectionURL;
        this.userID = userID;
        this.userPassword = userPassword;
        this.ssl=ssl;

    }
	
	public void connect() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
		log.debug("driver class: "+driver_class);
        Class.forName(driver_class).newInstance();
        
        final Properties properties = new Properties();
        properties.put("user",     userID);
        properties.put("password", userPassword);
        if(ssl)
        properties.put("ssl",      "true");
        conn = DriverManager.getConnection(connectionURL, properties);
	}

	public Connection getConn() {
		return conn;
	}

	@Override
	public String toString() {
		return "DBConnect [driver_class=" + driver_class + ", connectionURL="
				+ connectionURL + ", userID=" + userID + ", userPassword="
				+ userPassword + ", ssl=" + ssl + "]";
	}
	
	
}
