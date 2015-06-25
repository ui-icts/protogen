package edu.uiowa.icts.protogen.springhibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.uiowa.icts.protogen.springhibernate.ClassVariable.AttributeType;
import edu.uiowa.icts.protogen.springhibernate.ClassVariable.RelationshipType;
import edu.uiowa.icts.protogen.springhibernate.DomainClass.ClassType;
import edu.uiowa.webapp.Attribute;
import edu.uiowa.webapp.Database;
import edu.uiowa.webapp.Entity;
import edu.uiowa.webapp.Relationship;
import edu.uiowa.webapp.Schema;

/**
 * Completes the database model created previously to support Spring and Hibernate code generation
 * ...used by classes that extend AbstractSpringHiberernateCodeGenerator.java 
 * @author bkusenda
 * @since May 12, 2008
 */
public class SpringHibernateModel {

	private static final Log log = LogFactory.getLog( SpringHibernateModel.class );

	private String packageRoot = null;

	private List<DomainClass> domainClassList = new ArrayList<DomainClass>();
	private HashMap<Schema, List<DomainClass>> schemaMap;
	//private Schema currentSchema;

	private Properties properties;

	public List<DomainClass> getDomainClassList() {
		return domainClassList;
	}

	public void setDomainClassList( List<DomainClass> domainClassList ) {
		this.domainClassList = domainClassList;
	}

	public HashMap<Schema, List<DomainClass>> getSchemaMap() {
		return schemaMap;
	}

	public SpringHibernateModel( Database theDatabase, String packageRoot, Properties properties ) {
		this.packageRoot = packageRoot;
		this.properties = properties;
		loadDatabase( theDatabase );
	}

	private void loadDatabase( Database theDatabase ) {
		schemaMap = new HashMap<Schema, List<DomainClass>>();
		for ( Schema schema : theDatabase.getSchemas() ) {
			schema.populateEntityAttributeForeignReference();
			if ( !schemaMap.containsKey( schema ) ) {
				schemaMap.put( schema, new ArrayList<DomainClass>() );
			}
			loadSchema( schema );
		}
	}

	private void loadSchema( Schema schema ) {
		for ( int i = 0; i < schema.getEntities().size(); i++ ) {
			DomainClass ec = loadEntity( schema.getEntities().elementAt( i ) );
			if ( ec != null ) {
				domainClassList.add( ec );
				schemaMap.get( schema ).add( ec );
			}
			log.debug( "check:" + i + " -" + schema.getEntities().elementAt( i ).getLabel() );
		}
		connectLinks();
	}

