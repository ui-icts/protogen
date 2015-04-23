package edu.uiowa.icts.protogen.springhibernate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.plexus.util.StringUtils;

import edu.uiowa.icts.protogen.springhibernate.ClassVariable.AttributeType;
import edu.uiowa.icts.protogen.springhibernate.ClassVariable.RelationshipType;
import edu.uiowa.icts.protogen.springhibernate.velocity.VelocityEditJspGenerator;
import edu.uiowa.webapp.Attribute;

/**
 * @since May 12, 2008
 */
public class JSPCodeGenerator extends AbstractSpringHibernateCodeGenerator {

	private static final Log log = LogFactory.getLog( JSPCodeGenerator.class );
	public String jspRoot;
	private Properties properties;

	/**
	 * @param model
	 * @param pathBase
	 * @param packageRoot
	 */
	public JSPCodeGenerator( SpringHibernateModel model, String pathBase, String packageRoot, Properties properties ) {
		super( model, pathBase, packageRoot );
		this.properties = properties;
		this.jspRoot = pathBase;
	}

	public void generate() throws IOException {
		generateAllJSP( model.getDomainClassList() );
	}

	public void generateAllJSP( List<DomainClass> ecList ) throws IOException {
		generateMenu( ecList );
		Iterator<DomainClass> ecIter = ecList.iterator();
		while ( ecIter.hasNext() ) {
			DomainClass ec = ecIter.next();
			generateShowJSP( ec );
			generateListJSP( ec );
			generateListAltJSP( ec );
			generateEditJSP( ec );
			generateDeleteJSP( ec );
		}
	}

