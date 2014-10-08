package edu.uiowa.icts.protogen.springhibernate.velocity;

import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import edu.uiowa.icts.protogen.springhibernate.DomainClass;

public class ControllerMvcTestGenerator extends AbstractVelocityGenerator {

	public ControllerMvcTestGenerator( String packageRoot, DomainClass domainClass, Properties properties ) {
		super( packageRoot, domainClass, properties );
	}

	public String javaSourceCode() {
		/* lets make a Context and put data into it */
		VelocityContext context = new VelocityContext();
		context.put( "basePackageName", this.getBasePackageName() );
		context.put( "packageName", this.getPackageName() );
		context.put( "date", new Date().toString() ); // can be done with Velocity tools but let's keep it simple to start
		context.put( "className", this.domainClass.getIdentifier() );
		context.put( "pathPrefix", this.getPathPrefix() );
		context.put( "jspPath", this.getJspPath() );
		context.put( "pathExtension", this.getPathExtension());
		addDaoServiceNameToVelocityContext(context);
		context.put( "domainClass", this.domainClass );	

		/* lets render a template loaded from the classpath */
		StringWriter w = new StringWriter();
		Velocity.mergeTemplate( "/velocity-templates/ControllerMvcTest.java", Velocity.ENCODING_DEFAULT, context, w );
		return w.toString();

	}

}
