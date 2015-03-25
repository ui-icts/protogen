/*
 * Created on May 12, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.uiowa.icts.protogen.springhibernate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractSpringHibernateCodeGenerator {

	protected String pathBase = null;
	protected String packageRoot = null;
	protected String packageRootPath = null;
	protected SpringHibernateModel model = null;

	protected static final Log log = LogFactory.getLog( AbstractSpringHibernateCodeGenerator.class );

	public AbstractSpringHibernateCodeGenerator( SpringHibernateModel model, String pathBase, String packageRoot ) {
		this.model = model;
		this.pathBase = pathBase;
		this.packageRoot = packageRoot;
		packageRootPath = pathBase + "/" + packageRoot.replaceAll( "\\.", "/" );

	}

	/*
	 *  this method is used to generate the code
	 * 
	 * 
	 */
	public abstract void generate() throws IOException;

	public String getPathBase() {
		return pathBase;
	}

	public void setPathBase( String pathBase ) {
		this.pathBase = pathBase;
	}

	protected void lines( BufferedWriter out, int num ) throws IOException {
		for ( int i = 0; i < num; i++ ) {
			out.write( '\n' );
		}
	}

	protected void spaces( BufferedWriter out, int num ) throws IOException {
		for ( int i = 0; i < num; i++ ) {
			out.write( ' ' );
		}
	}

	protected void tabs( BufferedWriter out, int num ) throws IOException {
		for ( int i = 0; i < num; i++ ) {
			out.write( '\t' );
		}
	}

	public String createGetter( String type, String variableName, int indent ) {
		String sig = "";
		if ( variableName.length() > 1 ) {
			sig = "public " + type + " get" + variableName.substring( 0, 1 ).toUpperCase() + variableName.substring( 1, variableName.length() );
		} else {
			sig = "public " + type + " get" + variableName.substring( 0, 1 ).toUpperCase();
		}
		StringBuffer output = new StringBuffer();
		output.append( indent( indent ) + sig + "() {\n" );
		output.append( indent( indent * 2 ) + "return " + variableName + ";\n" );
		output.append( indent( indent ) + "}\n" );
		return output.toString();
	}

	public String createSetter( String type, String variableName, int indent ) {
		String sig = "";
		if ( variableName.length() > 1 ) {
			sig = "public void set" + variableName.substring( 0, 1 ).toUpperCase() + variableName.substring( 1, variableName.length() );
		} else {
			sig = "public void set" + variableName.substring( 0, 1 ).toUpperCase();
		}
		StringBuffer output = new StringBuffer();
		output.append( indent( indent ) + sig + "( " + type + " " + variableName + " ) {\n" );
		output.append( indent( indent * 2 ) + "this." + variableName + " = " + variableName + ";\n" );
		output.append( indent( indent ) + "}\n" );
		return output.toString();
	}

	public String indent( int indent ) {
		String output = "";
		for ( int i = 0; i < indent; i++ ) {
			output += " ";
		}
		return output;
	}

	/*
	 * Write file to src/ by default, if exists there, write to target/src/
	 */
	protected BufferedWriter createFileInSrcElseTarget( String packagePath, String fileName ) throws IOException {
		// make sure all directories have been created
		( new File( packagePath ) ).mkdirs();

		File file = new File( packagePath, fileName );
		if ( file.exists() ) {
			// create file with "target" prepended...
			log.debug( fileName + " exists in " + packagePath + ", creating it in 'target' directory" );
			packagePath = packagePath.replaceFirst( "src/", "target/src/" );
			( new File( packagePath ) ).mkdirs();
			file = new File( packagePath, fileName );
		}
		FileWriter fstream = new FileWriter( file );
		BufferedWriter out = new BufferedWriter( fstream );
		return out;
	}

}