	private void generateDeleteJSP( DomainClass ec ) throws IOException {

		int indent = 0;

		String output = spaces( indent ) + "<%@ include file=\"/WEB-INF/include.jsp\"  %>";
		output += lines( 2 );

		boolean deOb = Boolean.parseBoolean( properties.getProperty( "deobfuscate.column.names", "false" ) );

		Iterator<ClassVariable> cvIter = ec.getPrimaryKeys().iterator();
		output += "<h2>Delete " + ec.getIdentifier() + "</h2>";
		output += lines( 1 );

		output += spaces( indent ) + "";
		lines( 1 );

		cvIter = ec.listAllIter();
		output += lines( 1 );

		output += "<form method=\"post\" action=\"delete" + properties.getProperty( "controller.request.mapping.extension", "" ) + "\">";
		output += lines( 1 );
		indent += 4;

		output += spaces( indent ) + "<fieldset>";
		output += lines( 1 );
		indent += 4;

		output += spaces( indent ) + "<legend>Are you sure you want to delete this " + ec.getIdentifier() + "?</legend>";
		output += lines( 1 );

		output += spaces( indent ) + "<table class=\"table table-bordered table-hover\">";
		output += lines( 1 );
		indent += 4;

		output += spaces( indent ) + "<thead>";
		output += lines( 1 );
		indent += 4;

		output += spaces( indent ) + "<tr>";
		indent += 4;

		while ( cvIter.hasNext() ) {
			ClassVariable cv = cvIter.next();
			String th_label = cv.getUpperIdentifier();
			if ( deOb && cv.getAttribute() != null ) {
				th_label = "${ " + ec.getSchema().getLowerLabel() + ":deobfuscateColumn ( '" + ec.getTableName() + "', '" + cv.getAttribute().getSqlLabel() + "') }";
			}
			output += lines( 1 );
			output += spaces( indent ) + "<th>" + th_label + "</th>";
		}

		output += lines( 1 );
		indent -= 4;
		output += spaces( indent ) + "</tr>";

		output += lines( 1 );
		indent -= 4;
		output += spaces( indent ) + "</thead>";

		output += lines( 1 );
		output += spaces( indent ) + "<tbody>";
		indent += 4;

		output += lines( 1 );
		output += spaces( indent ) + "<tr>";
		indent += 4;

		cvIter = ec.listAllIter();
		while ( cvIter.hasNext() ) {

			ClassVariable cv = cvIter.next();

			output += lines( 1 );
			output += spaces( indent ) + "<td>";
			indent += 4;
			output += lines( 1 );

			if ( cv.getAttribType() == AttributeType.CHILD && cv.getAttribute().getEntity().getDomainClass() != null ) {
				if ( cv.getAttribute().getEntity().getDomainClass().isUsesCompositeKey() ) {
					output += spaces( indent ) + "<em>not implemented</em>";
				} else {
					output += spaces( indent ) + "<ul>";
					output += lines( 1 );
					indent += 4;

					output += spaces( indent ) + "<c:forEach items=\"${ " + ec.getLowerIdentifier() + "." + cv.getIdentifier() + " }\" var=\"item\" varStatus=\"itemStatus\">";
					output += lines( 1 );
					indent += 4;

					output += spaces( indent ) + "<li>${ item." + cv.getAttribute().getEntity().getDomainClass().getPrimaryKey().getLowerIdentifier() + " }</li>";
					output += lines( 1 );
					indent -= 4;

					output += spaces( indent ) + "</c:forEach>";
					output += lines( 1 );
					indent -= 4;

					output += spaces( indent ) + "</ul>";
				}
			} else if ( cv.isPrimary() && cv.getDomainClass().isUsesCompositeKey() ) {
				String label = "";
				for ( Attribute a : ec.getEntity().getPrimaryKeyAttributes() ) {
					String pk_label = a.getLowerLabel();
					if ( deOb ) {
						pk_label = "${ " + ec.getSchema().getLowerLabel() + ":deobfuscateColumn ( '" + ec.getTableName() + "', '" + a.getSqlLabel() + "') }";
					}
					label += "( " + pk_label + ", ${ " + ec.getLowerIdentifier() + ".id." + a.getLowerLabel() + " } )";
				}
				output += spaces( indent ) + label;
			} else {
				output += spaces( indent ) + "${ " + ec.getLowerIdentifier() + "." + cv.getIdentifier() + " }";
			}

			output += lines( 1 );
			indent -= 4;
			output += spaces( indent ) + "</td>";

		}

		output += lines( 1 );
		indent -= 4;
		output += spaces( indent ) + "</tr>";

		output += lines( 1 );
		indent -= 4;
		output += spaces( indent ) + "</tbody>";

		output += lines( 1 );
		indent -= 4;
		output += spaces( indent ) + "</table>";

		output += lines( 2 );
		output += spaces( indent ) + "<input type=\"submit\" name=\"submit\" value=\"Yes\" class=\"btn btn-danger\" />";
		output += lines( 1 );
		output += spaces( indent ) + "<input type=\"submit\" name=\"submit\" value=\"No\" class=\"btn btn-default\" />";

		output += lines( 1 );

		cvIter = ec.listAllIter();
		while ( cvIter.hasNext() ) {
			ClassVariable cv = cvIter.next();
			if ( ec.isUsesCompositeKey() && cv.isPrimary() ) {
				for ( Attribute a : ec.getEntity().getPrimaryKeyAttributes() ) {
					output += lines( 1 );
					output += spaces( indent ) + "<input type=\"hidden\" name=\"" + a.getLowerLabel() + "\" value=\"${ " + ec.getLowerIdentifier() + ".id." + a.getLowerLabel() + " }\" />";
				}
			} else if ( cv.isPrimary() ) {
				output += lines( 1 );
				output += spaces( indent ) + "<input type=\"hidden\" name=\"" + cv.getIdentifier() + "\" value=\"${ " + ec.getLowerIdentifier() + "." + cv.getIdentifier() + " }\" />";
			}
		}

		output += lines( 2 );
		indent -= 4;
		output += spaces( indent ) + "</fieldset>";

		output += lines( 1 );
		indent -= 4;
		output += spaces( indent ) + "</form>";

		String directory = getJspDirectory( ec );

		BufferedWriter out = jspWriter( directory, "delete.jsp" );
		try {
			out.write( output );
			out.flush();
		} finally {
			out.close();
		}
		log.debug( ".........delete done" );
	}

