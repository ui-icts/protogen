package edu.uiowa.icts.protogen.tiles;

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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import edu.uiowa.icts.protogen.springhibernate.SpringHibernateModel;

/**
 * @author rrlorent
 * @since October 9, 2014
 */
public class TilesTemplatesXMLGenerator {

	private static final Log log = LogFactory.getLog( TilesTemplatesXMLGenerator.class );

	private Properties properties;
	private String filePath;
	private SpringHibernateModel model;

	public TilesTemplatesXMLGenerator( SpringHibernateModel model, String filePath, Properties properties ) {

		this.properties = properties;
		this.filePath = filePath;
		this.model = model;

		Properties velocityProperties = new Properties();
		velocityProperties.setProperty( "resource.loader", "class" );
		velocityProperties.setProperty( "class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader" );
		velocityProperties.setProperty( "class.resource.loader.cache", "true" );
		velocityProperties.setProperty( "runtime.log.logsystem.log4j.logger", "Apache Velocity" );
		Velocity.init( velocityProperties );

	}

	public void generate() throws IOException {

		log.debug( "creating templates xml at :: " + filePath );

		( new File( filePath ) ).mkdirs();

		File templates = ( new File( filePath + ( filePath.endsWith( "/" ) ? "" : "/" ) + "templates.xml" ) );

		if ( !Boolean.parseBoolean( properties.getProperty( "overwrite.templates.xml", "false" ) ) && templates.exists() ) {

			log.debug( "templates xml already exists at :: " + templates.getAbsolutePath() );

		} else {

			FileWriter fileWriter = new FileWriter( templates );
			BufferedWriter out = new BufferedWriter( fileWriter );

			SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy HH:mm:ss z", Locale.US );
			sdf.setTimeZone( TimeZone.getDefault() );

			VelocityContext context = new VelocityContext();
			context.put( "date", sdf.format( new Date() ) );

			if ( Boolean.valueOf( properties.getProperty( "include.schema.in.jsp.path", "true" ) ) ) {
				context.put( "includeSchemaInJspPath", true );
				context.put( "schemaMap", model.getSchemaMap() );
			} else {
				context.put( "includeSchemaInJspPath", false );
			}

			StringWriter writer = new StringWriter();

			Velocity.mergeTemplate( "/velocity-templates/templates.xml", Velocity.ENCODING_DEFAULT, context, writer );

			out.write( writer.toString() );

			writer.close();

			out.flush();
			fileWriter.flush();

			out.close();
			fileWriter.close();

		}

	}

}