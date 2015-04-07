package edu.uiowa.icts.protogen;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.uiowa.webapp.Generator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author rrlorent
 * Unit test for simple App.
 */
public class JDBCTest extends TestCase {

	private static final Log log = LogFactory.getLog( JDBCTest.class );

	/**
	 * Create the test case
	 * @param testName name of the test case
	 */
	public JDBCTest( String testName ) {
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite( JDBCTest.class );
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
		props.setProperty( "generate.domain", "true" );
		props.setProperty( "generate.dao", "true" );
		props.setProperty( "generate.controller", "true" );
		props.setProperty( "generate.jsp", "true" );
		props.setProperty( "generate.tests", "true" );
		props.setProperty( "domain.file.location", pathPrefix + "/target/jdbc/test/java" + "src" );
		props.setProperty( "dao.file.location", pathPrefix + "/target/jdbc/test/java" + "src" );
		props.setProperty( "controller.file.location", pathPrefix + "/target/jdbc/test/java" + "src" );
		props.setProperty( "jsp.file.location", pathPrefix + "/target/jdbc/test/jsp" + "src" );
		props.setProperty( "test.file.location", pathPrefix + "/target/jdbc/test/javatest" + "src" );

		props.setProperty( "generate.templates.xml", "true" );
		props.setProperty( "overwrite.templates.xml", "true" );
		props.setProperty( "templates.xml.file.location", pathPrefix + "/target/jdbc/test/src/main/resources/tiles/" );
		
		props.setProperty( "model.source", "jdbc" );
		props.setProperty( "db.schema", "hero" );
		props.setProperty( "db.url", "jdbc:postgresql://localhost/test" );
		props.setProperty( "db.username", "test" );
		props.setProperty( "db.password", "test" );
		props.setProperty( "db.ssl", "false" );
		props.setProperty( "db.driver", "org.postgresql.Driver" );

		Generator gen = new Generator();
		int result = gen.runGenerator( props );
		log.debug( "result = " + result );
		assertEquals( "Error during domain code generation", 0, 0 );

	}
}
