package edu.uiowa.icts.protogen.springhibernate.velocity;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import edu.uiowa.icts.protogen.springhibernate.ClassVariable;
import edu.uiowa.icts.protogen.springhibernate.DomainClass;

public abstract class AbstractVelocityGenerator {
	protected static final String INTERFACE_SUFFIX = "Service";

	protected DomainClass domainClass;
	protected String packageRoot;
	protected Properties properties;
	
	public AbstractVelocityGenerator(String packageRoot, DomainClass domainClass, Properties properties) {
		this.packageRoot = packageRoot;
		this.domainClass = domainClass;
		this.properties = properties;
		/* 
		 * init the runtime engine, which only takes affect with first call to .init(p) 
		 * subsequent calls to init are ignored.
		 * */
		Properties p = new Properties();
	    p.setProperty("resource.loader", "class");
	    p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
	    p.setProperty("class.resource.loader.cache", "true");
	    p.setProperty("runtime.log.logsystem.log4j.logger","Apache Velocity");
	    Velocity.init( p );
	}
	
	public String getBasePackageName(){
		return this.packageRoot + ( Boolean.valueOf( properties.getProperty( "include.schema.in.package.name", "true" ) ) ? "." + domainClass.getSchema().getLowerLabel() : "" );
	}
	public String getPackageName(){
		return this.packageRoot + ( Boolean.valueOf( properties.getProperty( "include.schema.in.package.name", "true" ) ) ? "." + domainClass.getSchema().getLowerLabel() : "" ) + ".controller" ;
    }
	
	public String getPathPrefix(){
		if ( Boolean.valueOf( properties.getProperty( "include.schema.in.request.mapping", "true" ) ) ) {
			return  "/" + domainClass.getSchema().getLowerLabel() + "/" + domainClass.getLowerIdentifier().toLowerCase();
		} else {
			return "/" + domainClass.getLowerIdentifier().toLowerCase();
		}
    }
	public String getJspPath(){
		return "/" + domainClass.getSchema().getLowerLabel() + "/" + domainClass.getLowerIdentifier().toLowerCase();
    }
	
	public String getPathExtension(){
		return properties.getProperty( "controller.request.mapping.extension", "" );
	}
	protected void addDaoServiceNameToVelocityContext(VelocityContext context) {
		String daoServiceName = properties.getProperty( domainClass.getSchema().getUpperLabel().toLowerCase() + ".master.dao.service.name" );
		if( daoServiceName == null || "".equals( daoServiceName.trim() ) ){
			daoServiceName = domainClass.getSchema().getLowerLabel() + "DaoService";
		} else {
			daoServiceName = StringUtils.substring( daoServiceName, 0, 1 ).toLowerCase() + StringUtils.substring( daoServiceName, 1, daoServiceName.length() );
		}
		context.put( "daoServiceName", daoServiceName );
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
}
