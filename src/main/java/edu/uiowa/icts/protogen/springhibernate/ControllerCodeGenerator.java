package edu.uiowa.icts.protogen.springhibernate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.plexus.util.StringUtils;

import edu.uiowa.icts.protogen.springhibernate.velocity.AbstractControllerMVCTestsGenerator;
import edu.uiowa.icts.protogen.springhibernate.velocity.ControllerMvcTestGenerator;
import edu.uiowa.icts.protogen.springhibernate.velocity.VelocityControllerGenerator;
import edu.uiowa.webapp.Attribute;
import edu.uiowa.webapp.Schema;

/**
 * @author bkusenda, schappetj, rrlorent, rmjames
 * Generates spring controller java files
 */
public class ControllerCodeGenerator extends AbstractSpringHibernateCodeGenerator {

	protected static final Log log = LogFactory.getLog( ControllerCodeGenerator.class );

	private String interfaceSuffix = "Service";
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
				String packageName = model.getPackageRoot() + ( Boolean.valueOf( properties.getProperty( "include.schema.in.package.name", "true" ) ) ? "." + schema.getLowerLabel() : "" ) + ".controller";
				String packagePath = pathBase + "/" + packageName.replaceAll( "\\.", "/" );

				String abstractControllerClassName = properties.getProperty( schema.getLabel().toLowerCase() + ".abstract.controller.name" );
				if ( abstractControllerClassName == null ) {
					abstractControllerClassName = "Abstract" + schema.getUpperLabel() + "Controller";
				}
				generateAbstractController( schema, abstractControllerClassName, packageName, packagePath );
			}
		}
	}

	/**
	 * generates an abstract controller
	 */
	private void generateAbstractController( Schema schema, String className, String packageName, String packagePath ) throws IOException {
		// generate abstract controller test file first!
		AbstractControllerMVCTestsGenerator generator = new AbstractControllerMVCTestsGenerator( packageName );
		BufferedWriter testWriter = createFileInSrcElseTarget( packagePath.replaceFirst( "src/main", "src/test" ), "AbstractControllerMVCTests.java" );
		testWriter.write( generator.javaSourceCode() );
		testWriter.close();

		// then do real abstract controller
		String daoPackageName = model.getPackageRoot() + ( Boolean.valueOf( properties.getProperty( "include.schema.in.package.name", "true" ) ) ? "." + schema.getLowerLabel() : "" ) + ".dao";

		List<String> importList = new ArrayList<String>();
		importList.add( "import edu.uiowa.icts.spring.*;" );
		importList.add( "import org.springframework.beans.factory.annotation.Autowired;" );

		importList.add( "import org.springframework.security.core.context.SecurityContextHolder;" );
		importList.add( "import " + daoPackageName + ".*;" );

		BufferedWriter out = createFileInSrcElseTarget( packagePath, className + ".java" );

		/*
		 * Print Package
		 */
		out.write( "package " + packageName + ";\n" );
		lines( out, 1 );

		/*
		 * Print imports
		 */
		for ( String importSt : importList ) {
			out.write( importSt + "\n" );
		}

		lines( out, 1 );
		out.write( "/**\n" );
		out.write( " * Generated by Protogen\n" );
		out.write( " * " + ( new Date() ) + "\n" );
		out.write( " */\n" );

		String type = properties.getProperty( schema.getUpperLabel().toLowerCase() + ".master.dao.service.name" );
		if ( type == null || "".equals( type.trim() ) ) {
			type = schema.getUpperLabel() + "DaoService";
		}

		String variableName = StringUtils.substring( type, 0, 1 ).toLowerCase() + StringUtils.substring( type, 1, type.length() );

		/*
		 * Print  header
		 */
		out.write( "public abstract class " + className + " {\n\n" );
		spaces( out, 4 );
		out.write( "protected " + type + " " + variableName + ";\n\n" );

		spaces( out, 4 );
		out.write( "@Autowired \n" );
		out.write( createSetter( type, variableName, 4 ) );
		lines( out, 1 );
		spaces( out, 4 );
		out.write( "public String getUsername() {\n" );
		spaces( out, 8 );
		out.write( "return SecurityContextHolder.getContext().getAuthentication().getName();" );
		lines( out, 1 );
		spaces( out, 4 );
		out.write( "}" );

		lines( out, 1 );
		out.write( "}" );
		out.close();
	}

	public String generateListMethod( DomainClass dc, String accessor, String jspPath, int indent ) {
		StringBuffer output = new StringBuffer();
		output.append( indent( indent ) + "@RequestMapping(value = \"list.html\", method = RequestMethod.GET)\n" );
		output.append( indent( indent ) + "public ModelAndView list() {\n" );
		output.append( indent( indent * 2 ) + "return new ModelAndView(\"" + jspPath + "/list\");\n" );
		output.append( indent( indent ) + "}" );
		return output.toString();
	}

	public String generateNoScriptListMethod( DomainClass dc, String accessor, String jspPath, int indent ) {
		StringBuffer output = new StringBuffer();
		output.append( indent( indent ) + "@RequestMapping(value = \"list_alt.html\", method = RequestMethod.GET)\n" );
		output.append( indent( indent ) + "public ModelAndView listNoScript() {\n" );
		output.append( indent( indent * 2 ) + "ModelMap model = new ModelMap();\n" );
		output.append( indent( indent * 2 ) + "model.addAttribute(\"" + dc.getLowerIdentifier() + "List\"," + accessor + ".list());\n" );
		output.append( indent( indent * 2 ) + "return new ModelAndView(\"" + jspPath + "/list_alt\", model);\n" );
		output.append( indent( indent ) + "}" );
		return output.toString();
	}

	public String generateDataTableMethod( DomainClass dc, String accessor, String jspPath, int indent ) {

		StringBuffer output = new StringBuffer();
		output.append( indent( indent ) + "@RequestMapping(value = \"datatable.html\", method = RequestMethod.GET)\n" );
		output.append( indent( indent ) + "public void datatable( HttpServletRequest request, HttpServletResponse response, \n" );

		if ( StringUtils.equals( properties.getProperty( "datatables.generation", "1" ), "2" ) ) {

			output.append( indent( indent * 2 ) + "@RequestParam( value = \"length\" , required = false ) Integer limit, \n" );
			output.append( indent( indent * 2 ) + "@RequestParam( value = \"start\" , required = false ) Integer start, \n" );
			output.append( indent( indent * 2 ) + "@RequestParam( value = \"draw\" , required = false ) String draw, \n" );
			output.append( indent( indent * 2 ) + "@RequestParam( value = \"search[regex]\" , required = false , defaultValue = \"false\" ) Boolean searchRegularExpression, \n" );
			output.append( indent( indent * 2 ) + "@RequestParam( value = \"search[value]\" , required = false ) String search, \n" );
			output.append( indent( indent * 2 ) + "@RequestParam( value = \"columnCount\" , required = false , defaultValue = \"0\" ) Integer columnCount, \n" );
			output.append( indent( indent * 2 ) + "@RequestParam( value = \"individualSearch\" , required = false , defaultValue = \"false\" ) Boolean individualSearch, \n" );

		} else {

			output.append( indent( indent * 2 ) + "@RequestParam(value=\"iDisplayLength\") Integer limit, \n" );
			output.append( indent( indent * 2 ) + "@RequestParam(value=\"iDisplayStart\") Integer start, \n" );
			output.append( indent( indent * 2 ) + "@RequestParam(value=\"iColumns\") Integer numberColumns, \n" );
			output.append( indent( indent * 2 ) + "@RequestParam(value=\"iColumns\") Integer columnCount, \n" );
			output.append( indent( indent * 2 ) + "@RequestParam(value=\"sColumns\") String columns, \n" );
			output.append( indent( indent * 2 ) + "@RequestParam(value=\"sEcho\") String echo, \n" );
			output.append( indent( indent * 2 ) + "@RequestParam(value=\"bFilter\") String bFilter, \n" );
			output.append( indent( indent * 2 ) + "@RequestParam(value=\"iSortingCols\", required=false) Integer sortingColsCount, \n" );
			output.append( indent( indent * 2 ) + "@RequestParam(value=\"sSearch\", required=false) String search, \n" );

		}

		output.append( indent( indent * 2 ) + "@RequestParam( value = \"display\", required = false, defaultValue = \"list\" ) String display ) {\n\n" );

		if ( StringUtils.equals( properties.getProperty( "datatables.generation", "1" ), "2" ) ) {

		}

		output.append( indent( indent * 2 ) + "List<DataTableHeader> headers = new ArrayList<DataTableHeader>(); \n" );
		output.append( indent( indent * 2 ) + "for ( int i = 0; i < columnCount; i++ ) { \n" );
		output.append( indent( indent * 3 ) + "DataTableHeader dth = new DataTableHeader(); \n" );
		output.append( indent( indent * 3 ) + "dth.setData( Integer.valueOf( request.getParameter( \"columns[\" + i + \"][data]\" ) ) ); \n" );
		output.append( indent( indent * 3 ) + "dth.setName( request.getParameter( \"columns[\" + i + \"][name]\" ) ); \n" );
		output.append( indent( indent * 3 ) + "dth.setOrderable( Boolean.valueOf( request.getParameter( \"columns[\" + i + \"][orderable]\" ) ) ); \n" );
		output.append( indent( indent * 3 ) + "dth.setSearchable( Boolean.valueOf( request.getParameter( \"columns[\" + i + \"][searchable]\" ) ) ); \n" );
		output.append( indent( indent * 3 ) + "dth.setSearchValue( request.getParameter( \"columns[\" + i + \"][search][value]\" ) ); \n" );
		output.append( indent( indent * 3 ) + "dth.setSearchRegex( Boolean.valueOf( request.getParameter( \"columns[\" + i + \"][search][regex]\" ) ) ); \n" );
		output.append( indent( indent * 3 ) + "headers.add( dth ); \n" );
		output.append( indent( indent * 2 ) + "} \n" );

		output.append( indent( indent * 2 ) + "ArrayList<SortColumn> sorts = new ArrayList<SortColumn>();\n" );

		indent += 4;
		output.append( indent( indent ) + "try {\n\n" );

		indent += 4;

		output.append( indent( indent ) + "response.setContentType( \"application/json\" );\n\n" );

		output.append( indent( indent ) + "String[] colArr = columns.split(\",\");\n\n" );

		output.append( indent( indent ) + "if( sortingColsCount != null ){\n" );
		indent += 4;
		output.append( indent( indent ) + "for( int i = 0; i < sortingColsCount; i++){\n" );

		indent += 4;
		output.append( indent( indent ) + "if( i < colArr.length ){\n" );

		indent += 4;
		output.append( indent( indent ) + "Integer colnum = null;\n" );
		output.append( indent( indent ) + "String col = request.getParameter( \"iSortCol_\" + i );\n" );
		output.append( indent( indent ) + "if ( col != null ) {\n" );

		indent += 4;
		output.append( indent( indent ) + "try {\n" );

		indent += 4;
		output.append( indent( indent ) + "colnum = Integer.parseInt( col );\n" );

		indent -= 4;
		output.append( indent( indent ) + "} catch ( NumberFormatException e ) {\n" );

		indent += 4;
		output.append( indent( indent ) + "continue;\n" );

		indent -= 4;
		output.append( indent( indent ) + "}\n" );
		output.append( indent( indent ) + "if( colnum != null ){\n" );

		indent += 4;
		output.append( indent( indent ) + "sorts.add( new SortColumn( colArr[ colnum ], request.getParameter( \"sSortDir_\" + i ) ) );\n" );

		indent -= 4;
		output.append( indent( indent ) + "}\n" );

		indent -= 4;
		output.append( indent( indent ) + "}\n" );

		indent -= 4;
		output.append( indent( indent ) + "}\n" );

		indent -= 4;
		output.append( indent( indent ) + "}\n" );

		indent -= 4;
		output.append( indent( indent ) + "}\n\n" );

		output.append( indent( indent ) + "GenericDaoListOptions options = new GenericDaoListOptions();\n\n" );

		output.append( indent( indent ) + "if( Boolean.valueOf( bFilter ) ){\n" );
		indent += 4;
		output.append( indent( indent ) + "ArrayList<String> searchColumns = new ArrayList<String>();\n" );
		output.append( indent( indent ) + "for( int i = 0; i < numberColumns; i++ ){\n" );

		indent += 4;
		output.append( indent( indent ) + "if( Boolean.valueOf( request.getParameter( \"bSearchable_\"+i ) ) ){\n" );

		indent += 4;
		output.append( indent( indent ) + "searchColumns.add( colArr[i] );\n" );
		indent -= 4;
		output.append( indent( indent ) + "}\n" );
		indent -= 4;
		output.append( indent( indent ) + "}\n" );

		output.append( indent( indent ) + "options.setSearch( search );\n" );
		output.append( indent( indent ) + "options.setSearchColumns( searchColumns );\n" );

		indent -= 4;
		output.append( indent( indent ) + "} else {\n" );
		indent += 4;

		output.append( indent( indent ) + "HashMap<String,Object> likes = new HashMap<String, Object>();\n" );
		output.append( indent( indent ) + "for( String column : colArr ){\n" );
		indent += 4;
		output.append( indent( indent ) + "String columnValue = request.getParameter( column );\n" );
		output.append( indent( indent ) + "if( columnValue != null ){\n" );
		indent += 4;
		output.append( indent( indent ) + "for ( String splitColumnValue : StringUtils.split( columnValue, ' ' ) ) {\n" );
		indent += 4;
		output.append( indent( indent ) + "likes.put( column, splitColumnValue );\n" );
		indent -= 4;
		output.append( indent( indent ) + "}\n" );
		indent -= 4;
		output.append( indent( indent ) + "}\n" );
		indent -= 4;
		output.append( indent( indent ) + "}\n" );
		output.append( indent( indent ) + "options.setIndividualLikes(likes);\n" );

		indent -= 4;
		output.append( indent( indent ) + "}\n\n" );

		output.append( indent( indent ) + "Integer count = " + accessor + ".count( options );\n\n" );

		output.append( indent( indent ) + "options.setLimit(limit);\n" );
		output.append( indent( indent ) + "options.setStart(start);\n" );
		output.append( indent( indent ) + "options.setSorts(sorts);\n\n" );

		output.append( indent( indent ) + "List<" + dc.getIdentifier() + "> " + dc.getLowerIdentifier() + "List = " + accessor + ".list( options );\n\n" );

		output.append( indent( indent ) + "JSONObject ob = new JSONObject();\n" );
		output.append( indent( indent ) + "ob.put( \"sEcho\", echo );\n" );
		output.append( indent( indent ) + "ob.put( \"iTotalDisplayRecords\", count );\n" );
		output.append( indent( indent ) + "ob.put( \"iTotalRecords\", count );\n" );
		output.append( indent( indent ) + "JSONArray jsonArray = new JSONArray();\n" );
		output.append( indent( indent ) + "for( " + dc.getIdentifier() + " " + dc.getLowerIdentifier() + " : " + dc.getLowerIdentifier() + "List ){\n" );

		indent += 4;

		output.append( indent( indent ) + "JSONArray tmp = new JSONArray();\n" );
		output.append( indent( indent ) + "for( String column : colArr ){\n" );

		indent += 4;

		int count = 0;
		Iterator<ClassVariable> iter = dc.listAllIter();
		while ( iter.hasNext() ) {
			ClassVariable cv = iter.next();
			if ( cv.isPrimary() && dc.isUsesCompositeKey() ) {
				for ( Attribute a : dc.getEntity().getPrimaryKeyAttributes() ) {
					output.append( indent( indent ) + ( count > 0 ? "} else " : "" ) + "if( \"id." + a.getLowerLabel() + "\".equals(column) ){\n" );
					indent += 4;
					output.append( indent( indent ) + "tmp.put(" + dc.getLowerIdentifier() + ".getId().get" + a.getUpperLabel() + "());\n" );
					indent -= 4;
					count++;
				}
			} else {
				output.append( indent( indent ) + ( count > 0 ? "} else " : "" ) + "if( \"" + cv.getLowerIdentifier() + "\".equals(column) ){\n" );
				indent += 4;
				output.append( indent( indent ) + "tmp.put(" + dc.getLowerIdentifier() + ".get" + cv.getUpperIdentifier() + "());\n" );
				indent -= 4;
				count++;
			}
		}

		output.append( indent( indent ) + ( count > 0 ? "} else " : "" ) + "if( \"urls\".equals(column)) {\n" );
		indent += 4;

		output.append( indent( indent ) + "String urls = \"\";\n" );
		output.append( indent( indent ) + "if( \"list\".equals( display ) ){\n" );

		indent += 4;

		String params = "";
		List<String[]> compositeKey = new ArrayList<String[]>();
		if ( dc.isUsesCompositeKey() ) {
			for ( Attribute a : dc.getEntity().getPrimaryKeyAttributes() ) {
				params += "\"" + a.getLowerLabel() + "=\"+" + dc.getLowerIdentifier() + ".getId().get" + a.getUpperLabel() + "()+\"&\"+";
				compositeKey.add( new String[] { a.getType(), a.getLowerLabel() } );
			}
			if ( !"".equals( params ) ) {
				params = params.substring( 0, params.length() - 4 );
			}
		} else {
			for ( ClassVariable classVariable : dc.getPrimaryKeys() ) {
				params += "\"" + classVariable.getLowerIdentifier() + "=\"+" + dc.getLowerIdentifier() + ".get" + classVariable.getUpperIdentifier() + "()+\"&\"+";
			}
			if ( !"".equals( params ) ) {
				params = params.substring( 0, params.length() - 4 );
			}
		}

		output.append( indent( indent ) + "urls += \"<a href=\\\"show.html?\"+" + params + "\"\\\">[view]</a>\";\n" );
		output.append( indent( indent ) + "urls += \"<a href=\\\"edit.html?\"+" + params + "\"\\\">[edit]</a>\";\n" );
		output.append( indent( indent ) + "urls += \"<a href=\\\"delete.html?\"+" + params + "\"\\\">[delete]</a>\";\n" );

		indent -= 4;

		output.append( indent( indent ) + "} else {\n\n" );
		output.append( indent( indent ) + "}\n" );
		output.append( indent( indent ) + "tmp.put( urls );\n" );

		indent -= 4;

		output.append( indent( indent ) + "} else {\n" );

		indent += 4;

		output.append( indent( indent ) + "tmp.put( \"[error column \"+column+\" not supported]\" );\n" );

		indent -= 4;

		output.append( indent( indent ) + "}\n" );

		indent -= 4;

		output.append( indent( indent ) + "}\n" );
		output.append( indent( indent ) + "jsonArray.put( tmp );\n" );

		indent -= 4;

		output.append( indent( indent ) + "}\n" );
		output.append( indent( indent ) + "ob.put( \"aaData\", jsonArray );\n\n" );

		output.append( indent( indent ) + "StringReader reader = new StringReader( ob.toString() );\n" );
		output.append( indent( indent ) + "try {\n" );
		indent += 4;
		output.append( indent( indent ) + "IOUtils.copy( reader, response.getWriter() );\n" );
		indent -= 4;
		output.append( indent( indent ) + "} finally {\n" );
		indent += 4;
		output.append( indent( indent ) + "reader.close();\n" );
		indent -= 4;
		output.append( indent( indent ) + "}\n" );
		indent -= 4;

		output.append( indent( indent ) + "} catch ( Exception e ) {\n" );

		indent += 4;
		output.append( indent( indent ) + "try {\n" );

		indent += 4;
		output.append( indent( indent ) + "log.error( \"error builing datatable json object\", e );\n" );

		output.append( indent( indent ) + "String stackTrace = e.getMessage() + \"<br/>\";\n" );
		output.append( indent( indent ) + "for( StackTraceElement ste : e.getStackTrace() ){\n" );
		indent += 4;
		output.append( indent( indent ) + "stackTrace += ste.toString()+\"<br/>\";\n" );
		indent -= 4;
		output.append( indent( indent ) + "}\n" );

		output.append( indent( indent ) + "JSONObject ob = new JSONObject();\n" );
		output.append( indent( indent ) + "ob.put(\"sEcho\", echo);\n" );
		output.append( indent( indent ) + "ob.put(\"iTotalDisplayRecords\", 0);\n" );
		output.append( indent( indent ) + "ob.put(\"iTotalRecords\", 0);\n" );
		output.append( indent( indent ) + "ob.put(\"error\", e.getMessage());\n" );
		output.append( indent( indent ) + "ob.put(\"stackTrace\", stackTrace);\n" );
		output.append( indent( indent ) + "StringReader reader = new StringReader(ob.toString());\n" );
		output.append( indent( indent ) + "try {\n" );
		indent += 4;
		output.append( indent( indent ) + "IOUtils.copy( reader, response.getWriter() );\n" );
		indent -= 4;
		output.append( indent( indent ) + "} finally {\n" );
		indent += 4;
		output.append( indent( indent ) + "reader.close();\n" );
		indent -= 4;
		output.append( indent( indent ) + "}\n" );
		indent -= 4;
		output.append( indent( indent ) + "} catch ( JSONException je ) {\n" );

		indent += 4;
		output.append( indent( indent ) + "log.error( \"error writing json error to page\", je );\n" );

		indent -= 4;
		output.append( indent( indent ) + "} catch (IOException ioe) {\n" );

		indent += 4;
		output.append( indent( indent ) + "log.error(\"error writing json error to page\", ioe);\n" );

		indent -= 4;
		output.append( indent( indent ) + "}\n" );

		indent -= 4;
		output.append( indent( indent ) + "}\n" );

		indent -= 4;

		output.append( indent( indent ) + "}" );

		return output.toString();
	}

	public String generateAddMethod( DomainClass dc, String accessor, String jspPath, int indent )
	{

		StringBuffer output = new StringBuffer();
		output.append( indent( indent ) + "@RequestMapping(value = \"add.html\", method = RequestMethod.GET)\n" );
		output.append( indent( indent ) + "public ModelAndView add() {\n" );
		output.append( indent( indent * 2 ) + "ModelMap model = new ModelMap();\n" );
		output.append( indent( indent * 2 ) + dc.getIdentifier() + " " + dc.getLowerIdentifier() + " = new " + dc.getIdentifier() + "();\n" );
		output.append( indent( indent * 2 ) + "model.addAttribute(\"" + dc.getLowerIdentifier() + "\"," + dc.getLowerIdentifier() + ");\n" );
		for ( ClassVariable cv : dc.getForeignClassVariables() ) {
			output.append( indent( indent * 2 ) + "model.addAttribute(\"" + cv.getDomainClass().getLowerIdentifier() + "List\"," + cv.getDomainClass().getSchema().getLowerLabel() + "DaoService.get" + cv.getDomainClass().getIdentifier() + interfaceSuffix + "().list());\n" );
		}
		output.append( indent( indent * 2 ) + "return new ModelAndView(\"" + jspPath + "/edit\",model);\n" );

		output.append( indent( indent ) + "}" );
		return output.toString();
	}

	public String generateEditMethod( DomainClass dc, String accessor, String jspPath, int indent )
	{
		List<String[]> compositeKey = new ArrayList<String[]>();
		String sig = "";
		if ( dc.isUsesCompositeKey() )
		{

			for ( Attribute a : dc.getEntity().getPrimaryKeyAttributes() )
			{
				String type = a.getType();
				if ( type.equalsIgnoreCase( "date" ) )
					type = "String";
				sig += "@RequestParam(\"" + a.getLowerLabel() + "\") " + type + " " + a.getLowerLabel() + ", ";
				compositeKey.add( new String[] { a.getType(), a.getLowerLabel() } );
			}
			if ( !"".equals( sig ) ) {
				sig = sig.substring( 0, sig.length() - 2 );
			}
		}
		else
		{
			for ( ClassVariable cv : dc.getPrimaryKeys() )
			{

				sig += "@RequestParam(\"" + cv.getLowerIdentifier() + "\") " + cv.getType() + " " + dc.getLowerIdentifier() + "Id, ";
			}
			if ( !"".equals( sig ) ) {
				sig = sig.substring( 0, sig.length() - 2 );
			}
		}

		StringBuffer output = new StringBuffer();
		output.append( indent( indent ) + "@RequestMapping(value = \"edit.html\", method = RequestMethod.GET)\n" );
		output.append( indent( indent ) + "public ModelAndView edit(" + sig + ") {\n" );

		output.append( indent( indent * 2 ) + "ModelMap model = new ModelMap();\n" );

		/*
		 * set composite key values
		 */
		if ( dc.isUsesCompositeKey() )
		{
			output.append( indent( indent * 2 ) + dc.getIdentifier() + "Id " + dc.getLowerIdentifier() + "Id = new " + dc.getIdentifier() + "Id();\n" );
			for ( String[] starray : compositeKey )
			{
				String setter = "set" + starray[1].substring( 0, 1 ).toUpperCase() + starray[1].substring( 1, starray[1].length() );
				output.append( indent( indent * 2 ) + dc.getLowerIdentifier() + "Id." + setter + "(" + starray[1] + ");\n" );
			}

		}

		for ( ClassVariable cv : dc.getForeignClassVariables() )
		{
			output.append( indent( indent * 2 ) + "model.addAttribute(\"" + cv.getDomainClass().getLowerIdentifier() + "List\"," + cv.getDomainClass().getSchema().getLowerLabel() + "DaoService.get" + cv.getDomainClass().getIdentifier() + interfaceSuffix + "().list());\n" );
		}

		output.append( indent( indent * 2 ) + dc.getIdentifier() + " " + dc.getLowerIdentifier() + " = " + accessor + ".findById(" + dc.getLowerIdentifier() + "Id);\n" );
		output.append( indent( indent * 2 ) + "model.addAttribute(\"" + dc.getLowerIdentifier() + "\"," + dc.getLowerIdentifier() + ");\n" );
		output.append( indent( indent * 2 ) + "return new ModelAndView(\"" + jspPath + "/edit\",model);\n" );

		output.append( indent( indent ) + "}" );
		return output.toString();
	}

	public String generateShowMethod( DomainClass dc, String accessor, String jspPath, int indent )
	{
		List<String[]> compositeKey = new ArrayList<String[]>();
		String sig = "";
		if ( dc.isUsesCompositeKey() )
		{

			for ( Attribute a : dc.getEntity().getPrimaryKeyAttributes() )
			{
				sig += "@RequestParam(\"" + a.getLowerLabel() + "\") " + a.getType() + " " + a.getLowerLabel() + ", ";
				compositeKey.add( new String[] { a.getType(), a.getLowerLabel() } );
			}
			if ( !"".equals( sig ) ) {
				sig = sig.substring( 0, sig.length() - 2 );
			}
		}
		else
		{
			for ( ClassVariable cv : dc.getPrimaryKeys() )
			{

				sig += "@RequestParam(\"" + cv.getLowerIdentifier() + "\") " + cv.getType() + " " + dc.getLowerIdentifier() + "Id, ";
			}
			if ( !"".equals( sig ) ) {
				sig = sig.substring( 0, sig.length() - 2 );
			}
		}

		StringBuffer output = new StringBuffer();
		output.append( indent( indent ) + "@RequestMapping(value = \"show.html\", method = RequestMethod.GET)\n" );
		output.append( indent( indent ) + "public ModelAndView show(" + sig + ") {\n" );

		output.append( indent( indent * 2 ) + "ModelMap model = new ModelMap();\n" );

		/*
		 * set composite key values
		 */
		if ( dc.isUsesCompositeKey() )
		{
			output.append( indent( indent * 2 ) + dc.getIdentifier() + "Id " + dc.getLowerIdentifier() + "Id = new " + dc.getIdentifier() + "Id();\n" );
			for ( String[] starray : compositeKey )
			{
				String setter = "set" + starray[1].substring( 0, 1 ).toUpperCase() + starray[1].substring( 1, starray[1].length() );
				output.append( indent( indent * 2 ) + dc.getLowerIdentifier() + "Id." + setter + "(" + starray[1] + ");\n" );
			}

		}

		output.append( indent( indent * 2 ) + dc.getIdentifier() + " " + dc.getLowerIdentifier() + " = " + accessor + ".findById(" + dc.getLowerIdentifier() + "Id);\n" );
		output.append( indent( indent * 2 ) + "model.addAttribute(\"" + dc.getLowerIdentifier() + "\"," + dc.getLowerIdentifier() + ");\n" );
		output.append( indent( indent * 2 ) + "return new ModelAndView(\"" + jspPath + "/show\",model);\n" );

		output.append( indent( indent ) + "}" );
		return output.toString();
	}

	public String generateSaveMethod( DomainClass dc, String accessor, String jspPath, int indent )
	{
		List<String[]> compositeKey = new ArrayList<String[]>();
		String sig = "";
		if ( dc.isUsesCompositeKey() ) {

			for ( Attribute a : dc.getEntity().getPrimaryKeyAttributes() ) {
				if ( !a.isForeign() ) {
					String type = a.getType();
					if ( type.equalsIgnoreCase( "date" ) ) {
						type = "String";
					}
					sig += "@RequestParam(\"id." + a.getLowerLabel() + "\") " + type + " " + a.getLowerLabel() + ", ";
					compositeKey.add( new String[] { a.getType(), a.getLowerLabel() } );
				}
			}
			if ( !"".equals( sig ) ) {
				sig = sig.substring( 0, sig.length() );
			}

		}

		for ( ClassVariable cv : dc.getForeignClassVariables() ) {
			sig += "@RequestParam(\"" + cv.getDomainClass().getLowerIdentifier() + "." + cv.getDomainClass().getPrimaryKey().getIdentifier() + "\") " + cv.getDomainClass().getPrimaryKey().getType() + " " + cv.getDomainClass().getLowerIdentifier() + "_" + cv.getDomainClass().getPrimaryKey().getIdentifier() + ", ";
			//cv.getDomainClass().getLowerIdentifier()+"List\","+cv.getDomainClass().getSchema().getLowerLabel()+"DaoService.get"+cv.getDomainClass().getIdentifier()+interfaceSuffix+"().list());\n";
		}

		sig += "@ModelAttribute(\"" + dc.getLowerIdentifier() + "\") " + dc.getIdentifier() + " " + dc.getLowerIdentifier() + "";

		StringBuffer output = new StringBuffer();
		output.append( indent( indent ) + "@RequestMapping(value = \"save.html\", method = RequestMethod.POST)\n" );
		output.append( indent( indent ) + "public String save(" + sig + ") {\n" );

		/*
		 * set composite key values
		 */
		if ( dc.isUsesCompositeKey() ) {
			output.append( indent( indent * 2 ) + dc.getIdentifier() + "Id " + dc.getLowerIdentifier() + "Id = new " + dc.getIdentifier() + "Id();\n" );
			for ( String[] starray : compositeKey ) {
				String setter = "set" + starray[1].substring( 0, 1 ).toUpperCase() + starray[1].substring( 1, starray[1].length() );
				output.append( indent( indent * 2 ) + dc.getLowerIdentifier() + "Id." + setter + "(" + starray[1] + ");\n" );
			}
		}

		for ( ClassVariable cv : dc.getForeignClassVariables() ) {
			if ( !cv.isPrimary() && !dc.isUsesCompositeKey() ) {
				String getter = "" + dc.getSchema().getLowerLabel() + "DaoService.get" + cv.getDomainClass().getIdentifier() + interfaceSuffix + "().findById(" + cv.getDomainClass().getLowerIdentifier() + "_" + cv.getDomainClass().getPrimaryKey().getIdentifier() + ")";
				output.append( indent( indent * 2 ) + "" + dc.getLowerIdentifier() + ".set" + cv.getUpperIdentifier() + "(" + getter + ");\n" );
			} else {
				output.append( indent( indent * 2 ) + "" + dc.getLowerIdentifier() + "Id.set" + cv.getAttribute().getUpperLabel() + "(" + cv.getDomainClass().getLowerIdentifier() + "_" + cv.getDomainClass().getPrimaryKey().getIdentifier() + ");\n" );
			}
		}

		if ( dc.isUsesCompositeKey() ) {
			output.append( indent( indent * 2 ) + "" + dc.getLowerIdentifier() + ".setId(" + dc.getLowerIdentifier() + "Id);\n" );
		}

		output.append( indent( indent * 2 ) + accessor + ".saveOrUpdate(" + dc.getLowerIdentifier() + ");\n" );
		output.append( indent( indent * 2 ) + "return \"redirect:" + jspPath + "/list.html\";\n" );

		output.append( indent( indent ) + "}" );
		return output.toString();
	}

	public String generateDeleteMethodPOST( DomainClass dc, String accessor, String jspPath, int indent ) {

		List<String[]> compositeKey = new ArrayList<String[]>();
		String requestParameters = "@RequestParam( value = \"submit\" ) String submitButtonValue";
		if ( dc.isUsesCompositeKey() ) {
			for ( Attribute a : dc.getEntity().getPrimaryKeyAttributes() ) {
				String type = a.getType();
				if ( type.equalsIgnoreCase( "date" ) ) {
					type = "String";
				}
				requestParameters += ", @RequestParam( \"" + a.getLowerLabel() + "\" ) " + type + " " + a.getLowerLabel();
				compositeKey.add( new String[] { a.getType(), a.getLowerLabel() } );
			}
		} else {
			for ( ClassVariable cv : dc.getPrimaryKeys() ) {
				requestParameters += ", @RequestParam( \"" + cv.getLowerIdentifier() + "\" ) " + cv.getType() + " " + dc.getLowerIdentifier() + "Id ";
			}
		}

		StringBuffer output = new StringBuffer();
		output.append( indent( indent ) + "@RequestMapping( value = \"delete.html\", method = RequestMethod.POST )\n" );
		output.append( indent( indent ) + "public String doDelete( " + requestParameters + " ) {\n" );

		output.append( indent( indent * 2 ) + "if ( StringUtils.equalsIgnoreCase( submitButtonValue, \"yes\" ) ) {\n" );

		/*
		 * set composite key values
		 */
		if ( dc.isUsesCompositeKey() ) {
			output.append( indent( indent * 3 ) + dc.getIdentifier() + "Id " + dc.getLowerIdentifier() + "Id = new " + dc.getIdentifier() + "Id();\n" );
			for ( String[] starray : compositeKey ) {
				String setter = "set" + starray[1].substring( 0, 1 ).toUpperCase() + starray[1].substring( 1, starray[1].length() );
				output.append( indent( indent * 3 ) + dc.getLowerIdentifier() + "Id." + setter + "( " + starray[1] + " );\n" );
			}
		}

		output.append( indent( indent * 3 ) + dc.getIdentifier() + " " + dc.getLowerIdentifier() + " = " + accessor + ".findById( " + dc.getLowerIdentifier() + "Id );\n" );
		output.append( indent( indent * 3 ) + accessor + ".delete( " + dc.getLowerIdentifier() + " );\n" );

		output.append( indent( indent * 2 ) + "}\n" );

		output.append( indent( indent * 2 ) + "return \"redirect:" + jspPath + "/list.html\";\n" );

		output.append( indent( indent ) + "}" );
		return output.toString();
	}

	public String generateDeleteMethodGET( DomainClass dc, String accessor, String jspPath, int indent ) {
		List<String[]> compositeKey = new ArrayList<String[]>();
		String sig = "";
		if ( dc.isUsesCompositeKey() ) {
			for ( Attribute a : dc.getEntity().getPrimaryKeyAttributes() ) {
				sig += "@RequestParam(\"" + a.getLowerLabel() + "\") " + a.getType() + " " + a.getLowerLabel() + ", ";
				compositeKey.add( new String[] { a.getType(), a.getLowerLabel() } );
			}
			if ( !"".equals( sig ) ) {
				sig = sig.substring( 0, sig.length() - 2 );
			}
		} else {
			for ( ClassVariable cv : dc.getPrimaryKeys() ) {
				sig += "@RequestParam(\"" + cv.getLowerIdentifier() + "\") " + cv.getType() + " " + dc.getLowerIdentifier() + "Id, ";
			}
			if ( !"".equals( sig ) ) {
				sig = sig.substring( 0, sig.length() - 2 );
			}
		}

		StringBuffer output = new StringBuffer();
		output.append( indent( indent ) + "@RequestMapping( value = \"delete.html\", method = RequestMethod.GET )\n" );
		output.append( indent( indent ) + "public ModelAndView confirmDelete( " + sig + " ) {\n" );
		output.append( indent( indent * 2 ) + "ModelMap model = new ModelMap();\n" );

		/*
		 * set composite key values
		 */
		if ( dc.isUsesCompositeKey() ) {
			output.append( indent( indent * 2 ) + dc.getIdentifier() + "Id " + dc.getLowerIdentifier() + "Id = new " + dc.getIdentifier() + "Id();\n" );
			for ( String[] starray : compositeKey ) {
				String setter = "set" + starray[1].substring( 0, 1 ).toUpperCase() + starray[1].substring( 1, starray[1].length() );
				output.append( indent( indent * 2 ) + dc.getLowerIdentifier() + "Id." + setter + "(" + starray[1] + ");\n" );
			}

		}

		output.append( indent( indent * 2 ) + dc.getIdentifier() + " " + dc.getLowerIdentifier() + " = " + accessor + ".findById(" + dc.getLowerIdentifier() + "Id);\n" );
		output.append( indent( indent * 2 ) + "model.addAttribute( \"" + dc.getLowerIdentifier() + "\", " + dc.getLowerIdentifier() + " );\n" );
		output.append( indent( indent * 2 ) + "return new ModelAndView( \"" + jspPath + "/delete\", model );\n" );
		output.append( indent( indent ) + "}" );

		return output.toString();
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
