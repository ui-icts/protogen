package edu.uiowa.icts.protogen.springhibernate.velocity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNull;

import java.util.Properties;

import org.junit.Test;

import edu.uiowa.icts.protogen.springhibernate.DomainClass;
import edu.uiowa.icts.protogen.springhibernate.SpringHibernateModel;
import edu.uiowa.webapp.ClayLoader;
import edu.uiowa.webapp.Database;
import edu.uiowa.webapp.DatabaseSchemaLoader;
import edu.uiowa.webapp.Schema;

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
			assertNull( e );
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
		assertThat( sourceCode, containsString( "public class JobTypeResource extends AbstractAptamerResource {"));
		
		// generate show by id
		assertThat( sourceCode, containsString( "@RequestMapping( value = { \"{jobTypeId}\" }, method = RequestMethod.GET, produces = \"application/json\"  )" ) );
		assertThat( sourceCode, containsString( "public JobType get(@PathVariable( \"jobTypeId\" ) Integer jobTypeId ) {" ) );
		assertThat( sourceCode, containsString( "return aptamerDaoService.getJobTypeService().findById( jobTypeId );" ) );
		
		// generate create
		
		// generate save
		
		// generate delete
		
		// generate list all

		
	}
}
