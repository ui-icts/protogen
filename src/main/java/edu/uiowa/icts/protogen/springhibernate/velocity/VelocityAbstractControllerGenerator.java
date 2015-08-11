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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import edu.uiowa.webapp.Schema;

/**
 * Generate spring abstract controllers using velocity templates.
 * @author rrlorent
 * @since April 23, 2015
 */
public class VelocityAbstractControllerGenerator extends AbstractVelocityGenerator {

	public VelocityAbstractControllerGenerator( Schema schema, String packageRoot, Properties properties ) {
		super( schema, packageRoot, properties );
	}

	public String javaSourceCode() {

		SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy HH:mm:ss z", Locale.US );
		sdf.setTimeZone( TimeZone.getDefault() );

		VelocityContext context = new VelocityContext();
		context.put( "packageName", getControllerPackageName() );
		context.put( "abstractResourcePackageName", getBasePackageName() + ".web");
		context.put( "date", sdf.format( new Date() ) ); // can be done with Velocity tools but let's keep it simple to start

		String abstractControllerClassName = properties.getProperty( schema + ".abstract.controller.name" );
		if ( abstractControllerClassName == null ) {
			abstractControllerClassName = "Abstract" + schema.getUpperLabel() + "Controller";
		}
		context.put( "abstractControllerClassName", abstractControllerClassName );
		context.put( "abstractResourceClassName", getAbstractResourceClassName() );
		
		StringWriter writer = new StringWriter();
		Velocity.mergeTemplate( "/velocity-templates/AbstractController.java", Velocity.ENCODING_DEFAULT, context, writer );
		return writer.toString();

	}

}