package edu.uiowa.icts.protogen.springhibernate;

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
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.uiowa.icts.protogen.springhibernate.velocity.AbstractResourceGenerator;
import edu.uiowa.webapp.Schema;

/**
 * @author rrlorent, rmjames
 * Generates spring controller java files
 */
public class AbstractResourceCodeGenerator extends AbstractSpringHibernateCodeGenerator {

	protected static final Log log = LogFactory.getLog( AbstractResourceCodeGenerator.class );

	private Properties properties;

	public AbstractResourceCodeGenerator( SpringHibernateModel model, String pathBase, String packageRoot, Properties properties ) {
		super( model, pathBase, packageRoot );
		( new File( packageRootPath ) ).mkdirs();
		this.properties = properties;
	}

	/*
	 * generates abstract controllers
	 */
	private void generateAbstractResources() throws IOException {
		for ( Schema schema : model.getSchemaMap().keySet() ) {
			List<DomainClass> domainClassList = model.getSchemaMap().get( schema );
			if ( domainClassList != null && domainClassList.isEmpty() == false ) {
				generateAbstractResource( schema, model.getPackageRoot(), properties );
			}
		}
	}

	/**
	 * generates an abstract controller
	 */
	private void generateAbstractResource( Schema schema, String packageRoot, Properties properties ) throws IOException {
		String packageName = model.getPackageRoot() + ( Boolean.valueOf( properties.getProperty( "include.schema.in.package.name", "true" ) ) ? "." + schema.getLowerLabel() : "" ) + ".web";
		String packagePath = pathBase + "/" + packageName.replaceAll( "\\.", "/" );

		// generate abstract resource
		AbstractResourceGenerator generator = new AbstractResourceGenerator( schema, packageRoot, properties );

		String abstractControllerClassName = properties.getProperty( schema.getLabel().toLowerCase() + ".abstract.resource.name" );
		if ( abstractControllerClassName == null ) {
			abstractControllerClassName = "Abstract" + schema.getUpperLabel() + "Resource";
		}

		BufferedWriter out = createFileInSrcElseTarget( packagePath, abstractControllerClassName + ".java" );
		try {
			out.write( generator.javaSourceCode() );
		} finally {
			out.close();
		}
	}

	/*
	 * Public Function to generate java domain code
	 */
	public void generate() throws IOException {
		generateAbstractResources();
	}

}
