package edu.uiowa.icts.protogen;

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

import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.uiowa.webapp.Generator;

/**
 * Unit test for simple web application.
 */
public class GeneratorTest extends TestCase {

	private static final Log log = LogFactory.getLog( GeneratorTest.class );

	/**
	 * Create the test case
	 * @param testName name of the test case
	 */
	public GeneratorTest( String testName ) {
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite( GeneratorTest.class );
	}

	/**
	 * Tests Hibernate domain code generation
	 */
	public void testHibernateCodeGeneration() {

		String projectName = "Protogen";
		String pathPrefix = System.getProperty( "user.dir" );
		log.debug( "PathPrefix:" + pathPrefix );

		Properties props = new Properties();
		props.setProperty( "package.name", "protogen.test" );
		props.setProperty( "project.name", projectName );
		props.setProperty( "mode", "spring" );
		props.setProperty( "model.source", "clay" );
		props.setProperty( "clay.file", pathPrefix + "/src/test/resources/ryanlorentzen.clay" );
		props.setProperty( "generate.domain", "true" );
		props.setProperty( "generate.dao", "true" );
		props.setProperty( "generate.controller", "true" );
		props.setProperty( "generate.jsp", "true" );
		props.setProperty( "generate.tests", "true" );

		props.setProperty( "rest.api.file.location", pathPrefix + "/target/clay/test/java/resource" );
		props.setProperty( "domain.file.location", pathPrefix + "/target/clay/test/java/doamin" );
		props.setProperty( "dao.file.location", pathPrefix + "/target/clay/test/java/dao" );
		props.setProperty( "controller.file.location", pathPrefix + "/target/clay/test/java/controller" );
		props.setProperty( "jsp.file.location", pathPrefix + "/target/clay/test/jsp" );
		props.setProperty( "test.file.location", pathPrefix + "/target/clay/test/javatest" );

		props.setProperty( "generate.templates.xml", "true" );
		props.setProperty( "overwrite.templates.xml", "true" );
		props.setProperty( "templates.xml.file.location", pathPrefix + "/target/clay/test/src/main/resources/tiles/" );

		props.setProperty( "datatables.generation", "2" );
		props.setProperty( "controller.request.mapping.extension", ".html" );

		props.setProperty( "include.schema.in.request.mapping", "true" );
		props.setProperty( "include.schema.in.jsp.path", "false" );
		props.setProperty( "include.schema.in.package.name", "false" );

		props.setProperty( "ryanlorentzen.master.dao.service.name", "FooBarDaoService" );
		props.setProperty( "ryanlorentzen.abstract.controller.name", "SomeOtherAbstractControllerName" );

		props.setProperty( "deobfuscate.tld.file.location", pathPrefix + "/target/clay/test/resources/META-INF/" );
		props.setProperty( "deobfuscate.column.names", "true" );
		props.setProperty( "dictionary.table.name", "MST_DICT" );
		props.setProperty( "dictionary.table.columnname", "DICT_TABLE_NAME" );
		props.setProperty( "dictionary.column.columnname", "DICT_COL_NAME" );
		props.setProperty( "dictionary.deobfuscated.columnname", "DICT_COL_DESC" );

		int result = ( new Generator() ).runGenerator( props );
		assertEquals( "Error during domain code generation", 0, result );

	}

	/**
	 * Verify the generation 2 data tables method and list.jsp generates properly using velocity.
	 */
	public void generation2Datatable() {

		String projectName = "Protogen";
		String pathPrefix = System.getProperty( "user.dir" );
		log.debug( "PathPrefix:" + pathPrefix );

		Properties props = new Properties();
		props.setProperty( "package.name", "datatable.generation2.test" );
		props.setProperty( "project.name", projectName );
		props.setProperty( "mode", "spring" );
		props.setProperty( "model.source", "clay" );
		props.setProperty( "clay.file", pathPrefix + "/src/test/resources/ryanlorentzen.clay" );

		props.setProperty( "generate.controller", "true" );
		props.setProperty( "generate.jsp", "true" );
		props.setProperty( "generate.tests", "true" );

		props.setProperty( "generate.domain", "false" );
		props.setProperty( "generate.dao", "false" );

		props.setProperty( "templates.xml.file.location", pathPrefix + "/target/clay/test/src/main/resources/tiles/" );

		props.setProperty( "controller.file.location", pathPrefix + "/target/clay/test/javasrc" );
		props.setProperty( "jsp.file.location", pathPrefix + "/target/clay/test/jspsrc/generation2" );
		props.setProperty( "test.file.location", pathPrefix + "/target/clay/test/javatestsrc" );

		props.setProperty( "datatables.generation", "2" );

		props.setProperty( "deobfuscate.column.names", "false" );

		int result = ( new Generator() ).runGenerator( props );
		assertEquals( "Error during generation 2 controller code generation", 0, result );

	}

}