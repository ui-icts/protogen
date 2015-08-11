package edu.uiowa.webapp;

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

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Database extends ClayElement {

	private static final Log log = LogFactory.getLog( Database.class );

	Vector<Schema> schemas = new Vector<Schema>();

	public Vector<Schema> getSchemas() {
		return schemas;
	}

	public void setSchemas( Vector<Schema> schemas ) {
		this.schemas = schemas;
	}

	public Schema getSchemaByName( String label ) {
		for ( int i = 0; i < schemas.size(); i++ ) {
			if ( schemas.elementAt( i ).getLabel().equals( label ) ) {
				return schemas.elementAt( i );
			}
		}

		return null;
	}

	public void relabel() {
		relabel( false );
		for ( int i = 0; i < schemas.size(); i++ ) {
			schemas.elementAt( i ).relabel();
		}
	}

	public void dump() {
		log.debug( "database: " + label + "\tuid: " + uid );
		for ( int i = 0; i < schemas.size(); i++ ) {
			schemas.elementAt( i ).dump();
		}
	}

}