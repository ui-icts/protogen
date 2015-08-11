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

import edu.uiowa.icts.protogen.springhibernate.velocity.AbstractApiResourceGenerator;
import edu.uiowa.icts.protogen.springhibernate.velocity.DefaultResourceGenerator;
import edu.uiowa.icts.protogen.springhibernate.velocity.ResourceGenerator;
import edu.uiowa.icts.protogen.springhibernate.velocity.ResourceMvcTestGenerator;
import edu.uiowa.webapp.Schema;

/**
 * @author rrlorent, rmjames
 * Generates spring rest api resource java files
 */
public class ResourceCodeGenerator extends AbstractSpringHibernateCodeGenerator {

	protected static final Log log = LogFactory.getLog( ResourceCodeGenerator.class );

	private Properties properties;

	public ResourceCodeGenerator( SpringHibernateModel model, String pathBase, String packageRoot, Properties properties ) {
		super( model, pathBase, packageRoot );
		( new File( packageRootPath ) ).mkdirs();
		this.properties = properties;
	}

	private void generateResource( DomainClass domainClass ) throws IOException {

		String packageName = model.getPackageRoot() + ( Boolean.valueOf( properties.getProperty( "include.schema.in.package.name", "true" ) ) ? "." + domainClass.getSchema().getLowerLabel() : "" ) + ".resource";

		String packagePath = pathBase + "/" + packageName.replaceAll( "\\.", "/" );

		String className = domainClass.getIdentifier() + "Resource";

		if ( Boolean.valueOf( properties.getProperty( "generate.test", "false" ) ) ) {
			// Generate corresponding Spring MVC test file
			ResourceMvcTestGenerator generator = new ResourceMvcTestGenerator( model.getPackageRoot(), domainClass, properties );
			BufferedWriter testWriter = createFileInSrcElseTarget( packagePath.replaceFirst( "src/main", "src/test" ), className + "MvcTest.java" );
			try {
				testWriter.write( generator.javaSourceCode() );
			} finally {
				testWriter.close();
			}
		}

		ResourceGenerator vcg = new ResourceGenerator( model.getPackageRoot(), domainClass, properties );
		BufferedWriter controllerWriter = createFileInSrcElseTarget( packagePath, className + ".java" );
		try {
			controllerWriter.write( vcg.javaSourceCode() );
		} finally {
			controllerWriter.close();
		}

		return;
	}

	/*
	 * generates abstract controllers
	 */
	private void generateAbstractControllers() throws IOException {
		for ( Schema schema : model.getSchemaMap().keySet() ) {
			List<DomainClass> domainClassList = model.getSchemaMap().get( schema );
			if ( domainClassList != null && domainClassList.isEmpty() == false ) {
				generateDefaultResource( schema, model.getPackageRoot(), properties );
				generateAbstractResource( schema, model.getPackageRoot(), properties );
			}
		}
	}

	/**
	 * generates an default controller
	 */
	private void generateDefaultResource( Schema schema, String packageRoot, Properties properties ) throws IOException {

		String packageName = model.getPackageRoot() + ( Boolean.valueOf( properties.getProperty( "include.schema.in.package.name", "true" ) ) ? "." + schema.getLowerLabel() : "" ) + ".resource";
		String packagePath = pathBase + "/" + packageName.replaceAll( "\\.", "/" );

		// generate abstract resource
		DefaultResourceGenerator generator = new DefaultResourceGenerator( schema, packageRoot, properties );
		BufferedWriter out = createFileInSrcElseTarget( packagePath, "DefaultResource.java" );
		try {
			out.write( generator.javaSourceCode() );
		} finally {
			out.close();
		}
	}
	/**
	 * generates an abstract controller
	 */
	private void generateAbstractResource( Schema schema, String packageRoot, Properties properties ) throws IOException {

		String packageName = model.getPackageRoot() + ( Boolean.valueOf( properties.getProperty( "include.schema.in.package.name", "true" ) ) ? "." + schema.getLowerLabel() : "" ) + ".resource";
		String packagePath = pathBase + "/" + packageName.replaceAll( "\\.", "/" );

		// generate abstract resource
		AbstractApiResourceGenerator generator = new AbstractApiResourceGenerator( schema, packageRoot, properties );

		String abstractClassName = properties.getProperty( schema.getLabel().toLowerCase() + ".abstract.resource.name" + "Api");
		if ( abstractClassName == null ) {
			abstractClassName = "Abstract" + schema.getUpperLabel() + "ApiResource";
		}

		BufferedWriter out = createFileInSrcElseTarget( packagePath, abstractClassName + ".java" );
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
		for ( DomainClass dc : model.getDomainClassList() ) {
			// only generate REST API for tables with single column primary keys generated from sequences
			if (!dc.isUsesCompositeKey() && dc.getPrimaryKey() != null && dc.getPrimaryKey().getType() != null && dc.getPrimaryKey().getType().equals("Integer")){
				generateResource( dc );
			}
		}
		generateAbstractControllers();
	}

}
