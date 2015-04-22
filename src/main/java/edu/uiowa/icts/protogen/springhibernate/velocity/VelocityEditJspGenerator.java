package edu.uiowa.icts.protogen.springhibernate.velocity;

import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import edu.uiowa.icts.protogen.springhibernate.DomainClass;

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

		/* lets make a Context and put data into it */
		VelocityContext context = new VelocityContext();
		context.put( "domainClass", this.domainClass );
		context.put( "pathExtension", this.getPathExtension() );
		context.put( "deOb", Boolean.parseBoolean( this.properties.getProperty( "deobfuscate.column.names", "false" ) ) );

		boolean includeSchema = Boolean.parseBoolean( this.properties.getProperty( "include.schema.in.request.mapping", "true" ) );
		if ( includeSchema ) {
			context.put( "pathPrefix", "/" + this.domainClass.getSchema().getLowerLabel() );
		} else {
			context.put( "pathPrefix", "" );
		}

		// in the Velocity template, ${esc.d} will get converted to $ 
		context.put( "esc", new org.apache.velocity.tools.generic.EscapeTool() );

		/* render a template loaded from the classpath */
		StringWriter writer = new StringWriter();

		Velocity.mergeTemplate( "/velocity-templates/edit.jsp", Velocity.ENCODING_DEFAULT, context, writer );
		return writer.toString();
	}
}