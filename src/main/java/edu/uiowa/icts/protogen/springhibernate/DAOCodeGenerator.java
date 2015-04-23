/*
 * @author bkusenda
 * 
 * Generates dao files
 * 
 */
package edu.uiowa.icts.protogen.springhibernate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.uiowa.icts.util.IctsStringUtils;
import edu.uiowa.webapp.Schema;

public class DAOCodeGenerator extends AbstractSpringHibernateCodeGenerator {

	protected static final Log log = LogFactory.getLog( DAOCodeGenerator.class );

	private String interfaceSuffix = "Service";
	private String impleSuffix = "Home";
	private Properties properties;

	public DAOCodeGenerator( SpringHibernateModel model, String pathBase, String packageRoot, Properties properties ) {
		super( model, pathBase, packageRoot );
		this.properties = properties;
		( new File( packageRootPath ) ).mkdirs();
	}

	private void generateDao( DomainClass dc ) throws IOException {

		String daoPackageName = model.getPackageRoot() + ( Boolean.valueOf( properties.getProperty( "include.schema.in.package.name", "true" ) ) ? "." + dc.getSchema().getLowerLabel() : "" ) + ".dao";

		String packagePath = pathBase + "/" + daoPackageName.replaceAll( "\\.", "/" );

		String interfaceName = "" + dc.getIdentifier() + interfaceSuffix;
		String implementationName = "" + dc.getIdentifier() + impleSuffix;

		generateDaoInteface( dc, interfaceName, daoPackageName, packagePath );

		generateDaoImplementation( dc, implementationName, daoPackageName, packagePath, interfaceName );

	}

	private void generateDaoInteface( DomainClass dc, String className, String daoPackageName, String packagePath ) throws IOException {

		List<String> importList = new ArrayList<String>();

		importList.add( "import edu.uiowa.icts.spring.*;" );
		importList.add( "import " + dc.getPackageName() + ".*;" );
		importList.add( "import java.util.ArrayList;" );
		importList.add( "import java.util.List;" );
		importList.add( "import edu.uiowa.icts.util.SortColumn;" );

		BufferedWriter out = createFileInSrcElseTarget( packagePath, className + ".java" );
		/*
		 * Print Package
		 */
		out.write( "package " + daoPackageName + ";\n" );
		lines( out, 1 );

		/*
		 * Print imports
		 */
		for ( String importSt : importList ) {
			out.write( importSt + "\n" );
		}

		SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy HH:mm:ss z", Locale.US );
		sdf.setTimeZone( TimeZone.getDefault() );

		lines( out, 1 );
		out.write( "/**\n" );
		out.write( " * Generated by Protogen \n" );
		out.write( " * @since " + sdf.format( new Date() ) + "\n" );
		out.write( " */\n" );

		/*
		 * Print interface header
		 */
		out.write( "public interface " + className + " extends GenericDaoInterface<" + dc.getIdentifier() + "> {\n\n" );

		tabs( out, 1 );

		if ( dc.isUsesCompositeKey() ) {
			out.write( "public " + dc.getIdentifier() + " findById( " + dc.getIdentifier() + "Id id );\n" );
		} else {
			ClassVariable p_key = dc.getPrimaryKey();
			if ( p_key != null ) {
				out.write( "public " + dc.getIdentifier() + " findById( " + p_key.getType() + " id );\n\n" );
			}
		}

		if ( Boolean.parseBoolean( properties.getProperty( "deobfuscate.column.names", "false" ) ) ) {
			String table = properties.getProperty( "dictionary.table.name" );
			if ( table != null && dc.getEntity().getSqlLabel().equals( table ) ) {
				tabs( out, 1 );
				out.write( "public String getAlternateColumnName( String tableName, String columnName );\n" );
			}
		}

		if ( "SystemSetting".equals( dc.getIdentifier() ) ) {
			tabs( out, 1 );
			out.write( "public String getValue( String name, String defaultVal );\n" );
		} else if ( "Message".equals( dc.getIdentifier() ) ) {
			tabs( out, 1 );
			out.write( "public Integer getCurrentMessageId( String messageName );\n" );
			tabs( out, 1 );
			out.write( "public String getCurrentMessageText( String messageName );\n" );
			tabs( out, 1 );
			out.write( "public Message getCurrentMessage( String messageName );\n" );
		}

		out.write( "}" );
		out.close();
	}