	private DomainClass loadEntity( Entity entity ) {

		if ( isManyToMany( entity ) ) {
			log.debug( "Entity is manyToMany.  Not Creating Class" );
			return null;
		}

		boolean usesComposite = false;
		HashMap<String, Attribute> foreignAndPrimaryKeysAttributes = getHashFromAttributesFtPt( entity.getAttributes().iterator() );
		HashMap<String, Attribute> foreignAndNotPrimaryKeysAttributes = getHashFromAttributesFtPf( entity.getAttributes().iterator() );

		log.debug( "******** Entity = " + entity.getSqlLabel() );

		/***Domain Class components***/
		HashSet<String> symTableHash = new HashSet<String>();
		List<ClassVariable> symTable = new ArrayList<ClassVariable>();
		List<String> importList = new ArrayList<String>();

		/*
		 * build imports list
		 * 
		 */
		importList.add( "java.util.Set" );
		importList.add( "java.util.*" );
		importList.add( "java.text.DateFormat" );
		importList.add( "java.text.SimpleDateFormat" );
		importList.add( "java.text.ParseException" );
		importList.add( "java.util.Date" );
		importList.add( "javax.persistence.*" );
		importList.add( "javax.persistence.Entity" );
		importList.add( "javax.persistence.Table" );
		importList.add( "javax.persistence.Table" );
		importList.add( "javax.persistence.Table" );
		importList.add( "javax.persistence.Column" );
		importList.add( "javax.persistence.FetchType" );
		importList.add( "javax.persistence.JoinColumn" );
		importList.add( "javax.persistence.ManyToOne" );
		importList.add( "org.springframework.format.annotation.DateTimeFormat" );
		importList.add( "org.hibernate.annotations.*" );
		importList.add( "javax.persistence.CascadeType" );
		importList.add( "com.fasterxml.jackson.annotation.JsonIgnoreProperties" );
		importList.add( "javax.validation.constraints.NotNull");
		importList.add( "com.fasterxml.jackson.annotation.JsonIgnore");
		importList.add( packageRoot + ".*" );

		// If entity's primary key's are composite, create class and attribute for composite ID
		if ( entity.getPrimaryKeyAttributes().size() > 1 ) {

			// generateCompositeIdClassForEntity(entity);

			usesComposite = true;

			ClassVariable v = new ClassVariable( "private", entity.getUnqualifiedLabel() + "Id", entity.getUnqualifiedLowerLabel() + "Id" );

			v.setAttribType( AttributeType.COMPOSITEKEY );
			v.setComment( "Composite Key" );
			v.getGetterAnnotations().add( "@EmbeddedId" );
			v.getGetterAnnotations().add( "@AttributeOverrides( {\n" );
			Iterator<Attribute> at = entity.getPrimaryKeyAttributes().iterator();
			while ( at.hasNext() ) {
				Attribute a = at.next();
				v.getGetterAnnotations().add( "@AttributeOverride(name = \"" + a.getUnqualifiedLabel() + "\", column = @Column(name = \"" + a.getSqlLabel() + "\", nullable = false))" );
				if ( at.hasNext() ) {
					v.getGetterAnnotations().add( "," );
					;
				}
			}
			v.getGetterAnnotations().add( "})" );
			symTable.add( v );
			symTableHash.add( v.getIdentifier() );

		}

		//If only primary key
		else if ( entity.getPrimaryKeyAttributes().size() == 1 )
		{

			Iterator<Attribute> attribIter1 = entity.getPrimaryKeyAttributes().iterator();
			if ( attribIter1.hasNext() ) {

				Attribute attrib = attribIter1.next();
				//if type is number, we should disable key generation
				boolean number = false;
				if ( attrib.getType().equalsIgnoreCase( "int" ) || attrib.getType().equalsIgnoreCase( "integer" ) || attrib.getType().equalsIgnoreCase( "double" ) ) {
					number = true;
				}
				ClassVariable v = new ClassVariable( "private", attrib.getType(), attrib.getUnqualifiedLowerLabel() );
				if ( attrib.isForeign() ) {
					Entity parent = attrib.getReferencedEntity();//entity.getForeignReferenceEntity(attrib);

					v.setAttribType( AttributeType.FOREIGNPRIMARYKEY );
					v.setRelationshipType( RelationshipType.ONETOONE );

					v.setAttribute( attrib );
					v.setType( attrib.getType() );
					v.setComment( "Foreign-Primary Key" );

					v.getGetterAnnotations().add( "@GenericGenerator(name = \"generator\", strategy = \"foreign\", parameters = @Parameter(name = \"property\", value = \"" + parent.getUnqualifiedLowerLabel() + "\"))" );
					v.getGetterAnnotations().add( "@Id" );
					v.getGetterAnnotations().add( "@GeneratedValue(generator=\"generator\")" );
					v.getGetterAnnotations().add( "@Column(name = \"" + attrib.getSqlLabel() + "\", unique = true, nullable = false)" );
				}
				else
				{
					v.setAttribType( AttributeType.PRIMARYKEY );
					v.setRelationshipType( RelationshipType.NONE );

					v.setAttribute( attrib );
					v.setComment( "Primary key" );

					if ( number ) {
						v.getGetterAnnotations().add( "@javax.persistence.SequenceGenerator(  name=\"gen\",  sequenceName=\"" + entity.getSchema().getSqlLabel() + ".seqnum\",allocationSize=1)" );
					}
					v.getGetterAnnotations().add( "@Id" );
					if ( number ) {
						v.getGetterAnnotations().add( "@GeneratedValue( strategy=GenerationType.AUTO,generator=\"gen\")" );
					}
					v.getGetterAnnotations().add( "@Column(name = \"" + attrib.getSqlLabel() + "\", unique = true, nullable = false)" );
				}
				symTable.add( v );
				symTableHash.add( v.getIdentifier() );
			}

		}

		//Local Column Attributes
		Iterator<Attribute> attribIter = entity.getAttributes().iterator();

		while ( attribIter.hasNext() )
		{

			Attribute attrib = attribIter.next();
			if ( !attrib.isForeign() )
			{

				//don't add attribute if its part of the composite key
				if ( attrib.isPrimary() && usesComposite == true )
					continue;

				ClassVariable v = new ClassVariable( "private", attrib.getType(), attrib.getUnqualifiedLowerLabel() );

				v.setAttribType( AttributeType.LOCALATTRIBUTE );
				v.setRelationshipType( RelationshipType.NONE );

				v.setAttribute( attrib );
				v.setComment( "Local Attribute" );
				v.getGetterAnnotations().add( "@Column(name = \"" + attrib.getSqlLabel() + "\")" );

				if ( !symTableHash.contains( v.getIdentifier() ) )
					symTable.add( v );
			}

		}

		//Find Child Variables
		log.debug( "*************Entity:" + entity.getSqlLabel() + " ---Count:" + entity.getChildren().size() );
		Iterator<Relationship> iter = entity.getChildren().iterator();
		HashMap<String, Integer> checkExists = new HashMap<String, Integer>();
		child_loop: while ( iter.hasNext() ) {
			Relationship r = iter.next();
			Entity e = r.getTargetEntity();
			Iterator<Attribute> attribIter3 = getHashOfAttributesToEntity( entity, e ).iterator();

			while ( attribIter3.hasNext() )
			{

				Attribute attrib = attribIter3.next();
				log.debug( "*************Entity:" + entity.getSqlLabel() + " ---Attrib:" + attrib.getUnqualifiedLabel() );
				String postfix = "";
				if ( checkExists.containsKey( e.getUnqualifiedLabel() ) )
				{
					int counter = ( checkExists.get( e.getUnqualifiedLabel() ) );
					postfix = "For" + attrib.getUnqualifiedLabel().substring( 0, attrib.getUnqualifiedLabel().length() - 2 ) + "" + ( counter > 0 ? counter : "" );
					checkExists.put( e.getUnqualifiedLabel(), counter + 1 );
					continue child_loop;
				}
				else
				{
					checkExists.put( e.getUnqualifiedLabel(), 0 );

				}

				ClassVariable v = null;
				if ( isManyToMany( e ) )
				{
					v = getManyToManyVariable( entity, e );
					log.debug( "************** " + e.getUnqualifiedLabel() + " is ManyToMany Table" );

				}
				else
				{
					String variableName = plural( e.getUnqualifiedLowerLabel() ) + postfix;
					v = new ClassVariable( "private", "Set<" + e.getUnqualifiedLabel() + ">", variableName, " = new HashSet<" + e.getUnqualifiedLabel() + ">(0)" );
					//v = new ClassVariable("private", "Set", variableName, " = new HashSet(0)");
					if ( e.getUnqualifiedLowerLabel().equalsIgnoreCase( entity.getUnqualifiedLowerLabel() ) )
						v.getGetterAnnotations().add( "@OneToMany(fetch = FetchType.LAZY,   mappedBy = \"" + entity.getUnqualifiedLowerLabel() + "\", targetEntity=" + e.getUnqualifiedLabel() + ".class, cascade=CascadeType.REMOVE)" );
					else
						v.getGetterAnnotations().add( "@OneToMany(fetch = FetchType.LAZY,   mappedBy = \"" + entity.getUnqualifiedLowerLabel() + "\", targetEntity=" + e.getUnqualifiedLabel() + ".class, cascade=CascadeType.REMOVE)" );
				}
				v.setAttribType( AttributeType.CHILD );
				v.setAttribute( attrib );
				symTable.add( v );
				symTableHash.add( v.getIdentifier() );
			}
		}

		//Find Parent Variables
		List<Attribute> attribList = new ArrayList<Attribute>();

		attribList.addAll( foreignAndNotPrimaryKeysAttributes.values() );
		attribList.addAll( foreignAndPrimaryKeysAttributes.values() );

		log.debug( "***CREATING ForeignRefCode on Entity:" + entity.getSqlLabel() + " ---Count:" + attribList.size() );
		Iterator<Attribute> attribListIter = attribList.iterator();
		checkExists = new HashMap<String, Integer>();

		parent_loop: while ( attribListIter.hasNext() ) {

			Attribute at = attribListIter.next();
			log.debug( "***Attribute:" + at.getUnqualifiedLabel() );
			Entity e = at.getReferencedEntity();//entity.getForeignReferenceEntity(at);

			if ( e != null ) {

				log.debug( "***Current:" + entity.getUnqualifiedLabel() + " **PARENT ENTITY = " + e.getUnqualifiedLabel() + " for " + at.getUnqualifiedLabel() );

				String postfix = "";

				if ( checkExists.containsKey( e.getUnqualifiedLabel() ) ) {
					int counter = ( checkExists.get( e.getUnqualifiedLabel() ) );
					postfix = "By" + at.getUnqualifiedLabel().substring( 0, at.getUnqualifiedLabel().length() - 2 ) + "" + ( counter > 0 ? counter : "" );
					checkExists.put( e.getUnqualifiedLabel(), counter + 1 );
					continue parent_loop;
				} else {
					log.debug( "************EXISTS = " + e.getUnqualifiedLabel() + " for " + at.getUnqualifiedLabel() );
					checkExists.put( e.getUnqualifiedLabel(), 0 );
				}

				String variableName = e.getUnqualifiedLowerLabel() + "" + postfix;
				ClassVariable v = new ClassVariable( "private", "" + e.getUnqualifiedLabel() + "", variableName );

				log.debug( "Attribute Primary ? " + at.isPrimary() );

				if ( at.isPrimary() && entity.getPrimaryKeyAttributes().size() == 1 ) {
					v.setRelationshipType( RelationshipType.ONETOONE );
					v.getGetterAnnotations().add( "@ManyToOne(fetch = FetchType.LAZY,  targetEntity=" + e.getUnqualifiedLabel() + ".class)" );
					v.getGetterAnnotations().add( "@PrimaryKeyJoinColumn" );

					log.debug( entity.getLabel() + "." + e.getUnqualifiedLabel() );

				} else if ( at.isPrimary() && e.getPrimaryKeyAttributes().size() > 1 ) {
					int i = e.getPrimaryKeyAttributes().size() - 1;
					v.setRelationshipType( RelationshipType.MANYTOONE );
					v.getGetterAnnotations().add( "@ManyToOne(fetch = FetchType.LAZY,  targetEntity=" + e.getUnqualifiedLabel() + ".class )" );
					v.getGetterAnnotations().add( "@JoinColumns({" );
					for ( Attribute ta : e.getPrimaryKeyAttributes() ) {
						v.getGetterAnnotations().add( "\t@JoinColumn(name = \"" + ta.getSqlLabel() + "\",nullable = false, insertable = false, updatable = false)" + ( i > 0 ? "," : "" ) );
						i--;
					}

					v.getGetterAnnotations().add( "})" );
					//				} else if(!at.isPrimary() && attribList.size() > 1 ) {
					//					log.debug("new else");
					//					
					//					v.setRelationshipType(RelationshipType.MANYTOONE);
					//					v.getGetterAnnotations().add("@ManyToOne(fetch = FetchType.LAZY,  targetEntity="+e.getUnqualifiedLabel()+".class )");
					//					v.getGetterAnnotations().add("@JoinColumns({");
					//
					//					// Vector<Attribute> other_p_keys = e.getPrimaryKeyAttributes();
					//					Vector<Attribute> other_p_keys = at.getChildAttributes();
					//					int other_p_keys_count = other_p_keys.size() - 1;
					//					for(Attribute oa : other_p_keys){
					//						
					//						log.debug(oa.getSqlLabel());
					//						log.debug(oa.getReferencedEntityName());
					//						log.debug(oa.getParentAttribute());
					//						log.debug(oa.getForeignAttribute());
					//						
					//						// for (Attribute a : attribList) {
					//							// if(a.getSqlLabel().equalsIgnoreCase(oa.getSqlLabel())){
					//								v.getGetterAnnotations().add("\t@JoinColumn(name = \""+oa.getSqlLabel()+"\",nullable = false, insertable = false, updatable = false)"+(other_p_keys_count > 0 ? "," : "" ));
					//								other_p_keys_count--;
					//							//}
					//						// }
					//					}
					//					
					//					v.getGetterAnnotations().add("})");
				} else {

					log.debug( "entity: " + entity.getLabel() + " : " + at.getSqlLabel() );

					v.setRelationshipType( RelationshipType.MANYTOONE );
					v.getGetterAnnotations().add( "@ManyToOne(fetch = FetchType.LAZY,  targetEntity=" + e.getUnqualifiedLabel() + ".class )" );
					if ( at.isPrimary() && entity.getPrimaryKeyAttributes().size() > 1 ) {
						v.getGetterAnnotations().add( "@JoinColumn(name = \"" + at.getSqlLabel() + "\",nullable = false, insertable = false, updatable = false)" );
					} else {
						v.getGetterAnnotations().add( "@JoinColumn(name = \"" + at.getSqlLabel() + "\",nullable = true)" );//, insertable = false, updatable = false)");
					}
				}

				log.debug( at );

				v.setAttribute( at );
				v.setAttribType( AttributeType.FOREIGNATTRIBUTE );

				log.debug( at );

				symTable.add( v );
				symTableHash.add( v.getIdentifier() );

			}
			else
			{
				log.debug( "************PARENT ENTITY = NULL for " + at.getUnqualifiedLabel() );
			}

		}

		String packageName = packageRoot + ( Boolean.valueOf( properties.getProperty( "include.schema.in.package.name", "true" ) ) ? "." + entity.getSchema().getUnqualifiedLabel() : "" ) + ".domain";

		DomainClass domainClass = new DomainClass( properties );
		domainClass.setUsesCompositeKey( usesComposite );
		domainClass.setSchema( entity.getSchema() );
		domainClass.setClassType( ClassType.ENTITY );
		domainClass.setPackageName( packageName );
		domainClass.setModifier( "public" );
		domainClass.setIdentifier( entity.getUnqualifiedLabel() );
		domainClass.setTableName( entity.getSqlLabel() );
		domainClass.setImportList( importList );
		domainClass.setEntity( entity );
		domainClass.setSymTable( symTable );
		domainClass.populateClassVariableDomainClass();
		entity.setDomainClass( domainClass );

		return domainClass;

	}

