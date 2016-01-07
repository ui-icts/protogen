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
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNull;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import edu.uiowa.icts.protogen.springhibernate.DomainClass;
import edu.uiowa.icts.protogen.springhibernate.SpringHibernateModel;
import edu.uiowa.webapp.ClayLoader;
import edu.uiowa.webapp.Database;
import edu.uiowa.webapp.DatabaseSchemaLoader;

public class VelocityAngularEditJspGeneratorTest {
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
        
		VelocityAngularEditJspGenerator generator = new VelocityAngularEditJspGenerator(packageRoot,jobType,properties);
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
        
		VelocityAngularEditJspGenerator generator = new VelocityAngularEditJspGenerator(packageRoot,jobType,properties);
		String sourceCode = generator.javaSourceCode();
		assertThat(sourceCode, containsString("<div class=\"form-group\" ng-class=\"{ 'has-error' : resourceForm.name.$invalid && !resourceForm.name.$pristine }\">"));
        assertThat(sourceCode, containsString("<label for=\"name\" class=\"control-label\">Name</label>"));
        assertThat(sourceCode, containsString("<input type=\"text\" id=\"name\" ng-model=\"resource.name\" name=\"name\" required=\"\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<p ng-show=\"resourceForm.name.$invalid && !resourceForm.name.$pristine\" class=\"help-block\"> is required.</p>"));
        
		assertThat(sourceCode, containsString("<div class=\"form-group\" ng-class=\"{ 'has-error' : resourceForm.description.$invalid && !resourceForm.description.$pristine }\">"));
        assertThat(sourceCode, containsString("<label for=\"description\" class=\"control-label\">Description</label>"));
        assertThat(sourceCode, containsString("<input type=\"text\" id=\"description\" ng-model=\"resource.description\" name=\"description\" required=\"\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<p ng-show=\"resourceForm.description.$invalid && !resourceForm.description.$pristine\" class=\"help-block\"> is required.</p>"));
        
		assertThat(sourceCode, containsString("<div class=\"form-group\" ng-class=\"{ 'has-error' : resourceForm.parameters.$invalid && !resourceForm.parameters.$pristine }\">"));
        assertThat(sourceCode, containsString("<label for=\"parameters\" class=\"control-label\">Parameters</label>"));
        assertThat(sourceCode, containsString("<input type=\"text\" id=\"parameters\" ng-model=\"resource.parameters\" name=\"parameters\" required=\"\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<p ng-show=\"resourceForm.parameters.$invalid && !resourceForm.parameters.$pristine\" class=\"help-block\"> is required.</p>"));
        
                
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
        
		VelocityAngularEditJspGenerator generator = new VelocityAngularEditJspGenerator(packageRoot,tableFive,properties);
		String sourceCode = generator.javaSourceCode();
     
		assertThat(sourceCode, containsString("<div class=\"form-group\" ng-class=\"{ 'has-error' : resourceForm.id.idOne.$invalid && !resourceForm.id.idOne.$pristine }\">"));
        assertThat(sourceCode, containsString("<label for=\"id.idOne\" class=\"control-label\">idOne"));
        assertThat(sourceCode, containsString("<input type=\"text\" id=\"id.idOne\" ng-model=\"resource.id.idOne\" name=\"id.idOne\" required=\"\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<p ng-show=\"resourceForm.id.idOne.$invalid && !resourceForm.id.idOne.$pristine\" class=\"help-block\"> is required.</p>"));
        
		assertThat(sourceCode, containsString("<div class=\"form-group\" ng-class=\"{ 'has-error' : resourceForm.id.idTwo.$invalid && !resourceForm.id.idTwo.$pristine }\">"));
        assertThat(sourceCode, containsString("<label for=\"id.idTwo\" class=\"control-label\">idTwo"));
        assertThat(sourceCode, containsString("<input type=\"text\" id=\"id.idTwo\" ng-model=\"resource.id.idTwo\" name=\"id.idTwo\" required=\"\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<p ng-show=\"resourceForm.id.idTwo.$invalid && !resourceForm.id.idTwo.$pristine\" class=\"help-block\"> is required.</p>"));
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
        
		VelocityAngularEditJspGenerator generator = new VelocityAngularEditJspGenerator(packageRoot,tableFive,properties);
		String sourceCode = generator.javaSourceCode();

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
        
		VelocityAngularEditJspGenerator generator = new VelocityAngularEditJspGenerator(packageRoot,tableThree,properties);
		String sourceCode = generator.javaSourceCode();

		assertThat(sourceCode, containsString("<div class=\"form-group\" ng-class=\"{ 'has-error' : resourceForm.tableTwo.tableTwoId.$invalid && !resourceForm.tableTwo.tableTwoId.$pristine }\">"));
        assertThat(sourceCode, containsString("<label for=\"tableTwo.tableTwoId\" class=\"control-label\">Table Two</label>"));
        assertThat(sourceCode, containsString("<form:select ng-model=\"resource.tableTwo.tableTwoId\" required=\"required\" path=\"tableTwo.tableTwoId\" items=\"${tableTwoList}\" itemValue=\"tableTwoId\" itemLabel=\"tableTwoId\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<p ng-show=\"resourceForm.tableTwo.tableTwoId.$invalid && !resourceForm.tableTwo.tableTwoId.$pristine\" class=\"help-block\"> is required.</p>"));
        
