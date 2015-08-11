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

import edu.uiowa.icts.protogen.springhibernate.DomainClass;

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

		String packageName = getRestApiResourcePackageName();

		String className = domainClass.getIdentifier() + "Resource";
		String resourceName = packageName.replaceAll( "\\.", "_" ) + "_" + domainClass.getIdentifier().toLowerCase() + "_resource";

		VelocityContext context = new VelocityContext();
		context.put( "domainClass", domainClass );
		context.put( "resourceName", resourceName );
		context.put( "date", sdf.format( new Date() ) );
		context.put( "packageName", getBasePackageName() + ".resource" );
		context.put( "className", className );
		context.put( "pathPrefix", properties.getProperty( "rest.api.url", "/rest" ) + getPathPrefix() );
		context.put( "domainName", domainClass.getIdentifier() );
		context.put( "lowerDomainName", domainClass.getLowerIdentifier() );
		context.put( "abstractApiResourceClassName", getAbstractApiResourceClassName() );
		context.put( "domainPackageName", packageRoot + "." + domainClass.getSchema().getLowerLabel() + ".domain" );

		addDaoServiceNameToVelocityContext( context );

		//		context.put( "requestParameterIdentifier", requestParameterIdentifier() );
		//		context.put( "addEditListDependencies", addEditListDependencies() );
		//		context.put( "newCompositeKey", newCompositeKey() );
		//		context.put( "compositeKey", compositeKey() );
		//		context.put( "compositeKeySetter", compositeKeySetter() );
		//		context.put( "foreignClassParameters", foreignClassParameters() );
		//		context.put( "foreignClassSetters", foreignClassSetters() );

		StringWriter writer = new StringWriter();

		/* render a template loaded from the classpath */
		Velocity.mergeTemplate( "/velocity-templates/Resource.java", Velocity.ENCODING_DEFAULT, context, writer );

		return writer.toString();

	}

}