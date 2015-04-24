package edu.uiowa.icts.protogen.springhibernate.velocity;

import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import edu.uiowa.webapp.Schema;

public class AbstractControllerMVCTestsGenerator extends AbstractVelocityGenerator {

	public AbstractControllerMVCTestsGenerator( Schema schema, String packageRoot, Properties properties ) {
		super( schema, packageRoot, properties );
	}

	public String javaSourceCode() {

		VelocityContext context = new VelocityContext();
		context.put( "display", new org.apache.velocity.tools.generic.DisplayTool() );
		context.put( "packageName", getControllerPackageName() );
		context.put( "date", new Date().toString() );
		context.put( "daoPackageName", getDaoPackageName() );
		context.put( "daoServiceClassName", getDaoServiceClassName() );

		addDaoServiceNameToVelocityContext( context );

		/* lets render a template loaded from the classpath */
		StringWriter writer = new StringWriter();
		Velocity.mergeTemplate( "/velocity-templates/AbstractControllerMVCTests.java", Velocity.ENCODING_DEFAULT, context, writer );

		return writer.toString();
	}

}
