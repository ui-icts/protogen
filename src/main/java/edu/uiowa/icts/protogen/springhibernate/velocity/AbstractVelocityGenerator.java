package edu.uiowa.icts.protogen.springhibernate.velocity;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import edu.uiowa.icts.protogen.springhibernate.ClassVariable;
import edu.uiowa.icts.protogen.springhibernate.DomainClass;
import edu.uiowa.webapp.Schema;

public abstract class AbstractVelocityGenerator {

	protected final Log log = LogFactory.getLog( getClass() );

	protected static final String INTERFACE_SUFFIX = "Service";

	protected Schema schema;
	protected DomainClass domainClass;
	protected String packageRoot;
	protected Properties properties;

	public AbstractVelocityGenerator( Schema schema, String packageRoot, Properties properties ) {
		this.packageRoot = packageRoot;
		this.schema = schema;
		this.properties = properties;
		init();
	}

	public AbstractVelocityGenerator( String packageRoot, DomainClass domainClass, Properties properties ) {
		this.packageRoot = packageRoot;
		this.domainClass = domainClass;
		this.properties = properties;
		this.schema = domainClass.getSchema();
		init();
	}

	private void init() {
		/** 
		 * initialize the velocity runtime engine, which only takes affect with first call to .init( properties ) 
		 * subsequent calls to init are ignored.
		 */
		Properties properties = new Properties();
		properties.setProperty( "resource.loader", "class" );
		properties.setProperty( "class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader" );
		properties.setProperty( "class.resource.loader.cache", "true" );
		properties.setProperty( "runtime.log.logsystem.log4j.logger", "Apache Velocity" );
		Velocity.init( properties );
	}

	public String getBasePackageName() {
		if ( this.schema != null ) {
			return this.packageRoot + ( Boolean.valueOf( properties.getProperty( "include.schema.in.package.name", "true" ) ) ? "." + this.schema.getLowerLabel() : "" );
		}
		return null;
	}

	public String getDomainPackageName() {
		return getBasePackageName() + ".domain";
	}

	public String getDaoPackageName() {
		return getBasePackageName() + ".dao";
	}

	public String getControllerPackageName() {
		return getBasePackageName() + ".controller";
	}
	
	public String getRestApiResourcePackageName() {
		return getBasePackageName() + ".resource";
	}

	public String getPathPrefix() {
		if ( Boolean.valueOf( properties.getProperty( "include.schema.in.request.mapping", "true" ) ) ) {
			return "/" + this.schema.getLowerLabel() + "/" + domainClass.getLowerIdentifier().toLowerCase();
		} else {
			return "/" + domainClass.getLowerIdentifier().toLowerCase();
		}
	}

	public String getJspPath() {
		if ( Boolean.valueOf( properties.getProperty( "include.schema.in.jsp.path", "true" ) ) ) {
			return "/" + this.schema.getLowerLabel() + "/" + domainClass.getLowerIdentifier().toLowerCase();
		} else {
			return "/" + domainClass.getLowerIdentifier().toLowerCase();
		}
	}

	public String getPathExtension() {
		return properties.getProperty( "controller.request.mapping.extension", "" );
	}

	protected void addDaoServiceNameToVelocityContext( VelocityContext context ) {
		String daoServiceClassName = getDaoServiceClassName();
		context.put( "daoServiceName", StringUtils.substring( daoServiceClassName, 0, 1 ).toLowerCase() + StringUtils.substring( daoServiceClassName, 1, daoServiceClassName.length() ) );
	}

	protected String getDaoServiceClassName() {
		String daoServiceName = properties.getProperty( this.schema.getLowerLabel() + ".master.dao.service.name" );
		if ( daoServiceName == null || "".equals( daoServiceName.trim() ) ) {
			daoServiceName = this.schema.getUpperLabel() + "DaoService";
		}
		return daoServiceName;
	}
	
	protected String getAbstractResourceClassName() {
		String abstractResourceClassName = properties.getProperty( this.schema.getLowerLabel() + ".abstract.resource.name" );
		if ( abstractResourceClassName == null ) {
			abstractResourceClassName = "Abstract" + this.schema.getUpperLabel() + "Resource";
		}
		return abstractResourceClassName;
	}
	
	protected String getAbstractApiResourceClassName() {
		String abstractApiResourceClassName = properties.getProperty( this.schema.getLowerLabel() + ".abstract.resource.name" );
		if ( abstractApiResourceClassName == null ) {
			abstractApiResourceClassName = "Abstract" + this.schema.getUpperLabel() + "ApiResource";
		}
		return abstractApiResourceClassName;
	}
	
	protected String newCompositeKey() {
		StringBuilder output = new StringBuilder();
		if ( domainClass.isUsesCompositeKey() ) {
			output.append( tab( 2 ) + domainClass.getIdentifier() + "Id " + domainClass.getLowerIdentifier() + "Id = new " + domainClass.getIdentifier() + "Id();\n" );
		}
		return output.toString();
	}

	protected String compositeKeySetter() {
		StringBuilder output = new StringBuilder();
		if ( domainClass.isUsesCompositeKey() ) {
			output.append( tab( 2 ) + "" + domainClass.getLowerIdentifier() + ".setId( " + domainClass.getLowerIdentifier() + "Id );" );
		}
		return output.toString();
	}

	protected String foreignClassSetters() {
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

	protected String tab( int tabCount ) {
		StringBuilder output = new StringBuilder();
		for ( int i = 0; i < tabCount; i++ ) {
			output.append( '\t' );
		}
		return output.toString();
	}

	protected String splitCapitalizedWords( String substring ) {
		String newString = "";
		for ( char c : substring.toCharArray() ) {
			if ( Character.isUpperCase( c ) ) {
				newString += " ";
			}
			newString += String.valueOf( c );
		}
		return StringUtils.trim( newString );
	}
}
