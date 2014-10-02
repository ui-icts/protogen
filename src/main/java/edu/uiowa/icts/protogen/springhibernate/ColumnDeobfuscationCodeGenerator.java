package edu.uiowa.icts.protogen.springhibernate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.uiowa.icts.util.IctsStringUtils;
import edu.uiowa.webapp.Schema;

public class ColumnDeobfuscationCodeGenerator extends AbstractSpringHibernateCodeGenerator {

	protected static final Log log = LogFactory.getLog( ColumnDeobfuscationCodeGenerator.class );
	private Properties properties;

	public ColumnDeobfuscationCodeGenerator( SpringHibernateModel model, String pathBase, String packageRoot, Properties properties ) {
		super( model, pathBase, packageRoot );
		this.properties = properties;
		( new File( packageRootPath ) ).mkdirs();
	}

	private void generateFunctionsClass() throws IOException {
		for ( Schema key : model.getSchemaMap().keySet() ) {
			if ( key.getLowerLabel().equalsIgnoreCase( properties.getProperty( "db.schema" ) ) ) {
				if ( Boolean.valueOf( properties.getProperty( "generate.deobfuscation.class" ) ) ) {
					generateFunctionsClass( "DeobfuscationFunctions", key );
				}
				if ( Boolean.valueOf( properties.getProperty( "generate.deobfuscation.tld" ) ) ) {
					generateTldFile( "DeobfuscationFunctions", key );
				}
			}
		}
	}

	/**
	 * @param schema
	 * @throws IOException 
	 */
	private void generateTldFile( String className, Schema schema ) throws IOException {

		if ( properties == null ) {
			log.error( "properties file is null, not generating tld file" );
			return;
		}

		if ( properties.getProperty( "deobfuscate.tld.file.location" ) == null ) {
			log.error( "property deobfuscate.tld.file.location is null, not generating tld file" );
			return;
		}

		String location = properties.getProperty( "deobfuscate.tld.file.location" );
		File directory = new File( properties.getProperty( "deobfuscate.tld.file.location" ) );
		directory.mkdirs();

		File tld = new File( location + ( location.endsWith( "/" ) ? "" : "/" ) + "deobfuscate.tld" );

		if ( tld.exists() ) {
			log.debug( "File Exists" );
			return;
		} else if ( tld.exists() ) {
			log.debug( "Overwriting file...." );
		}

		FileWriter fstream = new FileWriter( tld );
		BufferedWriter out = new BufferedWriter( fstream );

		out.write( "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" );
		out.write( "<taglib xmlns=\"http://java.sun.com/xml/ns/j2ee\"\n" );
		spaces( out, 4 );
		out.write( "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" );
		spaces( out, 4 );
		out.write( "xsi:schemaLocation=\"http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-jsptaglibrary_2_0.xsd\"\n" );
		spaces( out, 4 );
		out.write( "version=\"2.0\">\n" );
		spaces( out, 4 );
		out.write( "<tlib-version>2.1</tlib-version>\n" );
		spaces( out, 4 );
		out.write( "<short-name>simp</short-name>\n" );
		spaces( out, 4 );
		out.write( "<uri>http://icts.uiowa.edu/" + schema.getLowerLabel() + "</uri>\n\n" );
		spaces( out, 4 );
		out.write( "<function>\n" );
		spaces( out, 8 );
		out.write( "<name>deobfuscateColumn</name>\n" );
		spaces( out, 8 );
		out.write( "<function-class>" + model.getPackageRoot() + "." + schema.getLowerLabel() + ".util." + className + "</function-class>\n" );
		spaces( out, 8 );
		out.write( "<function-signature>java.lang.String deobfuscateColumn( java.lang.String, java.lang.String )</function-signature>\n" );
		spaces( out, 4 );
		out.write( "</function>\n" );
		out.write( "</taglib>" );

		out.flush();
		out.close();
	}

	private void generateFunctionsClass( String className, Schema schema ) throws IOException {

		if ( properties == null ) {
			log.error( "properties file is null, not generating DeobfuscationFunctions class" );
			return;
		}

		String packageName = model.getPackageRoot() + ( Boolean.valueOf( properties.getProperty( "include.schema.in.package.name", "true" ) ) ? "." + schema.getLowerLabel() : "" ) + ".util";

		String packagePath = pathBase + "/" + packageName.replaceAll( "\\.", "/" );
		String daoPackageName = model.getPackageRoot() + ( Boolean.valueOf( properties.getProperty( "include.schema.in.package.name", "true" ) ) ? "." + schema.getLowerLabel() : "" ) + ".dao";

		List<String> importList = new ArrayList<String>();

		importList.add( "import org.springframework.beans.factory.annotation.Autowired;" );
		importList.add( "import org.springframework.stereotype.Component;" );
		importList.add( "import " + daoPackageName + ".*;" );

		( new File( packagePath ) ).mkdirs();

		File file = new File( packagePath, className + ".java" );
		if ( file.exists() ) {
			log.debug( "File Exists" );
			return;
		} else if ( file.exists() ) {
			log.debug( "Overwriting file...." );
		}

		FileWriter fstream = new FileWriter( file );
		BufferedWriter out = new BufferedWriter( fstream );

		/*
		 * Print Package
		 */
		out.write( "package " + packageName + ";\n" );
		lines( out, 1 );

		/*
		 * Print imports
		 */
		for ( String importSt : importList ) {
			out.write( importSt + "\n" );
		}

		IctsStringUtils stringUtils = new IctsStringUtils();

		lines( out, 1 );
		out.write( "/**\n" );
		out.write( " * Generated by Protogen\n" );
		out.write( " * " + ( new Date() ).toString() + "\n" );
		out.write( " */\n" );
		out.write( "@Component\n" );
		out.write( "public class " + className + " {\n\n" );

		spaces( out, 4 );
		out.write( "protected static " + schema.getUpperLabel() + "DaoService " + schema.getLowerLabel() + "DaoService;\n\n" );

		spaces( out, 4 );
		out.write( "@Autowired\n" );
		spaces( out, 4 );
		out.write( "public void set" + schema.getUpperLabel() + "DaoService(" + schema.getUpperLabel() + "DaoService " + schema.getLowerLabel() + "DaoService) {\n" );
		spaces( out, 8 );
		out.write( "DeobfuscationFunctions." + schema.getLowerLabel() + "DaoService = " + schema.getLowerLabel() + "DaoService;\n" );
		spaces( out, 4 );
		out.write( "}\n\n" );

		spaces( out, 4 );
		out.write( "// add this to include.jsp to use this function <%@ taglib prefix=\"" + schema.getLowerLabel() + "\" uri=\"http://icts.uiowa.edu/" + schema.getLowerLabel() + "\" %> \n" );
		spaces( out, 4 );
		out.write( "public static String deobfuscateColumn ( String table, String column) {\n" );
		spaces( out, 8 );
		out.write( "String newCol = " + schema.getLowerLabel() + "DaoService.get" + stringUtils.relabel( (String) properties.get( "dictionary.table.name" ), true ) + "Service().getAlternateColumnName(table, column);\n" );
		spaces( out, 8 );
		out.write( "if( newCol != null ){\n" );
		spaces( out, 12 );
		out.write( "return newCol;\n" );
		spaces( out, 8 );
		out.write( "}\n" );
		spaces( out, 8 );
		out.write( "return column;\n" );
		spaces( out, 4 );
		out.write( "}\n" );

		out.write( "}" );
		out.close();
	}

	/*
	 * Public Function to generate java Functions class
	 */
	public void generate() throws IOException {
		log.debug( "Generating dao classes" );
		generateFunctionsClass();
	}

}