	private void generateShowJSP( DomainClass ec ) throws IOException {

		int indent = 0;

		String output = spaces( indent ) + "<%@ include file=\"/WEB-INF/include.jsp\"  %>";
		output += lines( 2 );

		boolean deOb = Boolean.parseBoolean( properties.getProperty( "deobfuscate.column.names", "false" ) );

		Iterator<ClassVariable> cvIter = ec.getPrimaryKeys().iterator();
		output += "<h2>" + ec.getIdentifier() + "</h2>";
		output += lines( 1 );
		while ( cvIter.hasNext() ) {
			ClassVariable cv = cvIter.next();
			log.debug( "ClassVariable:" + cv.getIdentifier() );
			if ( ec.isUsesCompositeKey() && cv.isPrimary() ) {
				output += "<h2>";
				for ( Attribute a : ec.getEntity().getPrimaryKeyAttributes() ) {
					output += "${" + cv.getLowerIdentifier() + "." + a.getLowerLabel() + "}";
				}
				output += "</h2>";
			} else {
				output += "<h2> ${" + ec.getLowerIdentifier() + "." + cv.getIdentifier() + "} </h2>";
			}
			output += lines( 1 );
		}

		output += spaces( indent ) + "";
		lines( 1 );

		cvIter = ec.listAllIter();
		output += lines( 1 );

		output += spaces( indent ) + "<table class=\"table table-bordered table-hover\">";
		indent += 4;

		while ( cvIter.hasNext() ) {

			ClassVariable cv = cvIter.next();

			output += lines( 1 );
			output += spaces( indent ) + "<tr>";
			indent += 4;

			String th_label = cv.getUpperIdentifier();
			if ( deOb && cv.getAttribute() != null ) {
				th_label = "${ " + ec.getSchema().getLowerLabel() + ":deobfuscateColumn ( '" + ec.getTableName() + "', '" + cv.getAttribute().getSqlLabel() + "') }";
			}

			log.debug( "-ClassVariable:" + cv.getIdentifier() );
			output += lines( 1 );
			output += spaces( indent ) + "<th>" + th_label + "</th>";
			output += lines( 1 );

			String pkString = "";

			output += spaces( indent ) + "<td>";
			output += lines( 1 );
			indent += 4;

			if ( cv.getAttribType() == AttributeType.CHILD && cv.getAttribute().getEntity().getDomainClass() != null ) {
				log.debug( "isChild" );
				Iterator<ClassVariable> pkIter = cv.getAttribute().getEntity().getDomainClass().getPrimaryKeys().iterator();
				while ( pkIter.hasNext() ) {
					ClassVariable pk = pkIter.next();
					pkString += pk.getIdentifier() + "=${item." + pk.getIdentifier() + "}";
					while ( pkIter.hasNext() ) {
						pkString += "&";
					}
				}

				if ( cv.getAttribute().getEntity().getDomainClass().isUsesCompositeKey() ) {
					output += spaces( indent ) + "not implemented<br/><br/>";
				} else if ( cv.getAttribute().getEntity().getDomainClass().getLowerIdentifier() == null ) {
					output += spaces( indent ) + "cv.getAttribute().getEntity().getDomainClass().getLowerIdentifier() was null<br/><br/>";
				} else {
					output += spaces( indent ) + "<ul>";
					output += lines( 1 );
					indent += 4;

					output += spaces( indent ) + "<c:forEach items=\"${" + ec.getLowerIdentifier() + "." + cv.getIdentifier() + "}\" var=\"item\" varStatus=\"itemStatus\" >";
					output += lines( 1 );
					indent += 4;

					output += spaces( indent ) + "<li><a href=\"../" + cv.getAttribute().getEntity().getDomainClass().getLowerIdentifier().toLowerCase() + "/edit" + properties.getProperty( "controller.request.mapping.extension", "" ) + "?" + pkString + "\" > ${item." + cv.getAttribute().getEntity().getDomainClass().getPrimaryKey().getLowerIdentifier() + "}</a></li>";
					output += lines( 1 );
					indent -= 4;

					output += spaces( indent ) + "</c:forEach>";
					output += lines( 1 );
					indent -= 4;

					output += spaces( indent ) + "</ul>";
				}
			} else if ( cv.isPrimary() && cv.getDomainClass().isUsesCompositeKey() ) {
				String params = "";
				String label = "";
				for ( Attribute a : ec.getEntity().getPrimaryKeyAttributes() ) {
					String pk_label = a.getLowerLabel();
					if ( deOb ) {
						pk_label = "${ " + ec.getSchema().getLowerLabel() + ":deobfuscateColumn ( '" + ec.getTableName() + "', '" + a.getSqlLabel() + "') }";
					}
					label += "(" + pk_label + ", ${ " + ec.getLowerIdentifier() + ".id." + a.getLowerLabel() + " })";
					params += a.getLowerLabel() + "=${ " + ec.getLowerIdentifier() + ".id." + a.getLowerLabel() + " }&";
				}
				params = params.substring( 0, params.length() - 1 );
				output += spaces( indent ) + label;
			} else {
				output += spaces( indent ) + "${" + ec.getLowerIdentifier() + "." + cv.getIdentifier() + "}";
			}

			indent -= 4;
			output += lines( 1 );
			output += spaces( indent ) + "</td>";

			indent -= 4;
			output += lines( 1 );
			output += spaces( indent ) + "</tr>";
		}

		output += lines( 1 );
		indent -= 4;
		output += spaces( indent ) + "</table>";

		String directory = getJspDirectory( ec );

		BufferedWriter out = jspWriter( directory, "show.jsp" );
		try {
			out.write( output );
			out.flush();
		} finally {
			out.close();
		}
		log.debug( ".........show done" );

	}

