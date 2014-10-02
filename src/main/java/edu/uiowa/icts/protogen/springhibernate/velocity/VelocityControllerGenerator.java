package edu.uiowa.icts.protogen.springhibernate.velocity;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import edu.uiowa.icts.protogen.springhibernate.ClassVariable;
import edu.uiowa.icts.protogen.springhibernate.DomainClass;
import edu.uiowa.webapp.Attribute;

/**
 * Generate spring controllers using velocity templates.
 * @author rrlorent
 * @since August 4, 2014
 */
public class VelocityControllerGenerator extends AbstractVelocityGenerator {

	private static final String INTERFACE_SUFFIX = "Service";

	public VelocityControllerGenerator( String packageRoot, DomainClass domainClass, Properties properties ) {
		super( packageRoot, domainClass, properties );
	}
    
	public String javaSourceCode() {

		SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy HH:mm:ss z", Locale.US );
		sdf.setTimeZone( TimeZone.getDefault() );

		/* lets make a Context and put data into it */
		VelocityContext context = new VelocityContext();
		context.put( "date", sdf.format( new Date() ) ); // can be done with Velocity tools but let's keep it simple to start
		context.put( "packageName", this.getPackageName() );
		context.put( "className", domainClass.getIdentifier() + "Controller" );
		context.put( "pathPrefix", this.getPathPrefix() );
		context.put( "jspPath", this.getJspPath() );
		context.put( "pathExtension", this.getPathExtension());
		context.put( "domainName", domainClass.getIdentifier() );
		context.put( "lowerDomainName", domainClass.getLowerIdentifier() );
		
		String abstractControllerClassName = properties.getProperty( domainClass.getSchema().getLabel().toLowerCase() + ".abstract.controller.name" );
		if( abstractControllerClassName == null ){
			abstractControllerClassName = "Abstract" + domainClass.getSchema().getUpperLabel() + "Controller";
		}
		context.put( "abstractControllerClassName", abstractControllerClassName );
		
		String daoServiceName = properties.getProperty( domainClass.getSchema().getUpperLabel().toLowerCase() + ".master.dao.service.name" );
		if( daoServiceName == null || "".equals( daoServiceName.trim() ) ){
			daoServiceName = domainClass.getSchema().getLowerLabel() + "DaoService";
		} else {
			daoServiceName = StringUtils.substring( daoServiceName, 0, 1 ).toLowerCase() + StringUtils.substring( daoServiceName, 1, daoServiceName.length() );
		}
		context.put( "daoServiceName", daoServiceName );
		
		context.put( "domainPackageName", this.packageRoot + "." + domainClass.getSchema().getLowerLabel() + ".domain" );

		String dtMethod = datatableMethod( context );
		context.put( "datatableMethod", dtMethod );

		context.put( "requestParameterIdentifier", requestParameterIdentifier() );
		context.put( "addEditListDependencies", addEditListDependencies() );
		context.put( "compositeKey", compositeKey() );
		context.put( "compositeKeySetter", compositeKeySetter() );
		context.put( "foreignClassParameters", foreignClassParameters() );
		context.put( "foreignClassSetters", foreignClassSetters() );

		/* render a template loaded from the classpath */
		StringWriter writer = new StringWriter();

		Velocity.mergeTemplate( "/velocity-templates/Controller.java", Velocity.ENCODING_DEFAULT, context, writer );

		return writer.toString();

	}

	private String compositeKeySetter() {
		StringBuilder output = new StringBuilder();
		if ( domainClass.isUsesCompositeKey() ) {
			output.append( tab( 2 ) + "" + domainClass.getLowerIdentifier() + ".setId( " + domainClass.getLowerIdentifier() + "Id );" );
		}
		return output.toString();
	}

	private String foreignClassSetters() {
		StringBuilder output = new StringBuilder();
		for ( ClassVariable cv : domainClass.getForeignClassVariables() ) {
			if ( !cv.isPrimary() && !domainClass.isUsesCompositeKey() ) {
				String getter = domainClass.getSchema().getLowerLabel() + "DaoService.get" + cv.getDomainClass().getIdentifier() + INTERFACE_SUFFIX + "().findById( " + cv.getDomainClass().getLowerIdentifier() + "_" + cv.getDomainClass().getPrimaryKey().getIdentifier() + " )";
				output.append( tab( 2 ) + domainClass.getLowerIdentifier() + ".set" + cv.getUpperIdentifier() + "( " + getter + " );\n" );
			} else {
				output.append( tab( 2 ) + domainClass.getLowerIdentifier() + "Id.set" + cv.getAttribute().getUpperLabel() + "( " + cv.getDomainClass().getLowerIdentifier() + "_" + cv.getDomainClass().getPrimaryKey().getIdentifier() + " );\n" );
			}
		}
		return output.toString();
	}