	public String plural( String st )
	{
		return st + "s";
		//		if (st.charAt(st.length()) == 's')
		//			return st;
		//		else if (st.charAt(st.length()-1) == 'y')
		//			return st.substring(0, st.length()) + "ies";
		//		else
		//			return st + "s";

	}

	private HashMap<String, Attribute> getHashFromAttributesFtPt( Iterator<Attribute> attribIter )
	{
		HashMap<String, Attribute> hash = new HashMap<String, Attribute>();
		while ( attribIter.hasNext() )
		{
			Attribute attribute = attribIter.next();
			if ( attribute.isForeign() && attribute.isPrimary() ) {
				hash.put( attribute.getUnqualifiedLabel(), attribute );
				log.debug( "Adding F & P attribute: " + attribute.getUnqualifiedLabel() );
			}
		}
		return hash;
	}

	@SuppressWarnings( "unused" )
	private HashMap<String, Attribute> getHashFromAttributesFfPt( Iterator<Attribute> attribIter ) {
		HashMap<String, Attribute> hash = new HashMap<String, Attribute>();
		while ( attribIter.hasNext() ) {
			Attribute attribute = attribIter.next();
			if ( !attribute.isForeign() && attribute.isPrimary() ) {
				hash.put( attribute.getUnqualifiedLabel(), attribute );
			}
		}
		return hash;
	}