	private void generateListJSP( DomainClass ec ) throws IOException {

		String nameLabel = "sName";
		String titleLabel = "sTitle";
		String classLabel = "sClass";
		String sortableLabel = "bSortable";
		String searchableLabel = "bSearchable";
		String individualSearchingLabel = "includeSearches";

		String datatableUrl = "datatable" + properties.getProperty( "controller.request.mapping.extension", "" ) + "";

		if ( StringUtils.equals( properties.getProperty( "datatables.generation", "1" ), "2" ) ) {
			nameLabel = "name";
			titleLabel = "title";
			classLabel = "class";
			sortableLabel = "sortable";
			searchableLabel = "searchable";
			individualSearchingLabel = "individualSearching";
		}

		if ( Boolean.valueOf( properties.getProperty( "include.schema.in.request.mapping", "true" ) ) ) {
			datatableUrl = "/" + ec.getSchema().getUnqualifiedLabel() + "/" + ec.getIdentifier().toLowerCase() + "/" + datatableUrl;
		} else {
			datatableUrl = "/" + ec.getIdentifier().toLowerCase() + "/" + datatableUrl;
		}

		log.debug( "GeneratingListJSP:" + ec.getIdentifier() );
		log.debug( "...." + ec.getIdentifier() );

		int indent = 0;

		String output = spaces( indent ) + "<%@ include file=\"/WEB-INF/include.jsp\"  %>";
		output += lines( 2 );

		output += "<h2>" + ec.getIdentifier() + " List</h2>";
		output += lines( 1 );

		output += lines( 1 );
		output += spaces( indent ) + "<a href=\"add" + properties.getProperty( "controller.request.mapping.extension", "" ) + "\" class=\"btn btn-default\">Add</a>";
		output += lines( 2 );
		output += spaces( indent ) + "<div id=\"error_div\" class=\"alert alert-error\" style=\"display: none;\">";
		output += lines( 1 );
		indent += 4;
		output += spaces( indent ) + "<%-- div for showing errors, see messager.js.showMessage --%>";
		output += lines( 1 );
		indent -= 4;
		output += spaces( indent ) + "</div>";
		output += lines( 2 );

		output += spaces( indent ) + "<table id=\"" + ec.getIdentifier().toLowerCase() + "Table\" class=\"table table-bordered table-striped table-hover\">";
		output += lines( 1 );
		indent += 4;
		output += spaces( indent ) + "<%-- table filled by setDataTable call below --%>";
		output += lines( 1 );
		indent -= 4;
		output += spaces( indent ) + "</table>";
		output += lines( 2 );

		output += spaces( indent ) + "<c:url value=\"" + datatableUrl + "\" var=\"datatableUrl\">";
		indent += 4;
		output += lines( 1 );

		output += spaces( indent ) + "<c:param name=\"display\" value=\"list\" />";
		indent -= 4;
		output += lines( 1 );

		output += spaces( indent ) + "</c:url>";
		output += lines( 1 );

		output += spaces( indent ) + "<script type=\"text/javascript\">";
		output += lines( 1 );
		indent += 4;

		output += spaces( indent ) + "var columns = [];";
		output += lines( 1 );

		output += spaces( indent ) + "columns.push({ \"" + nameLabel + "\": \"urls\", \"" + titleLabel + "\":\"\", \"" + classLabel + "\":\"\", \"" + sortableLabel + "\":false, \"" + searchableLabel + "\": false });";
		output += lines( 1 );
		boolean deOb = Boolean.parseBoolean( properties.getProperty( "deobfuscate.column.names", "false" ) );

		Iterator<ClassVariable> cvIter = ec.listAllIter();
		while ( cvIter.hasNext() ) {
			ClassVariable cv = cvIter.next();
			// by default, don't display primary keys
			if ( cv.isPrimary() ) { //&& ec.isUsesCompositeKey() ) {
				//				for ( Attribute a : ec.getEntity().getPrimaryKeyAttributes() ) {
				//					output += spaces( indent ) + "columns.push({ \"" + nameLabel + "\": \"id." + a.getLowerLabel() + "\", \"" + titleLabel + "\":\"" + ( deOb ? " ${ " + ec.getSchema().getLowerLabel() + ":deobfuscateColumn ( '" + ec.getTableName() + "', '" + a.getSqlLabel() + "') } " : a.getLabel() ) + "\",	\"" + classLabel + "\":\"\", \"" + sortableLabel + "\":false, \"" + searchableLabel + "\": false });";
				//					output += lines( 1 );
				//				}
			} else {
				if ( RelationshipType.NONE == cv.getRelationshipType() ) {
					output += spaces( indent ) + "columns.push({ \"" + nameLabel + "\": \"" + cv.getLowerIdentifier() + "\", \"" + titleLabel + "\":\"" + ( deOb ? " ${ " + ec.getSchema().getLowerLabel() + ":deobfuscateColumn ( '" + ec.getTableName() + "', '" + cv.getAttribute().getSqlLabel() + "') } " : cv.getUpperIdentifier() ) + "\",	\"" + classLabel + "\":\"\", \"" + sortableLabel + "\":true, \"" + searchableLabel + "\": true });";
					output += lines( 1 );
				} else {
					output += spaces( indent ) + "columns.push({ \"" + nameLabel + "\": \"" + cv.getLowerIdentifier() + "\", \"" + titleLabel + "\":\"" + ( deOb ? " ${ " + ec.getSchema().getLowerLabel() + ":deobfuscateColumn ( '" + ec.getTableName() + "', '" + cv.getAttribute().getSqlLabel() + "') } " : cv.getUpperIdentifier() ) + "\",	\"" + classLabel + "\":\"\", \"" + sortableLabel + "\":false, \"" + searchableLabel + "\": false });";
					output += lines( 1 );
				}
			}
		}

		output += spaces( indent ) + "var table = setDataTable({";
		indent += 4;
		output += lines( 1 );
		output += spaces( indent ) + "id : '" + ec.getIdentifier().toLowerCase() + "Table',";
		output += lines( 1 );
		output += spaces( indent ) + "url : '${ datatableUrl }',";
		output += lines( 1 );
		output += spaces( indent ) + "columns : columns,";
		output += lines( 1 );
		output += spaces( indent ) + individualSearchingLabel + " : true ";
		indent -= 4;
		output += lines( 1 );
		output += spaces( indent ) + "});";
		output += lines( 1 );
		indent -= 4;
		output += spaces( indent ) + "</script>";

		String directory = getJspDirectory( ec );

		BufferedWriter out = jspWriter( directory, "list.jsp" );
		try {
			out.write( output );
			out.flush();
		} finally {
			out.close();
		}
		log.debug( ".........list done" );
	}