	private void generateDaoImplementation( DomainClass dc, String className, String daoPackageName, String packagePath, String interfaceName ) throws IOException {

		List<String> importList = new ArrayList<String>();

		importList.add( "import edu.uiowa.icts.spring.*;" );
		importList.add( "import " + dc.getPackageName() + ".*;" );

		importList.add( "import org.springframework.stereotype.Repository;" );
		importList.add( "import org.springframework.transaction.annotation.Transactional;" );

		if ( Boolean.parseBoolean( properties.getProperty( "deobfuscate.column.names", "false" ) ) ) {
			String table = properties.getProperty( "dictionary.table.name" );
			if ( table != null && dc.getEntity().getSqlLabel().equalsIgnoreCase( table ) ) {
				importList.add( "import org.hibernate.Criteria;" );
				importList.add( "import org.hibernate.criterion.Restrictions;" );
			}
		}

		if ( "SystemSetting".equalsIgnoreCase( dc.getIdentifier() ) ) {
			importList.add( "import org.hibernate.Criteria;" );
			importList.add( "import org.hibernate.criterion.Restrictions;" );
		}

		if ( "Message".equalsIgnoreCase( dc.getIdentifier() ) ) {
			importList.add( "import org.hibernate.Criteria;" );
			importList.add( "import org.hibernate.criterion.Order;" );
			importList.add( "import org.hibernate.criterion.Restrictions;" );
		}

		// importList.add( "import org.apache.commons.logging.LogFactory;" );
		// importList.add( "import org.apache.commons.logging.Log;" );

		BufferedWriter out = createFileInSrcElseTarget( packagePath, className + ".java" );

		/*
		 * Print Package
		 */
		out.write( "package " + daoPackageName + ";\n" );
		lines( out, 1 );

		/*
		 * Print imports
		 */
		for ( String importSt : importList ) {
			out.write( importSt + "\n" );
		}

		SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy HH:mm:ss z", Locale.US );
		sdf.setTimeZone( TimeZone.getDefault() );

		lines( out, 1 );
		out.write( "/**\n" );
		out.write( " * Generated by Protogen \n" );
		out.write( " * @since " + sdf.format( new Date() ) + "\n" );
		out.write( " */\n" );

		/*
		 * Print class header
		 */
		out.write( "@Transactional\n" );
		out.write( "@Repository( \"" + daoPackageName.replaceAll( "\\.", "_" ) + "_" + className + "\" )\n" );
		out.write( "public class " + className + " extends GenericDao<" + dc.getIdentifier() + "> implements " + interfaceName + " {\n\n" );

		// tabs( out, 1 );
		// out.write( "private static final Log log = LogFactory.getLog( " + className + ".class );\n\n" );

		tabs( out, 1 );
		out.write( "public " + className + "() {\n" );
		tabs( out, 2 );
		out.write( "setDomainName( \"" + dc.getPackageName() + "." + dc.getIdentifier() + "\" );\n" );
		tabs( out, 1 );
		out.write( "}\n\n" );

		tabs( out, 1 );
		if ( dc.isUsesCompositeKey() ) {
			out.write( "public " + dc.getIdentifier() + " findById( " + dc.getIdentifier() + "Id id ) {\n" );
		} else {
			ClassVariable p_key = dc.getPrimaryKey();
			if ( p_key != null ) {
				out.write( "public " + dc.getIdentifier() + " findById( " + p_key.getType() + " id ) {\n" );
			} else {
				log.error( "There is no primary key for " + dc.getIdentifier() );
				out.write( "//There is no primary key for " + dc.getIdentifier() );
				out.write( "\n\n}\n" );
				out.close();
				return;
			}
		}

		tabs( out, 2 );
		out.write( "return (" + dc.getIdentifier() + ") this.sessionFactory.getCurrentSession().get( " + dc.getIdentifier() + ".class, id );\n" );
		tabs( out, 1 );
		out.write( "}\n\n" );

		if ( Boolean.parseBoolean( properties.getProperty( "deobfuscate.column.names", "false" ) ) ) {

			IctsStringUtils stringUtils = new IctsStringUtils();
			String table = properties.getProperty( "dictionary.table.name" );

			if ( table != null && dc.getEntity().getSqlLabel().equals( table ) ) {
				tabs( out, 1 );
				out.write( "public String getAlternateColumnName( String tableName, String columnName ){\n" );
				tabs( out, 2 );
				out.write( "Criteria c = this.sessionFactory.getCurrentSession().createCriteria( " + dc.getIdentifier() + ".class );\n" );
				tabs( out, 2 );
				out.write( "c.add( Restrictions.eq( \"" + stringUtils.relabel( (String) properties.get( "dictionary.table.columnname" ), false ) + "\", tableName ) );\n" );
				tabs( out, 2 );
				out.write( "c.add( Restrictions.eq( \"" + stringUtils.relabel( (String) properties.get( "dictionary.column.columnname" ), false ) + "\", columnName ) );\n" );
				tabs( out, 2 );
				out.write( "c.setMaxResults( 1 );\n" );
				tabs( out, 2 );
				out.write( dc.getIdentifier() + " dict = (" + dc.getIdentifier() + ") c.uniqueResult();\n" );
				tabs( out, 2 );
				out.write( "if( dict != null ){\n" );
				tabs( out, 3 );
				out.write( "return dict.get" + stringUtils.relabel( (String) properties.get( "dictionary.deobfuscated.columnname" ), true ) + "();\n" );
				tabs( out, 2 );
				out.write( "}\n" );
				tabs( out, 2 );
				out.write( "return null;\n" );
				tabs( out, 1 );
				out.write( "}\n\n" );
			}
		}

		if ( "SystemSetting".equals( dc.getIdentifier() ) ) {
			tabs( out, 1 );
			out.write( "public String getValue(String name, String defaultVal){\n" );
			tabs( out, 2 );
			out.write( "Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(SystemSetting.class);\n" );
			tabs( out, 2 );
			out.write( "criteria.add(Restrictions.eq(\"name\", name));\n" );
			tabs( out, 2 );
			out.write( "SystemSetting set = (SystemSetting) criteria.uniqueResult();\n" );
			tabs( out, 2 );
			out.write( "if (set != null) {\n" );
			tabs( out, 3 );
			out.write( "return set.getValue();\n" );
			tabs( out, 2 );
			out.write( "} else {\n" );
			tabs( out, 3 );
			out.write( "return defaultVal;\n" );
			tabs( out, 2 );
			out.write( "}\n" );
			tabs( out, 1 );
			out.write( "}\n" );
		} else if ( "Message".equals( dc.getIdentifier() ) ) {
			tabs( out, 1 );
			out.write( "@Override\n" );
			tabs( out, 1 );
			out.write( "public Message getCurrentMessage(String messageName) {\n" );
			tabs( out, 2 );
			out.write( "Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(Message.class);\n" );
			tabs( out, 2 );
			out.write( "criteria.add(Restrictions.eq(\"messageName\", messageName));\n" );
			tabs( out, 2 );
			out.write( "criteria.addOrder(Order.desc(\"version\"));\n" );
			tabs( out, 2 );
			out.write( "criteria.setMaxResults(1);\n" );
			tabs( out, 2 );
			out.write( "return (Message) criteria.uniqueResult();\n" );
			tabs( out, 1 );
			out.write( "}\n\n" );

			tabs( out, 1 );
			out.write( "@Override\n" );
			tabs( out, 1 );
			out.write( "public Integer getCurrentMessageId(String messageName) {\n" );
			tabs( out, 2 );
			out.write( "Integer id = null;\n" );
			tabs( out, 2 );
			out.write( "Message m = getCurrentMessage(messageName);\n" );
			tabs( out, 2 );
			out.write( "if( m != null ){\n" );
			tabs( out, 3 );
			out.write( "id = m.get" + dc.getPrimaryKey().getUpperIdentifier() + "();\n" );
			tabs( out, 2 );
			out.write( "}\n" );
			tabs( out, 2 );
			out.write( "return id;\n" );
			tabs( out, 1 );
			out.write( "}\n\n" );

			tabs( out, 1 );
			out.write( "@Override\n" );
			tabs( out, 1 );
			out.write( "public String getCurrentMessageText(String messageName) {\n" );
			tabs( out, 2 );
			out.write( "String text = null;\n" );
			tabs( out, 2 );
			out.write( "Message m = getCurrentMessage(messageName);\n" );
			tabs( out, 2 );
			out.write( "if( m != null ){\n" );
			tabs( out, 3 );
			out.write( "text = m.getMessageText();\n" );
			tabs( out, 2 );
			out.write( "}\n" );
			tabs( out, 2 );
			out.write( "return text;\n" );
			tabs( out, 1 );
			out.write( "}\n" );
		}
		out.write( "}" );
		out.close();
	}