	private HashMap<String, Attribute> getHashFromAttributesFtPf( Iterator<Attribute> attribIter ) {

		log.debug( "getHashFromAttributesFtPf" );

		HashMap<String, Attribute> hash = new HashMap<String, Attribute>();
		while ( attribIter.hasNext() ) {
			Attribute attribute = attribIter.next();
			if ( attribute.isForeign() && !attribute.isPrimary() ) {
				hash.put( attribute.getUnqualifiedLabel(), attribute );
			}
		}
		return hash;
	}

	@SuppressWarnings( "unused" )
	private HashMap<String, Attribute> getHashFromAttributesFfPf( Iterator<Attribute> attribIter ) {
		HashMap<String, Attribute> hash = new HashMap<String, Attribute>();
		while ( attribIter.hasNext() ) {
			Attribute attribute = attribIter.next();
			if ( !attribute.isForeign() && !attribute.isPrimary() ) {
				hash.put( attribute.getUnqualifiedLabel(), attribute );
			}
		}
		return hash;
	}

	private List<Attribute> getHashOfAttributesToEntity( Entity parent, Entity child )
	{
		log.debug( "*******parent:" + parent.getUnqualifiedLowerLabel() + " ****child:" + child.getUnqualifiedLowerLabel() );

		List<Attribute> hash = new ArrayList<Attribute>();
		Iterator<Attribute> attribIter = child.getAttributes().iterator();
		while ( attribIter.hasNext() )
		{
			Attribute a = attribIter.next();
			log.debug( "*******atter:" + a.getUnqualifiedLowerLabel() );
			if ( a.isForeign() )
			{

				Entity e2 = a.getReferencedEntity();

				log.debug( "*******foreign:" + a.getUnqualifiedLowerLabel() + " ****e2label" + ( e2 != null ? e2.getUnqualifiedLabel() : "none" ) );

				if ( e2 != null && parent.getUnqualifiedLabel().equalsIgnoreCase( e2.getUnqualifiedLabel() ) )
				{
					log.debug( "*******foreign:DINGDINGDING" );
					hash.add( a );

				}

			}

		}
		return hash;
	}