	private void generateListAltJSP( DomainClass ec ) throws IOException {

		boolean deOb = Boolean.parseBoolean( properties.getProperty( "deobfuscate.column.names", "false" ) );

		log.debug( "GeneratingListJSP:" + ec.getIdentifier() );
		log.debug( "...." + ec.getIdentifier() );

		int indent = 0;

		String output = spaces( indent ) + "<%@ include file=\"/WEB-INF/include.jsp\"  %>";
		output += lines( 2 );

		output += "<h2>" + ec.getIdentifier() + " List</h2>";
		output += lines( 1 );

		output += lines( 1 );
		output += spaces( indent ) + "<a href=\"add" + properties.getProperty( "controller.request.mapping.extension", "" ) + "\" class=\"btn btn-default\">Add</a>";
		output += lines( 2 );
		output += spaces( indent ) + "<div id=\"error_div\" class=\"alert alert-error\" style=\"display: none;\">";
		output += lines( 1 );
		indent += 4;
		output += spaces( indent ) + "<%-- div for showing errors, see messager.js.showMessage --%>";
		output += lines( 1 );
		indent -= 4;
		output += spaces( indent ) + "</div>";
		output += lines( 2 );

		output += spaces( indent ) + "<table class=\"table table-bordered table-striped table-hover table-datatable\">";
		output += lines( 1 );
		indent += 4;

		output += spaces( indent ) + "<thead>";
		output += lines( 1 );
		indent += 4;

		Iterator<ClassVariable> cvIter = ec.listAllIter();
		output += spaces( indent ) + "<tr>";
		output += lines( 1 );
		indent += 4;
		while ( cvIter.hasNext() ) {
			ClassVariable cv = cvIter.next();
			if ( deOb && cv.getAttribute() != null ) {
				output += spaces( indent ) + "<th>${ " + ec.getSchema().getLowerLabel() + ":deobfuscateColumn ( '" + ec.getTableName() + "', '" + cv.getAttribute().getSqlLabel() + "') }</th>";
			} else {
				output += spaces( indent ) + "<th>" + cv.getUpperIdentifier() + "</th>";
			}
			output += lines( 1 );
		}
		output += spaces( indent ) + "<th></th>";
		output += lines( 1 );
		indent -= 4;

		output += spaces( indent ) + "</tr>";
		output += lines( 1 );
		indent -= 4;

		output += spaces( indent ) + "</thead>";
		output += lines( 1 );

		cvIter = ec.listAllIter();
		output += spaces( indent ) + "<tbody>";

		output += lines( 1 );
		indent += 4;

		output += spaces( indent ) + "<c:forEach items=\"${" + ec.getLowerIdentifier() + "List}\" var=\"" + ec.getLowerIdentifier() + "\"  varStatus=\"status\">";
		output += lines( 1 );
		indent += 4;

		output += spaces( indent ) + "<tr>";
		output += lines( 1 );
		indent += 4;

		String links = "";
		while ( cvIter.hasNext() ) {

			ClassVariable cv = cvIter.next();
			log.debug( cv.getLowerIdentifier() + " : " + cv.getRelationshipType() + " : " + cv.getAttribType() );

			if ( cv.isPrimary() ) {
				if ( ec.isUsesCompositeKey() && cv.isPrimary() ) {

					String params = "";
					String label = "";
					for ( Attribute a : ec.getEntity().getPrimaryKeyAttributes() ) {
						String pk_label = a.getLowerLabel();
						if ( deOb ) {
							pk_label = "${ " + ec.getSchema().getLowerLabel() + ":deobfuscateColumn ( '" + ec.getTableName() + "', '" + a.getSqlLabel() + "') }";
						}
						label += "(" + pk_label + ", ${ " + ec.getLowerIdentifier() + ".id." + a.getLowerLabel() + " })";
						params += a.getLowerLabel() + "=${ " + ec.getLowerIdentifier() + ".id." + a.getLowerLabel() + " }&";
					}

					params = params.substring( 0, params.length() - 1 );

					output += spaces( indent ) + "<td>";
					output += lines( 1 );
					indent += 4;
					output += spaces( indent ) + "<a href=\"edit" + properties.getProperty( "controller.request.mapping.extension", "" ) + "?" + params + "\">" + label + "</a>";
					indent -= 4;
					output += lines( 1 );
					output += spaces( indent ) + "</td>";

					links += spaces( indent ) + "<td>";
					indent += 4;
					links += lines( 1 );
					links += spaces( indent ) + "<a href=\"edit" + properties.getProperty( "controller.request.mapping.extension", "" ) + "?" + params + "\">edit</a> ";
					links += lines( 1 );
					links += spaces( indent ) + "<a href=\"show" + properties.getProperty( "controller.request.mapping.extension", "" ) + "?" + params + "\">view</a>";
					links += lines( 1 );
					links += spaces( indent ) + "<a href=\"delete" + properties.getProperty( "controller.request.mapping.extension", "" ) + "?" + params + "\">delete</a>";
					links += lines( 1 );
					indent -= 4;
					links += spaces( indent ) + "</td>";
				} else {
					links += spaces( indent ) + "<td>";
					indent += 4;
					links += lines( 1 );
					links += spaces( indent ) + "<a href=\"edit" + properties.getProperty( "controller.request.mapping.extension", "" ) + "?" + cv.getIdentifier() + "=${" + ec.getLowerIdentifier() + "." + cv.getIdentifier() + "}\">edit</a> ";
					links += lines( 1 );
					links += spaces( indent ) + "<a href=\"show" + properties.getProperty( "controller.request.mapping.extension", "" ) + "?" + cv.getIdentifier() + "=${" + ec.getLowerIdentifier() + "." + cv.getIdentifier() + "}\">view</a>";
					links += lines( 1 );
					links += spaces( indent ) + "<a href=\"delete" + properties.getProperty( "controller.request.mapping.extension", "" ) + "?" + cv.getIdentifier() + "=${" + ec.getLowerIdentifier() + "." + cv.getIdentifier() + "}\">delete</a>";
					links += lines( 1 );
					indent -= 4;
					links += spaces( indent ) + "</td>";

					output += spaces( indent ) + "<td><a href=\"edit" + properties.getProperty( "controller.request.mapping.extension", "" ) + "?" + cv.getIdentifier() + "=${" + ec.getLowerIdentifier() + "." + cv.getIdentifier() + "}\">${" + ec.getLowerIdentifier() + "." + cv.getIdentifier() + "}</a></td>";
				}
			} else if ( cv.getRelationshipType() == RelationshipType.ONETOMANY ) {
				output += spaces( indent ) + "<td>" + cv.getLowerIdentifier() + "</td>";
				//			output += spaces(indent) +"<td>${" +ec.getLowerIdentifier()+"."+cv.getDomainClass().getLowerIdentifier()+"."+cv.getDomainClass().getPrimaryKey().getIdentifier() + "}</td>";
			} else if ( cv.getRelationshipType() == RelationshipType.MANYTOMANY ) {
				//output += spaces(indent) +"<td>${" +ec.getLowerIdentifier()+"."+cv.getLowerIdentifier() + "}</td>";
				output += spaces( indent ) + "<td>" + cv.getLowerIdentifier() + "</td>";
			} else if ( cv.getRelationshipType() == RelationshipType.MANYTOONE ) {
				output += spaces( indent ) + "<td>${" + ec.getLowerIdentifier() + "." + cv.getDomainClass().getLowerIdentifier() + "." + cv.getDomainClass().getPrimaryKey().getLowerIdentifier() + "}</td>";
			} else if ( cv.getAttribType() == AttributeType.CHILD ) {
				output += spaces( indent ) + "<td>" + cv.getIdentifier() + "</td>";
			} else {
				output += spaces( indent ) + "<td>${" + ec.getLowerIdentifier() + "." + cv.getIdentifier() + "}</td>";
			}
			output += lines( 1 );
		}

		output += links;
		indent -= 4;
		output += lines( 1 );

		output += spaces( indent ) + "</tr>";
		output += lines( 1 );
		indent -= 4;

		output += spaces( indent ) + "</c:forEach>";
		indent -= 4;
		output += lines( 1 );

		output += spaces( indent ) + "</tbody>";
		indent -= 4;
		output += lines( 1 );

		output += spaces( indent ) + "</table>";

		String directory = getJspDirectory( ec );

		BufferedWriter out = jspWriter( directory, "list_alt.jsp" );
		try {
			out.write( output );
			out.flush();
		} finally {
			out.close();
		}
		log.debug( ".........list_alt done" );
	}

