package edu.uiowa.icts.protogen.springhibernate.velocity;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import edu.uiowa.icts.protogen.springhibernate.ClassVariable;
import edu.uiowa.icts.protogen.springhibernate.ClassVariable.AttributeType;
import edu.uiowa.icts.protogen.springhibernate.DomainClass;
import edu.uiowa.webapp.Attribute;

/**
 * Generate spring rest controllers using velocity templates.
 * @author rmjames
 * @since June 26, 2015
 */
public class ResourceGenerator extends AbstractVelocityGenerator {

	public ResourceGenerator( String packageRoot, DomainClass domainClass, Properties properties ) {
		super( packageRoot, domainClass, properties );
	}

	public String javaSourceCode() {

		SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy HH:mm:ss z", Locale.US );
		sdf.setTimeZone( TimeZone.getDefault() );

		String packageName = getControllerPackageName();

		String className = domainClass.getIdentifier() + "Resource";
//		String controllerName = packageName.replaceAll( "\\.", "_" ) + "_" + domainClass.getIdentifier().toLowerCase() + "_controller";

		/* lets make a Context and put data into it */
		VelocityContext context = new VelocityContext();
//		context.put( "domainClass", domainClass );
//		context.put( "controllerName", controllerName );
//		context.put( "date", sdf.format( new Date() ) ); // can be done with Velocity tools but let's keep it simple to start
		context.put( "packageName", getBasePackageName() + ".resource" );
		context.put( "className", className );
		context.put( "pathPrefix", properties.getProperty( "rest.api.url") + getPathPrefix() );
		context.put( "domainName", domainClass.getIdentifier() );
		context.put( "lowerDomainName", domainClass.getLowerIdentifier() );

		String abstractControllerClassName = properties.getProperty( domainClass.getSchema().getLabel().toLowerCase() + ".abstract.resource.name" );
		if ( abstractControllerClassName == null ) {
			abstractControllerClassName = "Abstract" + domainClass.getSchema().getUpperLabel() + "Resource";
		}
		context.put( "abstractControllerClassName", abstractControllerClassName );

		addDaoServiceNameToVelocityContext( context );

		context.put( "domainPackageName", packageRoot + "." + domainClass.getSchema().getLowerLabel() + ".domain" );
//
//
//		context.put( "requestParameterIdentifier", requestParameterIdentifier() );
//		context.put( "addEditListDependencies", addEditListDependencies() );
//		context.put( "newCompositeKey", newCompositeKey() );
//		context.put( "compositeKey", compositeKey() );
//		context.put( "compositeKeySetter", compositeKeySetter() );
//		context.put( "foreignClassParameters", foreignClassParameters() );
//		context.put( "foreignClassSetters", foreignClassSetters() );

		/* render a template loaded from the classpath */
		StringWriter writer = new StringWriter();

		Velocity.mergeTemplate( "/velocity-templates/Resource.java", Velocity.ENCODING_DEFAULT, context, writer );

		return writer.toString();

	}

}