	private ClassVariable getManyToManyVariable( Entity parent, Entity child )
	{
		ClassVariable v = new ClassVariable();
		Iterator<Attribute> attribIter = child.getAttributes().iterator();
		String thisKey = "";
		String thatKey = "";
		String targetEntity = "";
		while ( attribIter.hasNext() )
		{
			Attribute a = attribIter.next();

			//Entity e2 = child.getForeignReferenceEntity(a);
			Entity e2 = a.getReferencedEntity();

			if ( e2 != null && parent.getUnqualifiedLabel().equals( e2.getUnqualifiedLabel() ) ) {
				thisKey = a.getSqlLabel();
			} else {
				if ( e2 != null ) {
					targetEntity = e2.getUpperLabel();
					thatKey = a.getSqlLabel();
					v.setIdentifier( plural( e2.getUnqualifiedLowerLabel() ) );
					v.setType( "Set<" + targetEntity + ">" );
					v.setInitializer( " = new HashSet<" + targetEntity + ">(0)" );
					//					v.setType("Set<"+e2.getUnqualifiedLabel()+">");
					//					v.setInitializer(" = new HashSet<"+e2.getUnqualifiedLabel()+">(0)");
				}
			}
		}
		v.setAttribType( AttributeType.FOREIGNATTRIBUTE );

		v.setRelationshipType( RelationshipType.MANYTOMANY );

		v.setModifier( "private" );
		v.getGetterAnnotations().add( "@ManyToMany( cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity=" + targetEntity + ".class )" );
		v.getGetterAnnotations().add( "@JoinTable( name = \"" + child.getSchema().getSqlLabel() + "." + child.getSqlLabel() + "\", joinColumns = { @JoinColumn( name = \"" + thisKey + "\")}, inverseJoinColumns = { @ JoinColumn(name = \"" + thatKey + "\") } )" );

		return v;

	}