	private void generateEditJSP( DomainClass ec ) throws IOException {

		VelocityEditJspGenerator generator = new VelocityEditJspGenerator( packageRoot, ec, properties );
		String sourceCode = generator.javaSourceCode();

		String directory = getJspDirectory( ec );

		BufferedWriter out = jspWriter( directory, "edit.jsp" );
		try {
			out.write( sourceCode );
			out.flush();
		} finally {
			out.close();
		}
		log.debug( ".........edit done" );
	}

	private BufferedWriter jspWriter( String directory, String jspFileName ) throws IOException {
		( new File( directory ) ).mkdirs();
		File file = new File( directory + "/" + jspFileName );
		if ( file.exists() ) {
			log.debug( directory + "/" + jspFileName + " exists, creating in target" );
			directory = directory.replaceFirst( "src/", "target/src/" );
			( new File( directory ) ).mkdirs();
			file = new File( directory + "/" + jspFileName );
		}
		return new BufferedWriter( new FileWriter( file ) );
	}

	private String getJspDirectory( DomainClass ec ) {
		String directory;
		if ( Boolean.valueOf( properties.getProperty( "include.schema.in.jsp.path", "true" ) ) ) {
			directory = jspRoot + "/" + ec.getSchema().getUnqualifiedLabel() + "/" + ec.getIdentifier().toLowerCase();
		} else {
			directory = jspRoot + "/" + ec.getIdentifier().toLowerCase();
		}
		return directory;
	}

