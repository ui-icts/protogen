package edu.uiowa.icts.protogen.springhibernate.velocity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
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

        assertThat(sourceCode, containsString("<label for=\"name\" class=\"control-label\">${ aptamer:deobfuscateColumn ( 'job_type', 'name') }</label>"));
        assertThat(sourceCode, containsString("<label for=\"description\" class=\"control-label\">${ aptamer:deobfuscateColumn ( 'job_type', 'description') }</label>"));
        assertThat(sourceCode, containsString("<label for=\"parameters\" class=\"control-label\">${ aptamer:deobfuscateColumn ( 'job_type', 'parameters') }</label>"));
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

        assertThat(sourceCode, containsString("<%@ include file=\"/WEB-INF/include.jsp\"  %>"));
        assertThat(sourceCode, containsString("<form:form method=\"post\" commandName=\"jobType\" action=\"${formActionUrl}\" role=\"form\">"));
        assertThat(sourceCode, containsString("<legend>JobType</legend>"));
        assertThat(sourceCode, containsString("<form:hidden path=\"jobTypeId\" />"));
        
         
        assertThat(sourceCode, containsString("<spring:bind path=\"name\">"));
        assertThat(sourceCode, containsString("<label for=\"name\" class=\"control-label\">Name</label>"));
        assertThat(sourceCode, containsString("<form:input path=\"name\"  class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"name\" class=\"help-block\" element=\"span\" />"));
        
        assertThat(sourceCode, containsString("<spring:bind path=\"description\">"));
        assertThat(sourceCode, containsString("<label for=\"description\" class=\"control-label\">Description</label>"));
        assertThat(sourceCode, containsString("<form:input path=\"description\"  class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"description\" class=\"help-block\" element=\"span\" />"));
        
        assertThat(sourceCode, containsString("<spring:bind path=\"parameters\">"));
        assertThat(sourceCode, containsString("<label for=\"parameters\" class=\"control-label\">Parameters</label>"));
        assertThat(sourceCode, containsString("<form:input path=\"parameters\"  class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"parameters\" class=\"help-block\" element=\"span\" />"));
        
        assertThat(sourceCode, containsString("<div class=\"form-group ${status.error ? 'has-error' : ''}\">"));
        
        assertThat(sourceCode, containsString("<a class=\"btn btn-default\" href=\"${cancelUrl}\">Cancel</a>"));
        
        // assert that job child set doesn't have an input box
        assertThat(sourceCode, not(containsString("<form:input path=\"jobs\"  class=\"form-control\"/>")));
        
	}
	
	@Test
	public void shouldGenerateEditJSPForDomainClassWithCompositeKeyThatHasNoForeignKeyReferences() {
		String packageRoot = "edu.uiowa.icts";		

		Properties properties = new Properties();		
        SpringHibernateModel model = new SpringHibernateModel( this.database, packageRoot, properties );
        
        DomainClass tableFive = null;
        for ( DomainClass dc : model.getDomainClassList() ) {
			if (dc.getIdentifier().equals("TableFive")){
				tableFive = dc;
			}
		}
        
		VelocityEditJspGenerator generator = new VelocityEditJspGenerator(packageRoot,tableFive,properties);
		String sourceCode = generator.javaSourceCode();

        assertThat(sourceCode, containsString("<form:form method=\"post\" commandName=\"tableFive\" action=\"${formActionUrl}\" role=\"form\">"));
        assertThat(sourceCode, containsString("<legend>TableFive</legend>"));        
     
        assertThat(sourceCode, containsString("<spring:bind path=\"id.idOne\">"));
        assertThat(sourceCode, containsString("<label for=\"id.idOne\" class=\"control-label\">idOne"));
        assertThat(sourceCode, containsString("<form:input path=\"id.idOne\"  class=\"form-control\" />"));
        assertThat(sourceCode, containsString("<form:errors path=\"id.idOne\" class=\"help-block\" element=\"span\" />"));
        
        assertThat(sourceCode, containsString("<spring:bind path=\"id.idTwo\">"));
        assertThat(sourceCode, containsString("<label for=\"id.idTwo\" class=\"control-label\">idTwo"));
        assertThat(sourceCode, containsString("<form:input path=\"id.idTwo\"  class=\"form-control\" />"));
        assertThat(sourceCode, containsString("<form:errors path=\"id.idTwo\" class=\"help-block\" element=\"span\" />"));
	}
	
	@Test
	public void shouldGenerateEditJSPForDomainClassWithCompositeKeyThatHasNoForeignKeyReferencesAndDeobfuscateColumnNames() {
		String packageRoot = "edu.uiowa.icts";		

		Properties properties = new Properties();		
		properties.setProperty("deobfuscate.column.names", "true");

        SpringHibernateModel model = new SpringHibernateModel( this.database, packageRoot, properties );
        
        DomainClass tableFive = null;
        for ( DomainClass dc : model.getDomainClassList() ) {
			if (dc.getIdentifier().equals("TableFive")){
				tableFive = dc;
			}
		}
        
		VelocityEditJspGenerator generator = new VelocityEditJspGenerator(packageRoot,tableFive,properties);
		String sourceCode = generator.javaSourceCode();
	//	System.out.println(sourceCode);

        assertThat(sourceCode, containsString("<label for=\"id.idOne\" class=\"control-label\">${ aptamer:deobfuscateColumn ( 'table_five', 'id_one') }"));
        assertThat(sourceCode, containsString("<label for=\"id.idTwo\" class=\"control-label\">${ aptamer:deobfuscateColumn ( 'table_five', 'id_two') }"));   
	}
	
	@Test
	public void shouldGenerateEditJSPForDomainClassWithCompositeKeyComprisedOfForeignKeys() {
		String packageRoot = "edu.uiowa.icts";		

		Properties properties = new Properties();		
        SpringHibernateModel model = new SpringHibernateModel( this.database, packageRoot, properties );
        
        DomainClass tableThree = null;
        for ( DomainClass dc : model.getDomainClassList() ) {
			if (dc.getIdentifier().equals("TableThree")){
				tableThree = dc;
			}
		}
        
		VelocityEditJspGenerator generator = new VelocityEditJspGenerator(packageRoot,tableThree,properties);
		String sourceCode = generator.javaSourceCode();

        assertThat(sourceCode, containsString("<form:form method=\"post\" commandName=\"tableThree\" action=\"${formActionUrl}\" role=\"form\">"));
        assertThat(sourceCode, containsString("<legend>TableThree</legend>"));        
        
        assertThat(sourceCode, containsString("<spring:bind path=\"tableTwo.tableTwoId\">"));
        assertThat(sourceCode, containsString("<label for=\"tableTwo.tableTwoId\" class=\"control-label\">TableTwo</label>"));
        assertThat(sourceCode, containsString("<form:select path=\"tableTwo.tableTwoId\" items=\"${tableTwoList}\" itemValue=\"tableTwoId\" itemLabel=\"tableTwoId\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"tableTwo.tableTwoId\" class=\"help-block\" element=\"span\" />"));
        
        assertThat(sourceCode, containsString("<spring:bind path=\"tableOne.tableOneId\">"));
        assertThat(sourceCode, containsString("<label for=\"tableOne.tableOneId\" class=\"control-label\">TableOne</label>"));
        assertThat(sourceCode, containsString("<form:select path=\"tableOne.tableOneId\" items=\"${tableOneList}\" itemValue=\"tableOneId\" itemLabel=\"tableOneId\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"tableOne.tableOneId\" class=\"help-block\" element=\"span\" />")); 
	}
	
	@Test
	public void shouldGenerateEditJSPForDomainClassWithCompositeKeyComprisedOfForeignKeysAndDeobfuscateColumnNames() {
		String packageRoot = "edu.uiowa.icts";		

		Properties properties = new Properties();
		properties.setProperty("deobfuscate.column.names", "true");

        SpringHibernateModel model = new SpringHibernateModel( this.database, packageRoot, properties );
        
        DomainClass tableThree = null;
        for ( DomainClass dc : model.getDomainClassList() ) {
			if (dc.getIdentifier().equals("TableThree")){
				tableThree = dc;
			}
		}
        
		VelocityEditJspGenerator generator = new VelocityEditJspGenerator(packageRoot,tableThree,properties);
		String sourceCode = generator.javaSourceCode();

        assertThat(sourceCode, containsString("<label for=\"tableTwo.tableTwoId\" class=\"control-label\">${ aptamer:deobfuscateColumn ( 'table_three', 'table_two_id') }</label>"));
        assertThat(sourceCode, containsString("<label for=\"tableOne.tableOneId\" class=\"control-label\">${ aptamer:deobfuscateColumn ( 'table_three', 'table_one_id') }</label>"));   
	}
	
	@Test
	public void shouldGenerateEditJSPForDomainClassWithThatReferencesACompositeKeyComprisedOfForeignKeys() {
		String packageRoot = "edu.uiowa.icts";		

		Properties properties = new Properties();		
        SpringHibernateModel model = new SpringHibernateModel( this.database, packageRoot, properties );
        
        DomainClass tableFour = null;
        for ( DomainClass dc : model.getDomainClassList() ) {
			if (dc.getIdentifier().equals("TableFour")){
				tableFour = dc;
			}
		}
        
		VelocityEditJspGenerator generator = new VelocityEditJspGenerator(packageRoot,tableFour,properties);
		String sourceCode = generator.javaSourceCode();

        assertThat(sourceCode, containsString("<form:form method=\"post\" commandName=\"tableFour\" action=\"${formActionUrl}\" role=\"form\">"));
        assertThat(sourceCode, containsString("<legend>TableFour</legend>"));        
        
        assertThat(sourceCode, containsString("<form:hidden path=\"tableFourId\" />"));        
        
        assertThat(sourceCode, containsString("<spring:bind path=\"tableFour.tableFourId\">"));
        assertThat(sourceCode, containsString("<label for=\"tableFour.tableFourId\" class=\"control-label\">TableFour</label>"));
        assertThat(sourceCode, containsString("<form:select path=\"tableFour.tableFourId\" items=\"${tableFourList}\" itemValue=\"tableFourId\" itemLabel=\"tableFourId\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"tableFour.tableFourId\" class=\"help-block\" element=\"span\" />"));
        
        assertThat(sourceCode, containsString("<spring:bind path=\"tableThree.tableThreeId\">"));
        assertThat(sourceCode, containsString("<label for=\"tableThree.tableThreeId\" class=\"control-label\">TableThree</label>"));
        assertThat(sourceCode, containsString("<form:select path=\"tableThree.tableThreeId\" items=\"${tableThreeList}\" itemValue=\"tableThreeId\" itemLabel=\"tableThreeId\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"tableThree.tableThreeId\" class=\"help-block\" element=\"span\" />"));
	}
	
	@Test
	public void shouldGenerateEditJSPForDomainClassWithThatReferencesACompositeKeyComprisedOfForeignKeysAndDeobfuscateColumnNames() {
		String packageRoot = "edu.uiowa.icts";		

		Properties properties = new Properties();
		properties.setProperty("deobfuscate.column.names", "true");

        SpringHibernateModel model = new SpringHibernateModel( this.database, packageRoot, properties );
        
        DomainClass tableFour = null;
        for ( DomainClass dc : model.getDomainClassList() ) {
			if (dc.getIdentifier().equals("TableFour")){
				tableFour = dc;
			}
		}
        
		VelocityEditJspGenerator generator = new VelocityEditJspGenerator(packageRoot,tableFour,properties);
		String sourceCode = generator.javaSourceCode();

        assertThat(sourceCode, containsString("<label for=\"tableFour.tableFourId\" class=\"control-label\">${ aptamer:deobfuscateColumn ( 'table_four', 'table_two_id') }</label>"));
        assertThat(sourceCode, containsString("<label for=\"tableThree.tableThreeId\" class=\"control-label\">${ aptamer:deobfuscateColumn ( 'table_four', 'table_one_id') }</label>"));   
	}
	
	
	@Test
	public void shouldGenerateEditJSPForDomainClassWithForeignKeyFields() {
		String packageRoot = "edu.uiowa.icts";		

		Properties properties = new Properties();		
        SpringHibernateModel model = new SpringHibernateModel( this.database, packageRoot, properties );
        
        DomainClass jobType = null;
        for ( DomainClass dc : model.getDomainClassList() ) {
			if (dc.getIdentifier().equals("Job")){
				jobType = dc;
			}
		}
        
		VelocityEditJspGenerator generator = new VelocityEditJspGenerator(packageRoot,jobType,properties);
		String sourceCode = generator.javaSourceCode();

        assertThat(sourceCode, containsString("<form:form method=\"post\" commandName=\"job\" action=\"${formActionUrl}\" role=\"form\">"));
        assertThat(sourceCode, containsString("<legend>Job</legend>"));        
        assertThat(sourceCode, containsString("<form:hidden path=\"jobId\" />"));
     
        assertThat(sourceCode, containsString("<spring:bind path=\"fileName\">"));
        assertThat(sourceCode, containsString("<label for=\"fileName\" class=\"control-label\">FileName</label>"));
        assertThat(sourceCode, containsString("<form:input path=\"fileName\"  class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"fileName\" class=\"help-block\" element=\"span\" />"));
        
        assertThat(sourceCode, containsString("<spring:bind path=\"parameters\">"));
        assertThat(sourceCode, containsString("<label for=\"parameters\" class=\"control-label\">Parameters</label>"));
        assertThat(sourceCode, containsString("<form:input path=\"parameters\"  class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"parameters\" class=\"help-block\" element=\"span\" />"));
        
        assertThat(sourceCode, containsString("<spring:bind path=\"childJobId\">"));
        assertThat(sourceCode, containsString("<label for=\"childJobId\" class=\"control-label\">ChildJobId</label>"));
        assertThat(sourceCode, containsString("<form:input path=\"childJobId\"  class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"childJobId\" class=\"help-block\" element=\"span\" />"));
        
        assertThat(sourceCode, containsString("<spring:bind path=\"jobType.jobTypeId\">"));
        assertThat(sourceCode, containsString("<label for=\"jobType.jobTypeId\" class=\"control-label\">JobType</label>"));
        assertThat(sourceCode, containsString("<form:select path=\"jobType.jobTypeId\" items=\"${jobTypeList}\" itemValue=\"jobTypeId\" itemLabel=\"jobTypeId\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"jobType.jobTypeId\" class=\"help-block\" element=\"span\" />"));
        
        assertThat(sourceCode, containsString("<spring:bind path=\"person.personId\">"));
        assertThat(sourceCode, containsString("<label for=\"person.personId\" class=\"control-label\">Person</label>"));
        assertThat(sourceCode, containsString("<form:select path=\"person.personId\" items=\"${personList}\" itemValue=\"personId\" itemLabel=\"personId\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"person.personId\" class=\"help-block\" element=\"span\" />"));
	}
	
	@Test
	public void shouldGenerateEditJSPForDomainClassWithForeignKeyFieldsAndDeobfuscateColumnNames() {
		String packageRoot = "edu.uiowa.icts";		

		Properties properties = new Properties();		
		properties.setProperty("deobfuscate.column.names", "true");

        SpringHibernateModel model = new SpringHibernateModel( this.database, packageRoot, properties );
        
        DomainClass jobType = null;
        for ( DomainClass dc : model.getDomainClassList() ) {
			if (dc.getIdentifier().equals("Job")){
				jobType = dc;
			}
		}
        
		VelocityEditJspGenerator generator = new VelocityEditJspGenerator(packageRoot,jobType,properties);
		String sourceCode = generator.javaSourceCode();
      
        assertThat(sourceCode, containsString("<label for=\"fileName\" class=\"control-label\">${ aptamer:deobfuscateColumn ( 'job', 'file_name') }</label>"));
        assertThat(sourceCode, containsString("<label for=\"parameters\" class=\"control-label\">${ aptamer:deobfuscateColumn ( 'job', 'parameters') }</label>"));
        assertThat(sourceCode, containsString("<label for=\"childJobId\" class=\"control-label\">${ aptamer:deobfuscateColumn ( 'job', 'child_job_id') }</label>"));
        assertThat(sourceCode, containsString("<label for=\"jobType.jobTypeId\" class=\"control-label\">${ aptamer:deobfuscateColumn ( 'job', 'job_type_id') }</label>"));
        assertThat(sourceCode, containsString("<label for=\"person.personId\" class=\"control-label\">${ aptamer:deobfuscateColumn ( 'job', 'person_id') }</label>"));      
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

        assertThat(sourceCode, containsString("<form:form method=\"post\" commandName=\"jobJobStatus\" action=\"${formActionUrl}\" role=\"form\">"));
        assertThat(sourceCode, containsString("<legend>JobJobStatus</legend>"));        
     
        assertThat(sourceCode, containsString("<spring:bind path=\"dateSet\">"));
        assertThat(sourceCode, containsString("<label for=\"dateSet\" class=\"control-label\">DateSet</label>"));
        assertThat(sourceCode, containsString("<form:input path=\"dateSet\"  class=\"form-control dateinput \"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"dateSet\" class=\"help-block\" element=\"span\" />"));
        
        assertThat(sourceCode, containsString("<spring:bind path=\"jobStatus.jobStatusId\">"));
        assertThat(sourceCode, containsString("<label for=\"jobStatus.jobStatusId\" class=\"control-label\">JobStatus</label>"));
        assertThat(sourceCode, containsString("<form:select path=\"jobStatus.jobStatusId\" items=\"${jobStatusList}\" itemValue=\"jobStatusId\" itemLabel=\"jobStatusId\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"jobStatus.jobStatusId\" class=\"help-block\" element=\"span\" />"));
        
        assertThat(sourceCode, containsString("<spring:bind path=\"job.jobId\">"));
        assertThat(sourceCode, containsString("<label for=\"job.jobId\" class=\"control-label\">Job</label>"));
        assertThat(sourceCode, containsString("<form:select path=\"job.jobId\" items=\"${jobList}\" itemValue=\"jobId\" itemLabel=\"jobId\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<form:errors path=\"job.jobId\" class=\"help-block\" element=\"span\" />"));        
	}
	
	@Test
	public void shouldGenerateEditJSPForDomainClassWithCompositePrimaryKeyAndDateFieldsAndDeobfuscateColumnNames() {
		String packageRoot = "edu.uiowa.icts";		

		Properties properties = new Properties();		
		properties.setProperty("deobfuscate.column.names", "true");

        SpringHibernateModel model = new SpringHibernateModel( this.database, packageRoot, properties );
        
        DomainClass jobType = null;
        for ( DomainClass dc : model.getDomainClassList() ) {
			if (dc.getIdentifier().equals("JobJobStatus")){
				jobType = dc;
			}
		}
        
		VelocityEditJspGenerator generator = new VelocityEditJspGenerator(packageRoot,jobType,properties);
		String sourceCode = generator.javaSourceCode();
		
        assertThat(sourceCode, containsString("<label for=\"dateSet\" class=\"control-label\">${ aptamer:deobfuscateColumn ( 'job_job_status', 'date_set') }</label>"));
        assertThat(sourceCode, containsString("<label for=\"jobStatus.jobStatusId\" class=\"control-label\">${ aptamer:deobfuscateColumn ( 'job_job_status', 'job_status_id') }</label>"));
        assertThat(sourceCode, containsString("<label for=\"job.jobId\" class=\"control-label\">${ aptamer:deobfuscateColumn ( 'job_job_status', 'job_id') }</label>"));
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

        assertThat(sourceCode, containsString("<form:form method=\"post\" commandName=\"jobType\" action=\"${formActionUrl}\" role=\"form\">"));
        assertThat(sourceCode, containsString("<a class=\"btn btn-default\" href=\"${cancelUrl}\">Cancel</a>"));
        
	}
}