	private String foreignClassParameters() {
		StringBuilder output = new StringBuilder( " " );
		if ( domainClass.getForeignClassVariables() != null && !domainClass.getForeignClassVariables().isEmpty() ) {
			for ( ClassVariable cv : domainClass.getForeignClassVariables() ) {
				output.append( "@RequestParam( value = \"" + cv.getDomainClass().getLowerIdentifier() + "." + cv.getDomainClass().getPrimaryKey().getIdentifier() + "\" ) " + cv.getDomainClass().getPrimaryKey().getType() + " " + cv.getDomainClass().getLowerIdentifier() + "_" + cv.getDomainClass().getPrimaryKey().getIdentifier() + ", " );
			}
		}
		return output.toString();
	}

	private String compositeKey() {
		StringBuilder output = new StringBuilder();
		if ( domainClass.isUsesCompositeKey() ) {
			output.append( tab( 2 ) + domainClass.getIdentifier() + "Id " + domainClass.getLowerIdentifier() + "Id = new " + domainClass.getIdentifier() + "Id();\n" );
			for ( Attribute attribute : domainClass.getEntity().getPrimaryKeyAttributes() ) {
				String setter = "set" + attribute.getLowerLabel().substring( 0, 1 ).toUpperCase() + attribute.getLowerLabel().substring( 1, attribute.getLowerLabel().length() );
				output.append( tab( 2 ) + domainClass.getLowerIdentifier() + "Id." + setter + "( " + attribute.getLowerLabel() + " );\n" );
			}
		}
		return output.toString();
	}

	private String addEditListDependencies() {
		StringBuilder output = new StringBuilder();
		for ( ClassVariable cv : domainClass.getForeignClassVariables() ) {
			output.append( tab( 2 ) + "model.addAttribute( \"" + cv.getDomainClass().getLowerIdentifier() + "List\", " + cv.getDomainClass().getSchema().getLowerLabel() + "DaoService.get" + cv.getDomainClass().getIdentifier() + INTERFACE_SUFFIX + "().list() );\n" );
		}
		return output.toString();
	}

	private String requestParameterIdentifier() {
		String parameterString = "";
		if ( domainClass.isUsesCompositeKey() ) {
			for ( Attribute a : domainClass.getEntity().getPrimaryKeyAttributes() ) {
				String type = a.getType();
				if ( type.equalsIgnoreCase( "date" ) ) {
					type = "String";
				}
				parameterString += "@RequestParam( value = \"" + a.getLowerLabel() + "\" ) " + type + " " + a.getLowerLabel() + ", ";
			}
			if ( !"".equals( parameterString ) ) {
				parameterString = parameterString.substring( 0, parameterString.length() - 2 );
			}
		} else {
			for ( ClassVariable cv : domainClass.getPrimaryKeys() ) {
				parameterString += "@RequestParam( value = \"" + cv.getLowerIdentifier() + "\" ) " + cv.getType() + " " + domainClass.getLowerIdentifier() + "Id, ";
			}
			if ( !"".equals( parameterString ) ) {
				parameterString = parameterString.substring( 0, parameterString.length() - 2 );
			}
		}
		return parameterString;
	}

	private String datatableMethod( VelocityContext context ) {
		context.put( "datatableColumnForEach", datatableColumnForEach() );
		StringWriter writer = new StringWriter();
		if ( StringUtils.equals( properties.getProperty( "datatables.generation", "1" ), "2" ) ) {
			Velocity.mergeTemplate( "/velocity-templates/DatatableMethodGeneration2.java", Velocity.ENCODING_DEFAULT, context, writer );
		} else {
			Velocity.mergeTemplate( "/velocity-templates/DatatableMethodGeneration1.java", Velocity.ENCODING_DEFAULT, context, writer );
		}
		return writer.toString();
	}

