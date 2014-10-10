package edu.uiowa.icts.protogen.springhibernate.velocity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.validation.Valid;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;

import edu.uiowa.icts.protogen.springhibernate.DomainClass;
import edu.uiowa.icts.protogen.springhibernate.SpringHibernateModel;
import edu.uiowa.webapp.ClayLoader;
import edu.uiowa.webapp.Database;
import edu.uiowa.webapp.DatabaseSchemaLoader;

public class VelocityEditJspGeneratorTest {
	private Database database;
	
	 @Before
	 public void setup() {
		DatabaseSchemaLoader theLoader =  new ClayLoader();
        try {
			theLoader.run(System.getProperty( "user.dir" ) + "/src/test/resources/mvc-test-generator.clay");
		} catch (Exception e) {
			assertNull(e);
		}
        this.database =  theLoader.getDatabase();
	 }
	 
	@Test
	public void shouldGenerateEditJSPForDomainClassWithIntegerPrimaryKeyAndDeobfuscateColumnNames() {
		String packageRoot = "edu.uiowa.icts";		

		Properties properties = new Properties();		
		properties.setProperty("deobfuscate.column.names", "true");
		
        SpringHibernateModel model = new SpringHibernateModel( this.database, packageRoot, properties );
        
        DomainClass jobType = null;
        for ( DomainClass dc : model.getDomainClassList() ) {
			if (dc.getIdentifier().equals("JobType")){
				jobType = dc;
			}
		}
        
		VelocityEditJspGenerator generator = new VelocityEditJspGenerator(packageRoot,jobType,properties);
		String sourceCode = generator.javaSourceCode();
		System.out.println(sourceCode);

        assertThat(sourceCode, containsString("<label for=\"name\">${ aptamer:deobfuscateColumn ( 'job_type', 'name') }</label>"));
        assertThat(sourceCode, containsString("<label for=\"description\">${ aptamer:deobfuscateColumn ( 'job_type', 'description') }</label>"));
        assertThat(sourceCode, containsString("<label for=\"parameters\">${ aptamer:deobfuscateColumn ( 'job_type', 'parameters') }</label>"));
	}
	/*
	 * this is the master test that asserts all dynamic variables are working, other tests only assert specific differences	
	 */
	@Test
	public void shouldGenerateEditJSPForDomainClassWithIntegerPrimaryKey() {
		String packageRoot = "edu.uiowa.icts";		

		Properties properties = new Properties();		
        SpringHibernateModel model = new SpringHibernateModel( this.database, packageRoot, properties );
        
        DomainClass jobType = null;
        for ( DomainClass dc : model.getDomainClassList() ) {
			if (dc.getIdentifier().equals("JobType")){
				jobType = dc;
			}
		}
        
		VelocityEditJspGenerator generator = new VelocityEditJspGenerator(packageRoot,jobType,properties);
		String sourceCode = generator.javaSourceCode();
		System.out.println(sourceCode);

        assertThat(sourceCode, containsString("<%@ include file=\"/WEB-INF/include.jsp\"  %>"));
        assertThat(sourceCode, containsString("<form:form method=\"post\" commandName=\"jobType\" action=\"save\" role=\"form\">"));
        assertThat(sourceCode, containsString("<legend>JobType</legend>"));
        assertThat(sourceCode, containsString("<form:hidden path=\"jobTypeId\" />"));
        
         
        assertThat(sourceCode, containsString("<spring:bind path=\"name\">"));
        assertThat(sourceCode, containsString("<label for=\"name\">Name</label>"));
        assertThat(sourceCode, containsString("<form:input path=\"name\"  class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"name\" class=\"help-block\"/>"));
        
        assertThat(sourceCode, containsString("<spring:bind path=\"description\">"));
        assertThat(sourceCode, containsString("<label for=\"description\">Description</label>"));
        assertThat(sourceCode, containsString("<form:input path=\"description\"  class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"description\" class=\"help-block\"/>"));
        
        assertThat(sourceCode, containsString("<spring:bind path=\"parameters\">"));
        assertThat(sourceCode, containsString("<label for=\"parameters\">Parameters</label>"));
        assertThat(sourceCode, containsString("<form:input path=\"parameters\"  class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"parameters\" class=\"help-block\"/>"));
        
        assertThat(sourceCode, containsString("<div class=\"form-group ${status.error ? 'has-error' : ''}\">"));
        
        assertThat(sourceCode, containsString("<a class=\"btn btn-default\" href=\"list\">Cancel</a>"));
        
	}
	
	@Test
	public void shouldGenerateEditJSPForDomainClassWithCompositePrimaryKeyAndDateFields() {
		String packageRoot = "edu.uiowa.icts";		

		Properties properties = new Properties();		
        SpringHibernateModel model = new SpringHibernateModel( this.database, packageRoot, properties );
        
        DomainClass jobType = null;
        for ( DomainClass dc : model.getDomainClassList() ) {
			if (dc.getIdentifier().equals("JobJobStatus")){
				jobType = dc;
			}
		}
        
		VelocityEditJspGenerator generator = new VelocityEditJspGenerator(packageRoot,jobType,properties);
		String sourceCode = generator.javaSourceCode();
		System.out.println(sourceCode);

        assertThat(sourceCode, containsString("<form:form method=\"post\" commandName=\"jobJobStatus\" action=\"save\" role=\"form\">"));
        assertThat(sourceCode, containsString("<legend>JobJobStatus</legend>"));        
     
        assertThat(sourceCode, containsString("<spring:bind path=\"dateSet\">"));
        assertThat(sourceCode, containsString("<label for=\"dateSet\">DateSet</label>"));
        assertThat(sourceCode, containsString("<form:input path=\"dateSet\"  class=\"form-control dateinput \"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"dateSet\" class=\"help-block\"/>"));
        
//        assertThat(sourceCode, containsString("<spring:bind path=\"description\">"));
//        assertThat(sourceCode, containsString("<label for=\"description\">Description</label>"));
//        assertThat(sourceCode, containsString("<form:input path=\"description\"  class=\"form-control\"/>"));
//        assertThat(sourceCode, containsString("<form:errors path=\"description\" class=\"help-block\"/>"));
//        
//        assertThat(sourceCode, containsString("<spring:bind path=\"parameters\">"));
//        assertThat(sourceCode, containsString("<label for=\"parameters\">Parameters</label>"));
//        assertThat(sourceCode, containsString("<form:input path=\"parameters\"  class=\"form-control\"/>"));
//        assertThat(sourceCode, containsString("<form:errors path=\"parameters\" class=\"help-block\"/>"));        
	}
	
	@Test
	public void shouldGenerateEditJSPForDomainClassWithIntegerPrimaryKeyAndDotHtmlExtension() {
		String packageRoot = "edu.uiowa.icts";		

		Properties properties = new Properties();
		properties.setProperty( "controller.request.mapping.extension", ".html" );

        SpringHibernateModel model = new SpringHibernateModel( this.database, packageRoot, properties );
        
        DomainClass jobType = null;
        for ( DomainClass dc : model.getDomainClassList() ) {
			if (dc.getIdentifier().equals("JobType")){
				jobType = dc;
			}
		}
        
		VelocityEditJspGenerator generator = new VelocityEditJspGenerator(packageRoot,jobType,properties);
		String sourceCode = generator.javaSourceCode();

        assertThat(sourceCode, containsString("<form:form method=\"post\" commandName=\"jobType\" action=\"save.html\" role=\"form\">"));
        assertThat(sourceCode, containsString("<a class=\"btn btn-default\" href=\"list.html\">Cancel</a>"));
        
	}
}