	private boolean isManyToMany( Entity child )
	{
		log.debug( "**************Checking if child table is ManyToMany " + child.getUnqualifiedLabel() );
		log.debug( "* total attributes:" + child.getAttributes().size() );
		log.debug( "* primary keys:" + child.getPrimaryKeyAttributes().size() );
		log.debug( "* parent keys:" + child.getParentKeyAttributes().size() );
		log.debug( "* childern:" + child.getChildren().size() );

		if ( child.getAttributes().size() == 2 && child.getPrimaryKeyAttributes().size() == 2 && child.getParentKeyAttributes().size() == 2 && child.getChildren().size() == 0 )
			return true;
		return false;
	}

	private ClassVariable findReferencedClassVariable( ClassVariable cv ) {

		DomainClass dc = findDomainByIdentifier( cv.getAttribute().getReferencedEntity().getUnqualifiedLabel() );

		Iterator<ClassVariable> cvIter = dc.getSymTable().iterator();

		while ( cvIter.hasNext() ) {
			ClassVariable cv1 = cvIter.next();
			if ( cv1.getAttribute() != null ) {

				// log.debug("CV1 = "+cv1.getAttribType()+"."+cv1.getAttribute().getUnqualifiedLabel() + " : CV = " + cv.getAttribType()+"."+cv.getAttribute().getUnqualifiedLabel());

				if ( cv1.getAttribType() != AttributeType.CHILD && cv1.getAttribute().getUnqualifiedLabel().equals( cv.getAttribute().getUnqualifiedLabel() ) ) {
					// log.debug("found reference - "+ cv.getIdentifier() +" with "+cv1.getIdentifier());
					return cv1;
				} else {
					// log.debug("Error on "+ cv.getIdentifier() +" with "+cv1.getIdentifier());
				}
			} else {
				// log.debug(" attribute is null ");
			}
		}

		return null;

	}