		assertThat(sourceCode, containsString("<div class=\"form-group\" ng-class=\"{ 'has-error' : resourceForm.tableOne.tableOneId.$invalid && !resourceForm.tableOne.tableOneId.$pristine }\">"));
        assertThat(sourceCode, containsString("<label for=\"tableOne.tableOneId\" class=\"control-label\">Table One</label>"));
        assertThat(sourceCode, containsString("<form:select ng-model=\"resource.tableOne.tableOneId\" required=\"required\" path=\"tableOne.tableOneId\" items=\"${tableOneList}\" itemValue=\"tableOneId\" itemLabel=\"tableOneId\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<p ng-show=\"resourceForm.tableOne.tableOneId.$invalid && !resourceForm.tableOne.tableOneId.$pristine\" class=\"help-block\"> is required.</p>"));
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
        
		VelocityAngularEditJspGenerator generator = new VelocityAngularEditJspGenerator(packageRoot,tableThree,properties);
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
        
		VelocityAngularEditJspGenerator generator = new VelocityAngularEditJspGenerator(packageRoot,tableFour,properties);
		String sourceCode = generator.javaSourceCode();    
        
		assertThat(sourceCode, containsString("<div class=\"form-group\" ng-class=\"{ 'has-error' : resourceForm.tableFour.tableFourId.$invalid && !resourceForm.tableFour.tableFourId.$pristine }\">"));
        assertThat(sourceCode, containsString("<label for=\"tableFour.tableFourId\" class=\"control-label\">Table Four</label>"));
        assertThat(sourceCode, containsString("<form:select ng-model=\"resource.tableFour.tableFourId\" required=\"required\" path=\"tableFour.tableFourId\" items=\"${tableFourList}\" itemValue=\"tableFourId\" itemLabel=\"tableFourId\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<p ng-show=\"resourceForm.tableFour.tableFourId.$invalid && !resourceForm.tableFour.tableFourId.$pristine\" class=\"help-block\"> is required.</p>"));
        
		assertThat(sourceCode, containsString("<div class=\"form-group\" ng-class=\"{ 'has-error' : resourceForm.tableThree.tableThreeId.$invalid && !resourceForm.tableThree.tableThreeId.$pristine }\">"));
        assertThat(sourceCode, containsString("<label for=\"tableThree.tableThreeId\" class=\"control-label\">Table Three</label>"));
        assertThat(sourceCode, containsString("<form:select ng-model=\"resource.tableThree.tableThreeId\" required=\"required\" path=\"tableThree.tableThreeId\" items=\"${tableThreeList}\" itemValue=\"tableThreeId\" itemLabel=\"tableThreeId\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<p ng-show=\"resourceForm.tableThree.tableThreeId.$invalid && !resourceForm.tableThree.tableThreeId.$pristine\" class=\"help-block\"> is required.</p>"));
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
        
		VelocityAngularEditJspGenerator generator = new VelocityAngularEditJspGenerator(packageRoot,tableFour,properties);
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
        
		VelocityAngularEditJspGenerator generator = new VelocityAngularEditJspGenerator(packageRoot,jobType,properties);
		String sourceCode = generator.javaSourceCode();
     
		assertThat(sourceCode, containsString("<div class=\"form-group\" ng-class=\"{ 'has-error' : resourceForm.fileName.$invalid && !resourceForm.fileName.$pristine }\">"));
        assertThat(sourceCode, containsString("<label for=\"fileName\" class=\"control-label\">File Name</label>"));
        assertThat(sourceCode, containsString("<input type=\"text\" id=\"fileName\" ng-model=\"resource.fileName\" name=\"fileName\" required=\"\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<p ng-show=\"resourceForm.fileName.$invalid && !resourceForm.fileName.$pristine\" class=\"help-block\"> is required.</p>"));
        
		assertThat(sourceCode, containsString("<div class=\"form-group\" ng-class=\"{ 'has-error' : resourceForm.parameters.$invalid && !resourceForm.parameters.$pristine }\">"));
        assertThat(sourceCode, containsString("<label for=\"parameters\" class=\"control-label\">Parameters</label>"));
        assertThat(sourceCode, containsString("<input type=\"text\" id=\"parameters\" ng-model=\"resource.parameters\" name=\"parameters\" required=\"\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<p ng-show=\"resourceForm.parameters.$invalid && !resourceForm.parameters.$pristine\" class=\"help-block\"> is required.</p>"));
        
		assertThat(sourceCode, containsString("<div class=\"form-group\" ng-class=\"{ 'has-error' : resourceForm.childJobId.$invalid && !resourceForm.childJobId.$pristine }\">"));
        assertThat(sourceCode, containsString("<label for=\"childJobId\" class=\"control-label\">Child Job Id</label>"));
        assertThat(sourceCode, containsString("<input type=\"text\" id=\"childJobId\" ng-model=\"resource.childJobId\" name=\"childJobId\" required=\"\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<p ng-show=\"resourceForm.childJobId.$invalid && !resourceForm.childJobId.$pristine\" class=\"help-block\"> is required.</p>"));
        
