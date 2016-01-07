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

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import edu.uiowa.icts.protogen.springhibernate.DomainClass;
import edu.uiowa.icts.protogen.springhibernate.SpringHibernateModel;
import edu.uiowa.webapp.ClayLoader;
import edu.uiowa.webapp.Database;
import edu.uiowa.webapp.DatabaseSchemaLoader;
import edu.uiowa.webapp.Schema;

public class ControllerMvcTestGeneratorTest {

	@Before
	public void setup() {

	}

	@Test
	public void shouldGenerateJavaSourceCodeForSpringMVCTestFileWithSchemaNameInPackageName() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		//	properties.setProperty( "include.schema.in.package.name", "false" );	

		ControllerMvcTestGenerator generator = new ControllerMvcTestGenerator( packageRoot, domainClass, properties );

		String sourceCode = generator.javaSourceCode();

		assertThat( sourceCode, containsString( "package edu.uiowa.icts.ictssysadmin.controller;" ) );
	}

	@Test
	public void shouldGenerateJavaSourceCodeForSpringMVCTestFileWithoutSchemaNameInPackageName() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		properties.setProperty( "include.schema.in.package.name", "false" );

		ControllerMvcTestGenerator generator = new ControllerMvcTestGenerator( packageRoot, domainClass, properties );

		String sourceCode = generator.javaSourceCode();

		assertThat( sourceCode, containsString( "package edu.uiowa.icts.controller;" ) );
	}

	@Test
	public void shouldGenerateJavaSourceCodeForSpringMVCTestFileWithSchemaNameInRequestMapping() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		properties.setProperty( "datatables.generation", "2" );
		//	properties.setProperty( "include.schema.in.request.mapping", "false" );	

		ControllerMvcTestGenerator generator = new ControllerMvcTestGenerator( packageRoot, domainClass, properties );

		String sourceCode = generator.javaSourceCode();

		assertThat( sourceCode, containsString( "package edu.uiowa.icts.ictssysadmin.controller;" ) );
		assertThat( sourceCode, containsString( "* Generated by Protogen" ) );

		SimpleDateFormat ft = new SimpleDateFormat( "EEE MMM dd" );
		assertThat( sourceCode, containsString( ft.format( new Date() ) ) );
		assertThat( sourceCode, containsString( "ClinicalDocumentControllerMvcTest extends AbstractControllerMVCTests" ) );

		// test list_alt
		assertThat( sourceCode, containsString( "public void listAltShouldLoadListOfClinicalDocuments() throws Exception {" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/ictssysadmin/clinicaldocument/list_alt\"))" ) );
		assertThat( sourceCode, containsString( ".andExpect(model().attributeExists(\"clinicalDocumentList\"))" ) );
		assertThat( sourceCode, containsString( ".andExpect(view().name(\"/ictssysadmin/clinicaldocument/list_alt\"));" ) );

		// test list
		assertThat( sourceCode, containsString( "public void listShouldSimplyLoadPage() throws Exception {" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/ictssysadmin/clinicaldocument/list\"))" ) );
		assertThat( sourceCode, containsString( ".andExpect(view().name(\"/ictssysadmin/clinicaldocument/list\"));" ) );

		// test index
		assertThat( sourceCode, containsString( "public void indexShouldDisplayListPage() throws Exception {" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/ictssysadmin/clinicaldocument/\"))" ) );
		assertThat( sourceCode, containsString( ".andExpect(view().name(\"/ictssysadmin/clinicaldocument/list\"));" ) );

		// test add
		assertThat( sourceCode, containsString( "public void addShouldDisplayNewClinicalDocumentForm() throws Exception {" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/ictssysadmin/clinicaldocument/add\"))" ) );
		assertThat( sourceCode, containsString( ".andExpect(model().attributeExists(\"clinicalDocument\"))" ) );
		assertThat( sourceCode, containsString( ".andExpect(view().name(\"/ictssysadmin/clinicaldocument/edit\"));" ) );

		// test save new
		assertThat( sourceCode, containsString( "public void saveNewShouldPersistAndRedirectToListView() throws Exception {" ) );
		assertThat( sourceCode, containsString( "long count = ictssysadminDaoService.getClinicalDocumentService().count();" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(post(\"/ictssysadmin/clinicaldocument/save\").with(csrf())).andExpect(status().is3xxRedirection()).andExpect(view().name(\"redirect:/ictssysadmin/clinicaldocument/list\"));" ) );
		assertThat( sourceCode, containsString( "assertEquals(\"count should increase by 1\", count +1 , ictssysadminDaoService.getClinicalDocumentService().count());" ) );
	}

	@Test
	public void shouldGenerateJavaSourceCodeForSpringMVCTestFileWithoutSchemaNameInRequestMapping() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		properties.setProperty( "datatables.generation", "2" );
		properties.setProperty( "include.schema.in.request.mapping", "false" );

		ControllerMvcTestGenerator generator = new ControllerMvcTestGenerator( packageRoot, domainClass, properties );

		String sourceCode = generator.javaSourceCode();

		assertThat( sourceCode, containsString( "package edu.uiowa.icts.ictssysadmin.controller;" ) );
		assertThat( sourceCode, containsString( "* Generated by Protogen" ) );

		SimpleDateFormat ft = new SimpleDateFormat( "EEE MMM dd" );
		assertThat( sourceCode, containsString( ft.format( new Date() ) ) );
		assertThat( sourceCode, containsString( "ClinicalDocumentControllerMvcTest extends AbstractControllerMVCTests" ) );

		// test list_alt
		assertThat( sourceCode, containsString( "public void listAltShouldLoadListOfClinicalDocuments() throws Exception {" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/clinicaldocument/list_alt\"))" ) );
		assertThat( sourceCode, containsString( ".andExpect(model().attributeExists(\"clinicalDocumentList\"))" ) );
		assertThat( sourceCode, containsString( ".andExpect(view().name(\"/ictssysadmin/clinicaldocument/list_alt\"));" ) );

		// test list
		assertThat( sourceCode, containsString( "public void listShouldSimplyLoadPage() throws Exception {" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/clinicaldocument/list\"))" ) );
		assertThat( sourceCode, containsString( ".andExpect(view().name(\"/ictssysadmin/clinicaldocument/list\"));" ) );

		// test index
		assertThat( sourceCode, containsString( "public void indexShouldDisplayListPage() throws Exception {" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/clinicaldocument/\"))" ) );
		assertThat( sourceCode, containsString( ".andExpect(view().name(\"/ictssysadmin/clinicaldocument/list\"));" ) );

		// test add
		assertThat( sourceCode, containsString( "public void addShouldDisplayNewClinicalDocumentForm() throws Exception {" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/clinicaldocument/add\"))" ) );
		assertThat( sourceCode, containsString( ".andExpect(model().attributeExists(\"clinicalDocument\"))" ) );
		assertThat( sourceCode, containsString( ".andExpect(view().name(\"/ictssysadmin/clinicaldocument/edit\"));" ) );

		// test save
		assertThat( sourceCode, containsString( "public void saveNewShouldPersistAndRedirectToListView() throws Exception {" ) );
		assertThat( sourceCode, containsString( "long count = ictssysadminDaoService.getClinicalDocumentService().count();" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(post(\"/clinicaldocument/save\").with(csrf())).andExpect(status().is3xxRedirection()).andExpect(view().name(\"redirect:/clinicaldocument/list\"));" ) );
		assertThat( sourceCode, containsString( "assertEquals(\"count should increase by 1\", count +1 , ictssysadminDaoService.getClinicalDocumentService().count());" ) );

	}

	@Test
	public void shouldGenerateJavaSourceCodeForSpringMVCTestFileWithDotHTMLInRequestMapping() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		properties.setProperty( "controller.request.mapping.extension", ".html" );
		properties.setProperty( "include.schema.in.request.mapping", "false" );

		ControllerMvcTestGenerator generator = new ControllerMvcTestGenerator( packageRoot, domainClass, properties );

		String sourceCode = generator.javaSourceCode();

		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/clinicaldocument/list_alt.html\"))" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/clinicaldocument/list.html\"))" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/clinicaldocument/\"))" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/clinicaldocument/add.html\"))" ) );
	}

	@Test
	public void shouldGenerateJavaSourceCodeForSpringMVCTestFileWithoutDotHTMLInRequestMapping() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		properties.setProperty( "include.schema.in.request.mapping", "false" );

		ControllerMvcTestGenerator generator = new ControllerMvcTestGenerator( packageRoot, domainClass, properties );

		String sourceCode = generator.javaSourceCode();

		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/clinicaldocument/list_alt\"))" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/clinicaldocument/list\"))" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/clinicaldocument/\"))" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/clinicaldocument/add\"))" ) );
	}

	@Test
	public void shouldGenerateSetUpMethodThatLoadsDataAndTestsDatatablesForDomainClassWithIntegerPrimaryKey() {
		String packageRoot = "edu.uiowa.icts";
		String pathPrefix = System.getProperty( "user.dir" );

		Properties properties = new Properties();
		properties.setProperty( "datatables.generation", "2" );
		properties.setProperty( "include.schema.in.request.mapping", "false" );

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

		ControllerMvcTestGenerator generator = new ControllerMvcTestGenerator( packageRoot, jobType, properties );

		String sourceCode = generator.javaSourceCode();

		// test imports
		assertThat( sourceCode, containsString( "import edu.uiowa.icts.aptamer.domain.*;" ) );
		assertThat( sourceCode, containsString( "import edu.uiowa.icts.aptamer.dao.*;" ) );

		// test set up method
		assertThat( sourceCode, containsString( "for(int x=1; x<21; x++){" ) );
		assertThat( sourceCode, containsString( "JobType jobType = new JobType();" ) );
		assertThat( sourceCode, containsString( "aptamerDaoService.getJobTypeService().save(jobType);" ) );
		assertThat( sourceCode, containsString( "if (x == 1){" ) );
		assertThat( sourceCode, containsString( "firstJobType = jobType;" ) );

		// test datatables test
		assertThat( sourceCode, containsString( "public void defaultDatatableShouldReturnJSONDataWith10Rows() throws Exception {" ) );
		assertThat( sourceCode, containsString( "DataTableRequest dtr = getDataTableRequest( Arrays.asList(\"urls\",\"description\",\"jobTypeId\",\"jobs\",\"name\",\"parameters\" ));" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(post(\"/jobtype/datatable\")" ) );
		assertThat( sourceCode, containsString( ".param(\"display\", \"list\")" ) );
		assertThat( sourceCode, containsString( ".accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)" ) );
		assertThat( sourceCode, containsString( ".andExpect(status().isOk())" ) );
		assertThat( sourceCode, containsString( ".andExpect(content().contentType(\"application/json\"))" ) );
		assertThat( sourceCode, containsString( ".andExpect(jsonPath(\"$.recordsTotal\", is((int) aptamerDaoService.getJobTypeService().count())))" ) );
		assertThat( sourceCode, containsString( ".andExpect(jsonPath(\"$.recordsFiltered\", is((int) aptamerDaoService.getJobTypeService().count())))" ) );
		assertThat( sourceCode, containsString( ".andExpect(jsonPath(\"$.draw\", is(\"1\")))" ) );
		assertThat( sourceCode, containsString( ".andExpect(jsonPath(\"$.data\", hasSize(is(10))))" ) );
		assertThat( sourceCode, containsString( ".andExpect(jsonPath(\"$.data[0][0]\", containsString(\"show?\")))" ) );
		assertThat( sourceCode, containsString( ".andExpect(jsonPath(\"$.data[0][0]\", containsString(\"edit?\")))" ) );
		assertThat( sourceCode, containsString( ".andExpect(jsonPath(\"$.data[0][0]\", containsString(\"delete?\")))" ) );

		// test datatables bogus column name
		assertThat( sourceCode, containsString( "public void defaultDatatableShouldReturnErrorTextForBogusColumnName() throws Exception {" ) );
		assertThat( sourceCode, containsString( ".andExpect(jsonPath(\"$.data[0].0\", is(\"[error: column asdfasdf not supported]\")))" ) );

		// test datatables exception scenario
		assertThat( sourceCode, containsString( "public void defaultDatatableShouldReturnExceptionBecauseCantSearchColumnThatDoesntExist() throws Exception {" ) );
		assertThat( sourceCode, containsString( ".andExpect(jsonPath(\"$.data\", hasSize(is(0))))" ) );
		assertThat( sourceCode, containsString( ".andExpect(jsonPath(\"$.error\", IsNull.notNullValue()))" ) );

		// test edit
		assertThat( sourceCode, containsString( "public void editShouldLoadObjectAndDisplayForm() throws Exception {" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/jobtype/edit\").param(\"jobTypeId\", firstJobType.getJobTypeId().toString()))" ) );
		assertThat( sourceCode, containsString( ".andExpect(status().isOk())" ) );
		assertThat( sourceCode, containsString( ".andExpect(model().attributeExists(\"jobType\"))" ) );
		assertThat( sourceCode, containsString( ".andExpect(view().name(\"/aptamer/jobtype/edit\"));" ) );

		// test show
		assertThat( sourceCode, containsString( "public void showShouldLoadAndDisplayObject() throws Exception {" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/jobtype/show\").param(\"jobTypeId\", firstJobType.getJobTypeId().toString()))" ) );
		assertThat( sourceCode, containsString( ".andExpect(status().isOk())" ) );
		assertThat( sourceCode, containsString( ".andExpect(model().attributeExists(\"jobType\"))" ) );
		assertThat( sourceCode, containsString( ".andExpect(view().name(\"/aptamer/jobtype/show\"));" ) );

		// test delete GET
		assertThat( sourceCode, containsString( "public void deleteGetShouldLoadAndDisplayYesNoButtons() throws Exception {" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/jobtype/delete\").param(\"jobTypeId\", firstJobType.getJobTypeId().toString()))" ) );
		assertThat( sourceCode, containsString( ".andExpect(status().isOk())" ) );
		assertThat( sourceCode, containsString( ".andExpect(model().attributeExists(\"jobType\"))" ) );
		assertThat( sourceCode, containsString( ".andExpect(view().name(\"/aptamer/jobtype/delete\"));" ) );

		// test delete POST - YES
		assertThat( sourceCode, containsString( "long count = aptamerDaoService.getJobTypeService().count();" ) );
		assertThat( sourceCode, containsString( "public void deletePostSubmitYesShouldDeleteAndRedirectToListView() throws Exception {" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(post(\"/jobtype/delete\").param(\"jobTypeId\", firstJobType.getJobTypeId().toString())" ) );
		assertThat( sourceCode, containsString( ".param(\"submit\", \"Yes\").with(csrf())).andExpect(status().is3xxRedirection()).andExpect(view().name(\"redirect:/jobtype/list\")" ) );
		assertThat( sourceCode, containsString( "assertEquals(\"count should decrease by 1\", count - 1 , aptamerDaoService.getJobTypeService().count());" ) );

		// test delete POST - NO
		assertThat( sourceCode, containsString( "long count = aptamerDaoService.getJobTypeService().count();" ) );
		assertThat( sourceCode, containsString( "public void deletePostSubmitNoShouldNotDeleteAndRedirectToListView() throws Exception {" ) );
		assertThat( sourceCode, containsString( "mockMvc.perform(post(\"/jobtype/delete\").param(\"jobTypeId\", firstJobType.getJobTypeId().toString())" ) );
		assertThat( sourceCode, containsString( ".param(\"submit\", \"No\").with(csrf())).andExpect(status().is3xxRedirection()).andExpect(view().name(\"redirect:/jobtype/list\")" ) );
		assertThat( sourceCode, containsString( "assertEquals(\"count should NOT decrease by 1\", count , aptamerDaoService.getJobTypeService().count());" ) );

	}

	@Test
	public void shouldGenerateMVCTestsCorrectlyWhenPrimaryKeyDoesntIncludeClassName() {
		String packageRoot = "edu.uiowa.icts";
		String pathPrefix = System.getProperty( "user.dir" );

		Properties properties = new Properties();
		properties.setProperty( "datatables.generation", "2" );
		properties.setProperty( "include.schema.in.request.mapping", "false" );

		DatabaseSchemaLoader theLoader = new ClayLoader();
		try {
			theLoader.run( pathPrefix + "/src/test/resources/mvc-test-generator.clay" );
		} catch ( Exception e ) {
			assertNull( e );
		}
		Database database = theLoader.getDatabase();
		//   database.dump();
		SpringHibernateModel model = new SpringHibernateModel( database, packageRoot, properties );

		DomainClass tablewithprimarykeyasid = null;
		for ( DomainClass dc : model.getDomainClassList() ) {
			if ( dc.getIdentifier().equals( "TableWithPrimaryKeyAsId" ) ) {
				tablewithprimarykeyasid = dc;
			}
		}

		ControllerMvcTestGenerator generator = new ControllerMvcTestGenerator( packageRoot, tablewithprimarykeyasid, properties );

		String sourceCode = generator.javaSourceCode();
		// test edit
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/tablewithprimarykeyasid/edit\").param(\"id\", firstTableWithPrimaryKeyAsId.getId().toString()))" ) );

		// test show
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/tablewithprimarykeyasid/show\").param(\"id\", firstTableWithPrimaryKeyAsId.getId().toString()))" ) );

		// test delete GET
		assertThat( sourceCode, containsString( "mockMvc.perform(get(\"/tablewithprimarykeyasid/delete\").param(\"id\", firstTableWithPrimaryKeyAsId.getId().toString()))" ) );

		// test delete POST - YES
		assertThat( sourceCode, containsString( "mockMvc.perform(post(\"/tablewithprimarykeyasid/delete\").param(\"id\", firstTableWithPrimaryKeyAsId.getId().toString())" ) );

		// test delete POST - NO
		assertThat( sourceCode, containsString( "mockMvc.perform(post(\"/tablewithprimarykeyasid/delete\").param(\"id\", firstTableWithPrimaryKeyAsId.getId().toString())" ) );
	}
}