	private DomainClass findDomainByIdentifier( String ident )
	{
		Iterator<DomainClass> domainIter = domainClassList.iterator();

		while ( domainIter.hasNext() )
		{
			DomainClass dc = domainIter.next();
			if ( dc.getIdentifier().equalsIgnoreCase( ident ) )
				return dc;

		}
		return null;

	}

	/*
	 * Link DomainClass Objects that have foreign key references
	 *
	 * 
	 * this all needs updated and is inconsistant
	 */

	private void connectLinks()
	{
		log.debug( "***Connecting Links***" );
		Iterator<DomainClass> domainIter = domainClassList.iterator();
		while ( domainIter.hasNext() )
		{

			DomainClass dc = domainIter.next();
			log.debug( "   " + dc.getIdentifier() );
			Iterator<ClassVariable> cvIter = dc.getSymTable().iterator();
			while ( cvIter.hasNext() )
			{

				ClassVariable cv = cvIter.next();
				log.debug( "      " + cv.getIdentifier() );

				//finds the class of the parent
				if ( cv.getAttribType() == AttributeType.FOREIGNATTRIBUTE )
				{
					log.debug( "         -attribute is foreign" );
					Entity e = cv.getAttribute().getReferencedEntity();//dc.getEntity().getForeignReferenceEntity(cv.getAttribute());
					if ( e != null )
					{
						DomainClass c = findDomainByIdentifier( e.getUnqualifiedLabel() );
						if ( c == null )
							log.debug( "************** cannot find DomainClass:" + cv.getType() );
						cv.setDomainClass( c );
					} else {
						log.debug( "************** cannot getEntity from attribute:" + cv.getIdentifier() );
					}

				}
				//finds the class of the children
				else if ( cv.getAttribType() == AttributeType.CHILD )
				{
					log.debug( "         -attribute is child" );
					ClassVariable cvRef = findReferencedClassVariable( cv );
					if ( cvRef == null )
					{
						log.debug( "************** ERROR CONNECTING CLASSVARIABLE" );
						cv.setReferenedClassVariable( cvRef );
					}

				}

			}

		}

	}

	/**
	 * @return
	 */
	public String getPackageRoot() {
		return packageRoot;
	}

}
