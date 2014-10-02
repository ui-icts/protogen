package edu.uiowa.icts.protogen.springhibernate.velocity;

import java.util.Properties;

import org.apache.velocity.app.Velocity;

import edu.uiowa.icts.protogen.springhibernate.DomainClass;

public abstract class AbstractVelocityGenerator {
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
	
}
