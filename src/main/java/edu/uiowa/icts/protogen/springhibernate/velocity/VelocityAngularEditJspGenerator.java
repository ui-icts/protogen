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
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import edu.uiowa.icts.plugin.protogen.util.GeneratorUtil;
import edu.uiowa.icts.protogen.springhibernate.DomainClass;

/**
 * Generate spring controllers using velocity templates.
 * @author rrlorent
 * @since August 4, 2014
 */
public class VelocityAngularEditJspGenerator extends AbstractVelocityGenerator {

	public VelocityAngularEditJspGenerator( String packageRoot, DomainClass domainClass, Properties properties ) {
		super( packageRoot, domainClass, properties );
	}

	public String javaSourceCode() {

		/* lets make a Context and put data into it */
		VelocityContext context = new VelocityContext();
		context.put( "domainClass", this.domainClass );
		context.put( "domainClassLabel", splitCapitalizedWords( this.domainClass.getIdentifier() ) );
		context.put( "pathExtension", this.getPathExtension() );
		context.put( "deOb", Boolean.parseBoolean( this.properties.getProperty( "deobfuscate.column.names", "false" ) ) );

		boolean includeSchema = Boolean.parseBoolean( this.properties.getProperty( "include.schema.in.request.mapping", "true" ) );
		if ( includeSchema ) {
			context.put( "pathPrefix", "/" + this.domainClass.getSchema().getLowerLabel() );
		} else {
			context.put( "pathPrefix", "" );
		}

		context.put( "generatorUtil", new GeneratorUtil() );

		// in the Velocity template, ${esc.d} will get converted to $ 
		context.put( "esc", new org.apache.velocity.tools.generic.EscapeTool() );

		/* render a template loaded from the classpath */
		StringWriter writer = new StringWriter();

		Velocity.mergeTemplate( "/velocity-templates/edit-angular-fields.jsp", Velocity.ENCODING_DEFAULT, context, writer );
		return writer.toString();
	}
}