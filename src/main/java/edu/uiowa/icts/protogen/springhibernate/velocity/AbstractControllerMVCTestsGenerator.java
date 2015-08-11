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
