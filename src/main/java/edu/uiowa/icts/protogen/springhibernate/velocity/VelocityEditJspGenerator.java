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
import edu.uiowa.icts.protogen.springhibernate.DomainClass;
import edu.uiowa.webapp.Attribute;

/**
 * Generate spring controllers using velocity templates.
 * @author rrlorent
 * @since August 4, 2014
 */
public class VelocityEditJspGenerator extends AbstractVelocityGenerator {

	public VelocityEditJspGenerator( String packageRoot, DomainClass domainClass, Properties properties ) {
		super( packageRoot, domainClass, properties );
	}
    
	public String javaSourceCode() {

//		SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy HH:mm:ss z", Locale.US );
//		sdf.setTimeZone( TimeZone.getDefault() );

		/* lets make a Context and put data into it */
		VelocityContext context = new VelocityContext();
		context.put( "domainClass", this.domainClass );
		context.put( "pathExtension", this.getPathExtension());
		context.put("deOb", Boolean.parseBoolean( this.properties.getProperty( "deobfuscate.column.names", "false" ) ));
		// in the Velocity template, ${esc.d} will get converted to $ 
	    context.put("esc", new org.apache.velocity.tools.generic.EscapeTool());

		/* render a template loaded from the classpath */
		StringWriter writer = new StringWriter();
		
		Velocity.mergeTemplate( "/velocity-templates/edit.jsp", Velocity.ENCODING_DEFAULT, context, writer );
		return writer.toString();
	}
}