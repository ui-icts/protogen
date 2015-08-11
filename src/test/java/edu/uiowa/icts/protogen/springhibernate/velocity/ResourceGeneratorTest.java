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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.Test;

import edu.uiowa.icts.protogen.springhibernate.DomainClass;
import edu.uiowa.icts.protogen.springhibernate.SpringHibernateModel;
import edu.uiowa.webapp.ClayLoader;
import edu.uiowa.webapp.Database;
import edu.uiowa.webapp.DatabaseSchemaLoader;

public class ResourceGeneratorTest {

	@Test
	public void shouldGenerateRestMethods() {
		String packageRoot = "edu.uiowa.icts";
		String pathPrefix = System.getProperty( "user.dir" );

		Properties properties = new Properties();
		properties.setProperty( "include.schema.in.request.mapping", "false" );
		properties.setProperty( "rest.api.url", "/api" );

		DatabaseSchemaLoader theLoader = new ClayLoader();
		try {
			theLoader.run( pathPrefix + "/src/test/resources/mvc-test-generator.clay" );
		} catch ( Exception e ) {
			fail( e.getMessage() );
		}
		
		Database database = theLoader.getDatabase();
		//   database.dump();
		SpringHibernateModel model = new SpringHibernateModel( database, packageRoot, properties );

		DomainClass jobType = null;
		for ( DomainClass dc : model.getDomainClassList() ) {
			if ( dc.getIdentifier().equals( "JobType" ) ) {
				jobType = dc;
			}
		}

		ResourceGenerator generator = new ResourceGenerator( packageRoot, jobType, properties );

		String sourceCode = generator.javaSourceCode();

		assertThat( sourceCode, containsString( "package edu.uiowa.icts.aptamer.resource;" ) );
		assertThat( sourceCode, containsString( "@RestController" ) );
		assertThat( sourceCode, containsString( "@RequestMapping( \"/api/jobtype\" )" ) );
		assertThat( sourceCode, containsString( "public class JobTypeResource extends AbstractAptamerApiResource {" ) );

		// generate show by id
		assertThat( sourceCode, containsString( "@RequestMapping( value = { \"{jobTypeId}\" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )" ) );
		assertThat( sourceCode, containsString( "public JobType get( @PathVariable( \"jobTypeId\" ) Integer jobTypeId ) {" ) );
		assertThat( sourceCode, containsString( "return jobType;" ) );

		// generate create

		// generate save

		// generate delete

		// generate list all

	}
}