	/*
	 * generates all the primary dao inteface class
	 * 
	 * 
	 */
	private void generateDaoMasterServiceFiles() throws IOException {
		for ( Schema schema : model.getSchemaMap().keySet() ) {
			List<DomainClass> domainClassList = model.getSchemaMap().get( schema );
			if ( domainClassList != null && domainClassList.isEmpty() == false ) {
				String daoPackageName = model.getPackageRoot() + ( Boolean.valueOf( properties.getProperty( "include.schema.in.package.name", "true" ) ) ? "." + schema.getLowerLabel() : "" ) + ".dao";
				String packagePath = pathBase + "/" + daoPackageName.replaceAll( "\\.", "/" );
				String className = properties.getProperty( schema.getUpperLabel().toLowerCase() + ".master.dao.service.name" );
				if ( className == null || "".equals( className.trim() ) ) {
					className = schema.getUpperLabel() + "DaoService";
				}
				generateDaoMasterService( model.getSchemaMap().get( schema ), className, daoPackageName, packagePath );
			}
		}
	}

	/*
	 * generates a primary dao inteface class
	 * 
	 * 
	 */
	private void generateDaoMasterService( List<DomainClass> domainClassList, String className, String daoPackageName, String packagePath ) throws IOException {

		List<String> importList = new ArrayList<String>();
		importList.add( "import org.springframework.beans.factory.annotation.Autowired;" );
		importList.add( "import org.springframework.stereotype.Component;" );

		BufferedWriter out = createFileInSrcElseTarget( packagePath, className + ".java" );

		/*
		 * Print Package
		 */
		out.write( "package " + daoPackageName + ";\n" );
		lines( out, 1 );

		/*
		 * Print imports
		 */
		for ( String importSt : importList ) {
			out.write( importSt + "\n" );
		}

		SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy HH:mm:ss z", Locale.US );
		sdf.setTimeZone( TimeZone.getDefault() );

		lines( out, 1 );
		out.write( "/**\n" );
		out.write( " * Generated by Protogen\n" );
		out.write( " * @since " + sdf.format( new Date() ) + "\n" );
		out.write( " */\n" );
		out.write( "@Component\n" );
		out.write( "public class " + className + " {\n" );

		for ( DomainClass dc : domainClassList ) {
			String type = "" + dc.getIdentifier() + interfaceSuffix;
			String variableName = dc.getLowerIdentifier() + interfaceSuffix;
			lines( out, 1 );
			//	tabs( out, 1 );
			//	out.write( "/*********** " + variableName + " ****************/\n" );
			tabs( out, 1 );
			out.write( "@Autowired" );
			lines( out, 1 );
			tabs( out, 1 );
			out.write( "private " + type + " " + variableName + ";\n" );
			lines( out, 1 );
			out.write( createGetter( type, variableName, 4 ) );
			//			lines( out, 2 );
			//		tabs( out, 1 );
			//		out.write( createSetter( type, variableName, 4 ) );
			//		lines( out, 1 );

		}

		lines( out, 1 );
		out.write( "}\n" );
		out.close();
	}

	/*
	 * Public Function to generate java domain code
	 * 
	 */
	public void generate() throws IOException {
		log.debug( "Generating dao classes" );

		for ( DomainClass dc : model.getDomainClassList() ) {
			generateDao( dc );
		}

		generateDaoMasterServiceFiles();

	}

}