		assertThat(sourceCode, containsString("<div class=\"form-group\" ng-class=\"{ 'has-error' : resourceForm.jobType.jobTypeId.$invalid && !resourceForm.jobType.jobTypeId.$pristine }\">"));
        assertThat(sourceCode, containsString("<label for=\"jobType.jobTypeId\" class=\"control-label\">Job Type</label>"));
        assertThat(sourceCode, containsString("<form:select ng-model=\"resource.jobType.jobTypeId\" required=\"required\" path=\"jobType.jobTypeId\" items=\"${jobTypeList}\" itemValue=\"jobTypeId\" itemLabel=\"jobTypeId\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<p ng-show=\"resourceForm.jobType.jobTypeId.$invalid && !resourceForm.jobType.jobTypeId.$pristine\" class=\"help-block\"> is required.</p>"));
        
		assertThat(sourceCode, containsString("<div class=\"form-group\" ng-class=\"{ 'has-error' : resourceForm.person.personId.$invalid && !resourceForm.person.personId.$pristine }\">"));
        assertThat(sourceCode, containsString("<label for=\"person.personId\" class=\"control-label\">Person</label>"));
        assertThat(sourceCode, containsString("<form:select ng-model=\"resource.person.personId\" required=\"required\" path=\"person.personId\" items=\"${personList}\" itemValue=\"personId\" itemLabel=\"personId\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<p ng-show=\"resourceForm.person.personId.$invalid && !resourceForm.person.personId.$pristine\" class=\"help-block\"> is required.</p>"));
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
        
		VelocityAngularEditJspGenerator generator = new VelocityAngularEditJspGenerator(packageRoot,jobType,properties);
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
        
		VelocityAngularEditJspGenerator generator = new VelocityAngularEditJspGenerator(packageRoot,jobType,properties);
		String sourceCode = generator.javaSourceCode();       
     
		assertThat(sourceCode, containsString("<div class=\"form-group\" ng-class=\"{ 'has-error' : resourceForm.dateSet.$invalid && !resourceForm.dateSet.$pristine }\">"));
        assertThat(sourceCode, containsString("<label for=\"dateSet\" class=\"control-label\">Date Set</label>"));
        assertThat(sourceCode, containsString("<input type=\"text\" id=\"dateSet\" ng-model=\"resource.dateSet\" name=\"dateSet\" required=\"\" class=\"form-control dateinput\" data-provide=\"datepicker\" data-date-format=\"yyyy-mm-dd\" data-date-autoclose=\"true\"/>"));
        assertThat(sourceCode, containsString("<p ng-show=\"resourceForm.dateSet.$invalid && !resourceForm.dateSet.$pristine\" class=\"help-block\"> is required.</p>"));
        
		assertThat(sourceCode, containsString("<div class=\"form-group\" ng-class=\"{ 'has-error' : resourceForm.jobStatus.jobStatusId.$invalid && !resourceForm.jobStatus.jobStatusId.$pristine }\">"));
        assertThat(sourceCode, containsString("<label for=\"jobStatus.jobStatusId\" class=\"control-label\">Job Status</label>"));
        assertThat(sourceCode, containsString("<form:select ng-model=\"resource.jobStatus.jobStatusId\" required=\"required\" path=\"jobStatus.jobStatusId\" items=\"${jobStatusList}\" itemValue=\"jobStatusId\" itemLabel=\"jobStatusId\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<p ng-show=\"resourceForm.jobStatus.jobStatusId.$invalid && !resourceForm.jobStatus.jobStatusId.$pristine\" class=\"help-block\"> is required.</p>"));
        
		assertThat(sourceCode, containsString("<div class=\"form-group\" ng-class=\"{ 'has-error' : resourceForm.job.jobId.$invalid && !resourceForm.job.jobId.$pristine }\">"));
        assertThat(sourceCode, containsString("<label for=\"job.jobId\" class=\"control-label\">Job</label>"));
        assertThat(sourceCode, containsString("<form:select ng-model=\"resource.job.jobId\" required=\"required\" path=\"job.jobId\" items=\"${jobList}\" itemValue=\"jobId\" itemLabel=\"jobId\" class=\"form-control\"/>"));
        assertThat(sourceCode, containsString("<p ng-show=\"resourceForm.job.jobId.$invalid && !resourceForm.job.jobId.$pristine\" class=\"help-block\"> is required.</p>"));
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
        
		VelocityAngularEditJspGenerator generator = new VelocityAngularEditJspGenerator(packageRoot,jobType,properties);
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
        
		VelocityAngularEditJspGenerator generator = new VelocityAngularEditJspGenerator(packageRoot,jobType,properties);
		String sourceCode = generator.javaSourceCode();

		assertThat(sourceCode, containsString("<div class=\"form-group\" ng-class=\"{ 'has-error' : resourceForm.name.$invalid && !resourceForm.name.$pristine }\">"));
        assertThat(sourceCode, containsString("<label for=\"name\" class=\"control-label\">Name</label>"));
        
	}
}
