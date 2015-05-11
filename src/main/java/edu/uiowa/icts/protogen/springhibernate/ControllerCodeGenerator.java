package edu.uiowa.icts.protogen.springhibernate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.uiowa.icts.protogen.springhibernate.velocity.AbstractControllerMVCTestsGenerator;
import edu.uiowa.icts.protogen.springhibernate.velocity.ControllerMvcTestGenerator;
import edu.uiowa.icts.protogen.springhibernate.velocity.VelocityAbstractControllerGenerator;
import edu.uiowa.icts.protogen.springhibernate.velocity.VelocityControllerGenerator;
import edu.uiowa.webapp.Schema;

/**
 * @author bkusenda, schappetj, rrlorent, rmjames
 * Generates spring controller java files
 */
public class ControllerCodeGenerator extends AbstractSpringHibernateCodeGenerator {

	protected static final Log log = LogFactory.getLog( ControllerCodeGenerator.class );

	private Properties properties;

	public ControllerCodeGenerator( SpringHibernateModel model, String pathBase, String packageRoot, Properties properties ) {
		super( model, pathBase, packageRoot );
		( new File( packageRootPath ) ).mkdirs();
		this.properties = properties;
	}

	private void generateController( DomainClass domainClass ) throws IOException {

		String packageName = model.getPackageRoot() + ( Boolean.valueOf( properties.getProperty( "include.schema.in.package.name", "true" ) ) ? "." + domainClass.getSchema().getLowerLabel() : "" ) + ".controller";

		String packagePath = pathBase + "/" + packageName.replaceAll( "\\.", "/" );

		String className = domainClass.getIdentifier() + "Controller";

		if ( Boolean.valueOf( properties.getProperty( "generate.test", "false" ) ) ) {
			// Generate corresponding Spring MVC test file
			ControllerMvcTestGenerator generator = new ControllerMvcTestGenerator( model.getPackageRoot(), domainClass, properties );
			BufferedWriter testWriter = createFileInSrcElseTarget( packagePath.replaceFirst( "src/main", "src/test" ), className + "MvcTest.java" );
			try {
				testWriter.write( generator.javaSourceCode() );
			} finally {
				testWriter.close();
			}
		}

		VelocityControllerGenerator vcg = new VelocityControllerGenerator( model.getPackageRoot(), domainClass, properties );
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
				generateAbstractController( schema, model.getPackageRoot(), properties );
			}
		}
	}

	/**
	 * generates an abstract controller
	 */
	private void generateAbstractController( Schema schema, String packageRoot, Properties properties ) throws IOException {

		String packageName = model.getPackageRoot() + ( Boolean.valueOf( properties.getProperty( "include.schema.in.package.name", "true" ) ) ? "." + schema.getLowerLabel() : "" ) + ".controller";
		String packagePath = pathBase + "/" + packageName.replaceAll( "\\.", "/" );

		// generate abstract controller test
		AbstractControllerMVCTestsGenerator testGenerator = new AbstractControllerMVCTestsGenerator( schema, packageRoot, properties );
		BufferedWriter testWriter = createFileInSrcElseTarget( packagePath.replaceFirst( "src/main", "src/test" ), "AbstractControllerMVCTests.java" );
		testWriter.write( testGenerator.javaSourceCode() );
		testWriter.close();

		// generate abstract controller
		VelocityAbstractControllerGenerator generator = new VelocityAbstractControllerGenerator( schema, packageRoot, properties );

		String abstractControllerClassName = properties.getProperty( schema.getLabel().toLowerCase() + ".abstract.controller.name" );
		if ( abstractControllerClassName == null ) {
			abstractControllerClassName = "Abstract" + schema.getUpperLabel() + "Controller";
		}

		BufferedWriter out = createFileInSrcElseTarget( packagePath, abstractControllerClassName + ".java" );
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
			generateController( dc );
		}
		generateAbstractControllers();
	}

}
