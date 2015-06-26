package edu.uiowa.icts.protogen.springhibernate.velocity;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import edu.uiowa.webapp.Schema;

/**
 * Generate spring abstract controllers using velocity templates.
 * @author rrlorent
 * @since April 23, 2015
 */
public class AbstractResourceGenerator extends AbstractVelocityGenerator {

	public AbstractResourceGenerator( Schema schema, String packageRoot, Properties properties ) {
		super( schema, packageRoot, properties );
	}

	public String javaSourceCode() {

		SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy HH:mm:ss z", Locale.US );
		sdf.setTimeZone( TimeZone.getDefault() );

		VelocityContext context = new VelocityContext();
		context.put( "packageName", getBasePackageName() + ".resource");
		context.put( "controllerPackageName", getControllerPackageName() );
		context.put( "date", sdf.format( new Date() ) ); // can be done with Velocity tools but let's keep it simple to start
		
		String abstractControllerClassName = properties.getProperty( schema + ".abstract.controller.name" );
		if ( abstractControllerClassName == null ) {
			abstractControllerClassName = "Abstract" + schema.getUpperLabel() + "Controller";
		}
		context.put( "abstractControllerClassName", abstractControllerClassName );


		String abstractResourceClassName = properties.getProperty( schema + ".abstract.resource.name" );
		if ( abstractResourceClassName == null ) {
			abstractResourceClassName = "Abstract" + schema.getUpperLabel() + "Resource";
		}
		context.put( "abstractResourceClassName", abstractResourceClassName );
		
		StringWriter writer = new StringWriter();
		Velocity.mergeTemplate( "/velocity-templates/AbstractResource.java", Velocity.ENCODING_DEFAULT, context, writer );
		return writer.toString();

	}

}