package edu.uiowa.webapp;

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