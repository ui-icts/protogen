package edu.uiowa.icts.protogen.springhibernate.velocity;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import edu.uiowa.icts.protogen.springhibernate.ClassVariable;
import edu.uiowa.icts.protogen.springhibernate.DomainClass;

public class ResourceMvcTestGenerator extends AbstractVelocityGenerator {

	public ResourceMvcTestGenerator( String packageRoot, DomainClass domainClass, Properties properties ) {
		super( packageRoot, domainClass, properties );
	}

	public String javaSourceCode() {

		VelocityContext context = new VelocityContext();
		context.put( "display", new org.apache.velocity.tools.generic.DisplayTool() );
		context.put( "basePackageName", getBasePackageName() );
		context.put( "packageName", getBasePackageName() + ".resource" );
		context.put( "controllerPackageName", getControllerPackageName() );
		context.put( "date", new Date().toString() ); // can be done with Velocity tools but let's keep it simple to start
		context.put( "className", domainClass.getIdentifier() );
		context.put( "pathPrefix", properties.getProperty( "rest.api.url") + getPathPrefix() );
//		context.put( "jspPath", getJspPath() );
//		context.put( "pathExtension", getPathExtension() );
		addDaoServiceNameToVelocityContext( context );
		context.put( "domainClass", domainClass );

		List<String> columnNamesList = new ArrayList<String>();
		Iterator<ClassVariable> iter = domainClass.listAllIter();
		while ( iter.hasNext() ) {
			ClassVariable cv = iter.next();
		//	if ( !cv.isPrimary() ) {
			columnNamesList.add( cv.getLowerIdentifier() );
		//	}
		}
		Collections.sort( columnNamesList );
	    context.put( "columnNamesList", columnNamesList );

		/* lets render a template loaded from the classpath */
		StringWriter w = new StringWriter();
		Velocity.mergeTemplate( "/velocity-templates/ResourceMvcTest.java", Velocity.ENCODING_DEFAULT, context, w );
		return w.toString();

	}

}