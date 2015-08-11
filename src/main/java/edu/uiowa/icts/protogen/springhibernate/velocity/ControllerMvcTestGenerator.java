package edu.uiowa.icts.protogen.springhibernate.velocity;

/*
 * #%L
 * Protogen
 * %%
 * Copyright (C) 2009 - 2015 University of Iowa Institute for Clinical and Translational Science (ICTS)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import edu.uiowa.icts.protogen.springhibernate.ClassVariable;
import edu.uiowa.icts.protogen.springhibernate.DomainClass;

public class ControllerMvcTestGenerator extends AbstractVelocityGenerator {

	public ControllerMvcTestGenerator( String packageRoot, DomainClass domainClass, Properties properties ) {
		super( packageRoot, domainClass, properties );
	}

	public String javaSourceCode() {

		VelocityContext context = new VelocityContext();
		context.put( "display", new org.apache.velocity.tools.generic.DisplayTool() );
		context.put( "basePackageName", getBasePackageName() );
		context.put( "packageName", getControllerPackageName() );
		context.put( "date", new Date().toString() ); // can be done with Velocity tools but let's keep it simple to start
		context.put( "className", domainClass.getIdentifier() );
		context.put( "pathPrefix", getPathPrefix() );
		context.put( "jspPath", getJspPath() );
		context.put( "pathExtension", getPathExtension() );
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
		Velocity.mergeTemplate( "/velocity-templates/ControllerMvcTest.java", Velocity.ENCODING_DEFAULT, context, w );
		return w.toString();

	}

}