	private String datatableColumnForEach() {

		StringBuilder output = new StringBuilder();

		int indent = 4;

		output.append( tab( indent ) + "JSONArray tableRow = new JSONArray();\n" );

		if ( StringUtils.equals( properties.getProperty( "datatables.generation", "1" ), "2" ) ) {
			output.append( tab( indent ) + "for ( DataTableHeader header : headers ) {\n" );
			indent += 1;
			output.append( tab( indent ) + "String headerName = header.getName();\n" );
		} else {
			output.append( tab( indent ) + "for( String headerName : colArr ){\n" );
			indent += 1;
		}

		int count = 0;
		Iterator<ClassVariable> iter = domainClass.listAllIter();
		while ( iter.hasNext() ) {
			ClassVariable cv = iter.next();
			if ( cv.isPrimary() && domainClass.isUsesCompositeKey() ) {
				for ( Attribute a : domainClass.getEntity().getPrimaryKeyAttributes() ) {
					output.append( tab( indent ) + ( count > 0 ? "} else " : "" ) + "if( StringUtils.equals( \"id." + a.getLowerLabel() + "\", headerName ) ){\n" );
					indent += 1;
					output.append( tab( indent ) + "tableRow.put( " + domainClass.getLowerIdentifier() + ".getId().get" + a.getUpperLabel() + "() );\n" );
					indent -= 1;
					count++;
				}
			} else {
				output.append( tab( indent ) + ( count > 0 ? "} else " : "" ) + "if( StringUtils.equals( \"" + cv.getLowerIdentifier() + "\", headerName ) ){\n" );
				indent += 1;
				output.append( tab( indent ) + "tableRow.put( " + domainClass.getLowerIdentifier() + ".get" + cv.getUpperIdentifier() + "() );\n" );
				indent -= 1;
				count++;
			}
		}

		output.append( tab( indent ) + ( count > 0 ? "} else " : "" ) + "if( StringUtils.equals( \"urls\", headerName ) ) {\n" );
		indent += 1;

		output.append( tab( indent ) + "String urls = \"\";\n" );
		output.append( tab( indent ) + "if( StringUtils.equals( \"list\", display ) ){\n" );

		indent += 1;

		String params = "";
		if ( domainClass.isUsesCompositeKey() ) {
			for ( Attribute a : domainClass.getEntity().getPrimaryKeyAttributes() ) {
				params += "\"" + a.getLowerLabel() + "=\"+" + domainClass.getLowerIdentifier() + ".getId().get" + a.getUpperLabel() + "()+\"&\"+";
			}
			if ( !"".equals( params ) ) {
				params = params.substring( 0, params.length() - 4 );
			}
		} else {
			for ( ClassVariable classVariable : domainClass.getPrimaryKeys() ) {
				params += "\"" + classVariable.getLowerIdentifier() + "=\"+" + domainClass.getLowerIdentifier() + ".get" + classVariable.getUpperIdentifier() + "()+\"&\"+";
			}
			if ( !"".equals( params ) ) {
				params = params.substring( 0, params.length() - 4 );
			}
		}

		output.append( tab( indent ) + "urls += \"<a href=\\\"show"+this.getPathExtension()+"?\"+" + params + "\"\\\">[view]</a>\";\n" );
		output.append( tab( indent ) + "urls += \"<a href=\\\"edit"+this.getPathExtension()+"?\"+" + params + "\"\\\">[edit]</a>\";\n" );
		output.append( tab( indent ) + "urls += \"<a href=\\\"delete"+this.getPathExtension()+"?\"+" + params + "\"\\\">[delete]</a>\";\n" );

		indent -= 1;

		output.append( tab( indent ) + "} else {\n\n" );
		output.append( tab( indent ) + "}\n" );
		output.append( tab( indent ) + "tableRow.put( urls );\n" );

		indent -= 1;

		output.append( tab( indent ) + "} else {\n" );

		indent += 1;

		output.append( tab( indent ) + "tableRow.put( \"[error: column \" + headerName + \" not supported]\" );\n" );

		indent -= 1;

		output.append( tab( indent ) + "}\n" );

		indent -= 1;

		output.append( tab( indent ) + "}\n" );
		output.append( tab( indent ) + "jsonArray.put( tableRow );" );

		return output.toString();
	}

	private String tab( int tabCount ) {
		StringBuilder output = new StringBuilder();
		for ( int i = 0; i < tabCount; i++ ) {
			output.append( '\t' );
		}
		return output.toString();
	}

}