	private void generateMenu( List<DomainClass> ecList ) throws IOException {

		int indent = 0;

		Iterator<DomainClass> dcIter = ecList.iterator();

		String output = spaces( indent ) + "<%@ include file=\"/WEB-INF/include.jsp\"  %>";
		output += lines( 2 );
		output += "<div id=\"mainmenu\" class=\"list-group\">";
		output += lines( 1 );
		while ( dcIter.hasNext() ) {
			DomainClass dc = dcIter.next();
			output += tabs( 1 );
			output += "<a class=\"list-group-item\" href=\"<c:url value=\"" + ( Boolean.valueOf( properties.getProperty( "include.schema.in.request.mapping", "true" ) ) ? "/" + dc.getSchema().getUnqualifiedLabel() : "" ) + "/" + dc.getLowerIdentifier().toLowerCase() + "/list" + properties.getProperty( "controller.request.mapping.extension", "" ) + "\" />\" >" + dc.getIdentifier() + " List</a>";
			output += lines( 1 );
		}
		output += "</div>";
		output += lines( 1 );

		BufferedWriter out = jspWriter( jspRoot, "menu.jsp" );
		try {
			out.write( output );
			out.flush();
		} finally {
			out.close();
		}

	}

	private String tabs( int num ) {
		String out = "";
		for ( int i = 0; i < num; i++ ) {
			out += "\t";
		}
		return out;
	}

	private String lines( int num ) {
		String out = "";
		for ( int i = 0; i < num; i++ ) {
			out += "\n";
		}
		return out;
	}

	private String spaces( int num ) {
		String out = "";
		for ( int i = 0; i < num; i++ ) {
			out += " ";
		}
		return out;
	}
}