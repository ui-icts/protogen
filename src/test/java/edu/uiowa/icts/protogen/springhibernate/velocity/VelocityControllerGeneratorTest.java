package edu.uiowa.icts.protogen.springhibernate.velocity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.junit.Test;

import edu.uiowa.icts.protogen.springhibernate.DomainClass;
import edu.uiowa.icts.protogen.springhibernate.SpringHibernateModel;
import edu.uiowa.webapp.ClayLoader;
import edu.uiowa.webapp.Database;
import edu.uiowa.webapp.DatabaseSchemaLoader;
import edu.uiowa.webapp.Schema;

public class VelocityControllerGeneratorTest {

	@Test
	public void getPathExtensionShouldIncludeDotHTML() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		properties.setProperty( "controller.request.mapping.extension", ".html" );
		VelocityControllerGenerator generator = new VelocityControllerGenerator( packageRoot, domainClass, properties );
		assertEquals( ".html", generator.getPathExtension() );
	}

	@Test
	public void getPathExtensionShouldNotIncludeDotHTMLBecauseEmptyProperty() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		properties.setProperty( "controller.request.mapping.extension", "" );
		VelocityControllerGenerator generator = new VelocityControllerGenerator( packageRoot, domainClass, properties );
		assertEquals( "", generator.getPathExtension() );
	}

	@Test
	public void getPathExtensionShouldNotIncludeDotHTMLBecauseNullProperty() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		//	properties.setProperty( "controller.request.mapping.extension", "" );	
		VelocityControllerGenerator generator = new VelocityControllerGenerator( packageRoot, domainClass, properties );
		assertEquals( "", generator.getPathExtension() );
	}

	@Test
	public void getPathPrefixShouldIncludeSchemaName() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		//	properties.setProperty( "include.schema.in.request.mapping", "false" );			
		VelocityControllerGenerator generator = new VelocityControllerGenerator( packageRoot, domainClass, properties );
		assertEquals( "/ictssysadmin/clinicaldocument", generator.getPathPrefix() );
		assertEquals( "/ictssysadmin/clinicaldocument", generator.getJspPath() );
	}

	@Test
	public void getPathPrefixShouldNotIncludeSchemaName() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		properties.setProperty( "include.schema.in.request.mapping", "false" );
		VelocityControllerGenerator generator = new VelocityControllerGenerator( packageRoot, domainClass, properties );
		assertEquals( "/clinicaldocument", generator.getPathPrefix() );
		assertEquals( "/ictssysadmin/clinicaldocument", generator.getJspPath() );
	}

	@Test
	public void getBasePackageNameShouldIncludeSchemaName() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		//	properties.setProperty( "include.schema.in.package.name", "false" );			
		VelocityControllerGenerator generator = new VelocityControllerGenerator( packageRoot, domainClass, properties );
		assertEquals( "edu.uiowa.icts.ictssysadmin", generator.getBasePackageName() );
	}

	@Test
	public void getBasePackageNameShouldNotIncludeSchemaName() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		properties.setProperty( "include.schema.in.package.name", "false" );
		VelocityControllerGenerator generator = new VelocityControllerGenerator( packageRoot, domainClass, properties );
		assertEquals( "edu.uiowa.icts", generator.getBasePackageName() );
	}

	@Test
	public void getPackageNameShouldIncludeSchemaName() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		//	properties.setProperty( "include.schema.in.package.name", "false" );			
		VelocityControllerGenerator generator = new VelocityControllerGenerator( packageRoot, domainClass, properties );
		assertEquals( "edu.uiowa.icts.ictssysadmin.controller", generator.getPackageName() );
	}

	@Test
	public void getPackageNameShouldNotIncludeSchemaName() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		properties.setProperty( "include.schema.in.package.name", "false" );
		VelocityControllerGenerator generator = new VelocityControllerGenerator( packageRoot, domainClass, properties );
		assertEquals( "edu.uiowa.icts.controller", generator.getPackageName() );
	}

	@Test
	public void shouldGenerateAdddMethod() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();

		VelocityControllerGenerator generator = new VelocityControllerGenerator( packageRoot, domainClass, properties );

		String sourceCode = generator.javaSourceCode();

		// test add
		assertThat( sourceCode, containsString( "@RequestMapping( value = \"add\", method = RequestMethod.GET )" ) );
		assertThat( sourceCode, containsString( "public String add( Model model ) {" ) );
		assertThat( sourceCode, containsString( "model.addAttribute( \"clinicalDocument\", new ClinicalDocument() );" ) );
		assertThat( sourceCode, containsString( "return \"/ictssysadmin/clinicaldocument/edit\";" ) );
	}

	@Test
	public void shouldGenerateSaveMethodWithSingularPrimaryKey() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();

		VelocityControllerGenerator generator = new VelocityControllerGenerator( packageRoot, domainClass, properties );

		String sourceCode = generator.javaSourceCode();

		// test save
		assertThat( sourceCode, containsString( "@RequestMapping( value = \"save\", method = RequestMethod.POST )" ) );
		assertThat( sourceCode, containsString( "public String save( @Valid @ModelAttribute( \"clinicalDocument\" ) ClinicalDocument clinicalDocument, BindingResult result, Model model ) {" ) );
		// assertThat( sourceCode, containsString( "if (result.hasErrors()) { return \"/ictssysadmin/clinicaldocument/edit\"; }" ) );
		assertThat( sourceCode, containsString( "ictssysadminDaoService.getClinicalDocumentService().saveOrUpdate( clinicalDocument );" ) );
		assertThat( sourceCode, containsString( "return \"redirect:/ictssysadmin/clinicaldocument/list\";" ) );
	}

	@Test
	public void shouldGenerateJavaSourceCodeForSpringControllerFileWithSchemaNameInRequestMapping() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		properties.setProperty( "datatables.generation", "2" );
		//	properties.setProperty( "include.schema.in.package.name", "false" );	
		//	properties.setProperty( "include.schema.in.request.mapping", "false" );	

		VelocityControllerGenerator generator = new VelocityControllerGenerator( packageRoot, domainClass, properties );

		String sourceCode = generator.javaSourceCode();

		assertThat( sourceCode, containsString( "package edu.uiowa.icts.ictssysadmin.controller;" ) );
		assertThat( sourceCode, containsString( "* Generated by Protogen" ) );

		SimpleDateFormat ft = new SimpleDateFormat( "MM/dd/yyyy" );
		assertThat( sourceCode, containsString( ft.format( new Date() ) ) );
		assertThat( sourceCode, containsString( "@RequestMapping( \"/ictssysadmin/clinicaldocument/*\" )" ) );
		assertThat( sourceCode, containsString( "ClinicalDocumentController extends AbstractIctssysadminController" ) );
		assertThat( sourceCode, containsString( "private static final Log log = LogFactory.getLog( ClinicalDocumentController.class );" ) );

		// test list_alt
		assertThat( sourceCode, containsString( "public String listNoScript(Model model) {" ) );
		assertThat( sourceCode, containsString( "model.addAttribute( \"clinicalDocumentList\", ictssysadminDaoService.getClinicalDocumentService().list() );" ) );
		assertThat( sourceCode, containsString( "return \"/ictssysadmin/clinicaldocument/list_alt\";" ) );
		// test list
		assertThat( sourceCode, containsString( "@RequestMapping(value = {\"list\", \"\", \"/\"}, method = RequestMethod.GET)" ) );
		assertThat( sourceCode, containsString( "public String list() {" ) );
		assertThat( sourceCode, containsString( "return \"/ictssysadmin/clinicaldocument/list\";" ) );
	}

	@Test
	public void shouldGenerateJavaSourceCodeForSpringControllerFileWithoutSchemaNameInRequestMapping() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		properties.setProperty( "datatables.generation", "2" );
		//	properties.setProperty( "include.schema.in.package.name", "false" );	
		properties.setProperty( "include.schema.in.request.mapping", "false" );

		VelocityControllerGenerator generator = new VelocityControllerGenerator( packageRoot, domainClass, properties );

		String sourceCode = generator.javaSourceCode();

		assertThat( sourceCode, containsString( "package edu.uiowa.icts.ictssysadmin.controller;" ) );
		assertThat( sourceCode, containsString( "* Generated by Protogen" ) );

		SimpleDateFormat ft = new SimpleDateFormat( "MM/dd/yyyy" );
		assertThat( sourceCode, containsString( ft.format( new Date() ) ) );
		assertThat( sourceCode, containsString( "@RequestMapping( \"/clinicaldocument/*\" )" ) );
		assertThat( sourceCode, containsString( "ClinicalDocumentController extends AbstractIctssysadminController" ) );
		assertThat( sourceCode, containsString( "private static final Log log = LogFactory.getLog( ClinicalDocumentController.class );" ) );

		// test list_alt
		assertThat( sourceCode, containsString( "public String listNoScript(Model model) {" ) );
		assertThat( sourceCode, containsString( "model.addAttribute( \"clinicalDocumentList\", ictssysadminDaoService.getClinicalDocumentService().list() );" ) );
		assertThat( sourceCode, containsString( "return \"/ictssysadmin/clinicaldocument/list_alt\";" ) );
		// test list
		assertThat( sourceCode, containsString( "public String list() {" ) );
		assertThat( sourceCode, containsString( "return \"/ictssysadmin/clinicaldocument/list\";" ) );
	}

	@Test
	public void shouldGenerateJavaSourceCodeForSpringControllerFileWithoutDotHTMLExtension() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		//	properties.setProperty( "controller.request.mapping.extension", "" );	

		VelocityControllerGenerator generator = new VelocityControllerGenerator( packageRoot, domainClass, properties );

		String sourceCode = generator.javaSourceCode();

		assertThat( sourceCode, containsString( "@RequestMapping(value = {\"list\", \"\", \"/\"}, method = RequestMethod.GET)" ) );
		assertThat( sourceCode, containsString( "@RequestMapping( value = \"list_alt\", method = RequestMethod.GET )" ) );
		assertThat( sourceCode, containsString( "@RequestMapping( value = \"add\", method = RequestMethod.GET )" ) );
		assertThat( sourceCode, containsString( "@RequestMapping( value = \"edit\", method = RequestMethod.GET )" ) );
		assertThat( sourceCode, containsString( "@RequestMapping( value = \"show\", method = RequestMethod.GET )" ) );
		assertThat( sourceCode, containsString( "@RequestMapping( value = \"delete\", method = RequestMethod.GET )" ) );
		assertThat( sourceCode, containsString( "@RequestMapping( value = \"save\", method = RequestMethod.POST )" ) );
		assertThat( sourceCode, containsString( "@RequestMapping( value = \"delete\", method = RequestMethod.POST )" ) );
		assertThat( sourceCode, containsString( "return \"redirect:/ictssysadmin/clinicaldocument/list" + properties.getProperty( "controller.request.mapping.extension", "" ) + "\";" ) );
		// test datatable links
		assertThat( sourceCode, containsString( "urls += \"<a href=\\\"show?\"+\"\\\"><span class=\\\"glyphicon glyphicon-eye-open\\\"></a>\";" ) );
		assertThat( sourceCode, containsString( "urls += \"<a href=\\\"edit?\"+\"\\\"><span class=\\\"glyphicon glyphicon-pencil\\\"></a>\";" ) );
		assertThat( sourceCode, containsString( "urls += \"<a href=\\\"delete?\"+\"\\\"><span class=\\\"glyphicon glyphicon-trash\\\"></a>\";" ) );
	}

	@Test
	public void shouldGenerateJavaSourceCodeForSpringControllerFileWithDotHTMLExtension() {
		String packageRoot = "edu.uiowa.icts";

		Schema schema = new Schema();
		schema.setLabel( "ictssysadmin" );

		DomainClass domainClass = new DomainClass( null );
		domainClass.setSchema( schema );
		domainClass.setIdentifier( "ClinicalDocument" );

		Properties properties = new Properties();
		properties.setProperty( "controller.request.mapping.extension", ".html" );

		VelocityControllerGenerator generator = new VelocityControllerGenerator( packageRoot, domainClass, properties );

		String sourceCode = generator.javaSourceCode();

		assertThat( sourceCode, containsString( "@RequestMapping(value = {\"list.html\", \"\", \"/\"}, method = RequestMethod.GET)" ) );
		assertThat( sourceCode, containsString( "@RequestMapping( value = \"list_alt.html\", method = RequestMethod.GET )" ) );
		assertThat( sourceCode, containsString( "@RequestMapping( value = \"add.html\", method = RequestMethod.GET )" ) );
		assertThat( sourceCode, containsString( "@RequestMapping( value = \"edit.html\", method = RequestMethod.GET )" ) );
		assertThat( sourceCode, containsString( "@RequestMapping( value = \"show.html\", method = RequestMethod.GET )" ) );
		assertThat( sourceCode, containsString( "@RequestMapping( value = \"delete.html\", method = RequestMethod.GET )" ) );
		assertThat( sourceCode, containsString( "@RequestMapping( value = \"save.html\", method = RequestMethod.POST )" ) );
		assertThat( sourceCode, containsString( "@RequestMapping( value = \"delete.html\", method = RequestMethod.POST )" ) );
		assertThat( sourceCode, containsString( "return \"redirect:/ictssysadmin/clinicaldocument/list.html\";" ) );

		// test datatable links
		assertThat( sourceCode, containsString( "urls += \"<a href=\\\"show.html?\"+\"\\\"><span class=\\\"glyphicon glyphicon-eye-open\\\"></a>\";" ) );
		assertThat( sourceCode, containsString( "urls += \"<a href=\\\"edit.html?\"+\"\\\"><span class=\\\"glyphicon glyphicon-pencil\\\"></a>\";" ) );
		assertThat( sourceCode, containsString( "urls += \"<a href=\\\"delete.html?\"+\"\\\"><span class=\\\"glyphicon glyphicon-trash\\\"></a>\";" ) );
	}

	@Test
	public void shouldGenerateDatatablesWithSizeDisplayedForCollections() {
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

		VelocityControllerGenerator generator = new VelocityControllerGenerator( packageRoot, jobType, properties );

		String sourceCode = generator.javaSourceCode();
		//	System.out.println(sourceCode);

		assertThat( sourceCode, containsString( "tableRow.put( jobType.getJobTypeId() );" ) );
		assertThat( sourceCode, containsString( "tableRow.put( jobType.getParameters() );" ) );
		assertThat( sourceCode, containsString( "tableRow.put( jobType.getJobs().size() );" ) );
	}
}
